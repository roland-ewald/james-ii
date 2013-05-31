/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.util.id.IUniqueID;

/**
 * This class provides references to RUNNING! {@link BaseExperiment}s given its
 * id.
 * 
 * @author Simon Bartels
 * 
 */
public final class ExperimentManager {

  /**
   * The map containing for each id the experiment reference.
   */
  private static Map<IUniqueID, BaseExperiment> map = new HashMap<>();

  /**
   * This class is static.
   */
  private ExperimentManager() {
  }

  /**
   * Adds a new experiment to the map. Is called in
   * {@link BaseExperiment#execute()}. PRECONDITION: The experiment's id has
   * already been created.
   * 
   * @param experiment
   *          the experiment
   */
  public static synchronized void addExperiment(BaseExperiment experiment) {
    map.put(experiment.getUniqueIdentifier(), experiment);
  }

  /**
   * Gets an experiment for the given id.
   * 
   * @param id
   *          the experiment's id
   * @return the experiment
   */
  public static synchronized BaseExperiment getExperiment(IUniqueID id) {
    return map.get(id);
  }

  /**
   * Removes an experiment. Is called in {@link BaseExperiment#execute()}.
   * 
   * @param id
   *          the experiment's id
   */
  public static synchronized void removeExperiment(IUniqueID id) {
    map.remove(id);
  }

}
