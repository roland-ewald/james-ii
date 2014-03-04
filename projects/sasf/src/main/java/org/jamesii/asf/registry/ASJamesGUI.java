/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry;


import java.util.logging.Level;

import javax.swing.UnsupportedLookAndFeelException;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.james.JamesGUI;

/**
 * Start-up class for the GUI, using the {@link AlgoSelectionRegistry}.
 * 
 * WATCH OUT: No registry-related command line parameters will work for this
 * class, since the registry is initialised prior to anything else!
 * 
 * @author Roland Ewald
 * 
 */
public final class ASJamesGUI {

  /**
   * Should not be instantiated.
   */
  private ASJamesGUI() {
  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   * @throws ClassNotFoundException
   *           the class not found exception
   * @throws InstantiationException
   *           the instantiation exception
   * @throws IllegalAccessException
   *           the illegal access exception
   * @throws UnsupportedLookAndFeelException
   *           the unsupported look and feel exception
   */
  public static void main(String[] args) throws ClassNotFoundException,
      InstantiationException, IllegalAccessException,
      UnsupportedLookAndFeelException {
    SimSystem.setRegistry(new AlgoSelectionRegistry());
    SimSystem.report(Level.INFO, "WARNING: the system registry is set to: "
        + SimSystem.getRegistry().getClass().getName());
    JamesGUI.main(args);
  }
}
