/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devs;

import java.util.Collection;

import model.devscore.IBasicCoupledModel;
import model.devscore.IBasicDEVSModel;

/**
 * The Interface ICoupledModel.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public interface ICoupledModel extends IBasicCoupledModel {

  /**
   * The select function of a classic DEVS coupled model.
   * 
   * @param setOfModels
   *          the set of models which can be imminent in the current step (the
   *          tonies of these models are equal to the current simulation run
   *          time) - one of these has to be executed
   * 
   * @return the devs model which got selected and which will execute an
   *         internal state transition
   */
  IBasicDEVSModel select(Collection<IBasicDEVSModel> setOfModels);

}
