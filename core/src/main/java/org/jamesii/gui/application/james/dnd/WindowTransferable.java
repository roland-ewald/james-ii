/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.jamesii.gui.application.IWindow;

/**
 * This class provides a custom {@link Transferable} encapsulating an
 * {@link IWindow} that can be dragged and dropped.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change or to vanish in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
class WindowTransferable implements Transferable {
  /**
   * the window to transfer
   */
  private final IWindow window;

  /**
   * Creates a {@link Transferable} for the given {@link IWindow}
   * 
   * @param window
   *          the window to transfer
   */
  public WindowTransferable(IWindow window) {
    this.window = window;
  }

  @Override
  public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    if (WindowDataFlavor.getInstance().equals(flavor)) {
      return window;
    }

    return null;
  }

  @Override
  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[] { WindowDataFlavor.getInstance() };
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return WindowDataFlavor.getInstance().equals(flavor);
  }

}
