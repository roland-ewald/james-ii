/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.color;

import java.awt.Color;

import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.ICAValueMapperEditor;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.plugintype.CAValueMapperEditorFactory;

/**
 * Factory for the color value mapper editor.
 * 
 * @author Johannes Rössel
 */
@Plugin
public class ColorValueMapperEditorFactory extends CAValueMapperEditorFactory {

  /** Serial Version ID. */
  private static final long serialVersionUID = -7934831750150160856L;

  @Override
  public ICAValueMapperEditor createValueMapperEditor(Class<?> c) {
    if (Color.class.equals(c)) {
      return new ColorValueMapperEditor();
    }
    return null;
  }

  @Override
  public boolean supports(Class<?> c) {
    if (Color.class.equals(c)) {
      return true;
    }
    return false;
  }

}
