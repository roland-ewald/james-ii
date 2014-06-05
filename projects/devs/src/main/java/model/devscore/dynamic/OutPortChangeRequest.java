/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

import model.devscore.BasicDEVSModel;
import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

// TODO: Auto-generated Javadoc
/**
 * The Class OutPortChangeRequest.
 * 
 * @author Jan Himmelspach
 */
public class OutPortChangeRequest extends InOutPortChangeRequest {

  /**
   * Creates a new port object.
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
  public OutPortChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model,
      IPort port, boolean add) {
    super(source, model, port, add);
  }

  /**
   * Creates a new port object.
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
  public OutPortChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model,
      String portName, Class<IPort> portClass, boolean add) {
    super(source, model, portName, portClass, add);
  }

  @Override
  public void modifyModel(IBasicDEVSModel theModel) {
    if (isAddRequest()) {
      ((BasicDEVSModel) theModel).addOutPort(getPort());
    } else {
      ((BasicDEVSModel) theModel).removeOutPort(getPort());
    }
  }

}
