/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Graphics;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * A simple button that appears flat by being not focusable while clickable and
 * having no border as long as the mouse is not hovering over the button.
 * 
 * @author Stefan Rybacki
 * 
 */
public class FlatButton extends JButton {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -2911456238173879459L;

  /**
   * cached empty border
   */
  private Border empty = new EmptyBorder(2, 2, 2, 2);

  /**
   * helper function to init this component, since there more than one
   * constructor.
   */
  private void init() {
    super.setBorder(empty);
    setFocusable(false);
    setFocusPainted(false);
    setContentAreaFilled(false);
    setRolloverEnabled(true);
  }

  /**
   * @see JButton#JButton()
   */
  public FlatButton() {
    this(null, null);
  }

  /**
   * @see JButton#JButton(String)
   */
  public FlatButton(String text) {
    this(text, null);
  }

  /**
   * @see JButton#JButton(Action)
   */
  public FlatButton(Action a) {
    super(a);
    init();
  }

  /**
   * @see JButton#JButton(Icon)
   */
  public FlatButton(Icon icon) {
    this(null, icon);
  }

  /**
   * @see JButton#JButton(String, Icon)
   */
  public FlatButton(String text, Icon icon) {
    super(text, icon);
    init();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (model.isRollover()) {
      g.setColor(getBackground());
      g.draw3DRect(0, 0, getWidth() - 1, getHeight() - 1, true);
    }
    if (model.isPressed() && model.isArmed()) {
      g.setColor(getBackground());
      g.draw3DRect(0, 0, getWidth() - 1, getHeight() - 1, false);
    }
  }

  @Override
  public void setBorder(Border border) {
    super.setBorder(empty);
  }
}
