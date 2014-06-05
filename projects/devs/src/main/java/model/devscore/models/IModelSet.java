/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.models;


import java.util.List;
import java.util.NoSuchElementException;

import org.jamesii.core.util.collection.IElementSet;

import model.devscore.IBasicDEVSModel;
import model.devscore.NotUniqueException;

/**
 * The Interface IModelSet.
 * 
 * Defines a set of child models of a coupled/network DEVS model. It is a
 * <b>set</b> implementation in the mathematical sense, i.e., elements can only
 * be in here once.
 * 
 * @author Jan Himmelspach
 */
public interface IModelSet extends IElementSet<IBasicDEVSModel> {

  /**
   * Adds the model to this model set.
   * 
   * @param model
   *          the model to be added to set, should not already be in the set
   * 
   * @throws NotUniqueException
   *           the not unique exception will be thrown if the model is already
   *           in the <b>set</b> of models
   */
  void addModel(IBasicDEVSModel model);

  /**
   * Checks whether the model passed is contained in this model set.
   * 
   * @param model
   *          the model which could be in the set
   * 
   * @return true, if the model is in the set
   */
  boolean contains(IBasicDEVSModel model);

  /**
   * Gets the model with the given name.
   * 
   * @param name
   *          the name of the model to be retrieved
   * 
   * @returnThe retrieved IBasicDEVSModel, <code>null</code> if it does not
   *            exist.
   */
  IBasicDEVSModel getModel(String name);

  /**
   * Gets the model at the given index. Not: calling this method might be
   * expansive (if an order has to be created, there is no guarantee that the
   * order is constant at any subsequent access. Thus this method should only be
   * used while the simulation is not running or if you know what you are doing!
   * 
   * @param index
   *          the index
   * 
   * @return the i basic devs model
   */
  IBasicDEVSModel get(int index);

  /**
   * Gets the models which are of the class passed.
   * 
   * @param classOfModelsToGet
   *          the class of the models to get
   * 
   * @return the models of the class, the list may be empty
   */
  List<IBasicDEVSModel> getModels(Class<?> classOfModelsToGet);

  /**
   * Removes the model the model passed. If it is not in the set an exception
   * will be thrown.
   * 
   * @param model
   *          the model to be removed
   * 
   * @throws NoSuchElementException
   *           the no such element exception is thrown if the model cannot be
   *           removed from the model set because it is not there
   */
  void removeModel(IBasicDEVSModel model);

  /**
   * Removes the model with the given name. May throw a
   * {@link java.util.NoSuchElementException} if the model does not exist in the
   * set.
   * 
   * @param name
   *          the name of the model to be removed
   */
  void removeModel(String name);

  /**
   * Replace the current model entry with new model entry. This method may be
   * used by any migration / load- balancing mechanism to replace a model with
   * another one (e.g., with a proxy).<br>
   * Any two models passed in here should represent the same "physical" model
   * part, i.e., either the current model or the new model are at least a proxy.
   * 
   * @param currentModel
   *          the model currently in the set
   * @param newModel
   *          the new model which shall be used instead of the current one
   */
  void replace(IBasicDEVSModel currentModel, IBasicDEVSModel newModel);

}
