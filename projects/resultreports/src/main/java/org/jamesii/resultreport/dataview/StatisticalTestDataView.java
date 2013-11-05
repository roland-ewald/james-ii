/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.resultreport.dataview;

import org.jamesii.core.util.misc.Pair;

/**
 * Views the results of a statistical test. An alternative constructor is
 * provided to handle cases where only one data set needs to be provided.
 * 
 * @author Roland Ewald
 */
public class StatisticalTestDataView extends
    ResultDataView<Pair<Double[], Double[]>> {

  /**
   * The flag to determine whether to show a statistical summary of each data
   * set.
   */
  private final boolean showStatisticalSummary;

  /** The flag to determine whether to show all data (e.g. in a table). */
  private final boolean showAllData;

  /** The name of the first data set (w.r.t. order in data tuple). */
  private final String firstSetName;

  /** The name of the second data set (w.r.t. order in data tuple). */
  private final String secondSetName;

  /** The test definition. */
  private final StatisticalTestDefinition test;

  /**
   * Instantiates a new statistical test data view.
   * 
   * @param dataSets
   *          the two data sets
   * @param theCaption
   *          the caption
   * @param firstDataSetName
   *          the name of the first data set
   * @param secondDataSetName
   *          the name of the second data set
   * @param showStatSummary
   *          the show statistical summary flag
   * @param showData
   *          the show data flag
   * @param testToBeUsed
   *          the test to be used
   */
  public StatisticalTestDataView(Pair<Double[], Double[]> dataSets,
      String theCaption, String firstDataSetName, String secondDataSetName,
      boolean showStatSummary, boolean showData,
      StatisticalTestDefinition testToBeUsed) {
    super(dataSets, theCaption);
    firstSetName = firstDataSetName;
    secondSetName = secondDataSetName;
    showStatisticalSummary = showStatSummary;
    showAllData = showData;
    test = testToBeUsed;
  }

  /**
   * Alternative constructor in case only one data set is required.
   * 
   * @param dataSet
   *          the data set
   * @param theCaption
   *          the caption
   * @param dataSetName
   *          the data set name
   * @param showStatSummary
   *          the show statistical summary flag
   * @param showData
   *          the show data flag
   * @param testToBeUsed
   *          the test to be used
   */
  public StatisticalTestDataView(Double[] dataSet, String theCaption,
      String dataSetName, boolean showStatSummary, boolean showData,
      StatisticalTestDefinition testToBeUsed) {
    this(new Pair<Double[], Double[]>(dataSet, null), theCaption, dataSetName,
        null, showStatSummary, showData, testToBeUsed);
  }

  public boolean isShowStatisticalSummary() {
    return showStatisticalSummary;
  }

  public boolean isShowAllData() {
    return showAllData;
  }

  public String getFirstSetName() {
    return firstSetName;
  }

  public String getSecondSetName() {
    return secondSetName;
  }

  public StatisticalTestDefinition getTest() {
    return test;
  }
}
