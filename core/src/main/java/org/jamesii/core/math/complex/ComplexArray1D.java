/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.complex;

/**
 * This class represents a one-dimensional array of complex numbers. It stores
 * all complex numbers as {@code double} values inside a single double array
 * whose dimensions are two times as large as the desired {@code Complex} array.
 * Since all operations are done on atomic values this class has no runtime
 * penalty of creating and destroying objects (which would be done frequently if
 * using {@code Complex[]} which is immutable) and is thus preferred if many
 * calculations are done on arrays of complex values.
 * <p>
 * This class was inspired by the paper
 * <em><a href="http://portal.acm.org/citation.cfm?coll=GUIDE&dl=GUIDE&id=304109">Efficient
 * support for Complex Numbers in Java</a></em> by Peng Wu, Sam Midkiff, José
 * Moreira and Manish Gupta.
 * <p>
 * For simplicity reasons only the elementary arithmetic operations are
 * implemented, along with methods for accessing individual numbers or parts of
 * them.
 * <p>
 * All calculations are done with real and imaginary parts, as the numbers are
 * stored that way.
 * <p>
 * <strong>Question:</strong> Would it make sense to implement the Collection or
 * List interface? A problem I would have here, is that those interfaces have an
 * add() method which has substantially different semantics than the
 * arithmetical add which would have to be provided by this class.
 * 
 * @author Johannes Rössel
 */
public class ComplexArray1D {

  /**
   * The complex numbers of this array. This array is actually double the size
   * of the {@code Complex} array and for each complex number there is first the
   * real part and then the imaginary part. So every odd array element is a real
   * part and every even element is an imaginary part of a complex number.
   */
  private double[] data = null;

  /**
   * Creates a new {@code ComplexArray1D} from a {@code Complex[]}.
   * 
   * @param values
   *          The {@code Complex} array that should be represented as
   *          {@code ComplexArray1D}.
   */
  public ComplexArray1D(Complex[] values) {
    this(values.length);
    for (int i = 0; i < values.length; i++) {
      data[2 * i] = values[i].getReal();
      data[2 * i + 1] = values[i].getImaginary();
    }
  }

  /** Creates an empty array of complex numbers */
  public ComplexArray1D() {
    this(0);
  }

  /**
   * Creates a new {@code ComplexArray1D} of a given length. All numbers are
   * initialized to zero.
   * 
   * @param length
   *          The length of the new {@code ComplexArray1D}.
   */
  public ComplexArray1D(int length) {
    if (length < 0) {
      throw new IllegalArgumentException("Array length must be non-negative.");
    }
    data = new double[2 * length];
  }

  /**
   * Returns the length of this array.
   * 
   * @return The array length.
   */
  public int getLength() {
    return data.length / 2;
  }

  /**
   * Returns the real part of the <i>index</i>th number in the array.
   * 
   * @param index
   *          The index of the wanted complex number in the array.
   * @return The real part of it.
   */
  private double getRe(int index) {
    return data[2 * index];
  }

  /**
   * Returns the imaginary part of the <i>index</i>th number in the array.
   * 
   * @param index
   *          The index of the wanted complex number in the array.
   * @return The imaginary part of it.
   */
  private double getIm(int index) {
    return data[2 * index + 1];
  }

  /**
   * Resizes the array. Old values will be kept as far as they fit in the new
   * array. If the new length is greater than before the appended values will be
   * zero.
   * 
   * @param length
   *          The new length of the array.
   */
  public void setLength(int length) {
    // trivial cases
    if (length == getLength()) {
      return;
    }
    if (length < 0) {
      throw new IllegalArgumentException("Array length must be non-negative.");
    }

    double[] newdata = new double[2 * length];

    // copy old values, if possible
    for (int i = 0; i < Math.min(data.length, newdata.length); i++) {
      newdata[i] = data[i];
    }

    data = newdata;
  }

  /**
   * Puts a complex number at a specified index using a complex number as
   * parameter.
   * 
   * @param index
   *          The index where the number should be inserted. Note that the
   *          number at that position will be overwritten.
   * @param c
   *          The complex number to insert.
   */
  public void putComplex(int index, Complex c) {
    putComplex(index, c.getReal(), c.getImaginary());
  }

