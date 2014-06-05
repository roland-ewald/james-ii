/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

import model.devscore.IBasicCoupledModel;

/**
 * The Class DynamicStructureChangeHandler.
 * 
 * @author Jan Himmelspach
 * 
 */
public class DynamicStructureChangeHandler {

  /** The model. */
  private IBasicCoupledModel model;

  /**
   * Instantiates a new dynamic structure change handler.
   * 
   * @param model
   *          the model
   */
  public DynamicStructureChangeHandler(IBasicCoupledModel model) {
    super();
    this.setModel(model);
  }

  /**
   * @return the model
   */
  public final IBasicCoupledModel getModel() {
    return model;
  }

  /**
   * @param model the model to set
   */
  public final void setModel(IBasicCoupledModel model) {
    this.model = model;
  }

}
