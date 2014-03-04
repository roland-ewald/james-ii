/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.statistics.univariate.ArithmeticMean;
import org.jamesii.core.math.statistics.univariate.StandardDeviation;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.core.util.graph.trees.BasicTree;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.core.util.misc.Triple;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemScheme;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;
import org.jamesii.perfdb.recording.performance.totaltime.TotalRuntimePerfMeasurerFactory;
import org.jamesii.perfdb.recording.selectiontrees.SelectedFactoryNode;
import org.jamesii.perfdb.tools.AbstractPerformanceExtractor;
import org.jamesii.perfdb.tools.IProblemDefinitionDescriptor;
import org.jamesii.perfdb.tools.IRTCDescriptor;

/**
 * Extracts performance to files from the database. The results are stored in
 * two files of tab-separated data, one containing all data and one containing a
 * summary (with average performance and standard deviation), which can be
 * plotted more easily.
 * 
 * The performance extractor is extremely versatile. The description of
 * {@link IRuntimeConfiguration} and {@link IProblemDefinition} entities are
 * handled by implementations of {@link IRTCDescriptor} and
 * {@link IProblemDefinitionDescriptor}, respectively, which have to be provided
 * by the user.
 * 
 * @see IRTCDescriptor
 * @see IProblemDefinitionDescriptor
 * 
 * @author Roland Ewald
 */
public class FilePerformanceExtractor extends AbstractPerformanceExtractor {

  /**
   * Compares performance types by name.
   */
  private static final class ComparatorByName implements
      Comparator<IPerformanceType>, Serializable {
    private static final long serialVersionUID = -2534466330481238325L;

    @Override
    public int compare(IPerformanceType o1, IPerformanceType o2) {
      if (o1.getName() == null || o2.getName() == null) {
        return Double.compare(o1.getID(), o2.getID());
      }
      return o1.getName().compareTo(o2.getName());
    }
  }

  /** The delimiter for creating the output file. */
  private static final char DELIMITER = '\t';

  /** The output file name. */
  private String outputFile = "./perf_results.tab";

  /** The summary output file. */
  private String summaryOutputFile = "./perf_results_summary.tab";

  /** The performance measurer for summary. */
  private Class<? extends PerformanceMeasurerFactory> perfMeasurerForSummary =
      TotalRuntimePerfMeasurerFactory.class;

  /** The string containing the performance data summary. */
  private StringBuilder perfDataSummary;

  /** The abstract factory summary. */
  private Class<? extends AbstractFactory<? extends Factory<?>>> abstractFactorySummary =
      AbstractProcessorFactory.class;

  /** The concrete factory names for summary. */
  private List<String> concreteFactoryNamesForSummary = null;

  /** The types of algorithm performance. */
  private List<IPerformanceType> perfTypes;

  /** The descriptor for configurations. */
  private IRTCDescriptor rtcDesc;

  /** The descriptor for simulation problems. */
  private IProblemDefinitionDescriptor spDesc;

  /** The number of columns used to describe the problem. */
  private Integer problemCols = null;

  /** The number of columns used to describe the configuration. */
  private Integer configCols = null;

  /** The user specified factory names. */
  private List<String> userSpecifiedFactoryNames;

  /**
   * Instantiates a new performance extractor.
   * 
   * @param modelMatchURI
   *          the string to match the model URI
   * @param performanceDB
   *          the performance db
   */
  public FilePerformanceExtractor(String modelMatchURI,
      IPerformanceDatabase performanceDB) {
    super(modelMatchURI, performanceDB);
  }

  /**
   * Instantiates a new performance extractor.
   * 
   * @param modelLocation
   *          the model location
   * @param performanceDataBase
   *          the performance data base
   * @param fileID
   *          the file id, to distinguish between different extracted
   *          performances
   */
  public FilePerformanceExtractor(String modelLocation,
      IPerformanceDatabase performanceDataBase, String fileID) {
    this(modelLocation, performanceDataBase);
    outputFile = "./" + fileID + "_result.tab";
  }

