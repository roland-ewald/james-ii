/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server.view.util;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The Class UserMutableTreeNode.
 */
public class UserMutableTreeNode extends DefaultMutableTreeNode {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2790422525151647711L;

  /**
   * Instantiates a new user mutable tree node.
   * 
   * @param userObject
   *          the user object
   */
  public UserMutableTreeNode(Object userObject) {
    super(userObject);
  }

  @Override
  public Object getUserObject() {
    return userObject;
  }
}
