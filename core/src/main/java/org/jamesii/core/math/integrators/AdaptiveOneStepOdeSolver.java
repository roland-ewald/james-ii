/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.integrators;

/**
 * A pluggable AdaptiveOneStepOdeSolver which uses.
 * 
 * @author Martin Kell
 */
public class AdaptiveOneStepOdeSolver extends AbstractODESolver {

  /** Stepwidth (only a starting value here). */
  private double step;

  /** Cash-Karp coefficients with order 4(3) as default. */
  private static final double A[][] = { { 0 }, // NOSONAR
      { 1.0 / 5.0, 0 },// NOSONAR
      { 3.0 / 40.0, 9.0 / 40.0, 0 },// NOSONAR
      { 3.0 / 10.0, -9.0 / 10.0, 6.0 / 5.0, 0 },// NOSONAR
      { -11.0 / 54.0, 5.0 / 2.0, -70.0 / 27.0, 35.0 / 27.0, 0 },// NOSONAR
      { 1631.0 / 55296.0, 175.0 / 512.0, 575.0 / 13824.0, 44275.0 / 110592.0,// NOSONAR
          253.0 / 4096.0, 0 } };// NOSONAR

  /** The b1. */
  private static final double B1[] = { 37.0 / 378.0, 0, 250.0 / 621.0,// NOSONAR
      125.0 / 594.0,// NOSONAR
      0, 512.0 / 1771.0 };// NOSONAR

  /** The b2. */
  private static final double B2[] = { 2825.0 / 27648.0, 0, 18575.0 / 48384.0,// NOSONAR
      13525.0 / 55296.0, 277.0 / 14336.0, 1.0 / 4.0 };// NOSONAR

  /** The c. */
  private static final double C[] = { 0, 1.0 / 5.0, 3.0 / 10.0, 3.0 / 5.0, 1,// NOSONAR
      7.0 / 8.0 };// NOSONAR

  /** The order. */
  private static final int ORDER = 3;

  /** The step-size function. */
  private IOdeOneStep stepFunc;

  /** The step control. */
  private IOdeStepControl stepCtrl;

  /**
   * Instantiates a new adaptive one step ode solver.
   * 
   * @param odeSystem
   *          the equations
   * @param initialState
   *          the initial state
   * @param startVar
   *          the start value of the dependent variable
   * @param stopVar
   *          the end value of the dependent variable
   * @param startstep
   *          the start step-size
   */
  public AdaptiveOneStepOdeSolver(IOde odeSystem, double startVar,
      double stopVar, double startstep) {
    super(odeSystem, startVar, stopVar);
    stepFunc = new RKEmbedStep(A, B1, B2, C, 4);
    stepCtrl = new HairerStepControl();
    this.step = startstep;
  }

  /**
   * Instantiates a new adaptive one step ode solver.
   * 
   * @param odeSystem
   *          the equations
   * @param initialState
   *          the initial state
   * @param startVar
   *          the start value of the dependent variable
   * @param stopVar
   *          the end value of the dependent variable
   * @param startstep
   *          the start step-size
   * @param stepfunc
   *          the step-size function
   * @param stepctrl
   *          the step control
   */
  public AdaptiveOneStepOdeSolver(IOde odeSystem, double startVar,
      double stopVar, double startstep, IOdeOneStep stepfunc,
      IOdeStepControl stepctrl) {
    super(odeSystem, startVar, stopVar);
    this.stepFunc = stepfunc;
    this.stepCtrl = stepctrl;
    this.step = startstep;
  }

  /**
   * Generates the initial point (to avoid Index out of bounds).
   */
  @Override
  protected void initiate() {
    getInterpolationValues().add(0, getStartValue());

    // will throw exception if an ODE has no initial value.
    this.getOdeSystemTrace().add(0, getInitialState());
  }

  @Override
  public void solveStep(int cStep) {
    double ret[][] = createSolution(cStep);
    getOdeSystemTrace().add(cStep, ret[1]);
    getInterpolationValues().add(cStep,
        getInterpolationValues().get(cStep - 1) + ret[0][0]);
  }

