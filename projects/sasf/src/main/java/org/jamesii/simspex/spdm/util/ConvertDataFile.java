/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.util;


import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.util.PerformanceTuples;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.dataimport.xml.XMLImportManagerFactory;


/**
 * Uses {@link PerformanceTuples#convertToConfigDataFile(String, String)}.
 * 
 * @author Roland Ewald
 */
public final class ConvertDataFile {

  /**
   * Should not be instantiated.
   */
  private ConvertDataFile() {
  }

  /**
   * Simple command line tool.
   * 
   * @param args
   *          first argument is source file, second argument is destination file
   */
  public static void main(String[] args) {
    try {
      PerformanceTuples.convertToConfigDataFile(new ParameterBlock(args[0],
          XMLImportManagerFactory.RESULT_FILE), args[1]);
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, "Conversion failed", ex);
    }
  }

}
