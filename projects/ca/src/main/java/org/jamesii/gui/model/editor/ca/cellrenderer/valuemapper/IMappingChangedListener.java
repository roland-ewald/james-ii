/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper;

/**
 * Interface for listeners that get notified whenever the mapping of an
 * {@link ICAValueMapper} changes.
 * 
 * @author Johannes Rössel
 */
public interface IMappingChangedListener {
  /** Gets called whenever the mapping of the {@link ICAValueMapper} changes. */
  void mappingChanged();
}
