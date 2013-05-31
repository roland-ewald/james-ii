/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.logging;

import java.util.Collection;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * This class provides some utility methods for the log support.
 * 
 * @author Jan Himmelspach
 * 
 */
public final class LogUtils {

  /**
   * Hidden constructor.
   */
  private LogUtils() {
  }

  public static <O> O logCreation(O object, ParameterBlock parameters) {
    return object;
    // SimSystem.report(Level.INFO, "Created object");
  }

  /**
   * Method to infer the caller of a method in the LogUtils class.
   * 
   * @return the class name of the caller or null if the caller cannot be
   *         identified.
   */
  @SuppressWarnings("unused")
  private String inferCallerClass() {

    // Get the stack trace of the method call
    StackTraceElement stack[] = (new Throwable()).getStackTrace();

    // Find the LogUtils class in the stack trace
    int i = 0;
    while (i < stack.length) {
      StackTraceElement element = stack[i];
      String className = element.getClassName();
      if (className.equals(LogUtils.class.getName())) {
        // if found we exit the loop
        break;
      }
      i++;
    }
    // Find the first element before this class in the trace
    while (i < stack.length) {
      StackTraceElement element = stack[i];

      String className = element.getClassName();
      if (!className.equals("org.jamesii.core.util.logging.LogUtils")) {
        // if found return the class
        return className;
      }
      i++;
    }
    return null;
  }

  /**
   * Method to infer the call of another method, allowing specification of
   * classes not to consider (logger, logger helper & logger wrapper classes)
   * and levels to go up even further in the stack trace after the first "good"
   * entry (i.e. from a not-to-be-ignored class) has been found. The latter
   * parameter is for cases where logger calls are wrapped in a single log
   * method, for example. If the stack trace is shorter than the index of the
   * first "good" class plus the given value, the final stack trace element is
   * returned.
   * 
   * @param classesToIgnore
   *          Names of classes not to consider
   * @param levelsUp
   * @return StackTraceElement, or null if all classes in stack trace are to be
   *         ignored
   */
  public static StackTraceElement inferCallerClass(
      Collection<String> classesToIgnore, int levelsUp) {
    StackTraceElement stack[] = (new Throwable()).getStackTrace();
    int i = 0;

    while (i < stack.length
        && classesToIgnore.contains(stack[i].getClassName())) {
      i++;
    }
    if (i == stack.length) {
      return null; // only classes to ignore in stack trace
    }
    if (i + levelsUp >= stack.length) {
      return stack[stack.length - 1];
    }
    return stack[i + levelsUp];
  }

  /**
   * Method to infer the caller of a method in the LogUtils class.
   * 
   * @param beforeClasses
   *          ordered list of class names, we search for the 1st and then skip
   *          over all lines containing subsequent classes, if one does not
   *          exist it is skipped
   * @return the class name of the caller or null if the caller cannot be
   *         identified.
   */
  public static StackTraceElement inferCallerClass(String[] beforeClasses) {
    // Get the stack trace of the method call
    StackTraceElement stack[] = (new Throwable()).getStackTrace();

    int i = 0;

    for (int bc = 0; bc < beforeClasses.length; bc++) {
      int oldI = i;
      i = inferCallerClass(beforeClasses[bc], stack, i);
      if ((i == -1) && (bc > 0)) {
        i = oldI;
      }
      if (i == -1) {
        return null;
      }
    }
    return stack[i];
  }

  /**
   * Method to infer the caller of a method in the LogUils class.
   * 
   * @param beforeClass
   * @param stack
   * @param index
   * @return the class name of the caller or null if the caller cannot be
   *         identified.
   */
  public static int inferCallerClass(String beforeClass,
      StackTraceElement stack[], int index) {

    int i = index;
    // Find the given class to ignore in the stack trace
    while (i < stack.length) {
      StackTraceElement element = stack[i];
      String className = element.getClassName();
      if (className.equals(beforeClass)) {
        // if found we exit the loop
        break;
      }
      i++;
    }
    // Find the first element before this class in the trace
    while (i < stack.length) {
      StackTraceElement element = stack[i];
      String className = element.getClassName();
      if (!className.equals(beforeClass)) {
        // if found return the class
        return i;
      }
      i++;
    }
    return -1;
  }

}
