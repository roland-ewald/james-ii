/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.dialogs;

import java.io.Serializable;

import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Comparators;

/**
 * Class to hold all information needed to open an experiment.
 * 
 * @author Roland Ewald
 */
public class OpenExperimentData implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6594465899780392826L;

  /** Name of the experiment. */
  private String experimentName = "";

  /** Parameters to read/write the experiment. */
  private ParameterBlock parameters;

  /** Information on the Experiment (reference from parameters). */
  private ExperimentInfo experimentInfo = null;

  /**
   * Constructor for beans compliance.
   */
  public OpenExperimentData() {
  }

  /**
   * Standard constructor.
   * 
   * @param expName
   *          the experiment's name
   * @param params
   *          the parameters to read/write the experiment
   */
  public OpenExperimentData(String expName, ParameterBlock params) {
    experimentName = expName;
    parameters = params;
    experimentInfo =
        params
            .getSubBlockValue(AbstractExperimentReaderFactory.EXPERIMENT_INFO);
  }

  @Override
  public int hashCode() {
    return getExperimentInfo().getIdent().hashCode();
  }

  @Override
  public boolean equals(Object o) {

    if (!(o instanceof OpenExperimentData)) {
      return false;
    }

    ExperimentInfo myInfo = experimentInfo;
    ExperimentInfo otherInfos = ((OpenExperimentData) o).getExperimentInfo();

    if (Comparators.equal(myInfo.getIdent(), otherInfos.getIdent())
        && Comparators.equal(myInfo.getDataBase(), otherInfos.getDataBase())) {
      return true;
    }

    return false;
  }

  /**
   * Gets the experiment name.
   * 
   * @return the experiment name
   */
  public String getExperimentName() {
    return experimentName;
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
   * Sets the experiment name.
   * 
   * @param experimentName
   *          the new experiment name
   */
  public void setExperimentName(String experimentName) {
    this.experimentName = experimentName;
  }

  /**
   * Sets the parameters.
   * 
   * @param parameters
   *          the new parameters
   */
  public void setParameters(ParameterBlock parameters) {
    this.parameters = parameters;
    experimentInfo =
        parameters
            .getSubBlockValue(AbstractExperimentReaderFactory.EXPERIMENT_INFO);
  }

  /**
   * Gets the experiment info.
   * 
   * @return the experiment info
   */
  public ExperimentInfo getExperimentInfo() {
    return experimentInfo;
  }

}