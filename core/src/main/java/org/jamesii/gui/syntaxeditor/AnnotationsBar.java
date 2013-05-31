/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JToolTip;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;

import org.jamesii.gui.tooltip.StyledToolTip;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * The {@link AnnotationsBar} is meant to be used in combination with a
 * {@link JEditorPane} utilizing the {@link SyntaxEditorKit}. Usually placed
 * right next to the editor resp. the {@link JViewport} the editor is shown in.
 * It is discouraged to use it directly, use the complete {@link SyntaxEditor}
 * instead. It provides an easy to use Editor that supports syntax highlighting,
 * line numbers, annotations and line icons.
 * 
 * @see SyntaxEditor
 * @author Stefan Rybacki
 */
final class AnnotationsBar extends JComponent implements MouseListener,
    MouseMotionListener, IInfoProviderListener {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -2829566875527833843L;

  /**
   * the editor the annotations are shown for
   */
  private JEditorPane editor;

  /**
   * the viewport the editor is shown in, it is used to provide viewport
   * dependent scaling of the annotations position as well as scrolling
   * capabilities on click events on the bar
   */
  private JViewport viewport;

  /**
   * flag whether annotations are shown
   */
  private boolean showAnnotations = false;

  /**
   * the tooltip class used by this component
   */
  private JToolTip tooltipInstance = new StyledToolTip();

  /**
   * The registered information providers.
   */
  private final List<IInfoProvider> providers = new ArrayList<>();

  /**
   * Initializes an annotations bar using the editor the annotations are shown
   * for, the viewport the editor is shown in, the lexer the editor's document
   * is parsed with as well as a problem token stylizer for color and tooltip
   * properties of the annotations
   * 
   * @param editor
   *          the editor the annotations are shown for
   * @param viewport
   *          the viewport the editor is shown in
   */
  public AnnotationsBar(JEditorPane editor, JViewport viewport) {
    super();
    this.editor = editor;
    this.viewport = viewport;

    setShowAnnotations(showAnnotations);

    // register listeners
    addMouseListener(this);
    addMouseMotionListener(this);

    setPreferredSize(new Dimension(10, 10));
  }

  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    throw new UnsupportedOperationException("add() not supported!");
  }

  @Override
  public void setLayout(LayoutManager mgr) {
    throw new UnsupportedOperationException("setLayout() not supported!");
  }

  /**
   * Adds a information provider.
   * 
   * @param p
   *          the provider to add
   */
  public void addInfoProvider(IInfoProvider p) {
    if (p != null && !providers.contains(p)) {
      providers.add(p);
      p.addInfoProviderListener(this);
      tokensChanged(p);
    }
  }

  /**
   * Removes an information provider.
   * 
   * @param p
   *          the provider to remove
   */
  public void removeInfoProvider(IInfoProvider p) {
    if (p != null) {
      p.removeInfoProviderListener(this);
    }
    providers.remove(p);
    BasicUtilities.invokeLaterOnEDT(new Runnable() {

      @Override
      public void run() {
        repaint();
      }

    });
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    for (IInfoProvider p : providers) {
      ILexerTokenStylizer<ILexerToken> stylizer = p.getStylizer();
      if (p.getStylizer() != null) {
        // now go thru all problem tokens and get the related line
        // number
        for (int i = 0; i < p.getTokenCount(); i++) {
          ILexerToken t = p.getToken(i);

          if (stylizer.showAnnotationFor(t)) {
            Rectangle r = getRectForMarker(t);

            // draw rectangle according to line to and line from
            Color c = stylizer.getColorFor(t);

            Color clighter =
                new Color((c.getRed() + 255) / 2, (c.getGreen() + 255) / 2,
                    (c.getBlue() + 255) / 2);
            Color cdarker =
                new Color((c.getRed() * 4) / 5, (c.getGreen() * 4) / 5,
                    (c.getBlue() * 4) / 5);

            g.setColor(clighter);
            g.fillRect(r.x, r.y, r.width, r.height);

            g.setColor(cdarker);
            g.drawRect(r.x, r.y, r.width, r.height);
          }
        }
      }
    }
  }

  /**
   * Helper function to calculate the rectangle to draw for a given problem
   * token. This can be used to actually calculate the rectangle for drawing
   * purposes but can also be used to determine an area the mouse listener
   * responses to on certain events.
   * 
   * @param t
   *          the token the rectangle is calculated for
   * @return the annotation rectangle for the passed token
   * @see #getTokenAtPos(Point)
   */
  private Rectangle getRectForMarker(ILexerToken t) {
    int maxLine =
        editor.getDocument().getDefaultRootElement().getElementCount();

    int lineFrom =
        editor.getDocument().getDefaultRootElement()
            .getElementIndex(t.getStart());

    int vh = viewport.getHeight();
    int h = 5;

    return new Rectangle(1, (vh * lineFrom) / maxLine, getWidth() - 2, h);
  }

  /**
   * Helper function to retrieve a token identified by a drawn annotation at
   * position {@code p}.
   * 
   * @param p
   *          the point in the coordinate space of the annotations bar
   * @return the token an annotation was drawn for at position {@code p} or
   *         {@code null} if no annotation was drawn at position {@code p}
   */
  private ILexerToken getTokenAtPos(Point p) {
    for (IInfoProvider pv : providers) {
      ILexerTokenStylizer<ILexerToken> stylizer = pv.getStylizer();
      if (stylizer != null) {
        for (int i = pv.getTokenCount() - 1; i >= 0; i--) {
          if (stylizer.showAnnotationFor(pv.getToken(i))) {
            ILexerToken t = pv.getToken(i);
            Rectangle r = getRectForMarker(t);

            // check whether mouse clicked in that r
            if (p.getX() >= r.x && p.getX() <= r.x + r.width && p.getY() >= r.y
                && p.getY() <= r.y + r.height) {
              return t;
            }
          }
        }
      }
    }
    return null;
  }

  @Override
  public void mouseClicked(MouseEvent e1) {
    // find problem marker that was clicked on
    ILexerToken t = getTokenAtPos(new Point(e1.getX(), e1.getY()));
    if (t != null) {
      Rectangle r1;
      Rectangle r2;
      try {
        // scroll so that token is approximately in the middle of
        // the viewport
        r1 = editor.getUI().modelToView(editor, t.getStart());
        r2 = editor.getUI().modelToView(editor, t.getEnd());

        editor.scrollRectToVisible(new Rectangle((r2.x + r1.x) / 2
            - viewport.getWidth() / 2, r1.y - viewport.getHeight() / 2,
            viewport.getWidth(), viewport.getHeight()));

        // select the token to highlight its position
        editor.setCaretPosition(t.getEnd());
        editor.moveCaretPosition(t.getStart());
        editor.requestFocus();
      } catch (BadLocationException | IllegalArgumentException e) {
      }
    }
  }

  /**
   * @return true if annotations bar should be visible false else
   */
  public boolean isShowAnnotations() {
    return showAnnotations;
  }

  /**
   * @param s
   *          sets whether the annotations bar should be visible
   */
  public void setShowAnnotations(boolean s) {
    this.showAnnotations = s;
    setVisible(showAnnotations);
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // nothing to do
  }

  @Override
  public void mouseExited(MouseEvent e) {
    setCursor(Cursor.getDefaultCursor());
    super.setToolTipText(null);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    // nothing to do
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // nothing to do
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // nothing to do
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    // find a problem marker for mouse position and change cursor
    // accordingly
    ILexerToken t = getTokenAtPos(new Point(e.getX(), e.getY()));
    ILexerTokenStylizer<ILexerToken> stylizer = getStylizerForToken(t);
    if (t != null && stylizer != null) {
      // change cursor
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      // and set tooltip
      super.setToolTipText(stylizer.getTooltipFor(t));
    } else {
      // restore default cursor and tooltip
      setCursor(Cursor.getDefaultCursor());
      super.setToolTipText(null);
    }
  }

  /**
   * Gets the stylizer for a token by searching within all registered
   * {@link IInfoProvider}s.
   * 
   * @param t
   *          the t
   * @return the stylizer for given token
   */
  private ILexerTokenStylizer<ILexerToken> getStylizerForToken(ILexerToken t) {
    for (IInfoProvider p : providers) {
      for (int i = 0; i < p.getTokenCount(); i++) {
        ILexerToken token = p.getToken(i);
        if (token.equals(t)) {
          return p.getStylizer();
        }
      }
    }
    return null;
  }

  @Override
  public JToolTip createToolTip() {
    return tooltipInstance;
  }

  @Override
  public void remove(int index) {
    throw new UnsupportedOperationException("remove() is not supported;");
  }

  @Override
  public void removeAll() {
    throw new UnsupportedOperationException("removeAll() is not supported;");
  }

  @Override
  public void setToolTipText(String text) {
    throw new UnsupportedOperationException(
        "setToolTipText() is not supported; use Stylizers for this instead");
  }

  @Override
  public void tokenInserted(IInfoProvider provider, int tokeIndex) {
    tokensChanged(provider);
  }

  @Override
  public void tokenRemoved(IInfoProvider provider, int tokenIndex) {
    tokensChanged(provider);
  }

  @Override
  public void tokensChanged(IInfoProvider provider) {
    if (providers.contains(provider)) {
      BasicUtilities.invokeLaterOnEDT(new Runnable() {

        @Override
        public void run() {
          repaint();
        }

      });
    }
  }

}
