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

import org.jamesii.gui.utils.ListenerSupport;

/**
 * Simple abstract wizard page implementing some of the methods already. It also
 * provides a caching mechanism for created pages and allows to prepopulate and
 * restore data for that page.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractWizardPage implements IWizardPage {
  /**
   * created page (used to only create the page once)
   */
  private JComponent page = null;

  /**
   * the page's subtitle
   */
  private String subTitle;

  /**
   * the page's title
   */
  private String title;

  /**
   * support for wizard page listeners
   */
  private ListenerSupport<IWizardPageListener> listeners =
      new ListenerSupport<>();

  /**
   * Creates a new abstract wizard page with no title and no subtitle
   */
  public AbstractWizardPage() {
    this(null, null);
  }

  @Override
  public boolean canCancel(IWizard wizard) {
    return true;
  }

  /**
   * Creates a new abstract wizard page with the specified title and subtitle
   * 
   * @param title
   *          the page's title
   * @param subTitle
   *          the page's subtitle
   */
  public AbstractWizardPage(String title, String subTitle) {
    this.title = title;
    this.subTitle = subTitle;
  }

  @Override
  public final JComponent getPage() {
    return page;
  }

  /**
   * Creates the page without any prepopulated data
   * 
   * @return the plain page to display
   */
  protected abstract JComponent createPage();

  /**
   * Implement this if you want to populate data to the page. This can be used
   * to restore previously entered values when going back to that page or to
   * fill fields depending on previously entered data.
   * 
   * @param wizard
   *          the wizard the page is to be shown in
   */
  protected abstract void prepopulatePage(IWizard wizard);

  /**
   * Implement this if you want to transfer data from one page to another or to
   * persist current data to be able to restore or reuse it wihtin the wizard.
   * 
   * @param wizard
   *          the wizard the page is shown in
   */
  protected abstract void persistData(IWizard wizard);

  /**
   * Use this if you don't need cached page anymore. This can be in case there
   * is no way of going back to that page or just to save memory. In case this
   * method was called and the page is shown again, it will automatically be
   * recreated.
   * 
   */
  protected final void deletePage() {
    page = null;
  }

  @Override
  public final void hiding(IWizard wizard) {
    persistData(wizard);
  }

  @Override
  public final void showing(IWizard wizard) {
    if (page == null) {
      page = createPage();
    }
    prepopulatePage(wizard);
  }

  @Override
  public void shown(IWizard wizard) {
    // should be overridden as needed
  }

  @Override
  public Dimension getPreferredSize() {
    return null;
  }

  @Override
  public Icon getPageIcon() {
    return null;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getSubTitle() {
    return subTitle;
  }

  @Override
  public void addWizardPageListener(IWizardPageListener listener) {
    listeners.addListener(listener);
  }

  @Override
  public void removeWizardPageListener(IWizardPageListener listener) {
    listeners.removeListener(listener);
  }

  /**
   * Notifies the registered listeners that the return value of
   * {@link #canNext(IWizard)} changed.
   */
  protected final synchronized void fireNextChanged() {
    for (IWizardPageListener l : listeners.getListeners()) {
      l.nextChanged(this);
    }
  }

  /**
   * Notifies the registered listeners that the return value of
   * {@link #canBack(IWizard)} changed.
   */
  protected final synchronized void fireBackChanged() {
    for (IWizardPageListener l : listeners.getListeners()) {
      l.backChanged(this);
    }
  }

  /**
   * Notifies the registered listeners that the return value of
   * {@link #canHelp(IWizard)} changed.
   */
  protected final synchronized void fireHelpChanged() {
    for (IWizardPageListener l : listeners.getListeners()) {
      l.helpChanged(this);
    }
  }

  /**
   * Notifies the registered listeners that the return value of at least one of
   * the following methods changed
   * <ul>
   * <li>{@link #canBack(IWizard)}</li>
   * <li>{@link #canNext(IWizard)}</li>
   * <li>{@link #canHelp(IWizard)}</li>
   * </ul>
   */
  protected final synchronized void fireStatesChanged() {
    for (IWizardPageListener l : listeners.getListeners()) {
      l.statesChanged(this);
    }
  }

  /**
   * Notifies the registered listeners that the next action should be invoked if
   * {@link #canNext(IWizard)} returns {@code true} and the page is the current
   * active page
   */
  protected final synchronized void fireNext() {
    for (IWizardPageListener l : listeners) {
      l.next(this);
    }
  }

  @Override
  public void removeAllWizardPageListeners() {
    listeners.clear();
  }

  @Override
  public void closed(IWizard wizard) {
  }
}
