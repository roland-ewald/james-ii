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
 * The Class InPortChangeRequest.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */

public class InPortChangeRequest extends InOutPortChangeRequest {

  /**
   * Creates a new port change request with the given parameters.
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
  public InPortChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model,
      IPort port, boolean add) {
    super(source, model, port, add);
  }

  /**
   * Creates a new port change request with the given parameters.
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
  public InPortChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model,
      String portName, Class<IPort> portClass, boolean add) {
    super(source, model, portName, portClass, add);
  }
  
  @Override
  public void modifyModel(IBasicDEVSModel theModel) {
    if (isAddRequest()) {
      ((BasicDEVSModel) theModel).addInPort(getPort());
    } else {
      ((BasicDEVSModel) theModel).removeInPort(getPort());
    }
  }
}
