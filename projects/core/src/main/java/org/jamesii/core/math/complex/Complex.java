/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.complex;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;

/**
 * This class represents a complex number. It has two parts, real and imaginary.
 * Polar coordinates are represented by modulus and argument.
 * <p>
 * This class is immutable and all arithmetic operations do not alter the object
 * but instead return a new object with the result to allow chaining of
 * operations. If this presents too large a performance impact it could be
 * changed, however all of Java's numeric classes are immutable so this was done
 * out of conformance.
 * <p>
 * This class inherits from {@link Number} although it does not strictly for the
 * purpose of it. {@link Number} essentially seems to be a class intended for a
 * very small subset of numbers which does not include complex or hypercomplex
 * numbers. Thus the methods {@link #doubleValue()}, {@link #floatValue()},
 * {@link #intValue()}, {@link #byteValue()}, {@link #shortValue()} and
 * {@link #longValue()} are defined and implemented but only return the possibly
 * truncated or rounded real part of the complex number. This is only of limited
 * value but done to allow subclassing {@link Number} as an indication that this
 * class represents numbers.
 * 
 * @author Johannes Rössel
 */
public final class Complex extends Number {

  /** Serial version ID. */
  private static final long serialVersionUID = -8826305336173936066L;

  /** Real part. */
  private double re;

  /** Imaginary part. */
  private double im;

  /** Modulus of the number in polar form. */
  private double r;

  /** Angle of the number in polar form. */
  private double phi;

  /**
   * Creates a new complex number 0 + 0i.
   */
  public Complex() {
    re = im = r = phi = 0;
  }

  /**
   * Creates a complex number from a real number, that is, the imaginary part
   * will be zero. This constructor is just out of convenience to allow easy
   * conversion of real numbers to complex ones.
   * 
   * @param real
   *          The real part of the number.
   */
  public Complex(double real) {
    re = real;
    r = real < 0 ? -real : real;
    im = 0;
    phi = real < 0 ? PI : 0;
  }

  /**
   * Creates a new complex number from a real and an imaginary part.
   * 
   * @param real
   *          The real part of the number.
   * @param imaginary
   *          The imaginary part of the number.
   */
  public Complex(double real, double imaginary) {
    re = real;
    im = imaginary;
    updateRPhi();
  }

  /**
   * Creates a new complex number from Cartesian coordinates, that is a real and
   * an imaginary part. This method is identical to the constructor
   * Complex(double, double). It's just provided for completeness.
   * 
   * @param real
   *          The real part.
   * @param imaginary
   *          The imaginary part.
   * 
   * @return A complex number.
   */
  public static Complex fromCartesian(double real, double imaginary) {
    return new Complex(real, imaginary);
  }

  /**
   * Creates a new complex number from polar coordinates, that is an absolute
   * value <i>r</i> and an angle <i>&phi;</i>.
   * 
   * @param r
   *          The absolute value of the number.
   * @param phi
   *          The angle.
   * 
   * @return A complex number.
   */
  public static Complex fromPolar(double r, double phi) {
    return new Complex(r * cos(phi), r * sin(phi));
  }

  /**
   * Returns the real part of the complex number.
   * 
   * @return Real part.
   */
  public double getReal() {
    return re;
  }

  /**
   * Returns the imaginary part of the complex number. *
   * 
   * @return Imaginary part.
   */
  public double getImaginary() {
    return im;
  }

  /**
   * Returns the modulus of the complex number, also called its absolute value
   * or norm which is defined by &radic;(Re<sup>2</sup> + Im<sup>2</sup>).
   * 
   * @return Absolute value.
   */
  public double getModulus() {
    return r;
  }

  /**
   * Returns the argument of the complex number, also called its phase.
   * Basically this is the angle between the vector in an Argand diagram and the
   * real axis.
   * 
   * @return Angle.
   */
  public double getArgument() {
    return phi;
  }

  /**
   * Returns this number's negation.
   * 
   * @return The negated number.
   */
  public Complex negate() {
    return new Complex(-re, -im);
  }

