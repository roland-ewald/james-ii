/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

/**
 * Extension to the {@link IWizardPageListener} by adding notification methods
 * for cancel and finish state changes.
 * 
 * @author Stefan Rybacki
 */
public interface IWizardControllerListener extends IWizardPageListener {

  /**
   * cancel state changed for given page
   * 
   * @param page
   *          the wizard page which's cancel state changed
   * @see IWizardController#canCancel(String, IWizard)
   */
  void cancelChanged(IWizardPage page);

  /**
   * finish state changed for given page
   * 
   * @param page
   *          the wizard page which's finish state changed
   * @see IWizardController#canFinish(String, IWizard)
   */
  void finishChanged(IWizardPage page);

}
