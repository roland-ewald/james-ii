package org.jamesii.resultreport.util.experiments;


import java.util.Map;

import org.jamesii.resultreport.ResultReportSection;

/**
 * An element of an experiment report.
 * 
 * @author Roland Ewald
 * 
 */
public interface IExperimentReportElement {

  /**
   * Executes experiment to generate the performance data.
   */
  void executeExperiment();

  /**
   * Checks whether the experiment has been executed successfully. May only
   * return true *after* {@link IExperimentReportElement}
   * {@link #executeExperiment()} has been called and returned 'true'.
   * 
   * @return true, if successful
   */
  boolean isSuccessful();

  /**
   * Creates the report section that describes the given experiment. Only
   * returns a valid result in case {@link IExperimentReportElement}
   * {@link #isSuccessful()} is true;
   * 
   * @return the result report section
   */
  ResultReportSection createReportSection();

  /**
   * Sets the short name map. See {@link ExperimentReporter#shortNameMap}. Will
   * be called by the {@link ExperimentReporter}.
   * 
   * @param shortNameMap
   *          the short-name map (from the reporter)
   */
  void setShortNameMap(Map<String, String> shortNameMap);
}
