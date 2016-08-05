/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.listener;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IExperimentExecutionListener;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IInfoMapProvider;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulationrun.SimulationRun;
import org.jamesii.core.simulationrun.stoppolicy.CompositeCompTaskStopPolicy;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.CSVWriter;

/**
 * Listener writing csv tables (protocols) of model & simulator settings of all
 * runs in an exploration experiment
 *
 * @author Arne Bittig
 */
public class ExperimentSummaryListener implements IExperimentExecutionListener {

  /** Name and path of file to write to */
  private String fileName;

  /**
   * Whether to add experiment's unique ID to filename (".csv" will be added
   * appropriately, too)
   */
  private final boolean addExpIdToFileName;

  /** file writer Object */
  private transient FileWriter fw = null;

  /** Names of the parameters (=headings of the respective columns) */
  private Collection<String> parNames;

  /** Name of the plugin types whose instances and parameters to record */
  private final Collection<String> procFacParams = new ArrayList<>();

  /** Total number of columns to write */
  private int colNum = -1; // used to test whether all vars have been

  // initialized
  /** First column after the observations and simID (i.e. start of exp pars) */
  private int colParStart;

  private int colStopPolStart;

  private Collection<String> modelInfos = Collections.emptyList();

  /** first column for each LastStateInfoObserver's information */
  private final Map<String, Integer> stateInfoObsFirstColumn =
      new LinkedHashMap<>();

  /**
   * Default constructor - writes to %expID%.csv in current directory
   */
  public ExperimentSummaryListener() {
    this("", true);
  }

  /**
   * Full constructor - allows specification of path and filename to write to,
   * and whether the expID should be added to the filename (right after the
   * name, before ".csv")
   *
   * @param filename
   *          Name of file to write report to
   * @param addExpIdToFileName
   *          Flag whether to add experiment ID to filename
   */
  public ExperimentSummaryListener(String filename,
      boolean addExpIdToFileName) {
    this.fileName = filename;
    this.addExpIdToFileName = addExpIdToFileName;
  }

  @Override
  public void experimentExecutionStarted(BaseExperiment experiment) {
    if (colNum <= 0) { // init needed (not the case if exp is a follow-up)
      final int fileNameExtSize = 4;
      if (fileName.lastIndexOf(".csv") == fileName.length() - fileNameExtSize) {
        fileName = fileName.substring(0, fileName.length() - fileNameExtSize);
      }
      if (addExpIdToFileName) {
        fileName = fileName + "-" + experiment.getUniqueIdentifier();
      }
      fileName += ".csv";
    }

    try {
      fw = new FileWriter(fileName, fw != null); // append if fw existed
      ApplicationLogger.log(Level.CONFIG,
          "Using " + fw + " for experiment summary.");
    } catch (IOException e) {
      ApplicationLogger.log(Level.WARNING,
          "Error opening/creating file " + fileName);
      ApplicationLogger.log(e);
    }
  }

  @Override
  public void experimentExecutionStopped(BaseExperiment experiment) {
    try {
      fw.close();
      ApplicationLogger.log(Level.CONFIG,
          "Closed experiment summary file writer " + fw);
    } catch (Exception e) {
      ApplicationLogger.log(Level.WARNING, "Error writing to file " + fileName);
      ApplicationLogger.log(e);
    }

  }

  @Override
  public void simulationInitialized(ITaskRunner taskRunner,
      ComputationTaskRuntimeInformation crti) { /* noop */
  }

