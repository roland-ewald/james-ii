/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;

/**
 * CSV reader, storing the information in a list.
 * 
 * @author Stefan Leye
 * @author Arne Bittig
 * 
 */
public class CSVReader {

  private String separator = ",";

  private String sepEscape = null;

  /**
   * Reads a .csv file and returns the data as an ArrayList (lines) containing
   * Arrays (the dates). Ignores the first line (which usually defines the
   * variable names).
   * 
   * @param filename
   *          the .csv file
   * @param removeHeader
   *          flag whether to skip the first line
   * @return the data
   */
  public List<String[]> read(String filename, boolean removeHeader) {
    try {
      try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        // retrieve variable names from first line
        String strLine = reader.readLine();
        // initialize data map
        List<String[]> data = new ArrayList<>();
        if (!removeHeader) {
          data.add(getSplitLine(strLine));
        }
        // iterate names
        while ((strLine = reader.readLine()) != null) {
          data.add(getSplitLine(strLine));
        }
        return data;
      }
    } catch (IOException e) {
      SimSystem.report(e);
    }
    return null;
  }

  private String[] getSplitLine(String strLine) {
    if (sepEscape == null || sepEscape.isEmpty()
        || strLine.indexOf(sepEscape) == -1) {
      return strLine.split(separator);
    }
    List<String> retVal = new ArrayList<>();
    int pos = 0;
    while (pos < strLine.length()) {
      int nextPos;
      if (strLine.startsWith(sepEscape, pos)) {
        pos += sepEscape.length();
        nextPos = strLine.indexOf(sepEscape, pos);
        retVal.add(strLine.substring(pos, nextPos));
        nextPos += sepEscape.length();
      } else {
        nextPos = strLine.indexOf(separator, pos);
        if (nextPos > 0) {
          retVal.add(strLine.substring(pos, nextPos));
        } else {
          retVal.add(strLine.substring(pos));
          nextPos = strLine.length();
        }
      }
      assert nextPos == strLine.length()
          || strLine.indexOf(separator, nextPos) == nextPos;
      pos = nextPos + separator.length();
    }
    return retVal.toArray(new String[retVal.size()]);
  }

  /**
   * Returns the separator.
   * 
   * @return the separator
   */
  public String getSeparator() {
    return separator;
  }

  /**
   * Change the separator.
   * 
   * @param separator
   *          the new separator
   */
  public void setSeparator(String separator) {
    this.separator = separator;
  }

  /**
   * Change the separator.
   * 
   * @param separator
   *          the new separator
   */
  public void setSeparator(char separator) {
    this.separator = separator + "";
  }

  /**
   * @return the separator escape character/sequence
   */
  public final String getSepEscape() {
    return sepEscape;
  }

  /**
   * @param sepEscape
   *          the separator escape character/sequence
   */
  public final void setSepEscape(String sepEscape) {
    this.sepEscape = sepEscape;
  }
}
