/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.parsetree.print.DefaultPrintManager;
import org.jamesii.core.math.parsetree.print.IPrintManager;
import org.jamesii.core.math.parsetree.variables.IDependancy;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * The Class Node. A node in a parse tree can be anything on which operations
 * might be executed.
 *
 * A node may have children or not, but a node can always be calculated (by
 * calculating its sub tree).
 *
 * @author Jan Himmelspach
 */
public abstract class Node implements INode, Cloneable {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 2561004396538702179L;

  /**
   * The print manager.
   */
  private transient IPrintManager printManager = new DefaultPrintManager();
  private boolean inToString = false;

  @Override
  public abstract <N extends INode> N calc(IEnvironment<?> cEnv);

  /**
   * Create an empty node.
   */
  public Node() {
    super();
  }

  /**
   * Create a node and set the print manager passed to print the content of the
   * node.
   *
   * @param manager
   */
  public Node(IPrintManager manager) {
    this();
    printManager = manager;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /**
   * Get the children of the node, if any.
   *
   * @return the children
   */
  @Override
  public List<? extends INode> getChildren() {
    return new ArrayList<>();
  }

  /**
   * Get all nodes depending on external knowledge. These nodes have to
   * implement the marker interface IDependancy.
   *
   * This method will recursively identify all dependencies in the parse tree,
   * given by the passed root node.
   *
   * @param node the node
   * @param toTime - 0 means current time, 1 means one step back in history (if
   * supported)
   *
   * @return the dependencies
   */
  public static List<INode> getDependencies(INode node, Integer toTime) {

    List<INode> result = new ArrayList<>();

    if (node == null) {
      return result;
    }

    if ((node instanceof IDependancy) && ((IDependancy) node).dependsOn(toTime)) {
      result.add(node);
    }

    if (node.getChildren() != null) {
      for (INode n : node.getChildren()) {
        result.addAll(Node.getDependencies(n, toTime));
      }
    }
    return result;
  }

  /**
   * Sets the prints the manager.
   *
   * @param printManager the new prints the manager
   */
  @Override
  public void setPrintManager(IPrintManager printManager) {
    this.printManager = printManager;
  }

  /**
   * Gets the name. Human readable name of the node. Can be, e.g., ADD, or +, or
   * ... Has to be unique overall existing node classes (or empty).
   *
   * @return the name
   */
  @Override
  public String getName() {
    return "";
  }

  @Override
  public String toString() {
    synchronized (this) {
      if (printManager != null && !inToString) {
        try {
          inToString = true;
          return printManager.toString(this);
        } finally {
          inToString = false;
        }
      }
    }
    return super.toString();
  }

}
