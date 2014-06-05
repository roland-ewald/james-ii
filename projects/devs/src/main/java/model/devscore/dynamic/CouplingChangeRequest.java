/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;


import java.util.NoSuchElementException;

import org.jamesii.core.model.InvalidModelException;
import org.jamesii.core.util.misc.Pair;

import model.devscore.IBasicCoupledModel;
import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

// TODO: Auto-generated Javadoc
/**
 * The Class CouplingChangeRequest.
 * 
 * @author Jan Himmelspach
 */
public class CouplingChangeRequest<M extends IDynamicCoupledModel & IBasicCoupledModel>
    extends BasicCouplingChangeRequest<M> {

  /** Name of the output model. */
  private String fromModelString = null;

  /** Name of the output port. */
  private String fromPortString = null;

  // Alternative text representation

  /** The target model. */
  private IBasicDEVSModel model2;

  /** The target port. */
  private IPort port2;

  /** Name of the destination model. */
  private String toModelString = null;

  /** Name of the destination port. */
  private String toPortString = null;

  /**
   * Creates a new coupling object - from the given.
   * 
   * @param source
   *          the source of the coupling
   * @param model1
   *          the new source of the coupling
   * @param port1
   *          the new sourceport of the coupling
   * @param model2
   *          the new destination model
   * @param port2
   *          the new destination port
   * @param add
   *          if true it is an add request
   * @param context
   *          the context
   * 
   * @model1,
   * @port1 to
   * @model2,
   * @port2
   */
  public CouplingChangeRequest(IBasicDEVSModel source, IBasicDEVSModel context,
      IBasicDEVSModel model1, IPort port1, IBasicDEVSModel model2, IPort port2,
      boolean add) {
    super(source, context, model1, port1, add);
    this.model2 = model2;
    this.port2 = port2;

    this.fromModelString = model1.getName();
    this.fromPortString = port1.getName();
    this.toModelString = model2.getName();
    this.toPortString = port2.getName();
  }

  /**
   * Creates a new coupling object - from the given.
   * 
   * @param source
   *          the source of the coupling
   * @param model1
   *          the new source of the coupling
   * @param port1
   *          the new sourceport of the coupling
   * @param model2
   *          the new destination model
   * @param port2
   *          the new destination port
   * @param add
   *          if true it is an add request
   * 
   * @model1,
   * @port1 to
   * @model2,
   * @port2
   */
  public CouplingChangeRequest(IBasicDEVSModel source, IBasicDEVSModel model1,
      IPort port1, IBasicDEVSModel model2, IPort port2, boolean add) {
    super(source, model1, port1, add);
    this.model2 = model2;
    this.port2 = port2;

    this.fromModelString = model1.getName();
    this.fromPortString = port1.getName();
    this.toModelString = model2.getName();
    this.toPortString = port2.getName();
  }

  /**
   * Instantiates a new coupling change request.
   * 
   * @param source
   *          the source
   * @param context
   *          the context
   * @param fromModel
   *          the from model
   * @param fromPort
   *          the from port
   * @param toModel
   *          the to model
   * @param toPort
   *          the to port
   * @param add
   *          the add
   */
  public CouplingChangeRequest(IBasicDEVSModel source, IBasicDEVSModel context,
      String fromModel, String fromPort, String toModel, String toPort,
      boolean add) {
    super(source, context, null, null, add);
    this.fromModelString = fromModel;
    this.fromPortString = fromPort;
    this.toModelString = toModel;
    this.toPortString = toPort;
  }

  /**
   * Get name of source model.
   * 
   * @return the from model name
   */
  public String getFromModelName() {
    return this.fromModelString;
  }

  /**
   * Get name of source port.
   * 
   * @return the from port name
   */
  public String getFromPortName() {
    return this.fromPortString;
  }

  /**
   * Returns a pointer to the drain/target model.
   * 
   * @return reference to target model
   */
  public IBasicDEVSModel getModel2() {
    return model2;
  }

  /**
   * Returns a pointer to the drain/target port.
   * 
   * @return pointer to the target port
   */
  public IPort getPort2() {
    return port2;
  }

  /**
   * Get name of destination model.
   * 
   * @return the to model name
   */
  public String getToModelName() {
    return this.toModelString;
  }

  /**
   * Get name of destination port.
   * 
   * @return the to port name
   */
  public String getToPortName() {
    return this.toPortString;
  }

  /**
   * Returns true if the.
   * 
   * @param model
   *          to be compared to model2
   * 
   * @return true if the model is model2
   * 
   * @model is the model2 (that's the drain) of the coupling
   */
  public boolean isModel2(IBasicDEVSModel model) {
    return (model.equals(model2));
  }

  /**
   * Checks whether the parameters cmodel and port are non null. If they are
   * null the method tries to retrieve the objects from the coupled model
   * passed. If this fails the method will throw an exception.
   * 
   * @param cmb
   * @param cmodel
   * @param port
   * @return
   */
  private Pair<IBasicDEVSModel, IPort> getModelAndOutPort(
      IBasicCoupledModel cmb, IBasicDEVSModel cmodel, String modelString,
      IPort port, String portString) {

    Pair<IBasicDEVSModel, IPort> result =
        new Pair<>(cmodel, port);

    if (cmodel == null) {

      result.setFirstValue(cmb.getModel(modelString));

      if (result.getFirstValue() == null) {
        throw new NoSuchElementException(" Model '" + cmb.getName()
            + "' does not have a submodel '" + modelString + "'");
      }

      result.setSecondValue(result.getFirstValue().getOutPort(portString));

      if (result.getSecondValue() == null) {
        throw new NoSuchElementException(" Model '" + modelString
            + "' does not have an outgoing port '" + portString + "'");
      }

      // System.out.println("New coupling to:" + toModelString);

    }
    return result;
  }

  /**
   * Checks whether the parameters cmodel and port are non null. If they are
   * null the method tries to retrieve the objects from the coupled model
   * passed. If this fails the method will throw an exception.
   * 
   * @param cmb
   * @param cmodel
   * @param port
   * @return
   */
  private Pair<IBasicDEVSModel, IPort> getModelAndInPort(
      IBasicCoupledModel cmb, IBasicDEVSModel cmodel, String modelString,
      IPort port, String portString) {

    Pair<IBasicDEVSModel, IPort> result =
        new Pair<>(cmodel, port);

    if (cmodel == null) {

      result.setFirstValue(cmb.getModel(modelString));

      if (result.getFirstValue() == null) {
        throw new NoSuchElementException(" Model '" + cmb.getName()
            + "' does not have a submodel '" + modelString + "'");
      }

      result.setSecondValue(result.getFirstValue().getInPort(portString));

      if (result.getSecondValue() == null) {
        throw new NoSuchElementException(" Model '" + modelString
            + "' does not have an ingoing port '" + portString + "'");
      }

      // System.out.println("New coupling to:" + toModelString);

    }
    return result;
  }

  /**
   * Applies the change request to the given model.
   * 
   * @param model
   *          - the model must be implement the IDynamicCoupledModel interface
   */
  @Override
  public void modifyModel(M model) {

    try {

      IBasicDEVSModel fromModel = getModel1();
      IPort fromPort = getPort1();
      IBasicDEVSModel toModel = getModel2();
      IPort toPort = getPort2();

      // Lookup models if needed

      Pair<IBasicDEVSModel, IPort> src =
          getModelAndOutPort(model, fromModel, fromModelString, fromPort,
              fromPortString);
      Pair<IBasicDEVSModel, IPort> tar =
          getModelAndInPort(model, toModel, toModelString, toPort, toPortString);

      if (isAddRequest()) {
        model.addCoupling(src.getFirstValue(), src.getSecondValue(),
            tar.getFirstValue(), tar.getSecondValue());
      } else {
        model.removeCoupling(src.getFirstValue(), src.getSecondValue(),
            tar.getFirstValue(), tar.getSecondValue());
      }
    } catch (NoSuchElementException nsee) {
      throw new InvalidModelException(
          "On trying to add/remove a coupling the following error occured: In the model "
              + model.getFullName() + " the following " + nsee.getMessage(), nsee);
    }
  }

  @Override
  public String toString() {

    return super.toString() + "\n  Coupling target:\n   Model: "
        + model2.getFullName() + " Port: " + port2.getName();

  }

} // EOF
