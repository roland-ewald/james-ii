/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.cacore;

// TODO: Auto-generated Javadoc
/**
 * The Class Grid2DLauncher.
 */
public class Grid2DLauncher extends Grid1DLauncher {

  /** The height. */
  private int height = 10;

  /**
   * Instantiates a new grid2 d launcher.
   */
  public Grid2DLauncher() {
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
        + "\n-width=        [10] width of the grid (integer)"
        + "\n-height=       [10] height of the grid (integer)";
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
    if (param.compareTo("height") == 0) {
      setHeight(Integer.valueOf(value).intValue());
      // width parameter found => set argument to an emtpy string
      return true;
    }
    return super.handleParameter(param, value);
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @param height
   *          the height to set
   */
  public void setHeight(int height) {
    this.height = height;
  }

}