  /**
   * Puts a complex number at a specified index using real and imaginary parts
   * as parameters.
   * 
   * @param index
   *          The index where the number should be inserted. Note that the
   *          number at that position will be overwritten.
   * @param re
   *          The real part of the number.
   * @param im
   *          The imaginary part of the number.
   */
  public void putComplex(int index, double re, double im) {
    if (index < 0 || index >= getLength()) {
      throw new IndexOutOfBoundsException();
    }

    data[2 * index] = re;
    data[2 * index + 1] = im;
  }

  /**
   * Retrieves a complex number at a given index.
   * 
   * @param index
   *          The index of the number.
   * @return The complex number at the given index.
   */
  public Complex getComplex(int index) {
    if (index < 0 || index >= getLength()) {
      throw new IndexOutOfBoundsException();
    }

    return new Complex(getRe(index), getIm(index));
  }

  /**
   * Negates a specific number from the array, given by its index.
   * 
   * @param index
   *          The index of the number to negate.
   */
  public void negate(int index) {
    data[2 * index] *= -1;
    data[2 * index + 1] *= -1;
  }

  /**
   * Conjugates a specific number from the array, given by its index.
   * 
   * @param index
   *          The index of the number to conjugate.
   */
  public void conjugate(int index) {
    data[2 * index + 1] *= -1;
  }

  /**
   * Adds two numbers from the array, writing the result back into the array at
   * a specified position.
   * 
   * @param result
   *          The index of the array to write the result to.
   * @param op1
   *          The first number from the array.
   * @param op2
   *          The second number from the array.
   */
  public void add(int result, int op1, int op2) {
    add(result, getRe(op1), getIm(op1), getRe(op2), getIm(op2));
  }

  /**
   * Adds a number from the array and another complex number in decomposed form,
   * writing the result back into the array at a specified position.
   * 
   * @param result
   *          The index of the array to write the result to.
   * @param op1
   *          The first number from the array.
   * @param re
   *          The real part of the second number.
   * @param im
   *          The imaginary part of the second number.
   */
  public void add(int result, int op1, double re, double im) {
    add(result, getRe(op1), getIm(op1), re, im);
  }

  /**
   * Adds two complex numbers, writing the result back into the array at a
   * specified position.
   * 
   * @param result
   *          The index of the array to write the result to.
   * @param re1
   *          The real part of the first number to add.
   * @param im1
   *          The imaginary part of the first number to add.
   * @param re2
   *          The real part of the second number to add.
   * @param im2
   *          The imaginary part of the second number to add.
   */
  public void add(int result, double re1, double im1, double re2, double im2) {
    if (result < 0 || result >= getLength()) {
      throw new IndexOutOfBoundsException();
    }

    data[2 * result] = re1 + re2;
    data[2 * result + 1] = im1 + im2;
  }

  /**
   * Subtracts two numbers, taken from the array.
   * 
   * @param result
   *          The index to write the result to.
   * @param op1
   *          The index of the minuend.
   * @param op2
   *          The index of the subtrahend.
   */
  public void subtract(int result, int op1, int op2) {
    subtract(result, getRe(op1), getIm(op1), getRe(op2), getIm(op2));
  }

  /**
   * Subtracts a complex number from one taken from the array.
   * 
   * @param result
   *          The index to write the result to.
   * @param op1
   *          The number to subtract from.
   * @param re
   *          The real part of the number to subtract.
   * @param im
   *          The imaginary part of the number to subtract.
   */
  public void subtract(int result, int op1, double re, double im) {
    subtract(result, getRe(op1), getIm(op1), re, im);
  }

  /**
   * Subtracts a complex number, taken from the array, from another.
   * 
   * @param result
   *          The index to write the result to.
   * @param re
   *          The real part of the number to subtract from.
   * @param im
   *          The imaginary part of the number to subtract from.
   * @param op2
   *          The number to subtract.
   */
  public void subtract(int result, double re, double im, int op2) {
    subtract(result, re, im, getRe(op2), getIm(op2));
  }

