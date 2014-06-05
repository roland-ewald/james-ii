/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devscore;

import org.jamesii.core.processor.ITreeProcessor;

/**
 * The Interface IBasicDEVSProcessor.
 * 
 * @author Jan Himmelspach
 */
public interface IBasicDEVSProcessor extends ITreeProcessor<Double> {

  /**
   * Returns the tonie of this processor.
   * 
   * @return tonie for this processor
   */
  double getTimeOfNextInternalEvent();

}
