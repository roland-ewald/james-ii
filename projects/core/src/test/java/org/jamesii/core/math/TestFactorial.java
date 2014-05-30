/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import org.jamesii.core.math.Factorial;

import junit.framework.TestCase;

/**
 * Tests the {@link Factorial} class.
 * 
 * @author Johannes Rössel
 */
public class TestFactorial extends TestCase {

  /**
   * Test quick fac.
   */
  public void testQuickFac() {
    assertEquals(1., Factorial.quickFac(0));
    assertEquals(Factorial.quickFac(1), 1.);
    assertEquals(Factorial.quickFac(2), 2.);
    assertEquals(Factorial.quickFac(3), 6.);
    assertEquals(Factorial.quickFac(4), 24.);
    assertEquals(Factorial.quickFac(5), 120.);
    assertEquals(Factorial.quickFac(6), 720.);
    assertEquals(Factorial.quickFac(7), 5040.);
    assertEquals(Factorial.quickFac(8), 40320.);
    assertEquals(Factorial.quickFac(10), 3628800d);

    assertEquals(Factorial.quickFac(12), 479001600d);
    assertEquals(Factorial.quickFac(16), 20922789888000d);
    assertEquals(2432902008176640000d, Factorial.quickFac(20));
    assertEquals(15511210043330985984000000d, Factorial.quickFac(25));
    assertEquals(403291461126605635584000000d, Factorial.quickFac(26));

    assertEquals(3.0414093201713378043612608166065e+64d, Factorial.quickFac(50));
    // assertEquals(9.3326215443944152681699238856267e+157d,
    // Factorial.quickFac(100));

    // StopWatch sw = new StopWatch();
    //
    // sw.start();
    // double r = 0.;
    // for (int i = 0; i < 30; i++) {
    // for (int j = 1; j < 1000000; j ++) {
    // r += Factorial.quickFac(i);
    // }
    // }
    // sw.stop();
    // System.out.println("time (quick fac) "+sw.elapsedMilliseconds());
    // System.out.println("val (quick fac) "+r);
    //
    // sw = new StopWatch();
    //
    // sw.start();
    // r = 0.;
    // for (int i = 0; i < 30; i++) {
    // for (int j = 1; j < 1000000; j ++) {
    // r += Factorial.facD(i);
    // }
    // }
    // sw.stop();
    // System.out.println("time (normal fac) "+sw.elapsedMilliseconds());
    // System.out.println("val (fac) "+r);

  }

  /** Tests {@link Factorial#facD(int)}. */
  public void testFacIntD() {
    assertEquals(Factorial.facD(0), 1d);
    assertEquals(Factorial.facD(1), 1d);
    assertEquals(Factorial.facD(2), 2d);
    assertEquals(Factorial.facD(3), 6d);
    assertEquals(Factorial.facD(4), 24d);
    assertEquals(Factorial.facD(5), 120d);
    assertEquals(Factorial.facD(6), 720d);
    assertEquals(Factorial.facD(7), 5040d);
    assertEquals(Factorial.facD(8), 40320d);

    assertEquals(3.0414093201713378043612608166065e+64d, Factorial.facD(50));
    // assertEquals(9.3326215443944152681699238856267e+157d,
    // Factorial.facD(100));

  }

  /** Tests {@link Factorial#fac(int)}. */
  public void testFacInt() {
    assertEquals(Factorial.fac(0), 1);
    assertEquals(Factorial.fac(1), 1);
    assertEquals(Factorial.fac(2), 2);
    assertEquals(Factorial.fac(3), 6);
    assertEquals(Factorial.fac(4), 24);
    assertEquals(Factorial.fac(5), 120);
    assertEquals(Factorial.fac(6), 720);
    assertEquals(Factorial.fac(7), 5040);
    assertEquals(Factorial.fac(8), 40320);
  }

  /** Tests the {@link Factorial#fac(int, int)} method. */
  public void testFacIntInt() {
    assertEquals(Factorial.fac(0, 0), 1);
    for (int i = 1; i < 50; i++) {
      assertEquals(Factorial.fac(i, i), 1);
      assertEquals(Factorial.fac(i, 1), Factorial.fac(i));
      assertEquals(Factorial.fac(i, i - 1), i);
    }
    assertEquals(Factorial.fac(5, 3), 20);
    assertEquals(Factorial.fac(8, 5), 336);
    assertEquals(Factorial.fac(8, 4), 1680);
    assertEquals(Factorial.fac(20, 15), 1860480);

    try {
      Factorial.fac(1, 2);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

}
