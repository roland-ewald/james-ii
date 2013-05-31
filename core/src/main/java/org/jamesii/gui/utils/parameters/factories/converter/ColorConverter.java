/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter;

import java.awt.Color;
import java.lang.reflect.Field;

/**
 * Converter for Boolean string values.
 * 
 * @author Stefan Rybacki
 */
public class ColorConverter implements IStringConverter<Color> {

  @Override
  public Color convert(String encodedObject) {
    try {
      Field field = Color.class.getField(encodedObject);
      if (field != null) {
        Object c = field.get(null);
        if (c instanceof Color) {
          return (Color) c;
        }
      }
      return Color.getColor(encodedObject);
    } catch (Exception e) {
      return null;
    }
  }

}
