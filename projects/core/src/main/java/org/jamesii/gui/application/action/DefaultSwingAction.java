/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import javax.swing.AbstractAction;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.resource.ApplicationResourceManager;
import org.jamesii.gui.application.resource.ResourceLoadingException;

/**
 * Simple sub-class that provides some often used methods for convenience.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class DefaultSwingAction extends AbstractAction {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 4170067374578190826L;

  /**
   * Creates a new instance
   * 
   * @param name
   *          the action's name
   */
  public DefaultSwingAction(String name) {
    super(name);
  }

  /**
   * Sets an action's property with a loaded resource. Uses the
   * {@link ApplicationResourceManager} to load the resource and logs errors to
   * the {@link org.jamesii.core.util.logging.ApplicationLogger}.
   * 
   * @param value
   *          the action's property
   * @param resource
   *          the resource URL
   */
  protected void setValueFromResource(String value, String resource) {
    try {
      putValue(value, ApplicationResourceManager.getResource(resource));
    } catch (ResourceLoadingException e) {
      SimSystem.report(e);
    }
  }

}
