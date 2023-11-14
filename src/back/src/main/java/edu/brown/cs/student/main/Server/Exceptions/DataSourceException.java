package edu.brown.cs.student.main.Server.Exceptions;

/** Custom exception class corresponding to error_datasource. */
public class DataSourceException extends Exception {
  private final Throwable cause;

  /** Constructor for custom exception class. */
  public DataSourceException() {
    super();
    this.cause = null;
  }
}
