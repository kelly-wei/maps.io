import assert from "assert";
import { REPLFunction } from "./AccessBackend";

/**
 * Function to handle a search call
 * @param args Arguments to search
 * @returns Promise containing data/messsage
 */
export const handleSearch : REPLFunction = function (args: Array<string>) : Promise<[string[], string[][]]> {
    var searchString = ''
    if (args.length <= 0){
        searchString = 'http://localhost:323/searchcsv'
    } else if (args.length == 1) {
        searchString = 'http://localhost:323/searchcsv?searchterm=' + args[0]
    } else if (args.length == 3) {
        searchString = 'http://localhost:323/searchcsv?searchterm=' + args[0] + 
        '&&columnidentifier=' + args[1] + '&&isindex=' + args[2]
    } else {
        return new Promise((resolve, reject) => {
            const finalRet : [string[], string[][]]  = 
            [[], [["error_bad_request: missing keywords"]]]
            resolve(finalRet)
        })
    }
    return new Promise((resolve, reject) => {
        fetch(searchString)
        .then(response => response.json())
        .then(json => {
            if (json.result == undefined){
                const emptyRet : [string[], string[][]] = [[], [["Error accessing search data (response map undefined)."]]]
                resolve(emptyRet)
            }
            if (json.result == "success") {
                if (json.headers == undefined) {
                    const finalRet : [string[], string[][]] = [[], json.data]
                    if (finalRet[1].length == 0) {
                        const emptyRet : [string[], string[][]] = [[], [["no results found"]]]
                        resolve(emptyRet)
                    } else {
                        resolve(finalRet)
                    }
                } else {
                    const finalRet : [string[], string[][]] = [json.headers, json.data]
                    if (finalRet[1].length == 0) {
                        const emptyRet : [string[], string[][]] = [[], [["no results found"]]]
                        resolve(emptyRet)
                    } else {
                        resolve(finalRet)
                    }
                }
            } else {
                if (!(json.headers == undefined) || !(json.data == undefined)) {
                    throw new Error('not undefined at error stage')
                }
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