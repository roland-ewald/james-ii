/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.distributed.processor;

import java.util.Map;

import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.simulation.distributed.AbstractREMOTEFactory;

/**
 * A factory for creating AbstractREMOTEProcessor objects.
 */
public class AbstractREMOTEProcessorFactory extends
    AbstractREMOTEFactory<RemoteProcessorFactory<?>, IProcessor> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -821746810261308755L;

  /**
   * Gets the parent processor info.
   * 
   * @param neighbours
   *          the neighbours
   * @param modelName
   *          the model name
   * 
   * @return the parent processor info
   */
  public static ProcessorInformation getParentProcessorInfo(String modelName,
      Map<String, ProcessorInformation> neighbours) {

    // System.out.println("Anzahl der Nachbarn: "+neighbours.size());

    if (modelName.isEmpty()) {
      return null;
    }

    String modelNamePrep = modelName;

    if (modelName.lastIndexOf('.') != -1) {
      modelNamePrep =
          modelNamePrep.substring(0, modelNamePrep.lastIndexOf('.'));
    }// else: top most model has no parent model!

    /*
     * System.out.println("Getting reference for "+name+"
     * "+neighbours.get(name)); for (String i: neighbours.keySet()) {
     * System.out.println(i+" "+name+" "+i.compareTo(name)); if
     * (i.compareTo(name) == 0) { System.out.println("return:
     * "+neighbours.get(i)); return neighbours.get(i); } } return null;
     */

    // System.out.print(neighbours.get(name).getLocal());
    return neighbours.get(modelNamePrep);

  }

  /**
   * Instantiates a new abstract remote processor factory.
   */
  public AbstractREMOTEProcessorFactory() {
    super();

    // add criterias!!!!

  }

}
