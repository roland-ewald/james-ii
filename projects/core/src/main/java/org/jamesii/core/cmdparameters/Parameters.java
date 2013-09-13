/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.allocation.plugintype.SimulationResourceAllocatorFactory;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.ParameterUtils;

// TODO: Auto-generated Javadoc
/**
 * This (legacy) class encapsulates all command line parameters.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 */
public class Parameters extends AbstractParameters {

  /** Serialisation ID. */
  static final long serialVersionUID = 1663342806048923340L;

  /**
   * Name of the master server (to be looked up). Default is empty
   * {@link String}.
   */
  public static final String MASTER_SERVER_NAME = "masterServerName";

  /** Directly execute the experiment given. */
  private static final String PARAM_EXP_QUICKSTART = "exp";

  /**
   * Start with no gui parameter if you don't like to use the default GUI of
   * JAMES II.
   */
  private static final String PARAM_NO_GUI = "nogui";

  /** The plugin directory to be used for JAMES II. */
  private static final String PARAM_PLUGIN_DIR = "plugindir";

  /**
   * The Class ServerParamHandler.
   * 
   * @author Jan Himmelspach
   */
  private class ServerParamHandler extends ParamHandler {

    /** Serialisation ID. */
    private static final long serialVersionUID = -2279399645037295123L;

    @Override
    public void handleParamValue(String value, AbstractParameters parameters) {
      findServer(value);
    }
  }

  /**
   * A reference to a simulation server. A simulation need not to be run on a
   * server, it can be executed on a single machine without any extra programs
   * to be run. However, if a server is specified, it will be used.
   */
  private IMasterServer simulationServer = null;

  /**
   * Default constructor.
   */
  public Parameters() {
    super();
  }

  public String getInfoString() {
    StringBuilder sb = new StringBuilder();

    sb.append(getParameterBlock().toString());

    return sb.toString();
  }

  /**
   * Creates parameters object similar to original one.
   * 
   * @param original
   *          the original
   */
  public Parameters(Parameters original) {
    this.setArgumentList(new HashMap<>(original.getArgumentList()));
    this.setSimpleValues(new HashMap<>(original.getSimpleValues()));
    this.setParameterBlock(original.getParameterBlock().getCopy());
    this.simulationServer = original.simulationServer;
  }

  /**
   * Find server.
   * 
   * @param name
   *          the name
   */
  public final void findServer(String name) {
    try {
      simulationServer = (IMasterServer) Naming.lookup(name);
    } catch (Exception e) {
      throw new RuntimeException(
          "JAMES II - cannot find the server! Will shut down.", e);
    }
  }

  /**
   * Initialises and registers parameters known by this class.
   */
  @Override
  protected void initParameters() {

    // String[] ver = { "v", "verbose" };
    // registerParameter(ver, new TranslatingParamHandler<Boolean>("verbose",
    // !SimSystem.consoleOut), SimSystem.consoleOut, "Print more information");
    registerParameter(
        "monitoringEnabled",
        new TranslatingParamHandler<>("monitoringEnabled", true),
        false,
        "Enable detailed thread monitoring. Thread monitoring is not available for all Java VMs!");
    registerParameter("silent", new TranslatingParamHandler<>("silent",
        !isSilent()), isSilent(), "Silent, do not print anything");
    registerParameter(
        "interactive",
        new TranslatingParamHandler<>("interactive", !isInteractive()),
        isInteractive(),
        "Interactive command line mode.\nThe system will start in an interactive command line mode. "
            + "Thus the progress of the simulation of the model can be controlled and many different information "
            + "(about the model, the processor, the simulation, the vm) can be retrieved.");
    registerParameter(
        "logTime",
        new TranslatingParamHandler<>("logTime", !isLogTime()),
        isLogTime(),
        "Write the time which was required for the run into a file (currently c:\\simprot.txt)");

    registerParameter(PARAM_NO_GUI, new TranslatingParamHandler<>(PARAM_NO_GUI,
        true), false,
        "Use the no gui option if you'd like to start without the JAMES II GUI");

    String[] pd = { PARAM_PLUGIN_DIR };
    registerParameter(pd, new StringParamHandler(PARAM_PLUGIN_DIR, null), "",
        "Set an additional plugin directory");

    String[] pq = { PARAM_EXP_QUICKSTART };
    registerParameter(pq, new StringParamHandler(PARAM_EXP_QUICKSTART, null),
        "", "Directly start the experiment passed");

    registerParameter(
        "server",
        new ServerParamHandler(),
        null,
        "Use this server to run the simulation distributed. "
            + "Must be a server where a valid SimulationMasterServer has been started on before!");

  }

  /**
   * Parses command-line arguments.
   * 
   * @param args
   *          the command-line arguments
   */
  public void parseArgs(String args[]) {

    String s;
    for (int i = 0; i < args.length; i++) {
      if (!args[i].isEmpty()) {
        // drop the first char of an argument (- or / or \)
        s = args[i].substring(1);

        String param;
        String value;

        if (s.indexOf('=') != -1) {
          param = s.substring(0, s.indexOf('='));
          value = s.substring(s.indexOf('=') + 1, s.length());
        } else {
          param = s;
          value = null;
        }

        Parameter par = this.getParameter(param);
        if (par == null) {
          continue;
        }
        par.getHandler().handleParamValue(value, this);
      }
    }
  }

