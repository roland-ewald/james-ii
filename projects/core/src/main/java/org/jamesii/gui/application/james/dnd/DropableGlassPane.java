/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james.dnd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jamesii.gui.utils.BasicUtilities;

/**
 * A glass pane for the JAMES II GUI that is used as replacement for the normal
 * glass pane of the main window of the JAMES II GUI. It supports the
 * registration of {@link IDropRegion} where those regions are shown when drag
 * and drop is active and the user dragged into such a region.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change or to vanish in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
public class DropableGlassPane extends JPanel implements DropTargetListener {

  /** Serialization ID. */
  private static final long serialVersionUID = -3190828166387435315L;

  /**
   * flag indicating whether drag and drop is currently in progress
   */
  private boolean dnd = false;

  /**
   * specifies the currently active {@link IDropRegion}
   */
  private IDropRegion currentDropRegion = null;

  /**
   * list of all registered {@link IDropRegion}s
   */
  private final List<IDropRegion> regions = new ArrayList<>();

  /**
   * Adds a drop region.
   * 
   * @param region
   *          the region to add
   */
  public synchronized void addDropRegion(IDropRegion region) {
    if (region != null && !regions.contains(region)) {
      regions.add(region);
    }
  }

  /**
   * Removes a drop region.
   * 
   * @param region
   *          the region to remove
   */
  public synchronized void removeDropRegion(IDropRegion region) {
    regions.remove(region);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (dnd && currentDropRegion != null) {
      g.setColor(Color.WHITE);
      g.setXORMode(Color.DARK_GRAY);
      ((Graphics2D) g).setStroke(new BasicStroke(4f));

      ((Graphics2D) g).draw(currentDropRegion.getBounds());
    }
  }

  /**
   * Instantiates a new dropable glass pane.
   */
  public DropableGlassPane() {
    super();
    setOpaque(false);
    new DropTarget(this, this);
  }

  @Override
  public void dragEnter(DropTargetDragEvent dtde) {
    currentDropRegion = getDropRegion(dtde.getLocation());
    if (currentDropRegion != null) {
      currentDropRegion.dragEnter(dtde);
    }
  }

  @Override
  public void dragExit(final DropTargetEvent dte) {
    currentDropRegion = null;
    BasicUtilities.invokeLaterOnEDT(new Runnable() {

      @Override
      public void run() {
        repaint();
      }

    });
  }

  @Override
  public void dragOver(final DropTargetDragEvent dtde) {
    IDropRegion last = currentDropRegion;
    currentDropRegion = getDropRegion(dtde.getLocation());

    if (last != currentDropRegion) {
      if (last != null) {
        last.dragExit(dtde);
      }
      if (currentDropRegion != null) {
        currentDropRegion.dragEnter(dtde);
      }
    }

    if (currentDropRegion == null) {
      dtde.rejectDrag();
    } else {
      currentDropRegion.dragOver(dtde);
    }

    BasicUtilities.invokeLaterOnEDT(new Runnable() {

      @Override
      public void run() {
        repaint();
      }

    });
  }

  /**
   * Gets a drop region that covers at the given {@code location}. If there are
   * more than one region that covers the specified {@code location} the first
   * found is returned.
   * 
   * @param location
   *          the location on the glass pane
   * 
   * @return the first registered drop region that covers the given
   *         {@code location}
   */
  private IDropRegion getDropRegion(Point location) {
    for (IDropRegion r : regions) {
      if (r.getBounds().contains(location)) {
        return r;
      }
    }
    return null;
  }

  @Override
  public void drop(DropTargetDropEvent dtde) {
    currentDropRegion = getDropRegion(dtde.getLocation());

    if (currentDropRegion == null) {
      dtde.rejectDrop();
    } else {
      currentDropRegion.drop(dtde);
    }
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent dtde) {
    currentDropRegion = getDropRegion(dtde.getLocation());
    if (currentDropRegion != null) {
      currentDropRegion.dropActionChanged(dtde);
    } else {
      dtde.rejectDrag();
    }
  }

  @Override
  public void setVisible(boolean flag) {
    if (!flag) {
      currentDropRegion = null;
    }
    dnd = flag;
    super.setVisible(flag);
  }

}
