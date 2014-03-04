/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.simulator;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory for a simple test simulator.
 * 
 * @author Roland Ewald
 * 
 */
public class BogusSimulatorFactoryC extends FlexibleBogusSimulatorFactory {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 33221513678311884L;

	@Override
	protected IBogusSimulatorProperties determineProperties(
			ParameterBlock params) {
		return new ParameterBasedProperties(this.getClass(), params);
	}
}
