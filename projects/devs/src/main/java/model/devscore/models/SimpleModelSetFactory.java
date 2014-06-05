/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.models;

import model.devscore.models.plugintype.BaseModelSetFactory;

/**
 * A factory for creating SimpleModelSet objects.
 * 
 * @author Jan Himmelspach
 */
public class SimpleModelSetFactory extends BaseModelSetFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7020181071377096594L;

  @Override
  public IModelSet createDirect() {
    return new SimpleModelSet();
  }

}
