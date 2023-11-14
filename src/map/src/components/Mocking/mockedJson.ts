/**
 * File holding mocked data for frontend demo
 */

/**
 * Datasets to be loaded
 */
export const loading_datasets = new Map<string, [string[], string[][]]>([
  [
    "data/dance_data.csv",
    [
      ["Style", "Cheerfulness", "Shoes"],
      [
        ["Contemporary", "Variable", "No"],
        ["Jazz", "Happy", "Yes"],
        ["Ballet", "Sad", "Yes"],
        ["Tap", "Happy", "Yes"],
      ],
    ],
  ],
  [
    "data/animals_data.csv",
    [
      [],
      [
        ["Bob", "30", "Tortoise", "123"],
        ["Quack", "1", "Duck", "203"],
        ["Moo", "3", "Elephant", "20"],
      ],
    ],
  ],
  ["data/oneCol_data.csv", [[], [["this"], ["very"], ["singular"]]]],
]);

/**
 * Dance search results
 */
export const dance_results = new Map<string, string[][]>([
  [
    "Shoes Yes",
    [
      ["Jazz", "Happy", "Yes"],
      ["Ballet", "Sad", "Yes"],
      ["Tap", "Happy", "Yes"],
    ],
  ],
  [
    "Yes",
    [
      ["Jazz", "Happy", "Yes"],
      ["Ballet", "Sad", "Yes"],
      ["Tap", "Happy", "Yes"],
    ],
  ],
  ["Style Yes", [["No rows found for Yes in column Style"]]],
  ["Shoes No", [["Contemporary", "Variable", "No"]]],
  ["0 Jazz", [["Jazz", "Happy", "Yes"]]],
  ["0 Ballet", [["Ballet", "Sad", "Yes"]]],
  ["0 Hip-hop", [["No rows found for Hip-hop in column 0"]]],
  ["4 Jazz", [["Column index out of bounds"]]],
]);

/**
 * Animal search results
 */
export const animal_results = new Map<string, string[][]>([
  ["0 Moo", [["Moo", "3", "Elephant", "20"]]],
  ["Moo", [["Moo", "3", "Elephant", "20"]]],
  ["Header Moo", [["File must have headers to search by header value"]]],
  ["4 33", [["Column index out of bounds"]]],
]);
