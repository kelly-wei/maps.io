import assert from "assert";
import { FeatureCollection } from "geojson"

/**
 * This function checks if the passed in argument is of type FeatureCollection
 * @param json 
 * @returns 
 */
function isFeatureCollection(json:any): json is FeatureCollection{
    return json.type === "FeatureCollection";
}

/**
 * This function makes a call to the backend in order to retrieve the filtered geoJSON data given keyword(s).
 * @param commandString 
 * @returns 
 */
export function handleFilter(commandString: string) : Promise<GeoJSON.FeatureCollection | undefined> {
    let url = "http://localhost:323/filterarea?keyword="

    // an array after the command string was split on the regex
    let arr = commandString.split(/\s+/);

    // initializing the string that hosts the keywords 
    //arr[0] = "filter_area" - not needed; thus, starting at arr[1]
    let searchString = arr[1]

    // if there are multiple key words
    if(arr.length > 2){
        for(let i = 2; i <= arr.length; i++){
            if(arr[i] === undefined){
                continue
            }
            //fill in the %20 to represent a space in the url fetch call
            else{
                searchString += "%20" + arr[i]

            }
        }
    }

    // update the url with the keyword(s)
    url += searchString

    // retrieve the promise containing the FeatureCollection json
    return new Promise(async (resolve, reject) => {
        await fetch(url)
        .then(response => response.json())
        .then(json => {
            isFeatureCollection(json) ? resolve(json) : resolve(undefined)
        })
        
        // catch the error (if any)
        .catch(json => resolve(undefined));
    })
}
