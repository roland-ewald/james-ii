/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

import org.jamesii.core.util.exceptions.OperationNotSupportedException;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

// TODO: Auto-generated Javadoc
/**
 * The Class PortChangeRequest.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public class PortChangeRequest extends ChangeRequest<IBasicDEVSModel> {

  /** Flag whether the port should be added or removed. */
  private boolean add;

  /** The model to which the change should be applied. */
  private IBasicDEVSModel model;

  /** The port to be added/removed. */
  private IPort port;

  /**
   * Creates a new port for the given model.
   * 
   * @param source
   *          the source
   * @param model
   *          the model
   * @param add
   *          the add
   */
  public PortChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model,
      boolean add) {
    super(source);
    this.model = model;
    this.add = add;
  }

  /**
   * Creates a new port for the given model.
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
  public PortChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model,
      IPort port, boolean add) {
    super(source);
    this.model = model;
    this.port = port;
    this.add = add;
  }

  /**
   * Returns a reference to the source model.
   * 
   * @return reference to the source model
   */
  public IBasicDEVSModel getModel() {
    return model;
  }

  /**
   * Returns a reference to the source port.
   * 
   * @return reference to the source port
   */
  public IPort getPort() {
    return port;
  }

  /**
   * Returns true if this is an add request.
   * 
   * @return true if add request
   */
  public boolean isAddRequest() {
    return add;
  }

  /**
   * Returns true if the model is the source model.
   * 
   * @param theModel
   *          the the model
   * 
   * @return true if the model and the source model are identical
   */
  public boolean isModel(IBasicDEVSModel theModel) {
    return (this.getSource() == theModel);
  }

  /**
   * Returns true if a remove request.
   * 
   * @return true if remove request
   */
  public boolean isRemoveRequest() {
    return !add;
  }

  @Override
  public void modifyModel(IBasicDEVSModel theModel) {

    throw new OperationNotSupportedException

    ("Port change request: Modification of models (here " + theModel
        + ") is no supported, yet.");
    /*
     * if (add) { model.REOTEaddPort (); } else { model.REMOTEremovePort (); }
     */
  }

} // EOF
