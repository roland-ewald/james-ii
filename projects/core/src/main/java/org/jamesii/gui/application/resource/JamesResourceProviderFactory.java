/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.resource.plugintype.ResourceProviderFactory;

/**
 * Basic resource provider factory that manages a basic set of resources
 * providers. For now the following providers are automatically integrated,
 * other can be added using the extension point {@link ResourceProviderFactory}:
 * <p>
 * <ul>
 * <li>{@link ImageResourceProvider}</li>
 * <li>{@link IconResourceProvider}</li>
 * <li>{@link InputStreamResourceProvider}</li>
 * <li>{@link TextfileResourceProvider}</li>
 * </ul>
 * 
 * @author Stefan Rybacki
 * 
 */
public final class JamesResourceProviderFactory implements
    IResourceProviderFactory {
  /**
   * list of resource providers
   */
  private final List<IResourceProvider> providers = new ArrayList<>();

  /**
   * singleton instance of this class
   */
  private static final JamesResourceProviderFactory instance =
      new JamesResourceProviderFactory();

  /**
   * omitted constructor that registers the default set of resource providers
   */
  private JamesResourceProviderFactory() {
  }

  @Override
  public final IResourceProvider getResourceProviderFor(String domain) {
    updateResourceProviders();
    for (IResourceProvider p : providers) {
      if (p.canHandleDomain(domain)) {
        return p;
      }
    }

    return null;
  }

  private synchronized void updateResourceProviders() {
    providers.clear();
    addResourceProvider(ImageResourceProvider.getInstance());
    addResourceProvider(IconResourceProvider.getInstance());
    addResourceProvider(InputStreamResourceProvider.getInstance());
    addResourceProvider(TextfileResourceProvider.getInstance());

    Registry registry = SimSystem.getRegistry();

    if (registry != null) {
      List<ResourceProviderFactory> providerFactories = null;
      try {
        providerFactories =
            SimSystem.getRegistry().getFactories(ResourceProviderFactory.class);
      } catch (Throwable e) {
        SimSystem.report(Level.WARNING, null,
            "Couldn't get resource provider factories");
      }

      if (providerFactories == null) {
        providerFactories = new ArrayList<>();
      }

      ParameterBlock params = new ParameterBlock();

      for (ResourceProviderFactory f : providerFactories) {
        try {
          IResourceProvider provider = f.create(params, SimSystem.getRegistry().createContext());
          addResourceProvider(provider);
        } catch (Throwable e) {
          SimSystem.report(Level.WARNING, null,
              "Couldn't create resource provider %s (%s)",
              new Object[] { f.getName(), e.getMessage() });
        }
      }
    }

  }

  /**
   * @return the singleton instance of this factory
   */
  public static JamesResourceProviderFactory getInstance() {
    return instance;
  }

  /**
   * Adds another resource provider the the list of providers.
   * 
   * @param provider
   *          the provider to add
   */
  public final void addResourceProvider(IResourceProvider provider) {
    if (!providers.contains(provider) && provider != null) {
      providers.add(provider);
    }
  }

  /**
   * Removes a previously registered resource provider
   * 
   * @param provider
   *          the provider to remove
   */
  public final void removeResourceProvider(IResourceProvider provider) {
    providers.remove(provider);
  }

  @Override
  public IResourceProvider[] getResourceProviders() {
    return providers.toArray(new IResourceProvider[providers.size()]);
  }

  @Override
  public IResourceProvider[] getResourceProvidersFor(String domain) {
    updateResourceProviders();
    List<IResourceProvider> result = new ArrayList<>();

    for (IResourceProvider p : providers) {
      if (p.canHandleDomain(domain)) {
        result.add(p);
      }
    }

    return result.toArray(new IResourceProvider[result.size()]);
  }

}
