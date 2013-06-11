/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.base;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.gui.syntaxeditor.InsertTokenAction;

/**
 * @author Stefan Rybacki
 * 
 */
public class InsertTokenActionTest extends ChattyTestCase {

  private Random random;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    long seed = System.nanoTime() * System.currentTimeMillis();
    this.addParameter("seed", seed);
    random = new Random(seed);
  }

  public final void testRun() {

    String original =
        Strings.generateRandomString(random.nextInt(10000) + 25, random);

    InsertTokenAction action = new InsertTokenAction(null, 0, "HelloWorld!");

    assertNotNull(action);

    Reader input = new StringReader(original);
    StringWriter output = new StringWriter();

    action.run(input, output);

    assertEquals("HelloWorld!" + original, output.toString());

    action = new InsertTokenAction(null, 10, "HelloWorld!");

    assertNotNull(action);

    input = new StringReader(original);
    output = new StringWriter();

    action.run(input, output);

    assertEquals(
        original.substring(0, 10) + "HelloWorld!" + original.substring(10),
        output.toString());

  }
}
