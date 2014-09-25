/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.plugintype;

import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.plugins.annotations.PluginType;

/**
 * Abstract factory for value mapper editors.
 * 
 * @author Johannes Rössel
 */
@PluginType(description = "The support of value mapper editors for CA cell renderers that can be plugged into the GUI")
public class AbstractCAValueMapperEditorFactory extends
    AbstractFactory<CAValueMapperEditorFactory> {

  /** Serial version ID. */
  private static final long serialVersionUID = 2401234565839268159L;

}
