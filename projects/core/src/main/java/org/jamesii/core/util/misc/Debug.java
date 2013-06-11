/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.lang.reflect.Field;

import org.jamesii.SimSystem;

/**
 * The Class Debug.
 * 
 * @author Jan Himmelspach
 */
public final class Debug {

  /**
   * Hidden constructor.
   */
  private Debug() {
  }

  /**
   * To string.
   * 
   * @param o
   *          the o
   * 
   * @return the string
   */
  public static String toString(Object o) {
    StringBuilder result = new StringBuilder();

    for (Field f : o.getClass().getFields()) {
      result.append(f.getName() + "\n");
      result.append("   " + fieldToString(0, f));
    }

    return result.toString();
  }

  /**
   * Field to string.
   * 
   * @param o
   *          the o
   * @param f
   *          the f
   * 
   * @return the string
   */
  public static String fieldToString(Object o, Field f) {
    StringBuilder result = new StringBuilder();

    try {
      result.append(f.get(o));
    } catch (IllegalArgumentException | IllegalAccessException e) {
    }

    return result.toString();
  }

  /**
   * To string rec.
   * 
   * @param o
   *          the o
   * @param f
   *          the f
   * @param indent
   *          the indent
   * 
   * @return the string
   */
  private static String toStringRec(Object o, Field f, String indent) {
    StringBuilder result = new StringBuilder();

    for (Field f2 : f.getClass().getFields()) {
      result.append(f2.getName() + "\n");

      if (f2.getClass().getFields().length > 0) {
        try {
          result.append(indent + toStringRec(f.get(o), f2, indent + indent));
        } catch (IllegalArgumentException | IllegalAccessException e) {
          SimSystem.report(e);
        }
      } else {
        result.append(indent + fieldToString(0, f2));
      }
    }

    return result.toString();
  }

  /**
   * To string recursive.
   * 
   * @param o
   *          the object to be "printed"
   * 
   * @return the string
   */
  public static String toStringRecursive(Object o) {
    StringBuilder result = new StringBuilder();

    for (Field f : o.getClass().getFields()) {
      result.append(f.getName() + "\n");
      result.append("   " + toStringRec(o, f, "   "));
    }

    return result.toString();
  }

}
