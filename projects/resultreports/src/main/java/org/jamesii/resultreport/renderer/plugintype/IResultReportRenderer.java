/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.renderer.plugintype;


import java.io.IOException;
import java.net.URI;

import org.jamesii.resultreport.ResultReport;
import org.jamesii.resultreport.ResultReportSection;
import org.jamesii.resultreport.dataview.ResultDataView;

/**
 * Interface for components that render the report for a specific output format.
 * 
 * @author Roland Ewald
 * 
 */
public interface IResultReportRenderer {

  /**
   * Initializes the renderer to write to the given URI.
   * 
   * @param target
   *          the target URI
   * @param report
   *          the report
   */
  void init(URI target, ResultReport report);

  /**
   * Signals the start of a section. Can be called multiple times to signal
   * sub-sections.
   * 
   * @param section
   *          the section
   */
  void startSection(ResultReportSection section);

  /**
   * Display data contained in the view specification.
   * 
   * @param data
   *          the content
   */
  void displayData(ResultDataView<?> data);

  /**
   * Adds all data views of the given section.
   * 
   * @param section
   *          the section
   */
  void addDataViews(ResultReportSection section);

  /**
   * Signals the end of a section. Can be called multiple times to signal the
   * end of (sub-)sections.
   */
  void endSection();

  /**
   * Signals the end of the document.
   * 
   * @throws IOException
   *           in case document cannot be written
   */
  void endDocument() throws IOException;

}
