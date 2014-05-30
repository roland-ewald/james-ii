/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import org.jamesii.core.math.Matrix;
import org.jamesii.core.math.MatrixException;

import junit.framework.TestCase;

/**
 * Tests the {@link Matrix} class.
 * 
 * @author Johannes Rössel
 */
public class TestMatrix extends TestCase {

  /** Tests the {@link Matrix#Matrix(int)} constructor. */
  public void testIntConstructor() {
    // should create a square identity matrix
    Matrix m;

    for (int i = 1; i < 30; i++) {
      m = new Matrix(i);

      assertEquals(i, m.getRows());
      assertEquals(i, m.getColumns());

      for (int c = 0; c < m.getColumns(); c++) {
        for (int r = 0; r < m.getRows(); r++) {
          assertEquals(r == c ? 1.0 : 0.0, m.getElement(r, c));
        }
      }
    }
  }

  /** Tests the {@link Matrix#Matrix(int, int)} constructor. */
  public void testIntIntConstructor() {
    Matrix m;

    for (int i = 1; i < 30; i++) {
      for (int j = 1; j < 30; j++) {
        m = new Matrix(i, j);

        assertEquals(i, m.getRows());
        assertEquals(j, m.getColumns());

        for (int c = 0; c < m.getColumns(); c++) {
          for (int r = 0; r < m.getRows(); r++) {
            assertEquals(0.0, m.getElement(r, c));
          }
        }
      }
    }
  }

  /** Tests the {@link Matrix#Matrix(double[][])} constructor. */
  public void testDoubleArrayConstructor() {
    double[][] data = new double[][] { { 1., 2., 3. }, { 4., 5., 6. } };
    Double[][] dData =
        new Double[][] { { data[0][0], data[0][1], data[0][2] },
            { data[1][0], data[1][1], data[1][2] } };
    checkConstructedDoubleArrayMatrix(data, new Matrix(data));
    checkConstructedDoubleArrayMatrix(data, new Matrix(dData));
  }

  /**
   * Checks constructed matrix.
   * 
   * @param data
   *          the data with which the matrix was initialized
   * @param matrix
   *          the matrix
   */
  private void checkConstructedDoubleArrayMatrix(double[][] data, Matrix matrix) {
    assertEquals(2, matrix.getRows());
    assertEquals(3, matrix.getColumns());
    for (int c = 0; c < matrix.getColumns(); c++) {
      for (int r = 0; r < matrix.getRows(); r++) {
        assertEquals(data[r][c], matrix.getElement(r, c));
      }
    }
  }

  /** Tests the {@link Matrix#Matrix(double[])} constructor. */
  public void testOneDimDoubleArrayConstructor() {
    double[] data = new double[] { 1., 2., 3. };
    Double[] dData = new Double[] { data[0], data[1], data[2] };
    checkConstructedOneDimDoubleArrayMatrix(data, new Matrix(data));
    checkConstructedOneDimDoubleArrayMatrix(data, new Matrix(dData));
  }

  /**
   * Check the constructed one-dimensional double array matrix.
   * 
   * @param data
   *          the test data
   * @param matrix
   *          the constructed matrix
   */
  private void checkConstructedOneDimDoubleArrayMatrix(double[] data,
      Matrix matrix) {
    assertEquals(data.length, matrix.getRows());
    assertEquals(1, matrix.getColumns());
    for (int r = 0; r < matrix.getRows(); r++) {
      assertEquals(data[r], matrix.getElement(r, 0));
    }
  }

  /** Tests the {@link Matrix#add(Matrix)} method. */
  public void testAdd() {
    double[][] data1 = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    double[][] data2 = new double[][] { { 8, 7, 6 }, { -4, 23, -8 } };
    double[][] results = new double[][] { { 9, 9, 9 }, { 0, 28, -2 } };

    Matrix m = new Matrix(data1);
    m.add(new Matrix(data2));
    for (int c = 0; c < m.getColumns(); c++) {
      for (int r = 0; r < m.getRows(); r++) {
        assertEquals(results[r][c], m.getElement(r, c));
      }
    }
  }

