/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.Hook;

import junit.framework.TestCase;

/**
 * The Class TestHook.
 * 
 * @author Jan Himmelspach
 */
public class TestHook extends TestCase {

  /** The exec. */
  List<String> exec = new ArrayList<>();

  /**
   * Test hook.
   */
  public void testHook() {
    MyHook h = new MyHook(this);
    assertTrue(h != null);
  }

  /**
   * Test execute.
   */
  public void testExecute() {
    MyHook h = new MyHook(this);
    h.execute("testit");
    assertTrue(exec.get(0).compareTo("testit") == 0);
    assertTrue(exec.size() == 1);
    exec.clear();
    h.execute("testit2");
    assertTrue(exec.get(0).compareTo("testit2") == 0);
    assertTrue(exec.size() == 1);
    exec.clear();
    MyHook h2 = new MyHook(this, h);
    h2.execute("testit3");
    assertTrue(exec.get(0).compareTo("testit3") == 0);
    assertTrue(exec.get(1).compareTo("testit3") == 0);
    assertTrue(exec.size() == 2);
    exec.clear();
    h2.execute("testit4");
    assertTrue(exec.get(0).compareTo("testit4") == 0);
    assertTrue(exec.get(1).compareTo("testit4") == 0);
    assertTrue(exec.size() == 2);
    exec.clear();
    h.execute("testit5");
    assertTrue(exec.get(0).compareTo("testit5") == 0);
    assertTrue(exec.size() == 1);
  }

  /**
   * Test get old hook.
   */
  public void testGetOldHook() {
    MyHook h = new MyHook(this);
    assertTrue(h.getOldHook() == null);
    MyHook h2 = new MyHook(this, h);
    assertTrue(h.getOldHook() == null);
    assertTrue(h2.getOldHook() == h);
    MyHook h3 = new MyHook(this, h2);
    assertTrue(h.getOldHook() == null);
    assertTrue(h2.getOldHook() == h);
    assertTrue(h3.getOldHook() == h2);
    assertTrue(h3.getOldHook().getOldHook() == h);
    assertTrue(h3.getOldHook().getOldHook().getOldHook() == null);
  }

  /**
   * The Class MyHook.
   */
  class MyHook extends Hook<String> {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7012631449090900921L;

    /** The t. */
    TestHook t;

    /**
     * Instantiates a new my hook.
     * 
     * @param t
     *          the t
     */
    public MyHook(TestHook t) {
      super();
      this.t = t;
    }

    /**
     * Instantiates a new my hook.
     * 
     * @param t
     *          the t
     * @param oldHook
     *          the old hook
     */
    public MyHook(TestHook t, MyHook oldHook) {
      super(oldHook);
      this.t = t;
    }

    @Override
    protected void executeHook(String information) {
      t.exec.add(information);
    }

  }

}
