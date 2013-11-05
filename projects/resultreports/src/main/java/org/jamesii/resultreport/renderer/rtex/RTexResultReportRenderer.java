/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.renderer.rtex;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.CSVWriter;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.resultreport.ResultReport;
import org.jamesii.resultreport.ResultReportSection;
import org.jamesii.resultreport.dataview.BoxPlotDataView;
import org.jamesii.resultreport.dataview.HistogramDataView;
import org.jamesii.resultreport.dataview.IProvideVariableNames;
import org.jamesii.resultreport.dataview.LineChartDataView;
import org.jamesii.resultreport.dataview.ResultDataView;
import org.jamesii.resultreport.dataview.ResultPlot1DDataView;
import org.jamesii.resultreport.dataview.ResultPlot2DDataView;
import org.jamesii.resultreport.dataview.ScatterPlotDataView;
import org.jamesii.resultreport.dataview.StatisticalTestDataView;
import org.jamesii.resultreport.renderer.plugintype.ResultReportRenderer;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Renderer that creates an RTex report.
 * 
 * @author Roland Ewald
 * 
 */
public final class RTexResultReportRenderer extends ResultReportRenderer {

  /**
   * The supported nesting levels are: chapter, section, subsection, and
   * subsubsection.
   */
  private static final RTexElementType[] NESTING_LEVELS =
      new RTexElementType[] { RTexElementType.CHAPTER, RTexElementType.SECTION,
          RTexElementType.SUB_SECTION, RTexElementType.SUB_SUB_SECTION };

  /** The name of the directory that contains the raw data. */
  public static final String RAW_DATA_DIRECTORY_NAME = "raw";

  /**
   * The name of the R library that contains the helper functions for report
   * generation.
   */
  private String plottingLibFileName = "plotting.R";

  /** The name of the template file. */
  private String templateFileName = "basic_template.Rtfm";

  /** The name of the output file. */
  private String outputFileName = "report.Rtex";

  /** The template directory. */
  private String templateDirectory = "report_template";

  /** The target directory. */
  private File targetDir;

  /** The directory for raw data. */
  private File rawDataDir;

  /** The report. */
  private ResultReport report;

  /** The elements of the report. */
  private final List<RTexReportElement> elements = new ArrayList<>();

  /** The current nesting level. */
  private int currentNesting = 0;

  /** The counter to generate unique IDs throughout the report. */
  private int currentIDCount = 0;

  /**
   * Initializes the renderer.
   * 
   * @param target
   *          the target
   * @param theReport
   *          the report
   */
  @Override
  public void init(URI target, ResultReport theReport) {
    report = theReport;
    targetDir = initializeSubDirectory(target, report.getTitle());
    copyPlottingLibrary();
    elements.clear();
    currentNesting = 0;
    currentIDCount = 0;
  }

  /**
   * Start section.
   * 
   * @param section
   *          the section
   */
  @Override
  public void startSection(ResultReportSection section) {
    if (currentNesting < NESTING_LEVELS.length) {
      currentNesting++;
    }
    elements.add(new RTexReportElement(++currentIDCount,
        NESTING_LEVELS[currentNesting - 1], section, null));
  }

  /**
   * Display data.
   * 
   * @param data
   *          the data
   */
  @Override
  public void displayData(ResultDataView<?> data) {
    if (!RTexResultReportRendererFactory.getSupportedViews().contains(
        data.getClass())) {
      throw new IllegalArgumentException("Data view of type '"
          + data.getClass().getCanonicalName() + "' not supported.");
    }

    elements.add(new RTexReportElement(++currentIDCount,
        RTexElementType.DATA_VIEW, null, data));
  }

  /**
   * End section.
   */
  @Override
  public void endSection() {
    if (currentNesting > 0) {
      currentNesting--;
    }
  }

  /**
   * End document.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @Override
  public void endDocument() throws IOException {

    SimSystem.report(Level.INFO, "Processing data views, saving raw data.");
    processDataViews();

    SimSystem.report(Level.INFO, "Processing report template.");
    writeReport();
  }

  /**
   * Process all data views.
   */
  private void processDataViews() {
    for (RTexReportElement element : elements) {
      if (!element.isSectionElement()) {
        try {
          ResultDataView<?> dataView = element.getDataView();
          processDataView(element, dataView);
        } catch (IOException e) {
          SimSystem.report(Level.WARNING, "A dataview could not be processed",
              e);
        }
      }
    }
  }

