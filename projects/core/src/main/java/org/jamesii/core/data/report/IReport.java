/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.report;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * The Interface IReport.
 * 
 * @author Jan Himmelspach
 */
public interface IReport {

  /**
   * The constant REPORT. To be used in parameter blocks to link the report to
   * be used.
   */
  String REPORT = "__REPORT__REFERENCE__james.core.data.report.IReport";

  /**
   * Adds the parameter block.
   * 
   * @param block
   *          the block
   */
  void addParameterBlock(ParameterBlock block);

  /**
   * Adds the object instance.
   * 
   * @param object
   *          the object
   */
  void addObjectInstance(Object object);

  /**
   * Adds the sub report.
   * 
   * @return the i report
   */
  IReport addSubReport();

  /**
   * Adds a text to the output.
   * 
   * @param text
   * 
   */
  void addText(String text);

  /**
   * Adds a text, object and parameterblock as sub report to the output and
   * return the subreport.
   * 
   * @param text
   * @param object
   * @param block
   * @return
   * 
   */
  IReport addEntry(String text, Object object, ParameterBlock block);

}
