/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;

import org.jamesii.core.model.AbstractState;
import org.jamesii.core.model.State;

/**
 * The Interface IBasicAtomicModel.
 * 
 * All methods herein are methods for simulation algorithm access to the model.
 * Thus a modeler usually does not need to get in touch with this interface at
 * all. An example therefore is the {@link model.devscore.BasicAtomicModel}
 * class where the general DEVS methods are defined as abstract methods, these
 * have to be replaced by the modeler, while the methods of this interface are
 * completely realized.
 * 
 * @author Jan Himmelspach
 * @param <S>
 *          the type of the state
 */
public interface IBasicAtomicModel<S extends AbstractState> extends
    IBasicDEVSModel {

  /**
   * Method declaration for the external transition function. This method is
   * called if an atomic model has to do an internal transition, that's due if
   * it is an influencee. If it's additionally imminent the deltaCon function
   * will be called instead. The elapsed time parameter contains the difference
   * between the tole and the current time. <br>
   * The deltaExtSim method is called from the model executing processor. After
   * executing the user defined deltaExternal method it has to call the
   * inherited changed method for informing any attached observed that something
   * has happened inside the model.
   * 
   * @param elapsedTime
   *          - the time elapsed since the last state transition
   */
  void deltaExternalSim(double elapsedTime);

  /**
   * Method declaration for the internal transition function. This method is
   * called if an atomic model has to do an internal transition, that's due if
   * it is imminent. If it's additionally an influencee the deltaCon function
   * will be called instead.<br>
   * 
   * The deltaInt method is called from the model executing processor. After
   * executing the user defined deltaInternal method it has to call the
   * inherited changed method for informing any attached observed that something
   * has happened inside the model.
   * 
   */
  void deltaInternalSim();

  /**
   * Returns a reference to the current state of the model.
   * 
   * @return reference to the model state
   * 
   */
  AbstractState getState();

  /**
   * Inform observer and reset state change flag (to false).
   * 
   * @see State#isChangedRR() 
   * 
   * @return the stateis changed rr
   * 
   */
  boolean getStateisChangedRR();

  /**
   * The lambda method of this model. In DEVS formalisms the lambda method is
   * called before the {@link #deltaInternalSim()} method is called by a
   * simulation algorithm. <br>
   * Afterwards there might be values in the output ports of the model. The
   * state should not be modified if this method is executed.
   * 
   */
  void lambdaSim();

  /**
   * Sets the state of this model.
   * 
   * @param state
   *          new state of the model
   * 
   */
  void setState(AbstractState state);

  /**
   * The timeAdvance method of this model.
   * 
   * @return the time of the next event of this model
   * 
   */
  double timeAdvanceSim();

}
