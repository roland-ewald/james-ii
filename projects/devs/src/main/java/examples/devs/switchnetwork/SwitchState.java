/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.switchnetwork;

import org.jamesii.core.model.State;

import examples.devs.processor.Job;

/**
 * The state of a {@link Switch} model.
 * 
 * @author Alexander Steiniger
 * 
 */
public class SwitchState extends State {

  /**
   * The serialization id
   */
  private static final long serialVersionUID = -600715282739830979L;

  public static enum Phase {
    PASSIVE, ACTIVE;
  }

  private Phase phase = Phase.PASSIVE;

  private boolean polarity = true;
  
  private double sigma = Double.POSITIVE_INFINITY;

  private Job job = null;

  /**
   * Get the value of the phase.
   * 
   * @return the phase
   */
  public Phase getPhase() {
    return phase;
  }

  /**
   * Set the phase to the value passed via the phase attribute.
   * 
   * @param phase
   *          the phase to set
   */
  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  /**
   * Get the value of the polarity.
   * 
   * @return the polarity
   */
  public boolean getPolarity() {
    return polarity;
  }

  /**
   * Set the polarity to the value passed via the polarity attribute.
   * 
   * @param polarity
   *          the polarity to set
   */
  public void setPolarity(boolean polarity) {
    this.polarity = polarity;
  }

  /**
   * Get the value of the sigma.
   * @return the sigma
   */
  public double getSigma() {
    return sigma;
  }

  /**
   * Set the sigma to the value passed via the sigma attribute.
   * @param sigma the sigma to set
   */
  public void setSigma(double sigma) {
    this.sigma = sigma;
  }

  /**
   * Get the value of the job.
   * 
   * @return the job
   */
  public Job getJob() {
    return job;
  }

  /**
   * Set the job to the value passed via the job attribute.
   * 
   * @param job
   *          the job to set
   */
  public void setJob(Job job) {
    this.job = job;
  }

}
