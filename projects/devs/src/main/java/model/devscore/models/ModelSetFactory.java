/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.models;

import model.devscore.models.plugintype.BaseModelSetFactory;

/**
 * A factory for creating ModelSet objects.
 * 
 * @author Jan Himmelspach
 */
public class ModelSetFactory extends BaseModelSetFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3235290245723776216L;

  @Override
  public IModelSet createDirect() {
    return new ModelSet();
  }

}
