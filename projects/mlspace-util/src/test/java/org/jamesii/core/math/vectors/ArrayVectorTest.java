/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.vectors;

import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;

/**
 * Apply tests from {@link AbstractVectorTest} to an array-based vector (see
 * {@link AVectorFactory}).
 *
 * @author Arne Bittig
 */
public class ArrayVectorTest extends AbstractVectorTest {

  @Override
  protected IVectorFactory createVectorFactory() {
    return new AVectorFactory();
  }

}
