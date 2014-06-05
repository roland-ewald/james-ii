/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.abstractsequential;

import simulator.devscore.IBasicDEVSProcessor;

/**
 * The Interface IAbstractSequentialProcessor.
 * 
 * @author Jan Himmelspach
 */
public interface IAbstractSequentialProcessor extends IBasicDEVSProcessor {

  /**
   * Do remainder.
   * 
   * @param time
   *          the time
   * 
   * @return the double
   */
  double doRemainder(Double time);

  /**
   * Gets the outputs. This means we will execute the lambda function of an
   * atomic model or hand over to another coordinator for selecting the model to
   * be executed.
   * 
   * @return the outputs
   */
  void getOutputs();

  /**
   * Inits the.
   * 
   * @param time
   *          the time
   */
  void init(double time);

}
