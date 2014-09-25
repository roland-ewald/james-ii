/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.plugintype;

import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.plugins.annotations.PluginType;

/**
 * Abstract factory to manage all cell renderer factories for CA models
 * 
 * @author Stefan Rybacki
 */
@PluginType(
    description = "The support of diverse cell renderers for CA models that can be plugged into the GUI")
public class AbstractCACellRendererFactory extends
    AbstractFactory<CACellRendererFactory> {

  /** Serialization ID */
  private static final long serialVersionUID = 7933494330466735125L;

}
