/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;


import java.util.Iterator;

import org.jamesii.core.base.INamedEntity;
import org.jamesii.core.model.Model;

import model.devscore.ports.IPort;
import model.devscore.ports.IPortSet;
import model.devscore.ports.Port;
import model.devscore.ports.PortSet;
import model.devscore.utils.DEVSAccessRestriction;

/**
 * Ancestor class for all (coupled and atomic) models of a DEVS model. Each DEVS
 * model can have in and out ports as well as it must support a variety of
 * methods.
 * 
 * 
 * @author Jan Himmelspach
 */
public abstract class BasicDEVSModel extends Model implements IBasicDEVSModel {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5328109248822837647L;

  private static final int INIT_LEVEL = -2;

  /**
   * cache for the fullname (that's the parent's names separated by dots and
   * finally the own name (a.b.c.myname) This attribute is updated by the update
   * method
   */
  private String fullname = null;

  /**
   * Set of in and out ports. Each DEVS model can have an unlimited number of
   * uniquely named ports.
   */
  private IPortSet inPorts = new PortSet();

  /** The level in the hierarchy the model is located at. */
  private int level = INIT_LEVEL;

  /** The out ports. */
  private IPortSet outPorts = new PortSet();

  /** Parent model in a DEVS hierarchy. */
  private IBasicCoupledModel parent = null;

  /**
   * Constructor.
   */
  public BasicDEVSModel() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param name
   *          of the model
   */
  public BasicDEVSModel(String name) {
    super(name);
  }

  /**
   * Adds a new input port to the model. Addition will fail if the same object
   * is already used as a port.
   * 
   * @param port
   *          the port to add
   */
  public void addInPort(IPort port) {
    addPort(getInPorts(), port);
  }

  /**
   * Adds a port of the specified class with the given name to the set of
   * InPorts. Thereby the portClass must be inherited from the class Port. Two
   * ports cannot have the same name -> an attempt to insert a second port with
   * an already used name will fail!
   * 
   * @param name
   *          of the new port
   * @param portValueClass
   *          of the new port
   * 
   * @return returns a reference to the new port for further usage
   */
  public IPort addInPort(String name, Class<?> portValueClass) {
    return addPort(getInPorts(), name, portValueClass);
  }

  /**
   * Adds a new output port to the model. Addition will fail if the same object
   * is already used as a port.
   * 
   * @param port
   *          the port to add
   */
  public void addOutPort(IPort port) {
    addPort(getOutPorts(), port);
  }

  /**
   * Adds a port of the specified class with the given name to the set of
   * OutPorts. Thereby the portClass must be inherited from the class Port. Two
   * ports cannot have the same name -> an attempt to insert a second port with
   * an already used name will fail!
   * 
   * @param name
   *          of the new port
   * @param portValueClass
   *          of the new port
   * 
   * @return returns a reference to the new port for further usage
   */
  public IPort addOutPort(String name, Class<?> portValueClass) {
    return addPort(getOutPorts(), name, portValueClass);
  }

  /**
   * Add a port to the model.
   * 
   * @param portSet
   *          the port set
   * @param port
   *          the port
   */
  private void addPort(IPortSet portSet, IPort port) {
    portChangeAllowed();
    if (!getInPorts().hasPort(port) && !(getOutPorts().hasPort(port))) {
      portSet.addPort(port);
    }
  }

  /**
   * Adds a port of the specified class with the given name to the given set of
   * ports. Thereby the portClass must be inherited from the class Port. Two
   * ports cannot have the same name -> an attempt to insert a second port with
   * an already used name will fail! This method is used internally by the
   * addInPort and addOutPort methods.
   * 
   * @param portSet
   *          a set of ports
   * @param name
   *          of the new port
   * @param portValueClass
   *          of the new port
   * 
   * @return returns a reference to the new port for further usage
   */
  private IPort addPort(IPortSet portSet, String name, Class<?> portValueClass) {
    portChangeAllowed();
    Port port = new Port(name, portValueClass);
    portSet.addPort(port);
    return port;
  }

