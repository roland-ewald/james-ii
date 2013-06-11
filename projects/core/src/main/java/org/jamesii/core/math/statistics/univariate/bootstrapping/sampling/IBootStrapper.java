/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate.bootstrapping.sampling;

import java.util.List;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.misc.Pair;

/**
 * Basic interface for classes that create bootstrap samples.
 * 
 * @author Stefan Leye
 */
public interface IBootStrapper {

  List<Pair<Double, Double>> bootStrap(List<? extends Number> list,
      int repetitions, IRandom random);
}
