import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { ControlledInput } from "./ControlledInput";
import "../../styles/REPL.css"
import { getData } from "./REPLFunction/AccessBackend";
import { REPLHistory } from "./REPLHistory";
import { handleFilter } from "./REPLFunction/FilterGeoJSONFunc";

/**
 * Interface props necessary to share amongst the other files
 */
interface InputProps {
    history: Array<[string, [string[], string[][]]]>;
    setHistory: Dispatch<SetStateAction<Array<[string, [string[], string[][]]]>>>;
    highlightedData: GeoJSON.FeatureCollection | undefined;
    setHighlight: Dispatch<SetStateAction<GeoJSON.FeatureCollection | undefined>>;
    showCommands: boolean;
    setShowCommands: Dispatch<SetStateAction<boolean>>;
    
}

/**
 * This function exectutes the repl commands on the page
 * @param props 
 * @returns 
 */
export function Input(props: InputProps){

    // saves the command string inputed
    const [commandString, setCommandString] = useState<string>('');   

    // manages the button for the commands
    const [commandMessage, setCommandMessage] = useState<string>("Show Commands");
    
    /**
     * This function updates the buttom message when toggled
     */
    function commandRules() {
        props.setShowCommands(!props.showCommands);
        if (props.showCommands) {
        setCommandMessage("Show Commands");
        } else {
        setCommandMessage("Hide Commands");
        }
    }

    /**
     * The role of this function is to execute any commands passed through that is valid within the map dictionary
     * @param commandString 
     */
    function handleSubmit(commandString: string){
        // handles if the user is filtering based on a keyword within specific area descriptions from the geoJSON
        if(commandString.includes("filter_area")){
            handleFilter(commandString).then(response => {
                props.setHighlight(response)
            })
        }
        // handles all other commands
        else{
            getData(commandString).then(response =>
                props.setHistory([...props.history, [commandString, response]])
            )
        }

        // updates the command string 
        setCommandString("");

    } 

    /**
     * This return statement displays:
     * 1) The header for the webpage
     * 2) A button to show the possible valid commands relevant to the user
     * 3) An input box for the user to enter commands
     * 4) A submit button to execute a command
     * 5) A running log of the user's command history
     */
    return(
        <div className="input" style={{display: "flex"}} aria-label="REPL History Section">
            <div onKeyDown={(key) => {
                if(key.code == "Equal"){
                    commandRules()
                }
            }}>
                <h1 aria-label="heading" tabIndex={0}>Maps</h1>

                <button aria-label={"Show Commands Button"} onClick={() => commandRules()} style={{width: "200px", background:"lightpink"}}><b>{commandMessage}</b></button>

                {props.showCommands ? (
                    <div aria-label="use the equals key (=) to hide or show the valid commands within the command input box">
                        <table className="repl-command-instruction" aria-label={"Command table"} tabIndex={0} aria-aria-live="polite">
                            <tr tabIndex={1}>
                                <th>Command</th>
                                <th>Output</th>
                            </tr>
                            <tr tabIndex={1}>
                                <td className="command">
                                    load_file <u>file</u>
                                </td>
                                <td>
                                    Load a valid file path by replacing <u style={{fontFamily:"monospace"}}>file</u>.
                                </td>
                            </tr>
                            <tr tabIndex={1}>
                                <td className="command">
                                    view

                                </td>
                                <td>
                                    View the data loaded.
                                </td>
                            </tr>
                            <tr tabIndex={1}>
                                <td className="command">
                                    search <u>columnID</u> <u>value</u>
                                </td>
                                <td>
                                    Replace <u style={{fontFamily: "monospace"}}>value</u> and <u style={{fontFamily: "monospace"}}>columnID</u> (optional) to find a keyword within the loaded dataset.
                                </td>
                            </tr>
                            <tr tabIndex={1}>
                                <td className="command">
                                    filter_area <u>keyword</u>(s)
                                </td>
                                <td>
                                    Replace <u style = {{fontFamily: "monospace"}}>keyword</u> with a term you're looking for within the area descriptions.
                                </td>
                            </tr>
                            <tr tabIndex={1}>
                                <td className="command">
                                    mock_load <u>filepath</u>
                                </td>
                                <td>
                                    Load a valid mocked file path by replacing <u style={{fontFamily:"monospace"}}>file</u>.
                                </td>                                
                            </tr>
                            <tr tabIndex={1}>
                                <td className="command">
                                    mock_view

                                </td>
                                <td>
                                    View the mocked data loaded.
                                </td>
                            </tr>
                            <tr>
                                <td className="command">
                                    mock_search <u>columnID</u> <u>value</u>
                                </td>
                                <td>
                                    Replace <u style={{fontFamily: "monospace"}}>value</u> and <u style={{fontFamily: "monospace"}}>columnID</u> (optional) to find a keyword within the mocked loaded dataset.
                                </td>
                            </tr>

                        </table>
                    </div>

                ):null}

                <p></p>

                <fieldset tabIndex={2} onKeyDown={(key) => {
                    switch(key.code){
                        case "Enter":
                            handleSubmit(commandString)
                            break
                    }
                }}>
                    <ControlledInput 
                        value={commandString}
                        setValue={setCommandString}
                        ariaLabel={"Command input box"}
                    />
                </fieldset>

                <p></p>
                <button aria-label={"Submit button"} tabIndex={3} onClick={()=>handleSubmit(commandString)}><b>Submit</b></button>

                <REPLHistory history={props.history} verbose={false}></REPLHistory>
            </div>

        </div>
    )

    
}