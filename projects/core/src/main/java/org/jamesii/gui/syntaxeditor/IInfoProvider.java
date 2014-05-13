/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.io.Reader;
import java.util.List;

/**
 * Interface for information providers, providing information tokens for the
 * {@link SyntaxEditor}. Where those tokens can be annotated using strokes,
 * colors and background colors as well as having tooltips, icons and
 * descriptions. Those information tokens can range from syntax error tokens to
 * actual information tokens like help and so on.
 * 
 * @author Stefan Rybacki
 * @see SyntaxEditor#addInfoProvider(IInfoProvider)
 */
public interface IInfoProvider {

  /**
   * Gets the information tokens.
   * <p/>
   * Must be sorted ascending by {@link ILexerToken#getStart()}. Use
   * {@link java.util.Collections#sort(List)} to sort if you use
   * {@link DefaultLexerToken} descendants.
   * 
   * @param index
   *          the token's index
   * @return the information tokens
   * @see #getTokenCount()
   */
  ILexerToken getToken(int index);

  /**
   * Gets the token count.
   * 
   * @return the token count
   * @see #getToken(int)
   */
  int getTokenCount();

  /**
   * Gets the stylizer for the tokens provided by {@link #getToken(int)} .
   * 
   * @return the stylizer for the tokens provided
   */
  ILexerTokenStylizer<ILexerToken> getStylizer();

  /**
   * Adds an information provider listener.
   * 
   * @param l
   *          the listener to add
   */
  void addInfoProviderListener(IInfoProviderListener l);

  /**
   * Removes a previously registered information provider listener.
   * 
   * @param l
   *          the listener to remove
   */
  void removeInfoProviderListener(IInfoProviderListener l);

  /**
   * Called if the content the provider was created for changed. This should
   * fire a checking of existing information tokens and the creation of new
   * information tokens as needed. This can be handled asynchronously by using
   * the {@link IInfoProviderListener}s.
   * 
   * @param content
   *          the content that changed
   * @param cursorPos
   *          the current cursor position
   */
  void contentChanged(Reader content, int cursorPos);

  /**
   * Called if the cursor/caret position within the content changes. This allows
   * to cursor/caret specific information tokens, like highlighting all
   * occurences of the word in a document the cursor/caret is pointing to.
   * 
   * @param newPos
   *          the new cursor position
   * @param content
   *          the content
   */
  void cursorPosChanged(int newPos, Reader content);

  // void contentSaved(Reader content);
  // ...

  /**
   * Determines whether the given token is just a token that is used for extra
   * styling the related content. Tokens that are style only should not appear
   * in info token tables or anywhere else but only be painted by the syntax
   * editor using the connected style. A use case might be cursor sensitive
   * tokens that are depended on cursor position e.g., tokens could be added
   * that highlight a specific word or phrase the cursor is currently in.
   * 
   * @param token
   *          the token
   * @return true, if is only style token
   */
  boolean isOnlyStyleToken(ILexerToken token);

  /**
   * Gets the actions for the specified token. This can be fixes, e.g. the
   * provider might implement a spell checker and recognizes an unknown word.
   * One action could then be to replace that word by a know one for instance
   * chosen by the user or by adding the unknown word to the dictionary.
   * 
   * @param token
   *          the token
   * @return the actions for token
   */
  List<ITokenAction> getActionsForToken(ILexerToken token);

  /**
   * Blocks the current thread until the last content change is parsed and the
   * resulting info tokens are present
   */
  void waitForParsingResult();
}
