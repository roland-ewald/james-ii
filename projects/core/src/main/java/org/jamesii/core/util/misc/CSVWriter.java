/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility to write CSV files. To avoid code duplication, 2D arrays of
 * primitives are converted to objects (e.g. <code>double[][]</code> to
 * <code>Double[][]</code>), so writing large data sets of primitives could
 * suffer a slowdown.
 * 
 * @see CSVReader
 * 
 * @author Roland Ewald
 * 
 */
public final class CSVWriter {

  /** The default delimiter. */
  public static final char DEFAULT_DELIMITER = ',';

  /**
   * Hidden constructor.
   */
  private CSVWriter() {
  }

  /**
   * Write result.
   * 
   * @param <X>
   *          the type of the results
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static <X> void writeResult(X[][] result, String fileName)
      throws IOException {
    writeResult(result, fileName, DEFAULT_DELIMITER);
  }

  /**
   * Write result. In case the first row is shorter than some other (in terms of
   * data elements separated by {@link CSVWriter#DEFAULT_DELIMITER}, not
   * character count!), it will be prolonged by adding a corresponding number of
   * 
   * @param <X>
   *          the generic type
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   *           {@link CSVWriter#DEFAULT_DELIMITER} at the end.
   */
  public static <X> void writeResultWithLongFirstRow(X[][] result,
      String fileName) throws IOException {
    writeResult(result, fileName, DEFAULT_DELIMITER, true);
  }

  /**
   * Write result.
   * 
   * @param <X>
   *          the type of the results
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the separator
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static <X> void writeResult(X[][] result, String fileName,
      char delimiter) throws IOException {
    writeResult(result, fileName, delimiter, false);
  }

  /**
   * Write result.
   * 
   * @param <X>
   *          the type of the results
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the separator
   * @param addDelimsToFirstRow
   *          the flag to adjust the first row (adding delimiters so that it is
   *          as large as the longest row)
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static <X> void writeResult(X[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      fw.append(toCSV(new ObjectMatrix<>(result), delimiter,
          addDelimsToFirstRow));
    }
  }

  /**
   * Write result.
   * 
   * @param <X>
   *          the type of the results
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the delimiter
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static <X> void writeResult(X[] result, String fileName, char delimiter)
      throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      fw.append(toCSV(result, delimiter));
    }
  }

  /**
   * Export to CSV.
   * 
   * @param <X>
   *          the generic type
   * @param matrix
   *          the matrix
   * @param delim
   *          the delimiter
   * @return the string builder containing the data
   */
  public static <X> StringBuilder toCSV(X[][] matrix, char delim) {
    return toCSV(new ObjectMatrix<>(matrix), delim, false);
  }

  /**
   * Export to CSV.
   * 
   * @param <X>
   *          the generic type
   * @param matrix
   *          the matrix
   * @param delim
   *          the delimiter
   * @param addDelimsToFirstRow
   *          if true and there is at least one row that is longer than the
   *          first one, a corresponding number of additional delimiters is
   *          appended to first row
   * @return the string builder containing the data
   */
  public static <X> StringBuilder toCSV(MatrixWrapper<X> matrix, char delim,
      boolean addDelimsToFirstRow) {
    if (matrix == null) {
      return new StringBuilder("null");
    }
    StringBuilder matrixString = new StringBuilder();
    for (int i = 0; i < matrix.length(); i++) {
      int rowLength = matrix.length(i);
      for (int j = 0; j < rowLength; j++) {
        X element = matrix.element(i, j);
        matrixString.append(element == null ? "" : element.toString());
        if (j < rowLength - 1) {
          matrixString.append(delim);
        }
      }
      if (i == 0 && addDelimsToFirstRow) {
        matrixString.append(createAdditionalDelimiters(matrix, delim));
      }
      matrixString.append('\n');
    }
    return matrixString;
  }

  /**
   * Creates the additional delimiters that should be added to the first row.
   * Adding such delimiters may facilitate interaction with tools like R and its
   * <code>read.csv()</code> function, as these often use the first rows to
   * infer the number of variables.
   * 
   * @param <X>
   *          the generic type
   * @param matrix
   *          the matrix
   * @param delim
   *          the delimiter to be used
   * @return the string
   */
  private static <X> String createAdditionalDelimiters(MatrixWrapper<X> matrix,
      char delim) {
    int maxRowLength = 0;
    for (int i = 0; i < matrix.length(); i++) {
      maxRowLength = Math.max(maxRowLength, matrix.length(i));
    }
    return Strings.copyChar(delim, maxRowLength - matrix.length(0));
  }

