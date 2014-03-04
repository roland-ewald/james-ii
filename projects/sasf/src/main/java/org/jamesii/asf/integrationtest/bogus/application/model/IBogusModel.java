/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.model;


import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;

/**
 * Represents a bogus model.
 * 
 * @author Roland Ewald
 */
public interface IBogusModel extends ISymbolicModel<IBogusModel>, IModel {

  /**
   * Gets the content of the bogus model.
   * 
   * @return the content
   */
  Map<String, Serializable> getContent();

}
