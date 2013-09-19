/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.util.Map;

/**
 * Simple resource provider supporting the <b>inputstream</b> domain. The given
 * location is used to open an {@link java.io.InputStream} that is then
 * returned. Since the provider never knows when the {@link java.io.InputStream}
 * is not needed anymore it is in the users responsibility that it is closed
 * properly after usage.<br/>
 * Additionally this kind of resource should not be cached on the
 * {@link java.io.InputStream} level.
 * 
 * @author Stefan Rybacki
 * 
 */
final class InputStreamResourceProvider implements IResourceProvider {
  /**
   * singleton instance
   */
  private static final IResourceProvider instance =
      new InputStreamResourceProvider();

  @Override
  public Object getResourceFor(String location, Map<String, String> params,
      Class<?> requestingClass) {
    if (requestingClass == null) {
      return getClass().getResourceAsStream(location);
    }
    return requestingClass.getResourceAsStream(location);
  }

  /**
   * @return an instance of this provider
   */
  public static IResourceProvider getInstance() {
    return instance;
  }

  /**
   * hide constructor to expose this class as singleton
   */
  private InputStreamResourceProvider() {
    // nothing to do here
  }

  @Override
  public boolean canHandleDomain(String domain) {
    return domain.equals("inputstream");
  }

  @Override
  public String[] getSupportedDomains() {
    return new String[] { "inputstream" };
  }

}
