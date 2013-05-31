/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.serialization;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.Reflect;

/**
 * @author Stefan Rybacki
 * 
 */
public class ReflectTest extends ChattyTestCase {

  /**
   * The Class TestClass.
   */
  private static class TestClass {

    /**
     * The Constant pMsg.
     */
    public static final String pMsg = "Private Constructor Called!";

    /**
     * The msg.
     */
    public String msg = null;

    /**
     * Instantiates a new test class.
     */
    private TestClass() {
      msg = pMsg;
    }

    /**
     * Instantiates a new test class.
     * 
     * @param m
     *          the m
     */
    private TestClass(String m) {
      msg = m;
    }

    /**
     * Instantiates a new test class.
     * 
     * @param m
     *          the m
     * @param x
     *          the x
     */
    private TestClass(String m, Number x) {
      msg = m + x.toString();
    }
  }

  /**
   * The o.
   */
  private Object o;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    o = new String("Hello World");
  }

  /**
   * Test set field.
   */
  public void testSetField() {
    assertEquals("Hello World", o);

    Reflect.setField(o, "value", "World Hello".toCharArray());

    assertEquals("World Hello", o);
  }

  /**
   * Test instantiate.
   */
  public void testInstantiate() {
    try {
      TestClass t = Reflect.instantiate(TestClass.class);
      assertNotNull(t);
      assertEquals(TestClass.pMsg, t.msg);

      t = Reflect.instantiate(TestClass.class, "Hello World");
      assertNotNull(t);
      assertEquals("Hello World", t.msg);

      Double d = Double.valueOf(10000.0);

      t = Reflect.instantiate(TestClass.class, "Hello World", d);
      assertNotNull(t);
      assertEquals("Hello World" + d.toString(), t.msg);

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
