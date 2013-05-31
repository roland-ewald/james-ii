/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree;

import java.io.Serializable;
import java.util.List;

import org.jamesii.core.math.parsetree.print.IPrintManager;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * The Interface INode.
 * 
 * This interface is the base interface to be implemented by all nodes of the
 * parse / computation tree.
 * 
 * @author Jan Himmelspach
 */
public interface INode extends Serializable {

  /**
   * Calc. Computes the node, and thus typically all sub nodes. If called on the
   * root node the complete tree should be computed.
   * 
   * @param cEnv
   *          the environment that is used to calculate the node (should be
   *          passed on to sub nodes)
   * @param <N>
   *          the type of the node to be returned.
   * @return the N
   */
  <N extends INode> N calc(IEnvironment<?> cEnv);

  /**
   * Gets children nodes if any of node.
   * 
   * @return the children if any
   */
  List<? extends INode> getChildren();

  /**
   * Gets the node's name.
   * 
   * @return the node's name
   */
  String getName();

  /**
   * Sets the print manager.
   * 
   * @param printManager
   *          the new print manager
   */
  void setPrintManager(IPrintManager printManager);
}
