/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JColorChooser;
import javax.swing.JComponent;

/**
 * A component that can be used to choose and setup a {@link Color}. It provides
 * options to set the {@link Color}. It also provides a preview of the currently
 * setup {@link Color}.
 * 
 * @author Stefan Rybacki
 */
public class ColorChooserComponent extends JComponent {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -3210131568214629670L;

  /** The color chooser to use. */
  private JColorChooser chooser;

  /**
   * Instantiates a new color chooser component.
   * 
   * @param value
   *          the value
   */
  public ColorChooserComponent(Color value) {
    setLayout(new BorderLayout());
    chooser = new JColorChooser(value);
    add(chooser, BorderLayout.CENTER);
  }

  /**
   * Sets the selected color.
   * 
   * @param value
   *          the new selected color
   */
  public void setSelectedColor(Color value) {
    chooser.setColor(value);
  }

  /**
   * Gets the selected color.
   * 
   * @return the selected color
   */
  public Color getSelectedColor() {
    return chooser.getColor();
  }

}
