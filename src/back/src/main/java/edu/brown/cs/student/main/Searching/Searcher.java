package edu.brown.cs.student.main.Searching;

import java.io.IOException;
import java.util.List;

/**
 * Interface enforcing the search() method for all Searcher classes
 *
 * @param <T> Generic type for searching
 */
public interface Searcher<T> {

  /**
   * Method to search a ProcessedFile for particular objects of a developer-specified type
   *
   * @return List of T objects satisfying a specified criteria
   * @throws IOException Indicating issue with file to search
   */
  List<T> search() throws IOException;
}
