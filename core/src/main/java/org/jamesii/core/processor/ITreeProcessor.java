/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor;

/**
 * The Interface ITreeProcessor.
 */
public interface ITreeProcessor<TimeBase extends Comparable<TimeBase>> extends
    IProcessor<TimeBase> {

  /**
   * Gets the parent.
   * 
   * @return the parent
   */
  IProcessor<TimeBase> getParent();

  /**
   * Sets the parent.
   * 
   * @param parent
   *          the new parent
   */
  void setParent(IProcessor<TimeBase> parent);

  /**
   * Adds the child.
   * 
   * @param proc
   *          the proc
   */
  void addChild(IProcessor<TimeBase> proc);

  /**
   * Removes the child.
   * 
   * @param proc
   *          the proc
   */
  void removeChild(IProcessor<TimeBase> proc);

}
