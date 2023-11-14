import { loading_datasets, dance_results, animal_results } from "./mockedJson";
import { REPLFunction } from "../REPL/REPLFunction/AccessBackend";

/**
 * Class for mocking, to access mocked data and return information
 * Contains all mocked REPLFunctions
 */

// Currently loaded file
var currentFile: [string[], string[][]] = [[], []];
// Is a valid file loaded
var isLoaded: Boolean = false;
// Does this file have headers
var hasHeaders: Boolean = false;
// What filepath are we currently accessing
var filepath: string;

export const mockBroadband : REPLFunction = function (args: Array<string>) : Promise<[string[], string[][]]> {
  return new Promise((resolve, reject) => {
    resolve([[], [["Broadband: 89% coverage"]]])
  })
}

/**
 * Mocks view.
 * @param args array of strings
 * @returns A promise that resolves with an array containing an array of strings and an array of array of strings
 */
export const mockView : REPLFunction = function (args: Array<string>) : Promise<[string[], string[][]]> {
  return new Promise((resolve, reject) => {
    if (!isLoaded) {
      resolve([[], [["Error (view): Please load a valid file before viewing"]]]);
    } else {
      resolve(currentFile);
    }
  })
}

/**
 * Mocks load.
 * @param args array of strings
 * @returns A promise that resolves with an array containing an array of strings and an array of array of strings
 */
export const mockLoad : REPLFunction = function (args: Array<string>) : Promise<[string[], string[][]]> {
  return new Promise((resolve, reject) => {
    args.unshift("load_file")
    const reqArray = args
    if (reqArray.length > 2 && reqArray[2] == "true") {
      hasHeaders = true;
    }

    const reqDataset = loading_datasets.get(reqArray[1]);

    if (reqDataset == undefined) {
      isLoaded = false;
      currentFile = [[], []];
      filepath = "";
      resolve([[], [["Error (load_file): No file found at filepath"]]]);
    } else {
      currentFile = reqDataset;
      isLoaded = true;
      filepath = reqArray[1];

      resolve([[], [["Success loading file!"]]]);
    }
  })
}

/**
 * Mocks search.
 * @param args array of strings
 * @returns A promise that resolves with an array containing an array of strings and an array of array of strings. (Our result)
 */
export const mockSearch : REPLFunction = function (args: Array<string>) : Promise<[string[], string[][]]> {
  return new Promise((resolve, reject) => {
    args.unshift("search")
    const reqArray = args
    if (!isLoaded || filepath == "") {
      resolve([
        [],
        [["Error (search): Please load a valid file before searching"]],
      ]);
    }

    if (filepath == "data/animals_data.csv") {
      if (reqArray.length < 3) {
        const animalRows = animal_results.get(reqArray[1]);
        if (typeof animalRows == "undefined") {
          resolve([
            [],
            [["Error (search): Please input a valid column and value"]],
          ]);
        } else {resolve([currentFile[0], animalRows])};
      }

      const animalRows = animal_results.get(reqArray[1] + " " + reqArray[2]);
      if (animalRows == undefined) {
        resolve([
          [],
          [["Error (search): Please input a valid column and value"]],
        ]);
      } else {resolve([currentFile[0], animalRows])};
    }

    if (filepath == "data/dance_data.csv") {
      if (reqArray.length < 3) {
        const danceRows = dance_results.get(reqArray[1]);
        if (danceRows == undefined) {
          resolve([
            [],
            [["Error (search): Please input a valid column and value"]],
          ]);
        } else {resolve([currentFile[0], danceRows])};
      }

      const danceRows = dance_results.get(reqArray[1] + " " + reqArray[2]);
      if (danceRows == undefined) {
        resolve([
          [],
          [["Error (search): Please input a valid column and value"]],
        ]);
      } else {resolve([currentFile[0], danceRows])};
    }
  })
}

