/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import org.jamesii.gui.utils.AbstractComboBoxModel;

/**
 * Simple list model that provides a list of installed fonts.
 * 
 * @author Stefan Rybacki
 */
public class FontListModel extends AbstractComboBoxModel<Font> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -1529985540283667094L;

  /**
   * The Constant allFonts.
   */
  private static final Font[] allFonts = GraphicsEnvironment
      .getLocalGraphicsEnvironment().getAllFonts();

  /**
   * Instantiates a new font list model.
   */
  public FontListModel() {
    for (Font f : allFonts) {
      addElement(f);
    }
  }

}
