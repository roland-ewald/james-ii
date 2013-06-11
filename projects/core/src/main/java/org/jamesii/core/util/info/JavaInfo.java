/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.info;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Strings;

/**
 * Collects information about the Java Runtime engine which is currently used.
 * 
 * @author Jan Himmelspach
 */
public class JavaInfo implements Serializable {

  /**
   * Class path as constant. If this is changed in Java this is the place to
   * change the constant in the framework.
   */
  public static final String CLASSPATH = "java.class.path";

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6146617774059639787L;

  /** The class format version. */
  private String classFormatVersion;

  /** The class path. */
  private String classPath;

  /** The count of cpus. */
  private int cpus;

  /** The extension dir. */
  private String extensionDir;

  /** The free memory. */
  private long freeMemory;

  /** The home directory. */
  private String instDir;

  /** The maximum memory. */
  private long maxMemory;

  /** The name. */
  private String name;

  /** The operating system. */
  private String os;

  /** The operating system arch. */
  private String osArch;

  /** The operating system version. */
  private String osVersion;

  /** The time when the session started. */
  private Date sessionStarted;

  /** The count of running threads. */
  private int threads;

  /** The total memory. */
  private long totalMemory;

  /** The running time. */
  private long upTime;

  /** The user home directory. */
  private String userHome;

  /** The user name. */
  private String userName;

  /** The user working directory. */
  private String userWorkingDir;

  /** The vendor. */
  private String vendor;

  /** The version. */
  private String version;

  /** The host name. */
  private String hostName;

  /** The JIT compiler */
  private String jitCompiler;

  /**
   * Get the JIT compiler information.
   * 
   * @return a string containing information about the JIT
   */
  public String getJitCompiler() {
    return jitCompiler;
  }

  /**
   * Set the JIT compiler information
   * 
   * @param jitCompiler
   *          information
   */
  public void setJitCompiler(String jitCompiler) {
    this.jitCompiler = jitCompiler;
  }

  /**
   * Get the library path if set.
   * 
   * @return a string containing the library path
   */
  public String getLibraryPath() {
    return libraryPath;
  }

  /**
   * Set the library path.
   * 
   * @param libraryPath
   */
  public void setLibraryPath(String libraryPath) {
    this.libraryPath = libraryPath;
  }

  /**
   * Get the IO temp directory.
   * 
   * @return a string containing the IO temp directory
   */
  public String getIOTmpDir() {
    return ioTmpDir;
  }

  /**
   * Set the information about the IO tmp dir.
   * 
   * @param ioTmpDir
   */
  public void setIOTmpDir(String ioTmpDir) {
    this.ioTmpDir = ioTmpDir;
  }

  /** The path to search for libraries */
  private String libraryPath;

  /** The temp path used */
  private String ioTmpDir;

  /** The MAC address of the system in use. */
  private final List<byte[]> macAddresses = new ArrayList<>();

  /**
   * Default constructor, reads all information from MX beans etc.
   */
  public JavaInfo() {
    Runtime r = Runtime.getRuntime();

    name = ManagementFactory.getRuntimeMXBean().getVmName();
    version = ManagementFactory.getRuntimeMXBean().getVmVersion();
    vendor = ManagementFactory.getRuntimeMXBean().getVmVendor();

    instDir = System.getProperty("java.home");
    classFormatVersion = System.getProperty("java.class.version");
    classPath = System.getProperty(CLASSPATH);
    extensionDir = System.getProperty("java.ext.dir");

    jitCompiler = System.getProperty("java.compiler");
    libraryPath = System.getProperty("java.library.path");
    ioTmpDir = System.getProperty("java.io.tmpdir");

    sessionStarted =
        new Date(ManagementFactory.getRuntimeMXBean().getStartTime());
    upTime = ManagementFactory.getRuntimeMXBean().getUptime();

    os = ManagementFactory.getOperatingSystemMXBean().getName();
    osVersion = ManagementFactory.getOperatingSystemMXBean().getVersion();
    osArch = System.getProperty("os.arch");

    cpus = r.availableProcessors();
    totalMemory = r.totalMemory();
    freeMemory = r.freeMemory();
    maxMemory = r.maxMemory();

    threads = ManagementFactory.getThreadMXBean().getThreadCount();

    userName = System.getProperty("user.name");
    userHome = System.getProperty("user.home");
    userWorkingDir = System.getProperty("user.dir");

    try {
      hostName = java.net.InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      SimSystem.report(e);
    }

    Enumeration<NetworkInterface> nwInterfaces;
    try {
      nwInterfaces = NetworkInterface.getNetworkInterfaces();

      while (nwInterfaces.hasMoreElements()) {
        NetworkInterface nwInterface = nwInterfaces.nextElement();
        if (nwInterface.isUp() && !nwInterface.isLoopback()) {
          byte[] macAddress;
          try {
            macAddress = nwInterface.getHardwareAddress();

            if (macAddress != null) {
              macAddresses.add(macAddress);
            }
          } catch (SocketException e) {
            SimSystem.report(e);
          }
        }
      }

    } catch (SocketException e) {
      SimSystem.report(e);
    }

  }

