/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor.highlighting.java;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import javax.swing.Icon;

import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.gui.syntaxeditor.StrokePosition;

/**
 * Stylizer for syntax tokens for Java syntax highlighting. Use
 * {@link #getInstance()} to retrieve the actual stylizer.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class JavaSyntaxTokenStylizer implements
    ILexerTokenStylizer<JavaSyntaxToken> {
  /**
   * singleton instance
   */
  private static JavaSyntaxTokenStylizer INSTANCE =
      new JavaSyntaxTokenStylizer();

  /**
   * default color
   */
  private static final Color DEFAULT_COLOR = Color.black;

  /**
   * default style
   */
  private static final int DEFAULT_STYLE = Font.PLAIN;

  /**
   * hidden constructor
   */
  private JavaSyntaxTokenStylizer() {
    // nothing to do here
  }

  @Override
  public Color getColorFor(JavaSyntaxToken to) {

    switch (to.getType()) {
    case COMMENT:
      return new Color(127, 159, 191);
    case STRING_ESC:
    case REGEX:
    case STRING:
      return Color.blue;
    case KEYWORD:
      return new Color(155, 75, 75);
    case NUMBER:
      return Color.orange;
    case OPER:
      return Color.red;
    case TYPE:
      return Color.MAGENTA;
    default:
      return DEFAULT_COLOR;
    }

  }

  @Override
  public int getFontStyleFor(JavaSyntaxToken to) {

    switch (to.getType()) {
    case KEYWORD:
      return Font.BOLD;
    case TYPE:
    case COMMENT:
      return Font.ITALIC;
    case NUMBER:
    case OPER:
    case STRING_ESC:
    case REGEX:
    case STRING:
    default:
      return DEFAULT_STYLE;
    }
  }

  @Override
  public Color getBgColorFor(JavaSyntaxToken t) {
    return null;
  }

  @Override
  public Color getDefaultBgColor() {
    return null;
  }

  @Override
  public Color getDefaultColor() {
    return Color.black;
  }

  @Override
  public int getDefaultStyle() {
    return Font.PLAIN;
  }

  @Override
  public Icon getIconFor(JavaSyntaxToken t) {
    return null;
  }

  @Override
  public String getDescriptionFor(JavaSyntaxToken t) {
    return null;
  }

  @Override
  public Stroke getStrokeFor(JavaSyntaxToken t) {
    return null;
  }

  @Override
  public StrokePosition getStrokePositionFor(JavaSyntaxToken t) {
    return null;
  }

  @Override
  public String getTooltipFor(JavaSyntaxToken t) {
    return null;
  }

  @Override
  public boolean showAnnotationFor(JavaSyntaxToken t) {
    return false;
  }

  /**
   * @return the singleton instance of the stylizer
   */
  public static JavaSyntaxTokenStylizer getInstance() {
    return INSTANCE;
  }

}
