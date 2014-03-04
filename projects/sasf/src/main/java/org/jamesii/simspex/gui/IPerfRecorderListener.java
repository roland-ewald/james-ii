/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui;


import java.util.Map;

import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;

/**
 * Interface for listeners of the {@link PerfDBRecorder}. These could use the
 * propagated information to give some state information to the user or to
 * collect some statistics.
 * 
 * @author Roland Ewald
 * 
 */
public interface IPerfRecorderListener {

  /**
   * Invoked when the performances was registered.
   * 
   * @param probInst
   *          the problem instance
   * @param application
   *          the application
   * @param performances
   *          the performances
   */
  void performanceRecorded(IProblemInstance probInst, IApplication application,
      Map<PerformanceMeasurerFactory, Double> performances);

}
