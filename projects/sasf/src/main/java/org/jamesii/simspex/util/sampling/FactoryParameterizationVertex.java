/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util.sampling;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.collection.list.SortedList;
import org.jamesii.core.util.misc.Pair;

/**
 * The vertex of a factory for which parameters shall be sampled. The vertex
 * maps the declared parameters to their corresponding parameter blocks,
 * somewhere in the overall hierarchy of {@link ParameterBlock} entities.
 * 
 * @author Roland Ewald
 */
public class FactoryParameterizationVertex {

  /**
   * Mapping from the parameter name to a pair of meta-data (regarding possible
   * parameter values, such as lower/upper bound) and the target blocks that
   * configures the parameter.
   */
  private final Map<String, Pair<BaseVariable<?>, ParameterBlock>> parameters =
      new HashMap<>();

  /** Defines the order of the parameters for this vertex. */
  private final SortedList<String> parameterNames = new SortedList<>();

  /** The class of the factory to be parameterized. */
  private final Class<? extends Factory<?>> factoryClass;

  /**
   * Instantiates a new factory parameterization vertex.
   */
  FactoryParameterizationVertex() {
    this(null);
  }

  /**
   * Instantiates a new factory parameterization vertex.
   * 
   * @param factoryClass
   *          the class of the factory to be parameterized
   */
  public FactoryParameterizationVertex(Class<? extends Factory<?>> factoryClass) {
    this.factoryClass = factoryClass;
  }

  /**
   * Adds a parameter.
   * 
   * @param variable
   *          the variable containing the parameter's meta data (suggested
   *          step-width etc.)
   * @param targetBlock
   *          the target block whose value shall be changed
   */
  public void addParameter(BaseVariable<?> variable, ParameterBlock targetBlock) {
    if (parameters.containsKey(variable.getName())) {
      throw new IllegalArgumentException(
          "This variable is already associated with parameter block '"
              + parameters.get(variable.getName()) + "'");
    }
    parameters.put(variable.getName(),
        new Pair<BaseVariable<?>, ParameterBlock>(variable, targetBlock));
    parameterNames.add(variable.getName());
  }

  public Class<? extends Factory<?>> getFactoryClass() {
    return factoryClass;
  }

  public Map<String, Pair<BaseVariable<?>, ParameterBlock>> getParameters() {
    return Collections.unmodifiableMap(parameters);
  }

  public int getParameterCount() {
    return parameterNames.size();
  }

  /**
   * Gets the parameter with the specified index.
   * 
   * @param index
   *          the parameter index
   * @return the parameter
   */
  public Pair<BaseVariable<?>, ParameterBlock> getParameter(int index) {
    if (index < 0 || index >= getParameterCount()) {
      throw new IllegalArgumentException("Wrong parameter index '" + index
          + "', size of parameter list is " + parameterNames.size());
    }
    return new Pair<>(
        parameters.get(parameterNames.get(index)));
  }
}