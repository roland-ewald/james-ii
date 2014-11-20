/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.algoselect.SelectionInformation;
import org.jamesii.core.base.InformationObject;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.FactoryInstantiationException;
import org.jamesii.core.factories.FactoryLoadingException;
import org.jamesii.core.factories.NoFactoryFoundException;
import org.jamesii.core.model.formalism.Formalism;
import org.jamesii.core.model.formalism.Formalisms;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.plugins.IId;
import org.jamesii.core.plugins.IParameter;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.plugins.IPluginTypeData;
import org.jamesii.core.plugins.PluginLoadException;
import org.jamesii.core.plugins.install.CachePlugInFinder;
import org.jamesii.core.plugins.install.DiscPlugInFinder;
import org.jamesii.core.plugins.install.IPlugInFinder;
import org.jamesii.core.plugins.install.XMLReader;
import org.jamesii.core.plugins.metadata.ComponentState;
import org.jamesii.core.plugins.metadata.FactoryDataSynchronizer;
import org.jamesii.core.plugins.metadata.FactoryRuntimeData;
import org.jamesii.core.plugins.metadata.FailureTolerance;
import org.jamesii.core.plugins.metadata.IFactoryRuntimeDataStorage;
import org.jamesii.core.plugins.metadata.file.RegFileDStorage;
import org.jamesii.core.util.Hook;
import org.jamesii.core.util.info.JavaInfo;
import org.jamesii.core.util.misc.Strings;
import org.xml.sax.InputSource;

/**
 * The Registry class is a wrapper for any (through the registry centralised)
 * information. It stores references to the installed plug-in types, plug-ins,
 * and it provides several services around them. There should be only one
 * instance of the registry. Any instantiation of the registry initiates a
 * search/loading process for available plug-ins. This instance is maintained
 * via the SimSystem class. Thus the Registry should be accessed by using the
 * static method SimSystem.getRegistry(). <br/>
 * You can search for / load a specific plug-in by using the get methods defined
 * in here (and you should always instantiate objects needed using the registry
 * and the factories returned). Typically the abstract factories per plug-in
 * type used internally support you on retrieving a suitable object.
 * 
 * @author Jan Himmelspach
 */
public class Registry extends InformationObject {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8943254205336692025L;

  /**
   * Default directory to search plug-ins in. Use root for now.
   */
  public static final String DEFAULT_PLUGIN_DIRECTORY = "";

  /**
   * A custom plug-in directory (if set, this will be used instead of the
   * default setting).
   */
  private static String customPluginDirectory = null;

  /**
   * Exclude the class path from the plug-in search.
   */
  private static boolean excludeClassPathOnSearch = false;

  /** Use plug-in cache. */
  private final boolean useCache = false;

  /** The start up time. */
  private final long startUpTime = System.currentTimeMillis();

  /** The factory data storage to be used. */
  private transient IFactoryRuntimeDataStorage dataStorage;

  /** The flag to decide whether the runtime data storage is used. */
  private transient boolean useRuntimeDataStorage = true;

  /**
   * Map abstract factory => managed factories super class.
   */
  private transient Map<Class<? extends AbstractFactory<?>>, Class<? extends Factory<?>>> abstractFactories =
      new LinkedHashMap<>();

  /**
   * Map managed factories super class => abstract factory.
   */
  private transient Map<Class<? extends Factory<?>>, Class<? extends AbstractFactory<?>>> abstractFactoriesInv =
      new LinkedHashMap<>();

  /**
   * Map of already initialised abstract factories.
   */
  private transient Map<Class<? extends AbstractFactory<?>>, AbstractFactory<?>> initializedAbstractFactories =
      new LinkedHashMap<>();

  /**
   * Class loader.
   */
  private transient URLClassLoader classLoader;

  /**
   * General factory list, for each type of factories an extra list is used.
   * Factories inheriting from each other (e.g. f2 extends f1) will not be in
   * one list!
   */
  private transient Map<Class<? extends Factory<?>>, List<Factory<?>>> factories =
      new LinkedHashMap<>();

  /** The mapping FQCN -> actual factory object. */
  private transient Map<String, Factory<?>> factoryNameMap =
      new LinkedHashMap<>();

  /**
   * Hook to be aware of any factory selections.
   */
  private Hook<SelectionInformation<?>> factorySelectionHook = null;

  /**
   * List of available formalisms.
   */
  private final Formalisms formalisms = new Formalisms();

  /**
   * A list of all plug-ins as they have been found on the system.
   */
  private transient List<IPluginData> foundPlugins = null;

  /**
   * A list of all plug-ins, grouped by the abstract factories.
   */
  private transient Map<Class<? extends AbstractFactory<?>>, List<IPluginData>> foundPluginsGrouped =
      new LinkedHashMap<>();

  /**
   * Maps the class names of the registered factories to their information.
   */
  private transient Map<String, IFactoryInfo> factoryInfo =
      new LinkedHashMap<>();

  /**
   * A list of all plug-in types as they have been found on the system.
   */
  private transient List<IPluginTypeData> foundPluginTypes = null;

  /**
   * Return the list of plug-in types available.
   * 
   * @return
   */
  public List<IPluginTypeData> getPluginTypes() {
    return foundPluginTypes;
  }

  /**
   * Map of information objects.
   */
  private transient Map<String, InformationObject> informationObjects =
      new LinkedHashMap<>();

  /** List of known base factory classes. */
  private transient List<Class<? extends Factory<?>>> knownBaseFactoryClasses =
      new ArrayList<>();

  /**
   * Default constructor.
   */
  public Registry() {
    super("registry");
  }

  /**
   * Instantiate class of given type. Presumes that this class has a default
   * constructor (i.e., public MyClass() {...}).
   * 
   * @param <T>
   *          the type of the class to be instantiated
   * @param type
   *          class to be instantiated
   * @return instance of this class
   */
  @SuppressWarnings("unchecked")
  private static <T> T instantiate(Class<T> type) {
    boolean emptyConstructorFound = false;
    try {
      for (Constructor<?> constructor : type.getConstructors()) {
        if (constructor.getParameterTypes().length == 0) {
          emptyConstructorFound = true;
          return (T) constructor.newInstance(new Object[0]);

        }
      }
    } catch (IllegalArgumentException | InstantiationException
        | IllegalAccessException | InvocationTargetException e) {
      SimSystem.report(Level.SEVERE,
          "Problem occured while instantiating factory '" + type + "'.", e);
    }

    if (!emptyConstructorFound) {
      SimSystem
          .report(Level.SEVERE, "No empty constructor was found for class "
              + type.getCanonicalName());
    }
    return null;
  }

  /**
   * Find the abstract factory class of a given factory inheriting from the base
   * factory.
   * 
   * @param cl
   *          the cl
   * @return the abstract factory
   */
  protected Class<? extends AbstractFactory<?>> getAbstractFactory(
      Class<? extends Factory<?>> cl) {
    return abstractFactoriesInv.get(getAncestor(cl));
  }

