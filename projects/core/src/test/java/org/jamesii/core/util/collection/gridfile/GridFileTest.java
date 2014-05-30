/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.gridfile;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.collection.gridfile.GridFile;

import junit.framework.TestCase;

public class GridFileTest extends TestCase {

  /**
   * Create 4^9 = 260.000 objects with 9 dimensions. Save them in a grid file
   * and search them afterwards. Use a root directory with 2^9 = 512 grid
   * directories.
   */
  public void testExample1() {

    List<List<Double>> rootScales = new ArrayList<>();
    for (int i = 0; i < 9; ++i) {
      List<Double> scale = new ArrayList<>();
      scale.add(-0.5);
      scale.add(3.5);
      scale.add(4.5);
      rootScales.add(scale);
    }

    GridFile<TestClass> file =
        new GridFile<>(new TestExtractor(), rootScales, 5, 0, 0);

    // insert 4^9 = 260.000 objects
    int n1 = 4;
    for (double dim0 = 0; dim0 < n1; ++dim0) {
      for (double dim1 = 0; dim1 < n1; ++dim1) {
        for (double dim2 = 0; dim2 < n1; ++dim2) {
          for (double dim3 = 0; dim3 < n1; ++dim3) {
            for (double dim4 = 0; dim4 < n1; ++dim4) {
              for (double dim5 = 0; dim5 < n1; ++dim5) {
                for (double dim6 = 0; dim6 < n1; ++dim6) {
                  for (double dim7 = 0; dim7 < n1; ++dim7) {
                    for (double dim8 = 0; dim8 < n1; ++dim8) {
                      TestClass element =
                          new TestClass(dim0, dim1, dim2, dim3, dim4, dim5,
                              dim6, dim7, dim8);
                      file.add(element);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    int n2 = 2;
    // search 2^9 = 512 objects

    List<Integer> ignore1 = new ArrayList<>();
    ignore1.add(0);

    List<Integer> ignore2 = new ArrayList<>();
    ignore2.add(0);
    ignore2.add(5);
    ignore2.add(8);

    double[] data = new double[9];
    for (double dim0 = 0; dim0 < n2; ++dim0) {
      data[0] = dim0;
      for (double dim1 = 0; dim1 < n2; ++dim1) {
        data[1] = dim1;
        for (double dim2 = 0; dim2 < n2; ++dim2) {
          data[2] = dim2;
          for (double dim3 = 0; dim3 < n2; ++dim3) {
            data[3] = dim3;
            for (double dim4 = 0; dim4 < n2; ++dim4) {
              data[4] = dim4;
              for (double dim5 = 0; dim5 < n2; ++dim5) {
                data[5] = dim5;
                for (double dim6 = 0; dim6 < n2; ++dim6) {
                  data[6] = dim6;
                  for (double dim7 = 0; dim7 < n2; ++dim7) {
                    data[7] = dim7;
                    for (double dim8 = 0; dim8 < n2; ++dim8) {
                      data[8] = dim8;

                      // first check: exact matching
                      assertEquals(file.exactMatch(data).size(), 1);

                      // second check: partial matching with one ignored
                      // dimension
                      assertEquals(file.partialMatch(data, ignore1).size(), n1);

                      // third check: partial matching with three ignored
                      // dimensions
                      assertEquals(file.partialMatch(data, ignore2).size(), n1
                          * n1 * n1);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

  }

}
