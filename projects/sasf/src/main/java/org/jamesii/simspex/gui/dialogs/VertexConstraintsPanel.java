/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui.dialogs;


import javax.swing.JPanel;

import org.jamesii.perfdb.recording.selectiontrees.SelTreeSetVertex;

/**
 * Super class for panels to edit constraints regarding {@link SelTreeSetVertex}
 * instances.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class VertexConstraintsPanel<S extends SelTreeSetVertex>
    extends JPanel {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8297464731024479609L;

  /** The current vertex. */
  private final S vertex;

  /**
   * Default constructor.
   * 
   * @param givenVertex
   *          the vertex of which the constraints shall be edited
   */
  public VertexConstraintsPanel(S givenVertex) {
    vertex = givenVertex;
  }

  /**
   * Trigger to save the current setting.
   */
  public abstract void save();

  public S getVertex() {
    return vertex;
  }

}
