/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.processor;

/**
 * A simple container that represents an abstract job. 
 *
 * @author Alexander Steiniger
 *
 */
public class Job {
  
  private Object job = null;
  
  public Job(Object job) {
    this.job = job;
  }

  /**
   * Get the value of the job.
   * @return the job
   */
  public Object getJob() {
    return job;
  }

  /**
   * Set the job to the value passed via the job attribute.
   * @param job the job to set
   */
  public void setJob(Object job) {
    this.job = job;
  }
  
  @Override
  public String toString() {
    return job.toString();
  }

}