  @Override
  public void simulationExecuted(ITaskRunner taskRunner,
      ComputationTaskRuntimeInformation crti, boolean jobDone) {
    if (crti.getRunInformation().failed()) {
      return; // failed computation task
    }
    if (colNum <= 0) {
      initColumnHeadlinesAndVariables(crti);
    }

    String[] line = new String[colNum];
    recordColumnsForObserverInfo(crti, line);
    recordColumnsForStopPolicy(crti, line);

    line[colParStart - 1] = crti.getComputationTaskID().toString();

    Map<String, ?> parMap =
        crti.getComputationTaskConfiguration().getParameters();
    int i = colParStart;
    for (String parName : parNames) {
      line[i++] = toString(parMap.get(parName));
    }

    ParameterBlock procFacParamBlock =
        crti.getSimulationRunConfiguration().getExecParams().getSubBlocks()
            .get("org.jamesii.core.processor.plugintype.ProcessorFactory");
    if (procFacParamBlock != null) {
      Map<String, ParameterBlock> pfps = procFacParamBlock.getSubBlocks();

      for (String pfp : procFacParams) {
        line[i++] = toString(pfps.get(pfp));
      }
    }

    if (!modelInfos.isEmpty()) {
      int idxm = colNum - modelInfos.size();
      Map<String, ?> infoMap =
          ((IInfoMapProvider<?>) crti.getComputationTask().getModel())
              .getInfoMap();
      for (String mi : modelInfos) {
        line[idxm++] = toString(infoMap.get(mi));
      }
    }
    csvWriteLn(line);
  }

  /**
   * @param crti
   */
  private void initColumnHeadlinesAndVariables(
      ComputationTaskRuntimeInformation crti) {
    parNames = new ArrayList<>(
        crti.getComputationTaskConfiguration().getParameters().keySet());

    List<String> headings = new ArrayList<>();
    initColumnsForObserverInfo(crti, headings);
    initColumnsForStopPolicy(crti, headings);

    headings.add("simID");
    for (String pn : parNames) {
      headings.add(toString(pn));
    }

    ParameterBlock procFacParamBlock =
        crti.getSimulationRunConfiguration().getExecParams().getSubBlocks()
            .get("org.jamesii.core.processor.plugintype.ProcessorFactory");
    if (procFacParamBlock != null) {
      Map<String, ParameterBlock> pfps = procFacParamBlock.getSubBlocks();
      procFacParams.addAll(pfps.keySet());
      for (String pfp : procFacParams) {
        headings.add(toString(pfp));
      }
    }

    IModel model = crti.getComputationTask().getModel();
    if (model instanceof IInfoMapProvider) {
      modelInfos = (((IInfoMapProvider<?>) model).getInfoIDs());
      headings.addAll(modelInfos);
    }

    colNum = headings.size();
    colParStart =
        colNum - parNames.size() - procFacParams.size() - modelInfos.size();
    csvWriteLn(headings.toArray(new String[headings.size()]));
  }

  private void initColumnsForObserverInfo(
      ComputationTaskRuntimeInformation crti, List<String> headings) {
    for (IObserver<?> obs : crti.getComputationTaskObservers()) {
      if (obs instanceof IInfoMapProvider) {
        IInfoMapProvider<?> obsLSI = (IInfoMapProvider<?>) obs;
        Collection<String> info = obsLSI.getInfoIDs();
        stateInfoObsFirstColumn.put(obsLSI.getClass().getName(),
            headings.size());
        for (String h : info) {
          headings.add(toString(h));
        }
      }
    }
  }

  private void initColumnsForStopPolicy(ComputationTaskRuntimeInformation crti,
      List<String> headings) {
    colStopPolStart = headings.size();
    if (crti.getComputationTask() instanceof SimulationRun) {
      Map<String, ?> stopPolicyInfo = getStopPolicyInfo(
          ((SimulationRun) crti.getComputationTask()).getStopPolicy());
      for (String h : stopPolicyInfo.keySet()) {
        headings.add(h);
      }
    }
  }

  private void recordColumnsForObserverInfo(
      ComputationTaskRuntimeInformation crti, String[] line) {
    for (IObserver<?> obs : crti.getComputationTaskObservers()) {
      if (obs instanceof IInfoMapProvider) {
        IInfoMapProvider<?> obsLSI = (IInfoMapProvider<?>) obs;
        Collection<?> infoVals = obsLSI.getInfoMap().values();
        int iCol = stateInfoObsFirstColumn.get(obsLSI.getClass().getName());
        for (Object info : infoVals) {
          line[iCol++] = toString(info);
        }
      }
    }
  }