  /**
   * Extracts the desired performance information.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void extract() throws IOException {
    init();
    StringBuilder perfData = new StringBuilder();
    perfDataSummary = new StringBuilder();
    appendDataPerProblemScheme(perfData);
    buildHeader(perfData);
    writeToFile(perfDataSummary, summaryOutputFile);
    writeToFile(perfData, outputFile);
  }

  /**
   * Appends performance data of models in list to given string builder.
   * 
   * @param perfData
   *          the string builder
   */
  private void appendDataPerProblemScheme(StringBuilder perfData) {
    for (IProblemScheme scheme : fetchMatchingSchemesFromDB()) {
      List<IProblemDefinition> problemDefinitions =
          getPerfDB().getAllProblemDefinitions(scheme);
      for (IProblemDefinition problemDefinition : problemDefinitions) {
        StringBuilder tabPart = retrievePerfData(problemDefinition);
        perfData.append(tabPart);
      }
    }
  }

  /**
   * Write to file.
   * 
   * @param stringBuilder
   *          the string builder
   * @param fileName
   *          the file name of the destination
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void writeToFile(StringBuilder stringBuilder, String fileName)
      throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
      writer.append(stringBuilder);
    }
  }

  /**
   * Initialises extraction.
   * 
   * @return writer for result file
   * @throws Exception
   *           the exception
   */
  @Override
  protected void init() {
    if (isVerbose()) {
      SimSystem.report(Level.INFO,
          "Storing results for simulation problem to file:" + outputFile);
    }
    problemCols = (spDesc == null ? null : spDesc.getHeader().length);
    configCols = (rtcDesc == null ? null : rtcDesc.getHeader().length);
    super.init();
    initPerformanceTypes();
  }

  /**
   * Initialises list of performance types (alphabetical order).
   * 
   * @throws Exception
   *           the exception
   */
  private void initPerformanceTypes() {
    perfTypes = getPerfDB().getAllPerformanceTypes();
    Collections.sort(perfTypes, new ComparatorByName());
  }

  /**
   * Retrieves performance data of a given simulation problem.
   * 
   * @param problem
   *          the simulation problem
   * @return the string builder
   */
  protected StringBuilder retrievePerfData(IProblemDefinition problem) {

    Map<String, IRuntimeConfiguration> configMap = new HashMap<>();
    for (IRuntimeConfiguration rtConfig : getPerfDB().getAllRuntimeConfigs(
        problem)) {
      configMap.put(rtConfig.getSelectionTree().toParamBlock().toString(),
          rtConfig);
    }
    List<String> configDescs = new ArrayList<>(configMap.keySet());
    Collections.sort(configDescs);

    return buildRowForSimProblem(problem, configMap, configDescs);
  }

  /**
   * Builds the header.
   * 
   * @param generalStringBuilder
   *          the general string builder
   */
  private void buildHeader(StringBuilder generalStringBuilder) {

    StringBuilder myStringBuilder = new StringBuilder();
    append(myStringBuilder, new Object[] { "Problem Scheme",
        "Simulation End Time", "Replications" });
    append(
        myStringBuilder,
        generateHeading("Problem", spDesc == null ? null : spDesc.getHeader(),
            problemCols));
    append(
        myStringBuilder,
        generateHeading("Algorithm",
            rtcDesc == null ? null : rtcDesc.getHeader(), configCols));
    appendPerformanceHeaders(myStringBuilder);
    myStringBuilder.append('\n');

    // Insert header at front
    generalStringBuilder.insert(0, myStringBuilder);
  }

  /**
   * Append performance headers.
   * 
   * @param stringBuilder
   *          the string builder
   */
  private void appendPerformanceHeaders(StringBuilder stringBuilder) {
    for (IPerformanceType perfType : perfTypes) {
      String perfName =
          perfType.getName() != null ? perfType.getName() : perfType
              .getPerformanceMeasurerFactory().getName();
      append(stringBuilder, new Object[] { perfName + "_avg",
          perfName + "_stddev", perfName + "_results" });
    }
  }

  /**
   * Generates heading.
   * 
   * @param heading
   *          the heading
   * @param headingElements
   *          additional elements to add to heading
   * @param columns
   *          the number of columns
   * 
   * @return the string[]
   */
  private String[] generateHeading(String heading, String[] headingElements,
      Integer columns) {
    List<String> headingStrings = new ArrayList<>();
    headingStrings.add(heading + getHeadingElement(headingElements, 0));
    if (columns != null) {
      for (int i = 0; i < columns - 1; i++) {
        headingStrings.add(heading + getHeadingElement(headingElements, i + 1));
      }
    }
    return headingStrings.toArray(new String[headingStrings.size()]);
  }

