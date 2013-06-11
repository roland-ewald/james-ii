/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package org.jamesii.gui.experiment.dialogs;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Locale;

import org.jamesii.core.util.misc.Files;
import org.jamesii.gui.utils.dialogs.IBrowseFSDialogEntry;

/**
 * Data content for BrowsExperimentsFSdialog.
 * 
 * @author Valerius Weigandt
 */

public class ModelExperimentData implements IBrowseFSDialogEntry {

  /** Holds the column names for the table. */
  private static String[] columnNames = { "Name", "Path", "Type", "Size",
      "Date" };

  /** Holds the name of the file. */
  private String name;

  /** Holds the path of the file. */
  private String path;

  /** Type of the file. */
  private String fileEnding;

  /** Size of the file. */
  private long size;

  /** The last modified date of the file. */
  private Date lastModDate;

  /** current file */
  private File file;

  /**
   * The current file
   * 
   * @return current file
   */
  @Override
  public File getFile() {
    return file;
  }

  /** Local date format */
  private DateFormat dateFormat = DateFormat.getDateTimeInstance(
      DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());

  /**
   * Default constructor
   * 
   * @param f
   *          file to be load
   */
  public ModelExperimentData(File f) {
    file = f;
    name = f.getName();
    path = getPath(f);
    fileEnding = Files.getFileEnding(f);
    lastModDate = new Date(f.lastModified());
    size = f.length();
  }

  /**
   * Returns the path of the file
   * 
   * @param f
   *          The file
   * @return Path to file
   */
  private String getPath(File f) {
    int fileName = f.getName().length();
    int absolutPath = f.getAbsolutePath().length();
    return f.getAbsolutePath().substring(0, absolutPath - fileName);
  }

  /**
   * Column names for the Table
   * 
   * @return Array of Names
   */
  @Override
  public String[] getColumnNames() {
    return columnNames;
  }

  @Override
  public Object getValue(int position) {
    switch (position) {
    case 0:
      return name;
    case 1:
      return path;
    case 2:
      return fileEnding;
    case 3:
      return size;
    case 4:
      return dateFormat.format(lastModDate);
    default:
      return null;
    }
  }
}
