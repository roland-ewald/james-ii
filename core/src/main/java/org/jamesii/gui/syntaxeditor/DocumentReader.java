/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.io.IOException;
import java.io.Reader;
import java.text.CharacterIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Segment;

import org.jamesii.SimSystem;

/**
 * Implements a {@link Reader} using a {@link Document} as source. It provides
 * the option to actually track changes made to the document. This way this
 * reader stays consistent even though the document gets changed while reading
 * from it.
 * <p>
 * NOTE: Consistency in a memory friendly way can only be ensured if the
 * supplied {@link Document} is a sub class of {@link AbstractDocument} and
 * therefore supports {@link DocumentFilter}s. Nevertheless the current
 * implementation becomes memory unfriendly if any change occurs to the document
 * while reading because to ensure consistency a copy of the old content is
 * created if any change occurs.
 * 
 * @author Stefan Rybacki
 */
public class DocumentReader extends Reader implements IDocumentFilterListener {
  /**
   * flag indicating whether the reader is already closed
   */
  private volatile boolean closed = false;

  /**
   * the document to read from
   */
  private Document document;

  /**
   * segments from the document
   */
  private Deque<Segment> segments = new ArrayDeque<>();

  /**
   * flag indicating whether the end of the document is reached
   */
  private boolean done = false;

  /**
   * current segment that is read from
   */
  private Segment current = null;

  /**
   * {@link DocumentFilter} that is used if an {@link AbstractDocument} is
   * supplied to intercept change events to the document to ensure consistency
   */
  private DocumentReaderDocumentFilter documentFilter = null;

  /**
   * current position in document
   */
  private int currentPos = 0;

  /**
   * Creates a new {@link DocumentReader} for the given {@link Document}. Use a
   * document that subclasses {@link AbstractDocument} for best memory usage.
   * 
   * @param doc
   *          the document to read from
   */
  public DocumentReader(Document doc) {
    super();
    document = doc;
    // use memory friendly way of reader only if given document is an
    // AbstractDocument, else copy document content
    if (doc instanceof AbstractDocument) {
      DocumentFilter filter = ((AbstractDocument) doc).getDocumentFilter();
      if (filter instanceof DocumentReaderDocumentFilter) {
        copyContent();
      } else {
        documentFilter = new DocumentReaderDocumentFilter(filter, this);
        ((AbstractDocument) doc).setDocumentFilter(documentFilter);

        int offset = 0;
        int length = document.getLength();
        // get segments from document in "partialreturn" mode (this
        // means
        // it might be necessary to retrieve more than one segment
        // depending
        // on how many chars a segment can return without copying
        while (length > 0) {
          try {
            Segment text = new Segment();
            text.setPartialReturn(true);
            document.getText(offset, length, text);
            segments.add(text);

            length -= text.getEndIndex() - text.getBeginIndex();
            offset = text.getEndIndex();
          } catch (BadLocationException e) {
            throw new TextProcessingException(
                "Creating the document reader for the document " + doc
                    + " failed.", e);
          }
        }
      }
    } else {
      // copy content of document to ensure consistency
      copyContent();
    }
  }

  @Override
  public synchronized void close() throws IOException {
    // if not yet closed clean up and mark as closed
    if (!closed) {
      closed = true;
      segments.clear();
      segments = null;

      // try to restore original document filter if document was
      // AbstractDocument
      if (documentFilter != null && document instanceof AbstractDocument) {
        ((AbstractDocument) document).setDocumentFilter(documentFilter
            .getOriginalFilter());
      }
      document = null;
    }
  }

  /**
   * Helper method that reads the next character from the current segment or
   * subsequent segments if the current one has no more characters
   * 
   * @return the next character, {@link CharacterIterator#DONE} if there are no
   *         more characters
   */
  private char nextCharFromSegments() {
    // initialization
    if (current == null) {
      current = segments.remove();
      current.first();
    }

    char c = current.current();

    if (c == CharacterIterator.DONE) {
      if (!segments.isEmpty()) {
        current = segments.remove();
        c = current.first();
      } else {
        done = true;
        return CharacterIterator.DONE;
      }
    }

    current.next();
    currentPos++;
    return c;
  }

  @Override
  public synchronized int read(char[] cbuf, int off, int len)
      throws IOException {
    // check whether there is no more to return
    if (closed || done || (segments.isEmpty() && current == null)) {
      return -1;
    }

    int l = 0;
    for (int i = 0; i < len; i++) {
      char c = nextCharFromSegments();

      if (c == CharacterIterator.DONE) {
        // this was added due to a JDK bug/feature in Reader#read()
        // where the return value is not checked for 0 characters read
        if (l > 0) {
          return l;
        }
        return -1;
      }

      l++;
      cbuf[i + off] = c;
    }

    return l;
  }

  /**
   * Helper method to copy content.
   */
  private synchronized void copyContent() {
    if (closed) {
      return;
    }
    try {
      current = null;
      if (segments == null) {
        segments = new ArrayDeque<>();
      }
      segments.clear();
      String s = document.getText(0, document.getLength());
      Segment text = new Segment(s.toCharArray(), 0, s.length());
      segments.add(text);
      if (documentFilter != null) {
        ((AbstractDocument) document).setDocumentFilter(documentFilter
            .getOriginalFilter());
      }
      documentFilter = null;
    } catch (BadLocationException e) {
      SimSystem.report(Level.WARNING, "Copying the content did not work.", e);
    }

    int seek = currentPos;
    currentPos = 0;
    for (int i = 0; i < seek; i++) {
      nextCharFromSegments();
    }
  }

  @Override
  public synchronized void insertString(int offset, String string) {
    // if anything is changed create a copy of document content to
    // ensure
    // consistency
    copyContent();
  }

  @Override
  public synchronized void remove(int offset, int length, String removedText) {
    // if anything is changed create a copy of document content to
    // ensure
    // consistency
    copyContent();
  }

  @Override
  public synchronized void replace(int offset, int length, String newText,
      String oldText) {
    // if anything is changed create a copy of document content to
    // ensure
    // consistency
    copyContent();
  }

}