  /**
   * Tests whether the {@link Matrix#add(Matrix)} method throws the correct
   * exceptions when encountering invalid arguments.
   */
  public void testAddExceptions() {
    double[][] data1 = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    double[][] data2 = new double[][] { { 8, 7, 6 }, { 4, 3, 8 }, { 1, 2, 3 } };

    Matrix m1 = new Matrix(data1);
    Matrix m2 = new Matrix(data2);

    try {
      m1.add(m2);
    } catch (MatrixException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }
  }

  /**
   * Tests the {@link Matrix#addCols(int, int)} and
   * {@link Matrix#addCols(int, int, double)} methods.
   */
  public void testAddCols() {
    double[][] data = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    double[][] results1 = new double[][] { { 3, 2, 3 }, { 9, 5, 6 } };
    double[][] results2 = new double[][] { { 3, 8, 3 }, { 9, 17, 6 } };

    Matrix m = new Matrix(data);
    m.addCols(0, 1);
    for (int c = 0; c < m.getColumns(); c++) {
      for (int r = 0; r < m.getRows(); r++) {
        assertEquals(results1[r][c], m.getElement(r, c));
      }
    }

    m.addCols(1, 2, 2);
    for (int c = 0; c < m.getColumns(); c++) {
      for (int r = 0; r < m.getRows(); r++) {
        assertEquals(results2[r][c], m.getElement(r, c));
      }
    }
  }

