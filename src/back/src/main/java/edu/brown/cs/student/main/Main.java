package edu.brown.cs.student.main;

import edu.brown.cs.student.main.Parsing.Parser;
import edu.brown.cs.student.main.Parsing.ProcessedFile;
import edu.brown.cs.student.main.Searching.SearcherAll;
import edu.brown.cs.student.main.Searching.SearcherIdentifier;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * The Main class – User-facing, allows user to find rows with a specific search term
 *
 * <p>Arguments: Without Column Identifier: 1. Filepath 2. Search Term 3. Are column headers
 * present? ("true"/"false") With Column Identifier: 1. Filepath 2. Search Term 3. Are column
 * headers present? ("true"/"false") 4. Desired column identifier 5. Is the column identifier an
 * index? ("true"/"false")
 */
public final class Main {

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  /**
   * Method that cleans up user arguments, parses, and searches the file.
   *
   * @param args An array of command line arguments
   */
  private Main(String[] args) {
    int argNum = args.length;
    if ((argNum != 3) && (argNum != 5)) { // Wrong number of arguments
      System.err.println("Please input the correct number of arguments");
      System.exit(1);
    }

    Reader file = null;
    try {
      file = new FileReader(args[0]);
    } catch (IOException e) { // Error with filepath – cannot convert to FileReader
      System.err.println("Encountered an error with file input: " + e.getMessage());
      System.exit(1);
    }

    String searchTerm = (args[1]);
    Boolean headers = Boolean.valueOf(args[2]); // Defaults to "false" in case of unexpected input

    // Creation of parser
    Parser<List<String>> parserUtil = new Parser<>(file, headers);
    try {
      parserUtil.packageFile();
    } catch (IOException e) {
      System.err.println("Error in file packaging");
      System.exit(1);
    }

    ProcessedFile<List<String>> finalFile = parserUtil.getFinalFile();

    if (argNum == 5) { // Identifier in use
      // Clean up extra arguments for SearcherIdentifier
      String columnIdentifier = args[3];
      Boolean isIndex = Boolean.valueOf(args[4]);
      // Create searcher
      SearcherIdentifier searchCol =
          new SearcherIdentifier(finalFile, searchTerm, columnIdentifier, isIndex);
      try {
        List<List<String>> searchResult = searchCol.search();
        System.out.println(searchResult);
        if (searchResult.size() == 0) {
          System.out.println("No results found"); // Clarify to user that search has been run
        }
      } catch (IOException e) { // Input error – likely with file
        System.err.println("Encountered an error with inputs:" + e.getMessage());
        System.exit(1);
      } catch (NumberFormatException e) { // Column index given is not a number
        System.err.println("Column index must be a number");
        System.exit(1);
      }
    } else { // Identifier not in use
      SearcherAll searchAll = new SearcherAll(finalFile, searchTerm);
      try {
        List<List<String>> searchResult = searchAll.search();
        System.out.println(searchResult);
        if (searchResult.size() == 0) { // Clarify to user that search has been run
          System.out.println("No results found");
        }
      } catch (IOException e) { // Input error – likely with file
        System.err.println("Encountered an error with inputs:" + e.getMessage());
        System.exit(1);
      }
    }
  }

  private void run() {}
}
