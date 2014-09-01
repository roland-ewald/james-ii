/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.tooltip;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.StringReader;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicToolTipUI;

import org.jamesii.gui.tooltip.StyledToolTipToken.Type;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Tooltip component that supports more than just one lined tooltips. It
 * supports an own description syntax where one can style, layout and iconify
 * their tooltips.
 * <p>
 * The syntax is oriented at the html syntax. So to describe what to do tags are
 * used, so for instance &lt;b&gt;bold&lt;/b&gt; will draw <b>bold</b> in bold
 * font.
 * <p>
 * In the following all available tags are explained:
 * <p>
 * <table>
 * <tr>
 * <td>&lt;b&gt;</td>
 * <td>turns on bold font</td>
 * </tr>
 * <tr>
 * <td>&lt;/b&gt;</td>
 * <td>turns off bold font</td>
 * </tr>
 * <tr>
 * <td>&lt;i&gt;</td>
 * <td>turns on italic font</td>
 * </tr>
 * <tr>
 * <td>&lt;/i&gt;</td>
 * <td>turns off bold font</td>
 * </tr>
 * <tr>
 * <td>&lt;c&gt;</td>
 * <td>turns on out crossing</td>
 * </tr>
 * <tr>
 * <td>&lt;/c&gt;</td>
 * <td>turns off out crossing</td>
 * </tr>
 * <tr>
 * <td>&lt;u&gt;</td>
 * <td>turns on underlining</td>
 * </tr>
 * <tr>
 * <td>&lt;/u&gt;</td>
 * <td>turns off underlining</td>
 * </tr>
 * <tr>
 * <td>&lt;font fontname/&gt;</td>
 * <td>example of font change usage the string between start and end defines the
 * font by specifying its name</td>
 * </tr>
 * <tr>
 * <td>&lt;size 12/&gt;</td>
 * <td>example of font size usage the string between start and end defines the
 * size (here it is 12)</td>
 * </tr>
 * <tr>
 * <td>&lt;color FFFFFF/&gt;</td>
 * <td>example of color usage the string between start and end tag the color in
 * Hex representation (can also be none for no color, or a string representative
 * like red, blue, green)</td>
 * </tr>
 * <tr>
 * <td>&lt;bgcolor FFFFFF/&gt;</td>
 * <td>example of background color usage the string between start and end tag
 * the color in Hex representation (can also be none for no color, or a string
 * representative like red for red)</td>
 * </tr>
 * <tr>
 * <td>&lt;hr/&gt;</td>
 * <td>draws a line from left to right in the set color in the next line</td>
 * </tr>
 * <tr>
 * <td>&lt;br/&gt;</td>
 * <td>inserts a line break \n,\r and \n\r do the same</td>
 * </tr>
 * <tr>
 * <td>&lt;&lt;/&gt;</td>
 * <td>inserts a &lt; in the text, this can be used for escaping tags</td>
 * </tr>
 * <tr>
 * <td>&lt;push/&gt;</td>
 * <td>puts the current style state on a stack</td>
 * </tr>
 * <tr>
 * <td>&lt;pop/&gt;</td>
 * <td>restores a previously pushed style state</td>
 * </tr>
 * <tr>
 * <td>&lt;pre&gt;</td>
 * <td>starts a verbatim block (all tags until &lt;pre/&gt; are ignored)</td>
 * </tr>
 * <tr>
 * <td>&lt;pre/&gt;</td>
 * <td>closes a verbatim block</td>
 * </tr>
 * <tr>
 * <td>&lt;width 100/&gt;</td>
 * <td>specifies a custom maximum width of the tooltip to render (here 100)</td>
 * </tr>
 * <tr>
 * <td>&lt;height 100/&gt;</td>
 * <td>specifies a custom maximum height of the tooltip to render (here 100)</td>
 * </tr>
 * </table>
 * <p>
 * 
 * still missing for now:
 * <ul>
 * <li>layouting</li>
 * <li>images</li>
 * <li>enumarations</li>
 * </ul>
 * 
 * <p>
 * <b>Example Tooltip could be:</b>
 * <p>
 * <code><pre>
 * &lt;size 18/&gt;&lt;font Arial Black/&gt;
 * This is a styled ToolTip&lt;hr/&gt;
 * &lt;size 12/&gt;&lt;font Arial/&gt;
 * - It supports different fonts and font sizes as well
 *   as line breaks
 * - It also supports &lt;bgcolor lightgray/&gt;&lt;color red/&gt;co&lt;color green/&gt;lo&lt;color blue/&gt;rs&lt;bgcolor none/&gt;&lt;color black/&gt;
 *   as well as different kinds of &lt;b&gt;font&lt;/b&gt; &lt;i&gt;styles&lt;/i&gt;
 * - It also supports &lt;u&gt;underlining&lt;/u&gt; &lt;c&gt;crossing&lt;/c&gt; and as already seen&lt;hr/&gt;
 *   seperation lines
 * </pre>
 * <code>
 * 
 * @author Stefan Rybacki
 * 
 */

