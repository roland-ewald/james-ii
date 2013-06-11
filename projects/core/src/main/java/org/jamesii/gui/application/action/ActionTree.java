/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import org.jamesii.gui.base.URLTreeModel;
import org.jamesii.gui.base.URLTreeNode;

/**
 * Tree structure that manages hierarchical {@link IAction}s.
 * 
 * @author Stefan Rybacki
 * @see ActionManager
 */
class ActionTree extends URLTreeModel<IAction> {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -560112718262004202L;

  /**
   * Creates a new instance of {@link ActionTree}
   */
  public ActionTree() {
    super(new URLTreeNode<IAction>("actiontree.root", null, null));
  }

}
