/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.vectors;

import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Periodic2DVectorFactory;

public class PeriodicVectorTest extends AbstractVectorTest {

  @Override
  protected IVectorFactory createVectorFactory() {
    return new Periodic2DVectorFactory(-2., -3., 4., 5.);
  }
}
