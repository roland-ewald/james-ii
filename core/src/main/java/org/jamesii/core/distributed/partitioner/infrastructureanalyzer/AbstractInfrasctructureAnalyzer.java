/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.infrastructureanalyzer;

import java.util.List;

import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * org.jamesii.core.simulation.partitionizer.infrasctructureanalyzer.
 * AbstractInfrasctructureAnalyzer
 * 
 * Base class for all infrastructure analysis algorithms
 * 
 * Created on Nov 16, 2004
 * 
 * @author Roland Ewald
 * 
 */
public abstract class AbstractInfrasctructureAnalyzer {

  /**
   * Analyses infrastructure
   * 
   * @param resources
   * @return graph representation of the infrastructure
   */
  public abstract ISimpleGraph analyzeInfrastrcutre(
      List<ISimulationServer> resources);

}
