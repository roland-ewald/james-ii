/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james.dnd;

import java.awt.datatransfer.DataFlavor;

import org.jamesii.gui.application.IWindow;

/**
 * Internally used custom {@link DataFlavor} for drag and drop of
 * {@link IWindow}s within the JAMES II GUI.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change or to vanish in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
final class WindowDataFlavor extends DataFlavor {

  /**
   * The Constant instance.
   */
  private static final WindowDataFlavor INSTANCE = new WindowDataFlavor();

  /**
   * Hidden constructor
   */
  private WindowDataFlavor() {
    super(IWindow.class, "IWindow Data Flavour");
  }

  /**
   * Gets the singleton instance of {@link WindowDataFlavor}.
   * 
   * @return singleton instance of {@link WindowDataFlavor}
   */
  public static WindowDataFlavor getInstance() {
    return INSTANCE;
  }
}
