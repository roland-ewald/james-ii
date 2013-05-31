/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.launch;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.cmdparameters.Parameters;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.model.IModel;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.simulationrun.SimulationRun;
import org.jamesii.core.util.StopWatch;

/**
 * A benchmark launcher executes a model several times with varying parameters.
 * The execution time is gathered (setup time, run time) and written into an out
 * stream (e.g. a file)
 * 
 * @author Jan Himmelspach
 */
public abstract class BenchmarkLauncher {

  /**
   * The Class RunResult.
   * 
   * @author Jan Himmelspach
   */
  public class RunResult {

    /** The model creation. */
    private double modelCreation = -1;

    /** The simulation creation. */
    private double simulationCreation = -1;

    /** The simulation run. */
    private double simulationRun = -1;
  }

  /** The delim string. */
  private String delimString = " ";

  /** The out. */
  private PrintStream out = System.out;

  /** The print mean. */
  private boolean printMean = true;

  /** Create the global registry. */
  private Registry registry = new Registry();

  /** The results. */
  private List<List<RunResult>> results;

  /**
   * Creates the model.
   * 
   * @return the i model
   */
  public abstract IModel createModel();

  /**
   * Do nothing, can be overridden in descendant classes.
   * 
   * @param runResults
   *          the run results
   */
  protected void experimentDone(List<RunResult> runResults) {
  }

  /**
   * Gets the all results as string.
   * 
   * @return the all results as string
   */
  protected String getAllResultsAsString() {
    StringBuffer result = new StringBuffer();

    for (List<RunResult> ar : results) {
      result.append(getResultsAsString(ar) + "\n");
    }

    return result.toString();
  }

  /**
   * Gets the results.
   * 
   * @return the results
   */
  protected List<List<RunResult>> getResults() {
    return results;
  }

  /**
   * Gets the results as string.
   * 
   * @param ar
   *          the ar
   * 
   * @return the results as string
   */
  protected String getResultsAsString(List<RunResult> ar) {
    StringBuilder result = new StringBuilder();

    double mc = 0;
    double sc = 0;
    double sr = 0;
    int div = ar.size();

    for (RunResult r : ar) {
      mc += r.modelCreation;
      sc += r.simulationCreation;
      sr += r.simulationRun;
      result.append(r.modelCreation);
      result.append(getDelimString());
      result.append(r.simulationCreation);
      result.append(getDelimString());
      result.append(r.simulationRun);
      result.append(getDelimString());
      result.append(";");
    }
    mc /= div;
    sc /= div;
    sr /= div;
    if (printMean) {
      result.append(mc);
      result.append(getDelimString());
      result.append(sc);
      result.append(getDelimString());
      result.append(sr);
      result.append(";");
    }
    return result.toString();
  }

  /**
   * More experiments to do.
   * 
   * @return true, if successful
   */
  public abstract boolean moreExperimentsToDo();

  /**
   * Println.
   * 
   * @param s
   *          the s
   */
  public void println(String s) {
    out.println(s);
    SimSystem.report(Level.INFO, s);
  }

