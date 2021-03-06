/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.stoppolicy;

import org.jamesii.core.experiments.tasks.IComputationTask;

/**
 * The Class AbstractComputationTaskStopPolicy. Requires a reference to a
 * computation task. Via this reference descendant classes can access almost
 * everything related to a computation task - the model, the computation
 * algorithm, data sinks, ... The information which can be retrieved from these
 * can be used to determine the computation end.
 * 
 * @author Jan Himmelspach
 * @param <T>
 *          the IComputationTask or descendant interface / class this stop
 *          policy can work on.
 */
public abstract class AbstractComputationTaskStopPolicy<T extends IComputationTask>
    implements IComputationTaskStopPolicy<T> {

}
