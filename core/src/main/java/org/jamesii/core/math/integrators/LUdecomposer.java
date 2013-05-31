/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

/**
 * LUdecompose - this is a class that L-U decomposes a given matrix. The two
 * resulting matrices are returned in a single one.
 * 
 * @author Gabriel Blum
 */
public class LUdecomposer {

  /** The d. */
  private int d;

  /** The index. */
  private int[] index;

  /** The length. */
  private int length;

  /** The matrix. */
  private double[][] matrix;

  /**
   * Instantiates a new lu decomposer.
   * 
   * @param input
   *          the input
   * @param n
   *          the n
   */
  public LUdecomposer(double[][] input, int n) {
    this.length = n;
    this.matrix = input;
  }

  /**
   * LU decomposes a matrix - original c code can be found in ludcmp.cpp Has
   * passed JUnit Test.
   * 
   * @return the lower and upper triangular matrix merged together
   */
  public double[][] ludecompose() {
    d = 1;
    int n = length;
    double[] vv = new double[n];
    int[] tempindex = new int[n];
    double sum;
    int maxi = 0;
    for (int i = 0; i < n; i++) {
      double max = 0;
      for (int j = 0; j < n; j++) {
        if (Math.abs(matrix[i][j]) > max) {
          max = Math.abs(matrix[i][j]);
        }
      }
      if (max == 0) {
        System.out.print("Singular Matrix");
      }
      vv[i] = 1.0 / max;
    }
    for (int j = 0; j < n; j++) {
      for (int i = 0; i < j; i++) {
        sum = matrix[i][j];
        for (int k = 0; k < i; k++) {
          sum -= matrix[i][k] * matrix[k][j];

        }
        matrix[i][j] = sum;
      }
      double max = 0;
      for (int i = j; i < n; i++) {
        sum = matrix[i][j];
        for (int k = 0; k < j; k++) {
          sum -= matrix[i][k] * matrix[k][j];
        }
        matrix[i][j] = sum;
        if (vv[i] * Math.abs(sum) >= max) {
          max = vv[i] * Math.abs(sum);
          maxi = i;
        }
      }
      if (j != maxi) {
        for (int k = 0; k < n; k++) {
          double temp = matrix[maxi][k];
          matrix[maxi][k] = matrix[j][k];
          matrix[j][k] = temp;
        }
        d = -d;
        vv[maxi] = vv[j];
      }
      tempindex[j] = maxi;
      if (j != n - 1) {
        double temp = (1.0 / matrix[j][j]);
        for (int i = j + 1; i < n; i++) {
          matrix[i][j] *= temp;
        }

      }

    }
    index = tempindex;
    return matrix;
  }

  /** The min number. */
  private static final double MINNUMBER = 0.00000000000001; // indicates a zero

  /**
   * LU-Decompostion with complete pivoting P*A*Q = LU - pivot is P and Q as
   * permutation vectors - overwrites the old matrix!.
   * 
   * @param a
   *          the a
   * @param pivot
   *          the pivot
   * 
   * @return true, if complete lu decomposition
   */
  public static boolean completeludecompose(double a[][], int[][] pivot) {
    int n = a.length;

    for (int i = 0; i < n; i++) {
      pivot[0][i] = i;
      pivot[1][i] = i;
    }

    for (int k = 0; k < n - 1; k++) {
      // search pivot
      int p = k, q = k;
      for (int j = k; j < n; j++) {
        for (int i = k; i < n; i++) {
          if (Math.abs(a[i][j]) > Math.abs(a[p][q])) {
            p = i;
            q = j;
          }
        }
      }

      // do pivot
      int tmp = pivot[0][k];
      pivot[0][k] = pivot[0][p];
      pivot[0][p] = tmp;

      tmp = pivot[1][k];
      pivot[1][k] = pivot[1][q];
      pivot[1][q] = tmp;

      // swap row
      for (int j = 0; j < n; j++) {
        double s = a[p][j];
        a[p][j] = a[k][j];
        a[k][j] = s;
      }
      // swap column
      for (int i = 0; i < n; i++) {
        double s = a[i][q];
        a[i][q] = a[i][k];
        a[i][k] = s;
      }

      // test if number is too small (division maybe infty!?)
      if (Math.abs(a[k][k]) < MINNUMBER) {
        // matrix is singular, but LU decomposion successfully finished
        return false;
      }

      // do LU-step
      for (int i = k + 1; i < n; i++) {
        a[i][k] /= a[k][k];
        for (int j = k + 1; j < n; j++) {
          a[i][j] -= a[i][k] * a[k][j];
        }
      }
    }

    return Math.abs(a[n - 1][n - 1]) > MINNUMBER;
  }

  /*
   * solves Ax=b with PAQ = LU (complete pivoting) if A is singular, then U has
   * a connected zero-submatrix (button left)
   */
  /**
   * Solve lu.
   * 
   * @param lu
   *          the lU
   * @param pivot
   *          the pivot
   * @param b
   *          the b
   * 
   * @return the double[]
   */
  public static double[] solveLU(double lu[][], int pivot[][], double b[]) {

    int n = lu.length;

    double x[] = new double[n];
    double y[] = new double[n];
    double z[] = new double[n];

    // solve Lz = Pb, and
    for (int i = 0; i < n; i++) {
      double tmp = b[pivot[0][i]];
      for (int j = 0; j < i; j++) {
        tmp -= z[j] * lu[i][j];
      }
      z[i] = tmp;
    }
    // solve Uy = z
    for (int i = n - 1; i >= 0; i--) {
      if (Math.abs(lu[i][i]) >= MINNUMBER) {
        double tmp = z[i];
        for (int j = i + 1; j < n; j++) {
          tmp -= y[j] * lu[i][j];
        }
        y[i] = tmp / lu[i][i];
      } else {
        y[i] = 0;
      }
    }

    for (int i = 0; i < n; i++) {
      x[pivot[1][i]] = y[i];
    }

    return x;
  }

  /**
   * Test LU solution.
   * 
   * TODO: Make a unit test out of this
   * 
   * @param a
   *          the a
   * @param x
   *          the x
   * @param b
   *          the b
   */
  public static void testLUSolution(double a[][], double x[], double b[]) {
    System.out.println("Testing LUdecomp-solution");
    int n = b.length;
    double y[] = new double[n];
    for (int i = 0; i < n; i++) {
      y[i] = 0;
      for (int j = 0; j < n; j++) {
        y[i] += a[i][j] * x[j];
      }
      System.out.println(x[i] + "\t" + b[i] + "\t" + y[i] + "\t\terr = "
          + (b[i] - y[i]));
    }

  }
}
