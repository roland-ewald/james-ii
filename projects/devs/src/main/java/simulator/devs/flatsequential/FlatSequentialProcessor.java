/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.flatsequential;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import model.devs.IAtomicModel;
import model.devs.ICoupledModel;
import model.devscore.IBasicAtomicModel;
import model.devscore.IBasicCoupledModel;
import model.devscore.IBasicDEVSModel;

import org.jamesii.core.experiments.tasks.stoppolicy.EmptyStopCondition;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.model.AbstractState;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.InvalidModelException;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.processor.ProcessorStatus;
import org.jamesii.core.processor.execontrol.ExecutionControl;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.plugintype.EventQueueFactory;

import simulator.devs.flatsequential.eventforwarding.ExternalEventForwardingHandler;
import simulator.devscore.BasicDEVSProcessor;
import simulator.devscore.DEVSProcessorState;

/**
 * The Class FlatSequentialProcessor.
 * 
 * <p>
 * This simulator virtually flattens the model tree. It is only working on
 * atomic models, and only relies on coupled models for the event forwarding.
 * Therefore all atomic models are enqueued with their tonies (time of next
 * internal event) in one event queue ({@link #events}). Using this as a base
 * the simulator can just dequeue all atomic models having the minimal time
 * stamp and execute their lambda functions. Then the outputs are copied to the
 * receivers by using the {@link #eventForwarding} mechanism to be used.
 * Afterwards the simulator will execute the state transition functions
 * (delta<sub>int</sub> and delta<sub>ext</sub>, depending on the events to be
 * processed per model. Finally the time advance method of all models which
 * executed any of the state transition functions is excuted and the new tonie
 * is enqueued (re - enqueued (see
 * {@link org.jamesii.core.util.eventset.IEventQueue#requeue(Object, Comparable) }
 * )) in the event list. Now the next models with minimal tonie can be
 * retrieved, and we start over again. <br>
 * This simulator implements the {@link org.jamesii.core.processor.IRunnable}
 * interface by using the
 * {@link org.jamesii.core.processor.execontrol.ExecutionControl} class, and
 * thus it is a stand alone simulation algorithm which can be used to compute a
 * models trajectory in a simulation run.
 * </p>
 * 
 * @author Jan Himmelspach
 * @author Alexander Steiniger
 * 
 */
