/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.jamesii.core.Language;
import org.jamesii.core.Registry;
import org.jamesii.core.math.random.rnggenerator.IRNGGenerator;
import org.jamesii.core.math.random.rnggenerator.simple.CurrentTimeRandSeedGenerator;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.info.JavaInfo;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * Class that holds objects and functions of central interest for all JAMES
 * classes. <br/>
 * 
 * This class is comparable to Java's "System" class. It provides access to the
 * core M&S framework. The Registry should be retrieved using the
 * {@link #getRegistry()} method whenever needed - it is created only once and
 * cached in here. In addition information about paths to be used by the
 * framework can be retrieved ( {@link #getConfigDirectory()},
 * {@link #getWorkingDirectory()}, {@link #getTempDirectory()},
 * {@link #getPlugInDirectories()}), messages can be reported (to the console or
 * to a special target like a file, a windows in a GUI, a.s.o.. It is considered
 * to be good practice to use the report methods defined in here for any
 * information, warning or error reporting -- for the core as well as for any
 * plug-in. The framework's version information is contained in here as well,
 * and accessible via the constants {@link #SYSTEMNAME}, currently
 * {@value #SYSTEMNAME}; {@link #SIMSYSTEM}, currently {@value #SIMSYSTEM}. <br/>
 * <h1>Utility methods</h1> The {@link #getUId()} method generates ascending
 * numbers, the {@link #getUniqueName()} methods generates names unique for each
 * instance of the framework.
 * 
 * <br/>
 * Via the {@link #getEncoding()} method the default encoding (to be) used
 * throughout the framework can be retrieved. If no system specific default
 * encoding can be retrieved the {@link #DEFAULTENCODING} will be returned.
 * 
 * <br/>
 * The {@link #getRNGGenerator()} method returns a
 * {@link org.jamesii.core.math.random.rnggenerator.IRNGGenerator} which can be
 * used to fetch a well initialized random number generator.
 * 
 * <br>
 * It is not possible to create instances of this class because there is no need
 * to do so. All attributes and methods are static.
 * 
 * <br/>
 * You should use the centralized error/warning/information reporting/logging
 * approach by using the {@link #report(Throwable)},
 * {@link #report(Level, String)}, {@link #report(Level, String, Throwable)},
 * {@link #report(Level, String, String, Object[])}, and the
 * {@link #report(Level, String, String, Object[], Throwable)} methods.
 * 
 * @author Jan Himmelspach
 */
public final class SimSystem {

  /**
   * Encoding to be used (e.g., for file names). Is used in the getEncoding
   * method as fallback.
   */
  private static final String DEFAULTENCODING = "UTF-8";

  /** Reference to the "singleton" of the registry. */
  private static Registry registry = null;

  /** The system's current random seed generator. */
  private static IRNGGenerator rngGenerator =
      new CurrentTimeRandSeedGenerator();

  /** The Constant majorVersion. */
  private static final String MAJORVERSION = "II";

  /** The Constant systemName. */
  public static final String SYSTEMNAME = "JAMES";

  /** The name of the simulation (name + major version). */
  public static final String SIMSYSTEM = SYSTEMNAME + " " + MAJORVERSION;

  /** The uid_counter. */
  private static long uidCounter = -1;

  /** The (minor) version of the simulation system. */
  public static final String VERSION = "0.9.4";

  /**
   * Helper variable to ensure that a "unique" name is not reported back more
   * than once.
   */
  private static Long lastUniqueName = Long.valueOf(0);

  /**
   * Flag to check whether {@link #getRegistry()} was called during
   * {@link #createRegistry()}.
   */
  private static boolean registryCreationInProgress = false;

  /**
   * Hidden constructor.
   */
  private SimSystem() {
  }

  /**
   * Returns the credits.
   * 
   * @return A non-empty string containing several lines (credits).
   */
  public static String getCredits() {

    String result = "";
    result += "Created at the University of Rostock";
    result += "\n started in 2003";
    result +=
        "\n in a project named CoSA, financed by the german research foundation,";
    result += "\n based on an architecture named ''Plug'n simulate''.";
    result +=
        "\n JAMES II is a JAva based Multipurpose Environment for Simulation.";

    return result;
  }

  /**
   * Get encoding name. Tries to fetch the system default encoding. If this
   * fails the value of {@link #DEFAULTENCODING} is returned.
   * 
   * @return encoding identifier (conforms to encoding names in java.io).
   */
  public static String getEncoding() {
    String encoding = System.getProperty("file.encoding");
    if (encoding != null) {
      return encoding;
    }
    return DEFAULTENCODING;
  }

  /**
   * Returns extensive information which can be printed if an error occurs.
   * 
   * @return A not empty string containing information about the system.
   */
  public static String getExceptionTraceInformation() {
    String s = SIMSYSTEM;

    s += "\n";
    s += "\n Running on:";
    s += "\n" + getVMInfo();

    s += "\n";

    // s += "\nClasspath: ";
    // s += System.getProperty("java.class.path");
    //
    // s += "\nClass version: ";
    // s += System.getProperty("java.class.version");
    //
    // s += "\nJava directory: ";
    // s += System.getProperty("java.home");

    // s += "\nOperating system architecture: ";
    // s += System.getProperty("os.arch");

    s += "\nOperating system name: ";
    s += System.getProperty("os.name");

    // s += "\nUser's current working directory: ";
    // s += System.getProperty("user.home");
    //
    // s += "\nUser account name: ";
    // s += System.getProperty("user.name");

    s += "\nList of registered plugins: ";
    s += getRegistry().getKnownFactoryClasses();
    // s += getRegistry().getPluginList();

    return s;
  }

  /**
   * Get the registry of the simulation system. If this is done the first time
   * the registry will be created and a reference to this registry will be kept.
   * 
   * @return the main registry where all factories get registered
   */
  public static Registry getRegistry() {
    if (registry == null) {
      createRegistry();
    }
    return registry;
  }

  /**
   * Return true if the registry has already been created, false otherwise.
   * 
   * @return true if the plug-in registry has been created
   */
  public static boolean hasRegistry() {
    return registry != null;
  }
 /**
   * Creates the default registry in a thread-safe manner. This method is
   * separated from {@link #getRegistry()} because it is
   * <code>synchronized</code> (i.e. a monitor is acquired) but only has to be
   * called once at start-up.
   */
  protected static synchronized void createRegistry() {
    if (registry == null) {
      initializeAndSetRegistry(new Registry());
    }
  }

  /**
   * Sets the registry, but only if the registry has not been set yet. The
   * registry will automatically set to its default when
   * {@link SimSystem#getRegistry()} is called the first time.
   * 
   * @param reg
   *          the registry to be used
   */
  public static synchronized void setRegistry(Registry reg) {
    if (registry != null) {
      report(Level.SEVERE, "The system is already configured for registry "
          + registry.getClass().getName() + "! Ignoring call to set registry "
          + reg + ".");
      return;
    }
    initializeAndSetRegistry(reg);
  }

  /**
   * Initialize and set registry. To prevent a cyclic re-entry during
   * {@link Registry#initPlugins} (only possible from within the same thread, as
   * the method should only be called from a <code>synchronized</code> method),
   * the flag ({@link #registryCreationInProgress}) is set to check for this
   * kind of error.
   * 
   * @param newRegistry
   *          the registry to be initialized
   */
  private static void initializeAndSetRegistry(Registry newRegistry) {
    if (registryCreationInProgress) {
      throw new IllegalStateException(
          "set/getRegistry() was called during initialization of Registry");
    }
    registryCreationInProgress = true;
    newRegistry.initPlugins();
    registryCreationInProgress = false;
    registry = newRegistry;
    registry.initRuntimeDataStorage();
  }

  /**
   * This implementation returns a UID. This implementation fails for
   * distributed computing!
   * 
   * You might be interested in the
   * {@link org.jamesii.core.util.id.UniqueIDGenerator} as well.
   * 
   * @return a unique number (per run).
   */
  public static synchronized long getUId() {
    return ++uidCounter;
  }

  /**
   * Gets the unique name. A call will generate a unique, numerical name.
   * 
   * You might be interested in the
   * {@link org.jamesii.core.util.id.UniqueIDGenerator} as well.
   * 
   * @return the unique name
   */
  public static synchronized String getUniqueName() {
    Long l = Long.valueOf(Calendar.getInstance().getTimeInMillis());

    // we need this here to be sure that we do not fix the unique name
    // to one we already had
    // this can happen if more names are retrieved as ms have been elapsed
    while (l.compareTo(lastUniqueName) <= 0) {
      l++;
    }

    lastUniqueName = l;
    return l.toString();
  }

  /**
   * Returns a string containing many information about the virtual machine.
   * 
   * @return A not empty (and not null) string containing information about the
   *         virtual machine.
   */
  public static String getVMInfo() {
    String s = "Information about the virtual machine";

    s += new JavaInfo().toString();

    return s;
  }

  /**
   * Gets the RNG generator.
   * 
   * @return the RNG generator
   */
  public static IRNGGenerator getRNGGenerator() {
    return rngGenerator;
  }

  /**
   * Sets the rand seed generator.
   * 
   * @param randSeedGenerator
   *          the new rand seed generator
   */
  public static void setRandSeedGenerator(IRNGGenerator randSeedGenerator) {
    SimSystem.rngGenerator = randSeedGenerator;
  }

  /**
   * Returns the directory JAMES II was started from. This directory is used as
   * working directory for the framework. If the user has not specified an extra
   * configuration nor an extra temporary directory this directory is used for
   * these purposes as well.
   * 
   * @return the working directory
   */
  public static String getWorkingDirectory() {
    return System.getProperty("user.dir");
  }

  /**
   * Returns the configuration directory of JAMES II. This is for instance where
   * to find the main configuration file for the JAMES GUI. It is equal to
   * {@link #getWorkingDirectory()} if not different specified as environment
   * variable <b>JAMES_CONFIG_DIR</b>.
   * 
   * @return the configuration directory of JAMES II
   */
  public static String getConfigDirectory() {
    // get config file location
    String configDir = System.getenv("JAMES_CONFIG_DIR");
    // check config directory for existence
    if (configDir != null) {
      File cDir = new File(configDir);
      if (cDir.exists() && cDir.isDirectory()) {
        return cDir.getAbsolutePath();
      }
      SimSystem.report(Level.INFO,
          "Could not find config directory: " + cDir.getAbsolutePath());
    }

    return SimSystem.getWorkingDirectory();
  }

  /**
   * Returns the temporary directory of JAMES II. The return value should be
   * used throughout the framework whenever there is a need to create a
   * temporary file. It is equal to {@link #getWorkingDirectory()} if not
   * differently specified as environment variable <b>JAMES_TEMPPATH</b> (which
   * has to be an existing directory) or java.io.tmpdir (which has to be an
   * existing directory) in that order.<br/>
   * 
   * You can use the {@link org.jamesii.core.util.misc.TemporaryFileManager} for
   * a more convenient management of files in the temp directory.
   * 
   * @return the temporary directory used by JAMES II
   */
  public static String getTempDirectory() {
    // get temp dir path
    String tempDir = System.getenv("JAMES_TEMPPATH");
    // check temp directory for existence
    if (tempDir != null) {
      File cDir = new File(tempDir);
      if (cDir.exists() && cDir.isDirectory()) {
        return cDir.getAbsolutePath();
      }
      SimSystem.report(Level.INFO, "Could not find JAMES II temp directory: "
          + cDir.getAbsolutePath());
    }
    // if no special JAMES II temp directory is given we try using the
    // system
    // wide temp directory
    tempDir = System.getProperty("java.io.tmpdir");
    // check temp directory for existence
    if (tempDir != null) {
      File cDir = new File(tempDir);
      if (cDir.exists() && cDir.isDirectory()) {
        return cDir.getAbsolutePath();
      }
      SimSystem.report(Level.INFO, "Could not find system temp directory: "
          + cDir.getAbsolutePath());
    }

    return SimSystem.getWorkingDirectory();
  }

  /**
   * Gets the plug in directories. From the environment variable
   * "JAMES_PLUGINPATH". If this is not set the default working directory will
   * be returned instead. Delimiter between the paths is a ";".
   * 
   * @return the plug in directories as list of strings representing the
   *         absolute paths to the directories
   */
  public static List<String> getPlugInDirectories() {

    // get temp dir path
    String plugInDirs = System.getenv("JAMES_PLUGINPATH");
    
    List<String> result = new ArrayList<String>();
    // check temp directory for existence
    if (plugInDirs != null) {
      StringTokenizer st = new StringTokenizer(plugInDirs, ";");
      while (st.hasMoreTokens()) {
        String pDir = st.nextToken();
        File cDir = new File(pDir);
        if (!cDir.exists() || !cDir.isDirectory()) {
          SimSystem.report(Level.INFO,
              "Could not find plug-in directory specified in the environment variable: "
                  + cDir.getAbsolutePath());
        } else {
          result.add(cDir.getAbsolutePath());
        }
      }
      return result;
    }

    result.add(SimSystem.getWorkingDirectory());

    return result;
  }

  /**
   * Print the string s by using the params. If there is a language dependent
   * version of s use this one instead ... <br>
   * If console logging is enabled (see
   * {@link ApplicationLogger#enableConsoleLog()}), the text will be printed to
   * the default output console defined by System.out. If false the message will
   * not be printed at all.<br>
   * Independent from printing to the console all messages are written to the
   * applications log file via the {@link ApplicationLogger} class.
   * 
   * @see String#format(String, Object...)
   * @see java.util.logging.Level
   * @param id
   *          the id of the string used in language dependent files
   * @param s
   *          the text to be printed
   * @param params
   *          the parameters to be used to format the text (there might be place
   *          holders in the text)
   * @param level
   *          the log level, only used for feeding the ApplicationLogger to be
   *          used
   */
  public static void report(Level level, String id, String s, Object... params) {
    String msg = outputLogMsg(id, s, params);
    ApplicationLogger.log(level, msg);
  }

  /**
   * Print the string s by using the params. If there is a language dependent
   * version of s use this one instead ... <br>
   * 
   * @param object
   *          the object the entry is created for
   * @param type
   *          the type (class) of the object created
   * @param block
   *          the parameters used to create the block
   * @param s
   *          the text to be printed
   * @param params
   *          the parameters to be used to format the text (there might be place
   *          holders in the text)
   */
  public static void reportCreation(Object object, Class<?> type,
      ParameterBlock block, String s, Object[] params) {
    String msg = outputLogMsg(null, s, params);
    ApplicationLogger.logCreation(object, type, block, msg);
  }

  /**
   * If console logging is enabled (see
   * {@link ApplicationLogger#enableConsoleLog()}), the text will be printed to
   * the default output console defined by System.out. If false the message will
   * not be printed at all.<br>
   * Independent from printing to the console all messages are written to the
   * applications log file via the {@link ApplicationLogger} class.
   * 
   * @see String#format(String, Object...)
   * @see java.util.logging.Level
   * @param s
   *          the text to be printed
   * @param level
   *          the log level, only used for feeding the ApplicationLogger to be
   *          used
   */
  public static void report(Level level, String s) {
    String msg = outputLogMsg(null, s, null);
    ApplicationLogger.log(level, msg);
  }

  /**
   * If console logging is enabled (see
   * {@link ApplicationLogger#enableConsoleLog()}), the text will be printed to
   * the default output console defined by System.out. If false the message will
   * not be printed at all.<br>
   * Independent from printing to the console all messages are written to the
   * applications log file via the {@link ApplicationLogger} class.
   * 
   * @see String#format(String, Object...)
   * @see java.util.logging.Level
   * @param s
   *          the text to be printed
   * @param level
   *          the log level, only used for feeding the ApplicationLogger to be
   *          used
   * @param thrown
   *          the {@link Throwable}
   */
  public static void report(Level level, String s, Throwable thrown) {
    String msg = outputLogMsg(null, s, null);
    ApplicationLogger.log(level, msg, thrown);
  }

  /**
   * Report the throwable. If console logging is enabled (see
   * {@link ApplicationLogger#enableConsoleLog()}), the stack trace will be
   * printed to the default error output console defined by System.err. If false
   * the trace will not be printed at all.<br>
   * Independent from printing to the console all throwables (exceptions,
   * errors, ...) are written to the applications log file via the
   * {@link ApplicationLogger} class.
   * 
   * @param throwable
   *          the {@link Throwable} describing the actual problem
   */
  public static void report(Throwable throwable) {
    ApplicationLogger.log(throwable);
  }

  /**
   * Report an error message including a throwable. Basically this combines
   * {@link SimSystem#report(Level, String, String, Object[])} and
   * {@link SimSystem#report(Throwable)}, and the {@link ApplicationLogger} is
   * only called once. This allows to combine a user-readable error message with
   * the {@link Throwable} describing the actual problem.
   * 
   * @param level
   *          the severity level
   * @param id
   *          the id of the message
   * @param s
   *          the message
   * @param params
   *          the parameters for message lookup
   * @param throwable
   *          the throwable
   */
  public static void report(Level level, String id, String s, Object[] params,
      Throwable throwable) {
    String msg = outputLogMsg(id, s, params);
    ApplicationLogger.log(level, msg, throwable);
  }

  /**
   * Retrieves log message and returns it. Also outputs it if consoleOut is
   * true.
   * 
   * @param id
   *          the id of the message
   * @param s
   *          the message
   * @param params
   *          the parameters for looking it up in the registry
   * 
   * @return the string
   */
  private static String outputLogMsg(String id, String s, Object[] params) {
    return Language.getMessage(id, s, params);
  }

  /**
   * Shut down the JVM (see {@link System#exit(int)}).
   * 
   * @param code
   *          exit status
   */
  public static void shutDown(int code) {
    System.exit(code);
  }

}