// functionality
/*
 * to achieve icon handling it might be smart to use an icon registry prior to
 * the usage of icons. Create labels for that matter and use those labels to
 * access the images in styled tooltips. Therefore this tooltip class should
 * provide a static ability to such a icon registry
 */
public class StyledToolTip extends JToolTip {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -7538108723175790605L;

  /**
   * maximum width for tooltips
   */
  private static int maxWidth =
      Toolkit.getDefaultToolkit().getScreenSize().width / 2;

  /**
   * maximum height for tooltips
   */
  private static int maxHeight =
      Toolkit.getDefaultToolkit().getScreenSize().height * 3 / 4;

  /**
   * parser used to parse the tooltip text and to generate render tokens
   */
  private StyledToolTipParser parser = new StyledToolTipParser();

  /**
   * Render tokens used for rendering purposes (generated by parser)
   */
  private List<StyledToolTipToken> tokens;

  /**
   * tooltip text
   */
  private String tipText = null;

  /**
   * The updated flag. Used to indicate that the content has been changed, and
   * that the control has to be recomputed.
   */
  private boolean updated;

  /**
   * Creates a styled tooltip.
   */
  public StyledToolTip() {
    setUI(new StyledToolTipUI());
  }

  /**
   * Creates a styled tooltip using the specified {@link BasicToolTipUI}
   * 
   * @param toolTipUI
   *          the ui to use
   */
  public StyledToolTip(BasicToolTipUI toolTipUI) {
    setUI(toolTipUI);
  }

  @Override
  public void setTipText(String tipText) {
    // parse tipText
    parser.parse(new StringReader(tipText));
    tokens = parser.getRenderTokens();
    updated = true;
    this.tipText = tipText;

    super.setTipText(tipText);
  }

  /**
   * @return the maximum tooltip width
   */
  public static int getMaxWidth() {
    return maxWidth;
  }

  /**
   * Sets the maximum tooltip height. Text that reaches over this width will be
   * tried to wrap.
   * 
   * @param height
   *          the height to set
   */
  public static void setMaxHeight(int height) {
    maxHeight = height;
  }

  /**
   * Gets the max height.
   * 
   * @return the maximum tooltip height
   */
  public static int getMaxHeight() {
    return maxHeight;
  }

  /**
   * Sets the maximum tooltip width. Text that reaches over this width will be
   * tried to wrap.
   * 
   * @param width
   *          the width to set
   */
  public static void setMaxWidth(int width) {
    maxWidth = width;
  }

  /**
   * StyledToolTip UI used for the {@link StyledToolTip}
   * 
   * @author Stefan Rybacki
   * 
   */
  private class StyledToolTipUI extends BasicToolTipUI {
    /**
     * Tooltip dimension after determining size according text style and size
     * etc.
     */
    private Dimension dim;

    /** The buffer. */
    private BufferedImage buffer;

    /**
     * create the styled tooltip ui
     */
    public StyledToolTipUI() {
      super();
      setBorder(new LineBorder(Color.DARK_GRAY, 1, true));
    }

