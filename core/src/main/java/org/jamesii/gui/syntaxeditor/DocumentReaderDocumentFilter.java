/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Helper class for {@link DocumentReader} in combination with
 * {@link AbstractDocument}s where one can set {@link DocumentFilter}s which are
 * notified about changes to a document before they happened in contrast to
 * {@link DocumentListener}s that are notified after the change happened.
 * 
 * @author Stefan Rybacki
 * 
 */
class DocumentReaderDocumentFilter extends DocumentFilter {
  /**
   * original filter
   */
  private DocumentFilter originalFilter;

  /**
   * listener
   */
  private IDocumentFilterListener listener;

  /**
   * Creates a new instance of a document filter that accepts an
   * {@link IDocumentFilterListener} for event forwarding plus a
   * {@link DocumentFilter} for event forwarding as well.
   * 
   * @param original
   *          the document filter to forward events to, normally the one
   *          previously set in the document
   * @param listener
   *          a listener for event forwarding
   */
  public DocumentReaderDocumentFilter(DocumentFilter original,
      IDocumentFilterListener listener) {
    originalFilter = original;
    this.listener = listener;
  }

  /**
   * @return the original filter
   */
  public DocumentFilter getOriginalFilter() {
    return originalFilter;
  }

  @Override
  public void insertString(FilterBypass fb, int offset, String string,
      AttributeSet attr) throws BadLocationException {
    if (listener != null) {
      listener.insertString(offset, string);
    }

    if (originalFilter == null) {
      super.insertString(fb, offset, string, attr);
    } else {
      originalFilter.insertString(fb, offset, string, attr);
    }
  }

  @Override
  public void replace(FilterBypass fb, int offset, int length, String text,
      AttributeSet attrs) throws BadLocationException {
    if (listener != null) {
      listener.replace(offset, length, text,
          fb.getDocument().getText(offset, length));
    }

    if (originalFilter == null) {
      super.replace(fb, offset, length, text, attrs);
    } else {
      originalFilter.replace(fb, offset, length, text, attrs);
    }
  }

  @Override
  public void remove(FilterBypass fb, int offset, int length)
      throws BadLocationException {
    if (listener != null) {
      listener.remove(offset, length, fb.getDocument().getText(offset, length));
    }

    if (originalFilter == null) {
      super.remove(fb, offset, length);
    } else {
      originalFilter.remove(fb, offset, length);
    }
  }

}
