package org.jamesii.resultreport.util.experiments;

import java.util.Collections;
import java.util.Map;

/**
 * Super class for {@link IExperimentReportElement} implementations. Manages
 * reference to {@link ExperimentReporter#shortNameMap}.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class ExperimentReportElement implements
    IExperimentReportElement {

  /**
   * The short name map (will be set by the reporter). See
   * {@link ExperimentReporter#shortNameMap}.
   */
  private Map<String, String> shortNameMap;

  @Override
  public void setShortNameMap(Map<String, String> shortNameMap) {
    this.shortNameMap = shortNameMap;
  }

  protected Map<String, String> getShortNameMap() {
    return Collections.unmodifiableMap(shortNameMap);
  }

}
