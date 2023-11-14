import { REPLFunction } from "./AccessBackend";

/**
 * Called by Access Backend when the command string contains "load_file." Will call backend to load the file.
 * @param args 
 * @returns a promise that returns an array of string array and a array of array of strings.
 */
export const handleLoad : REPLFunction = function (args: Array<string>) : Promise<[string[], string[][]]> {
    if (args.length <= 0){
        return new Promise((resolve, reject) => {
            const finalRet : [string[], string[][]]  = [[], [["error_bad_request: filepath must be entered"]]]
            resolve(finalRet)
        })
    }
    
    let searchString = 'http://localhost:323/loadcsv?filepath=' + args[0] + '&&headers=' + args[1]
    return new Promise((resolve, reject) => {
        fetch(searchString)
        .then(response => response.json())
        .then(json => {
            if (json.result == undefined || json.filepath == undefined) {
                const emptyRet : [string[], string[][]] = [[], [["Error accessing load data (response map undefined)."]]]
                resolve(emptyRet)
            }
            if (json.result == "success"){
                const finalRet : [string[], string[][]]  = [[], 
                [[json.result + ": Loaded " + json.filepath]]]
                resolve(finalRet)
            } else {
                const finalRet : [string[], string[][]]  = [[], 
                [[json.result + " with filepath = " + json.filepath + " : " + json.details]]]
                resolve(finalRet)
            }
        })
        .catch(response => {
            const finalRet : [string[], string[][]] = [[], [["error_connection: failed to fetch the backend server."]]]
            resolve(finalRet)
        })
    })
}