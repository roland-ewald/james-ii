/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;

import org.jamesii.core.model.IDynamicModel;
import org.jamesii.core.util.collection.list.ISelector;

import model.devscore.IBasicDEVSModel;
import model.devscore.couplings.MultiCouplingTarget;
import model.devscore.couplings.MultiCouplingTargetList;
import model.devscore.ports.IPort;

// TODO: Auto-generated Javadoc
/**
 * The Interface IDynamicCoupledModel. This interface has to be implemented by
 * all coupled models which support the dynDEVS formalism.
 * 
 * @author Jan Himmelspach
 */
public interface IDynamicCoupledModel extends IDynamicModel {

  /**
   * Add a class coupling with the given parameters.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   * @param selector
   *          the selector
   */
  void addCoupling(IBasicDEVSModel model1, IPort port1,
      Class<IBasicDEVSModel> model2, String port2,
      ISelector<IBasicDEVSModel> selector);

  /**
   * Adds a coupling from model1 port1 to model2 port2.
   * 
   * @param model1
   *          source of the coupling
   * @param port1
   *          source port of the coupling
   * @param model2
   *          sink of the coupling
   * @param port2
   *          sink port of the coupling
   */
  void addCoupling(IBasicDEVSModel model1, IPort port1, IBasicDEVSModel model2,
      IPort port2);

  /**
   * Add new multiple target coupling.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param name
   *          the name
   * @param selector
   *          the selector
   */
  void addCoupling(IBasicDEVSModel model1, IPort port1,
      MultiCouplingTargetList model2, String name,
      ISelector<IBasicDEVSModel> selector);

  /**
   * Add a new model to this coupled model.
   * 
   * @param model
   *          which is added
   */
  void addModel(IBasicDEVSModel model);

  /**
   * Adds the to coupling.
   * 
   * @param ident
   *          the ident
   * @param target
   *          the target
   */
  void addToCoupling(String ident, MultiCouplingTarget target);

  /**
   * Adds the to coupling.
   * 
   * @param ident
   *          the ident
   * @param targets
   *          the targets
   */
  void addToCoupling(String ident, MultiCouplingTargetList targets);

  /**
   * Remove the given model and all couplings it is a part of.
   * 
   * @param model
   *          the model
   */
  void removeCompleteModel(IBasicDEVSModel model);

  /**
   * Remove the class coupling matching the given parameters.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   */
  void removeCoupling(IBasicDEVSModel model1, IPort port1,
      Class<IBasicDEVSModel> model2, String port2);

  /**
   * Remove an existing coupling.
   * 
   * @param model1
   *          source of the coupling
   * @param port1
   *          source port of the coupling
   * @param model2
   *          sink of the coupling
   * @param port2
   *          sink port of the coupling
   */
  void removeCoupling(IBasicDEVSModel model1, IPort port1,
      IBasicDEVSModel model2, IPort port2);

  /**
   * Removes the coupling.
   * 
   * @param ident
   *          the ident
   */
  void removeCoupling(String ident);

  /**
   * Removes the from coupling.
   * 
   * @param ident
   *          the ident
   * @param target
   *          the target
   */
  void removeFromCoupling(String ident, MultiCouplingTarget target);

  /**
   * Removes the from coupling.
   * 
   * @param ident
   *          the ident
   * @param targets
   *          the targets
   */
  void removeFromCoupling(String ident, MultiCouplingTargetList targets);

  /**
   * Remove a model from this coupled model.
   * 
   * @param model
   *          which should be removed
   */
  void removeModel(IBasicDEVSModel model);

}
