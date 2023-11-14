package edu.brown.cs.student.main.Searching;

import edu.brown.cs.student.main.Parsing.ProcessedFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Searcher class that specifically searches a file with rows in List of String format. Only a
 * specified column is searched – for use if there is a column identifier
 */
public class SearcherIdentifier implements Searcher<List<String>> {

  private ProcessedFile finalFile;
  private String searchTerm;
  private String columnIdentifier;
  private Boolean isIndex;

  /**
   * SearcherIdentifier constructor
   *
   * @param searchTerm Search term to look for
   * @param columnIdentifier Column to search (index or name)
   * @param isIndex Indicates if a column identifier is an index or a column name
   */
  public SearcherIdentifier(
      ProcessedFile finalFile, String searchTerm, String columnIdentifier, Boolean isIndex) {
    this.finalFile = finalFile;
    this.searchTerm = searchTerm;
    this.columnIdentifier = columnIdentifier;
    this.isIndex = isIndex;
  }

  /**
   * Searches a List of List of Strings for List of Strings containing the search term in the
   * specified column
   *
   * @return List of List of String representing rows that fit the search criteria above
   * @throws IOException representing issue with file input
   * @throws NumberFormatException indicating that a non-integer was passed as a column index
   */
  public List<List<String>> search() throws IOException, NumberFormatException {
    // Parse file and obtain content
    List<List<String>> contents = this.finalFile.getContent();

    Integer toSearch = -1;
    if (isIndex) {
      // Column Identifier is intended to be an index
      toSearch = Integer.valueOf(columnIdentifier);
    } else {
      // Column Identifier is intended to be a name – find the index of the name
      List<String> colHeaders = this.finalFile.getHeaders();
      toSearch = colHeaders.indexOf(columnIdentifier);
    }

    // The column requested does not exist
    if (toSearch == -1) {
      throw new IOException("Column requested is not found");
    }

    // Container for search results
    List<List<String>> foundList = new ArrayList<>();

    // Case-insensitive results search
    for (List<String> parsedRow : contents) {
      String cellLower = (parsedRow.get(toSearch)).toLowerCase();
      if (cellLower.equals((this.searchTerm).toLowerCase())) {
        foundList.add(parsedRow);
      }
    }

    return new ArrayList<>(foundList);
  }
}
