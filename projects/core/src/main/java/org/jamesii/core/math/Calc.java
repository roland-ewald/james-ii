/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.model.variables.IQuantitativeVariable;

/**
 * Set of auxiliary math functions.
 * 
 * @author Susanne Biermann
 * @author Roland Ewald
 */
public final class Calc {

  /**
   * Square the number if used as exponent.
   */
  public static final int SQUARE = 2;

  /**
   * Cubic the number if used as exponent.
   */
  public static final int CUBIC = 3;

  /**
   * Hidden constructor.
   */
  private Calc() {
  }

  /**
   * Adds the.
   * 
   * @param <V1>
   * @param <V2>
   * 
   * @param v1
   *          the v1
   * @param v2
   *          the v2
   */
  @SuppressWarnings("unchecked")
  public static <V1 extends Number & Comparable<V1>, V2 extends Number & Comparable<V2>> void add(
      IQuantitativeVariable<V1> v1, IQuantitativeVariable<V2> v2) {
    v1.setValue((V1) add(v1.getValue(), v2.getValue()));
  }

  /**
   * Adds up two numbers of unknown type.
   * 
   * @param <N>
   * 
   * @param n1
   *          1st summand
   * @param n2
   *          2nd summand
   * 
   * @return number of the first number's type, null if it's not Double, Float,
   *         Integer, Long, Byte, BigDecimal, BigInteger or Short
   */
  @SuppressWarnings("unchecked")
  public static <N extends Number> N add(N n1, N n2) {

    if (n1 instanceof BigDecimal) {
      return (N) ((BigDecimal) n1).add(new BigDecimal(n2.toString()));
    }
    if (n1 instanceof BigInteger) {
      return (N) ((BigInteger) n1).add(new BigInteger(n2.toString()));
    }
    double n1Val = n1.doubleValue();
    double n2Val = n2.doubleValue();
    Double result = Double.valueOf(n1Val + n2Val);
    return returnResult(n1, result);
  }

  /**
   * Divide.
   * 
   * @param <V1>
   * @param <V2>
   * 
   * @param v1
   *          the v1
   * @param v2
   *          the v2
   */
  @SuppressWarnings("unchecked")
  public static <V1 extends Number & Comparable<V1>, V2 extends Number & Comparable<V2>> void divide(
      IQuantitativeVariable<V1> v1, IQuantitativeVariable<V2> v2) {
    v1.setValue((V1) divide(v1.getValue(), v2.getValue()));
  }

  /**
   * Multiplies two numbers of unknown type.
   * 
   * @param <N>
   * 
   * @param n1
   *          dividend
   * @param n2
   *          divisor
   * 
   * @return number of the first number's type, null if it's not Double, Float,
   *         Integer, Long, Byte, BigDecimal, BigInteger or Short
   */
  @SuppressWarnings("unchecked")
  public static <N extends Number> N divide(N n1, N n2) {

    if (n1 instanceof BigDecimal) {
      return (N) ((BigDecimal) n1).divide(new BigDecimal(n2.toString()));
    }
    if (n1 instanceof BigInteger) {
      return (N) ((BigInteger) n1).divide(new BigInteger(n2.toString()));
    }
    double n1Val = n1.doubleValue();
    double n2Val = n2.doubleValue();
    Double result = Double.valueOf(n1Val / n2Val);
    return returnResult(n1, result);
  }

  /**
   * Get base10-exponent of number (mapping between 1 and 10). E.g, 0.004 has to
   * be multiplied with 10^3 to be normalized as, so this function returns 3.
   * 
   * @param number
   *          the number
   * 
   * @return exponent of number
   */
  public static Integer getExponentBase10(double number) {
    double n = number;
    if (Double.compare(n, 0) < 0) {
      n = n * -1;
    }
    if (Double.compare(n, 1) < 0) {
      return getNegativeExponent(n);
    }
    return getPositiveExponent(n);
  }

  /**
   * Auxiliary function.
   * 
   * @param number
   *          the number
   * 
   * @return the negative exponent
   */
  public static Integer getNegativeExponent(double number) {
    int exponent = 0;
    double n = number;
    if (Double.valueOf(number).compareTo(0.) == 0) {
      return exponent; // get out if the number is 0, otherwise we've got an
    }
    // infinite loop
    n *= Math.signum(n);
    while (Double.compare(n, 1) < 0) {
      n *= 10;
      exponent--;
    }
    return exponent;
  }

