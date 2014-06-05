/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package test.processor.devs;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.State;

/**
 * State that records events during the execution by a processor, which can be
 * checked later.
 * 
 * Creation date: 17.08.2006
 * 
 * @author Roland Ewald
 * 
 */
public class DEVSDummyState extends State {

  /** Serialisation ID. */
  private static final long serialVersionUID = -8536200246756171492L;

  /** Counter for d_int. */
  private int deltaIntCounter = 0;

  /** Counter for d_ext. */
  private int deltaExtCounter = 0;

  /** Counter for lambda. */
  private int lambdaCounter = 0;

  /** Counter for ta. */
  private int taCounter = 0;

  /** The current state. */
  private int state = 0;

  /** Stores the last elapsed time. */
  private double lastElapsedTime = 0.0;

  /** List of received messages. */
  private List<Object> recvMsg = new ArrayList<>();

  /**
   * @see org.jamesii.core.model.AbstractState#hasChanged()
   */
  @Override
  public boolean hasChanged() {
    return false;
  }

  /**
   * @see org.jamesii.core.model.AbstractState#isChangedRR()
   */
  @Override
  public boolean isChangedRR() {
    return false;
  }

  /**
   * @see org.jamesii.core.model.AbstractState#clearChanged()
   */
  @Override
  public void clearChanged() {
  }

  public List<Object> getRecvMsg() {
    return recvMsg;
  }

  public int getTaCounter() {
    return taCounter;
  }

  public int getLambdaCounter() {
    return lambdaCounter;
  }

  public int getDeltaIntCounter() {
    return deltaIntCounter;
  }

  public int getDeltaExtCounter() {
    return deltaExtCounter;
  }

  public double getLastElapsedTime() {
    return lastElapsedTime;
  }

  /**
   * Registers an execution of delta_ext.
   */
  public void incDeltaExtCounter() {
    deltaExtCounter++;
  }

  /**
   * Registers an execution of delta_int.
   */
  public void incDeltaIntCounter() {
    deltaIntCounter++;
  }

  /**
   * Registers an execution of lambda.
   */
  public void incLambdaCounter() {
    lambdaCounter++;
  }

  /**
   * Registers an execution of ta.
   */
  public void incTaCounter() {
    taCounter++;
  }

  public void setLastElapsedTime(double elapsedTime) {
    lastElapsedTime = elapsedTime;
  }

  /**
   * Registers a transition to the next state.
   */
  public void nextState() {
    state++;
  }

  /**
   * Gets the state.
   * 
   * @return the state
   */
  public int getState() {
    return state;
  }

}
