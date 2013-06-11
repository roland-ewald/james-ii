/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.base;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;

import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.gui.syntaxeditor.DocumentReader;
import org.jamesii.gui.syntaxeditor.PasstroughTokenAction;

/**
 * @author Stefan Rybacki
 */
public class PasstroughTokenActionTest extends ChattyTestCase {

  private Random random;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    long seed = System.nanoTime() * System.currentTimeMillis();
    this.addParameter("seed", seed);
    random = new Random(seed);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.syntaxeditor.PasstroughTokenAction#run(java.io.Reader, java.io.Writer)}
   * .
   * 
   * @throws BadLocationException
   */
  public final void testRun() throws BadLocationException {

    String original =
        Strings.generateRandomString(random.nextInt(10000), random);

    PasstroughTokenAction action = new PasstroughTokenAction(null);

    assertNotNull(action);

    Reader input = new StringReader(original);
    StringWriter output = new StringWriter();

    action.run(input, output);

    assertEquals(original, output.toString());

    PlainDocument doc = new PlainDocument();
    doc.insertString(0, original, null);

    input = new DocumentReader(doc);
    output = new StringWriter();

    action.run(input, output);

    assertEquals(original, output.toString());
  }

}
