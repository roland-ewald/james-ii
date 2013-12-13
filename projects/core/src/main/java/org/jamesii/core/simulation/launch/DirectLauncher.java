/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.launch;

import java.io.File;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.cmdparameters.InvalidParameterException;
import org.jamesii.core.cmdparameters.ParamHandler;
import org.jamesii.core.cmdparameters.Parameter;
import org.jamesii.core.cmdparameters.Parameters;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.Model;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.simulationrun.SimulationRun;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;

/**
 * The Launcher class can be used for an easy creation of a parameterized
 * simulation launcher. Instead of directly integrating a main method in the
 * model's main class this class is model independent. I.e. it provides a set of
 * std parameters which are valid for all simulations. This class may even be
 * subclassed for a further extension of the supported parameters. Besides
 * reducing the code in the model's main class file this approach is cleaner and
 * easier to maintain. However, if someone like to do so, he can completely
 * create his own "launcher" methods - at least for a pure one host simulation
 * that's pretty easy.
 * 
 * Usually one will more likely use the BaseExperiment for the definition of a
 * simulation. A launcher always can execute just a single simulation run, which
 * is nice for some testing purposes, but usually not for sophisticated
 * simulation experiments.
 * 
 * @author Jan Himmelspach
 */
public class DirectLauncher {

  /** The stop time of the sim run started by the launcher. */
  Double stopTime = Double.POSITIVE_INFINITY;

  /**
   * Get a simulation system header (to be printed to the command line).
   * 
   * @param model
   *          maybe null
   * 
   * @return string containing a printable header
   */
  public static String getSimulationFrameworkHeader(IModel model) {
    String result = "";
    result +=
        "\n------------------------------------------------------------------------------------\n";
    result +=
        "Simulation framework " + SimSystem.SIMSYSTEM + ", Version "
            + SimSystem.VERSION + " Project CoSA\n";
    result += "\n";
    if (model != null) {
      result += "Executing the model\n";
      result += "\n";
      result += "\t\t\"" + model.getName() + "\n";
      result += "\n";
    }

    result +=
        "------------------------------------------------------------------------------------\n";
    result += "\n";

    return result;
  }

  /**
   * This method can be used for an easy way to get unique filenames. This can
   * be very useful if you have to do many runs, and each run writes an output
   * file which you don#t want to be overwritten on the next run. Input is
   * complete filename (path + filename + extension the return will be path +
   * filename + [number] + extension
   * 
   * @param filename
   *          the filename
   * 
   * @return the unique filename
   */
  public static String getUniqueFilename(String filename) {
    File f = new File(filename);
    // divide the filename up (into name (and extension) and pathname
    String path = f.getParent();
    String file = f.getName();
    // System.out.println(f.getAbsolutePath());
    String ext;
    String num = "";
    // divide the file up (into name and extension)
    if (file.lastIndexOf('.') != -1) {
      ext = file.substring(file.lastIndexOf('.'), file.length());
      file = file.substring(0, file.lastIndexOf('.') - 1);
    } else {
      ext = "";
    }

    int fc = 0;
    boolean found = true;
    // as long as we find files with the given name
    while (found) {
      f = new File(path + file + num + ext);
      found = f.exists();
      if (found) { // still found -> try next name
        fc++;
        num = Integer.toString(fc);
      }
    }
    return path + file + num + ext;
  }

  /** The parameters (command line. */
  private final Parameters parameters = new Parameters();

  /** The simulation. */
  private IComputationTask computationTask;

  /**
   * Creates a new instance of Launcher.
   */
  public DirectLauncher() {
    super();
  }

  /**
   * Returns the valid parameter list as a string. A call to this method
   * automatically adds the results of the {@link #extArgsToString} method.
   * 
   * @return a string representation of the usable parameters.
   */
  public final String argsToString() {
    return parameters.getHelp() + extArgsToString();
  }

  /**
   * Creates an instance of a given model class. The usage of this method avoids
   * the usage of java.rmi. in user Launchers!
   * 
   * @param modelClass
   *          the model class
   * @param name
   *          the name
   * 
   * @return the model
   */
  public Model createModel(Class<? extends IModel> modelClass, String name) {
    Model m = null;
    try {
      m = (Model) modelClass.newInstance();
      m.setName(name);
    } catch (Exception e) {
      SimSystem.report(e);
    }
    return m;
  }

  /**
   * Sets the stop time.
   * 
   * @param d
   *          the new stop time
   */
  public void setStopTime(double d) {
    stopTime = d;
  }

