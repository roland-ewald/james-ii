/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner;

import java.util.List;

import org.jamesii.core.model.IModel;

/**
 * Interface that defines the attribute management functions of a partitioning
 * algorithm, if this is supported
 * 
 * Created on 15.12.2004
 * 
 * @author Roland Ewald
 * 
 */
public interface IAttributeManager {

  // Managing registration of attribute handler

  /**
   * Adds a new attribute handler
   * 
   * @param handler
   */
  void addAttributeHandler(IAttributeHandler handler);

  /**
   * Retrieves all attribute handler
   * 
   * @return arraylist with all attribute handlers
   */
  List<IAttributeHandler> getAllAttributeHandler();

  /**
   * Returns a node objects (containinhg the state of the model within the
   * algorithm)
   * 
   * @param model
   * @return node if existing, otherwise null
   */
  Object getNodeForModel(IModel model);

  /**
   * Returns parent node of child, null if the child model is the root model
   * 
   * @param child
   * @return parent model if existing, otherwise null
   */
  IModel getParent(IModel child);

  // API for attribute handler

  /**
   * Removes all attribute handler
   */
  void removeAllAttributesHandler();

  /**
   * Removes an attribute handler
   * 
   * @param handler
   */
  void removeAttributeHandler(IAttributeHandler handler);

}
