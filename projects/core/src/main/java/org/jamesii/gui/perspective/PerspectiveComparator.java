/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Simple comparator for {@link IPerspective}s.
 * 
 * @author Stefan Rybacki
 */
public class PerspectiveComparator implements Comparator<IPerspective>,
    Serializable {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 8817534732237223959L;

  @Override
  public int compare(IPerspective o1, IPerspective o2) {
    if (o1 == o2) {
      return 0;
    }
    if (o1 == null) {
      return -1;
    }
    if (o2 == null) {
      return 1;
    }
    return (o1.getName().compareTo(o2.getName()));
  }
}
