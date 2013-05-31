/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import org.jamesii.ChattyTestCase;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * @author Stefan Rybacki
 */
public class BasicUtilitiesTest extends ChattyTestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.utils.BasicUtilities#makeCamelCaseReadable(java.lang.String)}
   * .
   */
  public final void testMakeCamelCaseReadable() {
    String test = "ThisIsACamelCaseTextThatShouldBeMadeReadable";
    assertEquals("This Is A Camel Case Text That Should Be Made Readable",
        BasicUtilities.makeCamelCaseReadable(test));
    test =
        "ThisIsAnACRONYMWithinThisCamelCaseAndIAmSureThisIsAWorkingVersionAsWell";
    assertEquals(
        "This Is An ACRONYM Within This Camel Case And I Am Sure This Is A Working Version As Well",
        BasicUtilities.makeCamelCaseReadable(test));
  }

}
