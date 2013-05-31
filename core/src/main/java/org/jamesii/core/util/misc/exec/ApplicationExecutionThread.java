/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.exec;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.jamesii.SimSystem;

/**
 * The Class ApplicationExecutionThread.
 * 
 * This thread encapsulates a process builder and output streams reading of the
 * external application started.
 * 
 * It can be used whenever an external application has to be called from within
 * the framework. By automatically reading the potential output streams of the
 * application called any blocking due to unread outputs is avoided. These
 * outputs are discarded.
 * 
 * @author Jan Himmelspach
 */
public class ApplicationExecutionThread extends Thread {

  /**
   * The process builder - used to create the instance of the application to be
   * executed.
   */
  private ProcessBuilder processBuilder;

  /**
   * Reader for the output stream of the process started. Only available after
   * the process has been started.
   */
  private StreamReader output;

  /**
   * Reader for the error stream of the process started. Only available after
   * the process has been started.
   */
  private StreamReader errors;

  /**
   * Reference to the external process which allows us to cancel it.
   */
  private Process externalProcess = null;

  /**
   * Instantiates a new application execution thread.
   * 
   * The external application is started if the thread is started.
   * 
   * @param command
   *          the commands to be used to start the application
   * @param workingDir
   *          the working directory to be used by the application
   */
  public ApplicationExecutionThread(File workingDir, String... command) {
    setProcessBuilder(new ProcessBuilder(command));

    getProcessBuilder().directory(workingDir);
  }

  @Override
  public void run() {

    externalProcess = null;
    try {
      // create the process
      externalProcess = getProcessBuilder().start();

      try {
        externalProcess.waitFor();
        // inform all objects waiting that the external process has finished
        synchronized (this) {
          notifyAll();
        }
      } catch (InterruptedException e) {
        SimSystem.report(e);
      }
    } catch (IOException ioe) {
      SimSystem.report(ioe);
    }

    // create the readers to empty the output stream(s) of the called
    // application, but forget anything written to these
    if (externalProcess != null) {
      output = new StreamReader(externalProcess.getInputStream(), true);
      errors = new StreamReader(externalProcess.getErrorStream(), true);

      // activate the stream readers
      output.start();
      errors.start();
    } else {
      SimSystem.report(Level.SEVERE, "Process could not be created.");
    }
  }

  /**
   * Cancel the external sub process.
   */
  public void cancel() {
    if (externalProcess != null) {
      externalProcess.destroy();
    }
  }

  /**
   * @return the output
   */
  protected final StreamReader getOutput() {
    return output;
  }

  /**
   * @param output
   *          the output to set
   */
  protected final void setOutput(StreamReader output) {
    this.output = output;
  }

  /**
   * @return the errors
   */
  protected final StreamReader getErrors() {
    return errors;
  }

  /**
   * @param errors
   *          the errors to set
   */
  protected final void setErrors(StreamReader errors) {
    this.errors = errors;
  }

  /**
   * @return the externalProcess
   */
  protected final Process getExternalProcess() {
    return externalProcess;
  }

  /**
   * @param externalProcess
   *          the externalProcess to set
   */
  protected final void setExternalProcess(Process externalProcess) {
    this.externalProcess = externalProcess;
  }

  /**
   * @return the processBuilder
   */
  protected final ProcessBuilder getProcessBuilder() {
    return processBuilder;
  }

  /**
   * @param processBuilder
   *          the processBuilder to set
   */
  protected final void setProcessBuilder(ProcessBuilder processBuilder) {
    this.processBuilder = processBuilder;
  }

}
