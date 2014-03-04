/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import java.net.URI;
import java.net.URISyntaxException;

import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.entities.IProblemScheme;

/**
 * Hibernate implementation of {@link IProblemScheme}.
 * 
 * @author Roland Ewald
 * 
 */
@SuppressWarnings("unused")
// Private methods are used by Hibernate
public class ProblemScheme extends NamedDBEntity implements IProblemScheme {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3604779598356444510L;

  /** URI of the scheme. */
  private URI uri;

  /** The scheme type. */
  private String type = "";

  /**
   * Empty constructor for beans compliance.
   */
  public ProblemScheme() {
  }

  /**
   * Instantiates a new benchmark model.
   * 
   * @param name
   *          the name
   * @param description
   *          the description
   * @param theURI
   *          the the uri
   * @param theType
   *          the the type
   */
  public ProblemScheme(String name, String description, URI theURI,
      String theType) {
    super(name, description);
    uri = theURI;
    type = theType;
  }

  @Override
  public URI getUri() {
    return uri;
  }

  @Override
  public void setUri(URI u) {
    uri = u;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String newType) {
    type = newType;
  }

  // Hibernate support

  private String getUriString() {
    return Strings.uriToString(uri); // NOSONAR:{used_by_hibernate}
  }

  private void setUriString(String string) throws URISyntaxException {
    uri = Strings.stringToURI(string); // NOSONAR:{used_by_hibernate}
  }
}
