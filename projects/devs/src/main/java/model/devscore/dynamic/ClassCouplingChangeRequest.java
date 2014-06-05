/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

import org.jamesii.core.util.collection.list.ISelector;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

// TODO: Auto-generated Javadoc
/**
 * The Class ClassCouplingChangeRequest.
 * 
 * @author Christian Ober
 */
public class ClassCouplingChangeRequest extends BasicCouplingChangeRequest<IDynamicCoupledModel> {

  /** The target class. */
  private Class<IBasicDEVSModel> model2;

  /** The name of the target port. */
  private String port2;

  /** The associated Selector. */
  private ISelector<IBasicDEVSModel> selector;

  /**
   * Creates a new ClassCoupling object - from the given model1, port1 to
   * model2, port2. If <code>null</code>s are set, this parameters will not be
   * modified.
   * 
   * @param source
   *          the source of the coupling
   * @param context
   *          the context
   * @param model1
   *          the new source of the coupling
   * @param port1
   *          the new sourceport of the coupling
   * @param model2
   *          the new destination class
   * @param port2
   *          the new destination port
   * @param add
   *          if true it is an add request else remove
   * @param selector
   *          the new selector
   */
  public ClassCouplingChangeRequest(IBasicDEVSModel source,
      IBasicDEVSModel context, IBasicDEVSModel model1, IPort port1,
      Class<IBasicDEVSModel> model2, String port2,
      ISelector<IBasicDEVSModel> selector, boolean add) {
    super(source, context, model1, port1, add);
    this.model2 = model2;
    this.port2 = port2;
    this.selector = selector;
  }

  /**
   * Creates a new ClassCoupling object - from the given model1, port1 to
   * model2, port2. If <code>null</code>s are set, this parameters will not be
   * modified.
   * 
   * @param source
   *          the source of the coupling
   * @param model1
   *          the new source of the coupling
   * @param port1
   *          the new sourceport of the coupling
   * @param model2
   *          the new destination class
   * @param port2
   *          the new destination port
   * @param add
   *          if true it is an add request else remove
   * @param selector
   *          the new selector
   */
  public ClassCouplingChangeRequest(IBasicDEVSModel source,
      IBasicDEVSModel model1, IPort port1, Class<IBasicDEVSModel> model2,
      String port2, ISelector<IBasicDEVSModel> selector, boolean add) {
    super(source, model1, port1, add);
    this.model2 = model2;
    this.port2 = port2;
    this.selector = selector;
  }

  /**
   * Returns a pointer to the drain/target model.
   * 
   * @return reference to target model
   */
  public Class<IBasicDEVSModel> getModel2() {
    return model2;
  }

  /**
   * Returns a pointer to the drain/target port.
   * 
   * @return pointer to the target port
   */
  public String getPort2() {
    return port2;
  }

  /**
   * Retrieves the currently set Selector.
   * 
   * @return The current Selector.
   */
  public ISelector<IBasicDEVSModel> getSelector() {
    return this.selector;
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
  public boolean isModel2(Class<IBasicDEVSModel> model) {
    return model.equals(model2);
  }

  /**
   * Applies the change request to the given model.
   * 
   * @param model
   *          the model
   */
  @Override
  public void modifyModel(IDynamicCoupledModel model) {
    if (isAddRequest()) {
      model.addCoupling(getModel1(), getPort1(),
          getModel2(), getPort2(), selector);
    } else {
      model.removeCoupling(getModel1(), getPort1(),
          getModel2(), getPort2());
    }
  }

  /**
   * Sets the new Selector for the ClassCoupling to change.
   * 
   * @param selector
   *          The new Selector.
   */
  public void setSelector(ISelector<IBasicDEVSModel> selector) {
    this.selector = selector;
  }

} // EOF
