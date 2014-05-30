/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner;

/**
 * An exception which might be thrown by task runner in case that the setup of an computation task failed.
 *
 * LICENCE: JAMESLIC
 * @author Jan Himmelspach
 *
 */
public class ComputationTaskSetupException extends RuntimeException {

  /**
   * 
   */
  private static final long serialVersionUID = -3180976967005320060L;

  public ComputationTaskSetupException() {
    super();
    // TODO Auto-generated constructor stub
  }

  public ComputationTaskSetupException(String arg0, Throwable arg1,
      boolean arg2, boolean arg3) {
    super(arg0, arg1, arg2, arg3);
    // TODO Auto-generated constructor stub
  }

  public ComputationTaskSetupException(String arg0, Throwable arg1) {
    super(arg0, arg1);
    // TODO Auto-generated constructor stub
  }

  public ComputationTaskSetupException(String arg0) {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

  public ComputationTaskSetupException(Throwable arg0) {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

}