  /**
   * Build full name, that's the model's parent names followed by the own name
   * each seperated by a dot - this name is always unique because two models
   * with the same parent a re not allowed to have the same names.
   */
  public void buildFullName() {

    if (parent == null) {
      fullname = getName();
    } else {
      StringBuilder sb = new StringBuilder(parent.getFullName());
      sb.append(".");
      sb.append(getName());
      fullname = sb.toString();
    }

  }

  /**
   * Clear all parcels from inports.
   */
  @Override
  public void clearInPorts() {
    getInPorts().clearPorts();
  }

  /**
   * Clear all parcels from Outports.
   */
  @Override
  public void clearOutPorts() {
    getOutPorts().clearPorts();
  }

  /**
   * Compare an object o to this model.
   * 
   * @param o
   *          which should be compared to this
   * 
   * @return the int
   */
  @Override
  public int compareTo(INamedEntity o) {

    return getFullName().compareTo(((IBasicDEVSModel) o).getFullName());
  }

  /**
   * ?.
   */
  @Override
  public void finalization() {

  }

  /**
   * Returns a new DEVSAccesssRestriction object.
   * 
   * @return the access restriction
   */
  protected DEVSAccessRestriction getAccessRestriction() {
    if (super.getAccessRestriction() == null) {
      return new DEVSAccessRestriction();
    }
    return (DEVSAccessRestriction) super.getAccessRestriction();
  }

  /**
   * Get all information encapsulated into this object.
   * 
   * @return the complete info string
   */
  @Override
  public String getCompleteInfoString() {
    return getFullName();
  }

  /**
   * Returns the full name of a model A.B.C.name whereby A B and C are the names
   * of the parent models and name is the name of this model
   * 
   * @return the fullname
   */
  @Override
  public String getFullName() {
    // only build the fullname if the fullname string is empty
    if (fullname == null) {
      // fullname == ""
      buildFullName();
      // fullname != ""
    }
    // fullname != ""
    return fullname;
  }

  /**
   * If an InPort with the given name exists this method will return a pointer
   * to the port's interface otherwise it returns null.
   * 
   * @param name
   *          of the searched port
   * 
   * @return reference to the port if it exists otherwise null
   */
  @Override
  public IPort getInPort(String name) {
    return getPortByName(getInPorts(), name);
  }

  /**
   * Returns the number of in ports.
   * 
   * @return number of inports
   */
  public int getInPortCount() {
    return getInPorts().size();
  }

  /**
   * Returns an iterator for iterating the in ports set.
   * 
   * @return iterator for the inports
   */
  @Override
  public final Iterator<IPort> getInPortIterator() {
    return getInPorts().iterator();
  }

  /**
   * DEVS models are hierarchical models and thus each model resides on a level
   * in the model tree. For some algorithms this level information can be used
   * to speed up the algorithms. The level information is generated when the
   * getLevel() method is called first, is it resetted whenever the parent of
   * the model changes (and automatically recomputed on the next getLevel()
   * call). The lowest level is -1, the "no valid" level value is
   * {@value #INIT_LEVEL}.
   * 
   * @return the level this model is situated in the hierarchy
   */
  @Override
  public int getLevel() {
    // if we need the level information and if it is not already there then
    // we try to compute it
    if (level == INIT_LEVEL) {
      if (parent != null) {
        level = parent.getLevel() + 1;
      } else {
        level = -1;
      }
    }
    return level;
  }

  /**
   * If an OutPort with the given name exists this method will return a pointer
   * to the port's interface otherwise it returns null.
   * 
   * @param name
   *          of the searched port
   * 
   * @return reference to the port if it exists otherwise null
   */
  @Override
  public IPort getOutPort(String name) {
    return getPortByName(getOutPorts(), name);
  }