  /**
   * Subtracts two complex numbers and writes the result to the array.
   * 
   * @param result
   *          The index to write the result to.
   * @param re1
   *          The real part of the number to subtract from.
   * @param im1
   *          The imaginary part of the number to subtract from.
   * @param re2
   *          The real part of the number to subtract.
   * @param im2
   *          The imaginary part of the number to subtract.
   */
  public void subtract(int result, double re1, double im1, double re2,
      double im2) {
    data[2 * result] = re1 - re2;
    data[2 * result + 1] = im1 - im2;
  }

  /**
   * Multiplies two numbers from the array, writing the result back in another
   * position.
   * 
   * @param result
   *          The index to write the result to.
   * @param op1
   *          The index of the first factor.
   * @param op2
   *          The index of the second factor.
   */
  public void multiply(int result, int op1, int op2) {
    multiply(result, getRe(op1), getIm(op1), getRe(op2), getIm(op2));
  }

  /**
   * Multiplies a number from the array with another complex number, writing the
   * result back into the array.
   * 
   * @param result
   *          The index to write the result to.
   * @param op1
   *          The index of the first factor.
   * @param re
   *          The real part of the second factor.
   * @param im
   *          The imaginary part of the second factor.
   */
  public void multiply(int result, int op1, double re, double im) {
    multiply(result, getRe(op1), getIm(op1), re, im);
  }

  /**
   * Multiplies two complex numbers, writing the result into the array.
   * 
   * @param result
   *          The index to write the result to.
   * @param re1
   *          The real part of the first factor.
   * @param im1
   *          The imaginary part of the first factor.
   * @param re2
   *          The real part of the second factor.
   * @param im2
   *          The imaginary part of the second factor.
   */
  public void multiply(int result, double re1, double im1, double re2,
      double im2) {
    if (result < 0 || result >= getLength()) {
      throw new IndexOutOfBoundsException();
    }

    data[2 * result] = re1 * re2 - im1 * im2;
    data[2 * result + 1] = im1 * re2 + re1 * im2;
  }

  /**
   * Divides two numbers, taken from the array.
   * 
   * @param result
   *          The index to write the result to.
   * @param op1
   *          The index of the dividend.
   * @param op2
   *          The index of the divisor.
   */
  public void divide(int result, int op1, int op2) {
    divide(result, getRe(op1), getIm(op1), getRe(op2), getIm(op2));
  }

  /**
   * Divides a complex number from the array by a given one.
   * 
   * @param result
   *          The index to write the result to.
   * @param op1
   *          The number to divide.
   * @param re
   *          The real part of the number to divide by.
   * @param im
   *          The imaginary part of the number to divide by.
   */
  public void divide(int result, int op1, double re, double im) {
    divide(result, getRe(op1), getIm(op1), re, im);
  }

  /**
   * Divides a given complex number by another one from the array.
   * 
   * @param result
   *          The index to write the result to.
   * @param re
   *          The real part of the number to divide.
   * @param im
   *          The imaginary part of the number to divide.
   * @param op2
   *          The number to divide by.
   */
  public void divide(int result, double re, double im, int op2) {
    divide(result, re, im, getRe(op2), getIm(op2));
  }

  /**
   * Divides two complex numbers and writes the result to the array.
   * 
   * @param result
   *          The index to write the result to.
   * @param re1
   *          The real part of the number to subtract from.
   * @param im1
   *          The imaginary part of the number to subtract from.
   * @param re2
   *          The real part of the number to subtract.
   * @param im2
   *          The imaginary part of the number to subtract.
   */
  public void divide(int result, double re1, double im1, double re2, double im2) {
    if (result < 0 || result >= getLength()) {
      throw new IndexOutOfBoundsException();
    }

    double divisor = re2 * re2 + im2 * im2;

    data[2 * result] = (re1 * re2 + im1 * im2) / divisor;
    data[2 * result + 1] = (im1 * re2 - re1 * im2) / divisor;
  }

  /**
   * Retrieves the contents of this array as a {@code Complex[]}.
   * 
   * @return This {@code ComplexArray1D} as a {@code Complex[]}.
   */
  public Complex[] toComplexArray() {
    Complex[] x = new Complex[getLength()];

    for (int i = 0; i < getLength(); i++) {
      x[i] = getComplex(i);
    }

    return x;
  }

}
