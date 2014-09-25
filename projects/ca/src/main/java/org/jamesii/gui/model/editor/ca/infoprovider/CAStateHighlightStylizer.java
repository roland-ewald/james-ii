/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.infoprovider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import javax.swing.Icon;

import org.jamesii.gui.syntaxeditor.ILexerToken;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.gui.syntaxeditor.StrokePosition;

/**
 * @author Stefan Rybacki
 * 
 */
public class CAStateHighlightStylizer implements ILexerTokenStylizer {

  /**
   * The stroke to use.
   */
  private final Stroke stroke = new BasicStroke(1f);

  @Override
  public Color getBgColorFor(ILexerToken t) {
    return Color.GRAY;
  }

  @Override
  public Color getColorFor(ILexerToken t) {
    return Color.GRAY;
  }

  @Override
  public Color getDefaultBgColor() {
    return Color.GRAY;
  }

  @Override
  public Color getDefaultColor() {
    return Color.GRAY;
  }

  @Override
  public int getDefaultStyle() {
    return Font.PLAIN;
  }

  @Override
  public String getDescriptionFor(ILexerToken t) {
    return null;
  }

  @Override
  public int getFontStyleFor(ILexerToken t) {
    return Font.PLAIN;
  }

  @Override
  public Icon getIconFor(ILexerToken t) {
    return null;
  }

  @Override
  public Stroke getStrokeFor(ILexerToken t) {
    return stroke;
  }

  @Override
  public StrokePosition getStrokePositionFor(ILexerToken t) {
    return StrokePosition.RECTANGLE;
  }

  @Override
  public String getTooltipFor(ILexerToken t) {
    return null;
  }

  @Override
  public boolean showAnnotationFor(ILexerToken t) {
    return true;
  }
}
