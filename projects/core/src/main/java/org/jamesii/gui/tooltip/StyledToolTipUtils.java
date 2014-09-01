/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.tooltip;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.gui.syntaxeditor.ILexer;
import org.jamesii.gui.syntaxeditor.ILexerToken;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.gui.tooltip.StyledToolTipToken.Type;

/**
 * Static class that covers some useful methods that are related to
 * {@link StyledToolTip}s.
 * 
 * @author Stefan Rybacki
 */
public class StyledToolTipUtils {
  // TODO sr137: add auto style for code to styled tooltip as well means create
  // something like:
  /*
   * <code ILexer.class, Stylizer.class>Auto formatted code</code>
   */

  /**
   * Base of hex numbers.
   */
  private static final int HEX = 16;

  /**
   * helper class so that it is possible to represent NONE colors used in styled
   * tooltips
   */
  private static final class NoneColor extends Color {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -4684544845513548304L;

    /**
     * The Constant instance.
     */
    public static final NoneColor INSTANCE = new NoneColor();

    /**
     * Instantiates a new none color.
     */
    private NoneColor() {
      super(0, 0, 0, 0);
    }
  }

  /**
   * Helper function to format a given {@code text} using the given
   * {@code state} by applying {@link StyledToolTip} tags.
   * 
   * @param state
   *          the state to apply
   * @param text
   *          to the text
   * @return the {@link StyledToolTip} tagged text
   */
  private static String formatText(StyleState state, String text) {
    StringBuilder buffer = new StringBuilder(text.length());

    String textPrep = getEscapedToolTip(text);

    textPrep = textPrep.replaceAll("\r\n|\n|\r", "<br/>\n");

    // create style
    if (state.getFontName() != null) {
      buffer.append("<font");
      buffer.append(state.getFontName());
      buffer.append("/>");
    }

    if (state.getFontSize() > 0) {
      buffer.append("<fontsize");
      buffer.append(state.getFontSize());
      buffer.append("/>");
    }

    if (state.getColor() != null) {
      buffer.append("<color");
      buffer.append(Integer.toHexString(state.getColor().getRGB() & 0xFFFFFF));
      buffer.append("/>");
    }

    if (state.getBgColor() != null) {
      if (state.getBgColor() instanceof NoneColor) {
        buffer.append("<bgcolor none/>");
      } else {
        buffer.append("<bgcolor");
        buffer.append(Integer
            .toHexString(state.getBgColor().getRGB() & 0xFFFFFF));
        buffer.append("/>");
      }
    }

    // style start tags
    if (state.isCrossout()) {
      buffer.append("<c>");
    }
    if (state.isUnderline()) {
      buffer.append("<u>");
    }
    if (state.isItalic()) {
      buffer.append("<i>");
    }
    if (state.isBold()) {
      buffer.append("<b>");
    }

    // set actual text
    buffer.append(textPrep);

    // style end tags
    if (state.isBold()) {
      buffer.append("</b>");
    }
    if (state.isItalic()) {
      buffer.append("</i>");
    }
    if (state.isUnderline()) {
      buffer.append("</u>");
    }
    if (state.isCrossout()) {
      buffer.append("</c>");
    }

    return buffer.substring(0);

  }

  /**
   * Escapes the given {@link StyledToolTip} tagged text so that it can be
   * displayed "as is" by the {@link StyledToolTip} renderer.
   * 
   * @param text
   *          the text to escape
   * @return escaped text
   */
  public static String getEscapedToolTip(String text) {
    // escape using <</> instead of <
    return text.replace("<", "<</>");
  }

