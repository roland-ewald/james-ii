/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This is a rudimentary abstract class for a numerical ODE integrator.
 * 
 * @author Roland Ewald
 */
public abstract class AbstractODESolver implements IODESolver {

  /** The index of the current step for the interpolation value. */
  private int currentStep = -1;

  /** Initial values of all variables. */
  private double[] initialState;

  /** List of interpolation values (x-value of interpolation point). */
  private List<Double> interpolationValues = new ArrayList<>();

  /** Array of ordinary differential equations. */
  private IOde odeSystem;

  /**
   * Variable values for the corresponding interpolation values(defined in
   * interpolationValues).
   */
  private List<double[]> odeSystemTrace = new ArrayList<>();

  /** Start value of the dependent variable. */
  private double startValue;

  /** Stop value of the dependent variable. */
  private double stopValue;

  /**
   * Instantiates a new abstract ode solver. Sets the initial state of the
   * system using the method {@link IOde#getInitialState()} from the odeSystem
   * object.
   * 
   * @param odeSystem
   *          the ode system
   * @param startVal
   *          the start value
   * @param stopVal
   *          the stop value
   */
  public AbstractODESolver(IOde odeSystem, double startVal, double stopVal) {
    this.odeSystem = odeSystem;
    this.initialState = odeSystem.getInitialState();
    this.startValue = startVal;
    this.stopValue = stopVal;
  }

  @Override
  public final void solveODESystem() {
    // initiate the system
    initiate();
    // solve the system by solving every single next step till the stop value
    for (int i = 1; getInterpolationValues().get(i) < getStopValue(); i++) {
      solveStep(i);
    }
  }

  @Override
  public final void solveNextStep() {
    // don't do anything if we reached the end
    if (currentStep >= getInterpolationValues().size() - 1) {
      return; // TODO or start all over?
    }

    // increase the current step
    currentStep++;
    // solve this step using the abstract method
    solveStep(currentStep);
  }

  /**
   * Initiates the solver with all necessary information. This method will be
   * called before solving the system sequentially using the method
   * {@link #solveODESystem()}.
   */
  protected abstract void initiate();

  /**
   * Core function for the solver subclasses. Creates a solution for the given
   * step. Will be called repeatedly by the {@link #solveODESystem()} method.
   * 
   * @param cStep
   *          the current step to solve
   */
  protected abstract void solveStep(int cStep);

  /**
   * Same as above, but also stores the x-values for methods with adaptive step
   * size.
   * 
   * @param fileName
   *          the file name
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void asCreateFile(String fileName) throws IOException {
    try (FileWriter fo = new FileWriter(fileName); PrintWriter out = new PrintWriter(fo)) {
      for (int a = 0; a < getOdeSystemTrace().size(); a++) {
        out.print(interpolationValues.get(a) + ",");
        for (int i = 0; i < odeSystem.getDimension(); i++) {
          out.print(getOdeSystemTrace().get(a)[i] + ",");
        }
        if (a >= 1) {
          out.print(interpolationValues.get(a) - interpolationValues.get(a - 1));
        }
        out.println();
      }
    }
  }

  /**
   * This creates a file with given name, which stores the solutions for easier
   * processing.
   * 
   * @param fileName
   *          the name of the output file
   * @throws IOException
   *           if output goes wrong
   */
  public void createFile(String fileName) throws IOException {
    FileWriter fo = null;
    PrintWriter out = null;
    try {
      fo = new FileWriter(fileName);
      out = new PrintWriter(fo);
      for (int a = 0; a < getOdeSystemTrace().size(); a++) {
        for (int i = 0; i < odeSystem.getDimension(); i++) {
          out.print(getOdeSystemTrace().get(a)[i] + ",");
        }
        out.println();
      }
    } finally {
      out.close();
      if (fo != null) {
        fo.close();
      }
    }
  }

  /**
   * Get the initial state of the ODE.
   * 
   * @return an array containing the initial values
   */
  protected double[] getInitialState() {
    return initialState;
  }

  /**
   * Get the interpolated values of the function resulting from the ODE.
   * 
   * @return Returns the interpolationValues.
   */
  @Override
  public List<Double> getInterpolationValues() {
    return interpolationValues;
  }

  /**
   * Return the ODE system (i.e. the equations)
   * 
   * @return Returns the odeSystem.
   */
  @Override
  public IOde getOdeSystem() {
    return odeSystem;
  }

  /**
   * Get the trace of the ODE's. Should be called after solveODESystem() (since
   * the trace is calculated during this method)!
   * 
   * @return Returns the odeSystemTrace.
   */
  @Override
  public List<double[]> getOdeSystemTrace() {
    return odeSystemTrace;
  }

  /**
   * Get the start value of the dependent parameter.
   * 
   * @return the start value
   */
  public double getStartValue() {
    return startValue;
  }

  /**
   * Get the end value of the dependent parameter.
   * 
   * @return the end value
   */
  public double getStopValue() {
    return stopValue;
  }

  /**
   * Get the index of the current step.
   * 
   * @return the current step
   */
  public int getCurrentStep() {
    return currentStep;
  }
}
