/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;

/**
 * Static methods aiding csv data writing
 *
 * @author Arne Bittig
 */
public final class CSVUtils {

  /**
   * String to replace not-a-numbers with (null for leaving what NumberFormat
   * produces)
   */
  private static final String NAN_REPLACEMENT = "NaN";

  /**
   * String to replace infinity with (null for leaving what NumberFormat
   * produces)
   */
  private static final String INF_REPLACEMENT = "Inf";

  private CSVUtils() {
  }

  /**
   * Convenience class encapsulating the separator and number formatter used
   * when writing a csv file
   *
   * @author Arne Bittig
   * @date 01.03.2013
   */
  public static class CSVWriter {
    private NumberFormat nf;

    private char csvSep;

    /**
     * @param csvSep
     *          csv separator
     * @param nf
     *          Number format
     */
    public CSVWriter(char csvSep, NumberFormat nf) {
      this.csvSep = csvSep;
      this.nf = nf;
    }

    /**
     * Write given data to given file
     *
     * @param columnData
     *          column title-> column data map
     * @param filename
     *          Name of file to write to
     * @return success value (exception is logged if false)
     */
    public boolean write(Map<String, ? extends Collection<?>> columnData,
        String filename) {
      return writeCsvData(filename, columnData, csvSep, nf);
    }

    public NumberFormat getNumberFormat() {
      return nf;
    }

    public void setNumberFormat(NumberFormat nf) {
      this.nf = nf;
    }

    public char getCsvSep() {
      return csvSep;
    }

    public void setCsvSep(char csvSep) {
      this.csvSep = csvSep;
    }
  }

  /**
   * Write given columns (stored in headline->list of entries map) to csv file.
   * Supports "collapsed" columns where entries are null or strings and contain
   * several actual entries separated by given csv separator. The respective
   * column heading must contain the csv separator the same number of times.
   * Null entries are replaced by the appropriate number of empty strings.
   *
   * @param fileName
   *          Path and name of file
   * @param columns
   *          Column data
   * @param csvSep
   *          Separator for csv file
   * @param nf
   *          Formatter numerical column entries (may be null if none present)
   * @return true if writing completed without {@link IOException}
   */
  public static boolean writeCsvData(String fileName,
      Map<String, ? extends Collection<?>> columns, char csvSep,
      NumberFormat nf) {
    String[][] csvData =
        columnsToStringMatrix(columns.keySet(), columns.values(), csvSep, nf);

    try {
      writeResult(csvData, fileName, csvSep);
      return true;
    } catch (IOException e) {
      SimSystem.report(Level.SEVERE, "Could not write to file " + fileName, e);
      return false;
    }

  }

  /**
   * @param csvData
   * @param fileName
   * @param csvSep
   */
  private static void writeResult(String[][] csvData, String fileName,
      char csvSep) throws IOException {
    try (FileWriter fw = new FileWriter(fileName)) {
      for (String[] element : csvData) {
        StringBuilder rowString = new StringBuilder();
        int maxIndex = element.length - 1;
        for (int j = 0; j <= maxIndex; j++) {
          rowString.append(element[j]);
          if (j < maxIndex) {
            rowString.append(csvSep);
          }
        }
        rowString.append('\n');
        fw.append(rowString);
      }

    }
  }

  /**
   * Convert headlines and columns to a String matrix fit for writing to a csv
   * file, formatting number columns in a given way
   *
   * The returned array will contain the n-th heading at position [0][n], the
   * first entry of the n-th column at [1][n],... . Each object's toString
   * method is used for non-null. If the first non-null entry in a column is a
   * {@link Number} and the given {@link NumberFormat} parameter is not null,
   * the latter is used on all entries of the column instead of toString().
   *
   * The headings and columns parameters accept {@link Collection}s instead of
   * {@link List}s to make them usable with {@link Map#keySet()} and
   * {@link Map#values()} of Map implementations with guaranteed iteration order
   * (e.g. LinkedHashMap). If headings contains fewer elements than columns, the
   * later columns are ignored, if it contains more, a
   * {@link java.util.NoSuchElementException} will probably be thrown.
   *
   * @param headings
   *          Headers for the columns
   * @param columns
   *          Columns as lists
   * @param nf
   *          NumberFormat used on columns containing numbers (if null, toString
   *          is used)
   * @param csvSep
   *          csv separation character
   * @return 2D string array compatible with {@link import
   *         org.jamesii.core.util.misc.CSVWriter#writeResult(Object[][],
   *         String)}
   */
  private static String[][] columnsToStringMatrix(Collection<String> headings,
      Collection<? extends Collection<?>> columns, char csvSep,
      NumberFormat nf) {
    if (headings.size() != columns.size()) {
      throw new IllegalArgumentException(
          "Number of headings (" + headings.size()
              + ") must equal number of columns (" + columns.size() + ")");
    }

    int nCols = headings.size();
    int nRowsMax = 0; // not counting headline
    for (Collection<?> column : columns) {
      int nRows = column.size();
      if (nRowsMax < nRows) {
        nRowsMax = nRows;
      }
    }

    String[][] t = new String[nRowsMax + 1][nCols];
    Iterator<String> headIt = headings.iterator();
    Iterator<? extends Collection<?>> colIt = columns.iterator();
    StringBuffer csvSepSeq = new StringBuffer().append(csvSep);
    for (int iCol = 0; iCol < nCols; iCol++) {
      List<String> colStr =
          col2str(headIt.next(), colIt.next(), nRowsMax + 1, csvSepSeq, nf);
      int iRow = 0;
      for (String entry : colStr) {
        t[iRow++][iCol] = entry;
      }
    }
    return t;
  }

