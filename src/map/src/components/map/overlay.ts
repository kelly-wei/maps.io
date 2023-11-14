import { FeatureCollection } from "geojson"
import mapboxgl, { LngLatLike, Popup } from "mapbox-gl";
import { CircleLayer, FillLayer, LineLayer } from "react-map-gl"

function isFeatureCollection(json:any): json is FeatureCollection{
    return json.type === "FeatureCollection";
}

export function getGeoJSON(): Promise<FeatureCollection | undefined> {
    let url = "http://localhost:323/geojson"

    return new Promise((resolve, reject) => {
        fetch(url)
        .then(response => response.json())
        .then(json => {
            isFeatureCollection(json) ? resolve(json) : resolve(undefined);
        })})    
}

export function overlayData(): Promise<GeoJSON.FeatureCollection | undefined>{
    const rl_data = getGeoJSON()
    return rl_data; 
}

export const geoLayer: FillLayer = {
    id: "geo_data", 
    type: "fill",
    paint:{
        "fill-color": ["match", ["get", "holc_grade"],
        "A",
        "#FF69B4",
        "B",
        "#04b8cc",
        "C",
        "#e9ed0e",
        "D",
        "#d11d1d",
        "#ccc",
    ],
    "fill-opacity": 0.2,
    }
}

export const stateFill: FillLayer = {
    id: "geo_highlight", 
    type: "fill", 
    paint:{
        "fill-color": "#800080",
        "fill-opacity": 0.5,
    },
}

export const pointFill: CircleLayer = {
    id: "income_highlight",
    type: "circle",
    paint:{
        'circle-radius': 15,
        'circle-color': '#007cbf',
        'circle-opacity': 0.5,
    }
}