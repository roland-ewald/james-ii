/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.generator;

import java.util.logging.Level;

import model.devs.AtomicModel;

import org.jamesii.SimSystem;

import examples.devs.generator.GeneratorState.Phase;
import examples.devs.processor.Job;

/**
 * Simple atomic DEVS model of a generator, adapted from the generator described by
 * Zeigler et al. (2000, p. 80ff.). The generator produces and outputs jobs in a
 * predefined period.
 * 
 * @author Alexander Steiniger
 * 
 */
public class Generator extends AtomicModel<GeneratorState> {

  /**
   * The serialization id
   */
  private static final long serialVersionUID = 6197074091438561320L;

  public static final String PORT_OUT = "out";

  public static final double DEFAULT_PERIOD = 10.0;

  /**
   * The period with which output will be generated
   */
  private double period = DEFAULT_PERIOD;

  /**
   * Auxiliary counter of jobs being created (extension of original model and is
   * from a formal point of view part of the state)
   */
  private int jobNum = 0;

  public Generator(String name) {
    super(name);
    init();
  }

  @Override
  public void init() {
    super.init();
    addOutPort(PORT_OUT, Job.class);
  }

  @Override
  protected GeneratorState createState() {
    return new GeneratorState();
  }

  @Override
  protected void deltaExternal(double elapsedTime) {
    // no external state transitions
    throw new IllegalStateException();
  }

  @Override
  protected void deltaInternal() {
    jobNum++;
  }

  @Override
  protected void lambda() {
    if (getState().getPhase() == Phase.ACTIVE) {
      SimSystem.report(Level.INFO, "created job " + jobNum);
      getOutPort(PORT_OUT).write(new Job(jobNum));
    } else {
      // should not happen
      throw new IllegalStateException();
    }
  }

  @Override
  public double timeAdvance() {
    return period;
  }

}
