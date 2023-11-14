import { Dispatch, SetStateAction } from 'react';

interface ControlledInputProps {
  value: string, 
  setValue: Dispatch<SetStateAction<string>>,
  ariaLabel: string
}
  
export function ControlledInput({value, setValue, ariaLabel}: ControlledInputProps) {
  return (
    <input type="text" className="repl-command-box"
        value={value} 
        placeholder="Enter command here!"
        onChange={(ev) => setValue(ev.target.value)}
        aria-label={ariaLabel}
        style={{width:"28vw"}}
    ></input>
  );
}