    @Override
    public void paint(Graphics g, JComponent c) {
      if (dim != null) {
        g.setClip(2, 2, dim.width - 5, dim.height - 5);
      }
      if (buffer != null) {
        g.drawImage(buffer, 1, 1, c);
      }

    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
      if (tipText != null) {
        if (updated) {
          updated = false;
          int width = maxWidth;
          int height = maxHeight;

          // check for width and height render tokens and change width and
          // height accordingly
          for (StyledToolTipToken t : tokens) {
            // only take the first occurrence of width and height token
            if (t.getType() == Type.WIDTH) {
              width = t.getStart();
            } else if (t.getType() == Type.HEIGHT) {
              height = t.getStart();
            } else {
              break;
            }
          }

          if (width != 0 && height != 0) {
            // check whether width and height are not greater than the screen
            // resolution
            width =
                Math.min(Toolkit.getDefaultToolkit().getScreenSize().width,
                    width);
            height =
                Math.min(Toolkit.getDefaultToolkit().getScreenSize().height,
                    height);

            // create back buffer image
            buffer =
                BasicUtilities.createCompatibleImage(width, height,
                    Transparency.TRANSLUCENT);
            Graphics2D g = (Graphics2D) buffer.getGraphics();

            dim = new Dimension(0, 0);

            List<StyledToolTipToken> textlist = new ArrayList<>();
            StringBuffer text = new StringBuffer();

            List<Integer> linebreakPos = new ArrayList<>();

            for (StyledToolTipToken t : tokens) {
              // try to concatenate all text tokens in a row
              if (t.getType() == StyledToolTipToken.Type.TEXT) {
                textlist.add(t);
                text.append(tipText.substring(t.getStart(), t.getEnd()));
              }
              if (t.getType() == StyledToolTipToken.Type.LINEBREAK) {
                textlist.add(new StyledToolTipToken(
                    StyledToolTipToken.Type.TEXT, 0, 1, new StyleState()));
                text.append("\n");
                linebreakPos.add(Integer.valueOf(text.length()));
              }
              if (t.getType() == StyledToolTipToken.Type.LINE
                  || tokens.lastIndexOf(t) == tokens.size() - 1) {
                if (text.length() > 0) {
                  // create one attributed string from strings in aslist and
                  // calculate
                  // its dimension
                  AttributedString as = new AttributedString(text.substring(0));

                  int offs = 0;
                  for (StyledToolTipToken a : textlist) {
                    int style = Font.PLAIN;
                    if (a.getState().isBold()) {
                      style |= Font.BOLD;
                    }
                    if (a.getState().isItalic()) {
                      style |= Font.ITALIC;
                    }

                    Font f =
                        new Font(a.getState().getFontName(), style, a
                            .getState().getFontSize());

                    as.addAttribute(TextAttribute.FONT, f, offs,
                        offs + a.getLength());
                    as.addAttribute(TextAttribute.FOREGROUND, a.getState()
                        .getColor(), offs, offs + a.getLength());
                    as.addAttribute(TextAttribute.BACKGROUND, a.getState()
                        .getBgColor(), offs, offs + a.getLength());
                    as.addAttribute(TextAttribute.UNDERLINE, a.getState()
                        .isUnderline() ? TextAttribute.UNDERLINE_ON : -1, offs,
                        offs + a.getLength());
                    as.addAttribute(TextAttribute.STRIKETHROUGH, a.getState()
                        .isCrossout() ? TextAttribute.STRIKETHROUGH_ON : false,
                        offs, offs + a.getLength());

                    offs += a.getLength();
                  }

                  FontRenderContext frc =
                      new FontRenderContext(null, false, false);

                  // now that we have all text tokens in a row in one attributed
                  // string we measure its dimension
                  LineBreakMeasurer lbm =
                      new LineBreakMeasurer(as.getIterator(), frc);

                  TextLayout layout;

                  int maxOffs = text.length();
                  if (linebreakPos.size() > 0) {
                    maxOffs = linebreakPos.get(0);
                  }

                  while ((layout =
                      lbm.nextLayout(Math.max(0, width - 6), maxOffs, false)) != null) {
                    Rectangle2D r = layout.getBounds();
                    dim.width = Math.max((int) r.getWidth(), dim.width);
                    dim.height +=
                        (int) (layout.getAscent() + layout.getDescent() + layout
                            .getLeading());

                    if (lbm.getPosition() >= maxOffs) {
                      if (linebreakPos.size() > 0) {
                        linebreakPos.remove(0);
                      }
                      maxOffs = text.length();
                      if (linebreakPos.size() > 0) {
                        maxOffs = linebreakPos.get(0);
                      }
                    }

                    layout.draw(g, 3, dim.height);
                  }

                  textlist.clear();
                  text.setLength(0);
                }

                if (t.getType() == StyledToolTipToken.Type.LINE) {
                  // add the line to the dimension
                  dim.height += 6;
                  g.setColor(t.getState().getColor());
                  g.drawLine(3, dim.height, width, dim.height);
                  dim.height += 1;
                }

                if (dim.height > height) {
                  break;
                }
              }

              if (dim.height > height) {
                break;
              }
            }

            g.dispose();

            dim.width += 10;
            dim.height += 8;

            dim.width = Math.min(width, dim.width);
            dim.height = Math.min(height, dim.height);

            buffer = buffer.getSubimage(0, 0, dim.width, dim.height);
          }
        }
        // defensive return
        return new Dimension(dim);
      }
      return null;
    }
  }
}