  /**
   * Process a data view.
   * 
   * @param element
   *          the element
   * @param dataView
   *          the data view
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void processDataView(RTexReportElement element,
      ResultDataView<?> dataView) throws IOException {
    if (dataView instanceof ScatterPlotDataView) {
      processScatterPlot(element);
    } else if (dataView instanceof BoxPlotDataView
        || dataView instanceof LineChartDataView) {
      processMultivariate2DPlot(element);
    } else if (dataView instanceof HistogramDataView) {
      processHistogram(element);
    } else if (dataView instanceof StatisticalTestDataView) {
      processStatisticalTest(element);
    }
  }

  /**
   * Basic processing of 1D result plot.
   * 
   * @param element
   *          the element
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void processResultPlot1D(RTexReportElement element)
      throws IOException {
    CSVWriter.writeResult(
        ((ResultPlot1DDataView) element.getDataView()).getData(),
        rawDataDir.getAbsolutePath() + File.separatorChar + element.getID()
            + "-data.dat", '\n');
  }

  /**
   * Basic processing of 2D result plot.
   * 
   * @param element
   *          the element
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void processResultPlot2D(RTexReportElement element)
      throws IOException {
    CSVWriter.writeResultWithLongFirstRow(
        ((ResultPlot2DDataView) element.getDataView()).getData(),
        rawDataDir.getAbsolutePath() + File.separatorChar + element.getID()
            + "-data.dat");
  }

  /**
   * Process element that denotes a statistical test.
   * 
   * @param element
   *          the element
   */
  private void processStatisticalTest(RTexReportElement element)
      throws IOException {
    StatisticalTestDataView statDataView =
        (StatisticalTestDataView) element.getDataView();
    Pair<Double[], Double[]> dataSet = statDataView.getData();
    writeVector(element.getID(), dataSet.getFirstValue(),
        statDataView.getFirstSetName());
    writeVector(element.getID(), dataSet.getSecondValue(),
        statDataView.getSecondSetName());
  }

  /**
   * Writes vector to file (an (n x 1) - matrix, so that R will interpret it as
   * a single column).
   * 
   * @param id
   *          the id of the corresponding report element
   * @param dataArray
   *          the data
   * @param varName
   *          the variable name
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void writeVector(int id, Double[] data, String varName)
      throws IOException {
    Double[][] dataMatrix = new Double[data.length][1];
    for (int i = 0; i < data.length; i++) { // NOSONAR:{arraycopy_not_applicable_here}
      dataMatrix[i][0] = data[i];
    }
    CSVWriter.writeResult(dataMatrix, rawDataDir.getAbsolutePath()
        + File.separatorChar + id + "-data-" + varName + ".dat");
  }

  /**
   * Processes a scatter plot data view.
   * 
   * @param element
   *          the element
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void processScatterPlot(RTexReportElement element)
      throws IOException {
    processResultPlot2D(element);
  }

  /**
   * Processes a histogram data view.
   * 
   * @param element
   *          the element
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void processHistogram(RTexReportElement element) throws IOException {
    processResultPlot1D(element);
  }

  /**
   * Processes a box plot data view.
   * 
   * @param element
   *          the element
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void processMultivariate2DPlot(RTexReportElement element)
      throws IOException {
    processResultPlot2D(element);
    CSVWriter.writeResult(
        new String[][] { quoteVarNames(((IProvideVariableNames) element
            .getDataView()).getVariableNames()) }, rawDataDir.getAbsolutePath()
            + File.separatorChar + element.getID() + "-data-vars.dat");
  }

  /**
   * Put variable names into quotation marks.
   * 
   * @param variableNames
   *          the variable names
   * @return the array with quoted variable names
   */
  private String[] quoteVarNames(String[] variableNames) {
    String[] result = new String[variableNames.length];

    for (int i = 0; i < variableNames.length; i++) {
      result[i] = "\"" + variableNames[i] + "\"";
    }
    return result;
  }

