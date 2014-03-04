/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.experiments;

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.simspex.gui.SimSpExPerspective;


/**
 * Super class for simple experiments using a {@link org.jamesii.simspex.exploration.ISimSpaceExplorer}.
 * 
 * @see org.jamesii.simspex.exploration.ISimSpaceExplorer
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleExplorationExperiment {

  /**
   * Instantiates a new simple exploration experiment.
   * 
   * @param dbURL
   *          the database url
   * @param usr
   *          the user name
   * @param pwd
   *          the password
   * @param driver
   *          the driver
   * @param clearDB
   *          the flag to clear the database
   */
  public SimpleExplorationExperiment(String dbURL, String usr, String pwd,
      String driver, boolean clearDB) {
    SimSpExPerspective.setDbConnectionData(new DBConnectionData(dbURL, usr,
        pwd, driver));

    if (clearDB) {
      SimSpExPerspective.getPerformanceDataBase().clear();
    }
  }
}
