/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;


/**
 * Helper class for the default mediator. Used to store updates internally.
 * Please note: this data does only store a reference to the entity of which the
 * state has been updated! Thus if you read the entity later its state may have
 * already changed. If you want to avoid this you have either to clone the whole
 * entity, or at least to clone the information which shall be reported.
 * 
 * @author Jan Himmelspach
 */
public class MediatorData {

  /** The observable entity. */
  private IObservable entity;

  /** The hint. */
  private Object hint = null;

  /**
   * Instantiates a new mediator data.
   * 
   * @param entity
   *          the entity
   */
  public MediatorData(IObservable entity) {
    super();
    this.setEntity(entity);
  }

  /**
   * Instantiates a new mediator data.
   * 
   * @param entity
   *          the entity
   * @param hint
   *          the hint
   */
  public MediatorData(IObservable entity, Object hint) {
    super();
    setEntity(entity);
    setHint(hint);
  }

  /**
   * @return the entity
   */
  protected final IObservable getEntity() {
    return entity;
  }

  /**
   * @param entity
   *          the entity to set
   */
  protected final void setEntity(IObservable entity) {
    this.entity = entity;
  }

  /**
   * @return the hint
   */
  protected final Object getHint() {
    return hint;
  }

  /**
   * @param hint
   *          the hint to set
   */
  protected final void setHint(Object hint) {
    this.hint = hint;
  }

}
