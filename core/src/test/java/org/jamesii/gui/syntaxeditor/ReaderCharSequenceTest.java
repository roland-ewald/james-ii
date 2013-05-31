/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.io.StringReader;
import java.util.Random;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.gui.syntaxeditor.ReaderCharSequence;

/**
 * @author Stefan Rybacki
 * 
 */
public class ReaderCharSequenceTest extends ChattyTestCase {

  /**
   * The generator for random numbers for reproducable results if needed.
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
   * {@link org.jamesii.gui.syntaxeditor.ReaderCharSequence#ReaderCharSequence(java.io.Reader)}
   * .
   */
  public final void testReaderCharSequence() {
    // check 1000 random strings
    for (int i = 0; i < 1000; i++) {
      String s =
          Strings.generateRandomString(generator.nextInt(10000), generator);
      ReaderCharSequence rcs;
      try (StringReader reader = new StringReader(s)) {
        rcs = new ReaderCharSequence(reader);
      }

      assertTrue(s.contentEquals(rcs));
    }
  }

}
