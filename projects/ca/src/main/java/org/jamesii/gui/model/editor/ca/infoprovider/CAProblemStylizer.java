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

import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.syntaxeditor.ILexerToken;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.gui.syntaxeditor.StrokePosition;
import org.jamesii.model.carules.reader.antlr.parser.CAProblemToken;

/**
 * @author Stefan Rybacki
 * 
 */
public class CAProblemStylizer implements ILexerTokenStylizer {
  private final Icon errorIcon = IconManager.getIcon(
      IconIdentifier.ERROR_SMALL, "x");

  private final Stroke stroke = new BasicStroke(1f);

  @Override
  public Color getBgColorFor(ILexerToken t) {
    return null;
  }

  @Override
  public Color getColorFor(ILexerToken t) {
    return Color.red;
  }

  @Override
  public Color getDefaultBgColor() {
    return null;
  }

  @Override
  public Color getDefaultColor() {
    return null;
  }

  @Override
  public int getDefaultStyle() {
    return Font.PLAIN;
  }

  @Override
  public int getFontStyleFor(ILexerToken t) {
    return Font.PLAIN;
  }

  @Override
  public Icon getIconFor(ILexerToken t) {
    if (t instanceof CAProblemToken) {
      return errorIcon;
    }
    return null;
  }

  @Override
  public String getDescriptionFor(ILexerToken t) {
    if (t instanceof CAProblemToken) {
      return ((CAProblemToken) t).getMessage();
    }
    return null;
  }

  @Override
  public Stroke getStrokeFor(ILexerToken t) {
    return stroke;
  }

  @Override
  public StrokePosition getStrokePositionFor(ILexerToken t) {
    return StrokePosition.UNDERLINE;
  }

  @Override
  public String getTooltipFor(ILexerToken t) {
    if (t instanceof CAProblemToken) {
      return ((CAProblemToken) t).getMessage();
    }
    return null;
  }

  @Override
  public boolean showAnnotationFor(ILexerToken t) {
    return t instanceof CAProblemToken;
  }

}
