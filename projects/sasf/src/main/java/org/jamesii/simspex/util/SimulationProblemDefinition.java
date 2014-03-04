/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulationrun.stoppolicy.CompositeCompTaskStopPolicyFactory;
import org.jamesii.core.simulationrun.stoppolicy.ConjunctiveSimRunStopPolicyFactory;
import org.jamesii.core.simulationrun.stoppolicy.DisjunctiveSimRunStopPolicyFactory;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.entities.IProblemDefinition;

/**
 * Helper class to handle the {@link IProblemDefinition} instances that refer to
 * simulation problems.
 * 
 * @author Roland Ewald
 * 
 */
public final class SimulationProblemDefinition {

  /**
   * Should not be instantiated.
   */
  private SimulationProblemDefinition() {
  }

  /** Common part of exception message. */
  private static final String WARNING_PROBLEM_DEF =
      "The problem definition with ID ";

  /** The parameter name for the stop time. */
  private static final String STOP_TIME = "StopTime";

  /** The parameter name for the policy class. */
  private static final String STOP_POLICY_CLASS = "StopPolicyFactory";

  /** The parameter name for the policy parameters. */
  private static final String STOP_POLICY_PARAMETERS = "StopPolicyParameters";

  /**
   * Gets the definition parameters to define an {@link IProblemDefinition} for
   * simulation.
   * 
   * @param stopPolicyFactory
   *          the stop policy factory
   * @param stopPolicyParameters
   *          the stop policy parameters
   * @return the definition parameters
   */
  public static Map<String, Serializable> getDefinitionParameters(
      Class<? extends ComputationTaskStopPolicyFactory> stopPolicyFactory,
      ParameterBlock stopPolicyParameters) {
    Map<String, Serializable> result = new HashMap<>();
    result.put(STOP_POLICY_CLASS, stopPolicyFactory.getCanonicalName());
    result.put(STOP_TIME,
        extractSimStopTime(stopPolicyParameters, stopPolicyFactory));
    if (stopPolicyParameters != null) {
      result.put(STOP_POLICY_PARAMETERS, stopPolicyParameters);
    }
    return result;
  }

  /**
   * Gets the simulation stop time. Note that this has been previously extracted
   * by
   * {@link SimulationProblemDefinition#extractSimStopTime(ParameterBlock, Class)}
   * , so all its caveats apply here as well.
   * 
   * @param problemDefinition
   *          the problem definition
   * @return the simulation stop time
   */
  public static Double getSimStopTime(IProblemDefinition problemDefinition) {
    Object simStopTime =
        problemDefinition.getDefinitionParameters().get(STOP_TIME);
    if (!(simStopTime instanceof Double)) {
      throw new IllegalArgumentException(WARNING_PROBLEM_DEF
          + problemDefinition.getID()
          + " is not a valid simulation problem, as simulation stop time is"
          + (simStopTime == null ? "null" : simStopTime.toString()));
    }
    return (Double) simStopTime;
  }

  /**
   * Gets the factory class for the stop policy that was used.
   * 
   * @param problemDefinition
   *          the problem definition
   * @return the stop policy factory class
   */
  @SuppressWarnings("unchecked")
  // Class type is ensured by insertion method
  // (SimulationProblemDefinition#getDefinitionParameters)
  public static Class<? extends ComputationTaskStopPolicyFactory> getStopFactoryClass(
      IProblemDefinition problemDefinition) {

    Object stopFactoryClass =
        problemDefinition.getDefinitionParameters().get(STOP_POLICY_CLASS);
    if (!(stopFactoryClass instanceof String)) {
      throw new IllegalArgumentException(
          WARNING_PROBLEM_DEF
              + problemDefinition.getID()
              + " is not a valid simulation problem, as the stop factory class is '"
              + stopFactoryClass.toString() + "'");
    }

    Class<?> factoryClass = null;
    try {
      factoryClass = Class.forName((String) stopFactoryClass);
    } catch (ClassNotFoundException e) {
      SimSystem.report(Level.SEVERE, "Could not load stop policy factory.", e);
    }
    if (factoryClass == null
        || !ComputationTaskStopPolicyFactory.class
            .isAssignableFrom(factoryClass)) {
      throw new IllegalArgumentException(WARNING_PROBLEM_DEF
          + problemDefinition.getID()
          + " has a stop policy factory class that is '" + factoryClass
          + "', not a subclass of "
          + ComputationTaskStopPolicyFactory.class.getCanonicalName() + ".");
    }

    return (Class<? extends ComputationTaskStopPolicyFactory>) factoryClass;
  }

  /**
   * Gets the stop factory parameters.
   * 
   * @param problemDefinition
   *          the problem definition
   * @return the stop factory parameters
   */
  public static ParameterBlock getStopFactoryParameters(
      IProblemDefinition problemDefinition) {
    Object stopFactoryParameters =
        problemDefinition.getDefinitionParameters().get(STOP_POLICY_PARAMETERS);
    if (!(stopFactoryParameters instanceof ParameterBlock)) {
      throw new IllegalArgumentException(
          WARNING_PROBLEM_DEF
              + problemDefinition.getID()
              + " is not a valid simulation problem, as the stop factory parameters are "
              + (stopFactoryParameters == null ? "null" : stopFactoryParameters
                  .toString()));
    }
    return (ParameterBlock) stopFactoryParameters;
  }

  /**
   * Extracts simulation stop time. Take care: this will ignore additional
   * stopping configurations (e.g. the WCT stop time restriction by the
   * calibrator), yet it will be able to cope with them if they are added.
   * 
   * @param stParams
   *          the stop policy parameters
   * @param stFactoryClass
   *          the class of the stop policy factory
   * 
   * @return the stop time (simulation time), or null in case the end time could
   *         not be extracted
   */
  public static Double extractSimStopTime(ParameterBlock stParams,
      Class<? extends ComputationTaskStopPolicyFactory> stFactoryClass) {
    if (SimTimeStopFactory.class.isAssignableFrom(stFactoryClass)) {
      if (stParams == null) {
        return Double.POSITIVE_INFINITY;
      } else if (stParams.hasSubBlock(SimTimeStopFactory.SIMEND)) {
        return (Double) stParams.getSubBlockValue(SimTimeStopFactory.SIMEND);
      }
    }

    if ((DisjunctiveSimRunStopPolicyFactory.class
        .isAssignableFrom(stFactoryClass) || ConjunctiveSimRunStopPolicyFactory.class
        .isAssignableFrom(stFactoryClass))
        && stParams
            .hasSubBlock(CompositeCompTaskStopPolicyFactory.POLICY_FACTORY_LIST)) {
      List<Pair<ComputationTaskStopPolicyFactory, ParameterBlock>> subPolicies =
          stParams
              .getSubBlockValue(CompositeCompTaskStopPolicyFactory.POLICY_FACTORY_LIST);
      for (Pair<ComputationTaskStopPolicyFactory, ParameterBlock> subPolicy : subPolicies) {
        if (subPolicy.getFirstValue() instanceof SimTimeStopFactory
            && subPolicy.getSecondValue()
                .hasSubBlock(SimTimeStopFactory.SIMEND)) {
          return (Double) subPolicy.getSecondValue().getSubBlockValue(
              SimTimeStopFactory.SIMEND);
        }
      }
    }
    return null;
  }
}
