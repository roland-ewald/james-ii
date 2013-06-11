/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.util.Map;

/**
 * This interface is implemented by each provider that supports at least one
 * resource domain. Those domains define the type of resource to be loaded. So
 * there might be a provider for an image domain where there is another for an
 * icon domain and so on.
 * 
 * @author Stefan Rybacki
 * 
 * @see ImageResourceProvider
 * @see IconResourceProvider
 * 
 */
public interface IResourceProvider {
  /**
   * Returns the resource for the given location using given parameters
   * 
   * @param location
   *          the location the resource is located
   * @param params
   *          parameters to access the resource
   * @param requestingClass
   *          TODO
   * @return the resource
   * @throws Throwable
   *           might throws an error or exception
   */
  Object getResourceFor(String location, Map<String, String> params,
      Class<?> requestingClass);

  /**
   * Returns true if this provider can handle a given domain
   * 
   * @param domain
   *          the domain to handle
   * @return true if domain is handleable by provider
   */
  boolean canHandleDomain(String domain);

  /**
   * Returns a list of domains that are supported by this provider.
   * 
   * @return a list of domains
   */
  String[] getSupportedDomains();
}
