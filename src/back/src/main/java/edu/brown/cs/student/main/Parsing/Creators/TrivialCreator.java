package edu.brown.cs.student.main.Parsing.Creators;

import edu.brown.cs.student.main.Parsing.FactoryFailureException;
import java.util.List;

/** A class to trivially create a list of strings from a list of strings */
public class TrivialCreator implements CreatorFromRow<List<String>> {

  /** Constructor for TrivialCreator */
  public TrivialCreator() {}

  /**
   * Trivially returns a list of strings from a list of strings
   *
   * @param row List of Strings representing row to be converted
   * @return Original row to be converted
   * @throws FactoryFailureException if conversion fails
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}
