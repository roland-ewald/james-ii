/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;


import java.util.Iterator;

import org.jamesii.core.util.collection.IElementSet;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

/**
 * The Interface ICouplingSet. Network / coupled models in the DEVS world
 * typically require different sets of couplings. This interface here should be
 * used to define the corresponding attributes, and to access these. By doing so
 * different coupling sets can be used, and thus a coupling set implementation
 * providing a good performance for the model to be simulated can be selected.
 * 
 * @author Jan Himmelspach
 * 
 * @see model.devscore.couplings.plugintype
 */
public interface ICouplingSet extends IElementSet<BasicCoupling> {

  /**
   * Adds the coupling to the set of couplings. This method should throw a
   * {@link model.devscore.NotUniqueException} if there is already a coupling with the same
   * characteristics (e.g., same start and end as another coupling).
   * 
   * @param coupling
   *          the coupling to be added, can be any extension of "BaseCoupling"
   *          (depending on the formalism using an implementation of this
   *          interface)
   * 
   * @throws model.devscore.NotUniqueException
   *           the not unique exception will be thrown if the coupling to be
   *           added is already in the <b>set</b> if couplings
   */
  void addCoupling(BasicCoupling coupling);

  /**
   * Check whether this set already contains the passed coupling or not.
   * 
   * @param coupling
   *          the coupling to check whether it is in the set or not
   * 
   * @return true, if the coupling is already in the set
   */

  boolean contains(BasicCoupling coupling);

  /**
   * Check whether this set already contains the passed "coupling identifier" or
   * not. Each coupling has to return a unique identifier which has to be used
   * here.
   * 
   * @param identifier
   *          the identifier identifying a coupling
   * 
   * @return true, if the coupling is already in the set
   */
  boolean contains(String identifier);

  /**
   * Gets the coupling identified by the passed identifier.
   * 
   * @param identifier
   *          the name
   * 
   * @return the coupling
   */
  BasicCoupling getCoupling(String identifier);

  /**
   * Return an iterator for all couplings starting at the model given.
   * 
   * @param model
   *          the model which is used as starting node of the couplings
   * 
   * @return the couplings iterator
   */
  Iterator<BasicCoupling> getCouplingsIterator(IBasicDEVSModel model);

  /**
   * Removes the coupling.
   * 
   * @param coupling
   *          the coupling
   * 
   * @throws java.util.NoSuchElementException
   *           the no such element exception
   */
  void removeCoupling(BasicCoupling coupling);

  /**
   * Removes the coupling.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   * 
   * @throws java.util.NoSuchElementException
   *           the no such element exception
   */
  void removeCoupling(IBasicDEVSModel model1, IPort port1,
      Class<IBasicDEVSModel> model2, IPort port2);

  /**
   * Removes the coupling.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   * 
   * @throws java.util.NoSuchElementException
   *           the no such element exception
   */
  void removeCoupling(IBasicDEVSModel model1, IPort port1,
      IBasicDEVSModel model2, IPort port2);

  /**
   * Removes the coupling.
   * 
   * @param identifier
   *          the coupling name
   * 
   * @throws java.util.NoSuchElementException
   *           the no such element exception
   */
  void removeCoupling(String identifier);

  /**
   * Removes all couplings (all targets in a x:n coupling) in which this model
   * is involved.
   * 
   * @param model
   *          the model of which all couplings shall be removed
   */
  void removeCouplings(IBasicDEVSModel model);

  /**
   * Replace the currentModel by the newModel. This method is useful in all the
   * cases where a model is replaced by a proxy, thus this method is essential
   * for a distributed model setup.
   * 
   * @param currentModel
   *          the current model
   * @param newModel
   *          the new model
   */
  void replace(IBasicDEVSModel currentModel, IBasicDEVSModel newModel);

}
