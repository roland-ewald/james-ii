/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Random;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.gui.syntaxeditor.DocumentReader;
import org.jamesii.gui.syntaxeditor.ReaderCharSequence;

/**
 * @author Stefan Rybacki
 * 
 */
public class DocumentReaderTest extends ChattyTestCase {

  /**
   * The generator used to reproduce results if needed.
   */
  private Random generator;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    long seed = System.nanoTime();
    addParameter("seed", seed);
    generator = new Random(seed);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.syntaxeditor.DocumentReader#DocumentReader(javax.swing.text.Document)}
   * .
   */
  public final void testDocumentReader() {
    // test 100 random start strings
    for (int j = 0; j < 1000; j++) {
      String testString =
          Strings.generateRandomString(generator.nextInt(10000) + 1000,
              generator);
      Document doc = new PlainDocument();
      try {
        doc.remove(0, doc.getLength());
        doc.insertString(0, testString, null);
        DocumentReader reader = new DocumentReader(doc);
        ReaderCharSequence rcs = new ReaderCharSequence(reader);
        reader.close();

        assertTrue(testString.contentEquals(rcs));

        reader = new DocumentReader(doc);
        StringWriter writer = new StringWriter();

        int read;
        while ((read = reader.read()) >= 0) {
          writer.write(read);
        }
        writer.flush();

        assertEquals(testString, writer.toString());

      } catch (Exception e) {
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test method to test whether changes to document while reading affect reader
   * output (it should NOT!)
   * 
   * Tests with a non AbstractDocument document so that the Reader uses a local
   * copy of the document content to provide consistency during reading. (This
   * is the memory unfriendly version)
   */
  public final void testConsistencyNotAbstractDocument() {
    // test documents that are not subclassing AbstractDocument
    for (int j = 0; j < 1000; j++) {
      String testString =
          Strings.generateRandomString(generator.nextInt(10000) + 1000,
              generator);
      Document doc = new Document() {
        /**
         * The document to use.
         */
        private final PlainDocument d = new PlainDocument();

        @Override
        public void addDocumentListener(DocumentListener listener) {
          d.addDocumentListener(listener);
        }

        @Override
        public void addUndoableEditListener(UndoableEditListener listener) {
          d.addUndoableEditListener(listener);
        }

        @Override
        public Position createPosition(int offs) throws BadLocationException {
          return d.createPosition(offs);
        }

        @Override
        public Element getDefaultRootElement() {
          return d.getDefaultRootElement();
        }

        @Override
        public Position getEndPosition() {
          return d.getEndPosition();
        }

        @Override
        public int getLength() {
          return d.getLength();
        }

        @Override
        public Object getProperty(Object key) {
          return d.getProperty(key);
        }

        @Override
        public Element[] getRootElements() {
          return d.getRootElements();
        }

        @Override
        public Position getStartPosition() {
          return d.getStartPosition();
        }

        @Override
        public String getText(int offset, int length)
            throws BadLocationException {
          return d.getText(offset, length);
        }

        @Override
        public void getText(int offset, int length, Segment txt)
            throws BadLocationException {
          d.getText(offset, length, txt);
        }

        @Override
        public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException {
          d.insertString(offset, str, a);
        }

        @Override
        public void putProperty(Object key, Object value) {
          d.putProperty(key, value);
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
          d.remove(offs, len);
        }

        @Override
        public void removeDocumentListener(DocumentListener listener) {
          d.removeDocumentListener(listener);
        }

        @Override
        public void removeUndoableEditListener(UndoableEditListener listener) {
          d.removeUndoableEditListener(listener);
        }

        @Override
        public void render(Runnable r) {
          d.render(r);
        }

      };
      try {
        doc.remove(0, doc.getLength());
        doc.insertString(0, testString, null);
        ReaderCharSequence rcs;
        try (DocumentReader reader = new DocumentReader(doc)) {
          for (int i = 0; i < 10; i++) {
            int r = (generator.nextInt(doc.getLength()));
            int l = (generator.nextInt(doc.getLength() - r));
            if (i % 2 == 0) {
              doc.remove(r, l);
            } else {
              String s =
                  Strings.generateRandomString(generator.nextInt(100),
                      generator);
              doc.insertString(r, s, null);
            }
          }
          rcs = new ReaderCharSequence(reader);
        }

        // the document reader should ignore changes made to document and still
        // return original testString
        assertTrue(
            "Expecting identical contents even though the document was changed during reading.",
            testString.contentEquals(rcs));
      } catch (BadLocationException | IOException e) {
        e.printStackTrace();
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test method to test whether changes to document while reading affect reader
   * output (it should NOT!)
   * 
   * Tests with an AbstractDocument document so that no local copy of the
   * document's content is created until any change to the document occurs which
   * would be a memory friendly version. This test also does not change the
   * document while reading but right before reading after the Reader was
   * defined.
   */
  public final void testConsistencyAbstractDocument() {
    // test 1000 random start strings
    for (int j = 0; j < 1000; j++) {
      String testString =
          Strings.generateRandomString(generator.nextInt(10000) + 1000,
              generator);
      Document doc = new PlainDocument();
      try {
        doc.remove(0, doc.getLength());
        doc.insertString(0, testString, null);
        ReaderCharSequence rcs;
        try (DocumentReader reader = new DocumentReader(doc)) {
          for (int i = 0; i < 10; i++) {
            int r = generator.nextInt(doc.getLength());
            int l = generator.nextInt((doc.getLength() - r));
            if (i % 2 == 0) {
              doc.remove(r, l);
            } else {
              String s =
                  Strings.generateRandomString(generator.nextInt(100),
                      generator);
              doc.insertString(r, s, null);
            }
          }
          rcs = new ReaderCharSequence(reader);
        }

        // the document reader should ignore changes made to document and still
        // return original testString
        assertTrue(testString.contentEquals(rcs));
      } catch (BadLocationException | IOException e) {
        e.printStackTrace();
        fail(e.getMessage());
      }
    }

  }

  /**
   * Test method to test whether changes to document while reading affect reader
   * output (it should NOT!)
   * 
   * Tests the memory friendly version of consistency of DocumentReader using an
   * AbstractDocument and the document is now changed while reading instead of
   * right before.
   */
  public final void testConsistencyAbstractDocumentOTF() {
    // test 1000 random start strings
    for (int j = 0; j < 1000; j++) {
      String testString =
          Strings.generateRandomString(generator.nextInt(10000) + 1000,
              generator);
      Document doc = new PlainDocument();
      try {
        doc.remove(0, doc.getLength());
        doc.insertString(0, testString, null);
        StringBuilder result;
        try (DocumentReader reader = new DocumentReader(doc)) {
          result = new StringBuilder(testString.length());
          {
            int l = (int) (testString.length() * generator.nextDouble() / 2f);
            char[] cbuf = new char[l];
            int read = reader.read(cbuf, 0, cbuf.length);
            if (read > 0) {
              result.append(cbuf, 0, read);
            }
          }
          for (int i = 0; i < 10; i++) {
            int r = (generator.nextInt(doc.getLength()));
            int l = (generator.nextInt(doc.getLength() - r));
            if (i % 2 == 0) {
              doc.remove(r, l);
            } else {
              String s =
                  Strings.generateRandomString(generator.nextInt(100),
                      generator);
              doc.insertString(r, s, null);
            }
          }
          while (true) {
            char[] cbuf = new char[1024];
            int read = reader.read(cbuf, 0, cbuf.length);
            if (read < 0) {
              break;
            }
            result.append(cbuf, 0, read);
          }
        }

        // the document reader should ignore changes made to document and still
        // return original testString
        assertTrue(testString.contentEquals(result.toString()));
      } catch (BadLocationException | IOException e) {
        e.printStackTrace();
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test method to test whether changes to document while reading affect reader
   * output (it should NOT!)
   * 
   * Tests the memory friendly version of consistency of DocumentReader using an
   * AbstractDocument and the document is now changed before and while reading
   * instead of only right before.
   */
  public final void testConsistencyAbstractDocumentOTF2() {
    // test 1000 random start strings
    for (int j = 0; j < 1000; j++) {
      String testString =
          Strings.generateRandomString(generator.nextInt(10000) + 1000,
              generator);
      Document doc = new PlainDocument();
      try {
        doc.remove(0, doc.getLength());
        doc.insertString(0, testString, null);
        StringBuilder result;
        try (DocumentReader reader = new DocumentReader(doc)) {
          result = new StringBuilder(testString.length());
          for (int i = 0; i < 10; i++) {
            int r = generator.nextInt(doc.getLength());
            int l = generator.nextInt(doc.getLength() - r);
            if (i % 2 == 0) {
              doc.remove(r, l);
            } else {
              String s =
                  Strings.generateRandomString(generator.nextInt(100),
                      generator);
              doc.insertString(r, s, null);
            }
          }
          {
            int l = (int) (testString.length() * generator.nextDouble() / 2f);
            char[] cbuf = new char[l];
            int read = reader.read(cbuf, 0, cbuf.length);
            if (read > 0) {
              result.append(cbuf, 0, read);
            }
          }
          for (int i = 0; i < 10; i++) {
            int r = generator.nextInt(doc.getLength());
            int l = generator.nextInt(doc.getLength() - r);
            if (i % 2 == 0) {
              doc.remove(r, l);
            } else {
              String s =
                  Strings.generateRandomString(generator.nextInt(100),
                      generator);
              doc.insertString(r, s, null);
            }
          }
          while (true) {
            char[] cbuf = new char[1024];
            int read = reader.read(cbuf, 0, cbuf.length);
            if (read < 0) {
              break;
            }
            result.append(cbuf, 0, read);
          }
        }

        // the document reader should ignore changes made to document and still
        // return original testString
        assertTrue(testString.contentEquals(result.toString()));
      } catch (BadLocationException | IOException e) {
        e.printStackTrace();
        fail(e.getMessage());
      }
    }
  }

}
