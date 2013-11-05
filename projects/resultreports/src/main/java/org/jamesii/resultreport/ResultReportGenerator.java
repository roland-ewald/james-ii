/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport;


import java.io.File;
import java.io.IOException;

import org.jamesii.resultreport.renderer.plugintype.IResultReportRenderer;

/**
 * Basic utility class to use for report generation.
 * 
 * @author Roland Ewald
 * 
 */
public class ResultReportGenerator {

  /**
   * The preorder flag. If true, dataviews of each section will be rendered
   * before its sub-sections are rendered. If flag is false, post-order is
   * used(i.e. dataviews are rendered after subsections).
   */
  private boolean preorder = true;

  /**
   * Generate report.
   * 
   * @param report
   *          the report to be written
   * @param renderer
   *          the renderer to be used
   * @param target
   *          the target file
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void generateReport(ResultReport report,
      IResultReportRenderer renderer, File target) throws IOException {

    renderer.init(target.toURI(), report);

    for (ResultReportSection section : report.getSections()) {
      addSection(renderer, section);
    }

    renderer.endDocument();
  }

  /**
   * Adds the section (and all of its sub-sections, recursively).
   * 
   * @param renderer
   *          the renderer
   * @param section
   *          the section
   */
  private void addSection(IResultReportRenderer renderer,
      ResultReportSection section) {
    renderer.startSection(section);
    if (preorder) {
      renderer.addDataViews(section);
    }
    for (ResultReportSection subSection : section.getSubSections()) {
      addSection(renderer, subSection);
    }
    if (!preorder) {
      renderer.addDataViews(section);
    }
    renderer.endSection();
  }

  /**
   * Checks if is preorder dataview display is activated.
   * 
   * @return true, if preorder dataview display is activated
   */
  public boolean isPreorder() {
    return preorder;
  }

  /**
   * Sets the preorder dataview flag.
   * 
   * @param preorder
   *          the new preorder setting
   */
  public void setPreorder(boolean preorder) {
    this.preorder = preorder;
  }

}
