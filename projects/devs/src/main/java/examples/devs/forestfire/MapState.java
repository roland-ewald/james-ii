/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire;

import examples.devs.JamesState;

/**
 * The Class MapState.
 * 
 * @author Jan Himmelspach *
 */
public class MapState extends JamesState {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 5210571759049357462L;

  /** The height. */
  int height;

  // in this version, we're just watching the fire
  int map[][];

  /** The width. */
  int width;

  /**
   * Gets the height.
   * 
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Gets the map pos.
   * 
   * @param x
   *          the x
   * @param y
   *          the y
   * 
   * @return the map pos
   */
  public int getMapPos(int x, int y) {
    return map[y][x];
  }

  /**
   * Gets the width.
   * 
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Sets the map pos.
   * 
   * @param x
   *          the x
   * @param y
   *          the y
   * @param value
   *          the value
   */
  public void setMapPos(int x, int y, int value) {
    map[y][x] = value;
    changed();
  }

  /**
   * Sets the map size.
   * 
   * @param height
   *          the height
   * @param width
   *          the width
   */
  public void setMapSize(int height, int width) {
    map = new int[height][width];
    this.width = width;
    this.height = height;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        map[x][y] = FireState.INITIALIZING;
      }
    }
  }

}
