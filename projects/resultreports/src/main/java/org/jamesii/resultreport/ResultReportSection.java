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

import org.jamesii.resultreport.dataview.ResultDataView;

/**
 * Represents a hierarchical element of a {@link ResultReport}. May have an
 * arbitrary number of {@link ResultDataView} and sub-elements (which are also
 * of this type).
 * 
 * @see ResultReport
 * @see ResultDataView
 * @see ResultReportGenerator
 * 
 * @author Roland Ewald
 * 
 */
public class ResultReportSection {

  /** The title of the section. */
  private final String title;

  /**
   * The description of the following data views.
   */
  private final String description;

  /** The result data views. */
  private final List<ResultDataView<?>> dataViews =
      new ArrayList<>();

  /** The sub sections. */
  private final List<ResultReportSection> subSections =
      new ArrayList<>();

  /**
   * Instantiates a new result report section.
   * 
   * @param myTitle
   *          the title
   * @param myDescription
   *          the description
   */
  public ResultReportSection(String myTitle, String myDescription) {
    title = myTitle;
    description = myDescription;
  }

  public void addSubSection(ResultReportSection section) {
    subSections.add(section);
  }

  public void addDataView(ResultDataView<?> dataView) {
    dataViews.add(dataView);
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public List<ResultDataView<?>> getDataViews() {
    return Collections.unmodifiableList(dataViews);
  }

  public List<ResultReportSection> getSubSections() {
    return Collections.unmodifiableList(subSections);
  }

}
