/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.jamesii.gui.base.AbstractPropertyChangeSupport;

/**
 * Simple wizard model, that stores wizard pages, holds the current visible page
 * and is used internally by {@link Wizard}.
 * 
 * @author Stefan Rybacki
 * 
 */
final class WizardModel extends AbstractPropertyChangeSupport {
  /**
   * wizard pages
   */
  private final Map<String, IWizardPage> pages = new ConcurrentHashMap<>();

  /**
   * storage map
   */
  private Map<String, Object> session = new ConcurrentHashMap<>();

  /**
   * current open wizard page
   */
  private IWizardPage current = null;

  /**
   * wizard instance
   */
  private IWizard wizard;

  /**
   * page history for {@link #getPreviousPageId()}
   */
  private Deque<String> pageHistory = new ArrayDeque<>();

  /**
   * current page's id
   */
  private String currentId = null;

  /**
   * Creates a new model
   * 
   * @param wizard
   *          the wizard instance the model belongs to
   */
  public WizardModel(IWizard wizard) {
    this.wizard = wizard;
  }

  /**
   * Called after the wizard was closed
   */
  public synchronized void cleanUp() {
    for (IWizardPage page : pages.values()) {
      page.removeAllWizardPageListeners();
    }
    pages.clear();
    session.clear();
    current = null;
    wizard = null;
    pageHistory.clear();
    currentId = null;
    pages.clear();
    session = null;
    pageHistory = null;
  }

  /**
   * Adds a wizard page to the model
   * 
   * @param page
   *          the page to ad
   * @param id
   *          the page's id
   */
  public synchronized void addPage(IWizardPage page, String id) {
    if (pages.containsKey(id)) {
      throw new IllegalArgumentException("Specified ID already in use!");
    }

    pages.put(id, page);
  }

  /**
   * Changes the currently visible page to the page identified by the given id.
   * 
   * @param id
   *          the new page's id
   * @param back
   *          flag indicating whether this action is result of a "back" action
   *          of the wizard
   * @return the wizard page that is identified by the given id ({@code null} if
   *         the id is unknown)
   */
  public synchronized IWizardPage setCurrentPage(String id, boolean back) {
    IWizardPage page = getPage(id);
    if (page != null) {
      if (back) {
        // pop as long as id==pop
        while (!pageHistory.isEmpty()) {
          if (pageHistory.pop().equals(id)) {
            break;
          }
        }
      } else if (currentId != null) {
        pageHistory.push(currentId);
      }

      IWizardPage old = current;
      current = page;
      currentId = id;

      // notify new page that it is about to show
      page.showing(wizard);

      // notify property change listeners
      firePropertyChange("currentPage", old, page);
      return current;
    }
    return null;
  }

  /**
   * @return the current active wizard page
   */
  public synchronized IWizardPage getCurrentPage() {
    return current;
  }

  /**
   * @return the current page's id
   */
  public synchronized String getCurrentPageId() {
    return currentId;
  }

  /**
   * Returns the wizard page identified by the given id
   * 
   * @param id
   *          the wizard page's id
   * @return the wizard page identified by the given id ({@code null} if id is
   *         unknown)
   */
  public synchronized IWizardPage getPage(String id) {
    return pages.get(id);
  }

  /**
   * Stores a given value into the current {@link #session} using the given id
   * to identify that value.
   * 
   * @param id
   *          the id of the value
   * @param v
   *          the value itself
   */
  public synchronized void putValue(String id, Object v) {
    if (v != null && id != null) {
      session.put(id, v);
    }
  }

  /**
   * Retrieves a previously stored value identified by the given id from the
   * current wizard {@link #session}
   * 
   * @param <B>
   *          the type to return
   * @param id
   *          the value's id
   * @return the value identified by the given id ({@code null} if id is unknown
   *         or the value is null)
   * @see #isValue(String)
   */
  @SuppressWarnings("unchecked")
  public synchronized <B> B getValue(String id) {
    return (B) session.get(id);
  }

  /**
   * Checks whether a value with the given id is stored in the current
   * {@link #session}
   * 
   * @param id
   *          the value id to check
   * @return true if a value of the id is stored in the session, false else
   */
  public synchronized boolean isValue(String id) {
    return session.containsKey(id);
  }

  /**
   * Deletes a previously stored value identified by the given id from the
   * current {@link #session}
   * 
   * @param id
   *          the value's id
   */
  public synchronized void removeValue(String id) {
    session.remove(id);
  }

  /**
   * @return the id of the wizard page that was visible before the current
   *         wizard page ({@code null} if the current page is the first page)
   */
  public synchronized String getPreviousPageId() {
    if (!pageHistory.isEmpty()) {
      return pageHistory.peek();
    }
    return null;
  }

  /**
   * Close pages.
   */
  public synchronized void closePages() {
    for (Entry<String, IWizardPage> page : pages.entrySet()) {
      page.getValue().closed(wizard);
    }
  }

}
