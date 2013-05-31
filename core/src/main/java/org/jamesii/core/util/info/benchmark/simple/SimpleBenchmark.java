/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.info.benchmark.simple;

import org.jamesii.core.util.StopWatch;
import org.jamesii.core.util.info.benchmark.IBenchmark;
import org.jamesii.core.util.info.benchmark.IBenchmarkingResult;

/**
 * The Class SimpleBenchmark. Performs numerical modifications on double, long,
 * and integer values. Does not take into account cache effects, does not take
 * into account mixed data type operations.<br/>
 * In addition multiple cores / cpus are not tested. Thus the results computed
 * here are the results of a single core/cpu!<br/>
 * 
 ********************************************************************** <br/>
 * 
 * <b>please note</b>: the order of arithmetic operations has been carefully
 * selected to avoid their (partial) removal during optimization efforts of the
 * JIT <br/>
 ********************************************************************** <br/>
 * 
 * 
 * @author Jan Himmelspach
 */
public class SimpleBenchmark implements IBenchmark {

  /**
   * The Constant OPS. The number of iterations of the four basic operation to
   * be done per data type measured.
   */
  static final int OPS = 10000000;// Integer.MAX_VALUE;

  /**
   * The Constant OPSd. Double version of {@link #OPS} - for computing the final
   * number of operations done (might exceed int).
   */
  private static final double OPSDONE = Double.valueOf(OPS);

  /**
   * The Constant minTime. The minimum time spend in each benchmark. The higher
   * the more reliable the results are.
   */
  private static final long MINTIME = 3000;

  /**
   * The fakeResult is a variable set by the different sub routines (the value
   * calc. is stored in here so that a compiler can't find out whether he can
   * get rid of all arithmetic operations in the loops). Setting this variable
   * from the outside has no effect, to read it is pretty senseless. It is
   * public to make sure that the compiler does not detect that it is never
   * read.
   */
  private double fakeResult;

  /**
   * Benchmark arithmetic operations based on data type double.
   * 
   * @return the number of operations (MFlops)
   */
  private long benchDouble() {
    // the counter used to find out how many loop passes had to be executed
    // before the minTime had been exceeded
    int c = 0;

    // stop watch used to find out the number of milliseconds elapsed
    StopWatch sw = new StopWatch();
    double e = 0.;
    do {
      c++;
      sw.start();
      for (int i = 0; i < OPS; i++) {
        // double e = 2.1 + 4.4;
        e /= 3d;
        e += 4.4e100d;
        e *= e;
        e -= 2d;
      }
      sw.stop();
      // System.out.println(sw.elapsedSeconds());
    } while (sw.elapsedMilliseconds() < MINTIME);

    // just here to make sure that not all computations on the local variables
    // are deleted by a compiler detecting that the result computed will not be
    // used later on
    fakeResult = e;

    return Double.valueOf(4d * c * OPSDONE / sw.elapsedSeconds() * 1.0e-6)
        .longValue();
  }

  /**
   * Benchmark arithmetic operations based on data type long.
   * 
   * @return the number of operations (MLops)
   */
  private long benchLong() {
    // the counter used to find out how many loop passes had to be executed
    // before the minTime had been exceeded
    int c = 0;

    // stop watch used to find out the number of milliseconds elapsed
    StopWatch sw = new StopWatch();
    long l = 0;

    do {

      c++;
      sw.start();
      for (int i = 0; i < OPS; i++) {
        l /= 3l;
        l += 100987988798789l;
        l *= l;
        l -= 2l;
      }
      sw.stop();
    } while (sw.elapsedMilliseconds() < MINTIME);

    // just here to make sure that not all computations on the local variables
    // are deleted by a compiler detecting that the result computed will not be
    // used later on
    fakeResult = Double.valueOf(l);

    return Double.valueOf(4d * c * OPSDONE / sw.elapsedSeconds() * 1.0e-6)
        .longValue();

  }

  /**
   * Benchmark arithmetic operations based on data type int.
   * 
   * @return the number of operations (MIops)
   */
  private long benchInt() {
    // the counter used to find out how many loop passes had to be executed
    // before the minTime had been exceeded
    int c = 0;

    // stop watch used to find out the number of milliseconds elapsed
    StopWatch sw = new StopWatch();

    int il = 0;

    do {
      c++;

      sw.start();
      for (int i = 0; i < OPS; i++) {
        il /= 3;
        il += 10098;
        il *= il;
        il -= 2;
      }
      sw.stop();
    } while (sw.elapsedMilliseconds() < MINTIME);

    // just here to make sure that not all computations on the local variables
    // are deleted by a compiler detecting that the result computed will not be
    // used later on
    fakeResult = Double.valueOf(il);

    return Double.valueOf(4d * c * OPSDONE / sw.elapsedSeconds() * 1.0e-6)
        .longValue();

  }

  @Override
  public IBenchmarkingResult run() {

    long[] res = new long[4];

    res[0] = benchDouble();

    res[1] = benchLong();

    res[2] = benchInt();

    // compute the arithmetic mean
    long l = 0;
    for (int i = 0; i < res.length - 1; i++) {
      l += res[i];
    }
    Double r = Double.valueOf(l / (res.length - 1.));

    res[3] = r.longValue();

    return new SimpleBenchmarkingResult(res);
  }

  /**
   * The main method. Just execute the benchmark standalone.
   * 
   * @param args
   *          the arguments
   */
  public static void main(String args[]) {

    System.out
        .println("Computing the simple benchmark (double, long, and integer arithmetic operations) ");
    System.out.println("...");
    System.out.println("");

    SimpleBenchmark b = new SimpleBenchmark();

    SimpleBenchmarkingResult res = (SimpleBenchmarkingResult) b.run();

    System.out.println("MFlops                : " + res.getMFlops());
    System.out.println("MOps (long)           : " + res.getLops());
    System.out.println("MOps (int)            : " + res.getIops());
    System.out.println("Mean Mops / per second: " + res.getMetric());

  }

  /**
   * Needed to make sure that the compiler does not remove the variable.
   * 
   * @return
   */
  public double getFakeResult() {
    return fakeResult;
  }

}