  /**
   * Returns the number of out ports.
   * 
   * @return number of outports
   */
  public int getOutPortCount() {
    return getOutPorts().size();
  }

  /**
   * Returns an iterator for iterating the out ports set.
   * 
   * @return iterator for the outports
   */
  @Override
  public Iterator<IPort> getOutPortIterator() {
    return getOutPorts().iterator();
  }

  /**
   * Returns the parent of this model.
   * 
   * @return Returns the parent of this model
   */
  @Override
  public IBasicCoupledModel getParent() {
    /*
     * if (this.getAccessRestriction().inUserFunction()) { throw new
     * OperationNotAllowedException("You are not allowed to call the "+
     * "getParent() function from within lambda, deltaExt, deltaIn or
     * deltaCon!"); }
     */

    // @TODO (general): addXRequest methods currently rely on the getParent
    // method ...
    return parent;
  }

  /**
   * This method looks up the given set of ports for a port with the given name.
   * It returns a pointer to the interface of the port if a port exists -
   * otherwise it will return null. The method does not perform any type check -
   * the given set must contain objects implementing the IPort interface -
   * therefore it is made private - currently it is called by the
   * <code>getInPortName</code> and <code>getOutPortName</code> methods only!
   * 
   * @param set
   *          of ports
   * @param name
   *          of the searched port
   * 
   * @return reference if the port exists otherwise null
   */
  private IPort getPortByName(IPortSet set, String name) {
    return set.getPort(name);
  }

  /**
   * Checks whether there is at least one input at any port.
   * 
   * @return true if any inport has an input
   */
  @Override
  public final boolean hasExternalInput() {

    Iterator<IPort> itp = getInPortIterator();
    IPort port;

    // loop through all in ports and check whether there are any pending
    // messages
    while (itp.hasNext()) {

      port = itp.next();
      // is there an incoming message?
      if (port.hasValue()) {
        // return with true if first port with a message has been found
        return true;
      }
    }
    // if we get here no port with a pending message has been found
    // so return false
    return false;
  }

  @Override
  public boolean hasInPort(IPort port) {
    return this.getInPorts().hasPort(port);
  }

  /**
   * Determines whether the model has an InPort with the given name.
   * 
   * @param name
   *          of the searched port
   * 
   * @return true if an inport exists
   */
  public boolean hasInPort(String name) {
    return (getInPort(name) != null);
  }

  @Override
  public boolean hasOutPort(IPort port) {
    return this.getOutPorts().hasPort(port);
  }

  /**
   * Determines whether the model has an OutPort with the given name.
   * 
   * @param name
   *          of the searched port
   * 
   * @return true if an outport exists
   */
  public boolean hasOutPort(String name) {
    return (getOutPort(name) != null);
  }

  /**
   * Checks whether this model owns the given port or not.
   * 
   * @param port
   *          which should be searched
   * 
   * @return true if the port exists
   */
  @Override
  public boolean hasPort(IPort port) {
    return (getOutPorts().hasPort(port) || getInPorts().hasPort(port));
  }

  /**
   * Checks whether this model owns the given port or not.
   * 
   * @param port
   *          which should be searched
   * 
   * @return true if the port exists
   */
  @Override
  public boolean hasPort(String port) {
    return (getOutPorts().hasPort(port) || getInPorts().hasPort(port));
  }

  /**
   * This method is called when a processor for this model gets instantiated. In
   * the default implementation this method does nothing.
   * 
   * @param time
   *          simulation start time
   */
  @Override
  public void initialization(double time) {

  }

  /**
   * Returns true if the model is imminent, i.e. its tonie is equal to the
   * current time
   * 
   * @return true if model is imminent
   */
  public final boolean isImminent() {
    return true;
  }

  /**
   * Returns true if the model is being influenced by an imminent model.
   * 
   * @return true if influenced
   */
  public final boolean isInfluencee() {
    return true;
  }

