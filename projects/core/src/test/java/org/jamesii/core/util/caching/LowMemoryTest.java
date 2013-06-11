/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.caching;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class LowMemoryTest extends TestCase {

  boolean breakLoop = false;

  List<Object[]> test = new ArrayList<>();

  public void test() {
    MemoryObserver mo = MemoryObserver.INSTANCE;

    ILowMemoryListener testListener = new MyCache();

    mo.register(testListener);

    try {
      while (!breakLoop) {
        if (Math.random() < .25) {
          // generate some work for the garbage collector
          if (test.size() > 0) {
            test.remove(0);
          }
        }
        test.add(new Object[10]);
      }

    } catch (OutOfMemoryError error) {
      if (!breakLoop) {
        fail();
      }
    }

    mo.unregister(testListener);
  }

  private class MyCache implements ILowMemoryListener {

    @Override
    public synchronized void lowMemory() {
      breakLoop = true;
    }

  }

}
