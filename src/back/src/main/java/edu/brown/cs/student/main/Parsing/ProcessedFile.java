package edu.brown.cs.student.main.Parsing;

import java.util.List;
import java.util.Objects;

/**
 * Class to hold Processed Files – contains a List of String of headers and a List of row object
 * types. Used after file parsing
 *
 * @param <T> Developer-specified row objects
 */
public class ProcessedFile<T> {

  List<String> headers;
  List<T> content;

  /**
   * Constructor for ProcessedFile
   *
   * @param headers Headers of the file
   * @param content Content of the file, in developer-specified type
   */
  public ProcessedFile(List<String> headers, List<T> content) {
    this.headers = headers;
    this.content = content;
  }

  /**
   * Getter for headers of the file
   *
   * @return List of String of the headers of the file
   */
  public List<String> getHeaders() {
    return this.headers;
  }

  /**
   * Getter for contents of the file
   *
   * @return List of the row objects of the file
   */
  public List<T> getContent() {
    return this.content;
  }

  /**
   * Overridden equals method for ProcessedFile. Generated by IntelliJ, as demonstrated in lecture
   *
   * @param o object to be compared for equality
   * @return Boolean representing whether the two objects are equal
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    ProcessedFile<T> that = (ProcessedFile<T>) o;
    return Objects.equals(this.headers, that.headers) && Objects.equals(this.content, that.content);
  }

  /**
   * Overridden hashing function for ProcessedFiles Generated by IntelliJ, as demonstrated in
   * lecture
   *
   * @return int representing the hashCode of the file
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.headers, this.content);
  }
}