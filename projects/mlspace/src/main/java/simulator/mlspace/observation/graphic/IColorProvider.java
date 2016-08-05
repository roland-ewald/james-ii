/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic;

import java.awt.Color;

/**
 * Interface for everything able to determine a color for objects of a certain
 * type. The color may be calculated from each object's properties, but lookup
 * of objects in a map of predefined colors or provision of a default color are
 * perfectly permissible.
 *
 * @author Arne Bittig
 * @param <T>
 *          Type for which to calculate color
 * @date 24.05.2012
 */
public interface IColorProvider<T> {

  /**
   * Calculate color for given object
   * 
   * @param obj
   *          Object
   * @return Color
   */
  Color getColor(T obj);
}
