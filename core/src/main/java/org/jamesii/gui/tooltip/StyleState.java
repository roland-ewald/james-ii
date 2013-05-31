/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.tooltip;

import java.awt.Color;

/**
 * Helper class that represents a state that determines how to render the token
 * this state is associated with. The state holds information like font style,
 * font, font size, color and so on. The renderer should use as much information
 * as possible to represent the state as close as possible. It is clear that a
 * token that represents a line doesn't care about underlining where a text
 * token does.
 * 
 * @author Stefan Rybacki
 */
public class StyleState {
  /**
   * flag indicating whether italic font style is to use
   */
  private boolean italic = false;

  /**
   * flag indicating whether bold font style is to use
   */
  private boolean bold = false;

  /**
   * flag indicating whether underlining is to use
   */
  private boolean underline = false;

  /**
   * flag indicating whether crossing out is to use
   */
  private boolean crossout = false;

  /**
   * indicates what foreground color to use
   */
  private Color color = Color.black;

  /**
   * indicates what background color to use
   */
  private Color bgColor = null;

  /**
   * indicates what font to use
   */
  private String fontName = null;

  /**
   * indicates what font size to use
   */
  private int fontSize = 12;

  /**
   * Convenience constructor setting the entire state with the constructor
   * 
   * @param bold
   *          determines whether font should be bold
   * @param italic
   *          determines whether font should be italic
   * @param color
   *          determines which color to render with
   * @param bgColor
   *          determines which background color to render with
   * @param underline
   *          determines whether to underline
   * @param fontName
   *          determines which font to use
   * @param fontSize
   *          determines which font size to use
   * @param crossout
   *          determines whether to cross out
   */
  public StyleState(boolean bold, boolean italic, Color color, Color bgColor,
      boolean underline, String fontName, int fontSize, boolean crossout) {
    this.setBold(bold);
    this.setItalic(italic);
    this.setColor(color);
    this.setBgColor(bgColor);
    this.setUnderline(underline);
    this.setFontName(fontName);
    this.setFontSize(fontSize);
    this.setCrossout(crossout);
  }

  /**
   * Constructs a default state
   */
  public StyleState() {
  }

  /**
   * Assigns this state to the passed state
   * 
   * @param state
   *          to assign from
   */
  public StyleState(StyleState state) {
    this(state.isBold(), state.isItalic(), state.getColor(),
        state.getBgColor(), state.isUnderline(), state.getFontName(), state
            .getFontSize(), state.isCrossout());
  }

  /**
   * @return the italic
   */
  public final boolean isItalic() {
    return italic;
  }

  /**
   * @return the bold
   */
  public final boolean isBold() {
    return bold;
  }

  /**
   * @return the underline
   */
  public final boolean isUnderline() {
    return underline;
  }

  /**
   * @return the crossout
   */
  public final boolean isCrossout() {
    return crossout;
  }

  /**
   * @return the color
   */
  public final Color getColor() {
    return color;
  }

  /**
   * @return the bgColor
   */
  public final Color getBgColor() {
    return bgColor;
  }

  /**
   * @return the fontName
   */
  public final String getFontName() {
    return fontName;
  }

  /**
   * @return the fontSize
   */
  public final int getFontSize() {
    return fontSize;
  }

  /**
   * @param bold
   *          the bold to set
   */
  public final void setBold(boolean bold) {
    this.bold = bold;
  }

  /**
   * @param crossout
   *          the crossout to set
   */
  public final void setCrossout(boolean crossout) {
    this.crossout = crossout;
  }

  /**
   * @param italic
   *          the italic to set
   */
  public final void setItalic(boolean italic) {
    this.italic = italic;
  }

  /**
   * @param underline
   *          the underline to set
   */
  public final void setUnderline(boolean underline) {
    this.underline = underline;
  }

  /**
   * @param color
   *          the color to set
   */
  public final void setColor(Color color) {
    this.color = color;
  }

  /**
   * @param bgColor
   *          the bgColor to set
   */
  public final void setBgColor(Color bgColor) {
    this.bgColor = bgColor;
  }

  /**
   * @param fontSize
   *          the fontSize to set
   */
  public final void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }

  /**
   * @param fontName
   *          the fontName to set
   */
  public final void setFontName(String fontName) {
    this.fontName = fontName;
  }
}
