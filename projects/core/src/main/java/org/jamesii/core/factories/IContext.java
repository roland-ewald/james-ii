/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import java.util.List;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * The IContext interface is used in factories to determine the context in which
 * an object is being created. This interface makes it easier to observe
 * dependencies between objects (which object created which).
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IContext {

  /**
   * Set the context this object belongs to (parent context).
   * 
   * @param context
   */
  void setContext(IContext context);

  /**
   * Get the context this object belongs to.
   * 
   * @return
   */
  IContext getContext();

  /**
   * Register a sub (child) context.
   */
  void registerContext(IContext context);

  /**
   * Get the list of child contexts.
   * 
   * @return
   */
  List<IContext> getChildContexts();
  
  /**
   * Create an instance of a plug-in identified by the plug-in type ident passed.
   * @param pluginType Canonical class name of ident given in the plug-in type definition file.
   * @param block Parameters used to filter and for instance creation.
   * @return an instance of a plug-in of the plug-in type passed or null if a plug-in could not be instantiated. 
   */
  <O> O create (String pluginType, ParameterBlock block);

}
