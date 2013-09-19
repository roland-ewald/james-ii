/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.collection.ArrayMap;

/**
 * Filter for getting all methods with the {@link TriggerableByName}
 * annotations.
 * 
 * @author Stefan Leye
 * 
 */
public class TriggerableByNameFilter {

  /**
   * This method returns the names of all methods of a class, that are annotated
   * by {@link TriggerableByName} annotations.
   * 
   * @param cl
   *          to be filtered
   * @return names of methods
   */
  public static Map<String, List<String[]>> filter(Class<?> cl) {
    Map<String, List<String[]>> results = new ArrayMap<>();
    Method[] methods = cl.getDeclaredMethods();
    for (int i = 0; i < methods.length; i++) {
      Method method = methods[i];
      TriggerableByName anno = method.getAnnotation(TriggerableByName.class);
      if (anno != null) {
        Class<?>[] params = method.getParameterTypes();
        if (paramsPrimitive(params)) {
          results.put(method.getName(),
              createParameterInformation(params, anno.parameterDescription()));
        } else {
          SimSystem.report(Level.WARNING, "Method: " + method.getName()
          + " of class: " + cl + " can not be triggered by name!");
        }
      }
    }
    return results;
  }

  /**
   * Params primitive.
   * 
   * @param params
   *          the params
   * 
   * @return true, if successful
   */
  private static boolean paramsPrimitive(Class<?>[] params) {
    boolean result = true;
    for (Class<?> param : params) {
      if (!param.isPrimitive() && param.isInstance(Number.class)
          && param.isInstance(String.class) && param.isInstance(Boolean.class)
          && param.isInstance(Character.class)) {
        result = false;
        SimSystem.report(Level.WARNING, "Parameter is not primitive!");
      }
    }
    return result;
  }

  /**
   * Creates the parameter information.
   * 
   * @param params
   *          the params
   * @param description
   *          the description
   * 
   * @return the list< string[]>
   */
  private static List<String[]> createParameterInformation(Class<?>[] params,
      String[] description) {
    List<String[]> result = new ArrayList<>();
    for (int i = 0; i < params.length; i++) {
      if (description.length < i + 1) {
        String[] info = { params[i].getName(), description[i] };
        result.add(info);
      } else {
        String[] info = { params[i].getName(), params[i].getName() };
        result.add(info);
      }
    }
    return result;
  }

  private TriggerableByNameFilter() {
  }
}
