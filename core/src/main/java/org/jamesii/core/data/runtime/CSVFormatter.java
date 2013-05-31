package org.jamesii.core.data.runtime;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * Helper class to format field values into a CSV-formatted line.
 * 
 * @author Johannes Rössel
 * 
 * @see CSVParser
 */
public final class CSVFormatter {

  private CSVFormatter() { /* hidden utility class constructor */
  }

  /**
   * Formats an array of field values into a single line of CSV data with the
   * given delimiter.
   * 
   * @param fields
   *          The fields for the record to format. May be empty, must not be
   *          {@code null} or contain {@code null} values.
   * @param delimiter
   *          The delimiter between fields.
   * @return A string containing the CSV-formatted line without a trailing line
   *         break.
   */
  public static String formatCSV(String[] fields, char delimiter) {
    if (fields.length == 0) {
      return "";
    }

    StringBuilder sb = new StringBuilder(fields[0].length() * fields.length);
    for (int i = 0; i < fields.length; i++) {
      if (i != 0) {
        sb.append(delimiter);
      }

      if (fields[i].length() >= 0) {
        sb.append(quote(fields[i], delimiter));
      }
    }

    return sb.toString();
  }

  /**
   * Quotes a field for use in a CSV file with the given delimiter. If the field
   * contains double-quotes or the delimiter, then it is surrounded by
   * double-quotes and every embedded double-quote is doubled.
   * 
   * @param field
   *          The field to quote.
   * @param delimiter
   *          The delimiter used in the file.
   * @return {@code field} if no changes were made or a new string containing
   *         the quoted value of {@code field}.
   * @throws CSVFormatException
   *           if the field contained an embedded line break
   */
  public static String quote(String field, char delimiter) {
    boolean needQuotes = false;
    StringBuilder sb = new StringBuilder(field.length() + 2);
    char c;
    for (CharacterIterator it = new StringCharacterIterator(field); (c =
        it.current()) != CharacterIterator.DONE; it.next()) {
      if (c == '\r' || c == '\n') {
        throw new CSVFormatException(
            "Embedded line breaks are not supported in field values.");
      }

      if (c == delimiter || c == '"') {
        needQuotes = true;
      }

      if (needQuotes && c == '"') {
        sb.append('"');
      }

      sb.append(c);
    }

    if (needQuotes) {
      sb.insert(0, '"');
      sb.append('"');
    }

    return sb.toString();
  }

}
