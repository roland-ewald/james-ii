/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import javax.swing.text.Position.Bias;

/**
 * This class provides the view on highlighted text using a given
 * {@link SyntaxEditorKit} it determines the styles and colors of the text to
 * render.
 * 
 * @author Stefan Rybacki
 */
class SyntaxEditorView extends PlainView {
  /**
   * the kit to be used
   */
  private SyntaxEditorKit kit;

  /**
   * Creates a {@link SyntaxEditorView} for a given element using stylizers for
   * problem and syntax tokens.
   * 
   * @param elem
   *          the element the view is created for
   * @param kit
   *          the {@link SyntaxEditorKit} to be used for the view
   */
  public SyntaxEditorView(Element elem, SyntaxEditorKit kit) {
    super(elem);
    this.kit = kit;
  }

  /**
   * Helper function to draw problem indicators. It determines the stroke color,
   * stroke style as well as the stroke position from the given
   * {@link ILexerToken} before it draws the indicator
   * 
   * @param g
   *          graphics object to draw on
   * @param x
   *          drawing start position (x-axis) of indicator
   * @param y
   *          drawing start position (y-axis) of indicator
   * @param s
   *          the segment the indicator is drawn for (used to determine the
   *          indicators length)
   * @param t
   *          the token the indicator is drawn for (used to determine stroke
   *          properties)
   * @param stylizer
   *          the stylizer used to draw the specified token
   */
  private void drawProblemMarking(Graphics g, int x, int y, int width,
      Segment s, ILexerToken t, ILexerTokenStylizer<ILexerToken> stylizer) {
    if (stylizer != null) {
      Graphics2D g2d = (Graphics2D) g;
      Stroke oldStroke = g2d.getStroke();

      Color c = stylizer.getColorFor(t);
      Color bgC = stylizer.getBgColorFor(t);
      Stroke st = stylizer.getStrokeFor(t);
      StrokePosition p = stylizer.getStrokePositionFor(t);

      if (c != null) {
        g2d.setColor(c);
      }
      if (st != null) {
        g2d.setStroke(st);
      }
      if (p == null) {
        p = StrokePosition.NONE;
      }

      int w = width;
      switch (p) {
      case RECTANGLE:
        if (bgC != null) {
          g.setColor(new Color(bgC.getRed(), bgC.getGreen(), bgC.getBlue(), bgC
              .getAlpha() / 2));
          g.fillRect(x, y - g.getFontMetrics().getAscent(), w, g
              .getFontMetrics().getAscent() + 1);
        }
        if (c != null) {
          g.setColor(c);
          g.drawRect(x, y - g.getFontMetrics().getAscent(), w, g
              .getFontMetrics().getAscent() + 1);
        }
        break;
      case CROSSOUT:
        y = y - g.getFontMetrics().getHeight() / 2;
        g.drawLine(x, y + g.getFontMetrics().getDescent() - 1, w + x, y
            + g.getFontMetrics().getDescent() - 1);
        break;
      case UNDERLINE:
        g.drawLine(x, y + g.getFontMetrics().getDescent() - 1, w + x, y
            + g.getFontMetrics().getDescent() - 1);
      default:
        // do nothing
        break;
      }

      g2d.setStroke(oldStroke);
    }
  }

  /**
   * @return the stylizer for syntax tokens registered in the kit
   */
  private ILexerTokenStylizer<ILexerToken> getSyntaxStylizer() {
    return kit.getSyntaxStylizer();
  }

  /**
   * Helper method.
   * 
   * @return the info providers
   */
  private Iterable<IInfoProvider> getInfoProviders() {
    return kit.getInfoProviders();
  }

