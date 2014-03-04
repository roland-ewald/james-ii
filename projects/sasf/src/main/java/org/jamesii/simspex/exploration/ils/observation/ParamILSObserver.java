/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils.observation;

import static org.jamesii.perfdb.util.ParameterBlocks.toUniqueString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Triple;
import org.jamesii.simspex.exploration.ils.algorithm.ParamILS;

/**
 * Tracks interesting performance data of {@link ParamILS} implementations.
 * 
 * @author Robert Engelke
 */
public class ParamILSObserver implements IObserver<ParamILS> {

  /**
   * The performance of interesting configurations such as the performance
   * development in the iterative improvement and restart configurations. List
   * of hill-climbing invocations[firstImprovement] (list of performance
   * development during one hill-climbing: triple (configuration, performance of
   * new theta, new performance of old theta)).
   */
  private final List<List<Triple<String, Double, Double>>> interestingPerformance =
      new ArrayList<>();
  {
    interestingPerformance.add(new ArrayList<Triple<String, Double, Double>>());
  }

  /**
   * The performance of the minimum configurations. List of events where better
   * resulted in a new 'incumbent' (best) theta, each pair gives the new best
   * theta and a tuple (configuration, performance of new theta, new performance
   * of old theta).
   */
  private final List<Triple<String, Double, Double>> minList =
      new ArrayList<>();

  /** The matching theta to their indices. */
  private final Map<String, Pair<ParameterBlock, Integer>> thetas =
      new HashMap<>();

  /**
   * The called theta indices. It contains the order in which the ParamILS
   * algorithm was going through the thetas.
   */
  private final List<Integer> calledThetas = new ArrayList<>();

  /** The actual theta max. */
  private int thetaMax = 0;

  /** The minimums updated. */
  private final List<Integer> minUpdates = new ArrayList<>();

  /** The log level to be used. */
  private final Level logLevel = Level.INFO;

  @Override
  public void update(ParamILS entity) {
    update(entity, null);
  }

  @Override
  public void update(ParamILS entity, Object hint) {
    if (!(hint instanceof ParamILSMessage)) {
      SimSystem.report(Level.WARNING, "Unsupported hint:" + hint);
    } else {
      processMessage((ParamILSMessage) hint);
    }
  }

  /**
   * Process a message depending on its type.
   * 
   * @param message
   *          the message
   */
  private synchronized void processMessage(ParamILSMessage message) {
    int theta = getOrAddTheta(message);
    switch (message.getMessageType()) {
    case MinimumChanged:
      addMinimum(message, theta);
      break;
    case MinimumMaintained:
      updateMinimum(message, theta);
      break;
    case IterativeImprovement:
      addHillClimbing(message, theta);
      break;
    case Restart:
      SimSystem.report(logLevel, "Restarted.");
      break;
    case ParameterAdjusting:
      SimSystem.report(logLevel, "New ParamILS round is started.");
      interestingPerformance
          .add(new ArrayList<Triple<String, Double, Double>>());
      break;
    case PerformanceEstimation:
      SimSystem.report(logLevel, "Performance estimation for new config #"
          + theta);
      calledThetas.add(theta);
      break;
    default:
      SimSystem.report(
          Level.WARNING,
          "Message type not supported yet: " + message.getMessageType() + "\n"
              + message.getPerformanceEstimation() + "\n"
              + message.getReferencedBlock());
      break;
    }
  }

  /**
   * Gets the theta for the ParameterBlock in the given message.
   * 
   * @param message
   *          the message
   * @return the theta's parameter block and index
   */
  public Pair<ParameterBlock, Integer> getTheta(String paramString) {
    return thetas.get(paramString);
  }

  /**
   * Adds a minimum to the observed data.
   * 
   * @param message
   *          the message
   * @param theta
   *          the configuration index
   */
  private void addMinimum(ParamILSMessage message, int theta) {
    minList.add(new Triple<>(toUniqueString(message
        .getReferencedBlock()), message.getPerformanceEstimation(), message
        .getComparedValue()));
    SimSystem.report(logLevel, "Minimum changed to config # " + theta
        + ", estimated performance:" + message.getPerformanceEstimation()
        + "\nConfig details:" + toUniqueString(message.getReferencedBlock()));
  }

