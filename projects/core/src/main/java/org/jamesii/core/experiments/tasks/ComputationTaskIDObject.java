/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks;

import java.io.Serializable;

import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.id.UniqueIDGenerator;

/**
 * Objects of this class represent the unique ID of a computation task.
 * 
 * @author Stefan Leye
 * 
 */

public class ComputationTaskIDObject implements IUniqueID, Serializable {
  static {
    SerialisationUtils.addDelegateForConstructor(ComputationTaskIDObject.class,
        new IConstructorParameterProvider<ComputationTaskIDObject>() {
          @Override
          public Object[] getParameters(ComputationTaskIDObject idObj) {
            return new Object[] { idObj.getExternalID(), idObj.getId() };
          }
        });
  }

  /**
   * The serializationID.
   */
  private static final long serialVersionUID = 4267081698805691365L;

  /**
   * The ID of the simulation.
   */
  private final IUniqueID id;

  /**
   * The ID given from the Database.
   */
  private final Serializable externalID;

  /**
   * Instantiates a new computation task id object.
   */
  public ComputationTaskIDObject() {
    id = UniqueIDGenerator.createUniqueID();
    externalID = 0;
  }

  /**
   * Constructor, if an additional ID shall be provided by a data storage.
   * 
   * @param storage
   */
  public ComputationTaskIDObject(IDataStorage<?> storage) {
    id = UniqueIDGenerator.createUniqueID();
    this.externalID = storage.setComputationTaskID(null, null, getId());
  }

  /**
   * Constructor, if an additional ID shall be used, e.g., if a database uses an
   * internal counter.
   * 
   * @param externalID
   */
  public ComputationTaskIDObject(Serializable externalID) {
    id = UniqueIDGenerator.createUniqueID();
    this.externalID = externalID;
  }

  /**
   * Constructor to be used by Java persistence mechanisms.
   * 
   * @param externalID
   *          the external id
   * @param uniqueID
   *          the unique id
   */
  ComputationTaskIDObject(Serializable externalID, IUniqueID uniqueID) {
    this.externalID = externalID;
    this.id = uniqueID;
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof ComputationTaskIDObject) {
      return ((ComputationTaskIDObject) o).getId().equals(getId());
    }
    return false;
  }

  @Override
  public String toString() {
    return getId().asString();
  }

  @Override
  public int compareTo(IUniqueID o) {
    return getId().compareTo(o);
  }

  @Override
  public String asString() {
    return toString();
  }

  /**
   * @return the id
   */
  public IUniqueID getId() {
    return id;
  }

  /**
   * @return the externalID
   */
  public <T extends Serializable> T getExternalID() {
    return (T) externalID;
  }

}
