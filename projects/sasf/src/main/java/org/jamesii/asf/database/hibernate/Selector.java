/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database.hibernate;


import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import org.jamesii.asf.database.ISelector;
import org.jamesii.asf.database.ISelectorType;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.hibernate.NamedDBEntity;


/**
 * Hibernate implementation of a selector.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
@SuppressWarnings("unused")
// Hibernate uses private methods
public class Selector extends NamedDBEntity implements ISelector {

  private static final long serialVersionUID = -4128529365731650485L;

  /** The type of this selector. */
  private SelectorType selectorType;

  /**
   * URI to the definition of the selector (if it has a file format, otherwise
   * the field 'code' can be used).
   */
  private URI uri;

  /**
   * Code of the selector.
   */
  // TODO: Specify how this should work...
  private Serializable code;

  /**
   * Parameter used to generate this selector, eg. weights of different
   * performance measures.
   */
  // TODO: Insert correct object here
  private Serializable parameters;

  @Override
  public Serializable getCode() {
    return code;
  }

  @Override
  public ISelectorType getSelectorType() {
    return selectorType;
  }

  @Override
  public URI getUri() {
    return uri;
  }

  @Override
  public void setCode(Serializable c) {
    code = c;
  }

  @Override
  public void setSelectorType(ISelectorType selType) {
    if (!(selType instanceof SelectorType)) {
      throw new IllegalArgumentException();
    }
    selectorType = (SelectorType) selType;
  }

  @Override
  public void setUri(URI u) {
    uri = u;
  }

  @Override
  public Serializable getParameters() {
    return parameters;
  }

  @Override
  public void setParameters(Serializable params) {
    parameters = params;
  }

  private SelectorType getSelType() {
    return selectorType; // NOSONAR:{used_by_hibernate}
  }

  private void setSelType(SelectorType selType) {
    selectorType = selType; // NOSONAR:{used_by_hibernate}
  }

  private String getUriString() {
    return Strings.uriToString(uri); // NOSONAR:{used_by_hibernate}
  }

  private void setUriString(String string) throws URISyntaxException {
    uri = Strings.stringToURI(string); // NOSONAR:{used_by_hibernate}
  }

}
