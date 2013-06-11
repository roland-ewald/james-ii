package org.jamesii.core.util.mosan;

import java.util.Collection;
import java.util.List;

/**
 * Some utility functions for the Mosan export.
 * 
 * @author Roland Ewald
 * @author Jan Himmelspach
 * 
 */
public final class MosanExportUtils {

  /** The line separator. */
  public static final String NL = System.getProperty("line.separator");

  /**
   * Hidden constructor.
   */
  private MosanExportUtils() {
  }

  /**
   * Write the file header.
   */
  public static String createHeader(Collection<Integer> idCollection) {
    StringBuilder builder = new StringBuilder();
    builder.append("index, time");
    for (Integer id : idCollection) {
      builder.append(", " + id);
    }

    builder.append(", eventid");
    builder.append(NL);

    return builder.toString();
  }

  /**
   * Creates a line for the result file.
   * 
   * @param line
   *          the line number
   * @param time
   *          the current time
   * @param currentState
   *          the current state (has to be in correct order!)
   * @param eventID
   *          the event id
   * @return the string containing the full line
   */
  public static String createLine(int line, double time,
      List<Long> currentState, int eventID) {

    StringBuilder builder = new StringBuilder();
    builder.append(line + ", " + time);

    for (Long amount : currentState) {
      builder.append(", " + amount);
    }

    builder.append(", " + eventID + MosanExportUtils.NL);

    return builder.toString();
  }
}
