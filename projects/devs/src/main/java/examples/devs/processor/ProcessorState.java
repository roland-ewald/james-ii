/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.processor;

import org.jamesii.core.model.State;

/**
 * The state of a {@link Processor}.
 * 
 * @author Alexander Steiniger
 * 
 */
public class ProcessorState extends State {

  /**
   * The serialization id
   */
  private static final long serialVersionUID = 4819953305304242954L;

  /**
   * The phases a processor can be in
   */
  public static enum Phase {
    ACTIVE, PASSIVE
  }

  /**
   * The phase of the processor
   */
  private Phase phase = Phase.PASSIVE;

  /**
   * The time left until the next internal state transition (in case the
   * processor works on a job, the time left until the job is finished)
   */
  private double sigma = Double.POSITIVE_INFINITY;

  /**
   * The current job the processor is working on
   */
  private Job job = null;

  /**
   * Get the value of the phase.
   * @return the phase
   */
  public Phase getPhase() {
    return phase;
  }

  /**
   * Set the phase to the value passed via the phase attribute.
   * @param phase the phase to set
   */
  public void setPhase(Phase phase) {
    this.phase = phase;
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
   * @return the job
   */
  public Job getJob() {
    return job;
  }

  /**
   * Set the job to the value passed via the job attribute.
   * @param job the job to set
   */
  public void setJob(Job job) {
    this.job = job;
  }

}
