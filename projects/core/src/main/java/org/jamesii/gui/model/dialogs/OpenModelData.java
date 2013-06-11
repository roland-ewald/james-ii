/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.dialogs;

import java.io.Serializable;

import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Data to re-open a recently opened model.
 * 
 * @author Roland Ewald
 */
public class OpenModelData implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = -8974419131823673672L;

  /** Factory to be used to create the model reader. */
  private ModelReaderFactory factory;

  /** Model name. */
  private String modelName;

  /** Model reader/writer parameters. */
  private ParameterBlock parameters;

  /** Custom model reader/writer parameters. */
  private ParameterBlock parameterBlock;

  /**
   * Constructor for bean compatibility.
   */
  public OpenModelData() {
  }

  /**
   * Default constructor.
   * 
   * @param mName
   *          model name
   * @param fac
   *          model reader/writer factory to be used
   * @param params
   *          model reader/writer parameters
   */
  public OpenModelData(String mName, ModelReaderFactory fac,
      ParameterBlock params) {
    modelName = mName;
    factory = fac;
    parameters = params;
    // Don't save the value of the parameters, it contains a ref to the model
    parameters.setValue(null);
  }

  @Override
  public boolean equals(Object obj) {

    if (!(obj instanceof OpenModelData)) {
      return false;
    }

    if (parameters.getSubBlockValue("URI").equals(
        ((OpenModelData) obj).getParameters().getSubBlockValue("URI"))) {
      return true;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return parameters.getSubBlockValue("URI").hashCode();
  }

  /**
   * Gets the factory.
   * 
   * @return the factory
   */
  public ModelReaderFactory getFactory() {
    return factory;
  }

  /**
   * Gets the model name.
   * 
   * @return the model name
   */
  public String getModelName() {
    return modelName;
  }

  /**
   * Gets the parameters.
   * 
   * @return the parameters
   */
  public ParameterBlock getParameters() {
    return parameters;
  }

  /**
   * Sets the factory.
   * 
   * @param factory
   *          the new factory
   */
  public void setFactory(ModelReaderFactory factory) {
    this.factory = factory;
  }

  /**
   * Sets the model name.
   * 
   * @param modelName
   *          the new model name
   */
  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  /**
   * Sets the parameters.
   * 
   * @param parameters
   *          the new parameters
   */
  public void setParameters(ParameterBlock parameters) {
    this.parameters = parameters;
  }

  /**
   * Gets the parameter block.
   * 
   * @return the parameter block
   */
  public ParameterBlock getParameterBlock() {
    return parameterBlock;
  }

  /**
   * Sets the parameter block.
   * 
   * @param parameterBlock
   *          the new parameter block
   */
  public void setParameterBlock(ParameterBlock parameterBlock) {
    this.parameterBlock = parameterBlock;
  }

}
