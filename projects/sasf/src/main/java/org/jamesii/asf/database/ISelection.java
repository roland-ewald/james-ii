/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database;


import java.util.Set;

import org.jamesii.perfdb.entities.IFeature;
import org.jamesii.perfdb.entities.IIDEntity;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;


/**
 * Represents the selection of a configuration for a simulation problem by a
 * selector, given a set of features that were taken into account.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public interface ISelection extends IIDEntity {

  IRuntimeConfiguration getRuntimeConfiguration();

  void setRuntimeConfiguration(IRuntimeConfiguration runtimeConfiguration);

  ISelector getSelector();

  void setSelector(ISelector selector);

  Set<IFeature> getFeatures();

  void setFeatures(Set<IFeature> feats);

}