  /**
   * Create a configuration based on the parameters.
   * 
   * @return
   */
  private IComputationTaskConfiguration createConfiguration() {
    TaskConfiguration config =
        new TaskConfiguration(1, null, null, parameters.getParameterBlock());
    ParameterBlock pb = new ParameterBlock();
    pb.addSubBlock(SimTimeStopFactory.SIMEND, stopTime);
    config
        .setComputationTaskStopFactory(new ParameterizedFactory<ComputationTaskStopPolicyFactory<?>>(
            new SimTimeStopFactory(), pb));
    return config
        .newComputationTaskConfiguration(new ComputationTaskIDObject());
  }

  /**
   * Creates a simulation for the given model, thereby using the passed
   * parameters that can, but need not, be equal to the parameters of this class
   * If a server has been specified the simulation is created on the server,
   * otherwise it'll be created without a server (and without the possibility to
   * run distributed). This method will NOT start the simulation!
   * 
   * @param model
   *          the model
   */
  public final void createSimulation(IModel model) {

    // // build a simple information string containing the user selected
    // // processor
    // // settings and the run mode
    // String s = "";

    // s = "a(n) " + parameters.getProc() + " ";
    //
    // s = s + "processor";
    //
    // s = s + " with the " + parameters.getEventqueue()
    // + " event queue implementation";

    // print out general information, i.e. model name and simulation system
    // version

    if (!parameters.isSilent()) {
      System.out.println(getSimulationFrameworkHeader(model));
    }

    // if (SimSystem.consoleOut) {
    // System.out.println("(Using " + s + ")");
    // System.out.println("Simulating model from "
    // + parameters.getParameterBlock().getSubBlockValue(
    // Parameters.START_TIME) + " to " + parameters.getEndTime());
    // }

    // if we shall use a server we will turn over the creation of the
    // simulation to the server
    if (parameters.useMasterServer()) {
      // TODO master server deactivated, January, 2009 - model should be
      // created on the exec. host, not here!
      // System.out.println("The simulation will be executed on the server: "
      // + parameters.getMasterServerName());
      // simulation = (SimulationRun) parameters.getMasterServer()
      // .createSimulation(
      // model,
      // new SimulationConfiguration(1, null, null, parameters
      // .getParameterBlock())).getSimulation();

      try {
        parameters.getMasterServer().executeSimulationConfiguration(
            createConfiguration(), null);
      } catch (RemoteException e) {
        SimSystem.report(Level.SEVERE,
            "Was not able to start the simulation at the master server.", e);
      }

    } else { // if we don't use a server we create a simulation on our own

      computationTask =
          new SimulationRun("SimRun", model,
              (SimulationRunConfiguration) createConfiguration(), null);
    }

    //

    // simulation = new Simulation (this.getClass().getName(), model, proc,
    // mode, simulationServer);
  }

  /**
   * Execute the model, i.e. start the simulation referenced by this object's
   * simulation variable. This variable is set by a preceeding call to
   * createSimulation.
   */
  public final void executeModel() {

    SimSystem.report(Level.INFO, "Simulation started at "
        + new SimpleDateFormat().format(new Date()));

    try {

      if (parameters.isInteractive()) {
        InteractiveConsole intCon = new InteractiveConsole();
        intCon.setSimulation(computationTask);
        intCon.commandLine();
      } else {
        if (parameters.useMasterServer()) {
          parameters.getMasterServer().execute(
              computationTask.getUniqueIdentifier(), null);
        } else { // if we don't use a server we create a simulation on our own
          computationTask.start();
        }
      }

    } catch (RemoteException e) {
      SimSystem.report(e);
      throw new RuntimeException("Remote exception occured. Shutting down.", e);
    }

  }

  /**
   * This method can be overwritten by descendant classes for returning model
   * specific parameter lists.
   * 
   * @return empty string, or a string containing a list of additional
   *         arguments.
   */
  public String extArgsToString() {
    return "";
  }

  /**
   * This method prepares an argument for further processing Input: an argument
   * of type -parameter -parameter=value will be parsed and placed into the
   * parameter and value.
   * 
   * @param argument
   *          the argument
   * 
   * @return the argument parameter
   */
  public String getArgumentParameter(String argument) {
    String s = argument.substring(1);
    if (s.indexOf('=') != -1) {
      return s.substring(0, s.indexOf('='));

    }
    return s;
  }