  private void recordColumnsForStopPolicy(
      ComputationTaskRuntimeInformation crti, String[] line) {
    if (crti.getComputationTask() instanceof SimulationRun) {
      Map<String, ?> stopPolicyInfo = getStopPolicyInfo(
          ((SimulationRun) crti.getComputationTask()).getStopPolicy());
      int iCol = colStopPolStart;
      for (Object info : stopPolicyInfo.values()) {
        line[iCol++] = toString(info);
      }
    }
  }

  /**
   * @param stopPol
   * @return
   */
  private static Map<String, ?> getStopPolicyInfo(
      IComputationTaskStopPolicy<?> stopPol) {
    Map<String, Object> rv = new LinkedHashMap<>();
    rv.put("stop criterion", stopPol.toString());
    IComputationTaskStopPolicy<?> relevantStopPol = stopPol;
    if (stopPol instanceof CompositeCompTaskStopPolicy) {
      relevantStopPol = ((CompositeCompTaskStopPolicy<?>) stopPol)
          .getLastRelevantStopPolicy();
      rv.put("stop-relevant sub-criterion", relevantStopPol.toString());
    }
    // if (relevantStopPol instanceof SteadyStateStopPolicy) {
    // SteadyStateStopPolicy ssssp = (SteadyStateStopPolicy) relevantStopPol;
    // SteadyStateEstimatorState sseState =
    // ssssp.getSteadyStateEstimatorState();
    // if (sseState instanceof IInfoMapProvider<?>) {
    // rv.putAll(((IInfoMapProvider<?>) sseState).getInfoMap());
    // } else {
    // rv.put("steady state detector result", sseState.getCurrentResult());
    // rv.put("steady state detector position", sseState.getCurrentPosition());
    // }
    // }
    return rv;

  }

  private static final char CSV_DELIM = ',';

  /**
   * Write string array as line into csv file (init filewriter if necessary;
   * must be closed elsewhere)
   *
   * @param line
   *          Line to write
   */
  private void csvWriteLn(String[] line) {
    try {
      fw.append(CSVWriter.toCSV(line, CSV_DELIM));
      fw.flush();
    } catch (IOException e) {
      ApplicationLogger.log(e);
    }
  }

  /**
   * Convert Object to String, replacing all occurrences of the designated csv
   * delimiter; return empty String for null argument
   *
   * @param o
   * @return
   */
  private String toString(Object o) {
    String s = toStringDirect(o);
    if (s.indexOf(CSV_DELIM) >= 0
        && (s.charAt(0) != '"' || s.charAt(s.length() - 1) != '"')) {
      return '"' + s + '"';
    } else {
      return s;
    }
  }

  private String toStringDirect(Object o) {
    if (o == null) {
      return "";
    }
    if (o instanceof ParameterBlock) {
      return parameterBlockToString((ParameterBlock) o);
    }
    if (o instanceof Object[]) {
      return Arrays.deepToString((Object[]) o);
      // .replace(csvDelim,delimReplacement);
    }
    if (o instanceof double[]) { // special treatment for vector content
      return Arrays.toString((double[]) o);
      // .replace(csvDelim,delimReplacement);
    }
    return o.toString();
    // .replace(csvDelim, delimReplacement);

  }

  /**
   * @param o
   * @return
   */
  private String parameterBlockToString(ParameterBlock pb) {
    StringBuilder pbs = new StringBuilder();
    if (pb.getValue() != null) {
      pbs.append(toString(pb.getValue()));
    }
    Map<String, ParameterBlock> pbsubs = pb.getSubBlocks();
    if (pbsubs != null && !pbsubs.isEmpty()) {
      if (pb.getValue() != null) {
        pbs.append('-');
      }
      pbs.append('{');
      for (Entry<String, ParameterBlock> sbe : pbsubs.entrySet()) {
        pbs.append(sbe.getKey());
        pbs.append(':');
        pbs.append(toString(sbe.getValue()));
        pbs.append(',');
      }
      pbs.setCharAt(pbs.length() - 1, '}');
    }
    return pbs.toString();
  }
}
