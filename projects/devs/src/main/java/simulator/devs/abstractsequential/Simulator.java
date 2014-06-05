/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.abstractsequential;

import org.jamesii.core.model.AbstractState;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;

import model.devs.IAtomicModel;
import simulator.devscore.BasicDEVSProcessor;
import simulator.devscore.DEVSProcessorState;

/**
 * The Class Simulator. A "simulator" in the world of DEVS is a simulation
 * algorithm which computes the trajectory of an atomic DEVS model.
 * 
 * @author Jan Himmelspach
 * @author Alexander Steiniger
 */
public class Simulator extends BasicDEVSProcessor implements
    IAbstractSequentialProcessor {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 1897148507092824833L;

  /** flag used internally for indicating whether the model is imminent or not. */
  private boolean isImminent = false;

  /**
   * The Constructor.
   * 
   * @param model
   *          the model
   */
  public Simulator(IAtomicModel<? extends AbstractState> model) {
    super(model);
  }

  /**
   * Compute outputs.
   */
  public void computeOutputs() {
    getAModel().lambdaSim();
    isImminent = true;
  }

  @Override
  public void doEvent() {
    throw new OperationNotSupportedException("Template method not used!!!");
  }

  @Override
  public double doRemainder(Double time) {

    // process messages

    // call state transition
    if (isImminent) {
      getAModel().deltaInternalSim();
    } else {
      getAModel().deltaExternalSim(
          time - ((DEVSProcessorState) getState()).getTole());
    }

    // compute and return the new tonie
    double tonie = time + getAModel().timeAdvanceSim();

    ((DEVSProcessorState) getState()).setTole(time);

    setTime(tonie);

    isImminent = false;

    return tonie;
  }

  /**
   * Gets the a model.
   * 
   * @return the a model
   */
  @SuppressWarnings("unchecked")
  public IAtomicModel<? extends AbstractState> getAModel() {
    return (IAtomicModel<? extends AbstractState>) getModel();
  }

  @Override
  public void getOutputs() {
    // execute lambda
    computeOutputs();
  }

  @Override
  public void init(double time) {
    ((DEVSProcessorState) getState()).setTole(time);
    setTime(getAModel().timeAdvanceSim());
  }

  @Override
  public void postEvent() {
    throw new OperationNotSupportedException("Template method not used!!!");
  }

  @Override
  public void preEvent() {
    throw new OperationNotSupportedException("Template method not used!!!");
  }

}