  /**
   * Adds a value to the observed hill climbing data.
   * 
   * @param message
   *          the message
   */
  private void addHillClimbing(ParamILSMessage message, int theta) {
    interestingPerformance.get(interestingPerformance.size() - 1).add(
        new Triple<>(toUniqueString(message
            .getReferencedBlock()), message.getPerformanceEstimation(), message
            .getComparedValue()));
    SimSystem.report(logLevel, "New hill-climbing step for config #" + theta
        + ", estimated performance:" + message.getPerformanceEstimation());
  }

  /**
   * Updates the consistency of the minimum given by the message.
   * 
   * @param message
   *          the message
   * @param theta
   *          the configuration index
   */
  private void updateMinimum(ParamILSMessage message, int theta) {
    boolean updated = false;
    for (int i = minList.size() - 1; i >= 0 && !updated; i--) {
      if (minList.get(i).getA()
          .equals(toUniqueString(message.getReferencedBlock()))) {
        minList.get(i).setB(message.getPerformanceEstimation());
        updated = true;
      }
    }
    if (!updated) {
      addMinimum(message, theta);
    }
    minUpdates.add(theta);
    SimSystem.report(logLevel, "Updated/added minimum for config #" + theta
        + ", estimated performance:" + message.getPerformanceEstimation());
  }

  /**
   * Adds the theta (configuration index) for the ParameterBlock in the message
   * if it does not exist already, otherwise returns it.
   * 
   * @param message
   *          the message
   * @return the theta
   */
  private Integer getOrAddTheta(ParamILSMessage message) {
    if (message.getReferencedBlock() != null
        && !thetas.containsKey(toUniqueString(message.getReferencedBlock()))) {
      addTheta(message.getReferencedBlock());
    }
    return thetas.get(toUniqueString(message.getReferencedBlock()))
        .getSecondValue();
  }

  /**
   * Adds parameter block to database. This method is called when information
   * about an ParamILS which was previously requested using an asynchronous
   * interface becomes available.
   * 
   * @param bl
   *          the ParameterBlock
   */
  private void addTheta(ParameterBlock bl) {
    thetas.put(toUniqueString(bl), new Pair<>(bl,
        thetaMax++));
    SimSystem.report(logLevel, "New config # "
        + thetas.get(toUniqueString(bl)).getSecondValue() + ":"
        + toUniqueString(bl));
  }

  /**
   * Gets the performance of interesting configurations such as the performance
   * development in the iterative improvement and restart configurations. It is
   * a list of hill-climbing invocations[firstImprovement] (list of performance
   * development during one hill-climbing: triple (configuration, performance of
   * new theta, new performance of old theta)).
   * 
   * @return the performance of interest
   */
  public List<List<Triple<String, Double, Double>>> getInterestingPerformance() {
    return interestingPerformance;
  }

  /**
   * Gets the performance of the minimum configurations. List of events where
   * better resulted in a new 'incumbent' (best) theta, each entry states the
   * new best configuration as a tuple (configuration, performance of new theta,
   * new performance of old theta).
   * 
   * @return the list of the minimums
   */
  public synchronized List<Triple<String, Double, Double>> getMinList() {
    return new ArrayList<>(minList);
  }

  /**
   * Gets the mapping of all ParameterBlock's to their theta.
   * 
   * @return the (parameter block , index) tuples for all thetas
   */
  public Map<String, Pair<ParameterBlock, Integer>> getThetas() {
    return thetas;
  }

  /**
   * Gets a list of the order the performance of the theta's was needed.
   * 
   * @return the called thetas
   */
  public List<Integer> getCalledThetas() {
    return new ArrayList<>(calledThetas);
  }

  /**
   * Gets the list of all minimas which were updated during the process.
   * 
   * @return the minimums which are updated during process
   */
  public List<Integer> getMinUpdates() {
    return minUpdates;
  }

  /**
   * Gets the overall best configuration and its cost.
   * 
   * @return the overall best configuration
   */
  public Pair<String, Double> getOverallBestConfiguration() {
    if (minList.isEmpty()) {
      throw new IllegalStateException("No best configuration recorded");
    }
    Triple<String, Double, Double> lastImprovement =
        minList.get(minList.size() - 1);
    return new Pair<>(lastImprovement.getA(),
        lastImprovement.getB());
  }

  /**
   * Get the log level for notifications on ILS behavior.
   * 
   * @return the log level
   */
  public Level getLogLevel() {
    return logLevel;
  }

}