  /**
   * Gets a heading element.
   * 
   * @param headingElements
   *          the array of heading elements
   * @param i
   *          the index of the requested element
   * 
   * @return the heading element
   */
  private String getHeadingElement(String[] headingElements, int i) {
    return (headingElements == null || headingElements.length <= i) ? ""
        : " / " + headingElements[i];
  }

  /**
   * Builds rows in the table for a given simulation problem.
   * 
   * @param problem
   *          the simulation problem
   * @param configMap
   *          the map of configurations
   * @param configDescs
   *          the configuration descriptions
   * 
   * @return the string builder
   */
  private StringBuilder buildRowForSimProblem(IProblemDefinition problem,
      Map<String, IRuntimeConfiguration> configMap, List<String> configDescs) {

    Map<String, Double[]> configPerfDataForSummary = new HashMap<>();
    StringBuilder perfDataStrB = new StringBuilder();
    for (String configDesc : configDescs) {
      IRuntimeConfiguration rtConfig = configMap.get(configDesc);
      List<IApplication> apps =
          getPerfDB().getAllApplications(problem, rtConfig);
      SimSystem.report(Level.INFO, "Found " + apps.size() + " replications.");

      if (apps.size() < getMinReplications()) {
        continue;
      }

      append(perfDataStrB, new Object[] { extractModelURI(problem),
          SimulationProblemDefinition.getSimStopTime(problem), apps.size() });
      appendProblemDescription(perfDataStrB, problem);
      appendConfigDescription(perfDataStrB, configDesc, rtConfig);
      Map<Class<? extends PerformanceMeasurerFactory>, Double[]> perfData =
          appendPerformanceStats(perfDataStrB, apps);
      perfDataStrB.append('\n');

      if (perfData.containsKey(perfMeasurerForSummary)) {
        configPerfDataForSummary.put(configDesc,
            perfData.get(perfMeasurerForSummary));
      }
    }

    writeSummary(problem, configPerfDataForSummary, configMap);
    return perfDataStrB;
  }

  /**
   * Write summary.
   * 
   * @param problem
   *          the problem
   * @param configPerfDataForSummary
   *          the config perf data for summary
   * @param configMap
   *          the config map
   */
  protected void writeSummary(IProblemDefinition problem,
      Map<String, Double[]> configPerfDataForSummary,
      Map<String, IRuntimeConfiguration> configMap) {

    Map<String, List<Pair<String, Double[]>>> groupedPerfData =
        splitPerfData(configPerfDataForSummary, configMap);

    if (concreteFactoryNamesForSummary == null) {

      List<String> concreteFactoryNames;
      if (userSpecifiedFactoryNames == null) {
        concreteFactoryNames = new ArrayList<>(groupedPerfData.keySet());
      } else {
        concreteFactoryNames = new ArrayList<>(userSpecifiedFactoryNames);
      }
      if (concreteFactoryNames.size() == 0) {
        return;
      }
      Collections.sort(concreteFactoryNames);
      concreteFactoryNamesForSummary = concreteFactoryNames;
      buildHeaderForSummary();
    }

    Map<String, Pair<String, Double[]>> minPerfPerFactory = new HashMap<>();
    Map<String, Pair<String, Double[]>> maxPerfPerFactory = new HashMap<>();
    for (String concreteFactoryName : concreteFactoryNamesForSummary) {
      List<Pair<String, Double[]>> perfDataForFactory =
          groupedPerfData.get(concreteFactoryName);
      if (perfDataForFactory == null) {
        continue;
      }
      Double[] minPerf = new Double[] { Double.POSITIVE_INFINITY, 0. };
      String minConfigDesc = null;
      Double[] maxPerf = new Double[] { Double.NEGATIVE_INFINITY, 0. };
      String maxConfigDesc = null;
      for (Pair<String, Double[]> perfData : perfDataForFactory) {
        String configDesc = perfData.getFirstValue();
        Double[] stats = perfData.getSecondValue();
        if (stats[0] < minPerf[0]) {
          minPerf = stats;
          minConfigDesc = configDesc;
        }
        if (stats[0] > maxPerf[0]) {
          maxPerf = stats;
          maxConfigDesc = configDesc;
        }
      }
      minPerfPerFactory.put(concreteFactoryName, new Pair<>(minConfigDesc,
          minPerf));
      maxPerfPerFactory.put(concreteFactoryName, new Pair<>(maxConfigDesc,
          maxPerf));
    }

    append(perfDataSummary, new Object[] { extractModelURI(problem),
        SimulationProblemDefinition.getSimStopTime(problem) });
    appendProblemDescription(perfDataSummary, problem);
    writeResultSummary(minPerfPerFactory, maxPerfPerFactory, configMap);
  }

