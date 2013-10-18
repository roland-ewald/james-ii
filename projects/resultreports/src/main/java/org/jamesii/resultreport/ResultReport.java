/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of a result report.
 * 
 * @author Roland Ewald
 */
public class ResultReport {

  /** Name of the report. */
  private String title;

  /** The preamble. */
  private String preamble;

  /** Flag to specify if table of contents shall be shown. */
  private boolean showTOC = true;

  /** List of sections contained in the report. */
  private List<ResultReportSection> sections =
      new ArrayList<>();

  /**
   * Instantiates a new result report.
   * 
   * @param reportTitle
   *          the report title
   * @param reportPreamble
   *          the report preamble
   */
  public ResultReport(String reportTitle, String reportPreamble) {
    title = reportTitle;
    preamble = reportPreamble;
  }

  public void addSection(ResultReportSection section) {
    sections.add(section);
  }

  public String getPreamble() {
    return preamble;
  }

  public void setPreamble(String preamble) {
    this.preamble = preamble;
  }

  public boolean isShowTOC() {
    return showTOC;
  }

  public void setShowTOC(boolean showTOC) {
    this.showTOC = showTOC;
  }

  public List<ResultReportSection> getSections() {
    return Collections.unmodifiableList(sections);
  }

  public void setSections(List<ResultReportSection> sections) {
    this.sections = sections;
  }

  public void setTitle(String newTitle) {
    this.title = newTitle;
  }

  public String getTitle() {
    return title;
  }

}
