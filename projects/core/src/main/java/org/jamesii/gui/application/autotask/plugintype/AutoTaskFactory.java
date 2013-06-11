/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.autotask.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.autotask.IAutoTask;

/**
 * Basic factory for all factories that are autotasks
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class AutoTaskFactory extends Factory<IAutoTask> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 1167909894284902432L;

  /**
   * Creates autotask
   * 
   * @param params
   *          parameters
   * @return auto task
   */
  @Override
  public abstract IAutoTask create(ParameterBlock params);

}
