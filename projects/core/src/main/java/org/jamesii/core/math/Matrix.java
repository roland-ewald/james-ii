/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * The Class Matrix. Class containing an implementation of a matrix together
 * with some basic matrix manipulation methods.
 * 
 * Created on 25.02.2005
 * 
 * @author Jan Himmelspach
 */
public class Matrix implements Serializable {

  /** Serial version ID. */
  static final long serialVersionUID = 4608222608627769042L;

  /**
   * Prints the given matrix.
   * 
   * @param m
   *          matrix to be printed
   */
  public static void print(Matrix m) {
    print(m, null);
  }

  /**
   * Prints the given matrix, using the given {@link DecimalFormat} to format
   * the values
   * 
   * @param m
   *          matrix to be printed
   * @param df
   *          {@link DecimalFormat} to use for formatting the values. May be
   *          null
   */
  public static void print(Matrix m, DecimalFormat df) {
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < m.getRows(); i++) {
      text.append(printline(m.getRow(i), df));
      text.append("\n");
    }
    System.out.print(text.toString());
  }

  /**
   * Prints the given array by using the given {@link DecimalFormat} to format
   * the numbers.
   * 
   * @param m
   *          Array to be printed
   * @param df
   *          {@link DecimalFormat} to use for printing. May be null
   */
  public static String printline(double[] m, DecimalFormat df) {
    StringBuilder text = new StringBuilder();
    for (int i = 0; i < m.length; i++) {
      if (df != null) {
        text.append(df.format(m[i]));
        text.append(" ");
      } else {
        text.append(m[i]);
        text.append(" ");
      }
    }
    return text.toString();
  }

  /** Number of columns of this matrix. */
  private int cols;

  /** Data of the matrix. This array has the size rows X cols. */
  private double[][] data;

  /** Number of rows of this matrix. */
  private int rows;

  /**
   * Create a new matrix using the given array as data. Note: the array must be
   * rectangular! This is NOT checked!
   * 
   * @param data
   *          array (of size n x m) of values
   */
  public Matrix(double[][] data) {
    this.rows = data.length;
    this.cols = data[0].length;
    setData(data);
  }

  /**
   * Create a new matrix using the given array as data. Note: the array must be
   * rectangular! This is NOT checked!
   * 
   * @param data
   *          array (of size n x m) of values
   */
  public Matrix(Double[][] data) {
    this(data.length, data[0].length);
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data[0].length; j++) {
        this.data[i][j] = data[i][j];
      }
    }
  }

  /**
   * Instantiates a new matrix with a single column containing all data (i.e., a
   * vector).
   * 
   * @param d
   *          the column data
   */
  public Matrix(double[] d) {
    this(d.length, 1);
    for (int i = 0; i < d.length; i++) {
      data[i][0] = d[i];
    }
  }

  /**
   * Instantiates a new matrix with a single column containing all data (i.e., a
   * vector).
   * 
   * @param d
   *          the column data
   */
  public Matrix(Double[] d) {
    this(d.length, 1);
    for (int i = 0; i < d.length; i++) {
      data[i][0] = d[i];
    }
  }

  /**
   * Creates a square id matrix, i.e. a matrix with the same number of rows and
   * columns is created. The diagonal from the left top edge to the right bottom
   * edge is filled with the value 1.0, all other cells are set to 0.0!
   * 
   * @param rowcol
   *          number of columns and rows
   */
  public Matrix(int rowcol) {
    this(rowcol, rowcol);
    flood(0.0);
    setDiagonal(1.0);
  }

  /**
   * Create a new matrix with the given dimensions.
   * 
   * @param rows
   *          number of rows
   * @param cols
   *          number of columns
   */
  public Matrix(int rows, int cols) {
    data = new double[rows][cols];
    this.rows = rows;
    this.cols = cols;
  }

  /**
   * Adds the given matrix (m) on this<br>
   * <br>
   * A + B =<br>
   * a00+b00&nbsp;a01+b01&nbsp;a02+b02<br>
   * a10+b10&nbsp;a11+b11&nbsp;a12+b12<br>
   * a20+b20&nbsp;a21+b21&nbsp;a22+b22<br>
   * .
   * 
   * @param m
   *          matrix to be added on this matrix
   * 
   * @exception MatrixException
   *              This method will throw a MatrixException if the two matrices
   *              are not compatible! I.e. if do not have the same number of
   *              rows and columns.
   */
  public void add(Matrix m) {

    if ((rows != m.getRows()) || (cols != m.getColumns())) {
      throw new MatrixException("Cannot add these two matrices \n Matrix A ("
          + rows + "x" + cols + ") Matrix B (" + m.getRows() + "x"
          + m.getColumns() + ")");
    }

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        data[i][j] = data[i][j] + m.getElement(i, j);
      }
    }

  }

  /**
   * Adds the values of col2 on those of col1.
   * 
   * @param col1
   *          number of column the values from cal2 shall be added to
   * @param col2
   *          number of column which contains the values to be added to col1
   */
  public void addCols(int col1, int col2) {
    for (int i = 0; i < rows; i++) {
      data[i][col1] += data[i][col2];
    }
  }

  /**
   * Adds the values of col2 multiplied with the given factor on those of col1.
   * 
   * @param col1
   *          number of column the values of col2 shall be added to
   * @param col2
   *          number of column from which the values to be added shall be taken
   * @param factor
   *          the values of col2 gets multiplied with this factor before they
   *          are added
   */
  public void addCols(int col1, int col2, double factor) {
    for (int i = 0; i < rows; i++) {
      data[i][col1] += data[i][col2] * factor;
    }
  }

  /**
   * Adds the values of row2 on those of row1.
   * 
   * @param row1
   *          the row1
   * @param row2
   *          the row2
   */
  public void addRows(int row1, int row2) {
    for (int i = 0; i < cols; i++) {
      data[row1][i] += data[row2][i];
    }
  }

  /**
   * Adds the values of row2 multiplied with the given factor on those of row1.
   * 
   * @param row1
   *          the row1
   * @param row2
   *          the row2
   * @param factor
   *          the factor
   */
  public void addRows(int row1, int row2, double factor) {
    for (int i = 0; i < cols; i++) {
      data[row1][i] += data[row2][i] * factor;
    }
  }

  /**
   * Concatenate the given matrix to this If matrix A has n cols and matrix B m
   * cols than the resulting matrix has n+m cols! Number of rows must be equal
   * a11 a12 ... a1n b1
   * 
   * @param m
   *          matrix to be concatenated on this matrix
   * 
   * @exception MatrixException
   *              This method will throw a MatrixException if the two matrices
   *              are not compatible! I.e. if the row numbers are not equal.
   */
  public void concat(Matrix m) {

    if (rows != m.getRows()) {
      throw new MatrixException(
          "Cannot concatenate these two matrices \n Matrix A (" + rows + "x"
              + cols + ") Matrix B (" + m.getRows() + "x" + m.getColumns()
              + ")");
    }

    double[][] data2 = new double[rows][cols + m.getColumns()];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        data2[i][j] = data[i][j];
      }
    }

    for (int i = 0; i < m.getRows(); i++) {
      for (int j = 0; j < m.getColumns(); j++) {
        data2[i][cols + j] = m.getElement(i, j);
      }
    }

    this.cols += m.getColumns();
    data = data2;

  }

  /**
   * Creates a (deep) copy of the matrix.
   * 
   * @return new matrix, equivalent to this one
   */
  public Matrix copy() {
    Matrix m = new Matrix(rows, cols);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        m.setElement(i, j, data[i][j]);
      }
    }
    return m;
  }

  /**
   * Returns the minor <i>M</i><sub><i>i</i>, <i>j</i></sub>, the matrix that
   * results from removing the <i>i</i>-th row and the <i>j</i>-th column from
   * the given matrix.
   * 
   * @param i
   *          The row to remove from the matrix.
   * @param j
   *          The column to remove from the matrix.
   * 
   * @return This matrix, reduced by the <i>i</i>-th row and the <i>j</i>-th
   *         column.
   */
  private Matrix getMinor(int i, int j) {
    Matrix m = new Matrix(rows - 1, cols - 1);
    for (int r = 0; r < rows; r++) {
      if (r != i) {
        for (int c = 0; c < cols; c++) {
          if (c != j) {
            m.setElement(r - (r > i ? 1 : 0), c - (c > j ? 1 : 0), data[r][c]);
          }
        }
      }
    }
    return m;
  }

  /**
   * Calculates the determinant value of the matrix. This is currently done with
   * Laplacian expansion by minors which can be slow on large matrices.
   * <p>
   * TODO: Laplacian expansion is horribly slow for large (> 6×6) matrices.
   * Better to use Gaussian elimination here.
   * 
   * @return The determinant of the matrix
   * 
   * @exception MatrixException
   *              if the matrix is not square
   */
  public double det() {
    if (!isSquare()) {
      throw new MatrixException("Determinants only exist for square matrices");
    }

    // special cases
    if (rows == 0) {
      return 1.0;
    }
    if (rows == 1) {
      return data[0][0];
    } else if (rows == 2) {
      // usual determinant definition
      return data[0][0] * data[1][1] - data[0][1] * data[1][0];
    } else if (rows == 3) {
      // rule of Sarrus
      return data[0][0] * data[1][1] * data[2][2] + data[1][0] * data[2][1]
          * data[0][2] + data[2][0] * data[0][1] * data[1][2] - data[2][0]
          * data[1][1] * data[0][2] - data[1][0] * data[0][1] * data[2][2]
          - data[0][0] * data[2][1] * data[1][2];
    } else {
      // Laplacian expansion by minors
      double result = 0.0;
      for (int i = 0; i < cols; i++) {
        result += (i % 2 == 0 ? 1 : -1) * data[0][i] * getMinor(0, i).det();
      }
      return result;
    }
  }

  /**
   * Sets/Fills/Floods all cells of the matrix to the given value.
   * 
   * 
   * @param value
   *          the value all cells of the matrix shall be set to
   */
  public final void flood(double value) {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        data[i][j] = value;
      }
    }
  }

  /**
   * Gauss - Jordan Transformation First part of the matrix will be converted
   * into id matrix.
   */
  public void gaussJordan() {

    int col = 0;

    for (int r = 0; (r < rows) && (col < cols); r++) {

      int row = r;
      // find row with a non zero value at col
      while ((row < rows) && (Double.compare(data[row][col], 0) == 0)) {
        row++;
      }
      // if we reached the "row after the last row"
      // thus we haven't found a value != 0 we should exit ...
      if (row == rows) {
        col++;
        continue;
        // throw new RuntimeException ("Found no more convertable rows");
        // return;
      }

      // System.out.println("Base");
      // print();
      //
      // make this row the "top most row"
      if (r != row) {
        swapRows(r, row);
        // System.out.println("Swapped rows ");
        // print();
        //
        // System.out.println("Dividing by multiple value of "+data[row][col]+"
        // "+1 / data[row][col]);
      }

      // divide values by the first value (thereby skip the first col-1 columns
      // - they should be 0!)
      multRowFrom(r, col, 1 / data[r][col]);

      // System.out.println("Dividing by multiple value of "+col);
      // print();
      //
      for (int i = 0; i < rows; i++) {
        if (i != r) {
          this.subRows(i, r, data[i][col] / data[r][col]);
        }
      }

      // System.out.println("Computed others");
      // print();

      col++;
    }
  }

  // TODO: gaussJordan() might yield NaN in the matrix
  // below is a different computation (though same idea), taken from
  // en.wikipedia:Row echelon form. It doesn't seem to work for the matrices
  // that produce NaN yet, but I'll leave it in here

  /*
   * Let lead = 0 Let rowCount be the number of rows in M Let columnCount be the
   * number of columns in M For r = 0 to rowCount - 1 If columnCount <= lead
   * STOP Let i = r While M[i, lead] = 0 Increment i If rowCount = i Let i = r
   * Increment lead If columnCount = lead STOP Swap rows i and r Divide row r by
   * M[r, lead] For all rows i except r Subtract M[i, lead] multiplied by row r
   * from row i Increment lead
   */
  /**
   * Gauss jordan2.
   */
  public void gaussJordan2() {
    int lead = 0;
    int rowCount = getRows();
    int columnCount = getColumns();
    for (int r = 0; r < rowCount; r++) {
      if (columnCount <= lead) {
        return;
      }
      int i = r;
      while (Double.compare(getElement(i, lead), 0) == 0) {
        i++;
        if (rowCount == i) {
          i = r;
          lead++;
          if (columnCount == lead) {
            return;
          }
        }
      }
      swapRows(i, r);
      multRow(r, 1 / getElement(r, lead));
      for (int x = 0; x < rowCount; x++) {
        if (x != r) {
          setElement(i, x,
              getElement(i, x) - getElement(i, lead) * getElement(r, x));
        }
      }
      lead++;
    }
  }

  /**
   * Calculates the rank of a matrix.
   * 
   * @return the int
   */
  public int rank() {
    // create a copy, since Gauss-Jordan is destructive
    Matrix m = this.copy();
    m.gaussJordan();
    // find non-zero rows and count them
    int retval = 0;
    for (int i = 0; i < m.getRows(); i++) {
      boolean nullLine = true;
      for (int j = 0; j < m.getColumns(); j++) {
        if (m.getElement(i, j) != 0) {
          nullLine = false;
          break;
        }
      }
      if (!nullLine) {
        retval++;
      }
    }
    return retval;
  }

  /**
   * Returns an array of length m (getRows()) containing the values of the given
   * column. This method needs to create a new array and to copy the values -
   * thus it needs O(getRows()) steps.
   * 
   * @param col
   *          number of column to be extracted
   * 
   * @return array of getRows() double values stored in the given column
   */
  public double[] getColumn(int col) {
    double[] row = new double[rows];

    for (int i = 0; i < rows; i++) {
      row[i] = data[i][col];
    }
    return row;
  }

  /**
   * Returns the number of columns of this matrix.
   * 
   * @return number of columns
   */
  public int getColumns() {
    return cols;
  }

  /**
   * Return the data stored in the matrix.
   * 
   * @return "pointer" to the internal data of the matrix
   */
  public double[][] getData() {
    return internalGetData();
  }

  /**
   * Return the value stored at the specified index<br>
   * <br>
   * a00&nbsp;a01&nbsp;a02<br>
   * a10&nbsp;a11&nbsp;a12<br>
   * a20&nbsp;a21&nbsp;a22<br>
   * For example: <code>getElement(1,1)</code> returns a11.
   * 
   * @param row
   *          row the value shall be taken from (0 .. getRows()-1)
   * @param col
   *          column the value shall be taken from (0 .. getColumns()-1)
   * 
   * @return the value stored at (row, col) in the matrix
   */
  public double getElement(int row, int col) {
    return data[row][col];
  }

  /**
   * Returns an array of length n (getColumns()) containing the values of the
   * given row. In contrast to the getColumn method this method works in O(1)
   * 
   * @param row
   *          number of row to be extracted
   * 
   * @return array of getColumns() double values stored in the given row
   */
  public double[] getRow(int row) {
    return data[row];
  }

  /**
   * Returns the number of rows of this matrix. Rows can be accessed in
   * [0..getRows()-1].
   * 
   * @return number of rows
   */
  public int getRows() {
    return rows;
  }

  /**
   * Gets a sub matrix of this matrix Note: first row and column have a index of
   * 0! The resulting matrix has a size of row2 - row1 + 1 times col2 - col1 +
   * 1.
   * 
   * @param row1
   *          first row of sub matrix
   * @param col1
   *          first column of sub matrix
   * @param row2
   *          last row to be copied into the sub matrix
   * @param col2
   *          last column to be copied into the sub matrix
   * 
   * @return new matrix containing the values from this matrix out of the given
   *         sub field
   * 
   * @exception MatrixException
   *              This method will throw a MatrixException if the indices are
   *              invalid!
   */
  public Matrix getSubMatrix(int row1, int col1, int row2, int col2) {

    if ((rows <= row2) || (cols <= col2) || (row1 > row2) || (col1 > col2)
        || (row1 < 0) || (col1 < 0)) {
      throw new MatrixException("Not a valid sub matrix! \n Matrix A (0, 0, "
          + (rows - 1) + ", " + (cols - 1) + ") sub matrix B (" + row1 + ", "
          + col1 + ", " + row2 + ", " + col2 + ")");
    }

    Matrix m = new Matrix(row2 - row1 + 1, col2 - col1 + 1);
    for (int i = row1; i <= row2; i++) {
      for (int j = col1; j <= col2; j++) {
        m.setElement(i - row1, j - col1, data[i][j]);
      }
    }

    return m;
  }

  /**
   * Internal get data.
   * 
   * @return the double[][]
   */
  protected double[][] internalGetData() {
    return data;
  }

  /**
   * This method determines whether this matrix is a square matrix, or not.
   * 
   * @return true if the number of columns and rows are equal, false otherwise
   */
  public boolean isSquare() {
    return cols == rows;
  }

  /**
   * Multiplies this matrix with another one and returns the result.
   * 
   * @param m
   *          matrix this matrix shall be multiplied with
   * 
   * @return product of this matrix times the given matrix m
   * 
   * @exception MatrixException
   *              This method will throw a MatrixException if the two matrices
   *              are not compatible! I. e. matrix mult only works if the
   *              matrices are m x n and n x r.
   */
  public Matrix mult(Matrix m) {

    if (cols != m.getRows()) {
      throw new MatrixException(
          "Cannot multiply these two matrices \n Matrix A (" + rows + "x"
              + cols + ") Matrix B (" + m.getRows() + "x" + m.getColumns()
              + ")");
    }

    Matrix product = new Matrix(rows, m.getColumns());
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < m.getColumns(); j++) {
        double result = 0;
        for (int sum = 0; sum < this.cols; sum++) {
          result += getElement(i, sum) * m.getElement(sum, j);
        }
        product.setElement(i, j, result);
      }
    }

    return product;
  }

  /**
   * Multiplies all values of the given column with the given factor.
   * 
   * The column number "col" has to be 0 <= col <= number of columns.
   * 
   * @param col
   *          the column number of which the values shall be multiplied with the
   *          given factor
   * @param factor
   *          the factor
   */
  public void multCol(int col, double factor) {
    for (int i = 0; i < rows; i++) {
      data[i][col] *= factor;
    }
  }

  /**
   * Multiplies the complete matrix (each value in the matrix) with the given
   * factor.
   * 
   * @param factor
   *          the factor
   */
  public void multMat(double factor) {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        data[i][j] *= factor;
      }
    }
  }

  /**
   * Multiplies all values of the given row with the given factor.
   * 
   * @param row
   *          row to be multiplied by the given factor
   * @param factor
   *          with which the values of the row shall be multiplied with
   */
  public void multRow(int row, double factor) {
    for (int i = 0; i < cols; i++) {
      data[row][i] *= factor;
    }
  }

  /**
   * Multiplies all values of the given row with the given factor.
   * 
   * @param row
   *          row to be multiplied by the given factor
   * @param col
   *          column to start with (cols < col will not be modified)
   * @param factor
   *          with which the values of the row shall be multiplied with
   */
  public void multRowFrom(int row, int col, double factor) {
    for (int i = col; i < cols; i++) {
      data[row][i] *= factor;
    }
  }

  /**
   * Print this matrix. Does not use any special number formatting, thus columns
   * may not be easily identifiable!
   */
  public void print() {
    print(this);
  }

  /**
   * Print this matrix, using the given {@link DecimalFormat} to format the
   * values
   * 
   * @param df
   *          decimal format to be used for formatting the numbers
   */
  public void print(DecimalFormat df) {
    print(this, df);
  }

  /**
   * Sets the data.
   * 
   * @param data
   *          the data
   */
  protected final void setData(double[][] data) {
    this.data = data;
  }

  /**
   * Sets the diagonal of the matrix to the given value.<br>
   * 
   * <p>
   * val&nbsp;a12&nbsp;a13<br>
   * a21&nbsp;val&nbsp;a23<br>
   * a31&nbsp;a32&nbsp;val<br>
   * 
   * If the matrix is not a square matrix the minimum of cols and rows is used <br>
   * rows: <br>
   * val&nbsp;a12&nbsp;a13<br>
   * a21&nbsp;val&nbsp;a23<br>
   * a31&nbsp;a32&nbsp;val<br>
   * a41&nbsp;a42&nbsp;a43<br>
   * cols: <br>
   * val&nbsp;a12&nbsp;a13&nbsp;a14<br>
   * a21&nbsp;val&nbsp;a23&nbsp;a24<br>
   * a31&nbsp;a32&nbsp;val&nbsp;a34<br>
   * </p>
   * 
   * @param value
   *          the value the diagonal shall be set to
   */
  public final void setDiagonal(double value) {
    for (int i = 0; i < Math.min(rows, cols); i++) {
      data[i][i] = value;
    }
  }

  /**
   * Set the element at the given index to value.
   * 
   * @param row
   *          the row number (0 <= row < {@link #getRows()})
   * @param col
   *          the column number (0 <= col < {@link #getColumns()})
   * @param value
   *          the value
   */
  public void setElement(int row, int col, double value) {
    data[row][col] = value;
  }

  /**
   * Solve ... if the matrix is a square matrix the return value is the inverse
   * matrix
   * 
   * @return the matrix
   */
  public Matrix solve() {
    Matrix m = new Matrix(data);
    Matrix id = new Matrix(rows);
    id.flood(0.0);
    id.setDiagonal(1.0);
    m.concat(id);
    m.gaussJordan();

    Matrix result = m.getSubMatrix(0, cols, rows - 1, cols + rows - 1);
    return result;
  }

  /**
   * Subtracts the given matrix from this one ... <br>
   * A - B =<br>
   * a00-b00&nbsp;a01-b01&nbsp;a02-b02<br>
   * a10-b10&nbsp;a11-b11&nbsp;a12-b12<br>
   * a20-b20&nbsp;a21-b21&nbsp;a22-b22<br>
   * 
   * @param m
   *          matrix to be subtracted from this one
   * 
   * @exception MatrixException
   *              This method will throw a MatrixException if the two matrices
   *              are not compatible! I. e. if do not have the same number of
   *              rows and columns.
   */
  public void sub(Matrix m) {

    if ((rows != m.getRows()) || (cols != m.getColumns())) {
      throw new MatrixException(
          "Cannot subtract these two matrices \n Matrix A (" + rows + "x"
              + cols + ") Matrix B (" + m.getRows() + "x" + m.getColumns()
              + ")");
    }

    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        data[i][j] = data[i][j] - m.getElement(i, j);
      }
    }
  }

  /**
   * Subtracts the values of col2 from those of col1.
   * 
   * @param col1
   *          number of column the values from cal2 shall be subtracted from
   * @param col2
   *          number of column which contains the values to be subtracted from
   *          col1
   */
  public void subCols(int col1, int col2) {
    for (int i = 0; i < rows; i++) {
      data[i][col1] -= data[i][col2];
    }
  }

  /**
   * Subtracts the values of col2 multiplied with the given factor from those of
   * col1.
   * 
   * @param col1
   *          the col1
   * @param col2
   *          the col2
   * @param factor
   *          the factor
   */
  public void subCols(int col1, int col2, double factor) {
    for (int i = 0; i < rows; i++) {
      data[i][col1] -= data[i][col2] * factor;
    }
  }

  /**
   * Subtracts the values of row2 from those of row1.
   * 
   * @param row1
   *          the row1
   * @param row2
   *          the row2
   */
  public void subRows(int row1, int row2) {
    for (int i = 0; i < cols; i++) {
      data[row1][i] -= data[row2][i];
    }
  }

  /**
   * Subtracts the values of row2 multiplied with the given factor from those of
   * row1.
   * 
   * @param row1
   *          the row1
   * @param row2
   *          the row2
   * @param factor
   *          the factor
   */
  public void subRows(int row1, int row2, double factor) {
    for (int i = 0; i < cols; i++) {
      data[row1][i] -= data[row2][i] * factor;
    }
  }

  /**
   * Swaps the two rows.
   * 
   * @param row1
   *          first row to be swapped
   * @param row2
   *          second row to be swapped
   */
  public void swapRows(int row1, int row2) {
    for (int i = 0; i < cols; i++) {
      double help = data[row1][i];
      data[row1][i] = data[row2][i];
      data[row2][i] = help;
    }

  }

  @Override
  public String toString() {
    return Arrays.toString(data);
  }

  /**
   * Transpose (swap columns and rows) the matrix Note: This method doubles the
   * amount of memory needed for holding the matrix! The internal matrix is not
   * changed: the transposed matrix is returned as method result.
   * 
   * @return transposed matrix
   */
  public Matrix transpose() {
    double[][] data2 = new double[cols][rows];
    // copy data
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        data2[j][i] = data[i][j];
      }
    }
    return new Matrix(data2);
  }

}