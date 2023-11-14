import "./styles/App.css";
import "./components/REPL/REPL"
import REPL from "./components/REPL/REPL";

/**
 * This is the main class that runs our app.
 * @returns 
 */
function App() {
  return <div className="App">

    <REPL></REPL>

  </div>
}

export default App;