  /**
   * Renders the unselected text in a given range using tokens to determine
   * coloring of text parts as well as drawing indicators for specified problem
   * tokens
   * 
   * @param g
   *          graphics context to draw on
   * @param x
   *          starting position in the graphics context (x-axis) where to draw
   * @param y
   *          starting position in the graphics context (x-axis) where to draw
   * @param p0
   *          start position of text to display in document
   * @param p1
   *          end position of text to display in document
   */
  @Override
  protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1)
      throws BadLocationException {
    return drawText(g, x, y, p0, p1, false);
  }

  @Override
  protected int drawSelectedText(Graphics g, int x, int y, int p0, int p1)
      throws BadLocationException {
    // try to keep font styles rather than colors
    return drawText(g, x, y, p0, p1, true);
  }

  /**
   * Renders the text in a given range using tokens to determine coloring of
   * text parts as well as drawing indicators for specified problem tokens. If
   * {@code selectionMode} is set to true only font style attributes are used to
   * render the text
   * 
   * @param g
   *          graphics context to draw on
   * @param x
   *          starting position in the graphics context (x-axis) where to draw
   * @param y
   *          starting position in the graphics context (x-axis) where to draw
   * @param p0
   *          start position of text to display in document
   * @param p1
   *          end position of text to display in document
   * @param selectionMode
   *          if true text is drawn in selection mode this means it uses the
   *          selectionForeground color for the EditorPane defined in the
   *          UIManager and only font styles are considered while rendering
   * @return position the drawn text ends at
   * @throws BadLocationException
   */
  private int drawText(Graphics g, int x, int y, int p0, int p1,
      boolean selectionMode) throws BadLocationException {
    Graphics2D g2d = (Graphics2D) g;

    // find tokens at position p0 to p1
    Segment s = getLineBuffer();
    int xe = x;
    Font defaultFont = g2d.getFont();
    Color defaultColor = g2d.getColor();

    // could also use SystemColor class
    if (selectionMode) {
      try {
        JTextComponent host = (JTextComponent) getContainer();
        defaultColor = host.getSelectedTextColor();
      } catch (Exception e) {
        // fail safe that might work
        defaultColor = UIManager.getColor("Editorpane.selectedTextColor");
      }
    }

    List<? extends ILexerToken> syntaxTokens = kit.getSyntaxTokens();

    int lastTokenEnd = p0;

    int startIndex = LexerTokenUtils.findFirstIndexInRange(syntaxTokens, p0);

    // find tokens that start or end in the range p0 and p1
    for (int i = startIndex; i < syntaxTokens.size(); i++) {
      ILexerToken t = syntaxTokens.get(i);

      /*
       * syntax tokens are not allowed to overlap this is checked here
       */
      if (lastTokenEnd > t.getStart() && t.getStart() >= p0) {
        throw new IllegalArgumentException(
            "Syntax tokens are not allowed to overlap! (" + t.getStart() + ":"
                + t.getEnd() + "  Token: " + i + ")");
      }

      // also draw spaces and stuff between tokens
      if (lastTokenEnd < t.getStart()) {
        g2d.setColor(defaultColor);
        g2d.setFont(defaultFont);

        int start = lastTokenEnd;
        int length = Math.max(0, Math.min(t.getStart() - start, p1 - start));

        getDocument().getText(start, length, s);

        // if background color is set draw rectangle
        g2d.setFont(defaultFont.deriveFont(getSyntaxStylizer()
            .getDefaultStyle()));
        if (!selectionMode) {
          if (getSyntaxStylizer().getDefaultBgColor() != null) {
            g2d.setColor(getSyntaxStylizer().getDefaultBgColor());
            int width =
                Utilities.getTabbedTextWidth(s, g.getFontMetrics(), 0, this, 0);
            g2d.fillRect(xe, y - g2d.getFontMetrics().getHeight()
                + g2d.getFontMetrics().getDescent(), width, g2d
                .getFontMetrics().getHeight());
          }
          g2d.setColor(getSyntaxStylizer().getDefaultColor());
        } else {
          g2d.setColor(defaultColor);
        }
        xe = Utilities.drawTabbedText(s, xe, y, g2d, this, 0);
        lastTokenEnd = t.getStart();
      }

      // draw tokens using color and font style provided by the
      // syntaxStylizer
      if (t.getStart() < p1 && t.getStart() + t.getLength() > p0) {
        g2d.setColor(defaultColor);
        g2d.setFont(defaultFont);

        int start = Math.max(p0, t.getStart());
        int length =
            Math.min(t.getLength() - (start - t.getStart()), p1 - start);

        getDocument().getText(start, length, s);

        g2d.setFont(defaultFont.deriveFont(getSyntaxStylizer().getFontStyleFor(
            t)));
        if (!selectionMode) {
          // if background color is set draw rectangle
          if (getSyntaxStylizer().getBgColorFor(t) != null) {
            g2d.setColor(getSyntaxStylizer().getBgColorFor(t));
            int width =
                Utilities.getTabbedTextWidth(s, g.getFontMetrics(), 0, this, 0);
            g2d.fillRect(xe, y - g2d.getFontMetrics().getHeight()
                + g2d.getFontMetrics().getDescent(), width, g2d
                .getFontMetrics().getHeight());
          }
          g2d.setColor(getSyntaxStylizer().getColorFor(t));
        } else {
          g2d.setColor(defaultColor);
        }

        xe = Utilities.drawTabbedText(s, xe, y, g2d, this, 0);
        lastTokenEnd = start + length;
      }

      if (t.getStart() > p1) {
        break;
      }
    }

    // also draw stuff after the last token
    if (lastTokenEnd < p1) {
      g2d.setColor(defaultColor);
      g2d.setFont(defaultFont);

      int start = lastTokenEnd;
      int length = p1 - start;

      getDocument().getText(start, length, s);

      g2d.setFont(defaultFont.deriveFont(getSyntaxStylizer().getDefaultStyle()));
      if (!selectionMode) {
        // if background color is set draw rectangle
        if (getSyntaxStylizer().getDefaultBgColor() != null) {
          g2d.setColor(getSyntaxStylizer().getDefaultBgColor());
          int width =
              Utilities.getTabbedTextWidth(s, g.getFontMetrics(), 0, this, 0);
          g2d.fillRect(xe, y - g2d.getFontMetrics().getHeight()
              + g2d.getFontMetrics().getDescent(), width, g2d.getFontMetrics()
              .getHeight());
        }
        g2d.setColor(getSyntaxStylizer().getDefaultColor());
      }
      xe = Utilities.drawTabbedText(s, xe, y, g2d, this, 0);
    } else {
      g2d.setColor(defaultColor);
    }

    for (IInfoProvider p : getInfoProviders()) {
      List<ILexerToken> problemTokens = new ArrayList<>(p.getTokenCount());
      for (int i = 0; i < p.getTokenCount(); i++) {
        problemTokens.add(p.getToken(i));
      }

      startIndex = LexerTokenUtils.findFirstIndexInRange(problemTokens, p0);
      // inspect problem tokens in range p0 p1 and visualize them
      // find tokens that start or end in the range p0 and p1
      for (int i = startIndex; i < problemTokens.size(); i++) {
        ILexerToken t = problemTokens.get(i);

        if (t.getStart() < p1 && t.getStart() + t.getLength() > p0) {
          int start = p0;
          int length = Math.max(0, Math.min(t.getStart() - start, p1 - start));

          getDocument().getText(start, length, s);

          // get drawing start position of problem token
          int pstart = getOffset(x, start, start + length);

          // now get length of problem token
          start = Math.max(p0, t.getStart());
          length = Math.min(t.getLength() - (start - t.getStart()), p1 - start);

          int width = getOffset(0, start, start + length);

          getDocument().getText(start, length, s);

          // draw problem indicator
          drawProblemMarking(g2d, pstart, y, width, s, t, p.getStylizer());

          g2d.setColor(defaultColor);
        }

        if (t.getStart() > p1) {
          break;
        }
      }
    }

    return xe;
  }

  /**
   * Helper method that returns the width plus added offset of a given text area
   * <b>(within a line)</b>. This is used in
   * {@link #modelToView(int, Shape, Bias)} to provide appropriate caret drawing
   * position.
   * 
   * @param offset
   *          the x offset
   * @param p0
   *          the p0 the starting position in document
   * @param p1
   *          the p1 the ending position in document
   * @return the offset
   * @throws BadLocationException
   *           the bad location exception
   */
  private int getOffset(int offset, int p0, int p1) throws BadLocationException {
    // find tokens at position p0 to p1
    Segment s = getLineBuffer();
    int xe = offset;

    if (getGraphics() == null) {
      return 0;
    }
    Font defaultFont = getGraphics().getFont();

    List<? extends ILexerToken> syntaxTokens = kit.getSyntaxTokens();

    int lastTokenEnd = p0;

    int startIndex = LexerTokenUtils.findFirstIndexInRange(syntaxTokens, p0);

    // find tokens that start or end in the range p0 and p1
    for (int i = startIndex; i < syntaxTokens.size(); i++) {
      ILexerToken t = syntaxTokens.get(i);

      /*
       * syntax tokens are not allowed to overlap this is checked here
       */
      if (lastTokenEnd > t.getStart() && t.getStart() >= p0) {
        throw new IllegalArgumentException(
            "Syntax tokens are not allowed to overlap! (" + t.getStart() + ":"
                + t.getEnd() + "  Token: " + i + ")");
      }

      // also draw spaces and stuff between tokens
      if (lastTokenEnd < t.getStart()) {
        int start = lastTokenEnd;
        int length = Math.max(0, Math.min(t.getStart() - start, p1 - start));

        getDocument().getText(start, length, s);

        // if background color is set draw rectangle
        Font f = defaultFont.deriveFont(getSyntaxStylizer().getDefaultStyle());

        xe +=
            Utilities.getTabbedTextWidth(s, this.getGraphics()
                .getFontMetrics(f), xe, this, 0);

        lastTokenEnd = t.getStart();
      }

      // draw tokens using color and font style provided by the
      // syntaxStylizer
      if (t.getStart() < p1 && t.getStart() + t.getLength() > p0) {
        int start = Math.max(p0, t.getStart());
        int length =
            Math.min(t.getLength() - (start - t.getStart()), p1 - start);

        getDocument().getText(start, length, s);

        Font f = defaultFont.deriveFont(getSyntaxStylizer().getFontStyleFor(t));

        xe +=
            Utilities.getTabbedTextWidth(s, getGraphics().getFontMetrics(f),
                xe, this, 0);
        lastTokenEnd = start + length;
      }

      if (t.getStart() > p1) {
        break;
      }
    }

    // also draw stuff after the last token
    if (lastTokenEnd < p1) {
      int start = lastTokenEnd;
      int length = p1 - start;

      getDocument().getText(start, length, s);

      Font f = defaultFont.deriveFont(getSyntaxStylizer().getDefaultStyle());
      xe +=
          Utilities.getTabbedTextWidth(s, getGraphics().getFontMetrics(f), xe,
              this, 0);
    }

    return xe;
  }

  @Override
  public Shape modelToView(int pos, Shape a, Bias b)
      throws BadLocationException {
    // line coordinates
    Document doc = getDocument();
    Element map = getElement();
    int lineIndex = map.getElementIndex(pos);
    if (lineIndex < 0) {
      return lineToRect(a, 0);
    }
    Rectangle lineArea = lineToRect(a, lineIndex);

    // determine span from the start of the line
    int tabBase = lineArea.x;
    Element line = map.getElement(lineIndex);
    int p0 = line.getStartOffset();
    Segment s = getLineBuffer();
    doc.getText(p0, pos - p0, s);

    int x = getOffset(tabBase, p0, pos);

    // fill in the results and return
    lineArea.x = x;
    lineArea.width = 1;
    lineArea.height = metrics.getHeight();
    return lineArea;
  }

  @Override
  public int viewToModel(float fx, float fy, Shape a, Bias[] bias) {
    // try to guess the line using the original viewToModel method
    int orgModel = super.viewToModel(fx, fy, a, bias);
    Element map = getElement();
    int lineIndex = map.getElementIndex(orgModel);
    if (lineIndex < 0) {
      return 0;
    }

    Element line = map.getElement(lineIndex);
    int p0 = line.getStartOffset();
    int p1 = line.getEndOffset();

    int tabBase = lineToRect(a, lineIndex).x;
    // now iterate through width of text in line to find the cursor position
    int lastOffset = tabBase;

    for (int i = 1; i <= p1 - p0; i++) {
      try {
        int currentOffset = getOffset(tabBase, p0, p0 + i);

        if (lastOffset <= fx && currentOffset >= fx) {
          if (fx - lastOffset < currentOffset - fx) {
            return p0 + i - 1;
          } else {
            return p0 + i;
          }
        }

        lastOffset = currentOffset;
      } catch (Exception e) {
        return orgModel;
      }
    }

    return orgModel;
  }
}
