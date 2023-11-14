package edu.brown.cs.student.main.Parsing;

import edu.brown.cs.student.main.Parsing.Creators.CreatorFromRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class for conversion of a Reader object to a ProcessedFile object suitable for further searching.
 *
 * @param <T> Generic type representing row objects
 */
public class Parser<T> {

  private Reader genReader;
  private CreatorFromRow<T> rowTransformer;
  private Boolean headers;
  private ProcessedFile<T> finalFile;

  /**
   * Constructor for Parser class – if rows are desired in default List of String representation
   *
   * @param genReader Reader object containing data to be converted
   * @param headers Boolean indicating if the Reader object has headers
   */
  public Parser(Reader genReader, Boolean headers) {
    this.genReader = genReader;
    this.rowTransformer = null;
    this.headers = headers;
  }

  /**
   * Constructor for Parser class – if rows are desired in custom Object type
   *
   * @param genReader Reader object containing data to be converted
   * @param rowTransformer Custom developer-provided CreatorFromRow
   * @param headers Boolean indicating if the Reader object has headers
   */
  public Parser(Reader genReader, CreatorFromRow<T> rowTransformer, Boolean headers) {
    this.genReader = genReader;
    this.rowTransformer = rowTransformer;
    this.headers = headers;
  }

  // Regex for comma-splitting of a string
  // Credit goes to the CS32 Staff Team!
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * Elimiate a single instance of leading or trailing double-quote, and replace pairs of double
   * quotes with singles.
   *
   * @param arg the string to process
   * @return the postprocessed string
   */
  public static String postprocess(String arg) {
    return arg
        // Remove extra spaces at beginning and end of the line
        .trim()
        // Remove a beginning quote, if present
        .replaceAll("^\"", "")
        // Remove an ending quote, if present
        .replaceAll("\"$", "")
        // Replace double-double-quotes with double-quotes
        .replaceAll("\"\"", "\"");
  }

  /**
   * Helper: Converts a string representing a row into a List<String> using comma separation
   *
   * @param singleRow String representing a single row
   * @return List<String> representing a single row (split on comma separation)
   */
  private List<String> rowToList(String singleRow) {
    // Splits string on commas using provided regex
    String[] splitRow = regexSplitCSVRow.split(singleRow);
    // Converts String[] to List for ease of later use
    List<String> rowAsList = Arrays.asList(splitRow);
    List<String> retList = new ArrayList<>();

    for (String arg : rowAsList) {
      retList.add(postprocess(arg));
    }
    return new ArrayList(retList);
  }

  /**
   * Method for the conversion of a Reader object to a ProcessedFile object
   *
   * @return ProcessedFile object with content as List of List of Strings
   * @throws IOException Indicates issue with file/filepath, passed to Main for user review
   */
  public void packageFile() throws IOException {
    // List holding contents of the file
    List<List<String>> contList = new ArrayList<>();

    // Creation of BufferedReader object for line-by-line reading
    BufferedReader bReader = new BufferedReader(this.genReader);
    String oneLine = bReader.readLine();

    if (this.headers) { // Headers are present
      // Isolate headers for separate parsing
      List<String> parsedHeaders = rowToList(oneLine);
      oneLine = bReader.readLine();
      while (oneLine != null) { // Convert each row and add to content list
        List<String> convertedLine = rowToList(oneLine);
        contList.add(convertedLine);
        oneLine = bReader.readLine();
      }
      bReader.close(); // Close BufferedReader
      ProcessedFile withHeaders = new ProcessedFile(parsedHeaders, contList);
      this.finalFile = withHeaders;
    } else { // No headers
      while (oneLine != null) { // Convert each row and add to content list
        List<String> convertedLine = rowToList(oneLine);
        contList.add(convertedLine);
        oneLine = bReader.readLine();
      }
      bReader.close(); // Close BufferedReader
      ProcessedFile noHeaders = new ProcessedFile(null, contList);
      this.finalFile = noHeaders;
    }
  }

  public ProcessedFile<T> getFinalFile() {
    return this.finalFile;
  }

  /**
   * Converts the contents of a ProcessedFile from the default of List of Strings to a
   * developer-specified object type.
   *
   * @param stringFile ProcessedFile, with list of contents of type List of Strings
   * @return ProcessedFile, with list of contents of type T
   * @throws FactoryFailureException Indicates an error with converting a row to type T
   */
  public ProcessedFile<T> stringToGeneric(ProcessedFile<List<String>> stringFile)
      throws FactoryFailureException {

    List<T> transformedList = new ArrayList<>();
    List<List<String>> stringList = stringFile.getContent();

    for (List<String> parsedRow : stringList) { // Convert each row into type T
      T transformedRow = this.rowTransformer.create(parsedRow);
      transformedList.add(transformedRow);
    }

    return new ProcessedFile(stringFile.getHeaders(), transformedList);
  }
}
