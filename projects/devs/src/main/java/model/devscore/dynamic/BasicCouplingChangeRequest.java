/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

import org.jamesii.core.model.IModel;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

/**
 * The Class BasicCouplingChangeRequest.
 * 
 * @author Jan Himmelspach To change the template for this generated type
 *         comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public abstract class BasicCouplingChangeRequest<M extends IModel> extends
    ChangeRequest<M> {

  /**
   * Autodetect the context to/from which this coupling shall be added/removed.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * 
   * @return the context
   */
  private static IBasicDEVSModel getContext(IBasicDEVSModel model1, IPort port1) {
    IBasicDEVSModel con = null;

    if (model1 instanceof IDynamicAtomicModel) {
      con = model1.getParent();
    } else {
      if (model1.hasInPort(port1)) {
        // external input coupling
        con = model1;
      } else {
        // internal coupling
        con = model1.getParent();
      }
    }

    // if (con == null)
    // it was not possible to autodetect the context, model1
    // has not been added to a context so far (getParent) has returned null

    return con;
  }

  /** Add- or remove-request. */
  private boolean add;

  /** The context. */
  private IBasicDEVSModel context;

  /** The source model. */
  private IBasicDEVSModel model1;

  /** The source port. */
  private IPort port1;

  /**
   * The Constructor.
   * 
   * @param context
   *          the context
   * @param source
   *          the source
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param add
   *          the add
   */
  public BasicCouplingChangeRequest(IBasicDEVSModel source,
      IBasicDEVSModel context, IBasicDEVSModel model1, IPort port1, boolean add) {
    super(source);
    this.context = context;
    this.model1 = model1;
    this.port1 = port1;
    this.add = add;
  }

  /**
   * The Constructor.
   * 
   * @param source
   *          the source
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param add
   *          the add
   */
  public BasicCouplingChangeRequest(IBasicDEVSModel source,
      IBasicDEVSModel model1, IPort port1, boolean add) {
    super(source);
    this.context = getContext(model1, port1);
    this.model1 = model1;
    this.port1 = port1;
    this.add = add;
  }

  /**
   * Returns the context, the model, in which the change has to take place.
   * 
   * @return the context
   */
  public IBasicDEVSModel getContext() {
    return context;
  }

  /**
   * Returns a pointer to the source model.
   * 
   * @return a pointer to the source model
   */
  public IBasicDEVSModel getModel1() {
    return model1;
  }

  /**
   * Returns a pointer to the source port.
   * 
   * @return source port
   */
  public IPort getPort1() {
    return port1;
  }

  /**
   * Is this an add request.
   * 
   * @return true if an add request
   */
  public boolean isAddRequest() {
    return add;
  }

  /**
   * Returns true if the.
   * 
   * @param model
   *          to be compared to model1
   * 
   * @return true if the model is model1
   * 
   * @model is the model1 (that's the source) of the coupling
   */
  public boolean isModel1(IBasicDEVSModel model) {
    return (model.equals(model1));
  }

  /**
   * Is this a remove request?.
   * 
   * @return true if an remove request
   */
  public boolean isRemoveRequest() {
    return !add;
  }

  @Override
  public String toString() {

    return super.toString() + "\n  Add: " + add
        + "\n  Coupling source:\n   Model: " + model1.getFullName() + " Port: "
        + port1.getName();

  }

}