  /**
   * Run.
   * 
   * @param parameters
   *          the parameters
   * 
   * @return the run result
   */
  public RunResult run(Parameters parameters) {
    RunResult result = new RunResult();
    // execute the garbage collector
    System.gc();
    try {
      println("Memory (): " + Runtime.getRuntime().totalMemory() + " : "
          + Runtime.getRuntime().freeMemory());
      StopWatch sw = new StopWatch();
      sw.start();
      IModel model = createModel();
      sw.stop();
      result.modelCreation = sw.elapsedSeconds();
      println("Seconds needed for creating the model: " + sw.elapsedSeconds());
      println("Memory (model - [total:free]): "
          + Runtime.getRuntime().totalMemory() + " : "
          + Runtime.getRuntime().freeMemory());

      println("Simulation started at "
          + new SimpleDateFormat().format(new Date()));
      ISimulationRun simulation = null;
      sw.reset();
      sw.start();
      if (parameters.useMasterServer()) {
        // TODO master server deactivated, January, 2009 - model should be
        // created on the exec. host, not here!
        // System.out.println("The simulation will be executed on the server: "
        // + parameters.getMasterServerName());
        // simulation = parameters.getMasterServer().createSimulation(
        // model,
        // new SimulationConfiguration(1, null, null, parameters
        // .getParameterBlock())).getSimulation();

      } else { // if we don't use a server we create a simulation on our own
        TaskConfiguration config =
            new TaskConfiguration(1, null, null, parameters.getParameterBlock());
        SimulationRunConfiguration srConfig =
            (SimulationRunConfiguration) config
                .newComputationTaskConfiguration(new ComputationTaskIDObject());
        simulation = new SimulationRun("SimRun", model, srConfig, null);
      }
      if (simulation == null) {
        SimSystem.report(Level.SEVERE, "Simulation creation failed!!!");
      }
      sw.stop();
      result.simulationCreation = sw.elapsedSeconds();
      println("Seconds needed for creating the simulation: "
          + sw.elapsedSeconds());
      println("Memory (model + simulation - [total:free]): "
          + Runtime.getRuntime().totalMemory() + " : "
          + Runtime.getRuntime().freeMemory());
      sw.reset();
      sw.start();
      if (parameters.useMasterServer()) {
        int tries = 0;
        boolean retry = true;
        while (retry) {
          retry = false;
          try {
            parameters.getMasterServer().execute(
                simulation.getUniqueIdentifier(), null);
          } catch (Exception e) {
            SimSystem.report(Level.SEVERE,
                "Error occured while trying to launch the model on the server");
            tries++;
            if (tries == 3) {
              SimSystem.report(Level.SEVERE,
                  " - stop retrying ... something seems to be wrong with the server "
                      + parameters.getMasterServerName());
              throw e; // rethrow exception
            }

            retry = true;
            SimSystem.report(Level.INFO,
                " - now retrying ... waiting some seconds ...");
            // parameters.findServer(name); refind the server????
            Thread.sleep(2000); // wait two seconds
            SimSystem.report(Level.INFO,
                "   resetting stopwatch ... restarting simulation ...");
            sw.reset();
            sw.start();
          }
        }
      } else { // if we don't use a server we create a simlation on our own
        simulation.start();
      }
      sw.stop();
      result.simulationRun = sw.elapsedSeconds();
      println("Seconds needed for running the simulation: "
          + sw.elapsedSeconds());
      // simulation.stop();
    } catch (Exception e) {
      SimSystem.report(e);
      throw new RuntimeException(e.getMessage(), e);
    }
    return result;
  }

  /**
   * Run benchmark.
   * 
   * @param repeatEachExperiment
   *          the repeat each experiment
   * @param filename
   *          the filename
   */
  public void runBenchmark(int repeatEachExperiment, String filename) {

    try {
      FileOutputStream fsgen = new FileOutputStream(filename + "log", true);
      out = new PrintStream(fsgen);

      // print information about the system
      println(SimSystem.getVMInfo());

      StopWatch sw = new StopWatch();

      results = new ArrayList<>();

      int counter = 0;

      sw.start();

      while (moreExperimentsToDo()) {

        println("@@@@@@@@@@@@@@@@ experiment (#" + ++counter
            + ") starting @@@@@@@@@@@@@@@@");

        List<RunResult> currentResults = new ArrayList<>();
        results.add(currentResults);
        Parameters parameters = setupExperiment();
        for (int i = 0; i < repeatEachExperiment; i++) {
          println("---------------- sim run (#" + counter + "." + (i + 1)
              + ") starting ----------------");
          currentResults.add(run(parameters));
        }

        experimentDone(currentResults);

        if (filename != null) {
          try {
            FileOutputStream fs = new FileOutputStream(filename, true);
            try (PrintStream p = new PrintStream(fs)) {
              p.println(getResultsAsString(currentResults));
            }
          } catch (Exception e) {
            SimSystem.report(e);
          }

        }

      }

      sw.stop();
      println("Seconds needed for all experiments " + sw.elapsedSeconds());

    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  /**
   * Setup experiment.
   * 
   * @return the parameters
   */
  public abstract Parameters setupExperiment();

  /**
   * @return the delimString
   */
  protected String getDelimString() {
    return delimString;
  }

  /**
   * @param delimString
   *          the delimString to set
   */
  protected void setDelimString(String delimString) {
    this.delimString = delimString;
  }

}