  /**
   * Write result summary.
   * 
   * @param minPerfPerFactory
   *          the min perf per factory
   * @param maxPerfPerFactory
   *          the max perf per factory
   * @param configMap
   *          the config map
   */
  void writeResultSummary(
      Map<String, Pair<String, Double[]>> minPerfPerFactory,
      Map<String, Pair<String, Double[]>> maxPerfPerFactory,
      Map<String, IRuntimeConfiguration> configMap) {

    double overallMaxPerf = Double.NEGATIVE_INFINITY;
    double overallMinPerf = Double.POSITIVE_INFINITY;

    for (String facName : concreteFactoryNamesForSummary) {

      Pair<String, Double[]> minPerformance = minPerfPerFactory.get(facName);
      if (minPerformance == null) {
        continue;
      }
      appendConfigDescription(perfDataSummary, minPerformance.getFirstValue(),
          configMap.get(minPerformance.getFirstValue()));
      append(perfDataSummary, minPerformance.getSecondValue());

      Pair<String, Double[]> maxPerformance = maxPerfPerFactory.get(facName);
      appendConfigDescription(perfDataSummary, maxPerformance.getFirstValue(),
          configMap.get(maxPerformance.getFirstValue()));
      append(perfDataSummary, maxPerformance.getSecondValue());

      append(perfDataSummary, new Double[] { maxPerformance.getSecondValue()[0]
          / minPerformance.getSecondValue()[0] });

      double minPerf = minPerformance.getSecondValue()[0];
      if (minPerf < overallMinPerf) {
        overallMinPerf = minPerf;
      }

      double maxPerf = maxPerformance.getSecondValue()[0];
      if (maxPerf > overallMaxPerf) {
        overallMaxPerf = maxPerf;
      }
    }

    append(perfDataSummary, new Double[] { overallMaxPerf / overallMinPerf });
    perfDataSummary.append('\n');
  }

  /**
   * Builds the header for summary.
   */
  private void buildHeaderForSummary() {
    append(perfDataSummary, new Object[] { "Model", "Simulation End Time" });
    append(
        perfDataSummary,
        generateHeading("Problem", spDesc == null ? null : spDesc.getHeader(),
            problemCols));
    for (String concreteFacName : concreteFactoryNamesForSummary) {
      for (String prefix : new String[] { "MinPerf ", "MaxPerf" }) {
        append(
            perfDataSummary,
            generateHeading(prefix + concreteFacName, rtcDesc == null ? null
                : rtcDesc.getHeader(), configCols));
        append(perfDataSummary, new Object[] { "Run Time (avg)",
            "Run Time (stddev.)" });
      }
      append(perfDataSummary, new Object[] { "Relative Improvement (max/min)" });
    }
    append(perfDataSummary,
        new Object[] { "Overall Relative Improvement (Max/Min)" });
    perfDataSummary.append('\n');
  }

  /**
   * Splits performance data according to.
   * 
   * @param configPerfDataForSummary
   *          the performance data per configuration
   * @param configMap
   *          the configuration map (description => configuration)
   * @return a map distinct sub-class of base-factory => (list of
   *         (configuration,performance) tuples)
   *         {@link FilePerformanceExtractor#abstractFactorySummary}. This
   *         means, e.g., to group all configurations by the same (topmost)
   *         processor factory.
   */
  Map<String, List<Pair<String, Double[]>>> splitPerfData(
      Map<String, Double[]> configPerfDataForSummary,
      Map<String, IRuntimeConfiguration> configMap) {
    Map<String, List<Pair<String, Double[]>>> splitPData = new HashMap<>();

    for (Entry<String, Double[]> configDesc : configPerfDataForSummary
        .entrySet()) {
      IRuntimeConfiguration rtConfig = configMap.get(configDesc.getKey());
      String topmostFacName = getTopmostSummaryFactoryName(rtConfig);
      if (!splitPData.containsKey(topmostFacName)) {
        splitPData.put(topmostFacName, new ArrayList<Pair<String, Double[]>>());
      }
      splitPData.get(topmostFacName).add(
          new Pair<>(configDesc.getKey(), configDesc.getValue()));
    }

    return splitPData;
  }