  /**
   * Returns the abstract factory class which has the given name.
   * 
   * @param name
   *          the name of the factory
   * @return the class of the abstract factory or null
   */
  public Class<? extends AbstractFactory<?>> getAbstractFactoryByName(
      String name) {
    for (Map.Entry<Class<? extends AbstractFactory<?>>, Class<? extends Factory<?>>> e : abstractFactories
        .entrySet()) {
      if (e.getKey().getName().compareTo(name) == 0) {
        return e.getKey();
      }
    }
    return null;
  }

  /**
   * Returns the abstract factory class by the plugin type name.
   * 
   * @param name
   *          the name of the plug-in type
   * @return the class of the abstract factory or null
   */
  public Class<? extends AbstractFactory<?>> getAbstractFactoryByPluginTypeName(
      String name) {
    for (IPluginTypeData ptd : foundPluginTypes) {
      if (ptd.getId().getName().compareTo(name) == 0) {
        return getAbstractFactoryByName(ptd.getAbstractFactory());
      }
    }
    return null;
  }

  /**
   * Gets the abstract factory for base factory.
   * 
   * @param factory
   *          the base factory for which the abstract factory shall be returned.
   * @return the abstract factory for base factory
   */
  public Class<? extends AbstractFactory<?>> getAbstractFactoryForBaseFactory(
      Class<? extends Factory<?>> factory) {
    return abstractFactoriesInv.get(factory);
  }

  /**
   * Gets the abstract factory for base factory.
   * 
   * @param factory
   *          the base factory for which the abstract factory shall be returned.
   * @return the abstract factory for base factory
   */
  public Class<? extends AbstractFactory<?>> getAbstractFactoryForBaseFactory(
      String factory) {
    for (Map.Entry<Class<? extends Factory<?>>, Class<? extends AbstractFactory<?>>> entry : abstractFactoriesInv
        .entrySet()) {
      if (entry.getKey().getName().compareTo(factory) == 0) {
        return entry.getValue();
      }
    }
    return null;
  }

  /**
   * Gets the base factory for abstract factory.
   * 
   * @param abstractFactory
   *          the abstract factory
   * @return the base factory for abstract factory
   */
  public Class<? extends Factory<?>> getBaseFactoryForAbstractFactory(
      Class<? extends AbstractFactory<?>> abstractFactory) {
    return abstractFactories.get(abstractFactory);
  }

  /**
   * Retrieves the abstract factory class for a given factory class.
   * 
   * @param factoryClass
   *          the class of the factory
   * @return the associated abstract factory, null if not found
   */
  @SuppressWarnings("unchecked")
  public Class<? extends AbstractFactory<?>> getAbstractFactoryForFactory(
      Class<? extends Factory<?>> factoryClass) {

    if (getAbstractFactoryForBaseFactory(factoryClass) != null) {
      return getAbstractFactoryForBaseFactory(factoryClass);
    }
    if (!Factory.class.isAssignableFrom(factoryClass.getSuperclass())) {
      return null;
    }

    Class<? extends Factory<?>> potentialBaseFactory =
        (Class<? extends Factory<?>>) factoryClass.getSuperclass();
    while (getAbstractFactoryForBaseFactory(potentialBaseFactory) == null
        && Factory.class.isAssignableFrom(potentialBaseFactory.getSuperclass())) {
      potentialBaseFactory =
          (Class<? extends Factory<?>>) potentialBaseFactory.getSuperclass();
    }

    return getAbstractFactoryForBaseFactory(potentialBaseFactory);
  }

  /**
   * Try to find the corresponding base factory class.
   * 
   * @param cl
   *          the cl
   * @return the ancestor
   */
  protected Class<? extends Factory<?>> getAncestor(
      Class<? extends Factory<?>> cl) {
    for (Class<? extends Factory<?>> fc : knownBaseFactoryClasses) {
      if (fc.isAssignableFrom(cl)) {
        return fc;
      }
    }
    return null;
  }

  /**
   * Gets the class loader.
   * 
   * @return the class loader
   */
  public final URLClassLoader getClassLoader() {
    return classLoader;
  }

  /**
   * Checks if the specified factory class is a base factory class.
   * 
   * @param fc
   *          the factory class to check
   * @return true, if the specified factory class is a base factory class
   */
  public final boolean isBaseFactory(Class<? extends Factory<?>> fc) {
    // check whether fc is a base factory
    Class<? extends Factory<?>> baseF = getBaseFactoryFor(fc);

    if (baseF == null) {
      return false;
    }

    return baseF.equals(fc);
  }

  /**
   * Gets the base factory for a given {@link Factory} class.
   * 
   * @param fc
   *          the factory class
   * @return the base factory for given factory class
   */
  public final Class<? extends Factory<?>> getBaseFactoryFor(
      Class<? extends Factory<?>> fc) {
    Class<? extends Factory<?>> baseF =
        getBaseFactoryForAbstractFactory(getAbstractFactoryForFactory(fc));

    if (baseF == null) {
      SimSystem.report(Level.SEVERE,
          "Was not able to determine a base factory for the factory: " + fc);
    }

    return baseF;
  }

  /**
   * Get all factories of the given class type.
   * 
   * @param <E>
   *          factory type (base factory)
   * @param fc
   *          the factory class, i.e.,
   *          {@link org.jamesii.core.data.model.read.plugintype.ModelReaderFactory}
   *          or {@link org.jamesii.core.data.model.ModelFileReaderFactory}
   * @return list of registered factories. Never null, but might be empty if no
   *         factory was found.
   */
  @SuppressWarnings("unchecked")
  public <E extends Factory<?>> List<E> getFactories(
      Class<? extends Factory<?>> fc) {
    if (!isBaseFactory(fc)) {
      List<E> allList = (List<E>) factories.get(getBaseFactoryFor(fc));
      List<E> list = new ArrayList<>();

      if (allList != null) {
        for (E e : allList) {
          if (fc.isAssignableFrom(e.getClass())) {
            list.add(e);
          }
        }
      }

      return list;
    }

    // return defensive copy
    return (factories.get(fc) == null) ? null : new ArrayList<>(
        (List<E>) factories.get(fc));
  }

  /**
   * Get a list of all factories installed via all plug-in of all plug-in types.
   * 
   * @return
   */
  public List<Factory<?>> getAllFactories() {
    List<Factory<?>> result = new ArrayList<>();

    for (List<Factory<?>> lf : factories.values()) {
      result.addAll(lf);
    }

    return result;
  }

  /**
   * The getFactory method returns one of the registered factories for the
   * abstract factory af.
   * 
   * @param <F>
   *          the type of the factory to be returned
   * @param af
   *          the abstract factory class type
   * @param afp
   *          the parameters used for determining the factory to be returned
   * @return a factory or throws {@link NoFactoryFoundException} if none was
   *         found
   */
  public <F extends Factory<?>> F getFactory(
      Class<? extends AbstractFactory<F>> af, ParameterBlock afp) {

    // use the filtering process of the abstract factory for
    // determining a
    // factory
    F factory =
        getInitializedAbstractFactory(af).create(afp,
            SimSystem.getRegistry().createContext());

    if (factorySelectionHook != null) {
      factorySelectionHook
          .execute(new SelectionInformation<>(af, afp, factory));
    }

    return factory;
  }

