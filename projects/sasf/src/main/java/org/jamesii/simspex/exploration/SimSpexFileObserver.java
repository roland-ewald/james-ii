/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration;


import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.IEntity;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.core.util.misc.Triple;

/**
 * Simple observer for {@link ISimSpaceExplorer} instances.
 * 
 * @author Roland Ewald
 * 
 */
public class SimSpexFileObserver implements IObserver<IEntity> {

  /** The file writer to be used. */
  private FileWriter writer;

  /** The file name of the log file. */
  private final String fileName;

  /**
   * Creates observer.
   * 
   * @param file
   *          the log file name
   */
  public SimSpexFileObserver(String file) {
    fileName = file;
  }

  @Override
  public void update(IEntity entity) {
    update(entity, null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void update(IEntity entity, Object hint) {

    if (writer == null) {
      try {
        writer = new FileWriter(fileName, true);
      } catch (Exception ex) {
        SimSystem.report(Level.SEVERE, "Could not create writer for file'"
            + fileName + "'.", ex);
      }
    }

    if (hint == null) {
      return;
    }

    Triple<ExplorationPhase, Double, Map<String, Serializable>> myHint =
        (Triple<ExplorationPhase, Double, Map<String, Serializable>>) hint;
    try {
      writer
          .write("CURRENT TIME:" + myHint.getB() + "\t Model:"
              + Strings.dispMap(myHint.getC()) + "\t phase:" + myHint.getA()
              + "\n");
      writer.flush();
    } catch (IOException ex) {
      SimSystem.report(Level.SEVERE, "Could not flush writer for file'"
          + fileName + "'.", ex);
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
      } catch (IOException ex) {
        SimSystem.report(Level.SEVERE, "Could not close writer for file'"
            + fileName + "'.", ex);
      }
    }

  }
}
