package edu.brown.cs.student.main.Server.Exceptions;

/** Custom exception class corresponding to error_bad_request. */
public class BadRequestException extends Exception {
  private final Throwable cause;

  /** Constructor of custom exception class. */
  public BadRequestException() {
    super();
    this.cause = null;
  }
}
