/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.abstractsequential;

import org.jamesii.core.experiments.tasks.stoppolicy.EmptyStopCondition;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.processor.ProcessorStatus;
import org.jamesii.core.processor.execontrol.ExecutionControl;
import org.jamesii.core.util.misc.Strings;

import simulator.devscore.BasicDEVSProcessor;
import simulator.devscore.DEVSProcessorState;

/**
 * The Class RootCoordinator. In the world of DEVS a root coordinator is the
 * driving simulation algorithm for any simulation run. A root coordinator is
 * responsible for initiating the simulation pulses to be executed.
 * 
 * @author Jan Himmelspach
 */
public class RootCoordinator extends BasicDEVSProcessor implements IRunnable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -5774338962718374366L;

  /** Execution control variable, used by all runnable processors. */
  private ExecutionControl executionControl;

  /** The topmost. */
  private IAbstractSequentialProcessor topmost;

  /**
   * Creates a DEVS processor, i.e. creates a DEVS specific state object
   * 
   * @param model
   *          for which this processor is created
   */
  public RootCoordinator(IModel model) {
    super(model);
    executionControl = new ExecutionControl(this);
  }

  @Override
  public void doEvent() {
    topmost.getOutputs();
    double tonie = topmost.doRemainder(getTime());
    ((DEVSProcessorState) getState()).setTole(this.getTime());
    this.setTime(tonie);
  }

  @Override
  public String getCompleteInfoString() {
    return super.getCompleteInfoString() + "\nTop most coordinator:\n"
        + Strings.indent(topmost.getCompleteInfoString(), "  ");

  }

  /**
   * Inits the simulation algorithm.
   * 
   * @param topMost
   *          the top most
   * @param time
   *          the time
   */
  public void init(IAbstractSequentialProcessor topMost, double time) {
    this.topmost = topMost;
    addChild(topMost);
    ((DEVSProcessorState) getState()).setTole(time);
    ((DEVSProcessorState) getState()).setTonie(topMost.getTime());
  }

  @Override
  public boolean isPausing() {
    return executionControl.isPausing();
  }

  @Override
  public boolean isRunning() {
    return executionControl.isRunning();
  }

  @Override
  public boolean isStopping() {
    return executionControl.isStopping();
  }

  @Override
  public void pause() {
    executionControl.pause();
  }

  @Override
  public void postEvent() {
    // TODO Auto-generated method stub

  }

  @Override
  public void preEvent() {
    // TODO Auto-generated method stub

  }

  @Override
  public void run() {
    run(new EmptyStopCondition());
  }

  @Override
  public void run(IComputationTaskStopPolicy end) {
    executionControl.run(end);
  }

  @Override
  public void run(IComputationTaskStopPolicy end, long pause) {
    executionControl.run(end, pause);
  }

  @Override
  public void run(IComputationTaskStopPolicy end, long pause, boolean paused) {
    executionControl.run(end, pause, paused);
  }

  @Override
  public void next(int num) {
    executionControl.next(num);
  }

  @Override
  public void stop() {
    executionControl.stop();
  }

  @Override
  public void setDelay(long pause) {
    executionControl.setDelay(pause);
  }

  @Override
  public ProcessorStatus getStatus() {
    return executionControl.getStatus();
  }

}
