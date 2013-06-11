/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

/**
 * Listener interface that is used to notify an {@link IWizard} to check the
 * state's of its next, back, finish, cancel and help buttons according to the
 * wizard page.
 * 
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IWizardPageListener {
  /**
   * next state changed for given page
   * 
   * @param page
   *          the wizard page which's next state changed
   * @see IWizardPage#canNext(IWizard)
   */
  void nextChanged(IWizardPage page);

  /**
   * back state changed for given page
   * 
   * @param page
   *          the wizard page which's back state changed
   * @see IWizardPage#canBack(IWizard)
   */
  void backChanged(IWizardPage page);

  /**
   * help state changed for given page
   * 
   * @param page
   *          the wizard page which's help state changed
   * @see IWizardPage#canHelp(IWizard)
   */
  void helpChanged(IWizardPage page);

  /**
   * any of the states (next, cancel, back, finished or help) changed
   * 
   * @param page
   *          the wizard page which's state changed
   * @see IWizardPage#canNext(IWizard)
   * @see IWizardPage#canBack(IWizard)
   * @see IWizardPage#canHelp(IWizard)
   */
  void statesChanged(IWizardPage page);

  /**
   * Called when the page likes to invoke a next action and the listener should
   * react accordingly if the page's {@link IWizardPage#canNext(IWizard)} method
   * returns {@code true} and the page is the current active page
   * 
   * @param page
   *          the invoking page
   */
  void next(IWizardPage page);
}