  /**
   * Gets the topmost factory name of a factory with a given abstract class.
   * 
   * @param rtConfig
   *          the rt config
   * @return the topmost summary factory name
   */
  String getTopmostSummaryFactoryName(IRuntimeConfiguration rtConfig) {
    List<SelectedFactoryNode> eligibleNodes = new ArrayList<>();
    Map<SelectedFactoryNode, SelectedFactoryNode> childParentMap =
        rtConfig.getSelectionTree().getChildToParentMap();
    for (SelectedFactoryNode selFacNode : childParentMap.keySet()) {
      if (selFacNode.hasSelectionInformation()
          && selFacNode.getAbstractFactoryClass()
              .equals(abstractFactorySummary)) {
        eligibleNodes.add(selFacNode);
      }
    }

    SelectedFactoryNode closestFacNode = null;
    int distanceToRoot = Integer.MAX_VALUE;
    for (SelectedFactoryNode selFacNode : eligibleNodes) {
      int thisNodesDistance =
          BasicTree.getVertexSequenceFromRoot(childParentMap, selFacNode)
              .size();
      if (distanceToRoot > thisNodesDistance) {
        closestFacNode = selFacNode;
        distanceToRoot = thisNodesDistance;
      }
    }

    return closestFacNode == null ? "" : Strings.dispClassName(closestFacNode
        .getFactoryClass());
  }

  /**
   * Append configuration description.
   * 
   * @param strB
   *          the string builder
   * @param configDesc
   *          the configuration description
   * @param rtConfig
   *          the rt config
   */
  private void appendConfigDescription(StringBuilder strB, String configDesc,
      IRuntimeConfiguration rtConfig) {
    if (rtcDesc == null) {
      append(strB, new String[] { configDesc });
    } else {
      append(strB, ensureLength(rtcDesc.describe(rtConfig), configCols));
    }
  }

  /**
   * Append problem description.
   * 
   * @param stringBuilder
   *          the string builder
   * @param problem
   *          the simulation problem
   */
  private void appendProblemDescription(StringBuilder stringBuilder,
      IProblemDefinition problem) {
    if (spDesc == null) {
      appendDefaultProblemDescription(stringBuilder, problem);
    } else {
      append(stringBuilder, ensureLength(spDesc.describe(problem), problemCols));
    }
  }

