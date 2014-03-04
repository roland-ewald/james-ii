/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database.hibernate;


import java.util.HashSet;
import java.util.Set;

import org.jamesii.asf.database.ISelection;
import org.jamesii.asf.database.ISelector;
import org.jamesii.perfdb.entities.IFeature;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.hibernate.IDEntity;
import org.jamesii.perfdb.hibernate.RuntimeConfiguration;


/**
 * Hibernate implementation of a selection.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
@SuppressWarnings("unused")
// Hibernate uses private methods
public class Selection extends IDEntity implements ISelection {

  private static final long serialVersionUID = 3686442043631394445L;

  /** Configuration that was chosen by this selector. */
  private RuntimeConfiguration runtimeConfiguration;

  /** The selector that made the selection. */
  private Selector selector;

  /**
   * List of feature type IDs, defining the features that were taken into
   * account.
   */
  private Set<IFeature> features = new HashSet<>();

  @Override
  public Set<IFeature> getFeatures() {
    return features;
  }

  @Override
  public IRuntimeConfiguration getRuntimeConfiguration() {
    return runtimeConfiguration;
  }

  @Override
  public ISelector getSelector() {
    return selector;
  }

  @Override
  public void setFeatures(Set<IFeature> feats) {
    features = feats;
  }

  @Override
  public void setRuntimeConfiguration(IRuntimeConfiguration runtimeConfig) {
    if (!(runtimeConfig instanceof RuntimeConfiguration)) {
      throw new IllegalArgumentException();
    }
    runtimeConfiguration = (RuntimeConfiguration) runtimeConfig;
  }

  @Override
  public void setSelector(ISelector selector) {
    if (!(selector instanceof Selector)) {
      throw new IllegalArgumentException();
    }
    this.selector = (Selector) selector;
  }

  // Hibernate functions

  private RuntimeConfiguration getRuntimeConfig() {
    return runtimeConfiguration; // NOSONAR:{used_by_hibernate}
  }

  private void setRuntimeConfig(RuntimeConfiguration runtimeConfig) {
    runtimeConfiguration = runtimeConfig; // NOSONAR:{used_by_hibernate}
  }

  private Selector getSel() {
    return selector; // NOSONAR:{used_by_hibernate}
  }

  private void setSel(Selector sel) {
    selector = sel; // NOSONAR:{used_by_hibernate}
  }
}