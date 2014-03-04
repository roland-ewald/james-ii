/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.tools;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IPerformance;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemScheme;

/**
 * Abstract super class of performance extractors. This are tools to format and
 * compile data from the {@link IPerformanceDatabase} in a somewhat useful
 * manner.
 * 
 * @see IPerformanceDatabase
 * 
 * @author Roland Ewald
 * 
 */
public abstract class AbstractPerformanceExtractor {

  /** The string to match the scheme URI. */
  private final String schemeURIMatch;

  /** The performance database. */
  private final IPerformanceDatabase perfDB;

  /** The verbosity flag. */
  private boolean verbose = true;

  /**
   * The minimal number of replications so that a runtime configuration is
   * included.
   */
  private int minReplications = 1;

  /**
   * Instantiates a new performance extractor.
   * 
   * @param schemeMatchURI
   *          the scheme URI to be matched
   * @param performanceDB
   *          the performance database
   */
  public AbstractPerformanceExtractor(String schemeMatchURI,
      IPerformanceDatabase performanceDB) {
    schemeURIMatch = schemeMatchURI;
    perfDB = performanceDB;
  }

  /**
   * Initialises the performance extraction.
   * 
   * @throws Exception
   *           the exception
   */
  protected void init() {
    perfDB.open();
  }

  /**
   * Fetches the problem schemes that match the desired URI part from the
   * performance database.
   * 
   * @return the list of matching problem schemes
   */
  protected List<IProblemScheme> fetchMatchingSchemesFromDB() {
    List<IProblemScheme> schemes = perfDB.getAllProblemSchemes();
    List<IProblemScheme> filteredSchemes = new ArrayList<>();
    for (IProblemScheme scheme : schemes) {
      if (verbose) {
        SimSystem.report(Level.INFO, "Checking problem scheme with URI:"
            + scheme.getUri());
      }
      if (scheme.getUri().toString().contains(schemeURIMatch)) {
        filteredSchemes.add(scheme);
      }
    }
    return filteredSchemes;
  }

  /**
   * Gathers the performances of a specific type for a list of applications.
   * 
   * @param apps
   *          the applications
   * @param perfType
   *          the performance type
   * 
   * @return the list of performance values for this type
   */
  protected List<Double> getPerformances(List<IApplication> apps,
      IPerformanceType perfType) {
    List<Double> perfs = new ArrayList<>();
    for (IApplication app : apps) {
      IPerformance perf = null;
      try {
        perf = perfDB.getPerformance(app, perfType);
      } catch (Exception ex) {
        SimSystem.report(Level.INFO,
            "Could not get performance for application #" + app.getID()
                + " and performance type #" + perfType.getID(), ex);
      }
      if (perf == null) {
        continue;
      }
      perfs.add(perf.getPerformance());
    }
    return perfs;
  }

  /**
   * Checks if report level is set to verbose.
   * 
   * @return true, if is verbose
   */
  public boolean isVerbose() {
    return verbose;
  }

  /**
   * Sets the report level to verbose.
   * 
   * @param verbose
   *          the new verbose
   */
  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /**
   * Gets the mininimal number of sufficient replications.
   * 
   * @return the minimal number of sufficient replications
   */
  public int getMinReplications() {
    return minReplications;
  }

  /**
   * Sets the mininimal number of sufficient replications.
   * 
   * @param minReplications
   *          the new mininimal number of sufficient replications
   */
  public void setMinReplications(int minReplications) {
    this.minReplications = minReplications;
  }

  public IPerformanceDatabase getPerfDB() {
    return perfDB;
  }

}
