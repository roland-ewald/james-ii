/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.switchnetwork;

import model.devs.AtomicModel;
import examples.devs.processor.Job;
import examples.devs.switchnetwork.SwitchState.Phase;

/**
 * A simple atomic DEVS model of a switch, adapted from the switch described by
 * Zeigler et al. (2000, p. 85). In contrast, the switch has only one input
 * port. The switch will alternately use one of its two output ports to send
 * incomming {@link Job jobs}.
 * 
 * @author Alexander Steiniger
 * 
 */
public class Switch extends AtomicModel<SwitchState> {

  /**
   * The serialization id
   */
  private static final long serialVersionUID = 8173433508506477209L;

  // ---- port names ----------------------------------------------------------
  public static final String PORT_IN = "in";

  public static final String PORT_OUT = "out";

  public static final String PORT_OUT1 = "out1";

  public static final double DEFAULT_PROCESSING_TIME = 0.0;

  /**
   * The processing time to route the inputs.
   */
  private double processTime = DEFAULT_PROCESSING_TIME;

  private void addPorts() {
    addInPort(PORT_IN, Job.class);
    addOutPort(PORT_OUT, Job.class);
    addOutPort(PORT_OUT1, Job.class);
  }

  @Override
  public void init() {
    super.init();
    addPorts();
  }

  public Switch(String name) {
    super(name);
    init();
  }

  @Override
  protected SwitchState createState() {
    return new SwitchState();
  }

  @Override
  protected void deltaExternal(double elapsedTime) {
    SwitchState state = getState();

    if (state.getPhase() == Phase.PASSIVE) {
      if (getInPort(PORT_IN).getValuesCount() == 1) {
        state.setJob((Job) getInPort(PORT_IN).read());
      } else {
        throw new IllegalStateException();
      }
      state.setPhase(Phase.ACTIVE);
      state.setPolarity(!state.getPolarity());
      state.setSigma(processTime);
    } else {
      // we keep current polarity
      state.setSigma(state.getSigma() - elapsedTime);
    }
  }

  @Override
  protected void deltaInternal() {
    if (getState().getPhase() == Phase.ACTIVE) {
      // change state variables that have to change
      getState().setPhase(Phase.PASSIVE);
      getState().setJob(null);
      getState().setSigma(Double.POSITIVE_INFINITY);
    } else {
      // should not happen
      throw new IllegalStateException();
    }
  }

  @Override
  protected void lambda() {
    SwitchState state = getState();
    if (state.getPhase() == Phase.ACTIVE) {
      // determine output port based on current polarity
      if (state.getPolarity()) {
        getOutPort(PORT_OUT).write(state.getJob());
      } else {
        getOutPort(PORT_OUT1).write(state.getJob());
      }
    } else {
      // should not happen
      throw new IllegalStateException();
    }
  }

  @Override
  public double timeAdvance() {
    return getState().getSigma();
  }

}
