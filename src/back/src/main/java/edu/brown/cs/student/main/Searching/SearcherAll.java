package edu.brown.cs.student.main.Searching;

import edu.brown.cs.student.main.Parsing.ProcessedFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Searcher class that specifically searches a file with rows in List of String format. The entire
 * file is searched
 */
public class SearcherAll implements Searcher<List<String>> {
  private ProcessedFile finalFile;
  private String searchTerm;

  /**
   * Constructor for SearcherAll
   *
   * @param searchTerm Search term to look for
   */
  public SearcherAll(ProcessedFile finalFile, String searchTerm) {
    this.finalFile = finalFile;
    this.searchTerm = searchTerm;
  }

  /**
   * Searches a List of List of String for Lists of String containing the search term
   *
   * @return List of List of String representing rows that fit the search criteria above
   * @throws IOException representing issue with file input
   */
  public List<List<String>> search() throws IOException {
    // Parse file and obtain content
    List<List<String>> parsedFile = finalFile.getContent();
    List<List<String>> foundList = new ArrayList<>();

    // Case-insensitive results search
    for (List<String> parsedRow : parsedFile) {
      for (String cellValue : parsedRow) {
        String cellLower = cellValue.toLowerCase();
        if (cellLower.equals((this.searchTerm).toLowerCase())) {
          foundList.add(parsedRow);
          break; // multiple matches in a row will not be added multiple times.
        }
      }
    }

    return new ArrayList(foundList);
  }
}
