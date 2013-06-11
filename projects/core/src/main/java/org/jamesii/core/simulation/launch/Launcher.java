/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.launch;

import java.io.IOException;

import org.jamesii.SimSystem;
import org.jamesii.core.cmdparameters.InvalidParameterException;
import org.jamesii.core.cmdparameters.ParamHandler;
import org.jamesii.core.cmdparameters.Parameter;
import org.jamesii.core.cmdparameters.Parameters;
import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.IExperimentReader;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The Class Launcher.
 * 
 * 
 * 
 */
public class Launcher {

  /** The experiment. */
  BaseExperiment experiment;

  /** The parameters (command line). */
  protected Parameters parameters = new Parameters();

  /**
   * execute the experiment.
   */
  public final void executeExperiment() {
    if (experiment == null) {
      throw new RuntimeException("No experiment specified!");
    }
    experiment.execute();
  }

  /**
   * This method can be used in descendant classes for handling custom model
   * parameters.
   * 
   * @param param
   *          the param
   * @param value
   *          the value
   * 
   * @return false if the passed parameter is not a custom parameter, true
   *         otherwise
   */
  public boolean handleParameter(String param, String value) {
    return false;
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
    // plug-ins there
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
   * Creates the specified experiment by determining the appropriate factory.
   * 
   * @param info
   *          the info
   * 
   * @return the base experiment
   */
  protected BaseExperiment setupExperiment(ExperimentInfo info) {

    ParameterBlock pb = new ParameterBlock();
    pb.addSubBlock("experimentInfo", new ParameterBlock(info));
    IExperimentReader reader =
        SimSystem.getRegistry()
            .getFactory(AbstractExperimentReaderFactory.class, pb).create(null);

    BaseExperiment exp = null;
    try {
      exp = reader.readExperiment(pb);
    } catch (IOException ex) {
      SimSystem.report(ex);
    }
    return exp;
  }

}
