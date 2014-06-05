/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.models;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jamesii.core.util.collection.ElementSet;

import model.devscore.IBasicDEVSModel;
import model.devscore.NotUniqueException;

/**
 * Special DEVS model set class. Stores submodels of a coupled model. This
 * implementation uses three lists alltogether:
 * <ul>
 * <li>a simple, array list based, one - for fast iteration over all sub models</li>
 * <li>a name - model mapping for fast searches</li>
 * <li>a model class - model mapping list for fast operations in combination
 * with class couplings</li>
 * </ul>
 * 
 * @author Jan Himmelspach
 * 
 * @author Christian Ober
 * 
 *         history 14.02.2004 Christian Ober added classMapping due to the
 *         introduction of ClassCouplings.
 * 
 */
public class ModelSet extends ElementSet<IBasicDEVSModel> implements IModelSet {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 2734797549350660176L;

  /**
   * This table contains tables of models, "sorted" and accessed by the class of
   * the models.
   */
  private Map<Class<?>, Map<String, IBasicDEVSModel>> classMappings =
      new HashMap<>();

  /**
   * Elements hashed by their names.
   */
  private Map<String, IBasicDEVSModel> helements =
      new HashMap<>();

  @Override
  public void addModel(IBasicDEVSModel model) {

    if (contains(model.getName())) {
      throw new NotUniqueException("Modelname already used " + model.getName());
    }
    getVelements().add(model);
    helements.put(model.getName(), model);

    // ////////////////////////////////////////////////////////////////////////
    // start of changes according the introduction of ClassCouplings
    // ////////////////////////////////////////////////////////////////////////

    Map<String, IBasicDEVSModel> modelTable =
        classMappings.get(model.getClass());
    if (modelTable != null) {
      // if modeltable is not null, enter new entry or overwrite old entry, to
      // avoid loss of performance by getting and comparing value(s)
      modelTable.put(model.getName(), model);
    } else {
      // create new hashtable, put new entry into it, containing the model and
      // enter the newly created hashtable into the classmappings-table
      modelTable = new HashMap<>();
      modelTable.put(model.getName(), model);
      classMappings.put(model.getClass(), modelTable);
    }

    // ////////////////////////////////////////////////////////////////////////
    // end of changes according the introduction of ClassCouplings
    // ////////////////////////////////////////////////////////////////////////

    setElementIterator(null);
    changed();

  }

  @Override
  public boolean contains(IBasicDEVSModel model) {

    return helements.containsKey(model.getName());

  }

  /**
   * Checks whether the model identified by the name exists in this ModelSet or
   * not.
   * 
   * @param name
   *          The name of the model to look for.
   * @return <code>true</code> if the model exists,<br>
   *         <code>false</code> else.
   */
  public boolean contains(String name) {
    return helements.containsKey(name);
  }

  @Override
  public IBasicDEVSModel getModel(String name) {
    return helements.get(name);
  }

  @Override
  public List<IBasicDEVSModel> getModels(Class<?> classOfModelsToGet) {
    List<IBasicDEVSModel> result = new ArrayList<>();
    Map<String, IBasicDEVSModel> modelTable =
        classMappings.get(classOfModelsToGet);
    if ((modelTable != null) && (modelTable.size() > 0)) {
      result = new ArrayList<>(modelTable.values());
    }
    return result;
  }

  @Override
  public void removeModel(IBasicDEVSModel model) {

    if (!getVelements().remove(model)) {
      throw new NoSuchElementException("Model " + model.getName()
          + " could not be removed!");
    }
    helements.remove(model.getName());

    // ////////////////////////////////////////////////////////////////////////
    // start of changes according the introduction of ClassCouplings
    // ////////////////////////////////////////////////////////////////////////

    Map<String, IBasicDEVSModel> modelTable =
        classMappings.get(model.getClass());
    // if a list of the given model's class exists
    if (modelTable != null) {
      // try to remove the model from the list
      if (modelTable.remove(model.getName()) == null) {
        // if the model could not be removed, throw exception
        throw new NoSuchElementException("Model " + model.getName()
            + " could not be removed from ClassMapping-Table! (1)");
      }
      // if the removal happened, check the size of the mappingtable
      if (modelTable.size() == 0) {
        classMappings.remove(model.getClass());
      }

    } else {
      throw new NoSuchElementException("Model " + model.getName()
          + " could not be removed from ClassMapping-Table! (2)");
    }

    // ////////////////////////////////////////////////////////////////////////
    // end of changes according the introduction of ClassCouplings
    // ////////////////////////////////////////////////////////////////////////

    setElementIterator(null);
    changed();

  }

  @Override
  public void removeModel(String name) {
    removeModel(helements.get(name));
  }

  @Override
  public void replace(IBasicDEVSModel currentModel, IBasicDEVSModel newModel) {

    removeModel(currentModel);
    addModel(newModel);

  }
}
