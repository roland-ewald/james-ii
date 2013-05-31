/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * Class defining some constants for placement within {@link URLTreeNode}s plus
 * it is used to define an actual placement for an {@link URLTreeNode} within
 * another {@link URLTreeNode}.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class URLTreeNodePlacement {
  /**
   * flag specifying the placement to be after another specified node
   */
  public static final String AFTER = "AFTER";

  /**
   * flag specifying the placement to be before another specified node
   */
  public static final String BEFORE = "BEFORE";

  /**
   * flag specifying the placement to be added to the beginning of the node
   */
  public static final String START = "START";

  /**
   * flag specifying the placement to be added to the end of the node
   */
  public static final String END = "END";

  /**
   * flag specifying the placement to be added to the very end after all END
   * placed placements
   */
  public static final String LAST = "LAST";

  /**
   * flag specifying the placement to be added to the very front before all
   * START placements
   */
  public static final String FIRST = "FIRST";

  /**
   * holds the placement id (AFTER or BEFORE or ...)
   */
  private String where;

  /**
   * holds the id of the node this node should be placed BEFORE/AFTER and so on
   */
  private String nodeId = null;

  /**
   * Creates a placement from the given placement id ({@code where}) and a
   * relative node id
   * 
   * @param where
   *          one of the valid placement ids (BEFORE, AFTER, ...)
   * @param nodeId
   *          id of another node that this node is to be placed relative to
   */
  public URLTreeNodePlacement(String where, String nodeId) {
    if (!isPlacement(where)) {
      throw new IllegalArgumentException("Specified value for where not valid!");
    }

    this.where = where.toUpperCase();
    this.nodeId = nodeId;
  }

  /**
   * @return the where
   */
  public synchronized String getWhere() {
    return where;
  }

  /**
   * @return the actionId
   */
  public synchronized String getNodeId() {
    return nodeId;
  }

  /**
   * @param w
   *          the placement identifier to check for know placement
   * @return true if {@code w} is a valid placement id
   */
  public static boolean isPlacement(String w) {
    return (w.equalsIgnoreCase(AFTER) || w.equalsIgnoreCase(BEFORE)
        || w.equalsIgnoreCase(START) || w.equalsIgnoreCase(END)
        || w.equalsIgnoreCase(LAST) || w.equalsIgnoreCase(FIRST));
  }

  @Override
  public String toString() {
    return String.format("%s %s", where,
        nodeId != null ? String.valueOf(nodeId) : "");
  }

  /**
   * @return true if placement id is END
   */
  public boolean isEnd() {
    return where.equalsIgnoreCase(END);
  }

  /**
   * @return true if placement id is LAST
   */
  public boolean isLast() {
    return where.equalsIgnoreCase(LAST);
  }

  /**
   * @return true if placement id is AFTER
   */
  public boolean isAfter() {
    return where.equalsIgnoreCase(AFTER);
  }

  /**
   * @return true if placement id is FIRST
   */
  public boolean isFirst() {
    return where.equalsIgnoreCase(FIRST);
  }

  /**
   * @return true if placement id is BEFORE
   */
  public boolean isBefore() {
    return where.equalsIgnoreCase(BEFORE);
  }

  /**
   * @return true if placement id is START
   */
  public boolean isStart() {
    return where.equalsIgnoreCase(START);
  }

  /**
   * Static helper function that can extract a placement modifier from a given
   * {@link URLTreeNodeURL}
   * 
   * @param url
   *          an {@link URLTreeNodeURL} valid string
   * @return the extracted placement
   */
  public static URLTreeNodePlacement createFromURLTreeNodeURL(String url) {
    if (url == null) {
      return null;
    }

    // parse placement
    try {
      URLTreeNodeURL u = new URLTreeNodeURL(url);
      return u.getPlacement();
    } catch (UnsupportedEncodingException | MalformedURLException e) {
    }
    return null;
  }

}
