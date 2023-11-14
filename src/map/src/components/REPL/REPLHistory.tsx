import { StringMappingType } from "typescript";
import { Dispatch, SetStateAction, useState } from "react";
import "../../styles/REPL.css";


/**
 * REPLHistory: Component that takes care of recording past commands and results
 */

/**
 * Props for REPLHistory
 * Fields:
 * history – array of commands to data
 * verbose – whether the frontend is in verbose mode or not
 */
interface REPLHistoryProps {
  history: Array<[string, [string[], string[][]]]>;
  verbose: Boolean;
}

/**
 * Function that outputs history as HTML
 * @param props Props for REPLHistory, as above
 * @returns HTML to display command history
 */
export function REPLHistory(props: REPLHistoryProps) {
  return (
    <div className="repl-history" id="repl-hist" 
    aria-label={"Command History and Output Window"}
    tabIndex={4}
    aria-keyshortcuts="Escape to go to command input box."
    onKeyDown={(key) => {
        var comInput = document.getElementById("command-input");
        switch (key.code) {
          case "Escape":{
            if(comInput != null){
              comInput.focus();
            }
            break;
          }
          case "ArrowDown": {
            
          }
        }
      }} >
      {props.verbose
        ? // For verbose mode
          props.history.map((command, index) => (
            <div className="singleAns" aria-label="Single answer window">
              <div aria-label={"Previous command:" + command[0]} tabIndex={0}>
                {"Command: " + command[0]} <br></br> {"Output: "}
              </div>  
              <table className="results-table" style={{ width: 500 }} aria-label="Command Output" tabIndex={0} aria-live="polite">
                <tbody>
                  <tr className="table-row" data-testid="test:table-header" tabIndex={0}>
                    {command[1][0].map((pastResponse, index) => (
                      <th className="table-cell" key={index}>
                        {pastResponse}
                      </th>
                    ))}
                  </tr>
                  {command[1][1].map((pastResponse, index) => (
                    <tr className="table-row" data-testid="test:table-row" 
                    aria-label={"Output table row " + 
                      (index + 1).toString() + 
                      " of " + command[1][1].length.toString() + 
                      "; Contents: "+ pastResponse.toString().trim()} 
                    tabIndex={0}>
                      {pastResponse.map((val, rowID) => (
                        <td className="table-cell" key={rowID}>
                          {val}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
              <br></br>
            </div>
          ))
        : // For brief mode
          props.history.map((command, index) => (
            <div className="singleAns" aria-label="Single answer window">
              <table className="results-table" style={{ width: 500 }} tabIndex={0}>
                <tbody>
                  <tr className="table-row" data-testid="test:table-header" 
                  aria-label="Output table header" 
                  aria-description={"Contents: "+ command[1][0].toString().trim()} tabIndex={0}>
                    {command[1][0].map((pastResponse, index) => (
                      <th className="table-cell" key={index}>
                        {pastResponse}
                      </th>
                    ))}
                  </tr>
                  {command[1][1].map((pastResponse, index) => (
                    <tr 
                    className="table-row" 
                    data-testid="test:table-row" 
                    aria-label={"Output table row " + 
                      (index + 1).toString() + 
                      " of " + command[1][1].length.toString()}
                    aria-description={"Contents: "+ pastResponse.toString().trim()} 
                    tabIndex={0}>
                      {pastResponse.map((val, rowID) => (
                        <td className="table-cell" key={rowID} >
                          {val}
                        </td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
              <br></br>
            </div>
          ))}
    </div>
  );
}
