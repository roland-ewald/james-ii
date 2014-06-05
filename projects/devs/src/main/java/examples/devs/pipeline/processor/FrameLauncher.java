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

/**
 * A launcher that executes the coupled model {@link Frame}.
 * 
 * 
 * @author Alexander Steiniger
 * 
 */
public class FrameLauncher extends DirectLauncher {

  public FrameLauncher() {
    super();
  }

  public static void main(String args[]) {
    FrameLauncher launcher = new FrameLauncher();

    launcher.stdParseArgs(args);

    Frame frame = new Frame("Frame");

    launcher.setStopTime(200);
    
    SimSystem.report(Level.INFO, "creating simulation");
    launcher.createSimulation(frame);
    
    SimSystem.report(Level.INFO, "running simulation");
    launcher.executeModel();
  }

}
