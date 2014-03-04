/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.simple;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;
import org.jamesii.simspex.exploration.AbstractSimSpaceExplorer;
import org.jamesii.simspex.exploration.ExplorationPhase;
import org.jamesii.simspex.exploration.calibration.SimpleSimStopTimeCalibrator;
import org.jamesii.simspex.exploration.utils.RandomModelSetupSampler;

/**
 * Simple simulation space explorer, just draws instances from the
 * {@link SelectionTreeSet} randomly.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleSimSpaceExplorer extends AbstractSimSpaceExplorer {

  /** The default number of replications. */
  private static final int DEFAULT_REPLICATIONS = 10;

  /** The default maximum number of model setups to be explored. */
  private static final int DEFAULT_MAX_MODELS_EXPLORED = 25;

  /** Maximal number of model space elements. */
  private int maxModelSpaceElems = DEFAULT_MAX_MODELS_EXPLORED;

  /** Replications per configuration. */
  private int numOfReplications = DEFAULT_REPLICATIONS;

  /**
   * Counter for the number of elements from the model space that have been
   * investigated.
   */
  private transient int currentElement = 0;

  /** Current list index. */
  private transient int currentIndex = 0;

  /** Current replication. */
  private transient int currentReplication = 0;

  /** The random number generator to draw new model parameter setups. */
  private IRandom random;

  /** The model setup sampler. */
  private RandomModelSetupSampler modelSetupSampler =
      new RandomModelSetupSampler();

  /**
   * Constructor for bean compliance.
   */
  public SimpleSimSpaceExplorer() {
  }

  /**
   * Instantiates a new simple simulation space explorer.
   * 
   * @param configs
   *          the configurations to explore
   */
  public SimpleSimSpaceExplorer(List<ParameterBlock> configs) {
    super(configs);
    setCalibrator(new SimpleSimStopTimeCalibrator(this));
  }

  /**
   * Instantiates a new simple simulation space explorer.
   * 
   * @param selectionTreeSet
   *          the selection tree set
   */
  public SimpleSimSpaceExplorer(SelectionTreeSet selectionTreeSet) {
    this(selectionTreeSet, null);
  }

  /**
   * Instantiates a new simple simulation space explorer.
   * 
   * @param selectionTreeSet
   *          the selection tree set
   * @param blackList
   *          the black list (of factories to avoid)
   */
  public SimpleSimSpaceExplorer(SelectionTreeSet selectionTreeSet,
      List<String> blackList) {
    super(selectionTreeSet, blackList);
    setCalibrator(new SimpleSimStopTimeCalibrator(this));
  }

  @Override
  public void init() {
    setCurrentElement(0);
    setCurrentIndex(0);
    setCurrentReplication(0);
    setPhase(ExplorationPhase.START_CALIBRATION);
  }

  @Override
  protected boolean nextProblem() {
    return getCurrentIndex() >= getConfigurations().size();
  }

  @Override
  protected ParameterBlock nextConfigToExplore() {
    if (getCurrentElement() > maxModelSpaceElems) {
      return null;
    }

    if (getCurrentReplication() < numOfReplications) {
      setCurrentReplication(getCurrentReplication() + 1);
      return getConfigurations().get(getCurrentIndex());
    }

    setCurrentReplication(1);
    setCurrentIndex(getCurrentIndex() + 1);
    return getCurrentIndex() < getConfigurations().size() ? getConfigurations()
        .get(getCurrentIndex()) : null;
  }

  @Override
  public boolean isFinished() {
    return getCurrentElement() > maxModelSpaceElems;
  }

  @Override
  protected Map<String, Serializable> nextModelSetupToExplore() {
    resetNewSetup();
    if (getCurrentElement() > maxModelSpaceElems) {
      return null;
    }
    SimSystem.getRNGGenerator().setSeed(getRandom().nextLong());
    return modelSetupSampler.sampleSetup(getModelVariables());
  }

  /**
   * Reset counters for new setup.
   */
  protected void resetNewSetup() {
    setCurrentElement(getCurrentElement() + 1);
    setCurrentIndex(0);
    setCurrentReplication(0);
  }

  public int getMaxModelSpaceElems() {
    return maxModelSpaceElems;
  }

  public void setMaxModelSpaceElems(int maxModelSpaceElems) {
    this.maxModelSpaceElems = maxModelSpaceElems;
  }

  public int getNumOfReplications() {
    return numOfReplications;
  }

  public void setNumOfReplications(int numOfReplications) {
    this.numOfReplications = numOfReplications;
  }

  protected IRandom getRandom() {
    if (random == null) {
      random = SimSystem.getRNGGenerator().getNextRNG();
    }
    return random;
  }

  public int getCurrentElement() {
    return currentElement;
  }

  public void setCurrentElement(int currentElement) {
    this.currentElement = currentElement;
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  public void setCurrentIndex(int currentIndex) {
    this.currentIndex = currentIndex;
  }

  public int getCurrentReplication() {
    return currentReplication;
  }

  public void setCurrentReplication(int currentReplication) {
    this.currentReplication = currentReplication;
  }

}
