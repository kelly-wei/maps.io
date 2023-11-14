import { REPLFunction } from "./AccessBackend";

/**
 * Function to handle a view call
 * @param args Arguments to view
 * @returns Promise containing data/messsage
 */
export const handleView : REPLFunction = function (args: Array<string>) : Promise<[string[], string[][]]> {
    return new Promise((resolve, reject) => {
        fetch("http://localhost:323/viewcsv")
        .then(response => response.json())
        .then(json => {
            if (json.result == undefined) {
                const emptyRet : [string[], string[][]] = [[], [["Error accessing broadband data"]]]
                resolve(emptyRet)
            }
            if (json.result == "success"){
                if (json.data == undefined) {
                    throw new Error('success despite undefined data')
                }
                if (json.headers == undefined) {
                    const finalRet : [string[], string[][]] = [[], json.data]
                    resolve(finalRet)
                } else {
                    const finalRet : [string[], string[][]] = [json.headers, json.data]
                    resolve(finalRet)
                }
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