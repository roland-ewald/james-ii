/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

/**
 * Listener interface for {@link IInfoProvider}s that can be used to notify
 * about changes to an {@link IInfoProvider}.
 * 
 * @author Stefan Rybacki
 */
public interface IInfoProviderListener {

  /**
   * Called when a token was removed from the list of
   * {@link IInfoProvider#getToken(int)}.
   * 
   * @param provider
   *          the provider a token was removed from
   * @param tokenIndex
   *          the token index of the removed token
   */
  void tokenRemoved(IInfoProvider provider, int tokenIndex);

  /**
   * Called when a token was inserted into the list of
   * {@link IInfoProvider#getToken(int)}.
   * 
   * @param provider
   *          the provider a token was inserted to
   * @param tokenIndex
   *          the token index of the inserted token
   */
  void tokenInserted(IInfoProvider provider, int tokenIndex);

  /**
   * Called when more than one tokens changed (removed, inserted, etc.).
   * 
   * @param provider
   *          the provider the tokens changed for
   */
  void tokensChanged(IInfoProvider provider);
}
