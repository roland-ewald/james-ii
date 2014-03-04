/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;


import java.util.Set;

import org.jamesii.core.plugins.IParameter;

/**
 * Vertex subclass for parameters.
 * 
 * @author Roland Ewald
 * 
 */
public class ParameterVertex extends SelTreeSetVertex {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1717597970944113645L;

  /** Set of parameters. */
  private Set<IParameter> parameters;

  /** Constraints for this vertex. */
  private ParameterVertexConstraints constraints =
      new ParameterVertexConstraints();

  /**
   * Constructor for bean compliance. Do not use manually.
   */
  public ParameterVertex() {
  }

  /**
   * Default constructor.
   * 
   * @param id
   *          Id of this vertex
   * @param params
   *          the set of parameters
   */
  public ParameterVertex(int id, Set<IParameter> params) {
    super(id);
    parameters = params;
  }

  @Override
  public String toString() {
    StringBuilder resultBuilder = new StringBuilder();
    for (IParameter parameter : parameters) {
      resultBuilder.append(parameter.getName());
      resultBuilder.append(',');
    }
    String result = resultBuilder.toString();
    if (result.length() > 0) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  public int getParameterCount() {
    return parameters.size();
  }

  public Set<IParameter> getParameters() {
    return parameters;
  }

  public ParameterVertexConstraints getConstraints() {
    return constraints;
  }

  public void setParameters(Set<IParameter> parameters) {
    this.parameters = parameters;
  }

  public void setConstraints(ParameterVertexConstraints constraints) {
    this.constraints = constraints;
  }
}