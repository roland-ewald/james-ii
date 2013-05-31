/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.core.experiments.optimization.parameter.Configuration;
import org.jamesii.core.experiments.optimization.parameter.representativeValue.IRepresentativeValuesComparator;
import org.jamesii.core.model.variables.BaseVariable;

/**
 * Base class for storing data for simulation runs on a defined configuration.
 * 
 * @author Arvid Schwecke
 * @author Roland Ewald
 */
public class ConfigurationInfos implements Comparable<ConfigurationInfos>,
    Serializable {

  /** Generated serialVersionUID. */
  private static final long serialVersionUID = -4099098351663490434L;

  /** List of single configuration infos. */
  private List<ConfigurationInfo> configInfos = new ArrayList<>();

  /** The configuration. */
  private Configuration configuration = null;

  /** ID of configuration. */
  private int configurationID;

  /** The accumulated objective (mean, median, or sth alike). */
  private Map<String, Double> representativeObjectives = null;

  /** Flag that indicates if storage was used. */
  private boolean storageUse = false;

  /** Total runtime. */
  private double totalRunTime = 0;

  private IRepresentativeValuesComparator repValueComparator;

  /**
   * Default constructor.
   * 
   * @param configuration
   *          the config
   * @param configID
   *          the D of the configuration
   * @param repValComparator
   *          comparator for the representative objective values
   */
  public ConfigurationInfos(Configuration configuration, int configID,
      IRepresentativeValuesComparator repValComparator) {
    this.configuration = configuration;
    this.configurationID = configID;
    this.repValueComparator = repValComparator;
  }

  /**
   * Adds the run.
   * 
   * @param response
   *          the response
   * @param runTime
   *          the run time
   */
  public void addRun(Map<String, BaseVariable<?>> response, double runTime) {
    configInfos.add(new ConfigurationInfo(configuration, response));
    this.totalRunTime += runTime;
  }

  /**
   * Adds the run.
   * 
   * @param response
   *          the response
   * @param objectves
   *          the objective values
   * @param runTime
   *          the run time
   */
  public void addRun(Map<String, BaseVariable<?>> response,
      Map<String, Double> objectves, double runTime) {
    configInfos.add(new ConfigurationInfo(configuration, response, objectves));
    this.totalRunTime += runTime;
  }

  /**
   * Compare to.
   * 
   * @param info
   *          the info
   * 
   * @return integer that determines the relation (<,=, or >0)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(ConfigurationInfos info) {
    return repValueComparator.compare(representativeObjectives,
        info.getRepresentativeObjectives());
  }

  @Override
  public boolean equals(Object info) {
    if (!(info instanceof ConfigurationInfos)) {
      return false;
    }
    return configurationID == ((ConfigurationInfos) info).configurationID;
  }

  @Override
  public int hashCode() {
    return configurationID + representativeObjectives.hashCode();
  }

  /**
   * Tests if there is an objective which is infinity.
   * 
   * @return true if one observed objective is infinity
   */
  public boolean containsInfinty() {
    for (Map<String, Double> objectives : getObjectives()) {
      for (Double o : objectives.values()) {
        if (o == Double.POSITIVE_INFINITY) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Gets the configuration.
   * 
   * @return the configuration
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Gets the iD.
   * 
   * @return the iD
   */
  public int getID() {
    return configurationID;
  }

  /**
   * Get last configuration info.
   * 
   * @return last config info if existent, otherwise null
   */
  public ConfigurationInfo getLastConfigurationInfo() {
    if (configInfos.size() == 0) {
      return null;
    }
    return configInfos.get(configInfos.size() - 1);
  }

  /**
   * Gets the objectives.
   * 
   * @return objectives
   */
  public List<Map<String, Double>> getObjectives() {
    List<Map<String, Double>> objectives = new ArrayList<>();
    for (ConfigurationInfo info : configInfos) {
      objectives.add(info.getObjectives());
    }
    return objectives;
  }

  /**
   * Gets the representative objectives.
   * 
   * @return the representative objectives
   */
  public Map<String, Double> getRepresentativeObjectives() {
    return representativeObjectives;
  }

  /**
   * Get number of runs.
   * 
   * @return number of runs
   */
  public int getRunCount() {
    return configInfos.size();
  }

  /**
   * Gets the run time.
   * 
   * @return the run time
   */
  public double getRunTime() {
    return totalRunTime;
  }

  /**
   * Checks if is storage use.
   * 
   * @return true, if is storage use
   */
  public boolean isStorageUse() {
    return storageUse;
  }

  /**
   * Sets the representative objective.
   * 
   * @param representativeObjective
   *          the new representative objective
   */
  public void setRepresentativeObjective(
      Map<String, Double> representativeObjective) {
    this.representativeObjectives = representativeObjective;
  }

  /**
   * Sets the storage use.
   * 
   * @param storageUse
   *          the new storage use
   */
  public void setStorageUse(boolean storageUse) {
    this.storageUse = storageUse;
  }

  /**
   * Contains configuration.
   * 
   * @param compared
   *          the compared
   * 
   * @return true, if successful
   */
  public boolean containsConfiguration(Configuration compared) {
    return (configuration.equals(compared));
  }

  /**
   * Gets the configuration infos.
   * 
   * @return the configuration infos
   */
  public List<ConfigurationInfo> getConfigInfos() {
    return configInfos;
  }

}