  /**
   * Tests whether the {@link Matrix#addCols(int, int)} and
   * {@link Matrix#addCols(int, int, double)} methods throw the correct
   * exceptions when encountering invalid arguments.
   */
  public void testAddColsExceptions() {
    // TODO: Should the exception being thrown here really be an
    // ArrayIndexOutOfBoundsException?
    Matrix m = new Matrix(3);

    // test addCols(int, int)

    try {
      m.addCols(1, 3);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addCols(5, 2);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addCols(-1, 1);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addCols(1, -3);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    // test addCols(int, int, double)

    try {
      m.addCols(1, 3, 1);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addCols(5, 2, 2);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addCols(-1, 1, 3);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addCols(1, -3, 4);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }
  }

  /**
   * Tests the {@link Matrix#addRows(int, int)} and
   * {@link Matrix#addRows(int, int, double)} methods.
   */
  public void testAddRows() {
    double[][] data = new double[][] { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
    double[][] res1 = new double[][] { { 5, 7, 9 }, { 4, 5, 6 }, { 7, 8, 9 } };
    double[][] res2 =
        new double[][] { { 5, 7, 9 }, { 18, 21, 24 }, { 7, 8, 9 } };

    Matrix m = new Matrix(data);
    m.addRows(0, 1);
    for (int c = 0; c < m.getColumns(); c++) {
      for (int r = 0; r < m.getRows(); r++) {
        assertEquals(res1[r][c], m.getElement(r, c));
      }
    }

    m.addRows(1, 2, 2);
    for (int c = 0; c < m.getColumns(); c++) {
      for (int r = 0; r < m.getRows(); r++) {
        assertEquals(res2[r][c], m.getElement(r, c));
      }
    }

  }

  /**
   * Tests whether the {@link Matrix#addRows(int, int)} and
   * {@link Matrix#addRows(int, int, double)} methods throw the correct
   * exceptions when encountering invalid arguments.
   */
  public void testAddRowsExceptions() {
    // TODO: Should the exception being thrown here really be an
    // ArrayIndexOutOfBoundsException?
    Matrix m = new Matrix(3);

    // test addRows(int, int)

    try {
      m.addRows(1, 3);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addRows(5, 2);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addRows(-1, 1);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addRows(1, -3);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    // test addRows(int, int, double)

    try {
      m.addRows(1, 3, 1);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addRows(5, 2, 2);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addRows(-1, 1, 3);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      m.addRows(1, -3, 4);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }
  }

  /** Tests the {@link Matrix#concat(Matrix)} method. */
  public void testConcat() {
    Matrix m1 = new Matrix(new double[][] { { 1, 2 }, { 3, 4 }, { 5, 6 } });
    Matrix m2 = new Matrix(new double[][] { { 7 }, { 6 }, { 5 } });
    double[][] result =
        new double[][] { { 1, 2, 7 }, { 3, 4, 6 }, { 5, 6, 5 } };

    m1.concat(m2);
    for (int c = 0; c < m1.getColumns(); c++) {
      for (int r = 0; r < m1.getRows(); r++) {
        assertEquals(result[r][c], m1.getElement(r, c));
      }
    }
  }

  /**
   * Tests whether the {@link Matrix#concat(Matrix)} method throws the correct
   * exceptions when the matrices can't be concatenated.
   */
  public void testConcatExceptions() {
    Matrix m1 = new Matrix(3, 2);
    Matrix m2 = new Matrix(4, 5);

    try {
      m1.concat(m2);
      fail();
    } catch (MatrixException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }
  }

  /** Tests the {@link Matrix#copy()} method. */
  public void testCopy() {
    double[][] data = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    Matrix m1 = new Matrix(data);

    Matrix m2 = m1.copy();

    assertNotNull(m2);
    assertFalse(m1 == m2);
    for (int c = 0; c < m1.getColumns(); c++) {
      for (int r = 0; r < m1.getRows(); r++) {
        assertEquals(data[r][c], m2.getElement(r, c));
      }
    }
  }

  /** Tests the {@link Matrix#det()} method. */
  public void testDet() {
    Matrix m = new Matrix(new double[][] { { 1, 2 }, { 4, 5 } });
    assertEquals(-3.0, m.det(), 0.000001);

    m = new Matrix(6);
    assertEquals(1.0, m.det(), 0.000001);

    m = new Matrix(new double[][] { { 1, 2, 0 }, { 4, 0, 8 }, { 1, 2, 2 } });
    assertEquals(-16.0, m.det(), 0.000001);

    m = new Matrix(0, 0);
    assertEquals(1.0, m.det(), 0.000001);

    m = new Matrix(1, 1);
    m.setElement(0, 0, 42.23);
    assertEquals(42.23, m.det(), 0.000001);
  }

  /**
   * Tests whether the {@link Matrix#det()} method throws the correct exceptions
   * when ran on a matrix that does not have a determinant.
   */
  public void testDetExceptions() {
    try {
      new Matrix(1, 2).det();
      fail();
    } catch (MatrixException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }
  }

  /** Tests the {@link Matrix#flood(double)} method. */
  public void testFlood() {
    Matrix m = new Matrix(10, 10);

    m.flood(42.23);

    for (int c = 0; c < m.getColumns(); c++) {
      for (int r = 0; r < m.getRows(); r++) {
        assertEquals(42.23, m.getElement(r, c));
      }
    }
  }

  /** Tests the {@link Matrix#getColumn(int)} method. */
  public void testGetColumn() {
    Matrix m = new Matrix(5);
    double[] c = m.getColumn(2);
    for (int i = 0; i < c.length; i++) {
      assertEquals(m.getElement(i, 2), c[i]);
    }
  }

  /** Tests the {@link Matrix#getData()} method. */
  public void testGetData() {
    double[][] data = new double[][] { { 1, 2 }, { 3, 4 } };
    Matrix m = new Matrix(data);

    double[][] x = m.getData();
    for (int c = 0; c < m.getColumns(); c++) {
      for (int r = 0; r < m.getRows(); r++) {
        assertEquals(data[r][c], x[r][c]);
      }
    }

    // since the docs say it's a pointer to the internal data we can change it
    // and the change will be reflected in the Matrix object
    x[1][1] = 5.0;
    assertEquals(5.0, m.getElement(1, 1));
  }

  /** Tests the {@link Matrix#getRow(int)} method. */
  public void testGetRow() {
    Matrix m = new Matrix(5);
    double[] c = m.getRow(2);
    for (int i = 0; i < c.length; i++) {
      assertEquals(m.getElement(2, i), c[i]);
    }
  }

  /** Tests the {@link Matrix#isSquare()} method. */
  public void testIsSquare() {
    assertTrue(new Matrix(20).isSquare());
    assertTrue(new Matrix(1, 1).isSquare());
    assertTrue(new Matrix(5, 5).isSquare());
    assertTrue(new Matrix(8).isSquare());

    assertFalse(new Matrix(1, 2).isSquare());
    assertFalse(new Matrix(15, 14).isSquare());

    double[][] data = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    assertFalse(new Matrix(data).isSquare());

    data = new double[][] { { 1, 2, 3 }, { 4, 5, 6 }, { 8, 7, 6 } };
    assertTrue(new Matrix(data).isSquare());
  }

  /** Tests the {@link Matrix#multMat(double)} method. */
  public void testMultMat() {
    double[][] data = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    double[][] result = new double[][] { { 1.5, 3, 4.5 }, { 6, 7.5, 9 } };

    Matrix m = new Matrix(data);
    m.multMat(1.5);

    for (int c = 0; c < m.getColumns(); c++) {
      for (int r = 0; r < m.getRows(); r++) {
        assertEquals(result[r][c], m.getElement(r, c));
      }
    }
  }

  /** Tests the {@link Matrix#setDiagonal(double)} method. */
  public void testSetDiagonal() {
    Matrix m;

    for (int i = 1; i < 30; i++) {
      m = new Matrix(i, i);
      m.setDiagonal(42.42);
      for (int c = 0; c < m.getColumns(); c++) {
        for (int r = 0; r < m.getRows(); r++) {
          assertEquals(r == c ? 42.42 : 0.0, m.getElement(r, c));
        }
      }
    }

    // TODO: test non-square matrices here
  }

  /** Tests the {@link Matrix#sub(Matrix)} method. */
  public void testSub() {
    Matrix m1 =
        new Matrix(new double[][] { { 7, 2, 6 }, { 2, 8, 2 }, { 2, 3, 1 } });
    Matrix m2 =
        new Matrix(new double[][] { { 2, 3, 5 }, { 1, -4, 2 }, { -2, 1, 7 } });
    double[][] result =
        new double[][] { { 5, -1, 1 }, { 1, 12, 0 }, { 4, 2, -6 } };

    m1.sub(m2);

    for (int c = 0; c < m1.getColumns(); c++) {
      for (int r = 0; r < m1.getRows(); r++) {
        assertEquals(result[r][c], m1.getElement(r, c));
      }
    }
  }

  /**
   * Tests whether the {@link Matrix#sub(Matrix)} method throws the correct
   * exceptions when encountering invalid arguments.
   */
  public void testSubExceptions() {
    try {
      new Matrix(1, 2).sub(new Matrix(2));
      fail();
    } catch (MatrixException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      new Matrix(2).sub(new Matrix(2, 3));
      fail();
    } catch (MatrixException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }
  }

  /** Tests the {@link Matrix#swapRows(int, int)} method. */
  public void testSwapRows() {
    double[][] data = new double[][] { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
    double[][] result =
        new double[][] { { 7, 8, 9 }, { 4, 5, 6 }, { 1, 2, 3 } };

    Matrix m = new Matrix(data);
    m.swapRows(0, 2);

    for (int c = 0; c < m.getColumns(); c++) {
      for (int r = 0; r < m.getRows(); r++) {
        assertEquals(result[r][c], m.getElement(r, c));
      }
    }
  }

  /**
   * Tests whether the {@link Matrix#swapRows(int, int)} method throws the
   * correct exceptions when encountering invalid arguments.
   */
  public void testSwapRowsExceptions() {
    // TODO: should this really be ArrayIndexOutOfBoundsExceptions here?

    try {
      new Matrix(5).swapRows(-1, 2);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }

    try {
      new Matrix(5).swapRows(3, -2);
      fail();
    } catch (ArrayIndexOutOfBoundsException e) {
      assertTrue(true);
    } catch (Throwable e) {
      fail();
    }
  }

  /** Tests the {@link Matrix#transpose()} method. */
  public void testTranspose() {
    double[][] data = new double[][] { { 1, 2, 3 }, { 4, 5, 6 } };
    Matrix m1 = new Matrix(data);

    Matrix m2 = m1.transpose();

    for (int c = 0; c < m2.getColumns(); c++) {
      for (int r = 0; r < m2.getRows(); r++) {
        assertEquals(data[c][r], m2.getElement(r, c));
      }
    }
  }

}
