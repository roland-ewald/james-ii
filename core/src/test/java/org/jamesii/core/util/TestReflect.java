package org.jamesii.core.util;

import java.lang.reflect.Method;

import org.jamesii.core.util.Reflect;

import junit.framework.TestCase;

/**
 * The Class TestReflect.
 * 
 * @author Jan Himmelspach
 */
public class TestReflect extends TestCase {

  /**
   * Test execute method object string object array.
   */
  public void testExecuteMethodObjectStringObjectArray() {

    TestClass test = new TestClass();

    Reflect.executeMethod(test, "test1", null);
    assertTrue(test.s.compareTo("test1") == 0);
    Reflect.executeMethod(test, "test2", null);
    assertTrue(test.s.compareTo("test2") == 0);
    Reflect.executeMethod(test, "test11", new Object[] { new Object() });
    assertTrue(test.s.compareTo("test11") == 0);
    Reflect.executeMethod(test, "test21", new Object[] { 1 });
    assertTrue(test.s.compareTo("test21i") == 0);
    Reflect.executeMethod(test, "test21", new Object[] { 1. });
    assertTrue(test.s.compareTo("test21d") == 0);

    assertTrue(1 == (Integer) Reflect.executeMethod(test, "test21",
        new Object[] { 1 }));
    assertTrue(456347 == (Integer) Reflect.executeMethod(test, "test21",
        new Object[] { 456347 }));

    assertEquals(1., Reflect.executeMethod(test, "test21", new Object[] { 1. }));
    assertEquals(45.6347,
        Reflect.executeMethod(test, "test21", new Object[] { 45.6347 }));

  }

  /**
   * Test execute method object string class of q array object array.
   */
  public void testExecuteMethodObjectStringClassOfQArrayObjectArray() {
    TestClass test = new TestClass();

    Reflect.executeMethod(test, "test1", null, null);
    assertTrue(test.s.compareTo("test1") == 0);
    Reflect.executeMethod(test, "test2", null, null);
    assertTrue(test.s.compareTo("test2") == 0);
    Reflect.executeMethod(test, "test11", new Class<?>[] { Object.class },
        new Object[] { new Object() });
    assertTrue(test.s.compareTo("test11") == 0);
    Reflect.executeMethod(test, "test21", new Class<?>[] { Integer.class },
        new Object[] { 1 });
    assertTrue(test.s.compareTo("test21i") == 0);
    Reflect.executeMethod(test, "test21", new Class<?>[] { Double.class },
        new Object[] { 1. });
    assertTrue(test.s.compareTo("test21d") == 0);

    assertTrue(1 == (Integer) Reflect.executeMethod(test, "test21",
        new Class<?>[] { Integer.class }, new Object[] { 1 }));
    assertTrue(456347 == (Integer) Reflect.executeMethod(test, "test21",
        new Class<?>[] { Integer.class }, new Object[] { 456347 }));

    assertEquals(1., Reflect.executeMethod(test, "test21",
        new Class<?>[] { Double.class }, new Object[] { 1. }));
    assertEquals(45.6347, Reflect.executeMethod(test, "test21",
        new Class<?>[] { Double.class }, new Object[] { 45.6347 }));
  }

  /**
   * Test execute method object method object array.
   */
  public void testExecuteMethodObjectMethodObjectArray() {
    TestClass test = new TestClass();

    try {
      Method m1 = TestClass.class.getMethod("test1", new Class<?>[] {});
      Reflect.executeMethod(test, m1, new Object[] {});
      assertTrue(test.s.compareTo("test1") == 0);
    } catch (Exception e) {
      fail("Reflection and method problem");
    }

  }

  /**
   * The Class TestClass.
   */
  class TestClass {

    /** The s. */
    String s;

    /**
     * Instantiates a new test class.
     */
    public TestClass() {

    }

    /**
     * Test1.
     */
    public void test1() {
      s = "test1";
    }

    /**
     * Test2.
     * 
     * @return the int
     */
    public int test2() {
      s = "test2";
      return -4;
    }

    /**
     * Test11.
     * 
     * @param o
     *          the o
     */
    public void test11(Object o) {
      s = "test11";
    }

    /**
     * Test21.
     * 
     * @param i
     *          the i
     * 
     * @return the int
     */
    public int test21(Integer i) {
      s = "test21i";
      return i;
    }

    /**
     * Test21.
     * 
     * @param i
     *          the i
     * 
     * @return the double
     */
    public double test21(Double i) {
      s = "test21d";
      return i;
    }

  }

}
