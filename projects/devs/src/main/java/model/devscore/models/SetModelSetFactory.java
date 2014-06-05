/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.models;

import model.devscore.models.plugintype.BaseModelSetFactory;

/**
 * A factory for creating SetModelSet objects.
 * 
 * @author Jan Himmelspach
 */
public class SetModelSetFactory extends BaseModelSetFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2278718179972252998L;

  @Override
  public IModelSet createDirect() {
    return new SetModelSet();
  }

}
