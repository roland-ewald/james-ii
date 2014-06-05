/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.models;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamesii.core.util.collection.ElementSet;

import model.devscore.IBasicDEVSModel;
import model.devscore.NotUniqueException;

/**
 * This model set is for experimental use only. The one in the "ModelSet" class
 * should be used for all productive experiments instead.
 * 
 * This IModelSet implementation has a very bad performance because most
 * operations are in O(n)!!!
 * 
 * @author Jan Himmelspach
 */
public class SimpleModelSet extends ElementSet<IBasicDEVSModel> implements
    IModelSet {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2902715745667298246L;

  @Override
  public void addModel(IBasicDEVSModel model) {
    if (contains(model)) {
      throw new NotUniqueException("");
    }
    add(model);
    setElementIterator(null);
  }

  @Override
  public boolean contains(IBasicDEVSModel model) {
    return getVelements().contains(model);
  }

  @Override
  public IBasicDEVSModel getModel(String name) {
    Iterator<IBasicDEVSModel> it = getVelements().iterator();
    while (it.hasNext()) {
      IBasicDEVSModel m = it.next();
      if (m.getName().compareTo(name) == 0) {
        return m;
      }
    }
    return null;
  }

  @Override
  public List<IBasicDEVSModel> getModels(Class<?> classOfModelsToGet) {
    List<IBasicDEVSModel> result = new ArrayList<>();
    for (IBasicDEVSModel m : getVelements()) {
      if (m.getClass().isInstance(classOfModelsToGet)) {
        result.add(m);
      }
    }
    return result;
  }

  @Override
  public void removeModel(IBasicDEVSModel model) {
    this.getVelements().remove(model);
    setElementIterator(null);
  }

  @Override
  public void removeModel(String name) {
    Iterator<IBasicDEVSModel> it = getVelements().iterator();
    while (it.hasNext()) {
      IBasicDEVSModel m = it.next();
      if (m.getName().compareTo(name) == 0) {
        it.remove();
        break;
      }
    }
    setElementIterator(null);
  }

  @Override
  public void replace(IBasicDEVSModel currentModel, IBasicDEVSModel newModel) {
    removeModel(currentModel);
    addModel(newModel);
  }

}
