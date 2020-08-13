package org.sn.core.exception;

public class SnException extends Exception {

  public SnException() {
    super();
  }

  public SnException(String message) {
    super(message);
  }

  public SnException(String message, Throwable cause) {
    super(message, cause);
  }

}