  /**
   * Export to CSV. If you want a column instead of a row vector, just choose
   * '\n' as delimiter.
   * 
   * @param <X>
   *          the generic type
   * @param array
   *          the array
   * @param delim
   *          the delimiter
   * @return the string builder
   */
  public static <X> StringBuilder toCSV(X[] array, char delim) {
    if (array == null) {
      return new StringBuilder("null");
    }
    StringBuilder matrixString = new StringBuilder();
    for (int i = 0; i < array.length; i++) {
      matrixString.append(array[i] == null ? "" : array[i].toString());
      // Add delimiter
      if (i < array.length - 1) {
        matrixString.append(delim);
      } else if (delim != '\n') {
        matrixString.append('\n');
      }
    }
    return matrixString;
  }

  /**
   * Export to CSV.
   * 
   * @param <X>
   *          the generic type
   * @param list
   *          the list
   * @param delim
   *          the delimiter
   * @return the string builder
   */
  public static <X> StringBuilder toCSV(List<X> list, char delim) {
    if (list == null) {
      return new StringBuilder("null");
    }
    StringBuilder matrixString = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      matrixString.append(list.get(i) == null ? "" : list.get(i).toString());
      if (i < list.size() - 1) {
        matrixString.append(delim);
      }
      matrixString.append('\n');
    }
    return matrixString;
  }

  // Special functions for primitive types:

  /**
   * Write result.
   * 
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the delimiter
   * @param addDelimsToFirstRow
   *          the add delims to first row
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void writeResult(double[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      fw.append(toCSV(new DoublePrimitiveMatrix(result), delimiter,
          addDelimsToFirstRow));
    }
  }

  /**
   * Write result.
   * 
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the delimiter
   * @param addDelimsToFirstRow
   *          the add delims to first row
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void writeResult(float[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      fw.append(toCSV(new FloatPrimitiveMatrix(result), delimiter,
          addDelimsToFirstRow));
    }
  }

  /**
   * Write result.
   * 
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the delimiter
   * @param addDelimsToFirstRow
   *          the add delims to first row
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void writeResult(boolean[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      fw.append(toCSV(new BooleanPrimitiveMatrix(result), delimiter,
          addDelimsToFirstRow));
    }
  }

  /**
   * Write result.
   * 
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the delimiter
   * @param addDelimsToFirstRow
   *          the add delims to first row
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void writeResult(char[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      fw.append(toCSV(new CharPrimitiveMatrix(result), delimiter,
          addDelimsToFirstRow));
    }
  }

  /**
   * Write result.
   * 
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the delimiter
   * @param addDelimsToFirstRow
   *          the add delims to first row
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void writeResult(byte[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      fw.append(toCSV(new BytePrimitiveMatrix(result), delimiter,
          addDelimsToFirstRow));
    }
  }

  /**
   * Write result.
   * 
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the delimiter
   * @param addDelimsToFirstRow
   *          the add delims to first row
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void writeResult(short[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      fw.append(toCSV(new ShortPrimitiveMatrix(result), delimiter,
          addDelimsToFirstRow));
    }
  }

  /**
   * Write result.
   * 
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the delimiter
   * @param addDelimsToFirstRow
   *          the add delims to first row
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void writeResult(int[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      fw.append(toCSV(new IntPrimitiveMatrix(result), delimiter,
          addDelimsToFirstRow));
    }
  }

  /**
   * Write result.
   * 
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @param delimiter
   *          the delimiter
   * @param addDelimsToFirstRow
   *          the add delims to first row
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void writeResult(long[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      fw.append(toCSV(new LongPrimitiveMatrix(result), delimiter,
          addDelimsToFirstRow));
    }
  }
}

/**
 * Simple interface to abstract away the distinction between 2D arrays of
 * primitives (e.g. double[][]) and 2D arrays of ordinary objects (e.g.
 * Double[][], String[][]).
 * 
 * @param <X>
 *          the type of the stored objects
 */
interface MatrixWrapper<X> {

  /**
   * Get number of rows.
   * 
   * @return number of rows.
   */
  int length();

  /**
   * Get length of row.
   * 
   * @param row
   *          the row
   * @return number of columns in row
   */
  int length(int row);

