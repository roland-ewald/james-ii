/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.plugins.IParameter;

/**
 * Represents constraints for a {@link ParameterVertex}.
 * 
 * @author Roland Ewald
 * 
 */
public class ParameterVertexConstraints implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = -5593461045819316755L;

  /** Parameter bounds. */
  private Map<IParameter, IParameterBounds<?>> paramBunds = new HashMap<>();

  public Map<IParameter, IParameterBounds<?>> getParamBunds() {
    return paramBunds;
  }

  public void setParamBunds(Map<IParameter, IParameterBounds<?>> paramBunds) {
    this.paramBunds = paramBunds;
  }

}
