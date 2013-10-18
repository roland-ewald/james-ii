package org.jamesii.resultreport.util.experiments;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Files;
import org.jamesii.resultreport.ResultReport;
import org.jamesii.resultreport.ResultReportGenerator;
import org.jamesii.resultreport.ResultReportSection;
import org.jamesii.resultreport.renderer.plugintype.IResultReportRenderer;
import org.jamesii.resultreport.renderer.rtex.RTexResultReportRenderer;

/**
 * Helps to generate reports based on executions of
 * {@link org.jamesii.core.experiments.BaseExperiment}.
 * 
 * @author Roland Ewald
 * 
 */
public class ExperimentReporter {

  /** The default target directory. */
  private static final String DEFAULT_TARGET_DIR = "report";

  /**
   * The default template directory (empty string means it is not set, the
   * default template directory is used instead).
   */
  private static final String DEFAULT_TEMPLATE_DIR = "";

  /** The report name. */
  private final String reportName;

  /** The report description. */
  private final String reportDescription;

  /** The directory where the report template can be found in. */
  private final String templateDirectory;

  /** The target directory. */
  private final File targetDirectory;

  /** The experiment report elements to be processed. */
  private List<IExperimentReportElement> reportElements =
      new ArrayList<>();

  /**
   * The map to resolve short names for long names. The short name map maps a
   * fully-qualified class name of a plug-in factory to a short name. For
   * example the canonical class name of
   * {@link org.jamesii.core.math.random.generators.lcg.LCGGeneratorFactory} => 'LCG'.
   */
  private final Map<String, String> shortNameMap =
      new HashMap<>();

  /**
   * Instantiates a new experiment reporter.
   * 
   * @param theName
   *          the name
   * @param theDescription
   *          the description
   */
  public ExperimentReporter(String theName, String theDescription) {
    this(theName, theDescription, DEFAULT_TARGET_DIR);
  }

  /**
   * Instantiates a new experiment reporter.
   * 
   * @param theName
   *          the name
   * @param theDescription
   *          the description
   * @param theTargetDirectory
   *          the target directory
   */
  public ExperimentReporter(String theName, String theDescription,
      String theTargetDirectory) {
    this(theName, theDescription, theTargetDirectory, DEFAULT_TEMPLATE_DIR);
  }

  /**
   * Instantiates a new experiment reporter.
   * 
   * @param theName
   *          the name
   * @param theDescription
   *          the description
   * @param theTargetDirectory
   *          the target directory
   * @param theTemplateDirectory
   *          the template directory
   */
  public ExperimentReporter(String theName, String theDescription,
      String theTargetDirectory, String theTemplateDirectory) {
    reportName = theName;
    reportDescription = theDescription;
    targetDirectory = new File(theTargetDirectory);
    templateDirectory = theTemplateDirectory;
  }

  /**
   * Adds report elements to be generated.
   * 
   * @param elements
   *          the elements
   */
  public void addReportElement(IExperimentReportElement... elements) {
    for (IExperimentReportElement element : elements) {
      element.setShortNameMap(shortNameMap);
      reportElements.add(element);
    }
  }

  /**
   * Generates the report.
   */
  public void generateReport() {
    ResultReport report = generateReportElements();
    IResultReportRenderer renderer = configureRenderer();
    try {
      if (Files.makeDirectory(targetDirectory)) {
        (new ResultReportGenerator()).generateReport(report, renderer,
            targetDirectory);
      }
    } catch (Throwable t) { // NOSONAR:{robustness_required}
      SimSystem.report(Level.WARNING, "Report generation failed. ", t);
    }
  }

  /**
   * Configures default renderer for experiment reports.
   * 
   * @return the result report renderer
   */
  protected IResultReportRenderer configureRenderer() {
    RTexResultReportRenderer renderer = new RTexResultReportRenderer();
    if (!templateDirectory.isEmpty()) {
      renderer.setTemplateDirectory(templateDirectory);
    }
    return renderer;
  }

  /**
   * Generates the report parts for all elements.
   * 
   * @return the complete result report
   */
  protected ResultReport generateReportElements() {
    ResultReport report = new ResultReport(reportName, reportDescription);
    for (IExperimentReportElement reportElement : reportElements) {
      reportElement.executeExperiment();
      // TODO: Implement graceful failure handling and dependencies
      // between report elements
      if (reportElement.isSuccessful()) {
        ResultReportSection perfByModelHeading =
            reportElement.createReportSection();
        report.addSection(perfByModelHeading);
      } else {
        SimSystem.report(Level.SEVERE,
            "Report element '" + reportElement.toString()
                + "' did not execute succesfully.");
      }
    }
    return report;
  }

  /**
   * Gets the short name map.
   * 
   * @return the short name map
   */
  public Map<String, String> getShortNameMap() {
    return shortNameMap;
  }
}
