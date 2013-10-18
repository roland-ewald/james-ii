/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.renderer.rtex;


import java.util.HashMap;
import java.util.Map;

import org.jamesii.resultreport.ResultReportSection;
import org.jamesii.resultreport.dataview.ResultDataView;

/**
 * An element of an RTex report.
 * 
 * @author Roland Ewald
 * 
 */
public class RTexReportElement {

  /** The type of the element. */
  private final RTexElementType type;

  /** The section. */
  private final ResultReportSection section;

  /** The data view. */
  private final ResultDataView<?> dataView;

  /** Flag to determine if this is a section. */
  private final boolean sectionElement;

  /** A map for auxiliary data. */
  private final Map<String, Object> valueMap = new HashMap<>();

  /** The ID, unique regarding all other elements of the report. */
  private final int id;

  /**
   * Instantiates a new RTex report element.
   * 
   * @param theID
   *          the unique ID
   * @param theType
   *          the the type
   * @param theSection
   *          the the section
   * @param theView
   *          the the view
   */
  public RTexReportElement(int theID, RTexElementType theType,
      ResultReportSection theSection, ResultDataView<?> theView) {
    id = theID;
    type = theType;
    section = theSection;
    dataView = theView;
    sectionElement = (section != null);
  }

  public RTexElementType getType() {
    return type;
  }

  public ResultReportSection getSection() {
    return section;
  }

  public ResultDataView<?> getDataView() {
    return dataView;
  }

  public Map<String, Object> getValueMap() {
    return valueMap;
  }

  public boolean isSectionElement() {
    return sectionElement;
  }

  public int getID() {
    return id;
  }

}