  /**
   * Creates a {@link StyledToolTip} tagged text from a highlighted text. That
   * means the highlighting of the given text using the given lexer and stylizer
   * is converted into {@link StyledToolTip} tags and applied to the text.
   * 
   * @param text
   *          the text to highlight
   * @param lexer
   *          lexer used for highlighting
   * @param stylizer
   *          stylizer used for highlighting given text
   * @return {@link StyledToolTip} tagged text representing the highlighted text
   */
  public static String getHighlightedToolTip(String text, ILexer lexer,
      ILexerTokenStylizer<ILexerToken> stylizer) {
    /*
     * Use the ILexer to generate syntax tokens that in return are used to
     * stylize the text. The text itself is then attributed with StyledToolTip
     * tags according to stylizer information
     */
    lexer.parse(new StringReader(text));
    // defensive copy
    List<ILexerToken> tokens = new ArrayList<>(lexer.getSyntaxTokens());

    StringBuilder result = new StringBuilder(text.length());

    result.append("<fontMonospaced/>");

    int last = 0;
    Color currentBg = null;
    Color currentColor = null;
    for (ILexerToken t : tokens) {
      if (t.getStart() > last) {
        // get default style and color etc.
        StyleState style = new StyleState();
        style.setFontSize(-1);
        style.setBold((stylizer.getDefaultStyle() & Font.BOLD) > 0);
        style.setItalic((stylizer.getDefaultStyle() & Font.ITALIC) > 0);

        Color c = stylizer.getDefaultColor();
        if (c == null && currentColor == null) {
          style.setColor(null);
        } else if (c != null && c.equals(currentColor)) {
          style.setColor(null);
        } else {
          style.setColor(c);
        }
        currentColor = c;

        c = stylizer.getDefaultBgColor();
        if (c == null && currentBg != null) {
          style.setBgColor(NoneColor.INSTANCE);
        } else {
          style.setBgColor(c);
        }

        result.append(formatText(style, text.substring(last, t.getStart())));
        currentBg = c;
      }

      // get style for token
      StyleState style = new StyleState();
      style.setFontSize(-1);
      style.setBold((stylizer.getFontStyleFor(t) & Font.BOLD) > 0);
      style.setItalic((stylizer.getFontStyleFor(t) & Font.ITALIC) > 0);

      Color c = stylizer.getColorFor(t);
      if (c == null && currentColor == null) {
        style.setColor(null);
      } else if (c != null && c.equals(currentColor)) {
        style.setColor(null);
      } else {
        style.setColor(c);
      }
      currentColor = c;

      c = stylizer.getBgColorFor(t);
      if (c == null && currentBg != null) {
        style.setBgColor(NoneColor.INSTANCE);
      } else {
        style.setBgColor(c);
      }

      result
          .append(formatText(style, text.substring(t.getStart(), t.getEnd())));
      last = t.getEnd();
      currentBg = c;
    }

    return result.substring(0);
  }

  /**
   * Converts a {@link StyledToolTip} tagged text into a HTML styled text having
   * the same style as given text.
   * <p>
   * <b>Note:</b>The conversion is not complete for now there are the following
   * restrictions:
   * <ul>
   * <li>no background color support</li>
   * <li>font and font size support can be a little off</li>
   * <li>states are not optimized that means the resulting HTML might contain
   * redundant information</li>
   * </ul>
   * 
   * @param text
   *          the {@link StyledToolTip} tagged text
   * @return HTML version of text1
   */
  public static String getHTMLFromStyledToolTip(String text) {
    StringBuilder result = new StringBuilder();
    if (text != null) {
      result.append("<html><body><pre>");

      StyledToolTipParser parser = new StyledToolTipParser();
      parser.parse(new StringReader(text));
      List<StyledToolTipToken> renderTokens = parser.getRenderTokens();

      for (StyledToolTipToken t : renderTokens) {
        if (t.getType() == Type.LINE) {
          result.append("<hr/>");
        }
        if (t.getType() == Type.LINEBREAK) {
          result.append("<br/>");
        }
        if (t.getType() == Type.TEXT) {

          if (t.getState().isBold()) {
            result.append("<b>");
          }
          if (t.getState().isItalic()) {
            result.append("<i>");
          }
          if (t.getState().isUnderline()) {
            result.append("<u>");
          }
          if (t.getState().isCrossout()) {
            result.append("<strike>");
          }

          // TODO sr137: background color is missing and maybe some other new
          // features recently implemented

          result.append("<font ");

          if (t.getState().getFontName() != null
              && t.getState().getFontName().length() > 0) {
            result.append("face=\"");
            if (t.getState().getFontName().equals(Font.MONOSPACED)) {
              result.append("Courier New");
            } else {
              result.append(t.getState().getFontName());
            }
            result.append("\" ");
          }

          if (t.getState().getColor() != null) {
            result.append("color=#");
            result.append(String.format("%06x", t.getState().getColor()
                .getRGB() & 0xFFFFFF));
            result.append(" ");
          }

          if (t.getState().getFontSize() > 0) {
            result.append("size=\"");
            result.append(String.valueOf(t.getState().getFontSize() / 4));
            result.append("\"");
          }

          result.append(">");

          result.append(text.substring(t.getStart(), t.getEnd())
              .replace("<", "&lt;").replace(">", "&gt;"));

          result.append("</font>");

          if (t.getState().isCrossout()) {
            result.append("</strike>");
          }
          if (t.getState().isUnderline()) {
            result.append("</u>");
          }
          if (t.getState().isItalic()) {
            result.append("</i>");
          }
          if (t.getState().isBold()) {
            result.append("</b>");
          }
        }
      }

      result.append("</pre></body></html>");
    } else {
      result.append("<html/>");
    }
    return result.toString();
  }

