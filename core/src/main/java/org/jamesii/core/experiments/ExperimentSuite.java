/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.Entity;
import org.jamesii.core.util.ICallBack;

/**
 * A suite of experiments. An experiment is defined as a set of simulation runs
 * with a model under a set of (varying) parameters. Sometimes it might be
 * useful to be able to setup a set of experiments. These experiments are
 * executed one after the other, without the need of further user interactions.
 * 
 * @param <E>
 *          the experiments need to be identified somehow. This class does not
 *          make any restrictions, e.g. E might be a URI which is used with the
 *          IExperimentReader for setting up an experiment.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 */
public class ExperimentSuite<E extends BaseExperiment> extends Entity {

  /** Serialisation ID. */
  private static final long serialVersionUID = -8477698303373561329L;

  /** List of experiments. */
  private List<E> experiments = new ArrayList<>();

  /**
   * Add an experiment to the internal list of experiments to be executed.
   * 
   * @param experiment
   *          the experiment to be added
   */
  public void addExperiment(E experiment) {
    experiments.add(experiment);
  }

  /**
   * Remove the given experiment from the internal list of experiments to be
   * executed.
   * 
   * @param experiment
   *          the experiment to be removed
   */
  public void removeExperiment(E experiment) {
    experiments.remove(experiment);
  }

  /**
   * Execute the suite of experiments.
   * 
   * @param setupCallback
   *          call-back to prepare experiment execution, may be null
   */
  public void run(ICallBack<E> setupCallback) {
    for (E e : experiments) {
      if (setupCallback != null) {
        setupCallback.process(e);
      }
      e.execute();
    }
  }

  /**
   * Gets the experiments.
   * 
   * @return the experiments
   */
  public List<E> getExperiments() {
    return experiments;
  }

  /**
   * Sets the experiments.
   * 
   * @param experiments
   *          the new experiments
   */
  public void setExperiments(List<E> experiments) {
    this.experiments = experiments;
  }
}
