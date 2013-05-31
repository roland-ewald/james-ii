/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.macos.autotask;

import java.util.Locale;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.autotask.IAutoTask;
import org.jamesii.gui.application.autotask.plugintype.AutoTaskFactory;

/**
 * Factory that creates the mac os menu init autotasks
 * 
 * @author Stefan Rybacki
 * 
 */
public class MacOSMenuInitTaskFactory extends AutoTaskFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 606371284240993256L;

  @Override
  public IAutoTask create(ParameterBlock params) {
    // check for macos first to avoid compilation or class not found
    // exceptions/errors
    String vers =
        System.getProperty("os.name").toLowerCase(Locale.getDefault());

    if (vers.indexOf("mac") >= 0) {
      return new MacOSMenuInitTask();
    }

    return null;
  }

}
