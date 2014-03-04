/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb;

/**
 * General interface for performance database implementations.
 * 
 * @author Roland Ewald
 * 
 */
public interface IPerformanceDatabase extends IProblemSchemeDatabase,
    IProblemDefinitionDatabase, IRTConfigDatabase, IFeatureDatabase,
    IMeasureDatabase, IHardwareDatabase {

  /**
   * Creates performance database DB scheme etc. Required for initialisation.
   */
  void create();

  /**
   * Opens database.
   */
  void open();

  /**
   * Clears database.
   */
  void clear();

  /**
   * Close database connection etc.
   */
  void close();

  /**
   * Flushes the database caches (if existent).
   */
  void flush();

}
