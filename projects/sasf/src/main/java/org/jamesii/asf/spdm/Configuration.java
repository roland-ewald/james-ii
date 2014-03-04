/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm;


import java.util.TreeMap;

import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * Represents parameter setup for a concrete simulation system configuration.
 * 
 * @author Roland Ewald
 * 
 */
public class Configuration extends TreeMap<String, Object> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7019299316455482458L;

  /** The corresponding selection tree. */
  private SelectionTree selectionTree;

  /**
   * Instantiates a new configuration.
   * 
   * @param selectionTree
   *          the selection tree
   */
  public Configuration(SelectionTree selectionTree) {
    if (selectionTree == null) {
      return;
    }
    this.selectionTree = selectionTree;
    SelectionTreeFlattener flattener =
        new SelectionTreeFlattener(selectionTree, this);
    flattener.flatten();
  }

  /**
   * Instantiates a new configuration. For beans compliance.
   */
  public Configuration() {
  }

  public SelectionTree getSelectionTree() {
    return selectionTree;
  }

  public void setSelectionTree(SelectionTree selectionTree) {
    this.selectionTree = selectionTree;
  }

}
