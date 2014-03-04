/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database;


import java.io.Serializable;
import java.net.URI;

import org.jamesii.perfdb.entities.INamedDBEntity;


/**
 * Represents a concrete selector that can be trained, tested or just used.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public interface ISelector extends INamedDBEntity {

  ISelectorType getSelectorType();

  void setSelectorType(ISelectorType selectorType);

  URI getUri();

  void setUri(URI uri);

  Serializable getCode();

  void setCode(Serializable code);

  Serializable getParameters();

  void setParameters(Serializable params);

}