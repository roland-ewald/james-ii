/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.execonfig;

import org.jamesii.core.experiments.execonfig.BasicParamBlockUpdate;
import org.jamesii.core.experiments.execonfig.OverallParamBlockUpdate;
import org.jamesii.core.experiments.execonfig.SingularParamBlockUpdate;
import org.jamesii.core.parameters.ParameterBlock;

import junit.framework.TestCase;

/**
 * Test for implementations of {@link BasicParamBlockUpdate}.
 * 
 * @author Roland Ewald
 */
public class TestSimpleParamBlockUpdates extends TestCase {

  /** Hypothetical path to abstract factory. */
  public static final String PATH_ABSTRACT_FACTORY = "path.to.abstract.Factory";

  /** Hypothetical path to concrete factory. */
  public static final String PATH_CONCRETE_FACTORY = "path.to.concrete.Factory";

  /** Hypothetical name of a parameter. */
  public static final String NAME_PARAMETER = "a parameter";

  /**
   * Tests {@link SingularParamBlockUpdate}.
   */
  public void testSingularParamBlockUpdate() {

    ParameterBlock paramBlock = new ParameterBlock(PATH_CONCRETE_FACTORY);
    paramBlock.addSubBlock("facParam", 23);

    SingularParamBlockUpdate update =
        new SingularParamBlockUpdate(new String[] { "just", "a", "test" },
            PATH_ABSTRACT_FACTORY, paramBlock);

    ParameterBlock testBlock = new ParameterBlock();

    update.update(testBlock);

    assertTrue(testBlock.hasSubBlock("just"));
    ParameterBlock justBlock = testBlock.getSubBlock("just");
    assertTrue(justBlock.hasSubBlock("a"));
    ParameterBlock aBlock = justBlock.getSubBlock("a");
    assertTrue(aBlock.hasSubBlock("test"));
    ParameterBlock testParamBlock = aBlock.getSubBlock("test");
    assertTrue(paramBlock.compareTo(testParamBlock
        .getSubBlock(PATH_ABSTRACT_FACTORY)) == 0);
  }

  /**
   * Tests {@link OverallParamBlockUpdate}.
   */
  public void testOverallParamBlockUpdate() {

    // Small tree with to occurrences of abstract factory definition
    ParameterBlock testBlock = new ParameterBlock("");
    Object o = new Object();
    ParameterBlock afBlock1 =
        testBlock.addSubBlock("x", o).addSubBlock("y", o)
            .addSubBlock(PATH_ABSTRACT_FACTORY, o);
    ParameterBlock afBlock2 =
        testBlock.addSubBlock("a", o).addSubBlock(PATH_ABSTRACT_FACTORY, o);
    afBlock2.addSubBlock(NAME_PARAMETER, o).addSubBlock("b", o);

    Integer newParam = new Integer(23);
    OverallParamBlockUpdate update =
        new OverallParamBlockUpdate(PATH_ABSTRACT_FACTORY, NAME_PARAMETER,
            newParam);

    update.update(testBlock);

    assertTrue(afBlock1.hasSubBlock(NAME_PARAMETER));
    assertTrue(afBlock2.hasSubBlock(NAME_PARAMETER));
    assertEquals(23, (Number)afBlock1.getSubBlockValue(NAME_PARAMETER));
    assertEquals(23, (Number)afBlock2.getSubBlockValue(NAME_PARAMETER));
    assertFalse(newParam == afBlock1.getSubBlockValue(NAME_PARAMETER));
    assertFalse(newParam == afBlock2.getSubBlockValue(NAME_PARAMETER));
    assertTrue(afBlock2.getSubBlock(NAME_PARAMETER).hasSubBlock("b"));
  }
}
