/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.simspex.exploration.ProblemInstanceSelectionMode;


/**
 * Component that allows to steer the problem instance selection process from
 * the user interface.
 * 
 * @author Roland Ewald
 * 
 */
public class ProblemInstanceGenerator {

  /** The current operation mode of the component. */
  private ProblemInstanceSelectionMode currentMode =
      ProblemInstanceSelectionMode.EXPLORATIVE;

  /** The mode as defined by the user. */
  private ProblemInstanceSelectionMode userDefinedMode = null;

  /** The RNG factory name. */
  private String rngFactoryName = SimSystem.getRNGGenerator().getRNGFactory()
      .getFactoryInstance().getClass().getName();

  /**
   * Stored single constant seed when the corresponding.
   * {@link ProblemInstanceSelectionMode} is on
   */
  private long singleSeed = 0;

  /** Counter for instance creation. */
  private int instanceCounter = 0;

  /** Counter for repeated initialization. */
  private int reInitCounter = 0;

  /**
   * List of available problem instance (seed,rng) tuples(for round robin mode).
   */
  private List<Pair<Long, String>> instanceSeeds =
      new ArrayList<>();

  /** Default user choice. */
  private UserChoice choice = null;

  /**
   * If necessary, the user gets prompted to (re-)configure the instance
   * selection mode.
   * 
   * @param problemDefinition
   *          problem definition at hand
   * @return random seed, i.e. what constitutes a problem instance of the given
   *         problem definition
   */
  public Pair<Long, String> requestNewInstance(
      IProblemDefinition problemDefinition) {
    instanceCounter++;

    if (choice == null
        || (reInitCounter > 0 && instanceCounter > reInitCounter)) {
      choice = new UserChoice();

      init(problemDefinition);
    }
    switch (currentMode) {

    case CONSTANT:
      return new Pair<>(singleSeed, rngFactoryName);

    case ROUND_ROBIN:
      return instanceSeeds.get(instanceCounter % instanceSeeds.size());

    case EXPLORATIVE:
      if (instanceSeeds.size() < choice.getDesiredInstances()) {
        Pair<Long, String> result =
            new Pair<>(System.currentTimeMillis(), rngFactoryName);
        instanceSeeds.add(result);
        return result;
      }
      currentMode = ProblemInstanceSelectionMode.ROUND_ROBIN;
      return instanceSeeds.get(instanceCounter % instanceSeeds.size());

    default:
      return new Pair<>(-1L, null);
    }

  }

  /**
   * Resets the instance generator.
   */
  void reset() {
    restoreUserDefinedMode();
    instanceSeeds.clear();
  }

  /**
   * Restore user defined mode.
   */
  private void restoreUserDefinedMode() {
    if (userDefinedMode != null) {
      currentMode = userDefinedMode;
    }
  }

  /**
   * Initializes and parameterizes the problem instance generation.
   * 
   * @param problemDefinition
   *          problem definition that shall be instanced
   */
  protected void init(IProblemDefinition problemDefinition) {

    // Get problem instances
    IPerformanceDatabase perfDB = SimSpExPerspective.getPerformanceDataBase();
    List<IProblemInstance> instances = null;
    try {
      instances = perfDB.getAllProblemInstances(problemDefinition);
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, null,
          "PerfDB Lookup failed:" + ex.getMessage(), null, ex);
    }

    restoreUserDefinedMode();

    // TODO ask user

    // Generate data structures
    for (IProblemInstance pInst : instances) {
      instanceSeeds.add(new Pair<>(pInst.getRandomSeed(), pInst
          .getRNGFactoryName()));
    }

  }

  /**
   * @return the currentMode
   */
  public ProblemInstanceSelectionMode getCurrentMode() {
    return currentMode;
  }

  /**
   * @param currentMode
   *          the currentMode to set
   */
  public void setCurrentMode(ProblemInstanceSelectionMode currentMode) {
    userDefinedMode = currentMode;
    restoreUserDefinedMode();
  }

}

/**
 * Class to representing the user's choice.
 * 
 * @author Roland Ewald
 * 
 */
class UserChoice {

  /** Default number of instances. */
  private int desiredInstances = Integer.MAX_VALUE;

  public int getDesiredInstances() {
    return desiredInstances;
  }

  public void setDesiredInstances(int desiredInstances) {
    this.desiredInstances = desiredInstances;
  }

}