  /**
   * Ensures length of string array.
   * 
   * @param strArray
   *          the string array
   * @param desiredLength
   *          the string array length
   * 
   * @return the de/inflated array of strings
   */
  private Object[] ensureLength(String[] strArray, Integer desiredLength) {

    List<String> result = new ArrayList<>();

    if (strArray.length > desiredLength) {
      SimSystem.report(Level.WARNING,
          "The array '" + Strings.dispArray(strArray) + "' contains "
              + strArray.length + " elements, but only " + desiredLength
              + " elements are allowed (last elements will be merged).");
    }

    for (int i = 0; i < strArray.length; i++) {
      if (result.size() < desiredLength) {
        result.add(strArray[i]);
      } else {
        result.set(desiredLength - 1, result.get(desiredLength - 1) + " "
            + strArray[i]);
      }
    }

    for (int i = 0; i < desiredLength - result.size(); i++) {
      result.add("");
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Append default problem description.
   * 
   * @param stringBuilder
   *          the string builder
   * @param problem
   *          the problem
   */
  private void appendDefaultProblemDescription(StringBuilder stringBuilder,
      IProblemDefinition problem) {
    Map<String, Serializable> params = problem.getSchemeParameters();
    List<String> paramNames = new ArrayList<>(params.keySet());
    Collections.sort(paramNames);
    String paramsDesc = Strings.dispMap(params, paramNames, "=", ";", true);
    append(stringBuilder, new String[] { paramsDesc });
  }

  /**
   * Extracts the model URI from the problem (removes schema).
   * 
   * @param problem
   *          the simulation problem
   * 
   * @return the string representation of the model URI
   */
  private String extractModelURI(IProblemDefinition problem) {
    String modelURI = problem.getProblemScheme().getUri().toString();
    int lastSlash = modelURI.lastIndexOf('/');
    if (lastSlash < modelURI.length() - 1 && lastSlash > -1) {
      modelURI = modelURI.substring(lastSlash + 1);
    }
    return modelURI;
  }

  /**
   * Appends performance statistics to table row.
   * 
   * @param strBuilder
   *          the string builder to be used
   * @param applications
   *          the applications
   * @return the map< class<? extends performance measurer factory>, double[]>
   */
  private Map<Class<? extends PerformanceMeasurerFactory>, Double[]> appendPerformanceStats(
      StringBuilder strBuilder, List<IApplication> applications) {
    Map<IPerformanceType, Triple<Double, Double, List<Double>>> perfMap =
        calculatePerformanceStats(applications);
    Map<Class<? extends PerformanceMeasurerFactory>, Double[]> perfTypeResults =
        new HashMap<>();
    for (IPerformanceType perfType : perfTypes) {
      Triple<Double, Double, List<Double>> stats = perfMap.get(perfType);
      append(
          strBuilder,
          new Object[] {
              stats.getA(),
              stats.getB(),
              Strings.dispArray(stats.getC().toArray(
                  new Double[stats.getC().size()])) });
      perfTypeResults.put(perfType.getPerformanceMeasurerFactory(),
          new Double[] { stats.getA(), stats.getB() });
    }
    return perfTypeResults;
  }

  /**
   * Calculates performance statistics for all measurements.
   * 
   * @param apps
   *          the available applications
   * 
   * @return a map performance type => (mean, standard deviation, [list of raw
   *         data])
   */
  private Map<IPerformanceType, Triple<Double, Double, List<Double>>> calculatePerformanceStats(
      List<IApplication> apps) {
    Map<IPerformanceType, Triple<Double, Double, List<Double>>> perfMap =
        new HashMap<>();
    for (IPerformanceType perfType : perfTypes) {
      List<Double> perfs = getPerformances(apps, perfType);
      perfMap.put(perfType, new Triple<>(ArithmeticMean.arithmeticMean(perfs),
          StandardDeviation.standardDeviation(perfs), perfs));
    }
    return perfMap;
  }

  /**
   * Appends objects to string.
   * 
   * @param strB
   *          the str b
   * @param objects
   *          the objects
   */
  protected void append(StringBuilder strB, Object[] objects) {
    for (int i = 0; i < objects.length; i++) {
      strB.append(objects[i]);
      strB.append(DELIMITER);
    }
  }

  /**
   * Gets the descriptor for runtime configurations.
   * 
   * @return the rtc desc
   */
  public IRTCDescriptor getRtcDesc() {
    return rtcDesc;
  }

  /**
   * Sets the descriptor for runtime configurations.
   * 
   * @param rtcDesc
   *          the new rtc desc
   */
  public void setRtcDesc(IRTCDescriptor rtcDesc) {
    this.rtcDesc = rtcDesc;
  }

  /**
   * Gets the descriptor for simulation problems.
   * 
   * @return the description of the problem definition
   */
  public IProblemDefinitionDescriptor getSpDesc() {
    return spDesc;
  }

  /**
   * Sets the descriptor for simulation problems.
   * 
   * @param spDesc
   *          the new sp desc
   */
  public void setSpDesc(IProblemDefinitionDescriptor spDesc) {
    this.spDesc = spDesc;
  }

  /**
   * Gets the output file.
   * 
   * @return the output file
   */
  public String getOutputFile() {
    return outputFile;
  }

  /**
   * Sets the output file.
   * 
   * @param outputFile
   *          the new output file
   */
  public void setOutputFile(String outputFile) {
    this.outputFile = outputFile;
  }

  /**
   * Gets the user specified factory names.
   * 
   * @return the user specified factory names
   */
  public List<String> getUserSpecifiedFactoryNames() {
    return userSpecifiedFactoryNames;
  }

  /**
   * Sets the user specified factory names.
   * 
   * @param userSpecifiedFactoryNames
   *          the new user specified factory names
   */
  public void setUserSpecifiedFactoryNames(
      List<String> userSpecifiedFactoryNames) {
    this.userSpecifiedFactoryNames = userSpecifiedFactoryNames;
  }

}