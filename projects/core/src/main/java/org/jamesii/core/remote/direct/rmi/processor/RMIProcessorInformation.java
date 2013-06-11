/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.processor;

import org.jamesii.core.processor.IProcessor;

/**
 * The Class ProcessorInformation.
 */
public class RMIProcessorInformation extends
    org.jamesii.core.processor.ProcessorInformation {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 4625459136949112529L;

  /** The ref. */
  private IProcessorRef ref;

  /**
   * Instantiates a new processor information.
   */
  public RMIProcessorInformation() {
    super();
    ref = null;
  }

  /**
   * The Constructor.
   * 
   * @param local
   *          the local
   */
  public RMIProcessorInformation(IProcessor local) {
    super(local);
    this.ref = null;
  }

  /**
   * The Constructor.
   * 
   * @param local
   *          the local
   * @param ref
   *          the ref
   */
  public RMIProcessorInformation(IProcessor local, IProcessorRef ref) {
    super(local);
    this.ref = ref;
  }

  /**
   * The Constructor.
   * 
   * @param ref
   *          the ref
   */
  public RMIProcessorInformation(IProcessorRef ref) {
    super();
    this.ref = ref;
  }

  /**
   * Gets the remote.
   * 
   * @return the remote
   */
  public IProcessorRef getRemote() {
    return ref;
  }

  /**
   * Sets the processor ref.
   * 
   * @param ref
   *          the ref
   */
  public void setProcessorRef(IProcessorRef ref) {
    this.ref = ref;
  }

  @Override
  public String toString() {
    return super.toString() + "\n remote:" + ref;
  }

}
