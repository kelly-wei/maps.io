package edu.brown.cs.student.main.Parsing.Creators;

import edu.brown.cs.student.main.Parsing.FactoryFailureException;
import java.util.List;

/**
 * This interface defines a method that allows the CSV parser to convert each row into an object of
 * some arbitrary passed type.
 */
public interface CreatorFromRow<T> {

  /**
   * Converts a List of Strings representing a single row into a developer-specified object type
   *
   * @param row List of Strings representing row to be converted
   * @return Object representing row, of developer-specified type
   * @throws FactoryFailureException indicating error converting row to object often due to type
   *     mismatches
   */
  T create(List<String> row) throws FactoryFailureException;
}