  /**
   * Get element.
   * 
   * @param row
   *          the row number
   * @param col
   *          the column number
   * @return the element
   */
  X element(int row, int col);
}

/**
 * Wrapper for 'normal' 2D arrays consisting of objects.
 */
class ObjectMatrix<X> implements MatrixWrapper<X> {

  private final X[][] matrix;

  ObjectMatrix(X[][] matrix) {
    this.matrix = matrix;
  }

  @Override
  public int length() {
    return matrix.length;
  }

  @Override
  public int length(int row) {
    return matrix[row].length;
  }

  @Override
  public X element(int row, int col) {
    return matrix[row][col];
  }

}

/**
 * Wrapper for double[][].
 */
class DoublePrimitiveMatrix implements MatrixWrapper<Double> {

  private final double[][] matrix;

  DoublePrimitiveMatrix(double[][] matrix) {
    this.matrix = matrix;
  }

  @Override
  public int length() {
    return matrix.length;
  }

  @Override
  public int length(int row) {
    return matrix[row].length;
  }

  @Override
  public Double element(int row, int col) {
    return matrix[row][col];
  }

}

/**
 * Wrapper for float[][].
 */
class FloatPrimitiveMatrix implements MatrixWrapper<Float> {

  private final float[][] matrix;

  FloatPrimitiveMatrix(float[][] matrix) {
    this.matrix = matrix;
  }

  @Override
  public int length() {
    return matrix.length;
  }

  @Override
  public int length(int row) {
    return matrix[row].length;
  }

  @Override
  public Float element(int row, int col) {
    return matrix[row][col];
  }

}

/**
 * Wrapper for boolean[][].
 */
class BooleanPrimitiveMatrix implements MatrixWrapper<Boolean> {

  private final boolean[][] matrix;

  BooleanPrimitiveMatrix(boolean[][] matrix) {
    this.matrix = matrix;
  }

  @Override
  public int length() {
    return matrix.length;
  }

  @Override
  public int length(int row) {
    return matrix[row].length;
  }

  @Override
  public Boolean element(int row, int col) {
    return matrix[row][col];
  }

}

/**
 * Wrapper for char[][].
 */
class CharPrimitiveMatrix implements MatrixWrapper<Character> {

  private final char[][] matrix;

  CharPrimitiveMatrix(char[][] matrix) {
    this.matrix = matrix;
  }

  @Override
  public int length() {
    return matrix.length;
  }

  @Override
  public int length(int row) {
    return matrix[row].length;
  }

  @Override
  public Character element(int row, int col) {
    return matrix[row][col];
  }

}

/**
 * Wrapper for byte[][].
 */
class BytePrimitiveMatrix implements MatrixWrapper<Byte> {

  private final byte[][] matrix;

  BytePrimitiveMatrix(byte[][] matrix) {
    this.matrix = matrix;
  }

  @Override
  public int length() {
    return matrix.length;
  }

  @Override
  public int length(int row) {
    return matrix[row].length;
  }

  @Override
  public Byte element(int row, int col) {
    return matrix[row][col];
  }

}

/**
 * Wrapper for short[][].
 */
class ShortPrimitiveMatrix implements MatrixWrapper<Short> {

  private final short[][] matrix;

  ShortPrimitiveMatrix(short[][] matrix) {
    this.matrix = matrix;
  }

  @Override
  public int length() {
    return matrix.length;
  }

  @Override
  public int length(int row) {
    return matrix[row].length;
  }

  @Override
  public Short element(int row, int col) {
    return matrix[row][col];
  }

}

/**
 * Wrapper for int[][].
 */
class IntPrimitiveMatrix implements MatrixWrapper<Integer> {

  private final int[][] matrix;

  IntPrimitiveMatrix(int[][] matrix) {
    this.matrix = matrix;
  }

  @Override
  public int length() {
    return matrix.length;
  }

  @Override
  public int length(int row) {
    return matrix[row].length;
  }

  @Override
  public Integer element(int row, int col) {
    return matrix[row][col];
  }

}

/**
 * Wrapper for long[][].
 */
class LongPrimitiveMatrix implements MatrixWrapper<Long> {

  private final long[][] matrix;

  LongPrimitiveMatrix(long[][] matrix) {
    this.matrix = matrix;
  }

  @Override
  public int length() {
    return matrix.length;
  }

  @Override
  public int length(int row) {
    return matrix[row].length;
  }

  @Override
  public Long element(int row, int col) {
    return matrix[row][col];
  }

}