  /**
   * Get an instance created by one of the factories installed for the abstract
   * factory passed.
   * 
   * @param af
   *          the plug-in type factory a plug-in shall be selected by
   * @param afp
   *          the parameters to be used to select the plug-in and the parameters
   *          to be used to create the instance returned
   * @return an instance created by a plug-in selected or null
   */
  @SuppressWarnings("unchecked")
  public <O> O getObject(Class<? extends AbstractFactory<Factory<Object>>> af,
      ParameterBlock afp) {
    Factory<Object> factory = getFactory(af, afp);
    if (factory == null) {
      return null;
    }
    return (O) factory.create(afp, SimSystem.getRegistry().createContext());
  }

  /**
   * The getFactory method returns one of the registered factories for the
   * abstract factory af. In contrast to
   * {@link #getFactory(Class, ParameterBlock)} this method returns null rather
   * than throwing an {@link NoFactoryFoundException} if no factory was found.
   * 
   * @param <F>
   *          the type of the factory to be returned
   * @param af
   *          the abstract factory class type
   * @param afp
   *          the parameters used for determining the factory to be returned
   * @return a factory or null if none exists
   */
  public <F extends Factory<?>> F getFactoryOrNull(
      Class<? extends AbstractFactory<F>> af, ParameterBlock afp) {
    try {
      return getFactory(af, afp);
    } catch (NoFactoryFoundException e) {
      return null;
    }
  }

  /**
   * Get factory class by name.
   * 
   * @param factoryName
   *          name of the factory
   * @return the factory class
   */
  public Class<? extends Factory<?>> getFactoryClass(String factoryName) {

    for (Entry<Class<? extends Factory<?>>, List<Factory<?>>> c : this.factories
        .entrySet()) {
      for (Factory<?> f : c.getValue()) {
        if (f.getName().compareTo(factoryName) == 0) {
          return (Class<? extends Factory<?>>) f.getClass();
        }
      }
    }
    return null;
  }

  /**
   * Get factory by name.
   * 
   * @param factoryName
   *          class name of the factory
   * @return the factory
   */
  public Factory<?> getFactory(String factoryName) {

    for (Entry<Class<? extends Factory<?>>, List<Factory<?>>> c : this.factories
        .entrySet()) {
      for (Factory<?> f : c.getValue()) {
        if (f.getClass().getName().compareTo(factoryName) == 0) {
          return f;
        }
      }
    }
    return null;
  }

  /**
   * Gets list of all factories of the abstract factories' plug-in type which
   * fulfil the given parameter.
   * 
   * @param <F>
   *          the type of the factories to be returned in the list
   * @param af
   *          the class of the abstract factory
   * @param afp
   *          parameter for the abstract factory
   * @return list of all factories fulfilling the given parameter
   *         <P>
   *         class of the abstract factory's parameter
   */
  public <F extends Factory<?>> List<F> getFactoryList(
      Class<? extends AbstractFactory<F>> af, ParameterBlock afp) {
    return getInitializedAbstractFactory(af).getFactoryList(afp);
  }

  /**
   * Gets list of factories gracefully (ie, without throwing an
   * {@link NoFactoryFoundException} when no factory could be found). See
   * {@link Registry#getFactoryList} for some more information.
   * 
   * @param <F>
   *          the type of the factories which are returned in the list
   * @param af
   *          the abstract factory
   * @param afp
   *          the parameter block
   * @return the factory or empty list
   */
  public <F extends Factory<?>> List<F> getFactoryOrEmptyList(
      Class<? extends AbstractFactory<F>> af, ParameterBlock afp) {
    List<F> results;
    try {
      results = getFactoryList(af, afp);
    } catch (NoFactoryFoundException ex) {
      results = new ArrayList<>();
    }
    return results;
  }

  /**
   * Gets the factories. This method does not work with generics. This makes it
   * more usable in rare cases.
   * 
   * @param af
   *          the abstract factory the factories shall be retrieved for
   * @param afp
   *          the parameters to be used to filter the factories
   * 
   * @return the factories
   */
  public List<Factory<?>> getFactories(Class<AbstractFactory<Factory<?>>> af,
      ParameterBlock afp) {
    List<Factory<?>> results;
    try {
      results = getInitializedAbstractFactory(af).getFactoryList(afp);
    } catch (NoFactoryFoundException ex) {
      results = new ArrayList<>();
    }
    return results;
  }

  /**
   * Get names of the factories. Either their shortnames are returned, or if no
   * shortname is given their fully qualified class name is used.
   * 
   * @param listOfFactories
   *          list of factories
   * @return list of names of the given factories or an empty list if the list
   *         of factories was null or empty
   */
  public List<String> getFactoryNames(List<? extends Factory<?>> listOfFactories) {
    return Strings.getEntityNames(listOfFactories);
  }

  /**
   * Gets current hook for factory selection.
   * 
   * @return the current hook for factory selection
   */
  public Hook<SelectionInformation<?>> getFactorySelectionHook() {
    return factorySelectionHook;
  }

  /**
   * Gets the formalisms.
   * 
   * @return the formalisms
   */
  public Formalisms getFormalisms() {
    return formalisms;
  }

  /**
   * Will get a concatenated list of all contained information objects.
   * 
   * @return non null string which might be empty if there are no info objects
   */
  @Override
  public String getInfo() {
    String result = "";
    for (InformationObject io : informationObjects.values()) {
      result +=
          "\n" + io.getIdent() + "\n"
              + Strings.indent(io.getInfo() + "\n\n", " ");
    }
    return result;
  }

  /**
   * Get information on an "information object". Each information object has an
   * ident by which here the object is located and the contained information is
   * returned. If there is no information the method will return a corresponding
   * string containing the info that there is no information.
   * 
   * @param theIdent
   *          identification
   * @return information on the object associated with the given identifier
   */
  public String getInformation(String theIdent) {
    InformationObject io = informationObjects.get(theIdent);
    if (io == null) {
      return "No information about " + theIdent + " available!!";
    }
    return io.getInfo();

  }

  /**
   * Returns an instance of an abstract factory, initialized with all factories
   * found for this plug-in type.
   * 
   * @param <F>
   *          the type of the factory to be returned
   * @param af
   *          abstract factory
   * @return instance of an abstract factory
   */
  public <F extends Factory<?>> AbstractFactory<F> getInitializedAbstractFactory(
      Class<? extends AbstractFactory<F>> af) {

    // First, check whether factory has already been initialized.
    AbstractFactory<F> instAF = getCachedAbstractFactoryInstance(af);
    if (instAF != null) {
      return instAF;
    }

    // create an instance of the abstract factory
    try {
      instAF = af.newInstance();
    } catch (Exception e) {
      SimSystem
          .report(
              Level.SEVERE,
              "An internal error occured. The system was not able to create the required instance of an abstract factory. Thus it cannot select an approbiate factory for and thus we cannot create the required datastructure/algorithm.");
      throw new FactoryInstantiationException(
          "Error! Was not able to create the required abstract factory " + af,
          e);
    }

    // System.out.println(abstractFactories);

    // get the list of factories from which the abstract factory can
    // select one
    List<F> facs = getFactories(abstractFactories.get(af));

    if (facs == null) {
      throw new NoFactoryFoundException(
          "Error! Was not able to fetch any factory for the required abstract factory (no factories of this type are installed) "
              + af);
    }

    // copy the list of factories to the abstract factory
    for (F ef : facs) {
      instAF.addFactory(ef);
    }

    initializedAbstractFactories.put(af, instAF);
    return instAF;
  }