  private static List<String> col2str(String head, Collection<?> col,
      int fillUpTo, CharSequence csvSep, NumberFormat nf) {
    List<String> rv = Arrays.asList(new String[fillUpTo]);
    rv.set(0, head);
    String defaultFiller = getFiller(head, csvSep);
    int i;
    if (nf != null && containsNumbers(col)) {
      assert defaultFiller.isEmpty();
      i = fillNumberCol(col, nf, rv, csvSep);
    } else {
      i = fillObjectCol(col, defaultFiller, rv, csvSep);
    }
    while (i < rv.size()) {
      rv.set(i++, defaultFiller);
    }
    return rv;
  }

  /**
   * Fill column data into given list (starting at position 1, after the place
   * for a header), replacing null values with the fiven "filler".
   *
   * @param col
   * @param filler
   * @param fillInto
   * @return index after last filled element
   */
  private static int fillObjectCol(Collection<?> col, String filler,
      List<String> fillInto, CharSequence csvSep) {
    int i = 1;
    for (Object o : col) {
      if (o == null) {
        fillInto.set(i, filler);
      } else {
        fillInto.set(i, sanitize(o.toString(), csvSep));
      }
      i++;
    }
    return i;
  }

  /**
   * @param col
   * @param nf
   * @param fillInto
   * @return index after last filled element
   */
  private static int fillNumberCol(Collection<?> col, NumberFormat nf,
      List<String> fillInto, CharSequence csvSep) {
    int i = 0;
    for (Object n : col) {
      i++;
      String str = n == null ? "" : null;

      if (str == null) {
        str = intStr(n);
      }
      if (str == null) {
        str = infOrNaNStr(n);
      }
      if (str == null) {
        str = nf.format(n);
      }
      str = sanitize(str, csvSep);
      fillInto.set(i, str);
    }
    return i + 1;
  }

  /**
   * @param str
   * @param csvSep
   * @return
   */
  private static String sanitize(String str, CharSequence csvSep) {
    if (str.contains(csvSep)) {
      return "\"" + str + "\"";
    } else {
      return str;
    }

  }

  /**
   * @param n
   * @return
   */
  private static String intStr(Object o) {
    if (o instanceof Number) {
      Number n = (Number) o;
      if (n.longValue() == n.doubleValue()) {
        return n.toString();
      }
    }
    return null;
  }

  /**
   * @param n
   * @return
   */
  private static String infOrNaNStr(Object n) {
    if (n instanceof Double || n instanceof Float) {
      double d = ((Number) n).doubleValue();
      if (Double.isNaN(d) && NAN_REPLACEMENT != null) {
        return NAN_REPLACEMENT;
      }
      if (Double.isInfinite(d) && INF_REPLACEMENT != null) {
        return INF_REPLACEMENT;
      }
    }
    return null;
  }

  /**
   * @param head
   * @return
   */
  static String getFiller(String head, CharSequence csvSep) {
    if (head == null || head.isEmpty()) {
      return "";
    }
    StringBuilder fillBuilder = new StringBuilder();
    for (int i = 0; i < head.length(); i++) {
      if (head.subSequence(i, i + csvSep.length()).equals(csvSep)) {
        fillBuilder.append(csvSep);
      }
    }
    return fillBuilder.toString();
  }

  /**
   * Check whether a list of Objects' first non-null entry is an instance of
   * {@link Number}
   *
   * @param list
   * @return true if the first non-null entry is a number
   */
  private static boolean containsNumbers(Collection<?> list) {
    for (Object entry : list) {
      if (entry != null) {
        return entry instanceof Number;
      }
    }
    return false;
  }

}
