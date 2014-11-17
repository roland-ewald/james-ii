/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.util.collection.ListenerSupport;

/**
 * Use this class as starting point for {@link IWizardController}
 * implementations. It already implements a couple of methods so that the
 * implementation can focus on the plain wizard controlling tasks. It even
 * provides basic wizard controlling implementation if it is not overridden.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractWizardController implements IWizardController,
    IWizardPageListener {

  /**
   * The start page.
   */
  private final String startPage;

  /**
   * The wizard pages.
   */
  private final List<IWizardPage> pages = new ArrayList<>();

  /**
   * The page id mapping.
   */
  private final Map<IWizardPage, String> pageIDMapping = new HashMap<>();

  /**
   * The Id page mapping.
   */
  private final Map<String, IWizardPage> idPageMapping = new HashMap<>();

  /**
   * The wizard controller listeners.
   */
  private final ListenerSupport<IWizardControllerListener> listeners =
      new ListenerSupport<>();

  /**
   * Instantiates a new abstract wizard controller.
   * 
   * @param startPage
   *          the start page
   */
  public AbstractWizardController(String startPage) {
    this.startPage = startPage;
  }

  /**
   * Adds the wizard page.
   * 
   * @param pageID
   *          the page id
   * @param page
   *          the page
   */
  public synchronized void addWizardPage(String pageID, IWizardPage page) {
    pages.add(page);
    pageIDMapping.put(page, pageID);
    idPageMapping.put(pageID, page);
    page.addWizardPageListener(this);
  }

  @Override
  public synchronized String getBackPage(String currentPage, IWizard wizard) {
    return wizard.getPreviousPage();
  }

  @Override
  public synchronized String getNextPage(String currentPage, IWizard wizard) {
    // basic implementation assuming the order of added pages is the
    // order of pages in wizard

    // get wizard page for id
    IWizardPage current = idPageMapping.get(currentPage);
    // find current page in pages list
    int index = pages.indexOf(current);

    if (index >= pages.size() - 1) {
      return null;
    }

    IWizardPage next = pages.get(index + 1);

    return pageIDMapping.get(next);
  }

  @Override
  public final synchronized String getStartPage() {
    return startPage;
  }

  @Override
  public final synchronized void init(IWizard wizard) {
    for (Entry<String, IWizardPage> e : idPageMapping.entrySet()) {
      wizard.registerPage(e.getValue(), e.getKey());
    }
  }

  @Override
  public synchronized boolean canCancel(String currentPage, IWizard wizard) {
    return idPageMapping.get(currentPage).canCancel(wizard);
  }

  @Override
  public synchronized boolean canNext(String currentPage, IWizard wizard) {
    IWizardPage page = idPageMapping.get(currentPage);
    int index = pages.indexOf(page);
    return index < pages.size() - 1 && page.canNext(wizard);
  }

  @Override
  public synchronized boolean canBack(String currentPage, IWizard wizard) {
    return idPageMapping.get(currentPage).canBack(wizard);
  }

  @Override
  public final synchronized boolean canFinish(String currentPage, IWizard wizard) {
    return idPageMapping.get(currentPage).canNext(wizard)
        && finishable(currentPage, wizard);
  }

  /**
   * Override this to determine if from the specified page and all gathered
   * information it is possible to finish the wizard. Also override this if you
   * have a custom implementation of {@link #getNextPage(String, IWizard)} so
   * that the last page added is not the last page in all cases. Also override
   * this if you want to have earlier finish options than last page.
   * 
   * @param currentPage
   *          the current page
   * @param wizard
   *          the wizard
   * @return true, if finishable
   */
  protected boolean finishable(String currentPage, IWizard wizard) {
    // return true if last page is reached
    return (pages.indexOf(idPageMapping.get(currentPage)) == pages.size() - 1);
  }

  @Override
  public final synchronized void addWizardControllerListener(
      IWizardControllerListener listener) {
    listeners.addListener(listener);
  }

  @Override
  public final synchronized void removeWizardControllerListener(
      IWizardControllerListener listener) {
    listeners.removeListener(listener);
  }

  @Override
  public void backChanged(IWizardPage page) {
    fireBackChanged(page);
  }

  @Override
  public void helpChanged(IWizardPage page) {
    fireStatesChanged(page);
  }

  @Override
  public void nextChanged(IWizardPage page) {
    fireNextChanged(page);
  }

  @Override
  public void statesChanged(IWizardPage page) {
    fireStatesChanged(page);
  }

  @Override
  public void next(IWizardPage page) {
    fireNext(page);
  }

  /**
   * Notifies the registered listeners that the return value of
   * {@link #canBack(String, IWizard)} changed.
   * 
   * @param page
   *          the page whose state changed
   */
  protected final synchronized void fireBackChanged(IWizardPage page) {
    for (IWizardControllerListener l : listeners.getListeners()) {
      l.backChanged(page);
    }
  }

  /**
   * Notifies the registered listeners that the return value of
   * {@link #canCancel(String, IWizard)} changed.
   * 
   * @param page
   *          the page whose state changed
   */
  protected final synchronized void fireCancelChanged(IWizardPage page) {
    for (IWizardControllerListener l : listeners.getListeners()) {
      l.cancelChanged(page);
    }
  }

  /**
   * Notifies the registered listeners that the return value of
   * {@link #canFinish(String, IWizard)} changed.
   * 
   * @param page
   *          the page whose state changed
   */
  protected final synchronized void fireFinishChanged(IWizardPage page) {
    for (IWizardControllerListener l : listeners.getListeners()) {
      l.finishChanged(page);
    }
  }

  /**
   * Notifies the registered listeners that the next action should be invoked if
   * {@link #canNext(String, IWizard)} returns {@code true} and the page is the
   * current active page
   * 
   * @param page
   *          the page who wants fire next event
   */
  protected final synchronized void fireNext(IWizardPage page) {
    for (IWizardControllerListener l : listeners) {
      l.next(page);
    }
  }

  /**
   * Notifies the registered listeners that the return value of
   * {@link #canNext(String, IWizard)} changed.
   * 
   * @param page
   *          the page whose state changed
   */
  protected final synchronized void fireNextChanged(IWizardPage page) {
    for (IWizardControllerListener l : listeners.getListeners()) {
      l.nextChanged(page);
    }
  }

  /**
   * Notifies the registered listeners that the return value of at least one of
   * the following methods changed
   * <ul>
   * <li>{@link #canCancel(String, IWizard)}</li>
   * <li>{@link #canBack(String, IWizard)}</li>
   * <li>{@link #canNext(String, IWizard)}</li>
   * <li>{@link #canFinish(String, IWizard)}</li>
   * </ul>
   * 
   * @param page
   *          the page whose states changed
   */
  protected final synchronized void fireStatesChanged(IWizardPage page) {
    for (IWizardControllerListener l : listeners.getListeners()) {
      l.statesChanged(page);
    }
  }

}
