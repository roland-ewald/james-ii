/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.carules;

import static org.junit.Assert.*;

import java.util.Random;

import org.jamesii.ChattyTestRule;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Rybacki
 * 
 */
public class CAMultiSimulatorTest extends ChattyTestRule {

  private Random rnd;

  @Before
  public void setUp() throws Exception {

    // set seed for random number generator
    long seed = System.nanoTime();
    addParameter("seed", seed);
    rnd = new Random(seed);
  }

  /**
   * Test method for
   * {@link org.jamesii.simulator.carules.CAMultiSimulator#convertNDimIndexTo1DimIndex(int[], int[])}
   * .
   */
  @Test
  public final void testConvertNDimIndexTo1DimIndex() {
    for (int j = 0; j < 10; j++) {
      int[] size =
          new int[] { (rnd.nextInt(100)), rnd.nextInt(100), rnd.nextInt(100) };

      // 3D
      for (int x = 0; x < size[0]; x++) {
        for (int y = 0; y < size[1]; y++) {
          for (int z = 0; z < size[2]; z++) {
            int r = size[0] * size[1] * z + size[0] * y + x;
            int res =
                CAMultiSimulator.convertNDimIndexTo1DimIndex(size, new int[] {
                    x, y, z });
            assertEquals(r, res);
          }
        }
      }

      // 2D
      size = new int[] { rnd.nextInt(100), rnd.nextInt(100) };

      for (int x = 0; x < size[0]; x++) {
        for (int y = 0; y < size[1]; y++) {
          int r = size[0] * y + x;
          int res =
              CAMultiSimulator.convertNDimIndexTo1DimIndex(size, new int[] { x,
                  y });
          assertEquals(r, res);
        }
      }

      // 1D
      size = new int[] { rnd.nextInt(100) };

      for (int x = 0; x < size[0]; x++) {
        int r = x;
        int res =
            CAMultiSimulator.convertNDimIndexTo1DimIndex(size, new int[] { x });
        assertEquals(r, res);
      }

      // 4D
      size =
          new int[] { rnd.nextInt(100), rnd.nextInt(100), rnd.nextInt(100),
              rnd.nextInt(100) };

      for (int x = 0; x < size[0]; x++) {
        for (int y = 0; y < size[1]; y++) {
          for (int z = 0; z < size[2]; z++) {
            for (int k = 0; k < size[3]; k++) {
              int r =
                  size[0] * size[1] * size[2] * k + size[0] * size[1] * z
                      + size[0] * y + x;
              int res =
                  CAMultiSimulator.convertNDimIndexTo1DimIndex(size, new int[] {
                      x, y, z, k });
              assertEquals(r, res);
            }
          }
        }
      }
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.simulator.carules.CAMultiSimulator#convert1DimIndexToNDimIndex(int[], int)}
   * .
   */
  @Test
  public final void testConvert1DimIndexToNDimIndex() {
    int dim = rnd.nextInt(9) + 1;
    int[] size = new int[dim];

    int maxSize = 1;

    for (int i = 0; i < dim; i++) {
      size[i] = 1 + rnd.nextInt(9);
      maxSize *= size[i];
    }

    for (int i = 0; i < 100000; i++) {
      int r = rnd.nextInt(maxSize);
      int[] res = CAMultiSimulator.convert1DimIndexToNDimIndex(size, r);
      int rNew = CAMultiSimulator.convertNDimIndexTo1DimIndex(size, res);
      assertEquals(r, rNew);
    }

  }

}
