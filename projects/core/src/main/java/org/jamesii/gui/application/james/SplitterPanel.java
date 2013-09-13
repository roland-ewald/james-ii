/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * This class is used to provide an easy way to support resizable GUI components
 * avoiding {@link javax.swing.JSplitPane}. In future release JSplitPane might
 * be used instead of this class.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change or to vanish in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
class SplitterPanel extends JPanel implements MouseListener,
    MouseMotionListener {

  /** Serialization ID. */
  private static final long serialVersionUID = -4821822313418335849L;

  /**
   * the mouse click x-coordinate
   */
  private int clickX;

  /**
   * the mouse click y-coordinate
   */
  private int clickY;

  /**
   * the direction of the splitter ({@link #HORIZONTAL} or {@link #VERTICAL}
   */
  private final int direction;

  /**
   * the component to resize
   */
  private final JComponent comp;

  /**
   * position of dragable splitter left/top of component if {@code true}
   * right/bottom else
   */
  private final boolean left;

  /**
   * horizontal orientation of the splitter
   */
  public static final int HORIZONTAL = 0;

  /**
   * vertical orientation of the splitter
   */
  public static final int VERTICAL = 1;

  /**
   * Instantiates a new splitter panel.
   * 
   * @param orientation
   *          the orientation of the splitter panel ( {@link #HORIZONTAL} or
   *          {@link #VERTICAL}
   * @param component
   *          the component the component encapsulated in this panel
   * @param left
   *          the left flag indicating whether the splitter should be positioned
   *          on top/left of component if {@code true} or right/bottom else (top
   *          or bottom if orientation is {@link #HORIZONTAL} right or left
   *          else)
   */
  public SplitterPanel(int orientation, JComponent component, boolean left) {
    super(null);
    addMouseListener(this);
    addMouseMotionListener(this);
    this.direction = orientation;
    this.comp = component;
    this.left = left;
    this.setPreferredSize(new Dimension(2, 2));
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    if (direction == VERTICAL) {
      setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
    } else {
      setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
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
  public void mouseReleased(MouseEvent e) {
    setCursor(Cursor.getDefaultCursor());
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    // if left button
    if (direction == VERTICAL) {
      setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
      Dimension preferredSize = comp.getPreferredSize();
      if (left) {
        preferredSize.width += e.getX() - clickX;
      } else {
        preferredSize.width -= e.getX() - clickX;
      }
      comp.setPreferredSize(preferredSize);
      comp.revalidate();

    } else {
      setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
      Dimension preferredSize = comp.getPreferredSize();
      if (left) {
        preferredSize.height += e.getY() - clickY;
      } else {
        preferredSize.height -= e.getY() - clickY;
      }
      comp.setPreferredSize(preferredSize);
      comp.revalidate();
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
  }

}
