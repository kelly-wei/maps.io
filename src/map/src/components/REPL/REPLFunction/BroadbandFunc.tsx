import { REPLFunction } from "./AccessBackend";

/**
 * Function to handle a broadband call
 * @param args Arguments to broadband
 * @returns Promise containing data/messsage
 */
export const handleBroadband : REPLFunction = function (args: Array<string>) : Promise<[string[], string[][]]> {
    if (args.length <= 1){
        return new Promise((resolve, reject) => {
            const finalRet : [string[], string[][]]  = [[], [["error_bad_request: a state and county must be entered"]]]
            resolve(finalRet)
        })
    }
    
    let searchString = 'http://localhost:323/broadband?state=' + args[0] + "&&county=" + args[1]
    
    return new Promise((resolve, reject) => {
        fetch(searchString)
        .then(response => response.json())
        .then(json => {
            if (json.result == undefined){
                const emptyRet : [string[], string[][]] = [[], [["Error accessing broadband data (response map undefined)."]]]
                resolve(emptyRet)
            }
            if (json.result == "success"){
                const finalRet : [string[], string[][]]  = [[], 
                    [[json.data + "% of households in " + json["county request received"] + " had broadband."]]]
                resolve(finalRet)
            } else {
                const finalRet : [string[], string[][]] = [[], [[json.result+": "+json.details]]]
                resolve(finalRet)
            }
        })
        .catch(response => {
            const finalRet : [string[], string[][]] = [[], [["error_connection: failed to fetch the backend server."]]]
            resolve(finalRet)
        })
    })
}