  /**
   * Addition of ports must not be done within model functions!.
   */
  private void portChangeAllowed() {
    if (this.getAccessRestriction().inUserFunction()) {
      throw new OperationNotAllowedException(
          "You are not allowed to directly"
              + " add or remove a port from within lambda, delta int, delta ext or delta con!");
    }
  }

  @Override
  public Object readFromOutPort(String name) {
    return getOutPorts().getPort(name);
  }

  @Override
  public Object readPort(IPort port) {
    if (getInPorts().hasPort(port.getName())) {
      return getInPorts().getPort(port.getName()).read();
    }
    return getOutPorts().getPort(port.getName()).read();

  }

  /**
   * Removes an existing input port from the model.
   * 
   * @param port
   *          the port
   */
  public void removeInPort(IPort port) {
    removePort(getInPorts(), port);
  }

  /**
   * Removes input port with the given name.
   * 
   * @param name
   *          the name
   */
  public void removeInPort(String name) {
    removePort(getInPorts(), name);
  }

  /**
   * Removes an existing input port from the model.
   * 
   * @param port
   *          the port
   */
  public void removeOutPort(IPort port) {
    removePort(getOutPorts(), port);
  }

  /**
   * Removes output port with the given name.
   * 
   * @param name
   *          the name
   */
  public void removeOutPort(String name) {
    removePort(getOutPorts(), name);
  }

  /**
   * Removes a port (if this is allowed at invocation).
   * 
   * @param portSet
   *          the port set
   * @param port
   *          the port
   */
  private void removePort(IPortSet portSet, IPort port) {
    portChangeAllowed();
    if (portSet.hasPort(port)) {
      portSet.removePort(port);
    }
  }

  /**
   * Removes port from a port set.
   * 
   * @param portSet
   *          the port set
   * @param name
   *          the name
   */
  private void removePort(IPortSet portSet, String name) {
    portChangeAllowed();
    if (portSet.hasPort(name)) {
      portSet.removePort(portSet.getPort(name));
    }
  }

  /**
   * Set the parent for this model.
   * 
   * @param parent
   *          which should become the parent of this model
   */
  @Override
  public void setParent(IBasicCoupledModel parent) {
    this.parent = parent;
    // any updating which is related to setting a new parent must be done in
    // the update method - this method is overwritten in descendant classes
    // e.g. the BasicCoupledModel for updating all children!!!
    update();
  }

  /**
   * Used internally for updating cached values if the structure changes (e.g.
   * model is moved to another parent)
   */
  @Override
  public void update() {
    // reset any level information (we be recomputed on next read action!)
    level = INIT_LEVEL;

    fullname = null;
  }

  /**
   * Write object on the specified port.
   * 
   * @param port
   *          on which should be written
   * @param o
   *          message that should be written
   */
  @Override
  public void writePort(IPort port, Object o) {
    if (getInPorts().hasPort(port.getName())) {
      getInPorts().getPort(port.getName()).write(o);
    } else {
      getOutPorts().getPort(port.getName()).write(o);
    }
  }

  /**
   * Write to a model's in port. It should be somewhat faster than directly
   * using writePort method, because it does not need to detect in which portset
   * the port shall be.
   * 
   * @param name
   *          the name
   * @param obj
   *          the obj
   */
  @Override
  public void writeToInPort(String name, Object obj) {
    getInPorts().getPort(name).write(obj);
  }

  /**
   * @return the inPorts
   */
  public IPortSet getInPorts() {
    return inPorts;
  }

  /**
   * @param inPorts
   *          the inPorts to set
   */
  public void setInPorts(IPortSet inPorts) {
    this.inPorts = inPorts;
  }

  /**
   * @return the outPorts
   */
  public IPortSet getOutPorts() {
    return outPorts;
  }

  /**
   * @param outPorts
   *          the outPorts to set
   */
  public void setOutPorts(IPortSet outPorts) {
    this.outPorts = outPorts;
  }

} // EOF
