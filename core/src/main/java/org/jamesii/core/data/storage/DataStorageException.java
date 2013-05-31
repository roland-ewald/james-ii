/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.storage;

/**
 * Exception to be thrown by data storages.
 * 
 * @author Sebastian Lieske
 */
public class DataStorageException extends RuntimeException {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7090811090659730426L;

  /**
   * Default constructor.
   */
  public DataStorageException() {
  }

  /**
   * The Constructor.
   * 
   * @param arg0
   *          the arg0
   */
  public DataStorageException(String arg0) {
    super(arg0);
  }

  /**
   * The Constructor.
   * 
   * @param arg0
   *          the arg0
   * @param arg1
   *          the arg1
   */
  public DataStorageException(String arg0, Throwable arg1) {
    super(arg0, arg1);
  }

  /**
   * The Constructor.
   * 
   * @param arg0
   *          the arg0
   */
  public DataStorageException(Throwable arg0) {
    super(arg0);
  }

}