  /**
   * Looks up hash map that stores previously initialised abstract factories.
   * 
   * @param <F>
   *          type of the base factory
   * @param af
   *          the class of the abstract factory
   * @return the cached initialised version of the factory, null if it does not
   *         exist
   */
  @SuppressWarnings("unchecked")
  // Cast to concrete generics is unsafe (that is, it would be if we
  // would not
  // know what we put into the cache)
  protected <F extends Factory<?>> AbstractFactory<F> getCachedAbstractFactoryInstance(
      Class<? extends AbstractFactory<F>> af) {
    return (AbstractFactory<F>) initializedAbstractFactories.get(af);
  }

  /**
   * Returns the list of registered factory classes.
   * 
   * @return list of known factory classes.
   */
  public List<Class<? extends Factory<?>>> getKnownFactoryClasses() {
    return Collections.unmodifiableList(knownBaseFactoryClasses);
  }

  /**
   * Return a simple list of all installed plugins.
   * 
   * @return string containing list of plugins
   */
  public String getPluginList() {
    StringBuilder result = new StringBuilder();

    if (foundPlugins != null) {
      for (IPluginData pd : foundPlugins) {
        result.append("\n");
        result.append(pd.getId().getName());
      }
    }

    return result.toString();
  }

  /**
   * Return the list of found plug-ins.
   * 
   * @return list of plugin data
   */
  public List<IPluginData> getPlugins() {
    return foundPlugins;
  }

  /**
   * Return all plug-ins installed for the given AbstractFactory.
   * 
   * @param typ
   *          the typ
   * @return a list of plug-ins installed
   */
  public List<IPluginData> getPlugins(Class<? extends AbstractFactory<?>> typ) {
    return foundPluginsGrouped.get(typ);
  }

  /**
   * Find the plug-in type the given abstract factory belongs to.
   * 
   * @param abstractFactoryClass
   *          the abstract factory class for which the type data shall be
   *          retrieved
   * @return plug-in type or null
   */
  public IPluginTypeData getPluginType(
      Class<? extends AbstractFactory<?>> abstractFactoryClass) {
    for (IPluginTypeData ptd : foundPluginTypes) {
      if (ptd.getAbstractFactory().compareTo(abstractFactoryClass.getName()) == 0) {
        return ptd;
      }
    }
    return null;
  }

  /**
   * Checks for factories.
   * 
   * @param abstractFactoryClass
   *          the abstract factory class for which shall be checked whether
   *          there are plug-ins or not.
   * @return true, if successful
   */
  public boolean hasFactories(
      Class<? extends AbstractFactory<?>> abstractFactoryClass) {
    return abstractFactories.get(abstractFactoryClass) != null;
  }

  private static IPlugInFinder initBySearch(Set<String> ps) {
    DiscPlugInFinder cr = new DiscPlugInFinder();

    // remember the start path for using it later on
    String startPath = System.getProperty("user.dir");

    // compute the default directory to look for plug-ins in
    String pathToDefaultDirectory =
        customPluginDirectory != null ? customPluginDirectory : startPath
            + java.io.File.separatorChar + DEFAULT_PLUGIN_DIRECTORY;

    if (pathToDefaultDirectory.endsWith(java.io.File.separator)) {
      pathToDefaultDirectory =
          pathToDefaultDirectory.substring(0,
              pathToDefaultDirectory.length() - 1);
    }

    // first add the default directory/directories
    if (pathToDefaultDirectory != null) {

      Set<String> paths =
          new HashSet<>(
              org.jamesii.core.util.misc.Files
                  .getListOfPaths(pathToDefaultDirectory));

      ps.addAll(paths);

    }

    // add the class path entries

    if (!excludeClassPathOnSearch) {
      SimSystem.report(Level.INFO,
          "Includig classpath into the search for plug-ins (Classpath used is: "
              + System.getProperty(JavaInfo.CLASSPATH) + ")");

      ps.addAll(org.jamesii.core.util.misc.Files.getListOfPaths(System
          .getProperty(JavaInfo.CLASSPATH)));
    }

    // If the environment variable is set we'll parse any paths given
    // in there
    // as well
    String envPlugInDirectory = System.getenv("JAMES_PLUGINPATH");
    if (envPlugInDirectory != null) {
      SimSystem
          .report(
              Level.INFO,
              "Searching for plug-ins in dirs defined in the environment variable: JAMES_PLUGINPATH");

      List<String> paths =
          org.jamesii.core.util.misc.Files.getListOfPaths(envPlugInDirectory);

      ps.addAll(paths);

    }

    for (String p : ps) {
      SimSystem.report(Level.INFO, "Searching for plug-ins in: " + p);
      cr.parseDirectory(p);
    }

    return cr;
  }

  /**
   * Initialize the modeling and simulation framework, both in terms of the
   * plug-ins and the runtime data that is associated with them. NOTE: this is a
   * convenience method to test the {@link Registry} and its sub-classes in
   * isolation. In {@link SimSystem}, the individual initialization methods are
   * called separately to avoid multi-threading problems.
   */
  public void init() {
    initPlugins();
    initRuntimeDataStorage();
  }

