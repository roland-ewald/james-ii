/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.distributed;

import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.model.ModelInformation;
import org.jamesii.core.processor.ProcessorInformation;

/**
 * This class maintains all neighbour information (regarding models and
 * processors) that needs to be handed around in remote processor factories.
 * 
 * @author Roland Ewald
 * 
 */
public class NeighbourInformation implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4181617979264940412L;

  /** The model information map. Key is the full model name. */
  private final Map<String, ModelInformation> modelInfos;

  /** The processor information map. Key is the full model name. */
  private final Map<String, ProcessorInformation> processorInfos;

  /**
   * Instantiates a new neighbour information instance.
   * 
   * @param modelInformations
   *          the model informations
   * @param processorInformations
   *          the processor informations
   */
  public NeighbourInformation(Map<String, ModelInformation> modelInformations,
      Map<String, ProcessorInformation> processorInformations) {
    modelInfos = modelInformations;
    processorInfos = processorInformations;
  }

  /**
   * Gets the model infos.
   * 
   * @return the modelInfos
   */
  public Map<String, ModelInformation> getModelInfos() {
    return modelInfos;
  }

  /**
   * Gets the processor infos.
   * 
   * @return the processorInfos
   */
  public Map<String, ProcessorInformation> getProcessorInfos() {
    return processorInfos;
  }

}
