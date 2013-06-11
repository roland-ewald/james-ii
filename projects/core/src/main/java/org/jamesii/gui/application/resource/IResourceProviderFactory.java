/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

/**
 * Interface for the resource provider factory. The implementation can be used
 * by the {@link ApplicationResourceManager} to obtain domain specific resource
 * providers.
 * 
 * @author Stefan Rybacki
 * 
 * @see JamesResourceProviderFactory
 */
public interface IResourceProviderFactory {
  /**
   * Main function of the factory, returning a provider for the specific domain
   * 
   * @param domain
   *          the resource's domain
   * @return an {@link IResourceProvider} that can handle the specified domain
   *         or null if there is none
   */
  IResourceProvider getResourceProviderFor(String domain);

  /**
   * Similar to {@link #getResourceProviderFor(String)} but returns all
   * available providers available for the given domain. This can be helpful if
   * one provider is more effective or reliable than another and the application
   * decides which provider to use rather than the factory.
   * 
   * @param domain
   *          the resource's domain
   * @return an array of {@link IResourceProvider}s that can handle the
   *         specified domain or an empty array if there is none
   */
  IResourceProvider[] getResourceProvidersFor(String domain);

  /**
   * @return all available providers
   */
  IResourceProvider[] getResourceProviders();
}
