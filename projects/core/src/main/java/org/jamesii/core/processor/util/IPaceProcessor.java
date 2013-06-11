/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.util;

import org.jamesii.core.processor.IProcessor;

/**
 * Interface for processors which support paced modes (together with a scale
 * factor)
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IPaceProcessor extends IProcessor<Double> {

  /**
   * Set the scale factor for the conversion between wall clock and simulation
   * time.
   * 
   * @param scale
   *          value to be set as scale scale factor
   */
  void setScale(double scale);

  /**
   * Get the current scale factor for the conversion between wall clock and
   * simulation time.
   * 
   * @return the scale factor
   */
  double getScale();

}
