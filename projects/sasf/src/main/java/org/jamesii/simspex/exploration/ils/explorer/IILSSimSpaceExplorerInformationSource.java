/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.explorer;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.util.sampling.IConfigurationSampler;


/**
 * The Interface IILSSimSpaceExplorerInformationSource is used by
 * {@link org.jamesii.simspex.exploration.ils.algorithm.ParamILS} sub-classes to get the
 * necessary simulation data to further improve the selected parameters.
 * 
 * @author Robert Engelke
 * 
 */
public interface IILSSimSpaceExplorerInformationSource extends
    IConfigurationSampler {

  /**
   * Calculates the cost for the given configuration by going through the
   * Training instances. The index n is the smallest number of a run that has
   * not yet been executed for this configuration, i.e. it is expected that the
   * cost for run 0 ... n-1 have already been retrieved by invoking this method.
   * Can be called repeatedly (with different resource cappings).
   * 
   * @param configuration
   *          the configuration
   * @param runNumber
   *          the run number, whereby 0 <= runNumber <= numberOfRuns (adding a
   *          run might be necessary)
   * @param resourceCap
   *          the value that characterizes an amount of resources (e.g.
   *          computing time), and after exceeding this the algorithm run is
   *          capped (if set to null, then this limit will be ignored)
   * @return the cost of the configuration
   */
  double calculateCost(ParameterBlock configuration, int runNumber,
      Double resourceCap);

  /**
   * Gets the number of algorithm runs for the given configuration.
   * 
   * @param configuration
   *          the configuration
   * @return the number of runs
   */
  int getNumberOfRuns(ParameterBlock configuration);

  /**
   * Gets the costs of run n of the given configuration by searching in the
   * cache.
   * 
   * @param configuration
   *          the configuration
   * @param runNumber
   *          the run number, where 0 <= runNumber < numberOfRuns
   * @return the costs of the n-th run
   */
  double getCost(ParameterBlock configuration, int runNumber);

  /**
   * Gets the sum of costs for run 1 to n of the given configuration.
   * 
   * @param configuration
   *          the configuration
   * @param runNumber
   *          the run number, where 0 <= runNumber < numberOfRuns
   * @return the costs for the first n runs
   */
  double getSumOfCosts(ParameterBlock configuration, int runNumber);

  /**
   * Gets the last resource capping used for the specified run of the
   * configuration. The resource capping that characterizes an amount of
   * resources (e.g. computing time), and after exceeding this the algorithm run
   * is capped (if set to null, then this limit will be ignored).
   * 
   * @param configuration
   *          the configuration
   * @param runNumber
   *          the run number
   * @return the resource capping
   */
  double getResourceCap(ParameterBlock configuration, int runNumber);
}