  /**
   * Auxiliary function.
   * 
   * @param number
   *          the number
   * 
   * @return the positive exponent
   */
  protected static Integer getPositiveExponent(double number) {
    if (number == Double.POSITIVE_INFINITY) {
      return Integer.MAX_VALUE; // TODO (hmmmm ....)
    }
    int exponent = 0;
    double n = number;
    while (Double.compare(n, 10) >= 0) {
      n /= 10;
      exponent++;
    }
    return exponent;
  }

  /**
   * Linear interpolation.
   * 
   * @param <V1>
   * @param <V2>
   * @param <V3>
   * 
   * @param v1
   *          the v1
   * @param v2
   *          the v2
   * @param v3
   *          the v3
   * @param factor
   *          the factor
   */
  @SuppressWarnings("unchecked")
  public static <V1 extends Number & Comparable<V1>, V2 extends Number & Comparable<V2>, V3 extends Number & Comparable<V3>> void linearInterpolation(
      IQuantitativeVariable<V1> v1, IQuantitativeVariable<V2> v2,
      IQuantitativeVariable<V2> v3, Double factor) {
    v1.setValue((V1) linearInterpolation(v2.getValue(), v3.getValue(), factor));
  }

  /**
   * Returns factor * (1st summand) + (1 - factor) * (2nd summand).
   * 
   * @param <N>
   * 
   * @param n1
   *          1st summand
   * @param n2
   *          2nd summand
   * @param factor
   *          the factor
   * 
   * @return number of the first number's type, null if it's not Double, Float,
   *         Integer, Long, Byte, BigDecimal, BigInteger or Short
   */
  @SuppressWarnings("unchecked")
  public static <N extends Number> N linearInterpolation(N n1, N n2,
      double factor) {
    if (n1 instanceof BigDecimal) {
      return (N) ((BigDecimal) n1).multiply(new BigDecimal(factor)).add(
          new BigDecimal(n2.toString()).multiply(new BigDecimal(factor)));
    }
    if (n1 instanceof BigInteger) {
      return (N) (new BigDecimal((BigInteger) n1))
          .multiply(new BigDecimal(factor))
          .add(new BigDecimal(n2.toString()).multiply(new BigDecimal(factor)))
          .toBigInteger();
    }
    double n1Val = n1.doubleValue();
    double n2Val = n2.doubleValue();
    Double result = Double.valueOf(factor * n1Val + (1 - factor) * n2Val);
    return returnResult(n1, result);
  }

  /**
   * Multiply.
   * 
   * @param <V1>
   * @param <V2>
   * 
   * @param v1
   *          the v1
   * @param v2
   *          the v2
   */
  @SuppressWarnings("unchecked")
  public static <V1 extends Number & Comparable<V1>, V2 extends Number & Comparable<V2>> void multiply(
      IQuantitativeVariable<V1> v1, IQuantitativeVariable<V2> v2) {
    v1.setValue((V1) multiply(v1.getValue(), v2.getValue()));
  }

  /**
   * Multiplies two numbers of unknown type.
   * 
   * @param <N>
   * 
   * @param n1
   *          1st multiplicand
   * @param n2
   *          2nd multiplicand
   * 
   * @return number of the first number's type, null if it's not Double, Float,
   *         Integer, Long, Byte, BigDecimal, BigInteger or Short
   */
  @SuppressWarnings("unchecked")
  public static <N extends Number> N multiply(N n1, N n2) {
    if (n1 instanceof BigDecimal) {
      return (N) ((BigDecimal) n1).multiply(new BigDecimal(n2.toString()));
    }
    if (n1 instanceof BigInteger) {
      return (N) ((BigInteger) n1).multiply(new BigInteger(n2.toString()));
    }
    double n1Val = n1.doubleValue();
    double n2Val = n2.doubleValue();
    Double result = Double.valueOf(n1Val * n2Val);
    return returnResult(n1, result);
  }

  /**
   * Parses a string and creates a number object similar to the type of number
   * (which has no other role to play except for providing the ).
   * 
   * @param <N>
   * 
   * @param number
   *          the number
   * @param value
   *          the value
   * 
   * @return the N
   */
  @SuppressWarnings("unchecked")
  public static <N extends Number> N parseNumber(N number, String value) {
    if (number instanceof Byte) {
      return (N) Byte.valueOf(Byte.parseByte(value));
    }
    if (number instanceof Short) {
      return (N) Short.valueOf(Short.parseShort(value));
    }
    if (number instanceof Integer) {
      return (N) Integer.valueOf(Integer.parseInt(value));
    }
    if (number instanceof Float) {
      return (N) Float.valueOf(Float.parseFloat(value));
    }
    if (number instanceof Double) {
      return (N) Double.valueOf(Double.parseDouble(value));
    }
    if (number instanceof Long) {
      return (N) Long.valueOf(Long.parseLong(value));
    }
    return null;
  }

