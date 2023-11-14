package edu.brown.cs.student.main.Server.Exceptions;

/** Custom Exception class that corresponds to error_bad_json; */
public class BadJsonException extends Exception {
  private final Throwable cause;

  /** Constructor for BadJson custom exception. */
  public BadJsonException() {
    super();
    this.cause = null;
  }
}
