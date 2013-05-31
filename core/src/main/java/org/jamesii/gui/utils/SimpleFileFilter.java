/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.io.File;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import org.jamesii.core.util.misc.Files;

/**
 * A simple file filter.
 * 
 * @author Roland Ewald
 */
public class SimpleFileFilter extends FileFilter implements java.io.FileFilter {

  /** Determines if directories are allowed. */
  private boolean allowDirecotries = true;

  /** Characteristic file ending. */
  private String[] allowedEndings;

  /** Description of that type of files. */
  private String description;

  /** Determines if hidden files should be filtered. */
  private boolean omittingHiddenFiles = true;

  /**
   * Instantiates a new simple file filter.
   * 
   * @param endings
   *          the endings
   * @param description
   *          the description
   */
  public SimpleFileFilter(List<String> endings, String description) {
    this(endings.toArray(new String[endings.size()]), description);
  }

  /**
   * Constructor for filtering a single kind of files.
   * 
   * @param ending
   *          the ending
   * @param description
   *          the description
   */
  public SimpleFileFilter(String ending, String description) {
    this(new String[] { ending }, description);
  }

  /**
   * Default constructor.
   * 
   * @param endings
   *          array of allowed endings
   * @param description
   *          filter description
   */
  public SimpleFileFilter(String[] endings, String description) {
    this.allowedEndings = endings.clone();
    this.description = description;
  }

  /**
   * Accept.
   * 
   * @param f
   *          the f
   * 
   * @return true, if accept
   * 
   * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
   */
  @Override
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return allowDirecotries;
    }

    if (f.isHidden() && isOmittingHiddenFiles()) {
      return false;
    }

    String fileEnding = Files.getFileEnding(f);

    for (String allowedEnding : allowedEndings) {
      if (fileEnding.equals(allowedEnding)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Gets the allowed endings.
   * 
   * @return Returns the fileEnding.
   */
  public String[] getAllowedEndings() {
    return allowedEndings.clone();
  }

  /**
   * Gets the description.
   * 
   * @return the description
   * 
   * @see javax.swing.filechooser.FileFilter#getDescription()
   */
  @Override
  public String getDescription() {

    StringBuilder endingsDescription = new StringBuilder();

    for (String allowedEnding : allowedEndings) {
      if (endingsDescription.length() > 0) {
        endingsDescription.append(", ");
      }
      endingsDescription.append("(*.");
      endingsDescription.append(allowedEnding);
      endingsDescription.append(")");
    }

    endingsDescription.append(" : ");
    endingsDescription.append(description);

    return endingsDescription.toString();
  }

  /**
   * Checks if is allow direcotries.
   * 
   * @return Returns the allowDirecotries.
   */
  public boolean isAllowDirecotries() {
    return allowDirecotries;
  }

  /**
   * Checks if is omitting hidden files.
   * 
   * @return true, if is omitting hidden files
   */
  public boolean isOmittingHiddenFiles() {
    return omittingHiddenFiles;
  }

  /**
   * Sets the allow direcotries.
   * 
   * @param allowDirecotries
   *          The allowDirecotries to set.
   */
  public void setAllowDirecotries(boolean allowDirecotries) {
    this.allowDirecotries = allowDirecotries;
  }

  /**
   * Sets the allowed endings.
   * 
   * @param endings
   *          The fileEnding to set.
   */
  public void setAllowedEndings(String[] endings) {
    this.allowedEndings = endings.clone();
  }

  /**
   * Sets the description.
   * 
   * @param description
   *          The description to set.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Sets the omitting hidden files.
   * 
   * @param omittingHiddenFiles
   *          the new omitting hidden files
   */
  public void setOmittingHiddenFiles(boolean omittingHiddenFiles) {
    this.omittingHiddenFiles = omittingHiddenFiles;
  }

}
