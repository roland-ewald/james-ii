/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

/**
 * The Class InOutPortChangeRequest.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public class InOutPortChangeRequest extends PortChangeRequest {

  /** Class of the port. */
  private Class<? extends IPort> portClass = null;

  /** Name of the port. */
  private String portName = "";

  /**
   * The Constructor.
   * 
   * @param source
   *          the source
   * @param model
   *          the model
   * @param port
   *          the port
   * @param add
   *          the add
   */
  public InOutPortChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model,
      IPort port, boolean add) {
    super(source, model, port, add);
  }

  /**
   * The Constructor.
   * 
   * @param source
   *          the source
   * @param model
   *          the model
   * @param portName
   *          the port name
   * @param portClass
   *          the port class
   * @param add
   *          the add
   */
  public InOutPortChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model,
      String portName, Class<? extends IPort> portClass, boolean add) {
    super(source, model, add);
    this.portName = portName;
    this.setPortClass(portClass);
  }

  /**
   * Gets the port name.
   * 
   * @return the port name
   */
  public final String getPortName() {
    return portName;
  }

  /**
   * @return the portClass
   */
  protected final Class<? extends IPort> getPortClass() {
    return portClass;
  }

  /**
   * @param portClass the portClass to set
   */
  protected final void setPortClass(Class<? extends IPort> portClass) {
    this.portClass = portClass;
  }

}// EOF
