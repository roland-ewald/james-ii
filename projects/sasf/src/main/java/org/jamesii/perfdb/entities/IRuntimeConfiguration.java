/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;


import java.util.Date;

import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;


/**
 * Interface for runtime configurations. Classes with this interface represent
 * the configuration of the simulation system. The mode of execution should be
 * reproducible from what is stored inside this object, i.e., the selection
 * information and parameter settings of all algorithms that can be tuned should
 * be stored here.
 * 
 * @author Roland Ewald
 * 
 */
public interface IRuntimeConfiguration extends IIDEntity {

  /**
   * Gets the selection tree. The selection tree characterises the setup of the
   * simulation system.
   * 
   * @return the selection tree
   */
  SelectionTree getSelectionTree();

  /**
   * Sets the selection tree.
   * 
   * @param selectionTree
   *          the new selection tree
   */
  void setSelectionTree(SelectionTree selectionTree);

  /**
   * Gets the selection tree hash. This hash code facilitates querying the
   * database for similar runtime configurations.
   * 
   * @return the selection tree hash
   */
  long getSelectionTreeHash();

  /**
   * Tests whether this application's results are still of interest. If false,
   * there are other applications with a newer version of the configuration.
   * 
   * @return true, if application is up to date
   */
  boolean isUpToDate();

  /**
   * Gets the version of the application.
   */
  int getVersion();

  /**
   * Gets the date when the runtime configuration was introduced to the
   * performance database.
   * 
   * @return the introduction rate
   */
  Date getIntroductionDate();

}