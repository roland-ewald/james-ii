package org.jamesii.core.data.runtime;

/**
 * Exception for errors that occur in CSV-formatted data or in data that is
 * about to be formatted as CSV.
 * <p>
 * Instances of this class may have a position of the error stored, available
 * via {@link #getErrorPosition()}.
 * 
 * @author Johannes Rössel
 */
public class CSVFormatException extends RuntimeException {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6202111556565542531L;

  /**
   * Initialises a new instance of the {@link CSVFormatException} class with the
   * given cause.
   * 
   * @param cause
   *          The cause of this exception. May be {@code null}.
   */
  public CSVFormatException(Throwable cause) {
    this(null, cause, -1);
  }

  /**
   * Initialises a new instance of the {@link CSVFormatException} class with the
   * given detail message and cause.
   * 
   * @param message
   *          A {@link String} containing details about the error occurred.
   * @param cause
   *          The cause of this exception. May be {@code null}.
   */
  public CSVFormatException(String message, Throwable cause) {
    this(message, cause, -1);
  }

  /**
   * Initialises a new instance of the {@link CSVFormatException} class with the
   * given detail message, cause and error position.
   * 
   * @param message
   *          A {@link String} containing details about the error occurred.
   * @param cause
   *          The cause of this exception. May be {@code null}.
   * @param errorPosition
   *          A position in a field or line where the error occurred.
   */
  public CSVFormatException(String message, Throwable cause, int errorPosition) {
    super(message, cause);
    this.errorPosition = errorPosition;
  }

  /**
   * Initialises a new instance of the {@link CSVFormatException} class with the
   * given detail message and error position.
   * 
   * @param message
   *          A {@link String} containing details about the error occurred.
   * @param errorPosition
   *          A position in a field or line where the error occurred.
   */
  public CSVFormatException(String message, int errorPosition) {
    this(message, null, errorPosition);
  }

  /**
   * Initialises a new instance of the {@link CSVFormatException} class with the
   * given detail message.
   * 
   * @param message
   *          A {@link String} containing details about the error occurred.
   */
  public CSVFormatException(String message) {
    this(message, null, -1);
  }

  /**
   * Initialises a new instance of the {@link CSVFormatException} class with no
   * detail message or error position.
   */
  public CSVFormatException() {
    this(null, null, -1);
  }

  /** Optional position of the error occurred. */
  private int errorPosition;

  /**
   * Retrieves the position where the error occurred in the input data.
   * 
   * @return The error position or {@code -1} if unknown.
   */
  public int getErrorPosition() {
    return errorPosition;
  }
}
