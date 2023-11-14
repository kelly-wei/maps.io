import { handleLoad } from "./LoadFunc";
import { handleView } from "./ViewFunc";
import { handleSearch } from "./SearchFunc";
import { handleBroadband } from "./BroadbandFunc";
import { mockBroadband, mockLoad, mockSearch, mockView } from "../../Mocking/AccessMocks";

/**
 * A command-processor function for our REPL. The function returns a Promise   
 * which resolves to a string, which is the value to print to history when 
 * the command is done executing.
 * 
 * The arguments passed in the input (which need not be named "args") should 
 * *NOT* contain the command-name prefix.
 */
export interface REPLFunction {    
    (args: Array<string>): Promise<[string[], string[][]]>
}

/**
 * Map of REPLFunctions
 */
export const mappedFuncs = new Map<string, REPLFunction>([
    ["load_file", handleLoad],
    ["view", handleView],
    ["search", handleSearch],
    ["broadband", handleBroadband],
    ["mock_load", mockLoad],
    ["mock_view", mockView],
    ["mock_search", mockSearch],
    ["mock_broadband", mockBroadband],
])

/**
 * Function to register REPLFunctions
 * @param command User-side command
 * @param newFunc New REPLFunction to register
 */
export function registerCommand(command: string, newFunc : REPLFunction) {
    mappedFuncs.set(command, newFunc);
    if (mappedFuncs.get(command) != newFunc) {
        throw new Error('new func not mapped')
    }
}

/**
 * Gets requested data based on user input string
 * @param request User request as a string
 * @returns Promise containing message/data
 */
export function getData(request: string): Promise<[string[], string[][]]> {
    const intermediateMatch = request.match(/"[^"]*"|\S+/g)
    if (intermediateMatch == null) {
        return new Promise((resolve, reject) => {
            resolve([[], [["Please enter a valid command."]]])
        })
    }
    
    const reqArray : string[] = intermediateMatch.map(m => m.slice(0, 1) === '"'? m.slice(1, -1): m)
    if (reqArray == null) {
        return new Promise((resolve, reject) => {
            resolve([[], [["Please enter valid text."]]])
        })
    } 
    
    const command = reqArray.shift()
    return new Promise((resolve, reject) => {
        if (command == undefined) {
            resolve([[], [["Please enter a valid command."]]])
        } else {
            const commandAct = mappedFuncs.get(command)
            if (commandAct == undefined) {
                resolve([[], [["Please enter a valid command."]]])
            } else {
                commandAct(reqArray).then(response => {resolve(response)})
            }
        }
    })
}
