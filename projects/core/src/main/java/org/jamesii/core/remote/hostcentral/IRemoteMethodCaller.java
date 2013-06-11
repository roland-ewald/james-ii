/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral;

/**
 * The Interface IRemoteMethodCaller.
 * 
 * This interface has to be implemented by a centralized instance to forward
 * method calls to the host where the object which shall execute the message is
 * on.
 * 
 * <br>
 * This centralized schema has some advantages, as well as some disadvantages
 * compared to the direct schema. <br>
 * Positive<br>
 * <ul>
 * <li>Easier to update if an object is migrated to a remote host (at most one
 * update per host)</li>
 * <li>Easier to handle connection breakdowns</li>
 * </ul>
 * 
 * Negative<br>
 * <ul>
 * <li>Centralized often means central bottleneck</li>
 * <li>Increases the overhead slightly due to the usage of Java Reflection</li>
 * <li>Depending on the implementation of the remote communication center the
 * memory consumption might be very high (if full maps, thus a map containing
 * all potential pairs of objectids and remote locations are required)</li>
 * </ul>
 * 
 * @author Jan Himmelspach
 * @author Simon Bartels
 */
public interface IRemoteMethodCaller {

  /**
   * Execute a method on a (remote) object. The object is identified by the
   * objectID which has to be unique. The parameters passed have to be in the
   * right order.<br>
   * If used for models: any problem should not be "reported" back to the model.
   * Model's in the framework are not aware of the environment they are executed
   * on.
   * 
   * @param methodName
   *          the method name to be called on the (remote) object identified by
   *          the parameter objectID
   * @param parameters
   *          the parameters to be passed over to the method of the (remote)
   *          object, they have to be in the right order
   * @param objectId
   *          the object id, this id has to identify the object where the method
   *          shall be called in an unambiguous way
   * 
   * @return the object
   */
  Object executeMethod(String methodName, Object[] parameters,
      IObjectId objectId);

  /**
   * Register an object so that it may be called remote.
   * 
   * @param objectID
   *          ID of the object thats going to be registered.
   * @param object
   *          The object itself.
   * 
   * 
   */
  void registerObject(IObjectId objectID, Object object);

}
