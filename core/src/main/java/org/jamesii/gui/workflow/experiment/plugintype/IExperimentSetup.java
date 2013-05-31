/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.plugintype;

import javax.swing.JComponent;

import org.jamesii.core.experiments.BaseExperiment;

/**
 * @author Stefan Rybacki
 * 
 */
public interface IExperimentSetup {

  /**
   * Gets the page at index to be placed on workflow wizard page.
   * 
   * @param index
   *          the page's index
   * @return the page to place on wizard page
   * @see #getPageCount()
   * @see #getPageTitle(int)
   */
  JComponent getPage(int index);

  /**
   * @return the number of available setup pages
   * @see #getPage(int)
   * @see #getPageTitle(int)
   */
  int getPageCount();

  /**
   * Gets the page title.
   * 
   * @param index
   *          the page's index the title is requested for
   * @return the page title for the page with the specified index
   * @see #getPageCount()
   */
  String getPageTitle(int index);

  /**
   * Sets the up the given experiment. By modifying what is necessary to reflect
   * the given setup in the {@link BaseExperiment}.
   * 
   * @param experiment
   *          the experiment to modify to reflect setup
   */
  void setupExperiment(BaseExperiment experiment);

  /**
   * Sets the up an initial state for the editor from the given experiment. This
   * method is called when the editor was created and can have already setup
   * experiment data because it might be loaded from file.
   * 
   * @param experiment
   *          the experiment to setup initial state from
   */
  void setupFromExperiment(BaseExperiment experiment);
  // TODO sr137: incorporate this
  // addValidListener();
  // removeValidListener();

  // public void validateSetup()
}
