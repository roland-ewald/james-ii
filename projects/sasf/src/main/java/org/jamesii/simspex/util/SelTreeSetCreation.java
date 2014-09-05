/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util;


import java.net.URI;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.distributed.partitioner.partitioning.AbstractExecutablePartition;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;

/**
 * Some auxiliary methods for the creation of selection tree sets. See
 * {@link SelectionTreeSet}.
 * 
 * 
 * TODO: This only allows to explore the simulation space below the processor
 * factory so far, not partitioning etc.
 * 
 * @author Roland Ewald
 * 
 */
public final class SelTreeSetCreation {

	/**
	 * Should not be instantiated.
	 */
	private SelTreeSetCreation() {
	}

	/**
	 * Creates a model based on the given reader parameters, the {@link URI}, and
	 * the model parameters.
	 * 
	 * @param rwSimCfgParams
	 *          parameter block to initialise the
	 *          {@link org.jamesii.core.data.model.IModelReader}
	 * @param modelLocation
	 *          the location of the model
	 * @param modelParameters
	 *          the parameters with which the model shall be initialised
	 * @return instantiated model
	 */
	public static IModel instantiateModel(ParameterBlock rwSimCfgParams,
	    URI modelLocation, Map<String, ?> modelParameters) {
		ParameterBlock rwParams = rwSimCfgParams == null ? new ParameterBlock()
		    : rwSimCfgParams.getCopy();
		rwParams.addSubBlock(IURIHandling.URI, new ParameterBlock(modelLocation));
		ModelReaderFactory modelReaderWriterFactory = SimSystem.getRegistry()
		    .getFactory(AbstractModelReaderFactory.class, rwParams);
		IModel model = modelReaderWriterFactory.create(rwParams, SimSystem.getRegistry().createContext()).read(
		    modelLocation, modelParameters);
		return model;
	}

	/**
	 * Creates selection tree set. Needs to create a model to do so, i.e.
	 * 
	 * @param rwSimCfgParams
	 *          parameter block to initialise the
	 *          {@link org.jamesii.core.data.model.IModelReader}
	 * @param modelLocation
	 *          the location of the model
	 * @param modelParameters
	 *          the parameters with which the model shall be initialised
	 * @return selection tree set
	 * @throws ClassNotFoundException
	 *           the class not found exception
	 */
	public static SelectionTreeSet createSelectionTreeSet(
	    ParameterBlock rwSimCfgParams, URI modelLocation,
	    Map<String, ?> modelParameters) throws ClassNotFoundException {
		ParameterBlock treeSetParams = new ParameterBlock(
		    new AbstractExecutablePartition(instantiateModel(rwSimCfgParams,
		        modelLocation, modelParameters), null, null),
		    AbstractProcessorFactory.PARTITION);
		return new SelectionTreeSet(AbstractProcessorFactory.class, treeSetParams);
	}

	/**
	 * Creates the selection tree set based on an experiment.
	 * 
	 * @param experiment
	 *          the experiment
	 * @return the selection tree set
	 * @throws ClassNotFoundException
	 *           the class not found exception
	 */
	public static SelectionTreeSet createSelectionTreeSet(
	    BaseExperiment experiment) throws ClassNotFoundException {
		return createSelectionTreeSet(experiment.getModelRWParameters(),
		    experiment.getModelLocation(), experiment.getFixedModelParameters());
	}
}
