/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model;

/**
 * Marker interface. Each model which can change its structure should implement
 * this interface.<br>
 * Models which change their structure / composition during runtime may need
 * additional logics on the simulation execution side
 * <ul>
 * <li>new entities can appear, existing can disappear</li>
 * <li>communication paths might be created or destroyed</li>
 * </ul>
 * All these might require updates for the observation, i.e., added entities
 * should be instrumented, and thus observed as well or for load-balancing stuff
 * in a distributed setup.
 * 
 * Created on 12.05.2004
 * 
 * @author Jan Himmelspach
 */
public interface IDynamicModel extends IModel {

}
