/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration;

import java.util.List;
import java.util.Set;

import org.jamesii.core.experiments.steering.IExperimentSteerer;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;

/**
 * Interface for simulation space explorers. It will be initialised with an
 * instance of {@link SelectionTreeSet}, which defines the space to be explored.
 * 
 * @author Roland Ewald
 * 
 */
public interface ISimSpaceExplorer extends IExperimentSteerer {

  /**
   * Get the selection tree set to be explored.
   * 
   * @return the selection tree set to be explored
   */
  SelectionTreeSet getSelectionTreeSet();

  /**
   * Get all model variables that are defined. These {@link BaseVariable}
   * instances define the model space to be explored.
   * 
   * @return set of all defined model variables
   */
  Set<BaseVariable<?>> getModelVariables();

  /**
   * Set the model variables, which constitute the model space to be explored.
   * 
   * @param modelVariables
   *          the set of model variables
   */
  void setModelVariables(Set<BaseVariable<?>> modelVariables);

  /**
   * Gets the configurations.
   * 
   * @return the configurations
   */
  List<ParameterBlock> getConfigurations();

  /**
   * Sets the configurations.
   * 
   * @param configs
   *          the new configurations
   */
  void setConfigurations(List<ParameterBlock> configs);

}
