/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.distributed.model;

import java.util.Map;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.ModelInformation;
import org.jamesii.core.simulation.distributed.AbstractREMOTEFactory;

/**
 * This class returns the factory to be used for creating the objects which will
 * be used for remote communication (here of models). This class needs to be
 * modified if new remote schemes for remote model communication are added.
 * 
 * @author Jan Himmelspach
 * 
 */
public class AbstractREMOTEModelFactory extends
    AbstractREMOTEFactory<RemoteModelFactory<?>, IModel> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8553312688924727055L;

  /**
   * Gets the parent model info.
   * 
   * @param neighbours
   *          the neighbours
   * @param modelName
   *          the model name
   * 
   * @return the parent model info
   */
  public static ModelInformation getParentModelInfo(String modelName,
      Map<String, ModelInformation> neighbours) {

    if (modelName.isEmpty()) {
      return null;
    }

    // if we have a parent model we'll fetch this one, but not for the top most
    // model as it naturally has no parent model!
    if (modelName.lastIndexOf('.') != -1) {
      modelName = modelName.substring(0, modelName.lastIndexOf('.'));
    }

    return neighbours.get(modelName);
  }

  /**
   * Instantiates a new abstract remote model factory.
   */
  public AbstractREMOTEModelFactory() {
    super();

    // add criteria!!!!

  }

}
