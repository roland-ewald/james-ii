/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.processor;

import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.remote.hostcentral.IObjectId;

/**
 * The Class ProcessorInformation.
 * 
 * @author Simon Bartels
 */
public class HostCentralProcessorInformation extends
    org.jamesii.core.processor.ProcessorInformation {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 4625459136949112529L;

  /** The ref. */
  private IObjectId ref;

  /**
   * Instantiates a new processor information.
   */
  public HostCentralProcessorInformation() {
    super();
    ref = null;
  }

  /**
   * The Constructor.
   * 
   * @param local
   *          the local
   */
  public HostCentralProcessorInformation(IProcessor local) {
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
  public HostCentralProcessorInformation(IProcessor local, IObjectId ref) {
    super(local);
    this.ref = ref;
  }

  /**
   * The Constructor.
   * 
   * @param ref
   *          the ref
   */
  public HostCentralProcessorInformation(IObjectId ref) {
    super();
    this.ref = ref;
  }

  /**
   * Gets the remote.
   * 
   * @return the remote
   */
  public IObjectId getRemote() {
    return ref;
  }

  /**
   * Sets the processor ref.
   * 
   * @param ref
   *          the ref
   */
  public void setProcessorRef(IObjectId ref) {
    this.ref = ref;
  }

  @Override
  public String toString() {
    return super.toString() + "\n remote:" + ref;
  }

}
