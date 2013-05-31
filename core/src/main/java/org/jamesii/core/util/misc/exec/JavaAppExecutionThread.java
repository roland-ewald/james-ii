/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.exec;

import java.io.File;
import java.util.Map;

/**
 * Executes Java Virtual Machine in working directory.
 * 
 * @author Roland Ewald
 * 
 */
public class JavaAppExecutionThread extends ApplicationExecutionThread {

  /** The Constant JAVA_CMD. */
  static final String JAVA_CMD = "java";

  public JavaAppExecutionThread(Class<?> mainClass, String... args) {
    super(new File(System.getProperty("user.dir")), getJavaCommand(mainClass,
        args));

    // fix the environment because we're in the java world
    Map<String, String> m = getProcessBuilder().environment();
    m.put("CLASSPATH", System.getProperty("java.class.path"));
  }

  /**
   * Gets the java command.
   * 
   * @param mainClass
   *          the main class
   * @param args
   *          the args
   * 
   * @return the java command
   */
  private static String[] getJavaCommand(Class<?> mainClass, String[] args) {
    String[] fullCommand = new String[args.length + 2];
    fullCommand[0] = JAVA_CMD;
    fullCommand[1] = mainClass.getCanonicalName();
    System.arraycopy(args, 0, fullCommand, 2, args.length);
    return fullCommand;
  }

}