  /**
   * This method returns the value part of a command line parameter (that's the
   * part behind a =). If there is no equal sign it will return an empty string
   * 
   * @param argument
   *          the argument
   * 
   * @return the argument value
   */
  public String getArgumentValue(String argument) {
    String s = argument.substring(1);

    if (s.indexOf('=') != -1) {
      return s.substring(s.indexOf('=') + 1, s.length());
    }
    return "";
  }

  /**
   * Returns a reference to the simulation created by createSimulation for
   * further usage,.
   * 
   * @return the simulation
   */
  public IComputationTask getComputationTask() {
    return computationTask;
  }

  /**
   * This method can be used in descendant classes for handling custom model
   * parameters.
   * 
   * @param param
   *          the parameter name to be treated
   * @param value
   *          the value of the parameter
   * 
   * @return false if the passed parameter is not a custom parameter, true
   *         otherwise
   */
  public boolean handleParameter(String param, String value) {
    return false;
  }

  /**
   * Checks whether the given string is equal to one of the given words.
   * 
   * @param src
   *          the string to be matched
   * @param words
   *          the list of words of which one could be equal to src.
   * 
   * @return true, if one word is identical to the src string
   */
  protected boolean isOne(String src, String words[]) {
    boolean result = false;

    int i = 0;

    while ((!result) && (i < words.length)) {
      result = (src.compareTo(words[i]) == 0) || result;
      i++;
    }

    return result;
  }

  /**
   * Default argument parser. This method will parse the list of given arguments
   * and it will return true if it was able to parse ALL parameters. If not this
   * can mean that a wrong parameter has been given or that custom parameters
   * have not been removed from the list of arguments before this method is
   * called. This method must be called manually in a concrete main method of a
   * subclass!! This method uses the HashMap returned by getParameter for
   * interpreting the given arguments.
   * 
   * @param args
   *          the args
   */
  public void parseArgs(String args[]) {

    // the return value is ignored, this line is placed here for making sure
    // that the
    // system has already been initialized (without having registered the
    // plugins there
    // are less parameters available!)
    // usually this line should be "ignored", thus it should already have been
    // executed
    // before
    SimSystem.getRegistry();

    String s;
    for (int i = 0; i < args.length; i++) {
      if (!args[i].isEmpty()) {
        // drop the first char of an argument (- or / or \)
        s = args[i].substring(1);

        String param;
        String value;

        if (s.indexOf('=') != -1) {
          param = s.substring(0, s.indexOf('='));
          value = s.substring(s.indexOf('=') + 1, s.length());
        } else {
          param = s;
          value = null;
        }

        if (!handleParameter(param, value)) {

          Parameter par = parameters.getParameter(param);

          if (par == null) {
            throw new InvalidParameterException(param);
          }
          ParamHandler handler = par.getHandler();
          handler.handleParamValue(value, parameters);

        }

      } // end of if args[i]
    } // end of for
  }

  /**
   * This method can be used for printing the table of std arguments. It should
   * be used in conjunction with the parseArgs method (if this method returns
   * false the list of arguments should be printed and the execution halted).
   */
  public void printArgs() {
    System.out.println(argsToString());
  }

  /**
   * Prints the invalid parameter note.
   * 
   * @param param
   *          the param
   */
  public final void printInvalidParameterNote(String param) {
    SimSystem.report(Level.WARNING, "Invalid parameter found: -- " + param
        + " -- !!!!");

    SimSystem
        .report(
            Level.INFO,
            "\n\nJ A M E S  I I  - Valid simulation start parameters"
                + "\n------------------------------------------------------------------------------"
                + "\nEach parameter must be be used with a usual command-line parameter introduction\n"
                + "character as there are - / and \\ depending on the system you are using."
                + "\nHere - is used as a placeholder.");

    SimSystem.report(Level.INFO, argsToString());
  }

  /**
   * Checks whether the given string starts with one of the given words.
   * 
   * @param src
   *          the string to be checked for the words in words
   * @param words
   *          the list of words with which the string could start
   * 
   * @return true, if successful
   */
  protected boolean startsWithOne(String src, String words[]) {
    boolean result = false;

    int i = 0;

    while ((!result) && (i < words.length)) {
      result = (src.indexOf(words[i]) == 0) || result;
      i++;
    }

    return result;
  }

  /**
   * Standard parameter parsing as defined in parseArgs Standard error note
   * (including wrong parameter and list of correct parameters, printed on std
   * out).
   * 
   * @param args
   *          the args
   */
  public final void stdParseArgs(String[] args) {
    try {
      parseArgs(args);
    } catch (InvalidParameterException ipe) {
      printInvalidParameterNote(ipe.getMessage());
      SimSystem.shutDown(0);
    }
  }

}
