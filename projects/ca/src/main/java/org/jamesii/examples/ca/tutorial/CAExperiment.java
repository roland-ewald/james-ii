/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.examples.ca.tutorial;

import java.net.URI;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * Sample experiment for the {@link BogusModel}.
 * 
 * @author Roland Ewald
 */
public class CAExperiment {

  /**
   * Hidden constructor.
   */
  private CAExperiment() {
  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    try {
      ApplicationLogger.setLogLevel(Level.ALL);
      BaseExperiment exp = new BaseExperiment();
      exp.setModelLocation(new URI(
          "java://org.jamesii.examples.ca.tutorial.BogusModel"));
      ParameterBlock pb = new ParameterBlock();
      pb.addSubBlock(SimTimeStopFactory.SIMEND, 10.0);

      exp.setComputationTaskStopPolicyFactory(new ParameterizedFactory<ComputationTaskStopPolicyFactory<?>>(
          new SimTimeStopFactory(), pb));
      exp.setRepeatRuns(1);
      exp.execute();
    } catch (Exception ex) {
      SimSystem.report(ex);
    }
  }
}
