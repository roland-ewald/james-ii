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
  public static <X> void appendResult(X[][] result, String fileName)
      throws IOException {
    appendResult(result, fileName, DEFAULT_DELIMITER);
  }

  /**
   * Write result. In case the first row is shorter than some other (in terms of
   * data elements separated by {@link CSVWriter#DEFAULT_DELIMITER}, not
   * character count!), it will be prolonged by adding a corresponding number of
   * {@link CSVWriter#DEFAULT_DELIMITER} at the end.
   * 
   * @param <X>
   *          the generic type
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static <X> void writeResultWithLongFirstRow(X[][] result,
      String fileName) throws IOException {
    writeResult(result, fileName, DEFAULT_DELIMITER, true);
  }

  /**
   * Write result. In case the first row is shorter than some other (in terms of
   * data elements separated by {@link CSVWriter#DEFAULT_DELIMITER}, not
   * character count!), it will be prolonged by adding a corresponding number of
   * {@link CSVWriter#DEFAULT_DELIMITER} at the end.
   * 
   * @param <X>
   *          the generic type
   * @param result
   *          the result
   * @param fileName
   *          the file name
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static <X> void appendResultWithLongFirstRow(X[][] result,
      String fileName) throws IOException {
    appendResult(result, fileName, DEFAULT_DELIMITER, true);
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
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static <X> void writeResult(X[] result, String fileName)
      throws IOException {
    writeResult(result, fileName, DEFAULT_DELIMITER);
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
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static <X> void appendResult(X[] result, String fileName)
      throws IOException {
    appendResult(result, fileName, DEFAULT_DELIMITER);
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
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static <X> void appendResult(X[][] result, String fileName,
      char delimiter) throws IOException {
    appendResult(result, fileName, delimiter, false);
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
      fw.append(toCSV(result, delimiter, addDelimsToFirstRow));
    }
  }

  public static void writeResult(double[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) {
//    fw.append(toCSV(row, delimiter, addDelimsToFirstRow)); //TODO
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
  public static <X> void appendResult(X[][] result, String fileName,
      char delimiter, boolean addDelimsToFirstRow) throws IOException {
    try (FileWriter fw = new FileWriter(fileName, true)) {
      fw.append(toCSV(result, delimiter, addDelimsToFirstRow));
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
  public static <X> void appendResult(X[] result, String fileName,
      char delimiter) throws IOException {
    try (FileWriter fw = new FileWriter(fileName, true)) {
      fw.append(toCSV(result, delimiter));
    }
  }

  /**
   * Export to CSV.
   * 
   * @param matrix
   *          the matrix
   * @param delim
   *          the delimiter
   * 
   * @return the string builder containing the data
   */
  public static <X> StringBuilder toCSV(X[][] matrix, char delim) {
    return toCSV(matrix, delim, false);
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
  public static <X> StringBuilder toCSV(X[][] matrix, char delim,
      boolean addDelimsToFirstRow) {
    if (matrix == null) {
      return new StringBuilder("null");
    }
    StringBuilder matrixString = new StringBuilder();
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        matrixString
            .append(matrix[i][j] == null ? "" : matrix[i][j].toString());
        if (j < matrix[i].length - 1) {
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
  private static <X> String createAdditionalDelimiters(X[][] matrix, char delim) {
    int maxRowLength = 0;
    for (X[] row : matrix) {
      maxRowLength = Math.max(maxRowLength, row.length);
    }
    return Strings.copyChar(delim, maxRowLength - matrix[0].length);
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

  private interface MatrixWrapper<X> {
    int length();

    int length(int row);

    X content(int row, int col);
  }

  private class ObjectMatrix<X> implements MatrixWrapper<X> {

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
    public X content(int row, int col) {
      return matrix[row][col];
    }

  }

  private class DoublePrimitiveMatrix implements MatrixWrapper<Double> {

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
    public Double content(int row, int col) {
      return matrix[row][col];
    }

  }

}
