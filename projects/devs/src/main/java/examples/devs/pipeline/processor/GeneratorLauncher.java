/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.pipeline.processor;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.simulation.launch.DirectLauncher;

import examples.devs.generator.Generator;

/**
 * A launcher that executes the atomic model {@link Generator}.
 * 
 * @author Alexander Steiniger
 *
 */
public class GeneratorLauncher extends DirectLauncher {

  public GeneratorLauncher() {
    super();
  }

  public static void main(String args[]) {
    GeneratorLauncher launcher = new GeneratorLauncher();

    launcher.stdParseArgs(args);

    Generator generator = new Generator("Generator");

    launcher.setStopTime(200);
    
    SimSystem.report(Level.INFO, "creating simulation");
    launcher.createSimulation(generator);
    
    SimSystem.report(Level.INFO, "running simulation");
    launcher.executeModel();
  }
  
}
