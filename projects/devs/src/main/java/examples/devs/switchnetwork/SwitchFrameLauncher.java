/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.switchnetwork;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.simulation.launch.DirectLauncher;
import org.jamesii.core.simulation.launch.Launcher;

/**
 * A simple {@link Launcher} for executing the {@link SwitchFrame}.
 *
 * @author Alexander Steiniger
 *
 */
public class SwitchFrameLauncher extends DirectLauncher {

  public SwitchFrameLauncher() {
    super();
  }
  
  public static void main(String[] args) {
    SwitchFrameLauncher launcher = new SwitchFrameLauncher();

    launcher.stdParseArgs(args);

    SwitchFrame frame = new SwitchFrame("Frame");

    launcher.setStopTime(200);
    
    SimSystem.report(Level.INFO, "creating simulation");
    launcher.createSimulation(frame);
    
    SimSystem.report(Level.INFO, "running simulation");
    launcher.executeModel();
  }

}
