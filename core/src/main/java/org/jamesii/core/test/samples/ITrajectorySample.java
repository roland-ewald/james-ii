/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.samples;

import java.util.Map;

/**
 * Interface for trajectory samples. Each variable is associated with an
 * (ascending) list of (simulation time, value) pairs. Apart from that, the
 * sample delivers information on start and stop time of the sampling (in
 * simulation time).
 * 
 * @author Roland Ewald
 * 
 * @param <V>
 *          the type of the sampled number
 */
public interface ITrajectorySample<V extends Number> extends
    ITimeBoundSample<Map<Double, V>> {

}
