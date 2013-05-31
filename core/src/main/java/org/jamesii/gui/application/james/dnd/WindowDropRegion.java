/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james.dnd;

import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.james.IWindowCreator;

/**
 * Implementation of a {@link IDropRegion} for {@link IWindow}s. That means this
 * drop region accepts {@link IWindow} drags provided in a proper
 * {@link WindowDataFlavor}.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change or to vanish in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
public class WindowDropRegion implements IDropRegion {
  /**
   * The bounds of the drop region.
   */
  private final Rectangle bounds;

  /**
   * The contribution this region represents.
   */
  private Contribution contribution;

  private final IWindowCreator windowCreator;

  /**
   * Creates a new drop region usable in {@link DropableGlassPane} that supports
   * {@link WindowDataFlavor} and {@link DnDConstants#ACTION_MOVE}
   * 
   * @param bounds
   *          the drop region's bounds
   * @param contribution
   *          the contribution this region represents
   */
  public WindowDropRegion(Rectangle bounds, Contribution contribution,
      IWindowCreator creator) {
    this.bounds = bounds;
    this.contribution = contribution;
    this.windowCreator = creator;
  }

  @Override
  public Rectangle getBounds() {
    return bounds;
  }

  @Override
  public void dragEnter(DropTargetDragEvent dtde) {
    if (!dtde.getTransferable().isDataFlavorSupported(
        WindowDataFlavor.getInstance())) {
      dtde.rejectDrag();
    } else {
      dtde.acceptDrag(DnDConstants.ACTION_MOVE);
    }
  }

  @Override
  public void dragExit(DropTargetEvent dte) {
  }

  @Override
  public void dragOver(DropTargetDragEvent dtde) {
    if (!dtde.getTransferable().isDataFlavorSupported(
        WindowDataFlavor.getInstance())) {
      dtde.rejectDrag();
    } else {
      dtde.acceptDrag(DnDConstants.ACTION_MOVE);
    }
  }

  @Override
  public void drop(DropTargetDropEvent dtde) {
    if (!dtde.getTransferable().isDataFlavorSupported(
        WindowDataFlavor.getInstance())) {
      dtde.rejectDrop();
    } else {
      dtde.acceptDrop(DnDConstants.ACTION_MOVE);
      dtde.dropComplete(completeDrop(dtde));
    }
  }

  /**
   * Override this method to handle drops made to that drop location. For
   * instance work with the provided transferable and complete the drop. By
   * default this method changes the {@link Contribution} of the transfered
   * {@link IWindow} to the specified {@link Contribution}
   * 
   * @param dtde
   *          the forwarded {@link DropTargetDropEvent}
   * @return true if the drop succeeded, false else
   */
  protected boolean completeDrop(DropTargetDropEvent dtde) {
    IWindow window;
    try {
      window =
          (IWindow) dtde.getTransferable().getTransferData(
              WindowDataFlavor.getInstance());
      if (window != null) {
        windowCreator.changeContribution(window,
            contribution);
      }
    } catch (Throwable e) {
      SimSystem.report(e);
    }
    return true;
  }

  @Override
  public void dropActionChanged(DropTargetDragEvent dtde) {
    dtde.acceptDrag(DnDConstants.ACTION_MOVE);
  }

}
