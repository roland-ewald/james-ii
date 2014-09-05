/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.autotask;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.autotask.IAutoTask;
import org.jamesii.gui.application.autotask.plugintype.AutoTaskFactory;

/**
 * Factory that creates the default autotasks
 * 
 * @author Stefan Rybacki
 * 
 */
public class DefaultAutoTaskFactory extends AutoTaskFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 606371284240993256L;

  @Override
  public IAutoTask create(ParameterBlock params, Context context) {
    return new DefaultAutoTask();
  }

}
