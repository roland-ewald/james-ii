/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

/**
 * Basic wizard interface that is used by {@link IWizardPage}s to access the
 * calling wizard.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IWizard {
  /**
   * Returns a previously by {@link #putValue} stored value. This way it is
   * possible to access data from one page in another without know that actual
   * page or holding a reference to it.
   * 
   * @param <B>
   *          the type to return
   * @param id
   *          the id the value is stored with
   * @return the stored object
   * @see #getValue(String)
   * @see #removeValue(String)
   * @see #putValue(String, Object)
   */
  <B> B getValue(String id);

  /**
   * In case {@link #getValue} return {@code null} this method checks whether
   * {@code null} is the actual value or just the result of an unknown
   * {@code id}
   * 
   * @param id
   *          the id to check for existence
   * @return true if id exists
   * @see #getValue(String)
   * @see #removeValue(String)
   * @see #putValue(String, Object)
   */
  boolean isValue(String id);

  /**
   * Removes a previously put value.
   * 
   * @param id
   *          the id of the value to remove
   * @see #getValue(String)
   * @see #putValue(String, Object)
   * @see #isValue(String)
   */
  void removeValue(String id);

  /**
   * Stores a given value with the given id so it can later on be requested
   * using {@link #getValue}. This way it is possible to access data from one
   * page in another without know that actual page or holding a reference to it.
   * 
   * @param id
   *          the id to store with
   * @param value
   *          the value to store
   * 
   * @see #getValue(String)
   * @see #removeValue(String)
   * @see #isValue(String)
   */
  void putValue(String id, Object value);

  /**
   * Returns the name of the previously displayed page
   * 
   * @return the previous page name
   */
  String getPreviousPage();

  /**
   * Use this method to add wizard pages to the wizard. This method is usually
   * called by the {@link IWizardController} within the
   * {@code IWizardController#init(IWizard)} method.
   * 
   * @param page
   *          the wizard page
   * @param id
   *          the page id
   */
  void registerPage(IWizardPage page, String id);

}