  // Wrapper for IQuantitativeVariable<V>

  /**
   * Get 2 to the power of x.
   * 
   * @param x
   *          the exponent
   * 
   * @return 2^x
   */
  public static int pow2(int x) {
    return (1 << x);
  }

  /**
   * Calculates the faculty.
   * 
   * @param num
   *          the number of which the faculty shall be calculated
   * 
   * @return num!
   */
  public static long faculty(int num) {
    if (num < 0) {
      throw new ArithmeticException(
          "A faculty cannot be computed for a negative value (" + num + ")");
    }
    long result = Math.max(num, 1);
    for (int prod = num - 1; prod > 1; prod--) {
      result *= prod;
    }
    return result;
  }

  /**
   * Return result.
   * 
   * @param <N>
   * 
   * @param number
   *          the number
   * @param result
   *          the result
   * 
   * @return the N
   */
  @SuppressWarnings("unchecked")
  public static <N extends Number> N returnResult(N number, Double result) {
    if (number instanceof Double) {
      return (N) result;
    }
    if (number instanceof Float) {
      return (N) Float.valueOf(result.floatValue());
    }
    if (number instanceof Integer) {
      return (N) Integer.valueOf(result.intValue());
    }
    if (number instanceof Short) {
      return (N) Short.valueOf(result.shortValue());
    }
    if (number instanceof Long) {
      return (N) Long.valueOf(result.longValue());
    }
    if (number instanceof Byte) {
      return (N) Byte.valueOf(result.byteValue());
    }
    return null;
  }

  /**
   * Rounds a double value to numOfPos digits after the comma.
   * 
   * @param x
   *          the x
   * @param numOfPos
   *          the num of pos
   * 
   * @return the double
   */
  public static double round(double x, int numOfPos) {
    int z = (int) Math.pow(10, numOfPos);
    return (Math.floor(x * z) / z);
  }

  /**
   * Subtract.
   * 
   * @param <V1>
   * @param <V2>
   * 
   * @param v1
   *          the v1
   * @param v2
   *          the v2
   */
  @SuppressWarnings("unchecked")
  public static <V1 extends Number & Comparable<V1>, V2 extends Number & Comparable<V2>> void subtract(
      IQuantitativeVariable<V1> v1, IQuantitativeVariable<V2> v2) {
    v1.setValue((V1) subtract(v1.getValue(), v2.getValue()));
  }

  /**
   * Subtracts two numbers of unknown type.
   * 
   * @param <N>
   * 
   * @param n1
   *          minuend
   * @param n2
   *          subtrahend
   * 
   * @return number of the first number's type, null if it's not Double, Float,
   *         Integer, Long, Byte, BigDecimal, BigInteger or Short
   */
  @SuppressWarnings("unchecked")
  public static <N extends Number> N subtract(N n1, N n2) {

    if (n1 instanceof BigDecimal) {
      return (N) ((BigDecimal) n1).subtract(new BigDecimal(n2.toString()));
    }
    if (n1 instanceof BigInteger) {
      return (N) ((BigInteger) n1).subtract(new BigInteger(n2.toString()));
    }
    double n1Val = n1.doubleValue();
    double n2Val = n2.doubleValue();
    Double result = Double.valueOf(n1Val - n2Val);
    return returnResult(n1, result);
  }

  /**
   * Generates sequence of integers.
   * 
   * @param start
   *          the start value
   * @param stop
   *          the stop value (exclusive)
   * @param step
   *          the step size
   * @return the list containing the sequence
   */
  public static List<Integer> sequence(int start, int stop, int step) {
    boolean decreasing = stop < start && step < 0;
    List<Integer> sequence = new ArrayList<>();
    for (int i = start; (i < stop && !decreasing) || (i > stop && decreasing); i +=
        step) {
      sequence.add(i);
    }
    return sequence;
  }

  /**
   * Generates sequence of integers.
   * 
   * @param start
   *          the start values
   * @param stop
   *          the stop value (exclusive)
   * @return the list containing the sequence
   */
  public static List<Integer> sequence(int start, int stop) {
    return sequence(start, stop, 1);
  }

}
