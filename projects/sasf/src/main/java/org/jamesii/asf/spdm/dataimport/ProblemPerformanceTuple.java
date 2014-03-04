/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.dataimport;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;

/**
 * A performance tuple that also contains information on the problem for which
 * the performance and the features are recorded.
 * 
 * @author Roland Ewald
 */
public class ProblemPerformanceTuple extends PerformanceTuple {
  static {
    SerialisationUtils.addDelegateForConstructor(ProblemPerformanceTuple.class,
        new IConstructorParameterProvider<ProblemPerformanceTuple>() {
          @Override
          public Object[] getParameters(ProblemPerformanceTuple perfTuple) {
            Object[] params =
                new Object[] { perfTuple.getProblemSchemeURI(),
                    perfTuple.getProblemSchemeParameters(),
                    Long.valueOf(perfTuple.getProblemDefinitionID()),
                    perfTuple.getProblemDefinitionParameters(),
                    perfTuple.getFeatures(), perfTuple.getConfiguration(),
                    perfTuple.getPerfMeasureFactory(),
                    Double.valueOf(perfTuple.getPerformance()) };
            return params;
          }
        });
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8763735539182147253L;

  /** The problem URI. */
  private final URI problemSchemeURI;

  /** The problem scheme parameters. */
  private final Map<String, Serializable> problemSchemeParameters;

  /** The problem definition ID (for database look-up). */
  private final long problemDefinitionID;

  /** The problem definition parameters. */
  private final Map<String, Serializable> problemDefinitionParameters;

  /**
   * Instantiates a new problem performance tuple.
   * 
   * @param problemDefinition
   *          the problem definition
   * @param features
   *          the features
   * @param configuration
   *          the configuration
   * @param perfMeasureFactory
   *          the performance measure factory
   * @param performance
   *          the performance
   */
  public ProblemPerformanceTuple(IProblemDefinition problemDefinition,
      Features features, Configuration configuration,
      Class<? extends PerformanceMeasurerFactory> perfMeasureFactory,
      Double performance) {
    this(problemDefinition.getProblemScheme().getUri(), problemDefinition
        .getSchemeParameters(), problemDefinition.getID(), problemDefinition
        .getDefinitionParameters(), features, configuration,
        perfMeasureFactory, performance);
  }

  /**
   * Instantiates a new problem performance tuple.
   * 
   * @param problemSchemeURI
   *          the problem scheme URI
   * @param probSchemeParameters
   *          the problem scheme parameters
   * @param problemDefinitionID
   *          the problem definition id
   * @param probDefParameters
   *          the problem definition parameters
   * @param features
   *          the features
   * @param configuration
   *          the configuration
   * @param perfMeasureFactory
   *          the performance measure factory
   * @param performance
   *          the performance
   */
  public ProblemPerformanceTuple(URI problemSchemeURI,
      Map<String, Serializable> probSchemeParameters, Long problemDefinitionID,
      Map<String, Serializable> probDefParameters, Features features,
      Configuration configuration,
      Class<? extends PerformanceMeasurerFactory> perfMeasureFactory,
      Double performance) {
    super(features, configuration, perfMeasureFactory, performance);
    this.problemSchemeURI = problemSchemeURI;
    this.problemDefinitionID = problemDefinitionID;
    this.problemSchemeParameters =
        new HashMap<>(probSchemeParameters);
    this.problemDefinitionParameters =
        new HashMap<>(probDefParameters);
  }

  public URI getProblemSchemeURI() {
    return problemSchemeURI;
  }

  public Map<String, Serializable> getProblemSchemeParameters() {
    return problemSchemeParameters;
  }

  public long getProblemDefinitionID() {
    return problemDefinitionID;
  }

  public Map<String, Serializable> getProblemDefinitionParameters() {
    return problemDefinitionParameters;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ProblemPerformanceTuple) || super.equals(obj)) {
      return false;
    }
    ProblemPerformanceTuple compare = (ProblemPerformanceTuple) obj;
    if (problemDefinitionID != compare.problemDefinitionID
        || !problemSchemeURI.equals(compare.problemSchemeURI)
        || !problemSchemeParameters.equals(compare.problemSchemeParameters)
        || !problemDefinitionParameters
            .equals(compare.problemDefinitionParameters)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return super.hashCode() ^ problemSchemeURI.hashCode();
  }
}
