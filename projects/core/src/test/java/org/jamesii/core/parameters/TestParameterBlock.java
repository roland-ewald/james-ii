/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.parameters;

import java.util.List;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

import junit.framework.TestCase;

//@formatter:off
/**
 * Simple unit test for {@link ParameterBlock}.
 * 
 * Structure of {@link TestParameterBlock#first}:
 * 
 * 1 | \ 2 3 | 7
 * 
 * Structure of {@link TestParameterBlock#second}:
 * 
 * 4 | \ 5 6 | 8
 * 
 * @author Roland Ewald
 */
// @formatter:on
public class TestParameterBlock extends TestCase {

  /** The Constant SB1. */
  static final String SB1 = "A";

  /** The Constant SSB1. */
  static final String SSB1 = "A2";

  /** The Constant SB2. */
  static final String SB2 = "B";

  /** The Constant SB3. */
  static final String SB3 = "C";

  /** A simple test parameter block. */
  ParameterBlock first;

  /** Another simple parameter block. */
  ParameterBlock second;

  @Override
  public void setUp() {
    first = new ParameterBlock(1);
    first.addSubBlock(SB1, 2).addSubBl(SSB1, 7);
    first.addSubBl(SB2, 3);
    second = new ParameterBlock(4);
    second.addSubBlock(SB1, 5).addSubBl(SSB1, 8);
    second.addSubBl(SB3, 6);
  }

  /**
   * Tests {@link ParameterBlocks#merge(ParameterBlock, ParameterBlock)}.
   */
  public void testMergeBlocks() {

    // Check Contents
    ParameterBlock result = ParameterBlocks.merge(first, second);
    assertEquals(1, (Number)result.getValue());
    assertEquals(3, result.getSubBlocks().size());
    assertTrue(result.hasSubBlock(SB1));
    assertEquals(2, (Number)result.getSubBlock(SB1).getValue());
    assertTrue(result.getSubBlock(SB1).hasSubBlock(SSB1));
    assertEquals(7, (Number)result.getSubBlock(SB1).getSubBlock(SSB1).getValue());
    assertTrue(result.hasSubBlock(SB2));
    assertEquals(3, (Number)result.getSubBlock(SB2).getValue());
    assertTrue(result.hasSubBlock(SB3));
    assertEquals(6, (Number)result.getSubBlock(SB3).getValue());

    // Check that there is no additional content
    assertEquals(1, result.getSubBlock(SB1).getSubBlocks().size());
    assertEquals(0, result.getSubBlock(SB1).getSubBlock(SSB1).getSubBlocks()
        .size());
    assertEquals(0, result.getSubBlock(SB2).getSubBlocks().size());
    assertEquals(0, result.getSubBlock(SB3).getSubBlocks().size());

    // Check that the value is set properly
    result = ParameterBlocks.merge(new ParameterBlock(), second);
    assertEquals(second.getSubBlocks().size(), result.getSubBlocks().size());
    assertEquals(second.getValue(), result.getValue());
  }

  /**
   * Tests {@link ParameterBlocks#searchSubBlock(ParameterBlock, String)} and
   * {@link ParameterBlocks#searchSubBlock(ParameterBlock, String, Object)}.
   */
  public void testSearchSubBlock() {
    assertNull(ParameterBlocks.searchSubBlock(first, "notaTestBlock"));
    assertNotNull(ParameterBlocks.searchSubBlock(first, SSB1));
    assertEquals(7, (Number)ParameterBlocks.searchSubBlock(first, SSB1).getValue());
    assertEquals(7, (Number)ParameterBlocks.searchSubBlock(first, SSB1, 7).getValue());
    assertNull(ParameterBlocks.searchSubBlock(first, SSB1, 8));
    assertEquals(8, (Number)ParameterBlocks.searchSubBlock(second, SSB1).getValue());
  }

  /**
   * Tests {@link ParameterBlocks#getValueList(ParameterBlock)} .
   */
  public void testValueListCreation() {
    List<Object> values1 = ParameterBlocks.getValueList(first);
    assertEquals(4, values1.size());
    assertTrue(values1.contains(1) && values1.contains(2)
        && values1.contains(3) && values1.contains(7));
    List<Object> values2 = ParameterBlocks.getValueList(second);
    assertEquals(4, values2.size());
    assertTrue(values2.contains(4) && values2.contains(5)
        && values2.contains(6) && values2.contains(8));
  }
}
