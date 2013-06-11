/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

/**
 * Interface for a wizard controller that is used in combination with
 * {@link Wizard}.
 * 
 * @author Stefan Rybacki
 */
public interface IWizardController {
  /**
   * Called by the wizard and is used to provide all wizard pages to the wizard.
   * 
   * @param wizard
   *          the wizard using this controller
   */
  void init(IWizard wizard);

  /**
   * Called if the user canceled the wizard
   * 
   * @param wizard
   *          the responsible wizard
   * @param page
   *          the page where the user canceled
   */
  void cancel(IWizard wizard, IWizardPage page);

  /**
   * Called if the user finished the wizard successfully
   * 
   * @param wizard
   *          the responsible wizard
   */
  void finish(IWizard wizard);

  /**
   * @return the start page the wizard is supposed to start with
   */
  String getStartPage();

  /**
   * Called by the wizard to retrieve the page id the "Next" button should go to
   * from current page.
   * 
   * @param currentPage
   *          the Id of the page currently shown
   * @param wizard
   *          the wizard
   * @return the page to display when next button is pressed (this can be
   *         dependent from the input and is called when the actual button is
   *         clicked not earlier)
   */
  String getNextPage(String currentPage, IWizard wizard);

  /**
   * Called by the wizard to retrieve the page id the "Back" button should go to
   * from the current page. The wizard is provided as parameter so one can make
   * use of {@link IWizard#getPreviousPage()} to retrieve the page that was
   * previously displayed.
   * 
   * @param currentPage
   *          the Id of the page currently shown
   * @param wizard
   *          the wizard
   * @return page to go back to when back button is pressed
   */
  String getBackPage(String currentPage, IWizard wizard);

  /**
   * Return true if the wizard has enough information to skip the remaining
   * wizard pages.
   * 
   * @param currentPage
   *          the Id of the page currently shown
   * @param wizard
   *          the calling wizard
   * @return true if all mandatory information are already known and any other
   *         page is optional
   */
  boolean canFinish(String currentPage, IWizard wizard);

  /**
   * Return true if it is possible to go back to a previous page from here.
   * 
   * @param currentPage
   *          the Id of the page currently shown
   * @param wizard
   *          the calling wizard
   * @return true if it is possible to go back from this page
   */
  boolean canCancel(String currentPage, IWizard wizard);

  /**
   * Return true if the page is filled properly and there is a next page.
   * 
   * @param currentPage
   *          the Id of the page currently shown
   * @param wizard
   *          the calling wizard
   * @return true if there is a following page as well as all entered
   *         information are valid
   */
  boolean canNext(String currentPage, IWizard wizard);

  /**
   * Return true if it is possible to go back to a previous page from here.
   * 
   * @param currentPage
   *          the Id of the page currently shown
   * @param wizard
   *          the calling wizard
   * @return true if it is possible to go back from this page
   */
  boolean canBack(String currentPage, IWizard wizard);

  /**
   * Adds a wizard controller listener.
   * 
   * @param listener
   *          the listener
   */
  void addWizardControllerListener(IWizardControllerListener listener);

  /**
   * Removes a previously registered wizard controller listener.
   * 
   * @param listener
   *          the listener
   */
  void removeWizardControllerListener(IWizardControllerListener listener);
}
