/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

/**
 * Listener interface used by the {@link DocumentReaderDocumentFilter} to
 * forward events of the {@link javax.swing.text.DocumentFilter} to the
 * {@link DocumentReader}. This is used to intercept changes to an
 * {@link javax.swing.text.AbstractDocument} before they happen to ensure
 * consistency within the {@link DocumentReader}.
 * 
 * @author Stefan Rybacki
 * 
 */
interface IDocumentFilterListener {
  /**
   * Called by {@link DocumentReaderDocumentFilter} whenever a string is to be
   * inserted into the {@link javax.swing.text.Document} the
   * {@link DocumentReaderDocumentFilter} is assigned to.
   * 
   * @param offset
   *          the offset where the given string is to be inserted
   * @param string
   *          the string to be inserted
   */
  void insertString(int offset, String string);

  /**
   * Called by {@link DocumentReaderDocumentFilter} whenever a string is to be
   * replaced within the {@link Document} the
   * {@link DocumentReaderDocumentFilter} is assigned to.
   * 
   * @param offset
   *          the offset where the replace takes place
   * @param length
   *          the length of the string to replace
   * @param newText
   *          the string that replaces the old string
   * @param oldText
   *          the string that is being replaced
   */
  void replace(int offset, int length, String newText, String oldText);

  /**
   * Called by {@link DocumentReaderDocumentFilter} whenever a string is to be
   * removed from the {@link Document} the {@link DocumentReaderDocumentFilter}
   * is assigned to.
   * 
   * @param offset
   *          the offset where the string is being removed
   * @param length
   *          the length of the string to remove
   * @param removedText
   *          the text that to be removed
   */
  void remove(int offset, int length, String removedText);
}
