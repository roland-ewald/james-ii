/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.plugintype;

import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;


/**
 * Represents performance data in matrix-form that can be used for portfolio
 * construction. Also includes important meta-information, e.g. which row
 * corresponds to which algorithm.
 * 
 * @see IPortfolioSelector
 * @see PortfolioProblemDescription
 * 
 * @author Roland Ewald
 */
public class PortfolioPerformanceData {
  static {
    SerialisationUtils.addDelegateForConstructor(
        PortfolioPerformanceData.class,
        new IConstructorParameterProvider<PortfolioPerformanceData>() {
          @Override
          public Object[] getParameters(PortfolioPerformanceData perfData) {
            Object[] params = new Object[] { perfData.performances,
                perfData.configurations, perfData.problems };
            return params;
          }
        });
  }

  /** Matrix of average performances (algorithm x problem). */
  public final Double[][] performances;

  /** The array of configurations (correspond to rows in performance matrix). */
  public final SelectionTree[] configurations;

  /** The array of problems (correspond to columns in performance matrix). */
  public final IProblemDefinition[] problems;

  /**
   * Instantiates a new portfolio performance data.
   * 
   * @param perfMatrix
   *          the performance matrix
   */
  public PortfolioPerformanceData(Double[][] perfMatrix) {
    this(perfMatrix, null, null);
  }

  /**
   * Instantiates a new portfolio performance data.
   * 
   * @param perfMatrix
   *          the performance matrix
   * @param configs
   *          the runtime configurations
   * @param simProblems
   *          the simulation problems
   */
  public PortfolioPerformanceData(Double[][] perfMatrix,
      SelectionTree[] configs, IProblemDefinition[] simProblems) {
    performances = perfMatrix;
    configurations = configs;
    problems = simProblems;
  }

  /**
   * Get the number of problems for which performance data is available.
   * 
   * @return the number of problems
   */
  public int getNumberOfProblems() {
    return performances[0].length;
  }

}
