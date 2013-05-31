/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.tooltip;

import org.jamesii.gui.syntaxeditor.DefaultLexerToken;

/**
 * Class representing syntax tokens for a styled tooltip.
 * 
 * @author Stefan Rybacki
 * 
 */
public class StyledToolTipSyntaxToken extends
    DefaultLexerToken<StyledToolTipSyntaxToken.Type> {
  /**
   * holds the value of token if necessary (for instance for font, font size and
   * color values)
   */
  private String value;

  /**
   * Creates a syntax token for styled tooltips
   * 
   * @param type
   *          what type of token
   * @param start
   *          start position of token in text
   * @param length
   *          token's length
   * @see Type
   */
  public StyledToolTipSyntaxToken(Type type, int start, int length) {
    super(type, start, length);
  }

  /**
   * Creates a syntax token for styled tooltips
   * 
   * @param type
   *          what type of token
   * @param start
   *          start position of token in text
   * @param length
   *          token's length
   * @param value
   *          the value of the token
   * 
   * @see Type
   */
  public StyledToolTipSyntaxToken(Type type, int start, int length, String value) {
    super(type, start, length);
    this.value = value;
  }

  /**
   * the assigned value (used for instance to specify the colorvalue)
   * 
   * @return the text representation of the token (not necessarily set)
   */
  public String getValue() {
    return value;
  }

  /**
   * This enum represents a token that reperesents tokens in a styled tooltip
   * and are used to format and layout tooltip text tokens later on.
   * 
   * @author Stefan Rybacki
   */
  public enum Type {
    /**
     * < token
     */
    LESSTHAN,
    /**
     * &lt;b>
     */
    BOLDON,
    /**
     * &lt;/b>
     */
    BOLDOFF,
    /**
     * &lt;i>
     */
    ITALICON,
    /**
     * &lt;/i>
     */
    ITALICOFF,
    /**
     * &lt;color
     */
    COLORSTART,
    /**
     * />
     */
    COLOREND,
    /**
     * &lt;hr>
     */
    LINE,
    /**
     * &lt;br/>
     */
    LINEBREAK,
    /**
     * text token
     */
    TEXT,
    /**
     * the token between {@link #COLORSTART} and {@link #COLOREND}
     */
    COLORVALUE,
    /**
     * &lt;bgcolor
     */
    BGCOLOREND,
    /**
     * />
     */
    BGCOLORSTART,
    /**
     * the token between {@link #BGCOLORSTART} and {@link #BGCOLOREND}
     */
    BGCOLORVALUE,
    /**
     * &lt;u>
     */
    UNDERLINEON,
    /**
     * &lt;/u>
     */
    UNDERLINEOFF,
    /**
     * &lt;font
     */
    FONTSTART,
    /**
     * the token between {@link #FONTSTART} and />
     */
    FONTVALUE,
    /**
     * the token between {@link #FONTSIZESTART} and />
     */
    FONTSIZEVALUE,
    /**
     * &lt;size
     */
    FONTSIZESTART,
    /**
     * &lt;c>
     */
    CROSSOUTON,
    /**
     * &lt;/c>
     */
    CROSSOUTOFF,
    /**
     * &lt;/pre>
     */
    PREOFF,
    /**
     * &lt;pre>
     */
    PREON,
    /**
     * &lt;push/>
     */
    PUSHSTATE,
    /**
     * &lt;pop/>
     */
    POPSTATE,
    /**
     * &lt;height
     */
    HEIGHTSTART,
    /**
     * the token between {@link #WIDTHSTART} and />
     */
    WIDTHVALUE,
    /**
     * &lt;width
     */
    WIDTHSTART,
    /**
     * the token between {@link #HEIGHTSTART} and />
     */
    HEIGHTVALUE
  }
}
