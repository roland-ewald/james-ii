/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.cacore;

import org.jamesii.core.simulation.launch.DirectLauncher;

// TODO: Auto-generated Javadoc
/**
 * The Class Grid1DLauncher.
 */
public class Grid1DLauncher extends DirectLauncher {

  /** The width. */
  private int width = 10;

  /**
   * Instantiates a new grid1 d launcher.
   */
  public Grid1DLauncher() {
    super();
  }

  /**
   * Print the default arguments plus the model specific ones!.
   * 
   * @return the string
   */
  @Override
  public String extArgsToString() {
    return "\nModel specific parameters:"
        + "\n-width=        [4] width of the grid (integer)";
  }

  /**
   * Checks wether the given parameter is known, if known the parameter is
   * interpreted and the function returns true, otherwise it will return the
   * inherited function's implementation return code.
   * 
   * @param param
   *          the param
   * @param value
   *          the value
   * 
   * @return true if the parameter was handled, false otherwise
   */
  @Override
  public boolean handleParameter(String param, String value) {
    if (param.compareTo("width") == 0) {
      setWidth(Integer.valueOf(value).intValue());
      // width parameter found => set argument to an emtpy string
      return true;
    }
    return super.handleParameter(param, value);
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param width
   *          the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

}
