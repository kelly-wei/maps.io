package edu.brown.cs.student.main.Parsing;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an error provided to catch any error that may occur when an object is created from a row.
 */
public class FactoryFailureException extends Exception {
  final List<String> row; // Row containing the error

  /**
   * Exception generated when an issue is encountered converting a row to a developer-specified
   * object
   *
   * @param message custom error message describing the error
   * @param row the row that cannot be converted
   */
  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
  }
}