  /**
   * Initialize the modeling and simulation framework. Search and register
   * plug-in types and plug-ins.
   */
  public void initPlugins() {

    SimSystem.report(Level.INFO, Language.getMessage("Registry:initSimSystem",
        "Initializing %s ... ", new String[] { SimSystem.SIMSYSTEM }));

    // FIXME find out why java.class.path only returns jamesII.jar
    // when
    // running from jamesII.jar
    SimSystem.report(
        Level.INFO,
        Language.getMessage(
            "Registry:pathInfo",
            "... which has been started from %s"
                + "\n... using the classpath %s",
            new String[] { System.getProperty("user.dir"),
                System.getProperty("java.class.path") }));

    // TODO (general) load language file
    // loadLanguage("simsystem_"+language+".po");

    Set<String> ps = new HashSet<>();

    // **************************** get plug-ins
    // *****************************
    IPlugInFinder pluginFinder;

    if (useCache) {
      try { // try to load from cache
        pluginFinder = new CachePlugInFinder();
      } catch (Exception e) {
        // loading the cache failed
        pluginFinder = initBySearch(ps);
        // create a new cache
        CachePlugInFinder cf = new CachePlugInFinder();
        cf.copyFrom(pluginFinder);
        cf.write();
      }
    } else {
      pluginFinder = initBySearch(ps);
    }

    // If you write the cache once with the lines above than this lines
    // reads to
    // start JII without scanning for new plug-ins

    // get the classpath content
    List<String> paths =
        org.jamesii.core.util.misc.Files.getListOfPaths(System
            .getProperty("java.class.path"));

    // the paths of plug-ins found
    ps.addAll(paths);

    // built a list of URLs from the string paths
    Set<URI> pURIs = new HashSet<>();
    for (String p : ps) {
      try {
        pURIs.add(new File(p).getCanonicalFile().toURI());
      } catch (IOException e1) {
        SimSystem.report(e1);
      }
    }

    // *************** load plug-in types and plug-ins
    // ****************

    List<URL> pURLs = new ArrayList<>();
    for (URI uri : pURIs) {
      try {
        pURLs.add(uri.toURL());
      } catch (MalformedURLException e1) {
        SimSystem.report(e1);
      }
    }

    // get the list of jar files searched (to be used for the class
    // path of the
    // class loader)
    List<URL> classPath = pluginFinder.getJARLocations();

    // merge the jar files and the normal directories
    classPath.addAll(0, pURLs);

    // sort classPath by locations to always have a deterministic list of urls
    Collections.sort(classPath, new Comparator<URL>() {
      @Override
      public int compare(URL o1, URL o2) {
        if (o1 == null) {
          return -1;
        }
        if (o2 == null) {
          return 1;
        }
        return o1.toExternalForm().compareTo(o2.toExternalForm());
      }
    });

    URL[] path = new URL[classPath.size()];
    classPath.toArray(path);

    final URL[] p = path;

    // TODO sr137: add classloader on top of each plugin classloader
    // and add jar urls on the fly

    classLoader =
        AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
          @Override
          public URLClassLoader run() {
            return new URLClassLoader(p, this.getClass().getClassLoader());
          }
        });

    foundPlugins = pluginFinder.getFoundPlugins();
    // sort plugin list to ensure deterministic ordering
    Collections.sort(foundPlugins, new Comparator<IPluginData>() {
      @Override
      public int compare(IPluginData o1, IPluginData o2) {
        if (o1 == null) {
          return -1;
        }
        if (o2 == null) {
          return 1;
        }
        return o1.getId().compareTo(o2.getId());
      }
    });

    foundPluginTypes = pluginFinder.getFoundPluginTypes();
    // sort plugintypes list first to ensure deterministic ordering
    Collections.sort(foundPluginTypes, new Comparator<IPluginTypeData>() {
      @Override
      public int compare(IPluginTypeData o1, IPluginTypeData o2) {
        if (o1 == null) {
          return -1;
        }
        if (o2 == null) {
          return 1;
        }
        return o1.getBaseFactory().getClass().getName()
            .compareTo(o2.getBaseFactory().getClass().getName());
      }
    });

    SimSystem.report(Level.INFO,
        "Found plug-in types: " + foundPluginTypes.size());
    SimSystem.report(Level.INFO,
        "Found and loaded plug-ins: " + foundPlugins.size());

    SimSystem.report(Level.INFO,
        "Loading classes in found plug-in types and plug-ins ... ");

    // load the classes of the abstract factories
    loadPluginTypesClasses();

    // load the classes of the factories
    loadFactoryClasses(pluginFinder);

    // compute some simple statistics (absolute counts)
    int count = 0;
    int typecount = 0;
    for (Map.Entry<Class<? extends Factory<?>>, List<Factory<?>>> e : factories
        .entrySet()) {
      typecount++;
      count += e.getValue().size();
    }
    SimSystem.report(Level.INFO, "Installed factory types : " + typecount);
    SimSystem.report(Level.INFO, "Installed factories : " + count);

    // further checks like whether default values for plugin type
    // parameters fit as well as if plugin type parameters have valid
    // types
    checkPluginTypeParameters();

    try {
      dataStorage = new RegFileDStorage(".regdata");
    } catch (Throwable t) { // NOSONAR:robustness:{deactive_runtime_ds_if_anything_goes_wrong}
      handleErrorDuringPluginRuntimeDataStoreInit(t);
    }
  }

  /**
   * Load the plug-in at the location url.
   * 
   * @param url
   *          of the plug-in location.
   */
  public void loadPlugin(URL url) {

    XMLReader reader = new XMLReader();

    InputSource source = null;
    try {
      source = new InputSource(new FileInputStream(url.getFile()));
    } catch (FileNotFoundException e) {
      throw new PluginLoadException(
          "It was not possible to load the plugin at " + url, e);
    }

    IPluginData data = reader.readPluginXMLFile(source, url.getFile(), "");

    foundPlugins.add(data);

    // TODO load classes and register factories in registry

    throw new PluginLoadException(
        "The plug-in "
            + data.getId().getName()
            + " has been loaded but not correctly installed as this method is not completely implemented so far.");
  }

  /**
   * Load the plug-in at the location url.
   * 
   * @param url
   *          of the plug-in location.
   */
  public void loadPluginType(URL url) {

    XMLReader reader = new XMLReader();

    InputSource source = null;
    try {
      source = new InputSource(new FileInputStream(url.getFile()));
    } catch (FileNotFoundException e) {
      throw new PluginLoadException(
          "It was not possible to load the plugin type at " + url, e);
    }

    IPluginTypeData data = reader.readPluginTypeXMLFile(source, url.getFile());

    foundPluginTypes.add(data);

    loadPlugInTypeClasses(data);

    throw new PluginLoadException(
        "The plug-in type "
            + data.getId().getName()
            + " has been loaded but not correctly installed as this method is not completely implemented so far.");
  }

  /**
   * Initializes runtime data storage. Sub-classes may lock on
   * {@link #dataStorage}, to get notified when the initialization of the
   * runtime data storage is done.
   */
  public void initRuntimeDataStorage() {
    try {
      dataStorage.open();
      FactoryDataSynchronizer.addNewFactories(factoryNameMap.values(),
          dataStorage);
      FactoryDataSynchronizer.synchroniseAbstractFactories(
          abstractFactories.keySet(), dataStorage);
      dataStorage.flush();
    } catch (Throwable t) { // NOSONAR:{must_be_robust}
      handleErrorDuringPluginRuntimeDataStoreInit(t);
    } finally {
      synchronized (dataStorage) {
        dataStorage.notifyAll();
      }
    }
  }

  /**
   * Handle error during the initialization of the plugin runtime-data store.
   * 
   * @param t
   *          the problem
   */
  private void handleErrorDuringPluginRuntimeDataStoreInit(Throwable t) {
    SimSystem.report(Level.WARNING,
        "Plugin runtime data storage has been disabled", t);
    useRuntimeDataStorage = false;
  }

  /**
   * Throw exception if {@link #useRuntimeDataStorage} is <code>false</code> or
   * the {@link #dataStorage} has not been initialized yet.
   */
  private void checkRuntimeDataStorageUsage() {
    synchronized (dataStorage) {
      if (!dataStorage.isOpen()) {
        try {
          System.err.println("Waiting to finish");
          dataStorage.wait();
          System.err.println("Woke up");
        } catch (InterruptedException e) {
          SimSystem.report(e);
        }
      }
    }
    if (!useRuntimeDataStorage) {
      throw new UnsupportedOperationException(
          "No runtime data storage is initialized.");
    }
  }

  /**
   * Sets the failure tolerance, which will be stored.
   * 
   * @param failureTolerance
   *          the new failure tolerance
   */
  public void setAndStoreFailureTolerance(FailureTolerance failureTolerance) {
    checkRuntimeDataStorageUsage();
    try {
      dataStorage.setAndStoreFailureTolerance(failureTolerance);
    } catch (Throwable t) { // NOSONAR:{must_be_robust}
      useRuntimeDataStorage = false;
    }
  }

  /**
   * Gets the failure tolerance.
   * 
   * @return the failure tolerance
   */
  public FailureTolerance getFailureTolerance() {
    checkRuntimeDataStorageUsage();
    return dataStorage.getFailureTolerance();
  }

  /**
   * Checks factory state with the configured failure tolerance.
   * 
   * @param factory
   *          the factory to be checked
   */
  protected void checkFactoryState(Factory<?> factory) {
    checkRuntimeDataStorageUsage();
    FailureTolerance tolerance = dataStorage.getFailureTolerance();
    ComponentState factoryState =
        dataStorage.getFactoryData(factory.getClass()).getState();
    if (!tolerance.accept(factoryState)) {
      SimSystem.report(Level.WARNING, "The factory " + factory.getName()
          + " you selected is in state " + factoryState
          + ", while the system's fault tolerance is set to " + tolerance
          + ". Will proceed with factory anyway.");
    }
  }

  /**
   * Checks whether factory is available and its status allows a selection with
   * the current failure tolerance settings.
   * 
   * @param factoryClass
   *          the factory class
   * 
   * @return true, if factory is available and eligible for selection
   */
  public <F extends Factory<?>> boolean factoryAvailable(Class<F> factoryClass) {
    checkRuntimeDataStorageUsage();
    IFactoryInfo infoOfFactory = this.getFactoryInfo(factoryClass.getName());
    if (infoOfFactory == null) {
      return false;
    }
    FactoryRuntimeData<F> frd = dataStorage.getFactoryData(factoryClass);
    if (frd == null) {
      return false;
    }
    return dataStorage.getFailureTolerance().accept(frd.getState());
  }

  /**
   * Gets any factory which complies to the current level of failure tolerance.
   * 
   * @param af
   *          the abstract factory
   * @param parameters
   *          the abstract factory parameters
   * 
   * @return the any factory
   */
  public <F extends Factory<?>> F getFactoryWithTolerance(
      Class<? extends AbstractFactory<F>> af, ParameterBlock parameters) {

    checkRuntimeDataStorageUsage();

    List<F> factoriesForAFwithParameter =
        getInitializedAbstractFactory(af).getFactoryList(parameters);
    for (F factory : factoriesForAFwithParameter) {
      FactoryRuntimeData<?> factoryRuntimeData =
          dataStorage.getFactoryData(factory.getClass());
      if (factoryRuntimeData == null) {
        continue;
      }
      if (dataStorage.getFailureTolerance().accept(
          factoryRuntimeData.getState())) {
        return factory;
      }
    }
    throw new NoFactoryFoundException("None of the "
        + factoriesForAFwithParameter.size()
        + " was accepted with fault tolerance "
        + dataStorage.getFailureTolerance() + "!");
  }

  /**
   * Check whether plug-in type parameters found in plug-in factory definitions
   * are valid.
   */
  @SuppressWarnings("unchecked")
  private void checkPluginTypeParameters() {
    List<IPluginData> plugins = getPlugins();

    for (IPluginData data : plugins) {
      for (IFactoryInfo info : data.getFactories()) {
        for (IParameter p : info.getParameters()) {
          if (p.hasPluginType()) {
            // check whether specified plugin type exists
            String pluginType = p.getPluginType();
            Class<? extends Factory<?>> fc = null;
            try {
              fc =
                  (Class<? extends Factory<?>>) getClassLoader().loadClass(
                      pluginType);
              if (!isBaseFactory(fc)) {
                SimSystem
                    .report(
                        Level.SEVERE,
                        "Plug-in: "
                            + info.getClassname()
                            + " ("
                            + info.getPluginDefLocation()
                            + ")"
                            + " has a plugintype parameter \""
                            + p.getName()
                            + "\" which does not specify a base factory as plug-in type ("
                            + p.getPluginType() + ")");
                // TODO remove plugin from registry
                continue;
              }
            } catch (ClassNotFoundException e) {
              SimSystem.report(
                  Level.SEVERE,
                  "Plug-in: " + info.getClassname() + " ("
                      + info.getPluginDefLocation() + ")"
                      + " has a plug-in type parameter \"" + p.getName()
                      + "\" which does not specify a known class ("
                      + p.getPluginType() + ")");
              // TODO remove plugin from registry
              continue;
            }

            if (p.getDefaultValue() != null && p.getDefaultValue().length() > 0
                && fc != null) {
              // check whether default value is assignable to fc
              try {
                Class<?> dc = getClassLoader().loadClass(p.getDefaultValue());

                if (!fc.isAssignableFrom(dc)) {
                  SimSystem.report(
                      Level.SEVERE,
                      "Plug-in: " + info.getClassname() + " ("
                          + info.getPluginDefLocation() + ")"
                          + " has a plug-in type parameter \"" + p.getName()
                          + "\" which specifies a default value (\""
                          + p.getDefaultValue()
                          + "\") not fitable for the given plug-in type");
                  // TODO remove plugin or remove default
                  // value?
                }
              } catch (ClassNotFoundException e) {
                SimSystem.report(
                    Level.SEVERE,
                    "Plug-in: " + info.getClassname() + " ("
                        + info.getPluginDefLocation() + ")"
                        + " has a plug-in type parameter \"" + p.getName()
                        + "\" which does not specify a known class ("
                        + p.getDefaultValue() + ") as default value");
                // TODO remove plugin from registry
              }
            }

          }
        }
      }
    }

  }

  /**
   * Sets a new hook to recognize factory selection.
   * 
   * @param hook
   *          hook to be set
   */
  public void installFactorySelectionHook(Hook<SelectionInformation<?>> hook) {
    factorySelectionHook = hook;
  }

  /**
   * Load classed defined in the plug-in types. The list of plug-in types to be
   * loaded is taken from the {@link #foundPluginTypes} list.
   */
  private void loadPluginTypesClasses() {
    // let's iterate over the found abstract factories and load these
    for (IPluginTypeData ptd : foundPluginTypes) {
      loadPlugInTypeClasses(ptd);
    }
  }

  /**
   * Load the classes given in the plug-in type passed.
   * 
   * @param ptd
   *          the plug-in type information containing the classes to be loaded
   */
  @SuppressWarnings("unchecked")
  private void loadPlugInTypeClasses(IPluginTypeData ptd) {
    try {
      Class<?> baseFacClass = loadClass(ptd.getBaseFactory());
      if (baseFacClass == null) {
        throw new FactoryLoadingException("Could not load base factory: "
            + ptd.getBaseFactory());
        // jump to the catch block
      }
      Class<?> abstractFacClass = loadClass(ptd.getAbstractFactory());
      if (abstractFacClass == null) {
        throw new FactoryLoadingException("Could not load abstract factory: "
            + ptd.getAbstractFactory());
        // jump to the catch block
      }

      foundPluginsGrouped.put((Class<AbstractFactory<?>>) abstractFacClass,
          new ArrayList<IPluginData>());
      knownBaseFactoryClasses.add((Class<Factory<?>>) baseFacClass);
      abstractFactories.put(
          (Class<? extends AbstractFactory<?>>) abstractFacClass,
          (Class<Factory<?>>) baseFacClass);
      abstractFactoriesInv.put((Class<Factory<?>>) baseFacClass,
          (Class<? extends AbstractFactory<?>>) abstractFacClass);
      SimSystem.report(Level.INFO, "Registry:LoadedPluginType",
          "Installed plug-in type : %s", new Object[] { ptd.getId().getName()
              + " - " + ptd.getId().getVersion() });
    } catch (Exception e) {
      SimSystem.report(Level.SEVERE,
          "Failed on loading a plug-in type (" + ptd.getAbstractFactory()
              + "), skipping plug-in");
      SimSystem.report(e);
    }
  }

  /**
   * Load factory classes.
   * 
   * @param cr
   *          the plug in finder used to find the classes
   */
  @SuppressWarnings("unchecked")
  private void loadFactoryClasses(IPlugInFinder cr) {
    // let's iterate over the found plug-ins, split'em according to their type
    // and register in the type dependent lists which are the base for the
    // factories

    Map<String, IId> pluginMem = new LinkedHashMap<>();

    // int cj = 0;
    for (IPluginData pd : foundPlugins) {
      // System.out.println(cj++);
      // get a list of class fac
      SimSystem.report(Level.INFO, "Loading the plug-in "
          + pd.getId().getName() + " ... ");

      // Test whether a plug-in with the same name has already been
      // loaded
      IId id = pd.getId();
      if (pluginMem.containsKey(id.getName())) {

        // plug-in with the same id already exists, let's create a
        // nice exception

        // search for the other location of the plugin.xml containing
        // the same id
        String pathFirstPlugIn = "";

        for (IPluginData pd2 : foundPlugins) {
          // for (int pd2c = 0; pd2c < foundPlugins.size(); pd2c++) {
          // IPluginData pd2 = foundPlugins.get(pd2c);
          IId id2 = pd2.getId();
          if ((pd != pd2) && (id2.getName().compareTo(id.getName()) == 0)) {
            pathFirstPlugIn = cr.getPaths().get(pd2);
          }
        }

        // fetch the current path
        String s = cr.getPaths().get(pd);

        if (s == null) {
          s = "";
        }

        // if the paths are exactly the same, this is likely a setup problem in
        // the configuration (e.g., a JAR file placed twice on the classpath),
        // but cannot be a true plug-in conflict - just issue a warning
        String loadedPlugInVersion = pluginMem.get(id.getName()).getVersion();
        if (s.equals(pathFirstPlugIn)
            && id.getVersion().equals(loadedPlugInVersion)) {
          SimSystem.report(Level.WARNING,
              "The declaration of plug-in " + id.getName()
                  + " was read TWICE from " + s);
          continue;
        }

        // the same plug-in (according to version information) was found twice,
        // just select one
        if (id.getVersion().equals(loadedPlugInVersion)) {
          SimSystem.report(
              Level.INFO,
              "The declaration of plug-in " + id.getName() + " with "
                  + id.getVersion() + " was read TWICE. Using the one from\n"
                  + pathFirstPlugIn + ", not the one from\n" + s);
          continue;
        }

        // throw the exception containing (hopefully) both paths
        throw new PluginLoadException(
            "Plug-in Conflict detected: the plug-in '" + id.getName()
                + "' was found twice on the classpath (Versions: "
                + id.getVersion() + " and " + loadedPlugInVersion + ") -- " + s
                + " -- and -- " + pathFirstPlugIn);
      }

      pluginMem.put(id.getName(), id);

      List<IFactoryInfo> facs = pd.getFactories();

      boolean first = true;

      if (facs.isEmpty()) {
        SimSystem.report(Level.WARNING,
            "The plug-in definition of " + id.getName()
                + " does not contain a factory.");
      }

      for (IFactoryInfo fac : facs) {
        // load class and instantiate factory
        if (fac.getClassname() == null) {
          SimSystem.report(Level.WARNING, "There is no factory class name for "
              + fac);
          continue;
        }

        Class<?> clazz = loadClass(fac.getClassname());

        if (clazz == null) {
          SimSystem.report(Level.WARNING,
              "Was not able to load factory class of " + fac.getClassname());
          continue;
        }

        if (first) {
          List<IPluginData> pdat =
              foundPluginsGrouped
                  .get(getAbstractFactory((Class<? extends Factory<?>>) clazz));

          if (pdat == null) {
            SimSystem.report(Level.WARNING, "Cannot look up plug-in data for "
                + clazz);
            continue;
          }

          pdat.add(pd);
          first = false;
        }
        Object loadedFactory = null;
        try {
          loadedFactory = instantiate(clazz);
        } catch (NoClassDefFoundError err) {
          // not everything named "...Factory" is a factory in the JamesII
          // sense, sometimes intentionally so
          SimSystem.report(Level.CONFIG, "Failed to load " + fac.getClassname()
              + "as a plug-in. Not a class.");
          continue;
        }
        if (loadedFactory instanceof Factory) {
          registerFactory((Factory<?>) loadedFactory, fac);
        } else {
          SimSystem.report(Level.WARNING,
              "Failed to load a plug-in (" + fac.getClassname()
                  + "). Loaded Factory is not an instance of " + Factory.class);
        }
      }
    }

  }

  /**
   * Load the class specified by the class name.
   * 
   * @param className
   *          the class name
   * @return the class<?>
   */
  private Class<?> loadClass(String className) {

    try {
      return classLoader.loadClass(className);
    } catch (ClassNotFoundException e) {
      SimSystem.report(Level.SEVERE,
          "*** Error/Exception on loading the class:  " + className
              + "  ! *** ", e);
    }
    // no class def found is here NOT an error we can't recover from
    return null;
  }

  /**
   * Tries to load the given language dependent message file.
   * 
   * @param filename
   *          the file to be read
   */
  public void loadLanguage(String filename) {
    // read file
    // TODO (general) language stuff
    // fetch msgid, msgstr pairs and add them to the messageTable
  }

  /**
   * Register a factory.
   * 
   * @param loadedFactory
   *          the factory to be registered
   * @param facInfo
   *          the factory information
   */
  public void registerFactory(Factory<?> loadedFactory, IFactoryInfo facInfo) {

    loadedFactory.setName(loadedFactory.getClass().getName());

    // search for the correct ancestor type of the factory to be
    // registered
    Class<? extends Factory<?>> fc2 =
        getAncestor((Class<? extends Factory<?>>) loadedFactory.getClass());
    if (fc2 != null) {
      // register in the "general" factories list
      List<Factory<?>> list = factories.get(fc2);
      if (list == null) {
        list = new ArrayList<>();
        factories.put(fc2, list);
      }
      list.add(loadedFactory);
    } else {
      // TODO: AUTO REGISTERING? WHAT TO DO WITH UNKNWON FACTORY
      // CLASSES?

      throw new FactoryLoadingException("Unable to find a base-factory for "
          + loadedFactory.getName());
    }

    // load the language and factory dependant messages into the
    // general
    // messageTable
    // TODO (general) loadLanguage (loadedFactory+".po");
    if (loadedFactory instanceof ModelFactory) {
      Formalism form = ((ModelFactory) loadedFactory).getFormalism();
      registerFormalism(form.getIdent(), form);
    }

    // Register factory information
    factoryInfo.put(loadedFactory.getClass().getName(), facInfo);
    factoryNameMap.put(loadedFactory.getClass().getCanonicalName(),
        loadedFactory);
  }

  /**
   * Registers a formalism.
   * 
   * @param identStr
   *          identifier of the formalism
   * @param formalism
   *          the formalism
   */
  public void registerFormalism(String identStr, Formalism formalism) {
    informationObjects.put(identStr, formalism);
    formalisms.addFormalism(identStr, formalism);
  }

  /**
   * Retrieves factory information for a factory with a given class name.
   * 
   * @param factoryClassName
   *          fully-qualified class name of factory
   * @return the factory information, or null if not available
   */
  public IFactoryInfo getFactoryInfo(String factoryClassName) {
    return factoryInfo.get(factoryClassName);
  }

  /**
   * Gets the custom plug-in directory. That's the default, custom plug-in
   * directory to search for plug-ins in. If not set the system wide default
   * plug-in directory will be used instead.
   * 
   * @return the custom plug-in directory
   */
  public static String getCustomPluginDirectory() {
    return customPluginDirectory;
  }

  /**
   * Sets the custom plug-in directory.
   * 
   * @param customPluginDirectory
   *          the new custom plug-in directory
   */
  public static void setCustomPluginDirectory(String customPluginDirectory) {
    Registry.customPluginDirectory = customPluginDirectory;
  }

  /**
   * Gets the start up time. That's the time of the first instantiation of the
   * registry object of the simulation system. Usually there is only one
   * instance of the registry class per sim system, thus
   * SimSystem.getRegistry().getStartUpTime() should always return the same
   * value.
   * 
   * @return the start up time
   */
  public long getStartUpTime() {
    return startUpTime;
  }

  /**
   * Instantiates James II factory given by class. Uses reflection and calls
   * empty constructor (which all factories need to have).
   * 
   * @param <F>
   *          the type of the factory to be returned
   * @param factoryClass
   *          the factory class
   * @return a new instance of the factory
   */
  public <F extends Factory<?>> F instantiateFactory(Class<F> factoryClass) {
    F factory = null;

    try {
      factory = factoryClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      SimSystem
          .report(
              Level.SEVERE,
              "Factory '"
                  + factoryClass.getCanonicalName()
                  + "' could not be instantiated (empty public constructor required).",
              e);
      return null;
    }
    return factory;
  }

  /**
   * Try to get the plug-in type information by using the plug-in data passed.
   * 
   * @param plugin
   * @return the plug-in type data of the plug-in or null
   */
  public IPluginTypeData getPluginType(IPluginData plugin) {

    for (Map.Entry<Class<? extends AbstractFactory<?>>, List<IPluginData>> entry : foundPluginsGrouped
        .entrySet()) {

      if (entry.getValue().contains(plugin)) {
        return getPluginType(entry.getKey());
      }

    }

    return null;
  }

  /**
   * Get the plug-in a factory is given in.
   * 
   * @param factory
   * @return the factory information or null
   */
  public IPluginData getPlugin(IFactoryInfo factory) {

    for (IPluginData data : foundPlugins) {

      if (data.getFactories().contains(factory)) {
        return data;
      }
    }

    return null;
  }

  /**
   * Gets the abstract factories.
   * 
   * @return the abstractFactories
   */
  protected Map<Class<? extends AbstractFactory<?>>, Class<? extends Factory<?>>> getAbstractFactories() {
    return abstractFactories;
  }

  /**
   * Get the value of the dataStorage.
   * 
   * @return the dataStorage
   */
  public final IFactoryRuntimeDataStorage getDataStorage() {
    checkRuntimeDataStorageUsage();
    return dataStorage;
  }

  /**
   * Get the value of the factoryNameMap.
   * 
   * @return the factoryNameMap
   */
  protected final Map<String, Factory<?>> getFactoryNameMap() {
    return factoryNameMap;
  }

  /**
   * Returns true if the class path has been excluded from the plug-in search.
   * 
   * @return true or false
   */
  public static boolean isExcludeClassPathOnSearch() {
    return excludeClassPathOnSearch;
  }

  /**
   * The class path can be excluded from the search by using this method. To
   * exclude pass true, the default is false (class path included in search).
   * This method can only be called BEFORE the first search!
   * 
   * @param excludeClassPathOnSearch
   * @return true if it was possible to exclude the class path; false otherwise
   */
  public static boolean setExcludeClassPathOnSearch(
      boolean excludeClassPathOnSearch) {
    if (SimSystem.hasRegistry()) {
      SimSystem
          .report(
              Level.INFO,
              "It was not possible to exclude the class path as it has already been used to create the registry. Call this method earlier!");
      return false;
    }
    Registry.excludeClassPathOnSearch = excludeClassPathOnSearch;
    return true;
  }

  /**
   * Get the abstract factory class of the plug-in type passed-
   * 
   * @param pluginType
   *          Either the canonical class name or the ident specified in the
   *          plug-in type file.
   * @return The class of the abstract factory of the plug-in type or null if no
   *         class can be found for the pluginType passed.
   */
  @SuppressWarnings("unchecked")
  public Class<AbstractFactory<Factory<?>>> getFactoryType(String pluginType) {

    Class<?> result = null;
    try {
      result = Class.forName(pluginType);
    } catch (ClassNotFoundException e) {

      for (IPluginTypeData ptd : foundPluginTypes) {
        if (ptd.getId().getName().compareTo(pluginType) == 0) {
          try {
            result = Class.forName(ptd.getAbstractFactory());
          } catch (ClassNotFoundException e1) {
            // silent - we will return null
          }
        }
      }

    }
    return (Class<AbstractFactory<Factory<?>>>) result;
  }

  /**
   * Create a new context and return it.
   */
  public Context createContext() {
    return new Context();
  }

}
