/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract factory for parameter editor factories.
 * 
 * Date: 06.07.2007
 * 
 * @author Roland Ewald
 */
public class AbstractParamEditorFactory extends
    AbstractFilteringFactory<ParamEditorFactory> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 675468942315106667L;

  /** Type to be edited, type {@link Class}. */
  public static final String TYPE = "type";

  /**
   * Checks if is supported primitive.
   * 
   * @param paramClass
   *          the parameter class
   * 
   * @return true, if is supported primitive
   */
  public static boolean isSupportedPrimitive(Class<?> paramClass) {

    return (Integer.class.equals(paramClass) || Double.class.equals(paramClass)
        || Boolean.class.equals(paramClass) || Long.class.equals(paramClass)
        || Short.class.equals(paramClass) || Byte.class.equals(paramClass) || Float.class
          .equals(paramClass));
  }

  /**
   * Gets the default for primitive.
   * 
   * @param paramClass
   *          the parameter class
   * 
   * @param <V>
   *          the type of the parameter
   * @return the default for primitive
   */
  @SuppressWarnings("unchecked")
  public static <V> V getDefaultForPrimitive(Class<V> paramClass) {
    if (Byte.class.equals(paramClass)) {
      return (V) Byte.valueOf((byte) 0);
    }
    if (Short.class.equals(paramClass)) {
      return (V) Short.valueOf((short) 0);
    }
    if (Integer.class.equals(paramClass)) {
      return (V) Integer.valueOf(0);
    }
    if (Long.class.equals(paramClass)) {
      return (V) Long.valueOf(0);
    }
    if (Double.class.equals(paramClass)) {
      return (V) Double.valueOf(0);
    }
    if (Float.class.equals(paramClass)) {
      return (V) Float.valueOf(0);
    }
    if (Boolean.class.equals(paramClass)) {
      return (V) Boolean.FALSE;
    }
    return null;
  }
}