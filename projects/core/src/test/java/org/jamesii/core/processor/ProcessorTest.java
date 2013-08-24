package org.jamesii.core.processor;

import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.simulationrun.stoppolicy.DummySimRun;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStop;

import junit.framework.TestCase;

/**
 * Super class for simple processor test cases. The general idea is to let the
 * processor under consideration simulate a simple model for which the processor
 * behaviour is known. Additionally, let the model store its behaviour during
 * the simulation, as this can then be checked afterwards.
 * 
 * Creation date: 15. 08. 2006
 * 
 * @author Roland Ewald
 * 
 */
public abstract class ProcessorTest<P extends IProcessor<Double>, M extends IModel>
    extends TestCase {

  /**
   * Get processor to be tested, which is initialised to execute the given
   * model.
   * 
   * @param model
   *          the test model
   * @return the processor to be tested
   */
  public abstract P getProcessor(M model);

  /**
   * Runs the given processor.
   * 
   * @param processor
   *          the processor to be executed
   */
  protected void runProcessor(P processor) {
    ISimulationRun simRun = setSimulationRun(processor);
    ((IRunnable) processor).run(new SimTimeStop(Double.POSITIVE_INFINITY));
  }

  /**
   * Sets the simulation run. Initialises simulation with {@link DummySimRun}.
   * 
   * @param processor
   *          the processor to which the dummy simulation run shall be attached
   * @return the simulation run associated with the processor
   */
  protected ISimulationRun setSimulationRun(P processor) {
    final P proc = processor;
    ISimulationRun simRun = new DummySimRun() {
      @Override
      public Double getTime() {
        return proc.getTime();
      }
    };
    processor.setComputationTask(simRun);
    return simRun;
  }

}
