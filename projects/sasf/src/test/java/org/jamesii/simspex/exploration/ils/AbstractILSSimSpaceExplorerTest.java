/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.ils;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.StochasticChattyTestCase;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IExperimentExecutionListener;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Triple;
import org.jamesii.resultreport.ResultReport;
import org.jamesii.resultreport.ResultReportGenerator;
import org.jamesii.resultreport.ResultReportSection;
import org.jamesii.resultreport.dataview.LineChartDataView;
import org.jamesii.resultreport.dataview.ScatterPlotDataView;
import org.jamesii.resultreport.renderer.plugintype.ResultReportRenderer;
import org.jamesii.resultreport.renderer.rtex.RTexResultReportRenderer;
import org.jamesii.simspex.exploration.ils.algorithm.BasicILS;
import org.jamesii.simspex.exploration.ils.explorer.ILSSimSpaceExplorer;
import org.jamesii.simspex.exploration.ils.observation.ParamILSObserver;

/**
 * Super class for test cases involving the ILS strategies.
 * 
 * @author Robert Engelke
 * @author Roland Ewald
 * 
 */
public abstract class AbstractILSSimSpaceExplorerTest extends
    StochasticChattyTestCase {

  protected abstract String getReportName();

  protected abstract int getNumberOfTrainingInstances();

  protected abstract Pair<BaseExperiment, ILSSimSpaceExplorer> configureILSExperiment(
      ParamILSObserver observer);

  public void testILSExplorationExperiment() throws InterruptedException {

    final ParamILSObserver ilsObserver = createNewObserver();
    final Pair<BaseExperiment, ILSSimSpaceExplorer> setup =
        configureILSExperiment(ilsObserver);

    final Object execDoneLock = new Object();

    setup.getFirstValue().getExecutionController()
        .addExecutionListener(new IExperimentExecutionListener() {

          AtomicInteger counter = new AtomicInteger(0);

          AtomicBoolean expDone = new AtomicBoolean(false);

          @Override
          public void simulationInitialized(ITaskRunner taskRunner,
              ComputationTaskRuntimeInformation crti) {
            counter.incrementAndGet();
          }

          @Override
          public void simulationExecuted(ITaskRunner taskRunner,
              ComputationTaskRuntimeInformation crti, boolean jobDone) {
            if (counter.decrementAndGet() == 0 && expDone.get()) {
              notifyEnd();
            }
          }

          @Override
          public void experimentExecutionStopped(BaseExperiment experiment) {
            if (counter.get() == 0) {
              notifyEnd();
            }
          }

          private void notifyEnd() {
            synchronized (execDoneLock) {
              execDoneLock.notifyAll();
            }
          }

          @Override
          public void experimentExecutionStarted(BaseExperiment experiment) {
          }
        });

    synchronized (execDoneLock) {
      setup.getFirstValue().execute();
      execDoneLock.wait();
    }
    createReport(ilsObserver);
    checkBasicILSInvariant(setup.getSecondValue(), ilsObserver);
    checkInvariants(ilsObserver);
  }

  /**
   * Creates report on ParamILS results.
   * 
   * @param ilsObserver
   *          the observer
   */
  private void createReport(ParamILSObserver ilsObserver) {
    ResultReport paramILSReport =
        new ResultReport(getReportName(), "Performance results for ParamILS.");
    ResultReportSection testSection = new ResultReportSection("ParamILS", "");
    paramILSReport.addSection(testSection);

    reportLastMinimum(testSection, ilsObserver);
    reportMinDataPlot(testSection, ilsObserver);
    reportHillClimbingPerformancePlot(testSection, ilsObserver);
    reportMinThetas(testSection, ilsObserver);
    reportUpdatedMinimumThetas(testSection, ilsObserver);
    reportInterestingThetas(testSection, ilsObserver);
    reportThetas(testSection, ilsObserver);

    ResultReportRenderer tutorialRenderer = new RTexResultReportRenderer();
    ResultReportGenerator tutorialGenerator = new ResultReportGenerator();
    try {
      File rep = new File("./ParamILSResults/reports");
      rep.mkdirs();
      tutorialGenerator.generateReport(paramILSReport, tutorialRenderer, rep);
    } catch (IOException e) {
      SimSystem.report(Level.SEVERE, "Report generation failed.", e);
    }
  }

  private void reportLastMinimum(ResultReportSection section,
      ParamILSObserver ilsObserver) {
    if (ilsObserver.getMinList().size() == 0) {
      SimSystem.report(Level.WARNING,
          "Skipped report section on discovered minima; none recorded.");
      return;
    }
    String res = ilsObserver.getOverallBestConfiguration().getFirstValue();
    ResultReportSection minimum =
        new ResultReportSection("Last minimum discovered", res);
    section.addSubSection(minimum);
  }

  /**
   * Report interesting performance.
   * 
   * @param section
   *          the report section to be filled
   * @param ilsObserver
   *          the ils observer
   */
  private void reportHillClimbingPerformancePlot(ResultReportSection section,
      ParamILSObserver ilsObserver) {
    int interestingNumber = 0;
    for (int i = 0; i < ilsObserver.getInterestingPerformance().size(); i++) {
      interestingNumber +=
          ilsObserver.getInterestingPerformance().get(i).size();
    }
    Double[][] plottableInterestData = new Double[2][interestingNumber];
    int actual = 0;
    for (int i = 0; i < ilsObserver.getInterestingPerformance().size(); i++) {
      for (int j = 0; j < ilsObserver.getInterestingPerformance().get(i).size(); j++) {
        plottableInterestData[0][actual] = Double.valueOf(actual);
        plottableInterestData[1][actual] =
            ilsObserver.getInterestingPerformance().get(i).get(j).getB();
        actual++;
      }
    }
    section.addDataView(new LineChartDataView(plottableInterestData, "",
        "Interesting performance data", new String[] { "Calculation number",
            "Performance" }, new String[] { "performance of Hillclimbing" }));
  }

  /**
   * Report min data.
   * 
   * @param section
   *          the report section to be filled
   * @param ilsObserver
   *          the ils observer
   */
  private void reportMinDataPlot(ResultReportSection section,
      ParamILSObserver ilsObserver) {
    if (!ilsObserver.getMinList().isEmpty()) {
      Double[][] plottableMinData =
          new Double[2][ilsObserver.getMinList().size()];
      for (int i = 0; i < ilsObserver.getMinList().size(); i++) {
        plottableMinData[0][i] = Double.valueOf(i);
        plottableMinData[1][i] = ilsObserver.getMinList().get(i).getB();
      }
      section.addDataView(new ScatterPlotDataView(plottableMinData, "",
          "Minimum performance data", new String[] { "Calculated Minimum",
              "Performance" }));
    }
  }

  /**
   * Report called thetas.
   * 
   * @param section
   *          the section
   * @param ilsObserver
   *          the ils observer
   */
  private void reportThetas(ResultReportSection section,
      ParamILSObserver ilsObserver) {
    String thetaVal = "";
    for (Integer i : ilsObserver.getCalledThetas()) {
      thetaVal += i + " ";
    }

    ResultReportSection theta =
        new ResultReportSection("Called Thetas", thetaVal);
    section.addSubSection(theta);
  }

  /**
   * Report min thetas.
   * 
   * @param section
   *          the section
   * @param ilsObserver
   *          the ils observer
   */
  private void reportMinThetas(ResultReportSection section,
      ParamILSObserver ilsObserver) {
    if (ilsObserver.getMinList().size() == 0) {
      return;
    }
    String res = "";
    for (Triple<String, Double, Double> pbd : ilsObserver.getMinList()) {
      res +=
          "(" + ilsObserver.getTheta(pbd.getA()) + ", " + pbd.getB() + ") ; ";
    }
    ResultReportSection min = new ResultReportSection("Minimum Thetas", res);
    section.addSubSection(min);
  }

  /**
   * Report updated minimum thetas.
   * 
   * @param section
   *          the section
   * @param ilsObserver
   *          the ils observer
   */
  private void reportUpdatedMinimumThetas(ResultReportSection section,
      ParamILSObserver ilsObserver) {
    if (ilsObserver.getMinUpdates().isEmpty()) {
      return;
    }
    String up = "";
    for (Integer i : ilsObserver.getMinUpdates()) {
      up += i.toString() + " updated, ";
    }
    ResultReportSection upp =
        new ResultReportSection("Minimum Thetas updated", up);
    section.addSubSection(upp);
  }

  /**
   * Report hill climbing thetas.
   * 
   * @param section
   *          the section
   * @param ilsObserver
   *          the ils observer
   */
  private void reportInterestingThetas(ResultReportSection section,
      ParamILSObserver ilsObserver) {
    String res = "";
    for (List<Triple<String, Double, Double>> subList : ilsObserver
        .getInterestingPerformance()) {
      res += "next Climbing: ";
      for (Triple<String, Double, Double> pbd : subList) {
        res +=
            "(" + ilsObserver.getTheta(pbd.getA()) + ", " + pbd.getB() + ") ; ";
      }
    }
    ResultReportSection hill = new ResultReportSection("Hill Climber", res);
    section.addSubSection(hill);
  }

  /**
   * Check basic ils invariant.
   * 
   * @param exp
   *          the explorer
   * @param ilsObserver
   *          the ils observer
   */
  private void checkBasicILSInvariant(ILSSimSpaceExplorer exp,
      ParamILSObserver ilsObserver) {
    if (exp.getParamILS() instanceof BasicILS) {
      for (Entry<String, Pair<ParameterBlock, Integer>> theta : ilsObserver
          .getThetas().entrySet()) {
        assertTrue(exp.getNumberOfRuns(theta.getValue().getFirstValue()) <= getNumberOfTrainingInstances());
      }
    }
  }

  /**
   * Check invariants.
   * 
   * @param ilsObserver
   *          the ils observer
   */
  private void checkInvariants(ParamILSObserver ilsObserver) {
    checkInvariantMinimumProgress(ilsObserver);
    checkInvariantHillClimbingProgress(ilsObserver);
  }

  /**
   * Check invariant of the minimum progress. The cost of the next minimum
   * Parameter configuration must be less than the cost of the previous one:
   * c(min1) > c(min2) > ...
   * 
   * @param ilsObserver
   *          the ils observer
   */
  private void checkInvariantMinimumProgress(ParamILSObserver ilsObserver) {
    if (ilsObserver.getMinList().size() < 2) {
      return;
    }
    List<Triple<String, Double, Double>> minList = ilsObserver.getMinList();
    for (Triple<String, Double, Double> perf : minList.subList(1,
        minList.size())) {
      boolean assertion = perf.getB() <= perf.getC();
      if (!assertion) {
        assertion = checkForUpdate(perf.getA(), ilsObserver);
      }
      assertTrue(assertion);
    }
  }

  private boolean checkForUpdate(String paramBl, ParamILSObserver ilsObserver) {
    Integer theta = ilsObserver.getTheta(paramBl).getSecondValue();
    if (ilsObserver.getMinUpdates().contains(theta)) {
      ilsObserver.getMinUpdates().remove(theta);
      return true;
    }
    return false;
  }

  /**
   * Check invariant of the hill climbing progress. The cost of the next
   * reported hill climbing station for one process of hill climbing has to be
   * less than the cost of the one before: c(h1) > c(h2) > ...
   * 
   * @param ilsObserver
   *          the ils observer
   */
  private void checkInvariantHillClimbingProgress(ParamILSObserver ilsObserver) {
    for (List<Triple<String, Double, Double>> hillPerf : ilsObserver
        .getInterestingPerformance()) {
      for (Triple<String, Double, Double> perf : hillPerf) {
        assertTrue(perf.getB() <= perf.getC());
      }
    }
  }

  protected ParamILSObserver createNewObserver() {
    return new ParamILSObserver();
  }
}
