/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Important interface for wizards. Classes implementing this interface
 * represent one page of a wizard with the ability to provide information on
 * next, previous pages as well as help information, title, sub title and icon.
 * <p>
 * The event call order is as follows:
 * <ul>
 * <li>{@link #showing(IWizard)}</li>
 * <li>{@link #getPage()}</li>
 * <li>{@link #shown(IWizard)}</li>
 * <li>{@link #hiding(IWizard)}</li>
 * </ul>
 * This means all initialization is done in {@link #showing(IWizard)}, before
 * {@link #getPage()} returns the prepared page and {@link #shown(IWizard)} is
 * called. {@link #hiding(IWizard)} is then again called when the page is
 * dismissed.
 * 
 * @author Stefan Rybacki
 * 
 * @see AbstractWizardPage
 */
public interface IWizardPage {
  /**
   * the actual page to display in the wizard dialog prepared in
   * {@link #showing(IWizard)}
   * 
   * @return page to display
   */
  JComponent getPage();

  /**
   * Is called by the wizard before the page is shown. This is meant to be used
   * to initialize the wizard page including prepopulating data and so on. To
   * access previously collected data by other {@link IWizardPage}s you can use
   * the {@link IWizard#getValue(String)} method from the {@link IWizard}
   * interface, assuming the desired page did put the values into the wizard
   * session.
   * 
   * @param wizard
   *          the calling wizard
   * @see IWizard#putValue(String, Object)
   * @see IWizard#getValue(String)
   */
  void showing(IWizard wizard);

  /**
   * Is called by the wizard after the page is displayed. This can be used to
   * start automatic tasks like an install routine or a server contact.
   * 
   * @param wizard
   *          the calling wizard
   */
  void shown(IWizard wizard);

  /**
   * Is called when the page is being closed. This happens when the close,
   * finish or cancel button is pressed. This can be used to clean up.
   * 
   * @param wizard
   *          the wizard
   */
  void closed(IWizard wizard);

  /**
   * Is called when the page is being disabled. This happens when the next or
   * back or finish button is pressed. This can be used to clean up or to
   * persist entered data. The {@link IWizard} interface provides session
   * management using {@link IWizard#putValue(String, Object)} and
   * {@link IWizard#getValue(String)}
   * 
   * 
   * @param wizard
   *          the calling wizard
   * 
   * @see IWizard#putValue(String, Object)
   * @see IWizard#getValue(String)
   */
  void hiding(IWizard wizard);

  /**
   * Return true if the page is filled properly and there is a next page.
   * 
   * @param wizard
   *          the calling wizard
   * 
   * @return true if there is a following page as well as all entered
   *         information are valid
   */
  boolean canNext(IWizard wizard);

  /**
   * Return true if the page and therefore the wizard can be canceled this
   * includes that currently on the page running actions can be canceled.
   * 
   * @param wizard
   *          the calling wizard
   * @return true if page can be canceled
   */
  boolean canCancel(IWizard wizard);

  /**
   * Return true if for the actual state there is help available for that page.
   * 
   * @param wizard
   *          the calling wizard
   * @return true if the page provides help
   */
  boolean canHelp(IWizard wizard);

  /**
   * Return true if it is possible to go back to a previous page from here.
   * 
   * @param wizard
   *          the calling wizard
   * 
   * @return true if it is possible to go back from this page
   */
  boolean canBack(IWizard wizard);

  /**
   * if {@link #canHelp(IWizard)} returns true this function might be called to
   * display help information. <b>Note</b>: this function is only called if the
   * help button is clicked so the provided help can even be context specific
   * 
   * @return the help provider for the page
   */
  IWizardHelpProvider getHelp();

  /**
   * @return the preferred size for this page (the wizard uses the maximum
   *         preferred size of all pages to determine its initial size)
   */
  Dimension getPreferredSize();

  /**
   * @return the icon to be displayed as page icon (in the top right corner)
   */
  Icon getPageIcon();

  /**
   * @return the title to display
   */
  String getTitle();

  /**
   * @return the sub title to display
   */
  String getSubTitle();

  /**
   * Adds a wizard page listener
   * 
   * @param listener
   *          the listener to attach
   */
  void addWizardPageListener(IWizardPageListener listener);

  /**
   * Removes a previously attached wizard page listener
   * 
   * @param listener
   *          the listener to remove
   */
  void removeWizardPageListener(IWizardPageListener listener);

  /**
   * Removes all registered wizard page listeners.
   */
  void removeAllWizardPageListeners();
}
