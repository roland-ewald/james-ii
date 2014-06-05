/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;

import java.util.Iterator;
import java.util.List;

import model.devscore.couplings.BasicCoupling;

/**
 * The Interface IBasicCoupledModel.
 * 
 * @author Jan Himmelspach
 */
public interface IBasicCoupledModel extends IBasicDEVSModel {

  /**
   * Returns an iterator of all external input couplings.
   * 
   * @return reference to eic iterator
   */
  Iterator<BasicCoupling> getEICIterator();

  /**
   * Gets the "external input coupling" iterator
   * 
   * Returns an iterator for iterating through the list of external in couplings
   * of the given model. The return value might not be a private iterator: there
   * should be only one process using this iterator per time. A subsequent call
   * might just reset the existing iterator!! (see
   * {@link org.jamesii.core.util.collection.list.ReusableListIterator} for more
   * details on the iterator which might be used by an implementation here.)
   * 
   * @param model
   *          the model
   * 
   * @return the EIC iterator
   */
  Iterator<BasicCoupling> getEICIterator(IBasicDEVSModel model);

  /**
   * Returns an iterator for all external output couplings.
   * 
   * @return iterator of EOC
   */
  Iterator<BasicCoupling> getEOCIterator();

  /**
   * Gets the eoc iterator.
   * 
   * @param model
   *          the model
   * 
   * @return the EOC iterator
   */
  Iterator<BasicCoupling> getEOCIterator(IBasicDEVSModel model);

  /**
   * Returns an iterator of all internal couplings.
   * 
   * @return refernce to ic iterator
   */
  Iterator<BasicCoupling> getICIterator();

  /**
   * Gets the ic iterator.
   * 
   * @param model
   *          the model
   * 
   * @return the IC iterator
   */
  Iterator<BasicCoupling> getICIterator(IBasicDEVSModel model);

  /**
   * Get model with a specified name.
   * 
   * @param name
   *          of the model which is searched
   * 
   * @return refernce to the model if it exists otherwise null (model's short
   *         name)
   */
  IBasicDEVSModel getModel(String name);

  /**
   * Returns the number of all submodels of this model.
   * 
   * @return number of all direct submodels
   */
  int getSubModelCount();

  /**
   * Gets the sub model at the given index. Please note that not all
   * implementations of model *sets* need to have a fixed and constant order.
   * 
   * @param index
   *          the index
   * 
   * @return the sub model
   */
  IBasicDEVSModel getSubModel(int index);

  /**
   * Returns an iterator for all submodels.
   * 
   * @return reference to the sub model iterator
   */
  Iterator<IBasicDEVSModel> getSubModelIterator();

  /**
   * Return all sub models of the given class.
   * 
   * @param classOfModelsToGet
   *          the class of models to get
   * 
   * @return reference to a vector ...
   */
  List<IBasicDEVSModel> getSubmodelsByClass(Class<?> classOfModelsToGet);

  /**
   * Update model.
   * 
   * @param present
   *          the present
   * @param replace
   *          the replace
   * 
   *          TODO
   */
  void updateModel(IBasicDEVSModel present, IBasicDEVSModel replace);

} // EOF
