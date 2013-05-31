/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Locale;

import org.jamesii.core.util.misc.Files;

/**
 * The default model for BrowseFSDialog implementations like
 * {@link BrowseFSDialogViaFactories}.
 * 
 * @author Valerius Weigandt
 */
public class BrowseDialogFileComponent implements IBrowseFSDialogEntry {

  /** Column names for the table. */
  private static String[] columnNames = { "Name", "Path", "Type", "Size",
      "Date" };

  /** Name of the file. */
  private String name;

  /** Path of the file. */
  private String path;

  /** Type of the file. */
  private String fileEnding;

  /** Size of the file. */
  private long size;

  /** The last modified date of the file. */
  private Date lastModDate;

  /** File of the model */
  private File file;

  /** DateFormat for the current SystemTime */
  private DateFormat dateFormat = DateFormat.getDateTimeInstance(
      DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());

  @Override
  public File getFile() {
    return file;
  }

  /**
   * Default constructor
   * 
   * @param f
   *          File to load.
   */
  public BrowseDialogFileComponent(File f) {
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
   *          File to load.
   * @return Path of the file.
   */
  private String getPath(File f) {
    int fileName = f.getName().length();
    int absolutPath = f.getAbsolutePath().length();
    return f.getAbsolutePath().substring(0, absolutPath - fileName);
  }

  @Override
  public Object getValue(int xPosition) {
    switch (xPosition) {
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

  @Override
  public String[] getColumnNames() {
    return columnNames;
  }
}
