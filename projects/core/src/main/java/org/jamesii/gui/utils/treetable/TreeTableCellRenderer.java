/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.treetable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Cell renderer for rendering the first column in a {@link TreeTable} . It will
 * create a tree-like look with expand and collapse icons.
 * 
 * @author Johannes Rössel
 * @author Stefan Rybacki
 */
class TreeTableCellRenderer implements TableCellRenderer {

  /** Amount of padding for each level of depth in the tree. */
  private static int levelDepth = 12;

  /** The plus img fallback. */
  private static BufferedImage plusImgFallback = BasicUtilities
      .createCompatibleImage(9, 9, Transparency.TRANSLUCENT);

  /** The minus img fallback. */
  private static BufferedImage minusImgFallback = BasicUtilities
      .createCompatibleImage(9, 9, Transparency.TRANSLUCENT);

  /**
   * The minus img.
   */
  private static Image minusImg = minusImgFallback;

  /**
   * The plus img.
   */
  private static Image plusImg = plusImgFallback;

  /**
   * The look and feel that was active the last time one instance of this
   * renderer was used.
   */
  private static String lastLookAndFeel = "";

  // initialize the images
  static {
    Graphics g = plusImg.getGraphics();
    g.setColor(Color.GRAY);
    g.drawRect(0, 0, 8, 8);
    g.drawLine(4, 2, 4, 6);
    g.drawLine(2, 4, 6, 4);
    g.dispose();

    g = minusImg.getGraphics();
    g.setColor(Color.GRAY);
    g.drawRect(0, 0, 8, 8);
    g.drawLine(2, 4, 6, 4);
    g.dispose();
  }

  /** The wrapped renderer. */
  private TableCellRenderer wrappedRenderer;

  /**
   * Initialises a new instance of the {@link TreeTableCellRenderer} class,
   * wrapping the given {@link TableCellRenderer}. This class just provides some
   * visuals for a tree-like appearance, leaving the rest to the original
   * renderer which gets wrapped.
   * 
   * @param wrappedRenderer
   *          The renderer that is wrapped by this instance.
   */
  public TreeTableCellRenderer(TableCellRenderer wrappedRenderer) {
    this.wrappedRenderer = wrappedRenderer;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    TreeTableViewModel ttvm = (TreeTableViewModel) table.getModel();

    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    p.setOpaque(false);

    Component c =
        wrappedRenderer.getTableCellRendererComponent(table, value, isSelected,
            hasFocus, row, column);

    checkImages();

    JLabel img;
    if (ttvm.isLeaf(ttvm.getNode(row))) {
      img = new JLabel();
      img.setBorder(new CompoundBorder(
          new EmptyBorder(0, levelDepth - 3, 0, 0), img.getBorder()));
    } else if (ttvm.isExpanded(ttvm.getNode(row))) {
      img = new JLabel(new ImageIcon(minusImg));
    } else {
      img = new JLabel(new ImageIcon(plusImg));
    }

    img.setOpaque(false);
    img.setBorder(new CompoundBorder(new EmptyBorder(0, levelDepth
        * ttvm.getDepth(row) + 2, 0, 0), img.getBorder()));

    p.add(img, BorderLayout.LINE_START);
    p.add(c, BorderLayout.CENTER);

    return p;
  }

  /**
   * Checks whether images must be reloaded because of a look and feel change.
   */
  private static void checkImages() {
    if (lastLookAndFeel.equals(UIManager.getLookAndFeel().getClass().getName())) {
      return;
    }
    lastLookAndFeel = UIManager.getLookAndFeel().getClass().getName();

    plusImg = plusImgFallback;
    minusImg = minusImgFallback;

    Icon i = UIManager.getLookAndFeelDefaults().getIcon("Tree.collapsedIcon");
    if (i != null) {
      try {
        plusImg = BasicUtilities.iconToImage(i);
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }
    levelDepth = plusImg.getWidth(null) + 3;

    i = UIManager.getLookAndFeelDefaults().getIcon("Tree.expandedIcon");
    if (i != null) {
      try {
        minusImg = BasicUtilities.iconToImage(i);
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }

    levelDepth = Math.max(minusImg.getWidth(null) + 3, levelDepth);
  }

  /**
   * Checks if the specified coordinates lie virtually on the plus resp. minus
   * icon if it was drawn. This can be used by click event handlers of
   * {@link TreeTable} to determine if a click took place on the expand/collapse
   * icon.
   * 
   * @param depth
   *          the depth which is used to calculate the padding
   * @param x
   *          the x coordinate
   * @param y
   *          the y coordinate
   * @return true, if is clicked on icon
   */
  public static boolean isClickedOnIcon(int depth, int x, int y) {
    checkImages();

    return (x >= levelDepth * depth + 2 && x <= levelDepth * depth + levelDepth
        - 3);
  }
}