public class FlatSequentialProcessor extends BasicDEVSProcessor implements
    IRunnable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7038946306707259645L;

  /** The event forwarding. */
  private ExternalEventForwardingHandler eventForwarding;

  /**
   * The list of events, here the list of devs models together with their
   * tonies.
   */
  private Map<IBasicCoupledModel, IEventQueue<IBasicDEVSModel, Double>> events;

  /** Execution control variable, used by all runnable processors. */
  private ExecutionControl executionControl;

  /** The influencees and imminent. */
  private Map<IBasicDEVSModel, Object> influenceesAndImminent;

  /** The state. */
  private DEVSProcessorState state;

  /** The event queue factory. */
  private EventQueueFactory eqFactory;

  /** The toles. */
  private Map<IBasicDEVSModel, Double> toles = new HashMap<>();

  /**
   * A flag that indicates whether the model associated to this processor is
   * atomic or not, i.e., coupled.
   */
  private Boolean isAssociatedModelAtomic = false;

  /**
   * Instantiates a new flat sequential processor.
   * 
   * @param model
   *          the root model to be computed
   * @param eqf
   *          the event queue factory to be used
   * @param eventForwarding
   *          the event forwarding mechanism
   */
  public FlatSequentialProcessor(IModel model, EventQueueFactory eqf,
      ExternalEventForwardingHandler eventForwarding) {
    super(model);
    state = (DEVSProcessorState) getState();
    eqFactory = eqf;
    this.eventForwarding = eventForwarding;
    executionControl = new ExecutionControl(this);
  }

  /**
   * Invokes the respective functions of the associated model in the defined
   * order, in case the associated model is an atomic DEVS model and not a
   * coupled DEVS model.
   */
  private void executeAtomicModel() {
    // call lambda function
    ((IBasicAtomicModel<?>) getModel()).lambdaSim();

    // trigger internal state transition as there can only be internal events in
    // case the associated model is a atomic one
    ((IBasicAtomicModel<?>) getModel()).deltaInternalSim();

    // clear output ports of associated model
    ((IBasicAtomicModel<?>) getModel()).clearOutPorts();

    // update time variables
    setTimeOfLastEvent(getTime());
    setTime(((IBasicAtomicModel<?>) getModel()).timeAdvanceSim() + getTime());
  }

  @Override
  public void doEvent() {
    if (isAssociatedModelAtomic) {
      // case 1: there is only one atomic model to execute
      executeAtomicModel();
    } else {
      // case 2: associated model is a coupled one

      // get all imminent models
      Map<IBasicDEVSModel, Object> imminents = getImminents();

      // call lambda of all imminent atomic models
      generateOutputs(imminents);

      Map<IBasicAtomicModel<? extends AbstractState>, Object> influencedAM =
          new HashMap<>();

      // propagate outputs
      eventForwarding.copyExternalEvents(imminents, influencedAM);

      // System.out.println("Number of influenced AM "+influencedAM.size());

      // call state transition and compute the timeAdvance value of all
      // influenced
      // or imminent models
      this.influenceesAndImminent = stateTransition(imminents, influencedAM);
    }
  }

  /**
   * Generate outputs.
   * 
   * @param imminents
   *          the imminents
   */
  @SuppressWarnings("unchecked")
  protected void generateOutputs(Map<IBasicDEVSModel, Object> imminents) {
    for (IBasicDEVSModel m : imminents.keySet()) {
      // System.out.println("Executing lambda of "+m.getFullName());
      ((IBasicAtomicModel<? extends AbstractState>) m).lambdaSim();
    }
  }

  @Override
  public String getCompleteInfoString() {
    return super.getCompleteInfoString() + "\nEvent queue: "
        + events.getClass().getName() + "\nCopy mechanism: "
        + eventForwarding.getClass().getName();
  }

  /**
   * Return a hash map containing all imminents.
   * 
   * @return the imminents
   */
  protected Map<IBasicDEVSModel, Object> getImminents() {
    // search for the atomic model to b executed by calling all select methods
    // on its path
    IBasicDEVSModel imminent = null;
    ICoupledModel cm = (ICoupledModel) getModel();

    // imminent can't be null in the end, there is always at least one imminent
    // model or the simulation run will terminate before
    while (imminent == null) {
      // get all potentially imminent children
      Collection<IBasicDEVSModel> imminentsSel = events.get(cm).dequeueAll();
      // select one of these using the responsible select method
      imminent = cm.select(new ArrayList<>(imminentsSel));
      // if the selected one is a coupled model we need to look in
      if (imminent instanceof IBasicCoupledModel) {
        cm = (ICoupledModel) imminent;
        imminent = null;
      }
      // now lets place all not imminent models back into the queue
      // there might be better solutions ...
      for (IBasicDEVSModel m : imminentsSel) {
        if (m != imminent) {
          events.get(m.getParent()).enqueue(m, getTime());
        }
      }
    }

    // add the imminent model found to the hashmap used later on
    Map<IBasicDEVSModel, Object> imminents = new HashMap<>();

    imminents.put(imminent, null);

    return imminents;
  }

  /**
   * Initializes the processor using the given time. Internally calls the
   * {@link #internalInit(IBasicCoupledModel, double)} method.
   * 
   * The event forwarding is initialized as well (thus the execution of this
   * method might take a while). <br>
   * The current time is moved to the minimal time (tonie) of all events.
   * 
   * @param time
   *          the time to start with
   */
  public void init(double time) {
    events = new HashMap<>();

    // check whether topmost model, i.e., associated model, is atomic or coupled
    // model
    if (getModel() instanceof IBasicAtomicModel) {
      // we do not need an event queue and event forwarding as we only have a
      // single atomic model to execute
      isAssociatedModelAtomic = true;

      // update time variables
      setTimeOfLastEvent(time);
      // update time of next internal event of atomic model
      // TODO check if it has to be tonie + tole
      double tonie = ((IBasicAtomicModel<?>) getModel()).timeAdvanceSim();
      setTime(tonie + time);
    } else if (getModel() instanceof IBasicCoupledModel) {
      events = new HashMap<>();
      internalInit((IBasicCoupledModel) getModel(), time);
      eventForwarding.init(getAssociatedDEVSModel());
      setTime(events.get(this.getAssociatedDEVSModel()).getMin());
    } else {
      // invalid model type
      throw new InvalidModelException(getModel()
          + " is neither an atomic nor a coupled DEVS model.");
    }
  }

  /**
   * Internal init.
   * 
   * @param model
   *          the model
   * @param time
   *          the time
   */
  @SuppressWarnings("unchecked")
  protected void internalInit(IBasicCoupledModel model, double time) {
    // initialize event queue for current coupled model
    ParameterBlock eqfp = new ParameterBlock(getModel());
    IEventQueue<IBasicDEVSModel, Double> queue = eqFactory.createDirect(eqfp);
    events.put(model, queue);

    // iterate and initialize all sub-models
    Iterator<IBasicDEVSModel> it = model.getSubModelIterator();
    while (it.hasNext()) {
      IBasicDEVSModel m = it.next();
      if (m instanceof IBasicCoupledModel) {
        internalInit((IBasicCoupledModel) m, time);
      } else {
        Double d =
            ((IBasicAtomicModel<? extends AbstractState>) m).timeAdvanceSim();
        if (!events.containsKey(m.getParent())) {
          events.put(m.getParent(),
              eqFactory.<IBasicDEVSModel> createDirect(null));

        }
        events.get(m.getParent()).enqueue(m, time + d);
        toles.put(m, time);
      }
    }
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
    if (!isAssociatedModelAtomic) {
      this.setTime(events.get(this.getAssociatedDEVSModel()).getMin());
    }
  }

  @Override
  public void preEvent() {

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

  /**
   * State transition.
   * 
   * @param imminents
   *          the imminents
   * @param influencedAM
   *          the influenced am
   * 
   * @return the hash map< i basic devs model, object>
   */
  @SuppressWarnings("unchecked")
  protected Map<IBasicDEVSModel, Object> stateTransition(
      Map<IBasicDEVSModel, Object> imminents,
      Map<IBasicAtomicModel<? extends AbstractState>, Object> influencedAM) {

    HashMap<IBasicDEVSModel, Object> influencedAndImminent = new HashMap<>();

    influencedAndImminent.putAll(influencedAM);

    // System.out.println("Influenced models: "+influencedAM);

    influencedAndImminent.putAll(imminents);

    Double t = getTime();
    setTimeOfLastEvent(t);
    // System.out.println("**********CURRENT TIME:" + t);

    // for all influenced or imminent models call the approbiate state
    // transition function
    for (IBasicDEVSModel m2 : influencedAndImminent.keySet()) {
      IAtomicModel<? extends AbstractState> m =
          (IAtomicModel<? extends AbstractState>) m2;

      if (!imminents.containsKey(m)) {
        // System.out.println("Executing ext state trans of "+m.getFullName());
        // System.out.println(m.getFullName()+" t:" + t + " , tole:" +
        // toles.get(m));
        m.deltaExternalSim(t - toles.get(m));
        m.clearInPorts();
      } else {
        if (!influencedAM.containsKey(m)) {
          // System.out.println("Executing int state trans of
          // "+m.getFullName());
          m.clearOutPorts();
          m.deltaInternalSim();
        } else {
          throw new InvalidModelException("the model is connected to itself");
        }
      }
      // inform any attached observer if the state has been changed
      m.getState().isChangedRR();
      this.changed();
      // Double d = t + m.timeAdvanceSim();
      // System.out.println("tonie of model "+m.getFullName()+" is set to "+d);
      toles.put(m, t);
      events.get(m.getParent()).requeue(m, t + m.timeAdvanceSim());
      // add the tonie of the coupled model to the event queue of its parent
      // coupled model
      if (m.getParent().getParent() != null) {
        events.get(m.getParent().getParent()).requeue(m.getParent(),
            events.get(m.getParent()).getMin());
      }
    }
    return influencedAndImminent;
  }

  @Override
  public void stop() {
    executionControl.stop();
  }

  /**
   * Useful for dynamic structure change handlers (since the list of toles is
   * protected).
   * 
   * This method is here because there is no abstract dynamic flat processor.
   * 
   * @param model
   *          the model
   */
  public void addModelToTOLEs(IBasicDEVSModel model) {
    toles.put(model, getTime());
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
