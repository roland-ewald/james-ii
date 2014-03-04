/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database;

import org.jamesii.perfdb.entities.IIDEntity;
import org.jamesii.perfdb.entities.IProblemDefinition;

/**
 * Represents a problem used to train a certain selector.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public interface ITrainingProblem extends IIDEntity {

  IProblemDefinition getSimulationProblem();

  void setSimulationProblem(IProblemDefinition problemDefinition);

  ISelector getSelector();

  void setSelector(ISelector selector);

  int getNumOfConfigs();

  void setNumOfConfigs(int numOfConfigs);

  int getNumOfFeatures();

  void setNumOfFeatures(int numOfFeatures);

}