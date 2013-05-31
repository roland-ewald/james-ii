/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.parameters;

import java.io.Serializable;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;

/**
 * Small helper class to encapsulate a factory and a parameter block for the
 * factory (the block can be used to create instances of the factory and it can
 * be used to create instances of the objects created by the factory - see below
 * for further details). This avoids the need to add two attributes per factory
 * needed in other classes.<br/>
 * The factory and the parameter block should fit to each other. This class
 * cannot and will not do any check for their compatibility.
 * <p/>
 * You can use the classical setter and getter for the two class attributes (
 * {@link #parameter} and {@link #factory} to set and retrieve the values. In
 * addition this class provides the possibility to generate the factory on its
 * own - in this case the {@link #parameter} has to contain the factorie's class
 * name as value. The instance will only be created if the
 * {@link #getFactoryInstance()} method is used and the {@link #factory}
 * parameter is null.
 * <p/>
 * This class does not assume that the factory can deal with parameter passed as
 * a parameter block. To create instances by using the factory is left to a user
 * of this class - it might happen that the user has to extract the parameters
 * from the parameter block to call a factory methods needing a different input.
 * <p/>
 * There is no real need for this class: as the convention is that the value of
 * a parameter block contains the class name of the class the parameters are for
 * the {@link #factory} parameter can be considered to be obsolete which reduces
 * this class to be a wrapper around a {@link ParameterBlock}. It has been
 * mostly introduced due to the fact that it might be better usable like this in
 * some situations and due to the fact that there is a lot of older code making
 * this distinction. Thus a simple parameter block could be used instead, in
 * principle. Here this class would only provide in addition the
 * {@link #getFactoryInstance()} method which encapsulates the instantiation of
 * the factory class from the parameter block (assuming {@link #factory} is
 * null).
 * <p/>
 * 
 * @author Jan Himmelspach
 * 
 * @param F
 *          the type of the factory which can be hold by the parameterized
 *          factory instance. Using this type avoids type casting later on.
 */
public class ParameterizedFactory<F extends Factory> implements Serializable {

  /**
   * The constant serial version ID.
   */
  private static final long serialVersionUID = -2252933680951198994L;

  /**
   * The parameters of the factory. Will be passed on to the factory to create
   * an instance.
   */
  private ParameterBlock parameter = null;

  /**
   * An instance of the factory. This value can be, but must not be set. If not
   * the classname of the factory should be provided as value parameter of
   * {@link #parameter}.
   */
  private F factory = null;

  /**
   * Create an empty instance of the parameterized factory class. Use at least
   * one of the setter methods {@link #setFactory(Factory)} or
   * {@link #setParameter(ParameterBlock)} to fill the instance later on.
   */
  public ParameterizedFactory() {
    super();
  }

  /**
   * Create a new instance of a parameterized factory with a factory only (thus
   * the parameters are null).
   * 
   * @param factory
   */
  public ParameterizedFactory(F factory) {
    this();
    this.factory = factory;
  }

  /**
   * Create a new instance of a parameterized factory with a parameter block
   * only. If the {@link #setFactory(Factory)} method is not called later on the
   * parameter block passed should contain the factorie's class name as value.
   * 
   * @param parameters
   */
  public ParameterizedFactory(ParameterBlock parameters) {
    this();
    this.parameter = parameters;
  }

  /**
   * Create a new instance of a parameterized factory and set the attributes to
   * the values passed.
   * 
   * @param factory
   * @param parameters
   */
  public ParameterizedFactory(F factory, ParameterBlock parameters) {
    this();
    this.factory = factory;
    this.parameter = parameters;
  }

  /**
   * Will return an instance of the factory. If the {@link #factory} attribute
   * is null the {@link #parameter} is checked whether a factory name has been
   * given - if an instance will be automatically created and returned. If the
   * instance creation fails this method will generate a log entry and return
   * null.
   * 
   * @return an instance of a factory or null
   */
  @SuppressWarnings("unchecked")
  public F getFactoryInstance() {
    F result = factory;
    if (factory == null && parameter != null && parameter.getValue() != null) {
      try {
        result = (F) Class.forName((String) parameter.getValue()).newInstance();
      } catch (Exception e) {
        SimSystem.report(Level.SEVERE,
            "Was not able to create the factory given " + parameter.getValue(),
            e);
      }
    }
    return result;
  }

  /**
   * Create an instance using the parameterized factory.
   * 
   * @return an instance of the type to be created by this factory or null
   */
  @SuppressWarnings("unchecked")
  public <I> I getInstance() {
    F factoryInstance = getFactoryInstance();
    if (factoryInstance == null) {
      return null;
    }
    return (I) factoryInstance.create(getParameter());
  }

  /**
   * Returns the factory attribute. In contrast to {@link #getFactoryInstance()}
   * this method may return null although the parameter attribute specifies a
   * factory.
   * 
   * @return the value of the factory attribute, maybe null
   */
  public F getFactory() {
    return factory;
  }

  /**
   * Set the factory attribute. Please take care of the {@link #parameter} as
   * well: they should fit to the factory set here.
   * 
   * @param factory
   */
  public void setFactory(F factory) {
    this.factory = factory;
  }

  /**
   * Set the parameter to be passed on to the factory on creation. If the
   * {@link #factory} will not be used / is not used the parameter should
   * contain the factorie's classname as value. In this case the
   * {@link #getFactory()} method will return still null but the
   * {@link #getFactoryInstance()} will return non null if the factory can be
   * generated automatically.
   * 
   * @param parameter
   */
  public void setParameter(ParameterBlock parameter) {
    this.parameter = parameter;
  }

  /**
   * Get the parameter block to be used for the the factory. The factory's class
   * name will be automatically set as value of this parameter block if it is
   * empty. If the parameter block has not been set a new one will be created,
   * if it has been set a copy of the block will be returned.<br/>
   * If you want to get the class attribute {@link #parameter} you have to use
   * the {@link #getParameter()} method instead.
   * 
   * @return the parameter block set with the factories class name as value (if
   *         set) or an empty parameter block
   */
  public ParameterBlock getParameters() {
    ParameterBlock result;
    if (parameter == null) {
      result = new ParameterBlock();
    } else {
      result = parameter.getCopy();
    }
    if (((result.getValue() == null) || (result.getValue().equals("")))
        && factory != null) {
      result.setValue(factory.getClass().getName());
    }
    return result;
  }

  /**
   * Get the class attribute {@link #parameter}. Consequently this method might
   * return null. If you want to use the block later on you might use the
   * {@link #getParameters()} methods instead which always returns a parameter
   * block (although this might be empty).
   * 
   * @return the content of the instance attribute {@link #parameter}
   */
  public ParameterBlock getParameter() {
    return parameter;
  }

  /**
   * Returns true if the parameter block has been set in a constructor or by
   * using the {@link #setParameter(ParameterBlock)} method. Please note that
   * the {@link #getParameters()} method will always return a parameter block,
   * independent from the fact whether the {@link #parameter} is null or not.
   * The {@link #getParameter()} method will return the "real" parameter block
   * and thus will return null is this method returns false.
   * 
   * @return true if the parameter block has been set, false otherwise
   */
  public boolean hasParameter() {
    return parameter != null;
  }

  /**
   * Checks whether the factory or the parameter block has been initialized. If
   * the factory has not been set the parameter block is examined (if set)
   * whether the value has been set as well.
   * 
   * @return true if factory not null or the parameter and parameter.value non
   *         null
   */
  public boolean isInitialized() {
    return (factory != null)
        || ((parameter != null) && (parameter.getValue() != null));
  }

}