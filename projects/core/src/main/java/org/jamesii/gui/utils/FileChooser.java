/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;

import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.utils.history.History;

/**
 * An alternative {@link JFileChooser} derivate that stores previously used
 * directories into {@link History}. This way it is possible to restore
 * previously used directories individual for each {@link FileChooser}
 * identified by the specified id. Using the id it is also possible to share
 * used directories between say open and save dialogs.
 * 
 * @author Stefan Rybacki
 */
public class FileChooser extends JFileChooser {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -7347758911126839166L;

  /**
   * The chooser id used to access the {@link History}.
   */
  private String chooserId;

  /**
   * Instantiates a new file chooser using the given id as identificator within
   * the {@link History}. It also sets the last used directory as starting
   * directory.
   * 
   * @param id
   *          the id
   */
  public FileChooser(String id) {
    super();
    chooserId = id;
    restoreCurrentDirectory();
  }

  /**
   * Helper method to restore current directory from {@link History}.
   */
  private final void restoreCurrentDirectory() {
    if (chooserId != null) {
      List<String> latestValues =
          History.getValues(chooserId, true, History.LATEST, 1);
      if (latestValues != null && latestValues.size() == 1) {
        setCurrentDirectory(new File(latestValues.get(0)));
      }
    }
  }

  /**
   * Instantiates a new file chooser and sets the specified directory.
   * 
   * @param id
   *          the id of the file chooser for later restoring of set directory
   * @param currentDirectory
   *          the current directory to use
   */
  public FileChooser(String id, String currentDirectory) {
    super(currentDirectory);
    chooserId = id;
    storeCurrentDirectory();
  }

  /**
   * Helper method to store current directory into {@link History}.
   */
  private final void storeCurrentDirectory() {
    File sf = getSelectedFile();
    if (sf != null && chooserId != null) {
      if (sf.isDirectory()) {
        History.putValueIntoHistory(chooserId, sf.getAbsolutePath());
      } else {
        History.putValueIntoHistory(chooserId, getCurrentDirectory()
            .getAbsolutePath());
      }
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * <b>if the specified parent is {@code null}
   * {@link org.jamesii.gui.application.IWindowManager#getMainWindow()} is used
   * as parent.
   */
  @Override
  public int showDialog(Component parent, String approveButtonText) {
    if (parent == null && WindowManagerManager.getWindowManager() != null) {
      parent = WindowManagerManager.getWindowManager().getMainWindow();
    }
    int showDialog = super.showDialog(parent, approveButtonText);

    if (showDialog == APPROVE_OPTION) {
      storeCurrentDirectory();
    }

    return showDialog;
  }

  /**
   * {@inheritDoc}
   * <p>
   * <b>if the specified parent is {@code null}
   * {@link org.jamesii.gui.application.IWindowManager#getMainWindow()} is used
   * as parent.
   */
  @Override
  public int showOpenDialog(Component parent) {
    if (parent == null && WindowManagerManager.getWindowManager() != null) {
      parent = WindowManagerManager.getWindowManager().getMainWindow();
    }
    int showDialog = super.showOpenDialog(parent);

    if (showDialog == APPROVE_OPTION) {
      storeCurrentDirectory();
    }

    return showDialog;
  }

  /**
   * {@inheritDoc}
   * <p>
   * <b>if the specified parent is {@code null}
   * {@link org.jamesii.gui.application.IWindowManager#getMainWindow()} is used
   * as parent.
   */
  @Override
  public int showSaveDialog(Component parent) throws HeadlessException {
    if (parent == null && WindowManagerManager.getWindowManager() != null) {
      parent = WindowManagerManager.getWindowManager().getMainWindow();
    }
    int showDialog = super.showSaveDialog(parent);

    if (showDialog == APPROVE_OPTION) {
      storeCurrentDirectory();
    }

    return showDialog;
  }

  @Override
  public void setCurrentDirectory(File dir) {
    super.setCurrentDirectory(dir);
    storeCurrentDirectory();
  }
}
