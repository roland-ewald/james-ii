/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.abstractsequential;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import model.devs.IAtomicModel;
import model.devs.ICoupledModel;
import model.devscore.IBasicAtomicModel;
import model.devscore.IBasicCoupledModel;
import model.devscore.IBasicDEVSModel;

import org.jamesii.core.model.AbstractState;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.eventset.plugintype.EventQueueFactory;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;

import simulator.devs.abstractsequential.util.AbstractSequentialCopyHandler;
import simulator.devscore.BasicDEVSProcessor;
import simulator.devscore.DEVSProcessorState;

/**
 * The Class Coordinator. A coordinator in the world of DEVS is a simulation
 * algorithm which computes the trajectory of a DEVS coupled model. This
 * coordinator there uses the simulation algorithms (coordinators and
 * simulators) instantiated for its children.
 * 
 * @author Jan Himmelspach
 * @author Alexander Steiniger
 */
public class Coordinator extends BasicDEVSProcessor implements
    IAbstractSequentialProcessor {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 3806337098005791079L;

  /** The copy handler. */
  private AbstractSequentialCopyHandler copyHandler = new AbstractSequentialCopyHandler();

  /**
   * The event queue factory to be used to create the instance used for the
   * event attribute.
   */
  private EventQueueFactory eqf;

  /** The event queue used internally. */
  private IEventQueue<IBasicDEVSModel, Double> events;

  /** The set of imminent models. */
  private final Map<IBasicDEVSModel, Object> imminents = new HashMap<>();

  /**
   * The subProcessors map contains a mapping between all submodels of the model
   * which is returned by a call to getModel() and their processors.
   */
  private Map<IModel, IAbstractSequentialProcessor> subProcessors =
      new HashMap<>();

  /**
   * The Constructor.
   * 
   * @param model
   *          the model
   * @param eqf
   *          the eqf
   */
  public Coordinator(ICoupledModel model, EventQueueFactory eqf) {
    super(model);
    this.eqf = eqf;
  }

  @Override
  public void doEvent() {
    throw new OperationNotSupportedException("Template method not used!!!");
  }

  /**
   * The doRemainder method of a coordinator copies the external inputs to the
   * models and calls the doRemainder method of all imminent or influenced
   * children. These children are "collected" in the global imminents list. Each
   * child's tonie value is stored in the internal event queue. Afterward the
   * new minimal tonie is computed and returned.
   * 
   * @param time
   *          the time
   * 
   * @return time of next internal event (tonie)
   */
  @Override
  public double doRemainder(Double time) {

    ICoupledModel cm = (ICoupledModel) getModel();
    imminents.remove(cm);

    // the models which get influenced by one of the imminent ones
    HashMap<IBasicAtomicModel<? extends AbstractState>, Object> influencees =
        new HashMap<>();

    HashMap<IBasicCoupledModel, Object> influencedCM =
        new HashMap<>();

    copyHandler.copyValues(cm.getEICIterator(), cm, influencees, influencedCM);

    imminents.putAll(influencees);
    imminents.putAll(influencedCM);

    for (IBasicDEVSModel model : imminents.keySet()) {
      Double tonie = subProcessors.get(model).doRemainder(time);
      // System.out.println("New tonie for model "+model.getFullName()+" is
      // "+tonie);
      events.requeue(model, tonie);

      model.clearInPorts();
    }

    imminents.clear();

    Double tonie = events.getMin();

    if (tonie == null) {
      tonie = Double.POSITIVE_INFINITY;
    }

    // update time variables
    this.setTime(tonie);
    this.setTimeOfLastEvent(time);

    return tonie;
  }

  @Override
  public String getCompleteInfoString() {
    return super.getCompleteInfoString() + "\nEvent queue: "
        + events.getClass().getName();
  }

  /**
   * The getOutputs() calls the getOutputs method of all imminent children.
   * After having called the getOutputs method it copies the outputs according
   * to the internal and external out couplings. Finally it merges the set of
   * imminents and influencees into the imminents set.
   */
  @Override
  public void getOutputs() {
    // call getOutputs imminent child

    ICoupledModel cm = (ICoupledModel) getModel();

    // System.out.println(getTime()+" -- "+subProcessors.size()+" --- "+
    // events.size());

    // while (events.size() > 0)
    // System.out.println(events.getMin()+" "+ events.dequeue());

    Collection<IBasicDEVSModel> imminentsSel = events.dequeueAll();
    
    // execute the select method
    IBasicDEVSModel imminent = cm.select(new HashSet<>(imminentsSel));

    imminents.put(imminent, null);

    // now lets place all not imminent models back into the queue
    for (IBasicDEVSModel m: imminentsSel) {
      if (m != imminent) {
        events.enqueue(m, getTime());
      }
    }

    // the models which get influenced by one the imminent one
    HashMap<IBasicAtomicModel<? extends AbstractState>, Object> influencees =
        new HashMap<>();

    HashMap<IBasicCoupledModel, Object> influencedCM =
        new HashMap<>();

    // System.out.println(((DEVSProcessorState) getState()).getTonie()+":

    // propagate the messages ...

    // Calling getOutputs for the model "+model);
    subProcessors.get(imminent).getOutputs();
    copyHandler.copyValues(cm.getICIterator(imminent), cm, influencees,
        influencedCM);
    copyHandler.copyValues(cm.getEOCIterator(imminent), cm, influencees,
        influencedCM);
    imminent.clearOutPorts();

    imminents.putAll(influencees);
    imminents.putAll(influencedCM);
    
  }

  /**
   * Initialize the coordinator's instance.
   * 
   * @param time
   *          the time
   */
  @Override
  @SuppressWarnings("unchecked")
  public void init(double time) {
    ParameterBlock eqfp = new ParameterBlock(getModel());
    events = eqf.createDirect(eqfp);

    ((DEVSProcessorState) getState()).setTole(time);
    Iterator<IBasicDEVSModel> modelIt =
        ((ICoupledModel) getModel()).getSubModelIterator();

    while (modelIt.hasNext()) {
      IBasicDEVSModel model = modelIt.next();

      IAbstractSequentialProcessor proc;
      if (model instanceof ICoupledModel) {
        proc = new Coordinator((ICoupledModel) model, eqf);

      } else {
        proc = new Simulator((IAtomicModel<? extends AbstractState>) model);
      }
      proc.init(time);
      subProcessors.put(model, proc);
      addChild(proc);
      events.enqueue(model, proc.getTimeOfNextInternalEvent());
      // System.out.println("added model "+model+" evq size now:
      // "+events.size());
    }
    this.setTime(events.getMin());
  }

  @Override
  public void postEvent() {
    throw new OperationNotSupportedException("Template method not used!!!");
  }

  /**
   * Inherited but unused methods.
   */
  @Override
  public void preEvent() {
    throw new OperationNotSupportedException("Template method not used!!!");
  }

}
