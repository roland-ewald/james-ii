/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.util.experiments;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.resultreport.ResultReportSection;

/**
 * Helper class to define {@link BaseExperiment} configurations, execute them,
 * and generate suitable result views.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class SingleExperimentReportElement extends
    ExperimentReportElement {

  /** The experiment. */
  private final BaseExperiment experiment = new BaseExperiment();

  /** The name. */
  private final String name;

  /** The description. */
  private final String description;

  /** The model URI. */
  private final URI modelURI;

  /**
   * Instantiates a new experiment report element.
   * 
   * @param theName
   *          the name
   * @param theDescription
   *          the description
   * @param theModelURI
   *          the model URI
   */
  public SingleExperimentReportElement(String theName, String theDescription,
      String theModelURI) {
    name = theName;
    description = theDescription;
    URI theURI = null;
    try {
      theURI = new URI(theModelURI);
    } catch (URISyntaxException e) {
      SimSystem.report(Level.SEVERE, "Model URI '" + "' is malformed.", e);
    }
    modelURI = theURI;
  }

  @Override
  public void executeExperiment() {
    experiment.setModelLocation(modelURI);
    configureExperiment();
    experiment.execute();
    experimentFinished();
  }

  /**
   * Configure the experiment as desired. The model location is already set.
   */
  protected abstract void configureExperiment();

  /**
   * Called after experiment is finished. Does nothing, but can be overridden.
   */
  protected abstract void experimentFinished();

  @Override
  public final ResultReportSection createReportSection() {
    ResultReportSection reportSection =
        new ResultReportSection(name, description);
    addContent(reportSection);
    return reportSection;
  }

  /**
   * Adds the content of the (finished) experiment to the given report section.
   * 
   * @param reportSection
   *          the report section
   */
  protected abstract void addContent(ResultReportSection reportSection);

  protected BaseExperiment getExperiment() {
    return experiment;
  }

  protected URI getModelURI() {
    return modelURI;
  }

}
