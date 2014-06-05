/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.processor;

import java.util.logging.Level;

import model.devs.AtomicModel;

import org.jamesii.SimSystem;

import examples.devs.processor.ProcessorState.Phase;

/**
 * A simple atomic DEVS model of an abstract processor, adapted from the
 * processor described by Zeigler et al. (2000, p. 82). The processor can
 * receive {@link Job jobs} which then will be processed for a defined
 * processing time. Afterwards the processor will output the "processed" job.
 * 
 * @author Alexander Steiniger
 * 
 */
public class Processor extends AtomicModel<ProcessorState> {

  /**
   * The serialization id
   */
  private static final long serialVersionUID = 495627599734529486L;

  public static final String PORT_OUT = "out";

  public static final String PORT_IN = "in";

  /**
   * Default value for the parameter processingTime
   */
  public static final double DEFAULT_PROCESSING_TIME = 15.0;

  /**
   * The processing time for incoming and accepted jobs
   */
  private double processingTime = DEFAULT_PROCESSING_TIME;

  public Processor(String name) {
    super(name);
    init();
  }

  /**
   * Adds ports to the model
   */
  private void addPorts() {
    addInPort(PORT_IN, Job.class);
    addOutPort(PORT_OUT, Job.class);
  }

  @Override
  public void init() {
    super.init();
    addPorts();
  }

  @Override
  protected ProcessorState createState() {
    return new ProcessorState();
  }

  @Override
  protected void deltaExternal(double elapsedTime) {
    Phase phase = getState().getPhase();
    if (phase == Phase.PASSIVE) {
      getState().setPhase(Phase.ACTIVE);
      getState().setSigma(processingTime);

      // get job
      if (getInPort(PORT_IN).getValuesCount() == 1) {
        getState().setJob((Job) getInPort(PORT_IN).read());
        SimSystem.report(Level.INFO, printReceivedJob());
      } else {
        // there should only be one input in case of DEVS
        throw new IllegalStateException();
      }
    } else {
      // phase == ACTIVE
      getState().setSigma(getState().getSigma() - elapsedTime);
    }
  }

  @Override
  protected void deltaInternal() {
    Phase phase = getState().getPhase();
    if (phase == Phase.ACTIVE) {
      getState().setPhase(Phase.PASSIVE);
      getState().setSigma(Double.POSITIVE_INFINITY);
      getState().setJob(null);
    } else {
      // should not happen
      throw new IllegalStateException();
    }
  }

  @Override
  protected void lambda() {
    if (getState().getPhase() == Phase.ACTIVE) {
      getOutPort(PORT_OUT).write(getState().getJob());
    } else {
      // should not happen
      throw new IllegalStateException();
    }
  }

  @Override
  public double timeAdvance() {
    return getState().getSigma();
  }

  private String printReceivedJob() {
    StringBuffer buffer = new StringBuffer();
    buffer.append(getName());
    buffer.append(" receieved new job ");
    buffer.append(getState().getJob().toString());
    return buffer.toString();
  }

}
