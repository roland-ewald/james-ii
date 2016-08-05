/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Helper class to generate snapshot times of given intervals without having to
 * generate a whole list in advance, and without the need to define a last
 * snapshot time.
 * 
 * @author Arne Bittig
 * @date 18.09.2012
 */
public class IncreasingDoubleIterable implements Iterable<Double>,
    java.io.Serializable {

  private static final long serialVersionUID = -2165788950691436044L;

  /** persistence delegate for XML-Bean serialization */
  static {
    SerialisationUtils.addDelegateForConstructor(
        IncreasingDoubleIterable.class,
        new IConstructorParameterProvider<IncreasingDoubleIterable>() {
          @SuppressWarnings("synthetic-access")
          @Override
          public Object[] getParameters(IncreasingDoubleIterable si) {
            if (si.start == null) {
              return new Object[] { si.step, si.exp, si.end };
            } else {
              return new Object[] { si.start, si.step, si.exp, si.end };
            }
          }
        });
  }

  private final Double start;

  private final double step;

  private final double exp;

  private final double end;

  /**
   * Steps with exponentially growing intervals (except for first pair; or
   * shrinking intervals for exp < 1, but this highly discouraged), , starting
   * at 0. Will return <code>0, step,
   * 2*step, 2*step+step*exp, 2*step+step*exp+step*exp+exp</code>,...
   * 
   * @param step
   *          Initial interval between returned times
   * @param exp
   *          Growth factor for each later interval
   * @param end
   *          End time (use {@link Double#POSITIVE_INFINITY} for no end)
   */
  public IncreasingDoubleIterable(double step, double exp, double end) {
    this.start = null;
    this.step = step;
    this.exp = exp;
    this.end = end;
  }

  /**
   * Steps with exponentially growing intervals (or shrinking for exp < 1, but
   * this highly discouraged), , starting at given start time. Will return
   * <code>0, step,
   * 2*step, 2*step+step*exp, 2*step+step*exp+step*exp+exp</code>,...
   * 
   * @param start
   *          Start time
   * @param step
   *          Initial interval between returned times
   * @param exp
   *          Growth factor for each later interval
   * @param end
   *          End time (use {@link Double#POSITIVE_INFINITY} for no end)
   */
  public IncreasingDoubleIterable(double start, double step, double exp,
      double end) {
    this.start = start;
    this.step = step;
    this.exp = exp;
    this.end = end;
  }

  /**
   * Constructor for iterable that returns values increasing in given steps. ...
   * 
   * @param intervalSpec
   *          Interval specification
   */
  public IncreasingDoubleIterable(final double... intervalSpec) {
    switch (intervalSpec.length) {
    case 4:
      this.start = intervalSpec[0];
      this.step = intervalSpec[1];
      this.exp = intervalSpec[2];
      this.end = intervalSpec[3];
      break;
    case 3:
      this.start = null;
      this.step = intervalSpec[0];
      this.exp = intervalSpec[1];
      this.end = intervalSpec[2];
      break;
    case 2:
      this.start = null;
      this.step = intervalSpec[0];
      this.exp = intervalSpec[1];
      this.end = Double.POSITIVE_INFINITY;
      break;
    case 1:
      this.start = null;
      this.step = intervalSpec[0];
      this.exp = 1;
      this.end = Double.POSITIVE_INFINITY;
      break;
    case 0:
      throw new IllegalArgumentException(
          "Too few input arguments. At least step size required.");
    default:
      throw new IllegalArgumentException("Too many input arguments");
    }
  }

  @Override
  public Iterator<Double> iterator() {
    if (start == null) {
      return new IncreasingDoubleIterator(step, exp, end);
    } else {
      return new IncreasingDoubleIterator(start, step, exp, end);
    }
  }

  private static class IncreasingDoubleIterator implements Iterator<Double> {

    private double current;

    private double nextStep;

    private boolean startAt0;

    private final double exp;

    private final double end;

    IncreasingDoubleIterator(double step, double exp, double end) {
      this.startAt0 = true;
      this.current = 0.;
      this.nextStep = step;
      this.exp = exp;
      this.end = end;
    }

    IncreasingDoubleIterator(double start, double step, double exp, double end) {
      this.startAt0 = false;
      this.current = start;
      this.nextStep = step;
      this.exp = exp;
      this.end = end;
    }

    @Override
    public boolean hasNext() {
      return current <= end;
    }

    @Override
    public Double next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      double retVal = current;
      current += nextStep;
      if (startAt0) {
        startAt0 = false;
      } else {
        nextStep *= exp;
      }
      return retVal;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}