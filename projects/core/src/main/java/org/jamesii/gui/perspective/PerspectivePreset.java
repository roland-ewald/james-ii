/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a preset of active {@link IPerspective}s. It is used to
 * group perspectives so that is easier to activate/deactivate groups of
 * perspectives.
 * 
 * @author Stefan Rybacki
 * 
 */
public class PerspectivePreset implements Serializable {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 6794343852082307681L;

  /**
   * list of class names of active presets
   */
  private final transient List<String> perspectives = new ArrayList<>();

  /**
   * name of preset
   */
  private String name;

  /**
   * Creates a new preset with the specified name using the given perspectives.
   * 
   * @param name
   *          the preset's name
   * @param perspectives
   *          the perspectives of the preset
   */
  public PerspectivePreset(String name, IPerspective... perspectives) {
    this.name = name;
    setPerspectives(perspectives);
  }

  /**
   * Creates a new preset with the specified name and an empty list of active
   * perspectives.
   * 
   * @param name
   *          the preset's name
   */
  public PerspectivePreset(String name) {
    this(name, (IPerspective[]) null);
  }

  /**
   * Creates a new preset with no name and no attached perspectives
   */
  public PerspectivePreset() {
    this(null, (IPerspective[]) null);
  }

  /**
   * Sets the name of the preset
   * 
   * @param n
   */
  public void setName(String n) {
    this.name = n;
  }

  /**
   * @return the preset's name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the perspectives that belong to this preset. Note: previously added
   * perspectives are deleted
   * 
   * @see #addPerspective(IPerspective)
   * @param persp
   *          an array of {@link IPerspective}s
   */
  public void setPerspectives(IPerspective... persp) {
    perspectives.clear();
    if (persp == null) {
      return;
    }
    for (IPerspective p : persp) {
      addPerspective(p);
    }
  }

  /**
   * Adds a perspective to the list of perspectives that belong to this preset
   * 
   * @param persp
   *          the perspective to add
   */
  public void addPerspective(IPerspective persp) {
    if (persp != null && !perspectives.contains(persp.getClass().getName())) {
      perspectives.add(persp.getClass().getName());
    }
  }

  /**
   * Removes a previously added perspective
   * 
   * @param persp
   *          the perspective to remove
   */
  public void removePerspective(IPerspective persp) {
    perspectives.remove(persp.getClass().getName());
  }

  /**
   * @param persp
   *          the perspective that is checked if in preset
   * @return true if perspective belongs to preset
   */
  public boolean isInPreset(IPerspective persp) {
    return perspectives.contains(persp.getClass().getName());
  }

  @Override
  public String toString() {
    return name;
  }

  /**
   * Function needed by the XMLEncoder for serialization.<br/>
   * <b>Do not use this method</b>
   * 
   * @return a list of class names of perspectives
   */
  public String[] getPerspectives() {
    return perspectives.toArray(new String[perspectives.size()]);
  }

  /**
   * Function needed by the XMLEncoder for deserialization.<br/>
   * <b>Do not use this method</b>
   * 
   * @param p
   */
  public void setPerspectives(String[] p) {
    perspectives.clear();
    for (String s : p) {
      perspectives.add(s);
    }
  }
}
