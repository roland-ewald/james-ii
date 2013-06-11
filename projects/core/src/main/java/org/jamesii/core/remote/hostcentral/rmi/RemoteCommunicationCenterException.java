/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.rmi;

public class RemoteCommunicationCenterException extends RuntimeException {


  /**
   * 
   */
  private static final long serialVersionUID = 8561307231394245226L;
  
  public RemoteCommunicationCenterException() {
    super();
  }

  public RemoteCommunicationCenterException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);    
  }

  public RemoteCommunicationCenterException(String message, Throwable cause) {
    super(message, cause);
  }

  public RemoteCommunicationCenterException(String message) {
    super(message);
  }

  public RemoteCommunicationCenterException(Throwable cause) {
    super(cause);
  }

}
