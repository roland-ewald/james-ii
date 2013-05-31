/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner;

import org.jamesii.core.model.IModel;

/**
 * Interface for attribute handling classes
 * 
 * AttributeHandler are invoked with the reference to the actual node in the
 * tree. They should be used to assign special attributes to each node, that can
 * be calculated either by use of certain attributes of child nodes (use
 * synthesizeAttribute()) or by the use of the parent's attributes.
 * 
 * Hint: By implementing own tree searches, an attribute handler can not only
 * access the node attributes for local changes, but in fact he can change the
 * whole tree.
 * 
 * Created on 15.12.2004
 * 
 * @author Roland Ewald
 * 
 */
public interface IAttributeHandler {

  /**
   * Inherits a set of attributes
   * 
   * @param manager
   * @param model
   */
  void inheritForAttribute(IAttributeManager manager, IModel model);

  /**
   * Synthesizes a set of attributes
   * 
   * @param manager
   * @param model
   */
  void synthesizeForAttribute(IAttributeManager manager, IModel model);

}