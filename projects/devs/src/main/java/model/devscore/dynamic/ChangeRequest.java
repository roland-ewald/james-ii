/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

import org.jamesii.core.model.IModel;

import model.devscore.IBasicDEVSModel;

/**
 * The Class ChangeRequest.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */

public abstract class ChangeRequest<M extends IModel> {

  /** The source of this change request. */
  private IBasicDEVSModel source;

  /**
   * Constructor.
   * 
   * @param source
   *          model of the change request
   */
  public ChangeRequest(IBasicDEVSModel source) {
    super();
    this.source = source;
  }

  /**
   * Returns a reference to the model of the ChangeRequest.
   * 
   * @return a reference to the model
   */
  public IBasicDEVSModel getSource() {
    return source;
  }

  /**
   * Applies the change to the given model.
   * 
   * @param model
   *          the model
   */
  public abstract void modifyModel(M model);

  @Override
  public String toString() {

    return this.getClass().getName() + ": [Issued by " + source.getFullName()
        + "]";

  }

}
