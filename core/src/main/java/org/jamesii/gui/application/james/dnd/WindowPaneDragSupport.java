/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james.dnd;

import java.awt.Point;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.IOException;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.james.WindowPane;

/**
 * This class adds support of drag functionality to the {@link WindowPane} class
 * that is internally used in the JAMES II GUI.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change or to vanish in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
public class WindowPaneDragSupport implements DragGestureListener,
    DragSourceListener {
  /**
   * The {@link WindowPane} this class is used for
   */
  private WindowPane pane;

  /** The drag source. */
  private DragSource dragSource;

  /**
   * Creates a window pane drag support
   * 
   * @param pane
   *          the {@link WindowPane} this drag support is used for
   */
  public WindowPaneDragSupport(WindowPane pane) {
    this.pane = pane;
    dragSource = DragSource.getDefaultDragSource();
    dragSource.createDefaultDragGestureRecognizer(pane,
        DnDConstants.ACTION_MOVE, this);
  }

  @Override
  public void dragGestureRecognized(DragGestureEvent dge) {
    // find out whether an actual tab was dragged
    Point dragOrigin = dge.getDragOrigin();
    int tabIndex =
        pane.getUI().tabForCoordinate(pane, dragOrigin.x, dragOrigin.y);

    // if tab is valid initiate drag
    if (tabIndex >= 0 && tabIndex < pane.getTabCount()) {
      // get IWindow for tab
      final IWindow window = pane.getWindowAt(tabIndex);
      WindowTransferable data = new WindowTransferable(window);
      try {
        dge.startDrag(DragSource.DefaultMoveDrop, data, this);
        WindowManagerManager.getWindowManager().getMainWindow().getGlassPane()
            .setVisible(true);
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }
  }

  @Override
  public void dragDropEnd(DragSourceDropEvent dsde) {
    // basic support for dropping outside of main window to support dialog drops
    // (hack so far)
    if (!dsde.getDropSuccess()
        && !WindowManagerManager.getWindowManager().getMainWindow().getBounds()
            .contains(dsde.getLocation())) {
      try {
        IWindow window =
            (IWindow) dsde.getDragSourceContext().getTransferable()
                .getTransferData(WindowDataFlavor.getInstance());
        if (window != null) {
          pane.changeContribution(window,
              Contribution.DIALOG);
        }
      } catch (UnsupportedFlavorException | IOException e) {
        SimSystem.report(e);
      }
    }

    WindowManagerManager.getWindowManager().getMainWindow().getGlassPane()
        .setVisible(false);

  }

  @Override
  public void dragEnter(DragSourceDragEvent dsde) {
  }

  @Override
  public void dragExit(DragSourceEvent dse) {
  }

  @Override
  public void dragOver(DragSourceDragEvent dsde) {

  }

  @Override
  public void dropActionChanged(DragSourceDragEvent dsde) {
  }
}
