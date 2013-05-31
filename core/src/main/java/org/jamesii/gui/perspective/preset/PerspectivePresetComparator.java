/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.preset;

import java.io.Serializable;
import java.util.Comparator;

import org.jamesii.gui.perspective.PerspectivePreset;

/**
 * Simple comparator for {@link PerspectivePreset}s.
 * 
 * @author Stefan Rybacki
 */
public class PerspectivePresetComparator implements
    Comparator<PerspectivePreset>, Serializable {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -7474146685940081811L;

  @Override
  public int compare(PerspectivePreset o1, PerspectivePreset o2) {
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