  /**
   * Returns true if name for master server is set.
   * 
   * @return true, if name for a master server is set.
   */
  public boolean useMasterServer() {
    return getMasterServerName().length() > 0;
  }

  /**
   * Retrieves master server (via RMI).
   * 
   * @return the interface of the (remote) server
   */
  public IMasterServer getMasterServer() {
    // Look up master server if name is set
    if (simulationServer == null && useMasterServer()) {
      try {
        simulationServer = (IMasterServer) Naming.lookup(getMasterServerName());
      } catch (Exception ex) {
        SimSystem.report(Level.SEVERE, "Can't find server: "
            + getMasterServerName(), ex);
      }
    }
    return simulationServer;
  }

  /**
   * Gets the master server name.
   * 
   * @return the master server name
   */
  public String getMasterServerName() {
    return getParameterBlock().getSubBlockValue(
        ParameterUtils.MASTER_SERVER_NAME);
  }

  /**
   * Sets the master server name.
   * 
   * @param msName
   *          the new master server name
   */
  public void setMasterServerName(String msName) {
    if (msName != null) {
      getParameterBlock()
          .addSubBlock(ParameterUtils.MASTER_SERVER_NAME, msName);
    }
  }

  /**
   * Sets the master server directly. Useful when the
   * {@link org.jamesii.core.distributed.masterserver.MasterServer} is in the
   * same JVM.
   * 
   * @param masterServer
   *          the master server
   * @throws RemoteException
   *           when the master server's name can not be resolved
   */
  public void setMasterServer(IMasterServer masterServer)
      throws RemoteException {
    String msName = masterServer.getName();
    setMasterServerName(msName);
    this.simulationServer = masterServer;
  }

  /**
   * Checks if is silent.
   * 
   * @return the boolean
   */
  public Boolean isSilent() {
    return getParameterBlock().getSubBlockValue(ParameterUtils.SILENT);
  }

  /**
   * Sets the silent.
   * 
   * @param silent
   *          the new silent
   */
  public void setSilent(boolean silent) {
    this.getParameterBlock().addSubBlock(ParameterUtils.SILENT, silent);
  }

  /**
   * Checks if is resilient.
   * 
   * @return the boolean
   */
  public Boolean isResilient() {
    return getParameterBlock().getSubBlockValue(ParameterUtils.RESILIENT);
  }

  /**
   * Sets the resilient.
   * 
   * @param resilient
   *          the new resilient
   */
  public void setResilient(boolean resilient) {
    this.getParameterBlock().addSubBlock(ParameterUtils.RESILIENT, resilient);
  }

  /**
   * Checks if is monitoring enabled.
   * 
   * @return the boolean
   */
  public Boolean isMonitoringEnabled() {
    return getParameterBlock().getSubBlockValue(
        ParameterUtils.MONITORING_ENABLED);
  }

  /**
   * Sets the monitoring enabled.
   * 
   * @param monitoringEnabled
   *          the new monitoring enabled
   */
  public void setMonitoringEnabled(boolean monitoringEnabled) {
    this.getParameterBlock().addSubBlock(ParameterUtils.MONITORING_ENABLED,
        monitoringEnabled);
  }

  /**
   * Checks if is log time.
   * 
   * @return the boolean
   */
  public Boolean isLogTime() {
    return getParameterBlock().getSubBlockValue(ParameterUtils.LOG_TIME);
  }

  /**
   * Sets the log time.
   * 
   * @param logTime
   *          the new log time
   */
  public void setLogTime(boolean logTime) {
    this.getParameterBlock().addSubBl(ParameterUtils.LOG_TIME, logTime);
  }

  /**
   * Checks if is interactive.
   * 
   * @return the boolean
   */
  public Boolean isInteractive() {
    return getParameterBlock().getSubBlockValue(ParameterUtils.INTERACTIVE);
  }

  /**
   * Sets the interactive.
   * 
   * @param interactive
   *          the new interactive
   */
  public void setInteractive(boolean interactive) {
    this.getParameterBlock().addSubBlock(ParameterUtils.INTERACTIVE,
        interactive);
  }

  /**
   * Checks if is copy at once.
   * 
   * @return the boolean
   */
  public Boolean isCopyAtOnce() {
    return getParameterBlock().getSubBlockValue(ParameterUtils.COPY_AT_ONCE);
  }

  /**
   * Sets the copy at once.
   * 
   * @param copyAtOnce
   *          the new copy at once
   */
  public void setCopyAtOnce(boolean copyAtOnce) {
    getParameterBlock().addSubBlock(ParameterUtils.COPY_AT_ONCE, copyAtOnce);
  }

  /**
   * Sets the simulation resource allocator factory.
   * 
   * @param factoryClass
   *          the factory class
   * @param paramBlock
   *          the parameter block
   */
  public void setSimResourceAllocator(
      Class<? extends SimulationResourceAllocatorFactory> factoryClass,
      ParameterBlock paramBlock) {
    paramBlock.setValue(factoryClass.getCanonicalName());
    getParameterBlock().addSubBlock(ParameterUtils.SIM_RESOURCE_ALLOCATION,
        paramBlock);
  }

}