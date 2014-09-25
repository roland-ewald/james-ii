/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.color;

import java.awt.Color;

import javax.swing.JColorChooser;

import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.ICAValueMapper;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.ICAValueMapperEditor;

/**
 * A simple editor for changing color mappings. Uses a {@link JColorChooser} to
 * ask the user for a new color.
 * 
 * @author Johannes Rössel
 */
public class ColorValueMapperEditor implements ICAValueMapperEditor {

  @Override
  public Color editMappingFor(String inputName, Object value,
      ICAValueMapper usedMapper) {
    Color oldColor = (Color) usedMapper.getMappingFor(value);
    Color newColor =
        JColorChooser.showDialog(null, String.format("Edit %s", inputName),
            oldColor);
    if (newColor != null) {
      usedMapper.setMappingFor(value, newColor);
    }
    return newColor == null ? oldColor : newColor;
  }

  @Override
  public Class<?> getSupportedType() {
    return Color.class;
  }

}
