/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

import java.io.Serializable;

import org.jamesii.core.util.Semaphore;

/**
 * The NextStepThread is a thread which constantly runs and calls in an
 * "endless" loop the nextStep method of the associated processor.
 * 
 * @author Jan Himmelspach
 */
public class NextStepThread extends Thread implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 7590723301002040270L;

  /** The is running. */
  private boolean isRunning = false;

  /** The pause. */
  private Semaphore pause;

  /** The processor. */
  private Processor processor;

  /** The stop. */
  private boolean stop = false;

  /**
   * Creates a thread object and assigns the associated processor.
   * 
   * @param processor
   *          which will be executed through this thread
   */
  public NextStepThread(Processor processor) {
    super(processor.getClass().getName() + " thread ("
        + processor.getModelName() + ")");

    this.processor = processor;
    // this semaphore can be used by the nextStep method implementation by using
    // the P and V operations of this thread
    pause = new Semaphore(-1);
  }

  /**
   * Checks if is running.
   * 
   * @return true, if is running
   */
  public boolean isRunning() {
    return isRunning;
  }

  /**
   * Checks if is stopping.
   * 
   * @return true, if is stopping
   */
  public boolean isStopping() {
    return stop;
  }

  /**
   * If once started we will simply call the processors nextStep method til we
   * are terminated.
   */
  @Override
  public void run() {
    isRunning = true;
    // System.out.println ("next round
    // "+((IBasicDEVSModel)processor.getModel()).getFullName());
    // repeat endlessly
    while ((this != null) && (!stop)) {
      // execute the processor's next step
      // System.out.println("I do the next step!");
      processor.executeNextStep();

    }
  }

  /**
   * Stops the thread as soon as possible.
   */
  public void stopSoon() {
    stop = true;
  }

  /**
   * Pause this thread until V is called!
   */
  /*
   * public void p () { //System.out.println("#############################
   * Sleeping: "+((IBasicDEVSModel)processor.getModel()).getFullName());
   * pause.p(); }
   */

  /**
   * Run again
   */
  /*
   * public void v () { //System.out.println("#############################
   * Running: "+((IBasicDEVSModel)processor.getModel()).getFullName());
   * pause.v(); }
   */

}
