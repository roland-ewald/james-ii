/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

/**
 * Inteface for the wizard help provider.
 * 
 * @author Stefan Rybacki
 */
public interface IWizardHelpProvider {
  /**
   * Shows the help in whatever form for the given wizard page in the given
   * wizard. This method is called when the "Help" button of a wizard is
   * clicked.
   * 
   * @param wizard
   *          the wizard the "Help" button was clicked on
   * @param page
   *          the page that was active when the button was clicked
   */
  void showHelp(IWizard wizard, IWizardPage page);
}