  /**
   * Writes the report.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void writeReport() throws IOException {
    Configuration cfg = new Configuration();
    cfg.setClassForTemplateLoading(RTexResultReportRenderer.class, "/"
        + templateDirectory);
    cfg.setObjectWrapper(new DefaultObjectWrapper());

    Map<String, Object> dataMap = prepareDataMap();
    Template basicTemplate = cfg.getTemplate(templateFileName);

    try (Writer out =
        new FileWriter(targetDir.getAbsolutePath() + File.separatorChar
            + outputFileName)) {
      basicTemplate.process(dataMap, out);
    } catch (TemplateException ex) {
      SimSystem.report(Level.SEVERE, "Error processing template file.", ex);
    }
  }

  /**
   * Copy plotting library to report directory.
   */
  private void copyPlottingLibrary() {
    String sourcePath = '/' + templateDirectory + '/' + plottingLibFileName;
    String targetPath =
        targetDir.getAbsolutePath() + File.separatorChar + plottingLibFileName;

    try (BufferedInputStream source =
        new BufferedInputStream(getClass().getResourceAsStream(sourcePath))) {
      java.nio.file.Files.copy(source, Paths.get(targetPath),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      SimSystem.report(Level.WARNING, "Could not copy library file from '"
          + sourcePath + "' to '" + targetPath
          + "' (required to execute R code).", e);
    }
  }

  /**
   * Prepares data map to be handed over to the {@link Template}.
   * 
   * @return the data map
   */
  private Map<String, Object> prepareDataMap() {
    Map<String, Object> dataMap = new HashMap<>();
    dataMap.put("date",
        Strings.getCurrentTimeAndDate("dd. MM. yyyy (HH:mm:ss)"));
    dataMap.put("report", report);
    dataMap.put(
        "vm",
        SimSystem.getVMInfo().replace(',', '\n').replace(';', '\n')
            .replace('\\', '/'));
    dataMap.put("elements", elements);
    return dataMap;
  }

  /**
   * Initialize sub directory.
   * 
   * @param target
   *          the target directory
   * @param directoryName
   *          the directory name
   * 
   * @return the file
   */
  private File initializeSubDirectory(URI target, String directoryName) {
    File selectedFile = new File(target);

    File subDirectory =
        new File(selectedFile.getAbsolutePath() + File.separatorChar
            + directoryName);
    Files.makeDirectory(subDirectory);

    rawDataDir =
        new File(subDirectory.getAbsolutePath() + File.separatorChar
            + RAW_DATA_DIRECTORY_NAME);
    Files.makeDirectory(rawDataDir);

    return subDirectory;
  }

  /**
   * Gets the plotting lib file name.
   * 
   * @return the plotting lib file name
   */
  public String getPlottingLibFileName() {
    return plottingLibFileName;
  }

  /**
   * Sets the plotting lib file name.
   * 
   * @param plottingLibFileName
   *          the new plotting lib file name
   */
  public void setPlottingLibFileName(String plottingLibFileName) {
    this.plottingLibFileName = plottingLibFileName;
  }

  /**
   * Gets the template file name.
   * 
   * @return the template file name
   */
  public String getTemplateFileName() {
    return templateFileName;
  }

  /**
   * Sets the template file name.
   * 
   * @param templateFileName
   *          the new template file name
   */
  public void setTemplateFileName(String templateFileName) {
    this.templateFileName = templateFileName;
  }

  /**
   * Gets the output file name.
   * 
   * @return the output file name
   */
  public String getOutputFileName() {
    return outputFileName;
  }

  /**
   * Sets the output file name.
   * 
   * @param outputFileName
   *          the new output file name
   */
  public void setOutputFileName(String outputFileName) {
    this.outputFileName = outputFileName;
  }

  /**
   * Gets the template directory.
   * 
   * @return the template directory
   */
  public String getTemplateDirectory() {
    return templateDirectory;
  }

  /**
   * Sets the template directory.
   * 
   * @param templateDirectory
   *          the new template directory
   */
  public void setTemplateDirectory(String templateDirectory) {
    this.templateDirectory = templateDirectory;
  }

}
