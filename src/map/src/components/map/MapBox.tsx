import React, { SetStateAction, useEffect, useState } from "react";

import Map, {Layer, MapLayerMouseEvent, Popup, Source, ViewStateChangeEvent} from "react-map-gl";

import "mapbox-gl/dist/mapbox-gl.css"

import "../../styles/MapBox.css"

import { APIKey } from "../../private/key";
import { geoLayer, getGeoJSON, pointFill, stateFill} from "../map/overlay";
import { getData } from "../REPL/REPLFunction/AccessBackend";

interface MapProps {
    highlighted: GeoJSON.FeatureCollection | undefined;
}

export default function MapBox(props: MapProps){
    const pvdLatitude = 41.82780
    const pvdLongitude = -71.40125
    const initialZoom = 10

    const[viewState, setViewState] = useState({
        longitude: pvdLongitude,
        latitude: pvdLatitude,
        zoom: initialZoom
    })

    // this manages the overlay data from the geojson 
    const[overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>();

    // this manages the acs_survey data to support income redlining
    const[points, setPoints] = useState<GeoJSON.FeatureCollection | undefined>();

    const[popupData, setPopup] = useState({longitude: 0.00, latitude: 0.00, city: "", medianIncome: ""});

    const[showPop, setShow] = useState<boolean>(false);

    // a new instance of a GeoJSON
    const incomeInfo: GeoJSON.FeatureCollection = {
        type: 'FeatureCollection',
        features: [],
    };

    // this function determines whether or not a load was successful by checking if 'success' is found
    function wasSuccessful(result: string){
        return (result.includes('success'))
    }

    useEffect(() => {
        // sets up the overlay data by retrieving it using a backend call
        getGeoJSON().then(response =>
            setOverlay(response)
        )

        // loads the acs survey census for use
        new Promise((resolve, reject) => {
            getData("load_file data/census/ACS_Survey.csv").then(response => { 
                wasSuccessful(response[1][0][0]) ? resolve(response) : resolve (undefined)
            }).catch(reject)        // error handling in case the call had a faulty file path
        })

        // populates the incomeInfo geoJSON dataset 
        new Promise((resolve, reject) => {
            getData("view").then(response => response[1]).then(json => {
                // checks to make sure incomeInfo is empty and not already populated 
                if(incomeInfo.features.length < 1){
                    for(let i = 2; i < json.length; i++){
                        let cityName = json[i][0]       // retrieves the city/town name
                        let surveyLat = parseFloat(json[i][1])  // retrieves the latitude coords
                        let surveyLong = parseFloat(json[i][2]) // retrieves the longitude coords
                        let surveyIncome = json[i][3]           // retrieves the median household income

                        // new instance of a GeoJSON.Feature
                        const newFeat: GeoJSON.Feature = {
                            "type": 'Feature',
                            "properties": {
                                name: cityName,
                                income: surveyIncome,
                            },
                            "geometry": {
                                "coordinates": [surveyLong, surveyLat],
                                "type": "Point",
                            }
                        }

                        // adds the feature to the existing income info
                        incomeInfo.features.push(newFeat)
                    }
                }
            })
            .then(data => resolve(data))    // resolves the promise
            .catch(reject)      // error handling - catches in case something goes wrong/data wasn't loaded properly
        })
        setPoints(incomeInfo)       // updates the points data
    }, []);
    /**
     * This function finds a specific feature given coordinates
     * @param targetCoords - [longitude, latitude]
     * @returns - the feature found 
     */
    function findFeatureByCoords(targetCoords: number[]){
        const pointsFeatures = points ? points.features : [];       // retrieves all features within the GeoJSON
    
        let foundFeat: GeoJSON.Feature;                 // an empty GeoJSON.Feature

        // iterate through until a feature is found (if any)
        while(foundFeat === undefined){
            // loop there every feature in the features list 
            for(const item of pointsFeatures){
                if(item.geometry.type === 'Point'){         // checks to make sure the type is a 'point'
                    // retrieves the coords of the feature
                    const itemCoords = item.geometry.coordinates

                    // checks to see if the difference between targetCoords and itemCoords - if marginal, it's safe to assume we're in the bubble to view popup
                    if(Math.abs(itemCoords[0] - targetCoords[0]) <= 0.001 && Math.abs(itemCoords[1] - targetCoords[1]) <= 0.001){
                        foundFeat = item;           // update the foundfeat to the specific feature
                        return foundFeat
                    }
                }
            }
            return null        
        }
    }

    // this function compiles the information for the popup based on the MapLayerMouseEvent e
    function displayPopUp(e:MapLayerMouseEvent){
        let coordsVar = [e.lngLat.lng, e.lngLat.lat]        // retrieves the coords

        let name = ""                                       // hosts the name of the city/town
        let income = ""                                    // hosts the value of the median household income for the area

        // finds the property that matches the coords from the MapLayerMouseEvent
        const property = findFeatureByCoords(coordsVar)?.properties

        // updates the vars
        if(property !== undefined && property !== null){
            name = property.name;
            income = property.income;
        }

        // updates the popupData field 
        setPopup({
            longitude: coordsVar[0],
            latitude: coordsVar[1],
            city: name,
            medianIncome: income,
        })

        setShow(true)       // update the boolean field
    }

    // this function resets the popup data to empty
    function resetPopUp(){
        setPopup({longitude: 0.00, latitude: 0.00, city: "", medianIncome: ""})
    }

    
    return(
        <div className="row">
            <div className="map" aria-label={"Map"}>
                <Map 
                    mapboxAccessToken={APIKey}
                    longitude={viewState.longitude}
                    latitude={viewState.latitude}
                    zoom={viewState.zoom}

                    onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}

                    style={
                        {width: "70vw", height: "100vh"}}

                    mapStyle={"mapbox://styles/mapbox/outdoors-v12"}

                    onClick={(ev:MapLayerMouseEvent) => displayPopUp(ev)}    
                    
                    >
                        {!(overlay === undefined) ?
                            <Source id="geo_data" type="geojson" data={overlay}>            
                                <Layer {...geoLayer}></Layer>
                            </Source>
                            : ""
                        }
                        {!(props.highlighted === undefined) ?
                            <Source id="geo_highlight" type="geojson" data={props.highlighted}>            
                                <Layer {...stateFill}></Layer>
                            </Source>
                            : ""
                        }
                        {!(incomeInfo === undefined) ? 
                            <Source id="income_highlight" type="geojson" data={points}>
                                <Layer {...pointFill}></Layer>
                            </Source>
                        :""}
                        {showPop ? (<Popup
                                latitude={popupData.latitude}
                                longitude={popupData.longitude}
                                onClose={() => resetPopUp}
                                closeButton={false}
                                closeOnClick={false}
                            ><div aria-label="pop up median household income data" aria-live="polite">
                                <b>Name:</b> {popupData.city}
                                <p></p>
                                <b>Median Household Income:</b> ${popupData.medianIncome}
                            </div>
                            </Popup>) 
                        
                        : resetPopUp}

                </Map>
            </div>
        </div>
    )
}