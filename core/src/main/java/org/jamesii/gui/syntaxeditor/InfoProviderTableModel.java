/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * Internally used class to provide a {@link JTable} with information tokens
 * including mechanisms to determine related {@link ITokenAction}s trough the
 * use of {@link IInfoProvider#getActionsForToken(ILexerToken)}.
 * 
 * @author Stefan Rybacki
 */
class InfoProviderTableModel extends AbstractTableModel implements
    IInfoProviderListener {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -3431119197477118629L;

  /**
   * The providers.
   */
  private final List<IInfoProvider> providers = new ArrayList<>();

  /**
   * The token cache.
   */
  private final List<ILexerToken> tokenCache = new ArrayList<>();

  /**
   * The token provider mapping.
   */
  private final Map<ILexerToken, IInfoProvider> tokenProviderMapping =
      new HashMap<>();

  /**
   * Instantiates a new info provider table model.
   * 
   * @param providers
   *          the available providers
   */
  public InfoProviderTableModel(IInfoProvider... providers) {
    this(Arrays.asList(providers));
  }

  /**
   * Instantiates a new info provider table model.
   * 
   * @param providers
   *          the available providers
   */
  public InfoProviderTableModel(List<IInfoProvider> providers) {
    for (IInfoProvider p : providers) {
      addInfoProvider(p);
    }
  }

  /**
   * Adds info provider.
   * 
   * @param provider
   *          the provider
   */
  public final synchronized void addInfoProvider(IInfoProvider provider) {
    providers.add(provider);
    provider.addInfoProviderListener(this);
    for (int i = 0; i < provider.getTokenCount(); i++) {
      tokenInserted(provider, i);
    }
  }

  /**
   * Removes info provider.
   * 
   * @param provider
   *          the provider
   */
  public final synchronized void removeInfoProvider(IInfoProvider provider) {
    providers.remove(provider);
    provider.removeInfoProviderListener(this);
    for (int i = 0; i < provider.getTokenCount(); i++) {
      tokenRemoved(provider, i);
    }
  }

  @Override
  public String getColumnName(int column) {
    switch (column) {
    case 1:
      return "Description";
    }
    return null;
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public synchronized int getRowCount() {
    return tokenCache.size();
  }

  @Override
  public synchronized Object getValueAt(int rowIndex, int columnIndex) {
    ILexerToken token = tokenCache.get(rowIndex);
    ILexerTokenStylizer<ILexerToken> stylizer =
        tokenProviderMapping.get(token).getStylizer();
    switch (columnIndex) {
    case 0:
      return stylizer == null ? null : stylizer.getIconFor(token);
    case 1:
      return stylizer == null ? token.toString() : stylizer
          .getDescriptionFor(token);
    }
    return null;
  }

  @Override
  public synchronized void tokenInserted(IInfoProvider provider, int tokenIndex) {
    ILexerToken token = provider.getToken(tokenIndex);
    if (!(provider).isOnlyStyleToken(token)) {
      tokenCache.add(token);
      Collections.sort(tokenCache);
      tokenProviderMapping.put(token, provider);
      int indexOf = tokenCache.indexOf(token);
      fireTableRowsInserted(indexOf, indexOf);
    }
  }

  @Override
  public synchronized void tokenRemoved(IInfoProvider provider, int tokenIndex) {
    ILexerToken token = provider.getToken(tokenIndex);
    int indexOf = tokenCache.indexOf(token);
    tokenCache.remove(token);
    tokenProviderMapping.remove(token);
    if (indexOf >= 0) {
      fireTableRowsDeleted(indexOf, indexOf);
    }
  }

  @Override
  public synchronized void tokensChanged(IInfoProvider provider) {
    reloadTokenCache();
    fireTableDataChanged();
  }

  /**
   * Helper method to reload token cache.
   */
  private synchronized void reloadTokenCache() {
    tokenCache.clear();
    tokenProviderMapping.clear();
    for (IInfoProvider p : providers) {
      for (int i = 0; i < p.getTokenCount(); i++) {
        ILexerToken token = p.getToken(i);
        // only make public if it is not a style only token
        if (!p.isOnlyStyleToken(token)) {
          tokenCache.add(token);
          tokenProviderMapping.put(token, p);
        }
      }
    }
    Collections.sort(tokenCache);
  }

  /**
   * Gets the token at specified index.
   * 
   * @param row
   *          the row index
   * @return the token at the specified index
   */
  public synchronized ILexerToken getTokenAt(int row) {
    return tokenCache.get(row);
  }

  /**
   * Gets the info provider for the specified token.
   * 
   * @param token
   *          the token
   * @return the info provider for token
   */
  public synchronized IInfoProvider getInfoProviderForToken(ILexerToken token) {
    return tokenProviderMapping.get(token);
  }
}
