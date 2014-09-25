/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.highlighter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import javax.swing.Icon;

import org.jamesii.gui.syntaxeditor.ILexerToken;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.gui.syntaxeditor.StrokePosition;
import org.jamesii.model.carules.reader.antlr.parser.CALexerToken;
import org.jamesii.model.carules.reader.antlr.parser.CaruleLexer;

/**
 * @author Stefan Rybacki
 * 
 */
public class CASyntaxStylizer implements ILexerTokenStylizer {

  @Override
  public Color getBgColorFor(ILexerToken t) {
    return null;
  }

  @Override
  public Color getColorFor(ILexerToken t) {
    if (t instanceof CALexerToken) {
      switch (((CALexerToken) t).getType()) {
      case CaruleLexer.TRUE:
      case CaruleLexer.FALSE:
      case CaruleLexer.STATE:
      case CaruleLexer.RULE:
      case CaruleLexer.ARROW:
      case CaruleLexer.CAVERSION:
      case CaruleLexer.DIMENSIONS:
      case CaruleLexer.NEIGHBORHOOD:
      case CaruleLexer.WOLFRAMRULE:
        return new Color(0x9b4b4b);
      case CaruleLexer.INTEGER:
      case CaruleLexer.DOUBLE:
        return Color.green.darker();
      case CaruleLexer.ID:
      case CaruleLexer.MOORE:
      case CaruleLexer.NEUMANN:
      case CaruleLexer.FREE:
        return Color.magenta.darker();
      case CaruleLexer.AND:
      case CaruleLexer.OR:
      case CaruleLexer.NOT:
        return Color.red;
      case CaruleLexer.GROUPCOMMENT:
      case CaruleLexer.LINECOMMENT:
        return Color.green.darker().darker();
      case CaruleLexer.LANGUAGECOMMENT:
        return new Color(0x7f9fbf);
      case CaruleLexer.LEFT_PAREN:
      case CaruleLexer.RIGHT_PAREN:
      case CaruleLexer.LEFT_BRACE:
      case CaruleLexer.RIGHT_BRACE:
      case CaruleLexer.LEFT_CURLEY:
      case CaruleLexer.RIGHT_CURLEY:
      case CaruleLexer.COMMA:
      case CaruleLexer.SEMICOLON:
        return Color.black;
      }
    }
    return Color.darkGray;
  }

  @Override
  public Color getDefaultBgColor() {
    return null;
  }

  @Override
  public Color getDefaultColor() {
    return Color.darkGray;
  }

  @Override
  public int getDefaultStyle() {
    return Font.PLAIN;
  }

  @Override
  public int getFontStyleFor(ILexerToken t) {
    if (t instanceof CALexerToken) {
      switch (((CALexerToken) t).getType()) {
      case CaruleLexer.TRUE:
      case CaruleLexer.FALSE:
      case CaruleLexer.STATE:
      case CaruleLexer.RULE:
      case CaruleLexer.ARROW:
      case CaruleLexer.CAVERSION:
      case CaruleLexer.DIMENSIONS:
      case CaruleLexer.NEIGHBORHOOD:
      case CaruleLexer.WOLFRAMRULE:
        return Font.BOLD; // NOSONAR
      case CaruleLexer.INTEGER:
      case CaruleLexer.DOUBLE:
        return Font.BOLD | Font.ITALIC;
      case CaruleLexer.ID:
      case CaruleLexer.MOORE:
      case CaruleLexer.NEUMANN:
      case CaruleLexer.FREE:
        return Font.BOLD; // NOSONAR
      case CaruleLexer.AND:
      case CaruleLexer.OR:
      case CaruleLexer.NOT:
        return Font.BOLD;
      case CaruleLexer.GROUPCOMMENT:
      case CaruleLexer.LINECOMMENT:
      case CaruleLexer.LANGUAGECOMMENT:
        return Font.PLAIN;
      case CaruleLexer.LEFT_PAREN:
      case CaruleLexer.RIGHT_PAREN:
      case CaruleLexer.LEFT_BRACE:
      case CaruleLexer.RIGHT_BRACE:
      case CaruleLexer.LEFT_CURLEY:
      case CaruleLexer.RIGHT_CURLEY:
      case CaruleLexer.COMMA:
      case CaruleLexer.SEMICOLON:
        return Font.PLAIN;
      }
    }
    return Font.PLAIN;
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
