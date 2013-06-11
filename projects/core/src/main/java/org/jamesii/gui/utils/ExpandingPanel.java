/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jamesii.core.util.misc.Colors;

/**
 * A component, containing a {@link JPanel}, as well as a caption. By default
 * only the caption is visible. The {@link ExpandingPanel} can then be expanded
 * to reveal the embedded {@link JPanel}.
 * <p>
 * The embedded panel can be accessed using the {@link #getInnerPanel()} method.
 * 
 * @author Johannes Rössel
 */
public class ExpandingPanel extends JComponent implements SwingConstants {

  private final class RotatedLabelExtension extends RotatedLabel {
    /** Serialisation ID. */
    private static final long serialVersionUID = 1178909904587543935L;

    /**
     * @param text
     */
    private RotatedLabelExtension(String text) {
      super(text);
    }

    @Override
    public void updateUI() {
      super.updateUI();
      setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    }
  }

  private final class SplitterMouseAdapter extends MouseAdapter {
    @Override
    public void mouseEntered(MouseEvent e) {
      if (isOnSplitter(e)) {
        if (direction == VERTICAL) {
          setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
        } else {
          setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
        }
      }
    }

    @Override
    public void mouseExited(MouseEvent e) {
      setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mousePressed(MouseEvent e) {
      // log coordinates
      clickX = e.getX();
      clickY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      if (isOnSplitter(e)) {
        if (direction == VERTICAL) {
          setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
        } else {
          setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
        }
      } else {
        setCursor(Cursor.getDefaultCursor());
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (isExpanded() && ExpandingPanel.this.resizable) {
        // if left button
        if (direction == VERTICAL) {
          setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
          Dimension preferredSize = innerPanel.getPreferredSize();
          if (left) {
            preferredSize.width += e.getX() - clickX;
          } else {
            preferredSize.width -= e.getX() - clickX;
          }
          innerPanel.setPreferredSize(preferredSize);
          innerPanel.revalidate();

        } else {
          setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
          Dimension preferredSize = innerPanel.getPreferredSize();
          if (left) {
            preferredSize.height += e.getY() - clickY;
          } else {
            preferredSize.height -= e.getY() - clickY;
          }
          innerPanel.setPreferredSize(preferredSize);
          innerPanel.revalidate();
        }
      }
    }
  }

  /** Icon for the chevron. */
  private final class ChevronIcon implements Icon {

    @Override
    public int getIconHeight() {
      return 16;
    }

    @Override
    public int getIconWidth() {
      return 16;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D gr = (Graphics2D) g.create();
      gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);

      // chevron
      Path2D.Double path = new Path2D.Double();
      path.moveTo(5, 7);
      path.lineTo(8, 10);
      path.lineTo(11, 7);

      // draw the chevron
      Color co = c.getForeground();
      gr.setColor(co);
      if (!c.isEnabled()) {
        gr.setColor(Colors.getIntermediateColor(co, c.getBackground()));
      }
      gr.setStroke(new BasicStroke(1.2f));
      gr.drawOval(x + 2, y + 2, 12, 12);
      gr.setStroke(new BasicStroke(1.5f));

      switch (getExpandingDirection()) {
      case NORTH:
      case WEST:
        path.transform(AffineTransform.getQuadrantRotateInstance(2, 8, 8));
        break;
      default:
        break;
      }

      if (isExpanded()) {
        path.transform(AffineTransform.getQuadrantRotateInstance(2, 8, 8));
      }

      // move the chevron to the correct position. We must do this last,
      // otherwise the rotates mess everything up
      path.transform(AffineTransform.getTranslateInstance(x, y));

      gr.draw(path);
    }
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = 3466640682918169259L;

  /** The component contained in the {@link ExpandingPanel}. */
  private final JPanel innerPanel = new JPanel();

  /** The caption to be displayed for the {@link ExpandingPanel}. */
  private String caption;

  /**
   * The direction this panel expands to. Can be one of {@code NORTH},
   * {@code EAST}, {@code SOUTH} or {@code WEST}.
   */
  private int expandingDirection = SOUTH;

  /** A value indicating whether the panel is currently expanded. */
  private boolean isExpanded = false;

  /** The label for the caption. */
  private RotatedLabel label;

  /**
   * The can expand flag.
   */
  private boolean canExpand = true;

  /**
   * The can collapse flag.
   */
  private boolean canCollapse = true;

  /** horizontal orientation of the splitter */
  private static final int HORIZONTAL = 0;

  /** vertical orientation of the splitter */
  private static final int VERTICAL = 1;

  /**
   * position of draggable splitter left/top of component if {@code true}
   * right/bottom else
   */
  private boolean left;

  /**
   * the direction of the splitter ({@link #HORIZONTAL} or {@link #VERTICAL}
   */
  private int direction;

  /** Last mouse pressed Postion : Y-Axis */
  private int clickY;

  /** Last mouse pressed Postion : X-Axis */
  private int clickX;

  /** the width of the splitter */
  private int splitterSize = 3;

  /** Indicates whether the panel can be resized or not */
  private boolean resizable;

  /**
   * Initialises a new instance of the {@link ExpandingPanel} class. The caption
   * is set to an empty string and the expanding direction to {@code SOUTH}.
   */
  public ExpandingPanel() {
    this("", SOUTH);
  }

  /**
   * Initialises a new instance of the {@link ExpandingPanel} class using the
   * given caption and expanding direction. <br>
   * The resizable ability will be deactivated. If you want activate the
   * resizable ability you can call {@link #setResizable(boolean)} or you can
   * call the other consturuktor {@link #ExpandingPanel(String, int, boolean)}.
   * 
   * @param caption
   *          The caption for the panel.
   * @param expandingDirection
   *          The direction for the panel to expand to.
   */
  public ExpandingPanel(String caption, int expandingDirection) {
    this(caption, expandingDirection, false);
  }

  /**
   * Initialises a new instance of the {@link ExpandingPanel} class using the
   * given caption,expanding direction and whether it can be resized or not.
   * 
   * @param caption
   *          The caption for the panel.
   * @param expandingDirection
   *          The direction for the panel to expand to.
   * @param resizable
   *          {@code true} if can be resized, else {@code false}
   */
  public ExpandingPanel(String caption, int expandingDirection,
      boolean resizable) {
    setCaption(caption);
    setExpandingDirection(expandingDirection);
    initializeComponent();
    setResizable(resizable);
  }

  /** Layout the control. */
  private void initializeComponent() {
    this.removeAll();
    this.setLayout(new BorderLayout());

    label = new RotatedLabelExtension(caption);
    label.setEnabled(true);
    if (expandingDirection == EAST || expandingDirection == WEST) {
      label.setDirection(RotatedLabel.Direction.VERTICAL_UP);
    }

    label.setIcon(new ChevronIcon());
    label.setDisabledIcon(new ChevronIcon());

    // hover effect
    label.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseEntered(MouseEvent e) {
        if (isEnabled()
            && ((!isExpanded && canExpand) || (isExpanded && canCollapse))) {
          label.setForeground(Colors.getIntermediateColor(
              ExpandingPanel.this.getForeground(), label.getBackground()));

        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        if (isEnabled()) {
          label.setForeground(ExpandingPanel.this.getForeground());
        }
      }
    });

    // expanding functionality
    label.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (!isOnSplitter(e)) {
          if (isEnabled()) {
            setExpanded(!isExpanded());
          }
        }
      }
    });

    MouseAdapter splitter = new SplitterMouseAdapter();

    label.addMouseListener(splitter);
    label.addMouseMotionListener(splitter);

    String labelPlacement;

    switch (getExpandingDirection()) {
    case NORTH:
      labelPlacement = BorderLayout.NORTH;
      break;
    case EAST:
      labelPlacement = BorderLayout.EAST;
      break;
    case WEST:
      labelPlacement = BorderLayout.WEST;
      break;
    case SOUTH:
    default:
      labelPlacement = BorderLayout.SOUTH;
      break;
    }
    this.add(label, labelPlacement);
  }

  /**
   * Indicates if the cursor is above the splitter.
   * 
   * @param e
   *          The MouseEvent
   * @return true if on splitter
   */
  private boolean isOnSplitter(MouseEvent e) {
    switch (getExpandingDirection()) {
    case NORTH:
      return (e.getPoint().getY() <= splitterSize) && isExpanded() && resizable;
    case SOUTH:
      return (label.getHeight() - e.getPoint().getY() <= splitterSize)
          && isExpanded() && resizable;
    case EAST:
      return (label.getWidth() - e.getPoint().getX() <= splitterSize)
          && isExpanded() && resizable;
    case WEST:
      return (e.getPoint().getX() <= splitterSize) && isExpanded() && resizable;
    default:
      return false;
    }
  }

  /**
   * Sets whether the panel is resizable.
   * 
   * @param resizable
   *          true if resizable, false else
   */
  public final void setResizable(boolean resizable) {
    this.resizable = resizable;
  }

  /**
   * Gets the embedded panel inside the {@link ExpandingPanel}.
   * 
   * @return The {@link JPanel} inside the {@link ExpandingPanel}.
   */
  public JComponent getInnerPanel() {
    return innerPanel;
  }

  /**
   * Gets the caption that is displayed.
   * 
   * @return The caption.
   */
  public String getCaption() {
    return caption;
  }

  /**
   * Sets the caption for this {@link ExpandingPanel}.
   * 
   * @param caption
   *          The new caption.
   */
  public final void setCaption(String caption) {
    String oldCaption = this.caption;
    this.caption = caption;
    firePropertyChange("caption", oldCaption, caption);
  }

  /**
   * Gets the direction this panel expands to.
   * <p>
   * Is one of the following constants defined in {@link SwingConstants}:
   * {@code NORTH}, {@code EAST}, {@code SOUTH} or {@code WEST}
   * 
   * @return The expanding direction.
   */
  public int getExpandingDirection() {
    return expandingDirection;
  }

  /**
   * Set the direction this panel will expand to.
   * 
   * @param expandingDirection
   *          The new expanding direction.
   */
  public final void setExpandingDirection(int expandingDirection) {
    int oldExpandingDirection = this.expandingDirection;
    switch (expandingDirection) {
    case NORTH:
    case SOUTH:
    case EAST:
    case WEST:
      this.expandingDirection = expandingDirection;
      firePropertyChange("expandingDirection", oldExpandingDirection,
          expandingDirection);
      initializeComponent();
      break;
    default:
      throw new IllegalArgumentException(
          "expandingDirection must be one of the following constants defined in SwingConstants: NORTH, EAST, SOUTH, WEST.");
    }

    switch (getExpandingDirection()) {
    case NORTH:
      direction = HORIZONTAL;
      left = false;
      break;
    case SOUTH:
      direction = HORIZONTAL;
      left = true;
      break;
    case WEST:
      direction = VERTICAL;
      left = false;
      break;
    case EAST:
      direction = VERTICAL;
      left = true;
      break;
    default:
    }

  }

  @Override
  public void setOpaque(boolean isOpaque) {
    super.setOpaque(isOpaque);
    label.setOpaque(isOpaque);
    innerPanel.setOpaque(isOpaque);
  }

  @Override
  public void setBackground(Color bg) {
    super.setBackground(bg);
    label.setBackground(bg);
    innerPanel.setBackground(bg);
  }

  @Override
  public void setForeground(Color fg) {
    super.setForeground(fg);
    label.setForeground(fg);
    innerPanel.setForeground(fg);
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    label.setEnabled(enabled);
    innerPanel.setEnabled(enabled);
  }

  /**
   * Sets a value, indicating whether the panel is expanded or not.
   * 
   * @param isExpanded
   *          {@code true} if the panel should be expanded, {@code false}
   *          otherwise.
   */
  public void setExpanded(boolean isExpanded) {

    if ((this.isExpanded || !isExpanded || !canExpand)
        && (!this.isExpanded || isExpanded || !canCollapse)) {
      return;
    }

    boolean oldValue = this.isExpanded;
    if (isExpanded) {
      this.add(innerPanel, BorderLayout.CENTER);
    } else {
      this.remove(innerPanel);
    }
    this.isExpanded = isExpanded;
    firePropertyChange("expanded", oldValue, isExpanded);
    SwingUtilities.updateComponentTreeUI(this);
    this.revalidate();
    this.repaint();
  }

  /**
   * Gets a value indicating whether the panel is expanded or not.
   * 
   * @return A value indicating whether the panel is expanded or not.
   */
  public boolean isExpanded() {
    return isExpanded;
  }

  /**
   * Sets the can expand.
   * 
   * @param canExpand
   *          the new can expand
   */
  public void setCanExpand(boolean canExpand) {
    if (this.canExpand != canExpand) {
      this.canExpand = canExpand;
      firePropertyChange("canExpand", !canExpand, canExpand);
    }
  }

  /**
   * Sets the can collapse.
   * 
   * @param canCollapse
   *          the new can collapse
   */
  public void setCanCollapse(boolean canCollapse) {
    if (this.canCollapse != canCollapse) {
      this.canCollapse = canCollapse;
      firePropertyChange("canCollapse", !canCollapse, canCollapse);
    }
  }

  /**
   * Checks if is can expand.
   * 
   * @return true, if is can expand
   */
  public boolean isCanExpand() {
    return canExpand;
  }

  /**
   * Checks if is can collapse.
   * 
   * @return true, if is can collapse
   */
  public boolean isCanCollapse() {
    return canCollapse;
  }

}
