/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.vectors;

import java.util.Arrays;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.geometry.vectors.Vectors;

/**
 * Test vector utility methods
 *
 * @author Arne Bittig
 * @date 07.08.2012
 */
public class VectorsTest extends ChattyTestCase {

  private static final double[][] TEST_VECTORS_2D = new double[][] {
      { 0., 1. }, { 1., -2. }, { -4., 3. } };

  private static final double[][] TEST_VECTORS_3D = new double[][] {
      { 0., 1., 0. }, { 1., -2., 1. }, { 1., -2., -1. }, { 1.5, -4., 3. } };

  private static final double DELTA = 1e-12;

  private static final double ROTATE_TEST_ANGLE = 1.;

  private static boolean compareDoubleArraysWithFactorAndDelta(double[] arr1,
      double[] arr2, double factor, double delta) {
    if (arr1.length != arr2.length) {
      throw new IllegalArgumentException("Arrays are of different lengths");
    }
    for (int i = arr1.length - 1; i >= 0; i--) {
      if (Math.abs(arr1[i] - arr2[i] * factor) > delta) {
        return false;
      }
    }
    return true;
  }

  /**
   * Test {@link Vectors#cartesianToSpherical(double[])} and
   * {@link Vectors#sphericalToCartesian(double, double...)}
   */
  public void testPolarToCartesianAndBack2d() {
    for (double[] testVector : TEST_VECTORS_2D) {
      double[] p = Vectors.cartesianToSpherical(testVector);
      double[] c =
          Vectors
              .sphericalToCartesian(p[0], Arrays.copyOfRange(p, 1, p.length));
      assertTrue("Cartesian-to-spherial conversion and back "
          + "did not yield original input: " + Arrays.toString(testVector)
          + "->" + Arrays.toString(p) + "->" + Arrays.toString(c),
          compareDoubleArraysWithFactorAndDelta(testVector, c, 1., DELTA));
    }

  }

  /** Test {@link Vectors#getAngle(double[], double[])} with 2d vectors */
  public void testGetAngle2d() {
    double[] all1s2d = new double[] { 1., 1. };
    assertEquals(Math.PI / 4,
        Vectors.getAngle(new double[] { 0., 1. }, all1s2d), DELTA);
    assertEquals(3 * Math.PI / 4,
        Vectors.getAngle(all1s2d, new double[] { -2., 0. }), DELTA);

  }

  /**
   * Test {@link Vectors#rotate2d(double[], double)}
   */
  public void testRotate2d() {
    for (double[] testVector : TEST_VECTORS_2D) {
      double[] r = Vectors.rotate2d(testVector, ROTATE_TEST_ANGLE);
      double[] r2 = Vectors.rotate2d(testVector, ROTATE_TEST_ANGLE - Math.PI);
      assertTrue("Rotation by some vector and by 180° opposed vector "
          + "did not yield opposite results for " + Arrays.toString(testVector)
          + ": " + Arrays.toString(r) + " vs. " + Arrays.toString(r2),
          compareDoubleArraysWithFactorAndDelta(r, r2, -1., DELTA));
      double testVectorLength = Vectors.vecNormEuclid(testVector);
      assertEquals(testVectorLength, Vectors.vecNormEuclid(r), DELTA);
      assertEquals(testVectorLength, Vectors.vecNormEuclid(r2), DELTA);
      // SimSystem.report(Level.INFO,
      // "Rotated " + Arrays.toString(testVector) + " by "
      // + ROTATE_TEST_ANGLE + ": " + Arrays.toString(r));
      assertEquals(ROTATE_TEST_ANGLE, Vectors.getAngle(testVector, r), DELTA);
      assertEquals(Math.PI - ROTATE_TEST_ANGLE,
          Vectors.getAngle(testVector, r2), DELTA);
    }
  }

  /** Test {@link Vectors#getAngle(double[], double[])} with 3d vectors */
  public void testGetAngle3d() {
    assertEquals(
        Math.PI / 2,
        Vectors.getAngle(new double[] { 0., 1., 1. }, new double[] { 0., -1.,
            1. }), DELTA);
    final double dihedralAngle = 1.2309594173407747;
    assertEquals(
        dihedralAngle,
        Vectors.getAngle(new double[] { -1., -1., 1. }, new double[] { -1.,
            -1., -1. }), DELTA);
    // CHECK: some more, maybe?!
  }

  /**
   * Test {@link Vectors#rotate2d(double[], double)}
   */
  public void testRotate3dTowards() {
    final double[] xAxisPosDir = new double[] { 1., 0., 0. };
    for (double[] testVector : TEST_VECTORS_3D) {
      double[] r =
          Vectors.rotate3dTowards(testVector, ROTATE_TEST_ANGLE, xAxisPosDir);
      double[] r2 =
          Vectors.rotate3dTowards(testVector, ROTATE_TEST_ANGLE - Math.PI,
              xAxisPosDir);
      assertTrue("Rotation by some vector and by 180° opposed vector "
          + "did not yield opposite results for " + Arrays.toString(testVector)
          + ": " + Arrays.toString(r) + " vs. " + Arrays.toString(r2),
          compareDoubleArraysWithFactorAndDelta(r, r2, -1., DELTA));
      // SimSystem.report(Level.INFO,
      // "Rotated " + Arrays.toString(testVector) + " by "
      // + ROTATE_TEST_ANGLE + ": " + Arrays.toString(r));
      double testVectorLength = Vectors.vecNormEuclid(testVector);
      assertEquals(testVectorLength, Vectors.vecNormEuclid(r), DELTA);
      assertEquals(testVectorLength, Vectors.vecNormEuclid(r2), DELTA);
      assertEquals(ROTATE_TEST_ANGLE, Vectors.getAngle(testVector, r), DELTA);
      assertEquals(Math.PI - ROTATE_TEST_ANGLE,
          Vectors.getAngle(testVector, r2), DELTA);

    }
  }

}