  /**
   * Create the solution at the actual step.
   * 
   * @param a
   *          the step
   * 
   * @return double[2][] with ret[0] = curstep, ret[1] = solution
   */
  private double[][] createSolution(int a) {
    double x = getInterpolationValues().get(a - 1);
    double[][] solutions; // = new double[2][getOdeSystem().getDimension()];
    double yold[] = this.getOdeSystemTrace().get(a - 1);
    double curstep = step;
    double to = getStopValue();
    boolean reject = false;

    if (x + 1.01 * curstep >= to) {
      curstep = to - x;
      step = curstep;
    }

    while (true) { // to avoid recurrence, do recalculate of steps using a loop
      if (Double.compare((x + step), x) == 0) {
        throw new java.lang.ArithmeticException(
            "Stepsize becoming to small uses another ode-solver");
      }

      curstep = step;

      // computes the two solutions use a embed runge-kutta one-step method
      solutions = stepFunc.doStep(yold, x, curstep, getOdeSystem());

      // should only happen, when stepFunc is implicit and Newton-Iterations is
      // impossible (e.g. diverges)
      if (solutions == null) {
        if ((Double.compare(step, 1e-20) <= 0)
            || (Double.compare(x + step / 2.0, x) == 0)) {
          throw new java.lang.ArithmeticException(
              "Stepsize becoming too small for this step-function (wrong Jacobian Matrix!?)");
        }
        step /= 2.0;
        reject = true;
        continue;
      }

      double nextStep[] = { step };
      // check this step, returns new step and accept (bool)
      boolean b =
          stepCtrl.checkStep(nextStep, solutions[0], yold, solutions[1], 4);

      step = nextStep[0];

      if (b) {
        if (reject && Double.compare(step, curstep) > 0) {
          // do not stretch, if shrink once
          step = curstep;
        }

        break;
      }
      reject = true;
    }

    // returns old taken step, and the current solution
    double ret[][] = { { curstep }, solutions[0] };
    return ret;
  }

  /**
   * Calculate the next step.
   * 
   * @param x
   *          the current point of the dependent variable
   * @param yold
   *          the old function value
   * @param step
   *          the step
   * @param to
   *          the to
   * @param ode
   *          the equation
   * @param stepFunc
   *          the step function
   * @param stepCtrl
   *          the step control
   * 
   * @return the double[][]
   */
  public static double[][] doStep(double x, double yold[], double step,
      double to, IOde ode, IOdeOneStep stepFunc, IOdeStepControl stepCtrl) {
    double[][] solutions; // = new double[2][ode.getDimension()];
    double curstep = step;

    boolean reject = false;

    if (x + 1.01 * curstep >= to) {
      curstep = to - x;
      step = curstep;
    }

    while (true) { // to avoid recurrence, do recalculate of steps using a loop
      if (Double.compare(x + step, x) == 0) {
        throw new java.lang.ArithmeticException(
            "Stepsize becoming to small uses another ode-solver");
      }

      curstep = step;

      // computes the two solutions use a embed runge-kutta one-step method
      solutions = stepFunc.doStep(yold, x, curstep, ode);

      // should only happen, when stepFunc is implicit and Newton-Iterations is
      // impossible (e.g. diverges)
      if (solutions == null) {
        if (Double.compare(step, 1e-20) <= 0
            || Double.compare((x + step / 2.0), x) == 0) {
          throw new java.lang.ArithmeticException(
              "Stepsize becoming to small for this step-function (wrong Jacobian Matrix!?)");
        }
        step /= 2.0;
        reject = true;
        continue;
      }

      double nextStep[] = { step };
      // check this step, returns new step and accept (bool)
      boolean b =
          stepCtrl.checkStep(nextStep, solutions[0], yold, solutions[1], 4);

      step = nextStep[0];

      if (b) {
        if (reject && Double.compare(step, curstep) > 0) {
          step = curstep;
        }
        break;
      }
      reject = true;
    }

    // returns old taken step, and the current solution
    double ret[][] = { { curstep, step }, solutions[0] };
    return ret;
  }
}
