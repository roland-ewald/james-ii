/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.failuredetection;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.core.factories.Factory;
import org.jamesii.perfdb.recording.selectiontrees.SelectedFactoryNode;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * This class implements a simple Failure Detection System (FDS). It remembers
 * which components also failed when a given component was involved in a setup
 * that crashed. If there are no such components left, Occam's razor is applied
 * and the given component is reported to be broken (a threshold defines the
 * minimal amount of trials before the evidence leads to a verdict).
 * 
 * <p/>
 * This FDS may work in two modes: in strict mode there is no way for a
 * component to re-gain its trustworthiness. In non-strict mode, a successful
 * execution of a set-up involving the given component will result in an erasure
 * of all evidence gathered in the past.
 * 
 * <p/>
 * The algorithm works by assigning lists of equally suspect factories to any
 * factory that was involved in a failure. If a new failure arrives, it checks
 * for each suspicious factory (from the current setup) how many other
 * suspicious components there are. Those 'codefendants' that are not suspicious
 * anymore (because not choosing them did not prevent the failure) will be
 * removed for the list of equally suspicious factories. If the list is empty
 * for a given factory (i.e. there are no other components that are equally
 * likely to contain a bug, by using Occam's razor) and a certain number of
 * failures has been recorded, this factory will then be regarded as broken. All
 * components that have the identified factory in their list of codefendants
 * will be rehabilitated (by removing them eniterly from the failure storage).
 * 
 * @author Roland Ewald
 */
public class FailureDetector {

  /**
   * The failure lists. For each factory, it stores which factories were also
   * involved in all past failures the given factory was involved in.
   */
  private Map<Class<? extends Factory<?>>, List<Class<? extends Factory<?>>>> suspectsLists =
      new HashMap<>();

  /** The execution failure counts per factory. */
  private Map<Class<? extends Factory<?>>, Integer> failureCounts =
      new HashMap<>();

  /** The map storing failure descriptions (for bug reporting). */
  private Map<Class<? extends Factory<?>>, List<FailureDescription>> failureDescs =
      new HashMap<>();

  /** Flag to activate strict mode (no trustworthiness can be re-gained). */
  private boolean strictMode = false;

  /**
   * Minimal number of failures before it is acted upon the given evidence.
   */
  private int failThreshold = 1;

  /**
   * Analyses the failure that occurred.
   * 
   * @param failDesc
   *          the failure description
   * 
   * @return the list< class<? extends factory>>
   */
  public List<FailureReport> failureOccurred(FailureDescription failDesc) {
    Class<? extends Factory<?>> brokenFactory = null;

    Set<Class<? extends Factory<?>>> involvedFactories =
        failDesc.getDefectiveFactories();

    // Any factory might be guilty...
    for (Class<? extends Factory<?>> involvedFactory : involvedFactories) {

      // This factory has not failed before: add all currently involved
      // factories and continue
      if (!suspectsLists.containsKey(involvedFactory)) {
        addSuspect(involvedFactory, involvedFactories);
      }

      // Remember failure description
      failureDescs.get(involvedFactory).add(failDesc);

      // Get all factories that might also cause the problem in the previous
      // failures, and check if they are also involved
      List<Class<? extends Factory<?>>> codefendantFactories =
          getCodefendantFactories(involvedFactories, involvedFactory);

      failureCounts
          .put(involvedFactory, failureCounts.get(involvedFactory) + 1);

      // Check if the given component can be removed because of evidence
      if (failureCounts.get(involvedFactory) >= failThreshold
          && codefendantFactories.size() == 0) {
        // Mark identified factory as broken - no additional verdicts are
        // possible
        brokenFactory = involvedFactory;
        break;
      }
    }

    return tryRehabilitation(brokenFactory);
  }

  /**
   * Adds a suspicious factory.
   * 
   * @param suspiciousFactory
   *          the suspicious factory
   * @param alsoInvolvedFactories
   *          the factories that were also involved
   */
  private void addSuspect(Class<? extends Factory<?>> suspiciousFactory,
      Set<Class<? extends Factory<?>>> alsoInvolvedFactories) {
    List<Class<? extends Factory<?>>> coDefendants =
        new ArrayList<>(alsoInvolvedFactories);
    coDefendants.remove(suspiciousFactory);
    suspectsLists.put(suspiciousFactory, coDefendants);
    failureCounts.put(suspiciousFactory, 1);
    failureDescs.put(suspiciousFactory, new ArrayList<FailureDescription>());
  }

  /**
   * Gets the codefendant factories. These are all factories that might also
   * cause the problem in the previous failures (checks if they are also
   * involved this time).
   * 
   * @param involvedFactories
   *          the involved factories
   * @param involvedFactory
   *          the involved factory
   * @return the codefendant factories
   */
  private List<Class<? extends Factory<?>>> getCodefendantFactories(
      Set<Class<? extends Factory<?>>> involvedFactories,
      Class<? extends Factory<?>> involvedFactory) {
    List<Class<? extends Factory<?>>> codefendantFactories =
        suspectsLists.get(involvedFactory);
    List<Class<? extends Factory<?>>> rehabilitated =
        new ArrayList<>();
    for (Class<? extends Factory<?>> codefFac : codefendantFactories) {
      if (!involvedFactories.contains(codefFac)) {
        rehabilitated.add(codefFac);
      }
    }
    codefendantFactories.removeAll(rehabilitated);
    return codefendantFactories;
  }

  /**
   * If guilty factory has been found, rehabilitate all others.
   * 
   * @param brokenFactory
   *          the broken factory
   * @return the list
   */
  private List<FailureReport> tryRehabilitation(
      Class<? extends Factory<?>> brokenFactory) {
    List<FailureReport> reports = new ArrayList<>();
    if (brokenFactory != null) {
      reports.add(new FailureReport(brokenFactory, failureDescs
          .get(brokenFactory)));
      clean(brokenFactory);
    }
    return reports;
  }

  /**
   * Rehabilitate all codefendants of the 'guilty' factory.
   * 
   * @param brokenFactory
   *          the guilty (= broken) factory
   */
  protected void clean(Class<? extends Factory<?>> brokenFactory) {

    List<Class<? extends Factory<?>>> rehabilitated =
        new ArrayList<>();
    for (Entry<Class<? extends Factory<?>>, List<Class<? extends Factory<?>>>> failEntry : suspectsLists
        .entrySet()) {
      Class<? extends Factory<?>> suspect = failEntry.getKey();
      if (suspect.equals(brokenFactory)
          || failEntry.getValue().contains(brokenFactory)) {
        rehabilitated.add(suspect);
      }
    }

    // Rehabilitate selected suspects
    for (Class<? extends Factory<?>> suspect : rehabilitated) {
      suspectsLists.remove(suspect);
      failureCounts.put(suspect, 0);
      failureDescs.remove(suspect);
    }
  }

  /**
   * Success occurred.
   * 
   * @param selTree
   *          the selection tree defining the successful set-up
   */
  public void successOccurred(SelectionTree selTree) {

    if (strictMode) {
      return;
    }

    // Rehabilitate all factories involved in the execution
    for (SelectedFactoryNode node : selTree.getVertices()) {
      Class<? extends Factory<?>> factoryClass =
          node.getSelectionInformation().getFactoryClass();
      suspectsLists.remove(factoryClass);
      failureCounts.put(factoryClass, 0);
    }
  }

  /**
   * Gets the number of suspects.
   * 
   * @return the number of suspects
   */
  public int getNumOfSuspects() {
    return suspectsLists.size();
  }
}
