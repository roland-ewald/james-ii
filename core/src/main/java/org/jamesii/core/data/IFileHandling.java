/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data;

/**
 * Interface for categorizing e.g., Factories and ensuring special method
 * availability.
 * 
 * @author Stefan Rybacki
 */
public interface IFileHandling extends IURIHandling {
  /**
   * Gets the description.
   * 
   * @return the description
   */
  String getDescription();

  /**
   * Gets the file ending.
   * 
   * @return the file ending
   */
  String getFileEnding();
}
