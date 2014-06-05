/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

import org.jamesii.core.util.collection.list.ISelector;

import model.devscore.IBasicDEVSModel;
import model.devscore.couplings.MultiCouplingTarget;
import model.devscore.couplings.MultiCouplingTargetList;
import model.devscore.ports.IPort;
import model.devscore.ports.Port;

// TODO: Auto-generated Javadoc
/**
 * The Class MultiCouplingChangeRequest.
 * 
 * @author Jan Himmelspach
 */
public class MultiCouplingChangeRequest extends
    BasicCouplingChangeRequest<IDynamicCoupledModel> {

  /**
   * Change- or Add/Remove-request.<br>
   * (<code>true</code> if change-request, <code>false</code> if
   * add/remove-request)
   */
  private boolean change;

  /** Name of the MultiCoupling to add/remove/change. */
  private String ident;

  /** The target models. */
  private MultiCouplingTargetList model2;

  /** The associated Selector. */
  private ISelector<IBasicDEVSModel> selector;

  /**
   * Helper constant.
   */
  private static final String DUMMY = "dummy";

  /**
   * Creates a MultiCouplingChangeRequest to remove a MultiCoupling identified
   * by the name of the MultiCoupling.
   * 
   * @param source
   *          the source of the coupling
   * @param ident
   *          the identifying name for the to-remove-multicoupling.
   * @param context
   *          the context
   */
  public MultiCouplingChangeRequest(IBasicDEVSModel source,
      IBasicDEVSModel context, String ident) {
    super(source, context, source, new Port(DUMMY, Object.class), false);
    this.ident = ident;
    this.change = false;
  }

  /**
   * Creates a new MultiCoupling object for adding a complete MultiCoupling.
   * 
   * @param source
   *          the source of the coupling
   * @param model1
   *          the new source of the coupling
   * @param port1
   *          the new sourceport of the coupling
   * @param model2
   *          the new destination model
   * @param add
   *          if true it is an add request
   * @param selector
   *          the associated selector
   * @param ident
   *          the identifier of the multicoupling (name)
   * @param context
   *          the context
   */
  public MultiCouplingChangeRequest(IBasicDEVSModel source,
      IBasicDEVSModel context, String ident, IBasicDEVSModel model1,
      IPort port1, MultiCouplingTargetList model2,
      ISelector<IBasicDEVSModel> selector, boolean add) {
    super(source, context, model1, port1, add);
    this.ident = ident;
    this.model2 = model2;
    this.selector = selector;
    this.change = false;
  }

  /**
   * Creates a MultiCouplingChangeRequest for changing an existing MultiCoupling
   * identified by the name. If <code>null</code>s are set, this parameters will
   * not be modified.
   * 
   * @param source
   *          the source of the coupling
   * @param targetPort
   *          the targetport of the model to add to targetlist
   * @param targetModel
   *          the new destination model
   * @param add
   *          if true it is an add request
   * @param ident
   *          the identifier of the multicoupling (name)
   * @param selector
   *          the new Selector
   * @param context
   *          the context
   */
  public MultiCouplingChangeRequest(IBasicDEVSModel source,
      IBasicDEVSModel context, String ident, IBasicDEVSModel targetModel,
      String targetPort, ISelector<IBasicDEVSModel> selector, boolean add) {
    super(source, context, source, new Port(DUMMY, Object.class), add);
    this.ident = ident;
    // this.model1 = source;
    this.model2 = new MultiCouplingTargetList();
    this.model2.addTarget(new MultiCouplingTarget(targetModel, targetPort));
    this.change = true;
    this.selector = selector;
  }

  /**
   * Creates a MultiCouplingChangeRequest to remove a MultiCoupling identified
   * by the name of the MultiCoupling.
   * 
   * @param source
   *          the source of the coupling
   * @param ident
   *          the identifying name for the to-remove-multicoupling.
   */
  public MultiCouplingChangeRequest(IBasicDEVSModel source, String ident) {
    super(source, source, new Port(DUMMY, Object.class), false);
    this.ident = ident;
    this.change = false;
  }

  /**
   * Creates a new MultiCoupling object for adding a complete MultiCoupling.
   * 
   * @param source
   *          the source of the coupling
   * @param model1
   *          the new source of the coupling
   * @param port1
   *          the new sourceport of the coupling
   * @param model2
   *          the new destination model
   * @param add
   *          if true it is an add request
   * @param selector
   *          the associated selector
   * @param ident
   *          the identifier of the multicoupling (name)
   */
  public MultiCouplingChangeRequest(IBasicDEVSModel source, String ident,
      IBasicDEVSModel model1, IPort port1, MultiCouplingTargetList model2,
      ISelector<IBasicDEVSModel> selector, boolean add) {
    super(source, model1, port1, add);
    this.ident = ident;
    this.model2 = model2;
    this.selector = selector;
    this.change = false;
  }

  /**
   * Creates a MultiCouplingChangeRequest for changing an existing MultiCoupling
   * identified by the name. If <code>null</code>s are set, this parameters will
   * not be modified.
   * 
   * @param source
   *          the source of the coupling
   * @param targetPort
   *          the targetport of the model to add to targetlist
   * @param targetModel
   *          the new destination model
   * @param add
   *          if true it is an add request
   * @param ident
   *          the identifier of the multicoupling (name)
   * @param selector
   *          the new Selector
   */
  public MultiCouplingChangeRequest(IBasicDEVSModel source, String ident,
      IBasicDEVSModel targetModel, String targetPort,
      ISelector<IBasicDEVSModel> selector, boolean add) {
    super(source, source, new Port(DUMMY, Object.class), add);
    this.ident = ident;
    // this.model1 = source;
    this.model2 = new MultiCouplingTargetList();
    this.model2.addTarget(new MultiCouplingTarget(targetModel, targetPort));
    this.change = true;
    this.selector = selector;
  }

  /**
   * Add remove the given target from the specified multi coupling.
   * 
   * @param source
   *          the source
   * @param ident
   *          the ident
   * @param target
   *          the target
   * @param add
   *          the add
   */
  public MultiCouplingChangeRequest(IBasicDEVSModel source, String ident,
      MultiCouplingTarget target, boolean add) {
    super(source, source, new Port(DUMMY, Object.class), add);
    this.ident = ident;
    this.change = true;
    this.model2 = new MultiCouplingTargetList();
    model2.addTarget(target);
  }

  /**
   * Add/remove the given targets from the specified multi coupling.
   * 
   * @param source
   *          the source
   * @param ident
   *          the ident
   * @param targets
   *          the targets
   * @param add
   *          the add
   */
  public MultiCouplingChangeRequest(IBasicDEVSModel source, String ident,
      MultiCouplingTargetList targets, boolean add) {
    super(source, source, new Port(DUMMY, Object.class), add);
    this.ident = ident;
    this.change = true;
    this.model2 = targets;
  }

  /**
   * Retrieves identifier.
   * 
   * @return the identifier
   */
  public String getIdentifier() {
    return this.ident;
  }

  /**
   * Returns a pointer to the drain/target model.
   * 
   * @return reference to target model
   */
  public MultiCouplingTargetList getModel2() {
    return model2;
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
   * Is this a add/remove request?.
   * 
   * @return true if a add/remove request for a complete MultiCoupling
   */
  public boolean isCreateOrRemoveRequest() {
    return !change;
  }

  /**
   * Is this a modification request?.
   * 
   * @return true if a modification request
   */
  public boolean isModificationRequest() {
    return change;
  }

  /**
   * Applies the change request to the given model.
   * 
   * @param model
   *          the model
   */
  @Override
  public void modifyModel(IDynamicCoupledModel model) {

    if (isCreateOrRemoveRequest()) {

      if (isAddRequest()) {
        model.addCoupling(getModel1(), getPort1(),
            getModel2(), ident, selector);
      } else {
        model.removeCoupling(ident);
      }
    } else { // change an exsting coupling
      if (isAddRequest()) {
        model.addToCoupling(ident, getModel2());
      } else {
        model.removeFromCoupling(ident, getModel2());
      }
    }
  }

  /**
   * Sets the new Selector for the MultiCoupling to change.
   * 
   * @param selector
   *          The new Selector.
   */
  public void setSelector(ISelector<IBasicDEVSModel> selector) {
    this.selector = selector;
  }

} // EOF
