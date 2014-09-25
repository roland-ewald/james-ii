/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.plugintype;

import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.plugins.annotations.PluginType;

/**
 * Abstract factory class for value mappers.
 * 
 * @author Johannes Rössel
 */
@PluginType(
    description = "The support of value mappers for CA cell renderers that can be plugged into the GUI")
public class AbstractCAValueMapperFactory extends
    AbstractFactory<CAValueMapperFactory> {

  /** Serial version ID. */
  private static final long serialVersionUID = 2408884565839268159L;

}
