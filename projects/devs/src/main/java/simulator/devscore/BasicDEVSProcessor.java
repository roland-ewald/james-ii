/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package simulator.devscore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;
import model.devscore.ports.PortParcel;
import model.devscore.utils.DEVSAccessRestriction;

import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.TreeProcessor;
import org.jamesii.core.processor.messages.IMessageHandler;

import simulator.devscore.util.DoneMessage;
import simulator.devscore.util.YMessage;

/**
 * The Class BasicDEVSProcessor. The BasicDEVSProcessor class is the ancestor
 * for all DEVS processing processors in the system. It cannot be directly used
 * - please check out the descendants for seeing "real simulation algorithms"
 * for DEVS formalisms.
 * 
 * Literature:<br>
 * Jan Himmelspach and Adelinde M Uhrmacher (2004)<br>
 * A Component-Based Simulation Layer for JAMES<br>
 * In: PADS '04: Proceedings of the eighteenth workshop on Parallel and
 * distributed simulation, ed. by ACM Press, pp. 115-122, IEEE Computer Society,
 * Los Alamitos, California.<br>
 * 
 * @author Jan Himmelspach et alii
 */
public abstract class BasicDEVSProcessor extends TreeProcessor<Double> implements
    IBasicDEVSProcessor {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1296169359806728817L;

  /**
   * Creates a DEVS processor.
   * 
   * 
   * @param model
   *          the model for which this processor is created
   */
  public BasicDEVSProcessor(IModel model) {
    super(model);
    setState(new DEVSProcessorState());
  }

  /**
   * This method can be used in descendant classes for finalizing the processing
   * of the current event. This method is called from within the sendDoneMessage
   * method and so gets executed BEFORE the done message is actually send and
   * AFTER the done message parameters have been calculated This method may be
   * used (e.g.) for resetting internal variables
   */
  protected void finalizeEventProcessing() {
  }

  /**
   * Gets the access restriction. The access restriction can and should be used
   * to avoid formalism violations. E.g., in DEVS formalisms you are typically
   * not allowed to modify the state during lambda which can be checked by using
   * this object, and other information.
   * 
   * @return a reference to the DEVSAccesRestriction used
   */
  public DEVSAccessRestriction getDEVSAccessRestriction() {
    return (DEVSAccessRestriction) getAccessRestriction();
  }

  /**
   * Returns the associated model as a BasicDEVSModel, this method can be used
   * instead of manually typecasting a model each time.
   * 
   * @return IBasicDEVSModel -> reference to the simulated model
   */
  public IBasicDEVSModel getAssociatedDEVSModel() {
    return (IBasicDEVSModel) getModel();
  }

  /**
   * Returns a list of out ports with existing values.
   * 
   * @return HashMap(String, ArrayList) -> list of out ports with existing
   *         values
   */
  public Map<String, List<Object>> getHashedSendValues() {
    // System.out.println("trying to get send values");
    IBasicDEVSModel model = getModel();

    Map<String, List<Object>> result = new HashMap<>();

    Iterator<IPort> itp = model.getOutPortIterator();
    IPort p;

    // while there are ports
    while (itp.hasNext()) {

      List<Object> list;

      // fetch a port
      p = itp.next();
      // if this port has at least one value
      if (p.hasValue()) {

        list = p.readAll();
        p.clear();
        p.changed();

        // add a new PortParcel (that's a port reference as well as the list
        // of objects
        result.put(p.getName(), list);
      }
    }
    // return a list of PortParcels
    return result;
  }

  /**
   * Returns a list of out ports with existing values as a list of PortParcels.
   * 
   * @return ArrayList -> list of out ports with existing values as a list of
   *         PortParcels
   */
  public List<PortParcel> getSendValues() {

    IBasicDEVSModel model = getModel();

    ArrayList<PortParcel> result = new ArrayList<>();

    Iterator<IPort> itp = model.getOutPortIterator();
    IPort p;

    // while there are ports
    while (itp.hasNext()) {

      List<Object> list;

      // fetch a port
      p = itp.next();

      // if this port has at least one value
      if (p.hasValue()) {

        list = p.readAll();
        p.clear();
        p.changed();

        // System.out.println("all values added");
        // add a new PortParcel (that's a port reference as well as the list
        // of objects
        result.add(new PortParcel(p, list));
      }
    }

    // return a list of PortParcels
    return result;
  }

  /**
   * You can use this method if you want to store the tonie of the incoming
   * message time, <b>but attention</b> you are about to override the tonie of
   * your </tt>Simulator, Coordinator</tt>. Do this only at the time when you
   * <b>do not</b> need the old once, e.g. your processor isn't imminent and you
   * have received a <tt>YMessage</tt> which will be processed. That save us a
   * private variable which don't need to be declared namely the
   * <tt>incomingMessageTime</tt>. This method declaration should show us at
   * what time we use the incoming message time and not more the expired tonie.
   * 
   * @param time
   *          -> incomingMessageTime (internally the tonie)
   */
  public void setTime(double time) {
    ((DEVSProcessorState) getState()).setTonie(time);
  }

  /**
   * This method declaration should show us at what time we use the incoming
   * message time and not more the expired tonie. <b>Before</b> you can use this
   * functionality, you have to store the incoming message time with
   * <tt>setTime(double)</tt>. <b>But attention</b> you are about to override
   * the old tonie and you have to check whether he was expired or not.
   * 
   * @return double -> incomingMessageTime (internally the tonie)
   * 
   * @see simulator.devscore.BasicDEVSProcessor#setTime(double)
   */
  @Override
  public Double getTime() {
    return ((DEVSProcessorState) getState()).getTonie();
  }

  /**
   * This method sets the tole from the associated state, thereby requiring that
   * it is a descendant of the DEVSProcessorState class and you do not cast
   * anything by yourself.
   * 
   * @param newTole
   *          the new tole
   */
  public void setTimeOfLastEvent(double newTole) {
    ((DEVSProcessorState) getState()).setTole(newTole);
  }

  /**
   * This method reads the tole from the associated state, thereby requiring
   * that it is a descendant of the DEVSProcessorState class and you do not cast
   * anything by yourself.
   * 
   * @return double -> tole
   */
  public double getTimeOfLastEvent() {
    return ((DEVSProcessorState) getState()).getTole();
  }

  /**
   * This method sets the tonie from the associated state, thereby requiring
   * that it is a descendant of the DEVSProcessorState class and you do not cast
   * anything by yourself.
   * 
   * @param newTonie
   *          the new tonie
   */
  public void setTimeOfNextInternalEvent(double newTonie) {
    ((DEVSProcessorState) getState()).setTonie(newTonie);
  }

  @Override
  public double getTimeOfNextInternalEvent() {
    return ((DEVSProcessorState) getState()).getTonie();
  }

  /**
   * Send a done message to the parent processor containing the passed
   * information. Before sending the message the finalizeEventProcessing method
   * is executed. (1) finalizeEventProcessing() (2) send done message
   * 
   * @param tonie
   *          time of next event for this processor
   */
  @SuppressWarnings("unchecked")
  public void sendDoneMessage(double tonie) {
    finalizeEventProcessing();
    ((IMessageHandler<IProcessor>) this.getParent())
        .receiveMessage(new DoneMessage(this, tonie));
  }

  /**
   * This method should be used directly after the lambda function of an atomic
   * model / after a coupled model has received all y messages from it's
   * children.
   * 
   * @return true if message could be sent to parent
   */
  @SuppressWarnings("unchecked")
  public boolean sendYMessage() {
    if (getParent() instanceof IMessageHandler) {
      YMessage ym = new YMessage(this, getHashedSendValues());
      // call the receiveMessage method of the parent and transmit all used
      // ports and their values to the parent (at once)
      ((IMessageHandler<IProcessor>) getParent()).receiveMessage(ym);
      return true;
    }
    return false;
  }

  @Override
  protected void nextStep() {
    preEvent();
    doEvent();
    postEvent();
  }

  /**
   * Calculations to be done before the "real" Event calculation takes place.
   */
  public abstract void preEvent();

  /**
   * In doEvent the next calculation Event will be calculated (so time is
   * constant during a call to doEvent.
   */
  public abstract void doEvent();

  /**
   * Clearing up. Calculations to be done after the "real" Event calculations
   * took place
   */
  public abstract void postEvent();

}