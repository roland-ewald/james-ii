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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import org.jamesii.core.base.Entity;

import model.devscore.IBasicDEVSModel;
import model.devscore.NotUniqueException;

/**
 * The Class SetModelSet.
 * 
 * @author Jan Himmelspach
 */
public class SetModelSet extends Entity implements IModelSet {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2975737719592143671L;

  /** The models. */
  private Set<IBasicDEVSModel> models = new TreeSet<>();

  @Override
  public void addModel(IBasicDEVSModel model) {
    if (!models.add(model)) {
      throw new NotUniqueException();
    }
  }

  @Override
  public boolean contains(IBasicDEVSModel model) {
    return models.contains(model);
  }

  @Override
  public IBasicDEVSModel getModel(String name) {
    for (IBasicDEVSModel m : models) {
      if (m.getName().compareTo(name) == 0) {
        return m;
      }
    }
    return null;
  }

  @Override
  public List<IBasicDEVSModel> getModels(Class<?> classOfModelsToGet) {
    Iterator<IBasicDEVSModel> it = models.iterator();
    List<IBasicDEVSModel> result = new ArrayList<>();
    while (it.hasNext()) {
      IBasicDEVSModel m = it.next();
      if (m.getClass().isInstance(classOfModelsToGet)) {
        result.add(m);
      }
    }
    return result;
  }

  @Override
  public Iterator<IBasicDEVSModel> iterator() {
    return models.iterator();
  }

  @Override
  public Iterator<IBasicDEVSModel> privateIterator() {
    return models.iterator();
  }

  @Override
  public void removeModel(IBasicDEVSModel model) {
    if (!models.remove(model)) {
      throw new NoSuchElementException();
    }
  }

  @Override
  public void removeModel(String name) {
    Iterator<IBasicDEVSModel> it = models.iterator();
    while (it.hasNext()) {
      IBasicDEVSModel m = it.next();
      if (m.getFullName().compareTo(name) == 0) {
        it.remove();
        break;
      }
    }

  }

  @Override
  public void replace(IBasicDEVSModel currentModel, IBasicDEVSModel newModel) {
    removeModel(currentModel);
    addModel(newModel);
  }

  @Override
  public int size() {
    return models.size();
  }

  @Override
  public IBasicDEVSModel get(int index) {
    int i = index;
    
    for (IBasicDEVSModel m : models) {
      i--;
      if (i == 0) {
        return m;
      }
    }

    return null;
  }

}
