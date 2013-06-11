/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Default implementation of the {@link IContext} interface to be used for
 * delegation.
 * 
 * 
 * @author Jan Himmelspach
 * 
 */
public class Context implements IContext {

  /**
   * The parent context.
   */
  private IContext parent;

  /**
   * The child contexts.
   */
  private List<IContext> children;

  @Override
  public void setContext(IContext context) {
    parent = context;
  }

  @Override
  public IContext getContext() {
    return parent;
  }

  @Override
  public void registerContext(IContext context) {
    if (children == null) {
      children = new LinkedList<>();
    }
    children.add(context);
  }

  @Override
  public List<IContext> getChildContexts() {
    return children;
  }

  @SuppressWarnings("unchecked")
  public static <O> O createInstance(String pluginType, ParameterBlock block, IContext context) {

    Class<? extends AbstractFactory<Factory<?>>> abstractFactory =
        SimSystem.getRegistry().getFactoryType(pluginType);

    Factory<?> factory =
        SimSystem.getRegistry().getFactory(abstractFactory, block);

    O result = null;
    if (factory != null) {
      result = (O) factory.create(block);
    }

    SimSystem.report(Level.FINEST, "Created an instance of " + pluginType
        + " in the context " + context);

    return (O) result;
  }
  
  @Override
  public <O> O create(String pluginType, ParameterBlock block) {    
    return createInstance (pluginType, block, this);
  }

}
