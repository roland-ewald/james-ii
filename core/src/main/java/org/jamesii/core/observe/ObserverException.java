package org.jamesii.core.observe;

/**
 * The Class ObserverException. This type of exception should be used to report
 * exception arising out of the observe prcesses.
 */
public class ObserverException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3809881753072546244L;

  /**
   * Instantiates a new observer exception.
   * 
   * @param msg
   *          the msg to be printed
   */
  public ObserverException(String msg) {
    super(msg);
  }

  /**
   * Instantiates a new observer exception.
   */
  public ObserverException() {
    super();
  }

}
