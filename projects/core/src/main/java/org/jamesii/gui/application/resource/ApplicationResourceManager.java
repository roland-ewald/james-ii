/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Let the resource manager be extendible by using a resource URL that specifies
 * the type of resource like file, database, bundle, class etc. where this is
 * specified in the resource url followed by the actual resource plus its
 * parameters if needed (for instance on database access it would be good to be
 * able to provide database access data)
 * <p>
 * So basically the url domain is used to retrieve an appropriate resource
 * provider using factory methods.
 * <p>
 * URL format: domain:location?parameter1=value1&parameter2=value1&parameter3
 * =value3 Does that make sense?! NOTE: work in progress
 * 
 * @author Stefan Rybacki
 */
public final class ApplicationResourceManager {
  /**
   * flag specifying how many entries to cache
   */
  private static final int MAX_CACHE_ENTRIES = 128;

  /**
   * caches a couple of resources
   */
  private static final ConcurrentHashMap<String, Resource> cachedResources =
      new ConcurrentHashMap<>(MAX_CACHE_ENTRIES);

  /**
   * factory that provides access to resource providers
   */
  private static IResourceProviderFactory resourceFactory;

  private ApplicationResourceManager() {
  }

  /**
   * Convenience method that calls {@link #getResource(String, boolean, Class)}
   * with the {@code cacheable} parameter set to {@code true} and which already
   * casts the output.
   * 
   * @param <T>
   *          specifies the auto return type of this method
   * @param resourceURL
   *          the resource url
   * @param requestingClass
   *          the class that requests this resource, this is useful if the
   *          resource location is provided relative to the calling class
   * @return the loaded resource
   * @throws ResourceLoadingException
   *           if no resource could be loaded
   */
  @SuppressWarnings("unchecked")
  public static final <T> T getResource(String resourceURL,
      Class<?> requestingClass) throws ResourceLoadingException {
    return (T) getResource(resourceURL, true, requestingClass);
  }

  /**
   * Convenience method that calls {@link #getResource(String, boolean, Class)}
   * with the {@code cacheable} parameter set to {@code true} and which already
   * casts the output and the requesting class set to {@code null}.
   * 
   * @param <T>
   *          specifies the auto return type of this method
   * @param resourceURL
   *          the resource url
   * @return the loaded resource
   * @throws ResourceLoadingException
   *           if no resource could be loaded
   */
  @SuppressWarnings("unchecked")
  public static final <T> T getResource(String resourceURL)
      throws ResourceLoadingException {
    return (T) getResource(resourceURL, true, null);
  }

  /**
   * Main method of the resource manager. It provides resource loading from
   * different locations using a special {@link ResourceURL} that specifies a
   * domain, a location and additional parameters.
   * <p>
   * A resource url looks like this: <br/>
   * <code><pre>domain:location[?parameter1=value1:parameter2=value1:parameter3=value3]</pre></code>
   * <br/>
   * An example resource URL could then be:<br/>
   * <code><pre>icon:/directory/icon.gif</pre></code> <br/>
   * This returns an icon created from /directory/icon.gif (no parameters needed
   * here)
   * 
   * @param <T>
   *          specifies the auto return type of this method
   * @param resourceURL
   *          the resource url pointing to the resource to load
   * @param cacheable
   *          specifies whether the generated resource should be cached
   * @param requestingClass
   *          the class that requested the resource
   * @return the loaded resource
   * @throws ResourceLoadingException
   *           thrown when resource couldn't be loaded
   */
  @SuppressWarnings("unchecked")
  public static synchronized final <T> T getResource(String resourceURL,
      boolean cacheable, Class<?> requestingClass)
      throws ResourceLoadingException {
    // check cache first
    Resource o = cachedResources.get(resourceURL);
    if (o != null && o.getResource() != null) {
      return (T) o.getResource();
    }

    // create a ResourceURL from specified url
    ResourceURL url;
    try {
      url = new ResourceURL(resourceURL);
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      throw new ResourceLoadingException("Error in resource url!", e);
    }

    String domain = url.getDomain();
    String location = url.getLocation();
    Map<String, String> params = url.getParameters();

    Object resource = null;

    // get all providers for a given domain and try to load resource
    // with all of
    // them until one returned the resource
    IResourceProvider[] providers =
        getDefaultResourceProviderFactory().getResourceProvidersFor(domain);

    if (providers == null || providers.length == 0) {
      throw new ResourceLoadingException(
          "No provider found for resource domain (" + domain + ")!", null);
    }

    for (IResourceProvider provider : providers) {
      resource = provider.getResourceFor(location, params, requestingClass);
      if (resource != null) {
        break;
      }
    }

    if (resource == null) {
      throw new ResourceLoadingException("Could not load resource!", null);
    }

    if (cacheable) {
      cachedResources.put(resourceURL, new Resource(resourceURL, resource));
    }

    checkCache();

    return (T) resource;
  }

  /**
   * invokes a cache cleaning operation as soon as the
   * {@link #MAX_CACHE_ENTRIES} constraint is heavy violated which means for now
   * if there are more than 110% of {@link #MAX_CACHE_ENTRIES} entries in the
   * cache it is going to be cleansed
   */
  private static void checkCache() {
    // give 10% overhead tolerance
    if (cachedResources.size() > MAX_CACHE_ENTRIES * 1.1) {
      cleanCache();
    }
  }

  /**
   * helper function that removes old entries from cache so that there are no
   * {@code null} values in the cache as well as the {@link #MAX_CACHE_ENTRIES}
   * constraint is fulfilled
   */
  private synchronized static void cleanCache() {
    Resource[] resources =
        cachedResources.values().toArray(new Resource[cachedResources.size()]);
    Arrays.sort(resources);

    // remove entries until all entries with lastUpdate=0 are gone and
    // the
    // maximum size of the cache list is not violated
    for (Resource r : resources) {
      if (r.isNull() || cachedResources.size() > MAX_CACHE_ENTRIES) {
        cachedResources.remove(r.getId());
      }
    }
  }

  /**
   * @return the default resource provider factory
   */
  public static final synchronized IResourceProviderFactory getDefaultResourceProviderFactory() {
    if (resourceFactory == null) {
      resourceFactory = JamesResourceProviderFactory.getInstance();
    }

    return resourceFactory;
  }

  /**
   * Sets the default resource provider factory that should be used to provide
   * {@link IResourceProvider}s for loading resources.
   * 
   * @param factory
   *          the factory to use
   */
  public static final synchronized void setDefaultResourceProviderFactory(
      IResourceProviderFactory factory) {
    if (factory == null) {
      throw new IllegalArgumentException("Factory can't be null");
    }

    resourceFactory = factory;
  }

}
