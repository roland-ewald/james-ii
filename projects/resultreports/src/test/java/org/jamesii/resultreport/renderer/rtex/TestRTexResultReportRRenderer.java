/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.renderer.rtex;


import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.resultreport.ResultReport;
import org.jamesii.resultreport.ResultReportGenerator;
import org.jamesii.resultreport.ResultReportSection;
import org.jamesii.resultreport.dataview.BoxPlotDataView;
import org.jamesii.resultreport.dataview.HistogramDataView;
import org.jamesii.resultreport.dataview.LineChartDataView;
import org.jamesii.resultreport.dataview.ScatterPlotDataView;
import org.jamesii.resultreport.dataview.StatisticalTestDataView;
import org.jamesii.resultreport.dataview.StatisticalTestDefinition;
import org.jamesii.resultreport.dataview.TableDataView;
import org.jamesii.resultreport.renderer.plugintype.ResultReportRenderer;
import org.jamesii.resultreport.renderer.rtex.RTexResultReportRenderer;

import junit.framework.TestCase;

/**
 * Tests for {@link RTexResultReportRenderer}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestRTexResultReportRRenderer extends TestCase {

  /** The target URI. */
  final URI target = new File("./").toURI();

  /** Some test data. */
  final Double[][] testData = generateTestData();

  /** The number of data points. */
  final int NUM_POINTS = 500;

  /** The renderer. */
  RTexResultReportRenderer renderer;

  /** The report. */
  ResultReport report;

  /** The report generator. */
  ResultReportGenerator generator;

  /**
   * Sets the test up.
   */
  @Override
  public void setUp() {
    renderer = new RTexResultReportRenderer();
    report = new ResultReport("Test Report", "This is a simple test report.");
    generator = new ResultReportGenerator();
  }

  /**
   * Tear down.
   */
  @Override
  public void tearDown() {
    // TODO: erase report file directory (only if flag says so)
  }

  /**
   * Generate random test data. First vector (result[0]) contains the time in
   * discrete steps (0, 1, ..., to @link
   * {@link TestRTexResultReportRRenderer#NUM_POINTS} ). The other vectors
   * (result[1])
   * 
   * @return the test data
   */
  private Double[][] generateTestData() {
    Double[][] result = new Double[4][500];
    UniformDistribution udist = new UniformDistribution(SimSystem
        .getRNGGenerator().getNextRNG(), 0, 10);
    for (int i = 0; i < result[0].length; i++) {
      result[0][i] = (double) i;
      for (int j = 1; j < result.length; j++) {
        result[j][i] = udist.getRandomNumber() * j + j * (double) (i * i);
      }
    }
    return result;
  }

  /**
   * Test init and finish of document.
   * 
   * @throws Exception
   *           the exception
   */
  public void testInitAndFinish() throws Exception {
    renderer.init(target, report);
    renderer.endDocument();
    checkReportFile();
  }

  /**
   * Test the code from the tutorial.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void testTutorialReport() throws IOException {
    ResultReport tutorialReport = new ResultReport("Test Report",
        "This is a simple test report.");
    ResultReportSection testSection = new ResultReportSection("Test Section",
        "This is a test section.");
    tutorialReport.addSection(testSection);

    testSection.addDataView(new ScatterPlotDataView(new Double[][] {
        testData[0], testData[3] }, "The scatter plot data view.",
        "This is a title for the scatter plot", new String[] { "Bogus X Label",
            "Bogus Y Label" }));

    testSection.addDataView(new BoxPlotDataView(testData,
        "The box plot data view.", "A box plot", new String[] {
            "The variables", "The quantity" }, new String[] { "Variable One",
            "Var. 2", "Var. III", "Var. Four" }));

    testSection.addDataView(new HistogramDataView(testData[1],
        "The histogram data view.", "The title", "The x-label", "The y-label"));

    testSection.addDataView(new LineChartDataView(testData,
        "The line chart data view.", "A line chart", new String[] { "The time",
            "The observed value" }, new String[] { "Variable One", "Var. 2",
            "Var. III" }));

    ResultReportRenderer tutorialRenderer = new RTexResultReportRenderer();
    ResultReportGenerator tutorialGenerator = new ResultReportGenerator();
    new File("./tutorial/").mkdir();
    tutorialGenerator.generateReport(tutorialReport, tutorialRenderer,
        new File("./tutorial/"));
  }

  /**
   * Test report generator.
   * 
   * @throws Exception
   *           the exception
   */
  public void testReportGenerator() throws Exception {

    ResultReportSection testChapter = new ResultReportSection("Test Chapter",
        "This is a test chapter.");
    ResultReportSection testSection = new ResultReportSection("Test Section",
        "This is a test section.");
    ResultReportSection testSubSection = new ResultReportSection(
        "Test Subsection", "This is a test subsection.");
    ResultReportSection testSubSubSection = new ResultReportSection(
        "Test Subsubsection", "This is a test subsubsection.");
    testChapter.addSubSection(testSection);
    testSection.addSubSection(testSubSection);
    testSubSection.addSubSection(testSubSubSection);

    ResultReportSection secondChapter = new ResultReportSection(
        "Second Test Chapter",
        "This is another test chapter (just to see if document structure is mapped correctly).");

    report.addSection(testChapter);
    report.addSection(secondChapter);

    generator.generateReport(report, renderer, new File(target));

    checkReportFile();
  }

  /**
   * Tests data views.
   * 
   * @throws Exception
   *           the exception
   */
  public void testDataViews() throws Exception {

    ResultReportSection testChapter = new ResultReportSection(
        "Dataview Test Chapter",
        "This is a chapter that contains examples for all supported data views.");

    addScatterPlot(testChapter);
    addBoxPlot(testChapter);
    addHistogram(testChapter);
    addLineChart(testChapter);
    addTable(testChapter);
    addStatisticalTests(testChapter);

    report.addSection(testChapter);
    generator.generateReport(report, renderer, new File(target));
    checkReportFile();
  }

  private void addScatterPlot(ResultReportSection testChapter) {
    testChapter.addDataView(new ScatterPlotDataView(new Double[][] {
        testData[0], testData[3] }, "The \\texttt{ScatterPlotDataView}.",
        "This is the title for the sample data", new String[] {
            "Bogus X Label", "Bogus Y Label" }));
  }

  private void addBoxPlot(ResultReportSection testChapter) {
    // Check whether wrong initialization is handled correctly
    boolean exceptionThrown = false;
    try {
      new BoxPlotDataView(testData, "The \\texttt{BoxPlotDataView}",
          "The title", new String[] { "The variables", "The quantity" },
          new String[] { "Var. 1", "Var. 2", "Var. 3" });
    } catch (Throwable t) {
      exceptionThrown = true;
    }
    assertTrue(exceptionThrown);
    testChapter.addDataView(new BoxPlotDataView(testData,
        "The \\texttt{BoxPlotDataView}", "A box plot", new String[] {
            "The variables", "The quantity" }, new String[] { "Variable One",
            "Var. 2", "Var. III", "Var. Four" }));
  }

  private void addHistogram(ResultReportSection testChapter) {
    testChapter.addDataView(new HistogramDataView(testData[1],
        "The \\texttt{HistogramDataView}", "The title", "The x-label",
        "The y-label"));
  }

  private void addLineChart(ResultReportSection testChapter) {
    testChapter.addDataView(new LineChartDataView(testData,
        "The \\texttt{LineChartDataView}", "A line chart", new String[] {
            "The time", "The observed value" }, new String[] { "Variable One",
            "Var. 2", "Var. III" }));
  }

  private void addTable(ResultReportSection testChapter) {
    String[] row1 = new String[] { "Heading 1", "Head 2", " Head. III",
        "Heading Four", "" };
    String[] row2 = new String[] { "A", "D", "E", "F", null };
    String[] row3 = new String[] { "G", "H", "I", "J", null };
    String[] row4 = null;
    testChapter.addDataView(new TableDataView(new String[][] { row1, row2,
        row3, row4 }, "The \\texttt{TableDataView}"));

  }

  private void addStatisticalTests(ResultReportSection testChapter) {
    Double[] bogusA = new Double[] { 1.1, 1.0, 1.0, 0.9, 1.0 };
    Double[] bogusB = new Double[] { 0.9, 1.0, 1.0, 1.1 };
    testChapter.addDataView(new StatisticalTestDataView(
        new Pair<>(bogusA, bogusB),
        "Non-statistical test of a statistical test :)", "bogusVarA",
        "bogusVarB", true, true, StatisticalTestDefinition.KOLMOGOROV_SMIRNOV));
  }

  /**
   * Checks report file.
   */
  private void checkReportFile() {
    assertTrue(new File("./Test Report/report.Rtex").exists());
    assertFalse(new File("./Test Report/report.Rtex").isDirectory());
  }

}