  /**
   * Returns this number's complex conjugate.
   * 
   * @return The complex conjugate of this number.
   */
  public Complex conjugate() {
    return new Complex(re, -im);
  }

  /**
   * Adds a complex number.
   * 
   * @param c
   *          The number to add.
   * 
   * @return The sum of both numbers.
   */
  public Complex add(Complex c) {
    return new Complex(re + c.getReal(), im + c.getImaginary());
  }

  /**
   * Subtracts a complex number.
   * 
   * @param c
   *          The number to subtract.
   * 
   * @return The difference between this number and c.
   */
  public Complex subtract(Complex c) {
    return new Complex(re - c.getReal(), im - c.getImaginary());
  }

  /**
   * Multiplies this number by a real number.
   * 
   * @param d
   *          The real number to multiply with.
   * 
   * @return The product of this number and d.
   */
  public Complex multiply(double d) {
    return Complex.fromPolar(r * d, phi);
  }

  /**
   * Multiplies this number by a complex number.
   * 
   * @param c
   *          The complex number to multiply with.
   * 
   * @return The product of this number and c.
   */
  public Complex multiply(Complex c) {
    return Complex.fromPolar(r * c.getModulus(), phi + c.getArgument());
  }

  /**
   * Divides this number by a real number.
   * 
   * @param d
   *          The real number to divide by.
   * 
   * @return The division of this number and d.
   */
  public Complex divide(double d) {
    return Complex.fromPolar(r / d, phi);
  }

  /**
   * Divides this number by a complex number.
   * 
   * @param c
   *          The complex number to divide by.
   * 
   * @return The division of this number and c.
   */
  public Complex divide(Complex c) {
    return Complex.fromPolar(r / c.getModulus(), phi - c.getArgument());
  }

  /**
   * Determines whether the number is a purely real number, that is, whether it
   * could be represented using a {@code double} alone.
   * 
   * @return {@code true} if this number has its imaginary part set to 0,
   *         {@code false} otherwise.
   */
  public boolean isReal() {
    return im == 0;
  }

  /**
   * Determines whether this number is purely imaginary.
   * 
   * @return {@code true} if this number has its real part set to 0,
   *         {@code false} otherwise.
   */
  public boolean isImaginary() {
    return re == 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!obj.getClass().equals(this.getClass())) {
      return false;
    }

    return (this.re == ((Complex) obj).getReal() && this.im == ((Complex) obj)
        .getImaginary())
        || (this.r == ((Complex) obj).getModulus() && this.phi == ((Complex) obj)
            .getArgument());
  }

  @Override
  public int hashCode() {
    return 4 * Double.valueOf(this.re).hashCode() + 3
        * Double.valueOf(this.im).hashCode() + 2
        * Double.valueOf(this.r).hashCode()
        + Double.valueOf(this.phi).hashCode();
  }

  @Override
  public String toString() {
    if (re == 0) {
      if (im == 0) {
        return "0";
      }
      return im + "i";
    }

    if (im > 0) {
      return re + " + " + im + "i";
    } else if (im < 0) {
      return re + " - " + abs(im) + "i";
    } else {
      return Double.toString(re);
    }

  }

  /**
   * Updates <i>r</i> and <i>&phi;</i>, necessary when the real or imaginary
   * part changed.
   */
  private void updateRPhi() {
    r = hypot(re, im);
    phi = atan2(im, re);
  }

  @Override
  public double doubleValue() {
    return re;
  }

  @Override
  public float floatValue() {
    return Double.valueOf(re).floatValue();
  }

  @Override
  public int intValue() {
    return Double.valueOf(re).intValue();
  }

  @Override
  public long longValue() {
    return Double.valueOf(re).longValue();
  }

  @Override
  public byte byteValue() {
    return Double.valueOf(re).byteValue();
  }

  @Override
  public short shortValue() {
    return Double.valueOf(re).shortValue();
  }

}
