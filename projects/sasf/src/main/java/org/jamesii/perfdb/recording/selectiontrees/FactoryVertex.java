/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;


import java.util.HashSet;
import java.util.Set;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.plugins.IParameter;

/**
 * Vertex subclass for factories.
 * 
 * @author Roland Ewald
 * 
 * @param <F>
 *          type of the factories
 */
public class FactoryVertex<F extends Factory<?>> extends SelTreeSetVertex {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1782308775978391751L;

  /** Set of factories. */
  private final Set<F> factories = new HashSet<>();

  /** Reference to parameter for which the set of factories is defined. */
  private IParameter parameter;

  /** The constraints associated with this vertex. */
  private FactoryVertexConstraints constraints = new FactoryVertexConstraints();

  /**
   * Constructor for bean compliance. Do not use manually.
   */
  public FactoryVertex() {
  }

  /**
   * Default constructor.
   * 
   * @param id
   *          the ID of this vertex
   * @param facs
   *          the set of factories
   * @param param
   *          the parameter for which the set of factories is defined
   */
  public FactoryVertex(int id, Set<F> facs, IParameter param) {
    super(id);
    factories.addAll(facs);
    parameter = param;
  }

  public Set<F> getFactories() {
    return new HashSet<>(factories);
  }

  public Set<F> getAvailableFactories() {
    Set<F> avFactories = new HashSet<>(factories);
    avFactories.removeAll(constraints.getIgnoreList());
    return avFactories;
  }

  public int getFactoryCount() {
    return factories.size();
  }

  public int getForbiddenFactoryCount() {
    return constraints.getIngoreListSize();
  }

  public int getAvailableFactoryCount() {
    return getFactoryCount() - getForbiddenFactoryCount();
  }

  @Override
  public String toString() {
    StringBuilder resultBuilder = new StringBuilder("");
    for (F factory : factories) {
      resultBuilder.append(factory.getName().substring(
          factory.getName().lastIndexOf('.') + 1, factory.getName().length()));
      resultBuilder.append(',');
    }
    String result = resultBuilder.toString();
    if (result.length() > 0) {
      result = result.substring(0, result.length() - 1);
    }
    return result;
  }

  public IParameter getParameter() {
    return parameter;
  }

  public FactoryVertexConstraints getConstraints() {
    return constraints;
  }

  public void setFactories(Set<F> factories) {
    this.factories.clear();
    this.factories.addAll(factories);
  }

  public void setParameter(IParameter parameter) {
    this.parameter = parameter;
  }

  public void setConstraints(FactoryVertexConstraints constraints) {
    this.constraints = constraints;
  }
}