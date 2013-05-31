/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.representativeValue;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

/**
 * Compares lists of representative objective values.
 * 
 * @author Stefan Leye
 * @author Roland Ewald
 * 
 */
public interface IRepresentativeValuesComparator extends Serializable,
    Comparator<Map<String, Double>> {

}
