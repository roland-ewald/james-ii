/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometric;

import org.jamesii.core.util.misc.Triple;

/**
 * Simple triangle class. Non optimized.
 * 
 * @author Stefan Rybacki
 */
class Triangle extends Triple<Vertex, Vertex, Vertex> {

  /**
   * Instantiates a new triangle.
   * 
   * @param a
   *          the a
   * @param b
   *          the b
   * @param c
   *          the c
   */
  public Triangle(Vertex a, Vertex b, Vertex c) {
    super(a, b, c);
  }

}
