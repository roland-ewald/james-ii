/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model;

import java.lang.reflect.Field;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.base.NamedEntity;

/**
 * Basic (executable) model class. This class implements the {@link IModel}
 * interface, and thus can be used as base class for all executable model
 * implementations for the framework. <br>
 * In contrast to other m&s software products, including the old JAMES, this
 * model does not provide any formalism specific attributes or methods.
 * Everything special (specific to a formalism / language) has to be realized in
 * extra interfaces + classes.
 * 
 * In addition to executable models the software is aware of symbolic models.
 * 
 * @see org.jamesii.core.model.symbolic
 * 
 * @author Jan Himmelspach
 * @author Mathias Röhl
 */
public abstract class Model extends NamedEntity implements IModel {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3446275034532589885L;

  /**
   * Create a model instance.
   * 
   * @param qualifiedClassName
   *          the qualified class name
   * @param instanceName
   *          the instance name
   * 
   * @return the i model
   */
  @SuppressWarnings("unchecked")
  public static IModel instantiate(String qualifiedClassName,
      String instanceName) {

    try {
      Class<Model> instanceClass =
          (Class<Model>) Class.forName(qualifiedClassName);
      // Class params[] = {String.class};
      Model m = instanceClass.newInstance();
      m.setName(instanceName);
      /*
       * Constructor instanceConst = instanceClass.getConstructor(params);
       * Object[] paramInstances = {instanceName}; IExecutableModel instanceObj
       * = (Model) instanceConst.newInstance(paramInstances); return
       * instanceObj;
       */
      return m;
    } catch (ClassNotFoundException | InstantiationException
        | IllegalAccessException e) {
      SimSystem.report(e);
    }
    /*
     * catch (InvocationTargetException e) { SimSystem.report (e); }
     */

    return null;
  }

  /**
   * Auxiliary function to select model parameters.
   * 
   * 
   * 
   * @param parameters
   *          the parameters
   * @param key
   *          the key
   * @param defaultValue
   *          the default value
   * 
   * @param <X>
   *          the type of the value to be returned
   * 
   * @return a value, either from the passed map (if the key exists), otherwise
   *         the default value is returned.
   */
  @SuppressWarnings("unchecked")
  public static <X> X getParam(Map<String, ?> parameters, String key,
      X defaultValue) {
    return (X) (parameters.get(key) != null ? parameters.get(key)
        : defaultValue);
  }

  /**
   * Attribute containing an access restriction block (e.g., dont't allow
   * operation Y if X is true ... This attribute should only be manipulated by a
   * processor / simulation algorithm and should not be directly worked with /
   * set by a modeler.
   */
  private AccessRestriction accessRestriction;

  /**
   * Instantiates a new model.
   */
  public Model() {
    super();
  }

  /**
   * Instantiates a new model with a given name.
   * 
   * @param name
   *          of the model
   */
  public Model(String name) {
    super(name);
  }

  /**
   * Override this method an return a value != 0 if this model should be placed
   * on another resource In later version this in value could be used for a
   * first initialization for a dynamic load distribution algorithm (a modeler
   * estimates which time a particular model will need).
   * 
   * @return always 0
   */
  public int demandsResource() {
    return 0;
  }

  /**
   * Gets the value of the attribute of this instance specified by paramName.
   * This method can be used to read model instance attributes for which no
   * getter is implemented.
   * 
   * @param paramName
   *          the attributes name
   * 
   * @return the value of the attribute "paramName"
   */
  public Object getParam(String paramName) {
    try {
      Field toGet = getClass().getField(paramName);
      return toGet.get(this);
    } catch (Exception e) {
      Entity.report(e);
    }
    return null;
  }

  /**
   * This method should be overridden to initialize a model.
   */
  @Override
  public void init() {
  }

  /**
   * Sets the access restriction.
   * 
   * @param accessRestriction
   * 
   */
  @Override
  public final void setAccessRestriction(AccessRestriction accessRestriction) {
    this.accessRestriction = accessRestriction;
  }

  /**
   * Sets the object's attribute specified by the given paramName. Thus the
   * paramValue has to be of the type of the attribute. Setting is done by using
   * java reflection. This method can be used to set model attributes for which
   * no setters exist.
   * 
   * @param paramName
   *          the attributes name
   * @param paramValue
   *          the value to be set for the attribute
   */
  public void setParam(String paramName, Object paramValue) {
    try {
      Field toSet = getClass().getField(paramName);
      toSet.set(this, paramValue);
    } catch (Exception e) {
      Entity.report(e);
    }
  }

  @Override
  public void cleanUp() {
    // by default we have nothing to clean up, however, this might be different
    // in descendant classes
  }

  /**
   * @return the accessRestriction
   */
  protected AccessRestriction getAccessRestriction() {
    return accessRestriction;
  }

}
