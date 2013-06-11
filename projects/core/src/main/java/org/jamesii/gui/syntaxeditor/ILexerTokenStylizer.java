/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.awt.Color;
import java.awt.Stroke;

import javax.swing.Icon;

/**
 * For custom syntax highlighting and info annotating it is needed to specify
 * colors for all {@link ILexerToken}s supported by the implemented
 * {@link ILexer}. Usually an {@link ILexerToken} implementation can also
 * contain an enum parameter of different states like STRING, COMMENT and
 * OPERAND if the lexer returns a token of type STRING it is passed to
 * {@link #getColorFor(ILexerToken)} and if the token is no info token also to
 * {@link #getFontStyleFor(ILexerToken)}. Those functions return the color and
 * style the token should be displayed in. In case of the info tokens only
 * {@link #getColorFor(ILexerToken)}, {@link #getIconFor(ILexerToken)},
 * {@link #getStrokePositionFor(ILexerToken)},
 * {@link #getTooltipFor(ILexerToken)}, {
 * {@link #getDescriptionFor(ILexerToken)} is called and used to emphasize the
 * area the token covers
 * 
 * @author Stefan Rybacki
 */
public interface ILexerTokenStylizer<T extends ILexerToken> {
  /**
   * @return the default color to be used for text
   */
  Color getDefaultColor();

  /**
   * 
   * @return the default color to be used as text background
   */
  Color getDefaultBgColor();

  /**
   * 
   * @return the default font style to be used for text
   */
  int getDefaultStyle();

  /**
   * Returns the color for the passed {@link ILexerToken}
   * 
   * @param t
   *          the token the color is needed for
   * @return the color for the passed token
   */
  Color getColorFor(T t);

  /**
   * Returns the background color for the passed {@link ILexerToken}
   * 
   * @param t
   *          the token the color is needed for
   * @return the background color for the passed token
   */
  Color getBgColorFor(T t);

  /**
   * Returns the font style for the passed {@link ILexerToken}
   * 
   * @param t
   *          the token the font style is needed for
   * @return the font style for the passed token
   * 
   * @see java.awt.Font#BOLD
   * @see java.awt.Font#ITALIC
   * @see java.awt.Font#BOLD
   */
  int getFontStyleFor(T t);

  /**
   * Returns the stroke used to mark info {@link ILexerToken}s
   * 
   * @param t
   *          the token the stroke is needed for
   * @return the stroke for the passed token
   */
  Stroke getStrokeFor(T t);

  /**
   * Returns the stroke position used to mark info {@link ILexerToken} s
   * 
   * @param t
   *          the token the stroke position is needed for
   * @return the stroke position for the passed token (might be <strong>
   *         {@code null}</strong> for no background!)
   */
  StrokePosition getStrokePositionFor(T t);

  /**
   * Returns an icon that can be used as icon displayed next to the line number
   * the info token exists in
   * 
   * @param t
   *          the token the icon is requested for
   * @return the icon for the passed token (might be <strong>{@code null}
   *         </strong> for no icon)
   */
  Icon getIconFor(T t);

  /**
   * Returns a tooltip text that can be displayed as help or tooltip message for
   * the passed token
   * 
   * @param t
   *          the token the tooltip is assigned to
   * @return the tooltip that is assigned to the passed token
   */
  String getTooltipFor(T t);

  /**
   * Returns a description text that can be displayed as detailed description
   * for the passed token
   * 
   * @param t
   *          the token the description is assigned to
   * @return the description text that is assigned to the passed token
   */
  String getDescriptionFor(T t);

  /**
   * Returns true if for the specified info token a annotation should be
   * displayed
   * 
   * @param t
   *          the info token
   * @return true if annotation should be visible in annotations bar
   */
  boolean showAnnotationFor(T t);
}
