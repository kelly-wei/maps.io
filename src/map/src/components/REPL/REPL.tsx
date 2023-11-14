import { useState } from "react";
import React from "react";
import { Input } from "./Input";
import "../../styles/REPL.css"
import { REPLHistory } from "./REPLHistory";
import MapBox from "../map/MapBox";
import { getData } from "./REPLFunction/AccessBackend";

/**
 * Overall REPL class
 */

/**
 * Class to group REPL components
 * @returns HTML to display all REPL components
 */
export default function REPL() {
  const [history, setHistory] = useState<
    Array<[string, [string[], string[][]]]>
  >([]);

  const [data, setData] = useState<GeoJSON.FeatureCollection | undefined>();

  const [showCommands, setShow] = useState<boolean>(false);
  
  // When setHistory is called in REPLInput, this method will wait for the layout change to take place and then set the focus to the output.
  React.useLayoutEffect(() => {
    var resultsTables = document.querySelectorAll<HTMLTableElement>('.results-table')
    if(resultsTables != null){
      var resultsTable = resultsTables.item(resultsTables.length-1)
      if (resultsTable != null){
        resultsTable.focus()
      }
    }
  })

  /**
   * This returns
   * 1) the REPL History if the user decides to use the left or right arrow to see previous inputs
   * 2) The Input div
   * 3) The MapBox
   * 
   * This div also allows the user to use the Enter key in order to view commands as a keyboard shortcut
   */
  return (
    <div className="repl" aria-label={"Please use the Tab key to navigate, Enter key to submit, and Equal key to view valid commands."}onKeyDown={(key) => {
      switch (key.code) {
        case "Equal":{
          setShow(!showCommands)
        }
      }
    }} >

        <div >
            <Input
                history={history}
                setHistory={setHistory}
                highlightedData={data}
                setHighlight={setData}
                setShowCommands={setShow}
                showCommands={showCommands}
            ></Input>

        </div>

        <div>
            <MapBox highlighted={data}></MapBox>
        </div>

      <hr></hr>
    </div>
  );
}