  /**
   * The Constructor.
   * 
   * @param empty
   *          TRUE means, the object will be constructed w/o any further
   *          initialization. <br>
   *          However, FALSE calls JavaInfo().
   */
  public JavaInfo(boolean empty) {
    if (!empty) {
      new JavaInfo();
    }
  }

  /**
   * Gets the class format version.
   * 
   * @return the class format version
   */
  public String getClassFormatVersion() {
    return classFormatVersion;
  }

  /**
   * Gets the class path.
   * 
   * @return the class path
   */
  public String getClassPath() {
    return classPath;
  }

  /**
   * Gets the count of cpus.
   * 
   * @return the cpus
   */
  public int getCpus() {
    return cpus;
  }

  /**
   * Gets the extension dir.
   * 
   * @return the extension dir
   */
  public String getExtensionDir() {
    return extensionDir;
  }

  /**
   * Gets the free memory.
   * 
   * @return the free memory
   */
  public long getFreeMemory() {
    return freeMemory;
  }

  /**
   * Gets the java home directory.
   * 
   * @return the directory
   */
  public String getInstDir() {
    return instDir;
  }

  /**
   * Gets the max memory.
   * 
   * @return the max memory
   */
  public long getMaxMemory() {
    return maxMemory;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the operating system.
   * 
   * @return the operating system
   */
  public String getOs() {
    return os;
  }

  /**
   * Gets the operating system arch.
   * 
   * @return the operating system arch
   */
  public String getOsArch() {
    return osArch;
  }

  /**
   * Gets the operating system version.
   * 
   * @return the operating system version
   */
  public String getOsVersion() {
    return osVersion;
  }

  /**
   * Gets the time when the session started.
   * 
   * @return the session start time
   */
  public Date getSessionStarted() {
    return sessionStarted;
  }

  /**
   * Gets the count of running threads.
   * 
   * @return the thread count
   */
  public int getThreads() {
    return threads;
  }

  /**
   * Gets the total memory.
   * 
   * @return the total memory
   */
  public long getTotalMemory() {
    return totalMemory;
  }

  /**
   * Gets the running time.
   * 
   * @return the up time
   */
  public long getUpTime() {
    return upTime;
  }

  /**
   * Gets the user home directory.
   * 
   * @return the user home directory
   */
  public String getUserHome() {
    return userHome;
  }

  /**
   * Gets the user name.
   * 
   * @return the user name
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Gets the user working directory.
   * 
   * @return the user working directory
   */
  public String getUserWorkingDir() {
    return userWorkingDir;
  }

  /**
   * Gets the vendor.
   * 
   * @return the vendor
   */
  public String getVendor() {
    return vendor;
  }

  /**
   * Gets the version.
   * 
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the class format version (just the info!).
   * 
   * @param classFormatVersion
   *          the new class format version
   */
  public void setClassFormatVersion(String classFormatVersion) {
    this.classFormatVersion = classFormatVersion;
  }

  /**
   * Sets the class path (just the info!).
   * 
   * @param classPath
   *          the new class path
   */
  public void setClassPath(String classPath) {
    this.classPath = classPath;
  }

  /**
   * Sets the count of cpus (just the info!).
   * 
   * @param cpus
   *          the new count of cpus
   */
  public void setCpus(int cpus) {
    this.cpus = cpus;
  }

  /**
   * Sets the extension directory (just the info!).
   * 
   * @param extensionDir
   *          the new extension directory
   */
  public void setExtensionDir(String extensionDir) {
    this.extensionDir = extensionDir;
  }

  /**
   * Sets the free memory (just the info!).
   * 
   * @param freeMemory
   *          the new free memory
   */
  public void setFreeMemory(long freeMemory) {
    this.freeMemory = freeMemory;
  }

  /**
   * Sets the java home directory (just the info!).
   * 
   * @param instDir
   *          the new java home directory
   */
  public void setInstDir(String instDir) {
    this.instDir = instDir;
  }

  /**
   * Sets the max memory (just the info!).
   * 
   * @param maxMemory
   *          the new max memory
   */
  public void setMaxMemory(long maxMemory) {
    this.maxMemory = maxMemory;
  }

  /**
   * Sets the name (just the info!).
   * 
   * @param name
   *          the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the operating system (just the info!).
   * 
   * @param os
   *          the new operating system info
   */
  public void setOs(String os) {
    this.os = os;
  }

  /**
   * Sets the operating system arch (just the info!).
   * 
   * @param osArch
   *          the new operating system arch info
   */
  public void setOsArch(String osArch) {
    this.osArch = osArch;
  }

  /**
   * Sets the operating system version (just the info!).
   * 
   * @param osVersion
   *          the new operating system version info
   */
  public void setOsVersion(String osVersion) {
    this.osVersion = osVersion;
  }

  /**
   * Sets the time when the session started (just the info!).
   * 
   * @param sessionStarted
   *          the new session started time
   */
  public void setSessionStarted(Date sessionStarted) {
    this.sessionStarted = (Date) sessionStarted.clone();
  }

  /**
   * Sets the count of running threads (just the info!).
   * 
   * @param threads
   *          the new thread count
   */
  public void setThreads(int threads) {
    this.threads = threads;
  }

  /**
   * Sets the total memory (just the info!).
   * 
   * @param totalMemory
   *          the new total memory
   */
  public void setTotalMemory(long totalMemory) {
    this.totalMemory = totalMemory;
  }

  /**
   * Sets the running time of the system (just the info!).
   * 
   * @param upTime
   *          the new running time
   */
  public void setUpTime(long upTime) {
    this.upTime = upTime;
  }

  /**
   * Sets the user home (just the info!).
   * 
   * @param userHome
   *          the new user home
   */
  public void setUserHome(String userHome) {
    this.userHome = userHome;
  }

  /**
   * Sets the user name (just the info!).
   * 
   * @param userName
   *          the new user name
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Sets the user working directory (just the info!).
   * 
   * @param userWorkingDir
   *          the new user working directory
   */
  public void setUserWorkingDir(String userWorkingDir) {
    this.userWorkingDir = userWorkingDir;
  }

  /**
   * Sets the vendor (just the info!).
   * 
   * @param vendor
   *          the new vendor
   */
  public void setVendor(String vendor) {
    this.vendor = vendor;
  }

  /**
   * Sets the version (just the info!).
   * 
   * @param version
   *          the new version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();

    result.append("\nName: ");
    result.append(getName());
    result.append("\nVersion: ");
    result.append(getVersion());
    result.append("\nVendor: ");
    result.append(getVendor());

    result.append("\nVM installation dir: ");
    result.append(getInstDir());
    result.append("\nclass format version number: ");
    result.append(getClassFormatVersion());
    result.append("\nclass path: ");
    result.append(getClassPath());
    result.append("\nJIT compiler: ");
    result.append(getJitCompiler());
    result.append("\nextension dir: ");
    result.append(getExtensionDir());
    result.append("\nlibrary path: ");
    result.append(getLibraryPath());

    result.append("\n\nSession started: ");
    result.append(getSessionStarted());
    result.append("\nUp time: ");
    result.append(getUpTime());

    result.append("\n\nRunning on: ");
    result.append(getOs());
    result.append("\nVersion: ");
    result.append(getOsVersion());
    result.append("\nArchitecture: ");
    result.append(getOsArch());

    result.append("\n\nAvailable CPUs: ");
    result.append(getCpus());
    result.append("\nAmount of memory: ");
    result.append(Files.convertToHumanReadableSize(getTotalMemory(), "bytes"));
    result.append("\nAmount of free memory: ");
    result.append(Files.convertToHumanReadableSize(getFreeMemory(), "bytes"));
    result.append("\nAmount of memory (max): ");
    result.append(Files.convertToHumanReadableSize(getMaxMemory(), "bytes"));
    for (byte[] macAddress : macAddresses) {
      result.append("\nMAC address: ");
      result.append(Strings.getMACAddressString(macAddress));
    }

    result.append("\n\nNumber of threads: ");
    result.append(getThreads());

    result.append("\n\nUser name: ");
    result.append(getUserName());
    result.append("\nUser home dir: ");
    result.append(getUserHome());
    result.append("\nUser working dir: ");
    result.append(getUserWorkingDir());
    result.append("\nIO tmp dir (Java): ");
    result.append(getIOTmpDir());

    return result.toString();

  }

  /**
   * Gets the mac addresses.
   * 
   * @return the mac addresses
   */
  public List<byte[]> getMacAddresses() {
    return Collections.unmodifiableList(macAddresses);
  }

  /**
   * Sets the mac address.
   * <p/>
   * This should not be used!!!
   * 
   * @param macAddresses
   *          the new mac addresses
   */
  // FIXME is this needed for anything other than serialization?
  public void setMacAddresses(List<byte[]> macAddresses) {
    this.macAddresses.clear();
    this.macAddresses.addAll(macAddresses);
  }

  /**
   * Gets the host name.
   * 
   * @return the host name
   */
  public String getHostName() {
    return hostName;
  }
}
