/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.infrastructureanalyzer.plugintype;

import java.util.List;

import org.jamesii.core.distributed.partitioner.infrastructureanalyzer.AbstractInfrasctructureAnalyzer;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Super class of all factories that provide infrastructure analyzers.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class InfrastructureAnalyzerFactory extends
    Factory<AbstractInfrasctructureAnalyzer> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 7138454273501821196L;

  /**
   * Returns the class of which the edge labels will be instances.
   * 
   * @return the class of which the edge labels will be instances
   */
  public abstract Class<?> getGeneratedEdgeLabel();

  /**
   * Returns the class of which the vertex labels will be instances.
   * 
   * @return the class of which the vertex labels will be instances
   */
  public abstract Class<?> getGeneratedVertexLabel();

  /**
   * Creates an adequate infrastructure analysis algorithm.
   * 
   * @param resources
   *          the available resources
   * @return instance of infrastructure analysis algorithm
   */
  public abstract AbstractInfrasctructureAnalyzer create(
      List<ISimulationServer> resources);

  @Override
  public AbstractInfrasctructureAnalyzer create(ParameterBlock parameters) {
    return create((List<ISimulationServer>) getParameter("RESOURCES",
        parameters));
  }

}