  /**
   * Helper function that returns a color value for a specific input text. This
   * text can be either a named representation of the color like: white, red,
   * blue or a system color value like desktop, window, caption and so on or it
   * can be a hexadecimal value representing the color in FFFFFF where the first
   * two chars represent red the next two green and the last two blue.
   * 
   * @param value
   *          value that represents the color
   * @param alternative
   *          if no match can be found in either way this color is returned
   * @return the color represented by the value if found, alternative otherwise
   * @see Color
   * @see SystemColor
   */
  public static Color getColorFromValue(String value, Color alternative) {
    /*
     * first check for color names, the supported names so far are: - blue -
     * white - green - yellow - red - magenta - orange - gray - darkgray -
     * lightgray - black - cyan
     * 
     * basically all the colors the Color class provides by default additionally
     * it supports the usage of system property color values like: - window
     * 
     * plus if none of the above matched we try the format FFFFFF where the
     * first two characters stand for red in hexadezimal, the next two for green
     * and the last two for blue.
     * 
     * If nothing helps it just returns the passed alternative value
     */
    Color c = alternative;

    // use reflection to get check whether the Color class defines a
    // color field
    // with the specified value
    String prepValue = value.trim().toLowerCase();

    if (prepValue.equals("none")) {
      return new Color(255, 255, 255, 0);
    }

    for (Field f : Color.class.getFields()) {
      if (f.getName().equalsIgnoreCase(prepValue)
          && f.getType() == Color.class) {
        try {
          c = (Color) f.get(Color.black);
          return c;
        } catch (IllegalArgumentException | IllegalAccessException e) {
        }
      }
    }

    // if we reach here no field in color was found that matches the
    // input
    // value, so we check SystemColor
    for (Field f : SystemColor.class.getFields()) {
      if (f.getName().equalsIgnoreCase(prepValue)
          && f.getType() == SystemColor.class) {
        try {
          c = (SystemColor) f.get(SystemColor.activeCaption);
          return c;
        } catch (IllegalArgumentException | IllegalAccessException e) {
        }
      }
    }

    // if we reach here no field in color or systemcolor was found
    // matching the
    // input value so we check for hex value
    try {
      int v = Integer.parseInt(prepValue, HEX);
      c = new Color(v);
    } catch (Exception e) {
    }

    return c;
  }

  /**
   * helper function that can invert a color
   * 
   * @param c
   *          the color to invert
   * @return the inverted color
   */
  public static Color invertColor(Color c) {
    Color result = null;
    if (c != null) {
      result =
          new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue(),
              c.getAlpha());
    }
    return result;
  }

  /**
   * Chooses a background for the given color. This makes sense for cases where
   * the chosen color is the same as the background to display it on. So
   * basically this function determines the brightness of the given color and
   * returns a dark or light color depending on the brightness value.
   * 
   * @param c
   *          the color a background color is needed for
   * @return the background color
   */
  public static Color getBackgroundFor(Color c) {
    Color result = null;
    if (c != null) {
      if ((c.getRed() + c.getGreen() + c.getBlue()) / 3 <= 128) {
        result = Color.white;
      } else {
        result = Color.black;
      }
    }
    return result;
  }

  private StyledToolTipUtils() {
  }
}
