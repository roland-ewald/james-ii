/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper;

import org.jamesii.core.util.collection.ListenerSupport;

/**
 * Abstract base class for value mappers, that handles most common actions, like
 * adding and removing listeners, already.
 */
public abstract class AbstractValueMapper implements ICAValueMapper {

  /** Registered listeners. */
  private final ListenerSupport<IMappingChangedListener> listeners =
      new ListenerSupport<>();

  /** The type supported by this value mapper. */
  private final Class<?> supportedType;

  /**
   * Constructor that defines the supported type for this value mapper.
   * 
   * @param supportedType
   *          The type that this value mapper is supposed to support.
   */
  public AbstractValueMapper(Class<?> supportedType) {
    this.supportedType = supportedType;
  }

  @Override
  public void addMappingChangedListener(IMappingChangedListener l) {
    listeners.addListener(l);
  }

  @Override
  public void removeMappingChangedListener(IMappingChangedListener l) {
    listeners.removeListener(l);
  }

  /**
   * Notifies all attached {@link IMappingChangedListener}s of a changed mapping
   * so that registered listeners have a change to update their display.
   */
  protected void fireMappingChanged() {
    for (IMappingChangedListener l : listeners.getListeners()) {
      l.mappingChanged();
    }
  }

  @Override
  public Class<?> getSupportedType() {
    return supportedType;
  }
}
