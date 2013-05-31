/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Files;

/**
 * Simple component that allows customised file export (for simple formats).
 * 
 * @author Roland Ewald
 */
@Deprecated
public abstract class SimpleGUIFileExporter {

  /** File chooser for export function. */
  private JFileChooser fileChooser = new FileChooser("org.jamesii.file.export");
  {
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    fileChooser.setDialogTitle("Save experiment overview to CSV");
    fileChooser.setFileHidingEnabled(true);
    fileChooser.setFileFilter(new SimpleFileFilter("dat",
        "Experiment Overview Data"));
  }

  /**
   * Sequence used to separate entries when exporting experiment overview to
   * TSV.
   */
  protected static final String sepSequence = "\t";

  /**
   * Default export function.
   * 
   * @param component
   *          parent of file chooser dialog
   */
  public void export(JComponent component) {
    if (this.fileChooser.showDialog(component, "Save") == JFileChooser.APPROVE_OPTION) {
      File file = this.fileChooser.getSelectedFile();
      if (!Files.getFileEnding(file).equalsIgnoreCase("dat")) {
        file = new File(file.getAbsolutePath() + ".dat");
      }
      if (file.exists()) {
        if (JOptionPane.showConfirmDialog(null, "Overwrite file?", "The file "
            + file + " already exists. Do you really want to overwrite it?",
            JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
          return;
        }
      }
      BufferedWriter bw = null;
      try {
        bw =
            new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file)));
        export(bw);
        bw.close();
        SimSystem.report(Level.INFO, "Exported information to: " + file);
      } catch (Exception ex) {
        BasicUtilities.close(bw);
        SimSystem.report(Level.SEVERE,
            "Error while writing to '" + file.getName() + "'.", ex);
      }
    }
  }

  /**
   * Export method.
   * 
   * @param bw
   *          the writer to be used
   * 
   * @throws Exception
   *           the exception
   */
  public abstract void export(BufferedWriter bw) throws IOException;

}
