/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor.highlighting;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import javax.swing.Icon;

import org.jamesii.gui.syntaxeditor.ILexerToken;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.gui.syntaxeditor.StrokePosition;

/**
 * Stylizer for the {@link DefaultHighlighter} in style
 * {@link org.jamesii.gui.syntaxeditor.highlighting.DefaultHighlighter.Style#DEFAULT}
 * . Highlighting is off text will be displayed in {@link Color#DARK_GRAY}
 * 
 * @author Stefan Rybacki
 * 
 */
public class DefaultStylizer implements ILexerTokenStylizer<ILexerToken> {

  @Override
  public Color getBgColorFor(ILexerToken t) {
    return getDefaultBgColor();
  }

  @Override
  public Color getColorFor(ILexerToken t) {
    return getDefaultColor();
  }

  @Override
  public Color getDefaultBgColor() {
    return null;
  }

  @Override
  public Color getDefaultColor() {
    return Color.DARK_GRAY;
  }

  @Override
  public int getDefaultStyle() {
    return Font.PLAIN;
  }

  @Override
  public int getFontStyleFor(ILexerToken t) {
    return getDefaultStyle();
  }

  @Override
  public Icon getIconFor(ILexerToken t) {
    return null;
  }

  @Override
  public String getDescriptionFor(ILexerToken t) {
    return null;
  }

  @Override
  public Stroke getStrokeFor(ILexerToken t) {
    return null;
  }

  @Override
  public StrokePosition getStrokePositionFor(ILexerToken t) {
    return null;
  }

  @Override
  public String getTooltipFor(ILexerToken t) {
    return null;
  }

  @Override
  public boolean showAnnotationFor(ILexerToken t) {
    return false;
  }

}
