/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * Title:        CoSA: BasicCAProcessor
 * Description:  Basic processor for simulating cellular automata (CA) models
 * Copyright:    Copyright (c) 2004
 * Company:      University of Rostock, Faculty of Computer Science
 *               Modeling and Simulation group
 * Created on 08.06.2004
 * @author       Jan Himmelspach
 * @version      1.0
 */
package org.jamesii.simulator.cacore;

import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.RunnableProcessor;
import org.jamesii.model.ca.grid.ICAGrid;

/**
 * The Class BasicCAProcessor.
 */
public abstract class BasicCAProcessor extends RunnableProcessor<Double> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8988399857138725249L;

  /**
   * The Constructor.
   * 
   * @param model
   *          the model
   */
  public BasicCAProcessor(IModel model) {
    super(model);
    setState(new CAProcessorState());
  }

  /**
   * Gets the cA state.
   * 
   * @return the cA state
   */
  protected CAProcessorState getCAState() {
    return (CAProcessorState) getState();
  }

  /**
   * This default implementation assumes that the model is a grid and that the
   * complete grid shall be returned.
   * 
   * @return interface reference (IGrid) to the CA grid model
   */
  protected ICAGrid getGrid() {
    return (ICAGrid) getModel();
  }

}
