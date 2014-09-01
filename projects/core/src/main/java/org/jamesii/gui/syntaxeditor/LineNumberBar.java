/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JToolTip;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

import org.jamesii.SimSystem;
import org.jamesii.gui.tooltip.StyledToolTip;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Component that draws line numbers and problem icons for a {@link JEditorPane}
 * utilizing {@link SyntaxEditorKit}. It is supposed to be used in combination
 * with an editor that uses the syntax highlighting kit and should be placed
 * left to the viewport the editor is in. It supports line numbering, problem
 * icon drawing as well as description tooltips for drawn icons. Line numbering
 * and icons are on and off switchable independent from each other. It is
 * discouraged to use this class directly but {@link SyntaxEditor} instead.
 * 
 * @see SyntaxEditor
 * @author Stefan Rybacki
 */
final class LineNumberBar extends JComponent implements ChangeListener,
    DocumentListener, MouseMotionListener, MouseListener,
    PropertyChangeListener, IInfoProviderListener {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -2173589419132547963L;

  /**
   * The default icon width.
   */
  private static final int DEFAULT_ICON_WIDTH = 13;

  /**
   * width of shown icons
   */
  private int iconWidth = DEFAULT_ICON_WIDTH;

  /**
   * editor the line numbers and icons are shown for
   */
  private JEditorPane editor;

  /**
   * the viewport the editor is in
   */
  private JViewport viewport;

  /**
   * a cache list storing all problem tokens for whom there are currently
   * displayed icons (this is used to find icons the mouse is on faster)
   */
  private List<ILexerToken> displayedTokens = new ArrayList<>();

  /**
   * flag determining whether numbers should be shown
   */
  private boolean showNumbers = false;

  /**
   * flag determining whether icons should be shown
   */
  private boolean showIcons = false;

  /**
   * the tooltip class used by this component
   */
  private JToolTip tooltipInstance = new StyledToolTip();

  /**
   * The registered information providers.
   */
  private final List<IInfoProvider> providers = new ArrayList<>();

  /**
   * Returns a line numbering element for a {@link JEditorPane} utilizing a
   * {@link SyntaxEditorKit}. The editor pane is embedded in a {@link JViewport}
   * which is specified using {@code viewport}.
   * 
   * @param editor
   *          the editor the line numbering should be shown for
   * @param viewport
   *          the viewport the editor is in
   */
  public LineNumberBar(JEditorPane editor, JViewport viewport) {
    super();
    this.editor = editor;
    this.viewport = viewport;

    // get line count
    setFont(editor.getFont());
    changedUpdate(null);

    // register listeners
    viewport.addChangeListener(this);
    editor.getDocument().addDocumentListener(this);
    addMouseMotionListener(this);
    addMouseListener(this);

    editor.addPropertyChangeListener(this);
  }

  /**
   * @param s
   *          true if line numbers should be shown false else
   */
  public void setShowLineNumbers(boolean s) {
    showNumbers = s;
    changedUpdate(null);
  }

  /**
   * @return true if line numbers are shown false else
   */
  public boolean isShowLineNumbers() {
    return showNumbers;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // paint the area with line numbers
    if (showNumbers && isOpaque()) {
      g.setColor(getBackground());
      g.fillRect((showIcons ? iconWidth + 1 : 0), 0, getWidth()
          - (showIcons ? iconWidth - 1 : 0), viewport.getHeight());
    }

    g.setColor(getForeground());
    g.drawLine(getWidth() - 1, 0, getWidth() - 1, viewport.getHeight());

    // try to get the first element still shown in the viewport
    Rectangle vr = viewport.getViewRect();
    TextUI ui1 = editor.getUI();

    int p0 = ui1.viewToModel(editor, new Point(vr.x, vr.y));
    int p1 =
        ui1.viewToModel(editor, new Point(vr.x + vr.width, vr.y + vr.height));

    // find elements in document that contain p0 and p1
    int start =
        editor.getDocument().getDefaultRootElement().getElementIndex(p0);
    int end = editor.getDocument().getDefaultRootElement().getElementIndex(p1);

    // set clip region so that we don't paint below viewport height
    g.setClip(0, 0, getWidth() - 1, viewport.getHeight());
    // draw line numbers
    displayedTokens.clear();
    for (int i = start; i <= end; i++) {
      Element e = editor.getDocument().getDefaultRootElement().getElement(i);

      Rectangle r = new Rectangle(0, 0, 0, 0);
      try {
        r = getIconRectFor(e.getStartOffset());
      } catch (BadLocationException e1) {
        SimSystem.report(e1);
      }

      // only render numbers if showNumbes is true
      if (showNumbers) {
        Segment s =
            new Segment(Integer.toString(i + 1).toCharArray(), 0, Integer
                .toString(i + 1).length());
        int w = Utilities.getTabbedTextWidth(s, g.getFontMetrics(), 0, null, 0);
        Utilities.drawTabbedText(s, getWidth() - w - 3, r.y + r.height
            - g.getFontMetrics().getDescent(), g, null, 0);
      }

      for (IInfoProvider p : providers) {
        ILexerTokenStylizer<ILexerToken> stylizer = p.getStylizer();
        if (stylizer != null) {
          /*
           * also draw the icon of the first problem existing in this line
           * (problems are only considered for this line if the problem token
           * starts in this line)
           */
          if (showIcons) {
            List<ILexerToken> infoTokens = new ArrayList<>(p.getTokenCount());
            for (int j = 0; j < p.getTokenCount(); j++) {
              infoTokens.add(p.getToken(j));
            }

            int startIndex =
                LexerTokenUtils.findFirstIndexInRange(infoTokens, p0);
            // find problem tokens starting in that line
            for (int j = startIndex; j < infoTokens.size(); j++) {
              ILexerToken t = infoTokens.get(j);

              // show icon only in first line where the error occurred
              if (t.getStart() >= e.getStartOffset()
                  && t.getStart() < e.getEndOffset()) {
                Icon icon = stylizer.getIconFor(t);
                if (icon != null) {
                  g.drawImage(BasicUtilities.iconToImage(icon),
                          r.x, r.y, r.width, r.height, this);
                  displayedTokens.add(t);
                }
              }
            }
          }
        }
      }
    }
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
   * Helper function to calculate the icon rectangle for a drawn icon. This is
   * used for mouse events so that in a mouse event it can be determined whether
   * the mouse is over a drawn icon.
   * 
   * @param startOffset
   *          the text offset the rectangle is calculated for
   * @return the icon's bounding rectangle
   * @throws BadLocationException
   */
  private Rectangle getIconRectFor(int startOffset) throws BadLocationException {
    // get top left corner of text token position in editor
    Rectangle r = editor.getUI().modelToView(editor, startOffset);
    // convert coordinates from editor to viewport space
    r.x -= viewport.getViewPosition().x;
    r.y -= viewport.getViewPosition().y;
    // create resulting rectangle
    return new Rectangle(1, r.y, iconWidth - 1, r.height);
  }

  /**
   * Helper function to calculate the icon rectangle for a drawn icon. This is
   * used for mouse events so that in a mouse event it can be determined whether
   * the mouse is over a drawn icon.
   * 
   * @param t
   *          the token the icon rectangle is calculated for
   * @return the icon's bounding rectangle
   * @throws BadLocationException
   */
  private Rectangle getIconRectFor(ILexerToken t) throws BadLocationException {
    return getIconRectFor(t.getStart());
  }

  /**
   * @param s
   *          specifies whether icons should be shown
   */
  public void setShowIcons(boolean s) {
    showIcons = s;
    changedUpdate(null);
  }

  /**
   * @return true if icons are generally shown false else
   */
  public boolean isShowIcons() {
    return showIcons;
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        repaint();
      }
    });
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    recalculateWidth();
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    changedUpdate(e);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    changedUpdate(e);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // not needed
  }

  @Override
  public void mouseMoved(MouseEvent e1) {
    // go thru displayed tokens and find the one the mouse is over
    StringBuilder tooltip = new StringBuilder();
    int markerCount = 0;
    for (ILexerToken t : displayedTokens) {
      try {
        // calculate rectangle for drawn token icon
        Rectangle r = getIconRectFor(t);
        ILexerTokenStylizer<ILexerToken> stylizer = getStylizerForToken(t);

        if (stylizer != null) {
          // if mouse is in r
          if (e1.getX() >= r.x && e1.getX() <= r.width + r.x
              && e1.getY() >= r.y && e1.getY() <= r.y + r.height) {
            // set tooltip according to retrieved value from stylizer
            tooltip
                .append("<color"
                    + Integer.toHexString(stylizer.getColorFor(t).darker()
                        .getRGB() & 0xFFFFFF) + "/>- ");
            tooltip.append(stylizer.getDescriptionFor(t));
            tooltip.append("<br/>\n");
            markerCount++;
          }
          // TODO sr137: also add the icons for that token once icons
          // are
          // supported by StyledToolTip
        }
      } catch (BadLocationException e) {
        SimSystem.report(e);
      }
    }
    if (markerCount > 0) {
      super.setToolTipText(String.format(
          "<size 14/><b>%d Markers</b><size 12/><br/>\n<i>%s</i></b>",
          Integer.valueOf(markerCount), tooltip.toString().trim()));
    } else {
      super.setToolTipText(null);
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    // not needed
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    // not needed
  }

  @Override
  public void mouseExited(MouseEvent e) {
    super.setToolTipText(null);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    // not needed
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    // not needed
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals("document")) {
      ((Document) evt.getOldValue()).removeDocumentListener(this);
      ((Document) evt.getNewValue()).addDocumentListener(this);
      recalculateWidth();
    }
    if (evt.getPropertyName().equals("font")) {
      setFont((Font) evt.getNewValue());
      recalculateWidth();
    }
  }

  /**
   * Helper function to adjust line bar's with according to changes that so that
   * the line numbers might need more space to be displayed. This happens for
   * instance when there were 9 lines and there is one added so that the bar now
   * needs the width of two digits to display line numbers correctly.
   */
  private void recalculateWidth() {
    int maxLine =
        editor.getDocument().getDefaultRootElement().getElementCount();

    iconWidth = getFontMetrics(getFont()).getHeight() + 1;

    // measure maxLine number width
    int width = 1;
    if (showIcons) {
      width += iconWidth + 1;
    }
    if (showNumbers) {
      width +=
          Utilities.getTabbedTextWidth(new Segment(Integer.toString(maxLine)
              .toCharArray(), 0, Integer.toString(maxLine).length()),
              getFontMetrics(getFont()), 0, null, 0) + 4;
    }

    // old with so we might be able to skip the revalidating step
    final int oldwidth = getPreferredSize().width;
    final int newwidth = width;

    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        if (oldwidth != newwidth) {
          setPreferredSize(new Dimension(newwidth, 30));
          setMinimumSize(getPreferredSize());
          setSize(getPreferredSize());
          setMaximumSize(getPreferredSize());
          revalidate();
        }
        repaint();
      }
    });
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

}
