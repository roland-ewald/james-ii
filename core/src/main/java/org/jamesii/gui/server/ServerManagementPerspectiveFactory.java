/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.perspective.IPerspective;
import org.jamesii.gui.perspective.plugintype.PerspectiveFactory;

/**
 * Factory that creates the server management perspective
 * 
 * @author Stefan Rybacki
 * 
 */
public class ServerManagementPerspectiveFactory extends PerspectiveFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -6063790384240993226L;

  @Override
  public IPerspective create(ParameterBlock params) {
    return new ServerManagementPerspective();
  }

}
