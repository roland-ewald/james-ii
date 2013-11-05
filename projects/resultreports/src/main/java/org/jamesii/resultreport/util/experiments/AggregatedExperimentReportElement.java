package org.jamesii.resultreport.util.experiments;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.resultreport.ResultReportSection;

/**
 * Helper class to nest experiment reports arbitrarily.
 * 
 * @author Roland Ewald
 * 
 */
public class AggregatedExperimentReportElement extends ExperimentReportElement {

  /** The sub elements of this report element. */
  private final List<IExperimentReportElement> subElements =
      new ArrayList<>();

  /** The name of this aggregated element. */
  private final String name;

  /** The description of this aggregated element. */
  private final String description;

  /**
   * Instantiates a new aggregated experiment report element.
   * 
   * @param theName
   *          the name
   * @param theDescription
   *          the description
   */
  public AggregatedExperimentReportElement(String theName, String theDescription) {
    name = theName;
    description = theDescription;
  }

  @Override
  public void executeExperiment() {
    for (IExperimentReportElement subElement : subElements) {
      subElement.executeExperiment();
    }
  }

  @Override
  public boolean isSuccessful() {
    for (IExperimentReportElement subElement : subElements) {
      if (!subElement.isSuccessful()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public ResultReportSection createReportSection() {
    ResultReportSection aggregatedSection =
        new ResultReportSection(name, description);
    for (IExperimentReportElement subElement : subElements) {
      aggregatedSection.addSubSection(subElement.createReportSection());
    }
    return aggregatedSection;
  }

}
