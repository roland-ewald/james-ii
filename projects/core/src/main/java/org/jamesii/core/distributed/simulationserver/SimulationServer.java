/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.simulationserver;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.ComputationSetupException;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.taskrunner.ComputationTaskHandler;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.IInitializedComputationTask;
import org.jamesii.core.hosts.Host;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.remote.hostcentral.rmi.CommunicationCenterFactory;
import org.jamesii.core.remote.hostcentral.rmi.IRemoteCommunicationCenter;
import org.jamesii.core.services.TriggerableByName;
import org.jamesii.core.simulation.launch.DirectLauncher;
import org.jamesii.core.simulation.resilience.ResilienceSimulationInformation;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.simulationrun.SimulationRun;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * The Class SimulationServer.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public class SimulationServer extends SimulationHost implements
    ISimulationServer {

  /** Serialization ID. */
  static final long serialVersionUID = -3933267778399227508L;

  /** Host name. */
  private String name = "unnamed simulation host";

  /** A reference to the server this client has itself registered to. */
  private IMasterServer server;

  /** ArrayList that will hold events for retrieval by the gui. */
  private List<String> log = new ArrayList<>();

  /**
   * ArrayList that will hold the previous entries of the log, to allow the gui
   * to get events further back.
   */
  private List<String> oldlog = new ArrayList<>();

  /** name of the file that stores the server log if it exceeds the logsize. */
  private String filename;

  /** The errorfilename. */
  private String errorfilename;

  /** The logfile. */
  private File logfile;

  /** The errorlogfile. */
  private File errorlogfile;

  /** modifier for the filename. */
  private int x = 0;

  /**
   * flag that denotes the server status (true = server is busy, false = server
   * is idle).
   */
  private Boolean busy = false;

  /** flag that denotes weather the old log exists. */
  private Boolean oldLogExists = false;

  /**
   * Flag to initiate a log save even if the maximum size has not be reached,
   * for example in abnormal shut downs.
   */
  private Boolean emergency = false;

  /** The master server address. */
  private String masterServerAddress = MasterServer.DEFAULT_BINDING_NAME;

  /**
   * flag that denotes whether the client has been started as part of a thread
   * or as an independent VM.
   */
  private Boolean ownVM = false;

  /**
   * Default constructor.
   * 
   * @param args
   *          command-line arguments
   * 
   * @throws RemoteException
   *           when connection to master server fails
   */
  public SimulationServer(String[] args) throws RemoteException {

    super();

    if (args.length < 2) {
      name = "SimServer" + Long.toString(System.currentTimeMillis());
      SimSystem.report(Level.WARNING,
          "Execution would proceed with unnamed host, auto named the host as "
              + name);
    } else {
      name = args[1];
    }

    masterServerAddress = getServerAddress(args[0]);
    SimSystem.report(Level.INFO, "Registering at master server: "
        + masterServerAddress);
    register(masterServerAddress);
  }

  /**
   * Returns the server address whereby it can be passed as real parameter
   * -server=name /server=name \server=name or as plain name.
   * 
   * @param cmdLineParam
   *          command-line parameter
   * 
   * @return name of the server
   */
  protected static String getServerAddress(String cmdLineParam) {
    String s = "";

    // Check if trailing name has to be removed
    if ((cmdLineParam.substring(0, 1).equals("-"))
        || (cmdLineParam.substring(0, 1).equals("/"))
        || (cmdLineParam.substring(0, 1).equals("\\"))) {
      s = cmdLineParam.substring(1, cmdLineParam.length());
      if (s.indexOf("server=") == 0) {
        s = s.substring(7, s.length());
      }
    }

    // Check if plain name should be used.
    if (s.compareTo("") == 0) {
      s = cmdLineParam;
    }

    // As a fall-back we can try whether the master server is on the local
    // machine, with the default name
    if (s.compareTo("") == 0) {
      s =
          "rmi://localhost:" + MasterServer.DEFAULT_PORT + "/"
              + MasterServer.DEFAULT_BINDING_NAME;
    }

    return s;
  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {

    // restrict the log level for a server to WARNING by default
    ApplicationLogger.setLogLevel(Level.INFO);

    try {
      LocateRegistry.createRegistry(MasterServer.DEFAULT_PORT);
    } catch (java.rmi.server.ExportException e1) {
      // let's assume that if we are here this means that there is already a
      // remote registry running at the given port which can be used by us as
      // well
      SimSystem.report(Level.WARNING, e1.getMessage());
      SimSystem.report(Level.WARNING,
          "Will continue and try to reuse the address ...");
    } catch (RemoteException e) {
      // ok, if we are here we no now that something is wrong
      SimSystem.report(e);
      return;
    }
    if (args.length == 0) {
      SimSystem
          .report(
              Level.SEVERE,
              "Server adress expected as first parameter! - use: -server=rmi://host:port/name");
      SimSystem.shutDown(2);
    }
    // create a client
    try {
      SimSystem.report(Level.INFO,
          DirectLauncher.getSimulationFrameworkHeader(null));
      SimSystem.report(Level.INFO, " --- Simulation (client) server --- ");
      SimulationServer ser = new SimulationServer(args);

      Host.publish(ser, MasterServer.DEFAULT_PORT);

    } catch (Exception e) {
      SimSystem.report(e);
      SimSystem.shutDown(2);
    }
  }

  @Override
  @TriggerableByName()
  public void shutDown() throws RemoteException {
    emergency = true;
    unregister();
    try {
      super.finalize();
    } catch (Throwable t) {
      SimSystem.report(t);
    }
    SimSystem.report(Level.INFO, "The simulation client has been shut down");
    if (ownVM) {
      SimSystem.shutDown(3);
    }
  }

  @Override
  public double getLoad() {
    return 0;
  }

  @Override
  public String getName() {
    return name;
  }

  /**
   * Initialise a sequential simulation.
   * 
   * @param config
   *          the config
   * @param simName
   *          the sim name
   * @param bookedResources
   *          the booked resources
   * 
   * @return the run information
   * 
   * @throws RemoteException
   *           the remote exception
   */
  @Override
  public RunInformation initializeSimulationRun(
      SimulationRunConfiguration config, String simName,
      List<ISimulationHost> bookedResources) throws RemoteException {

    RunInformation result = new RunInformation(config);
    ISimulationRun simulation;
    // create the model reader
    IModelReader modelReader = null;
    ModelReaderFactory modelReaderFactory =
        SimSystem.getRegistry().getFactory(AbstractModelReaderFactory.class,
            config.getAbsModelReaderFactoryParams());
    modelReader = modelReaderFactory.create(config.getModelReaderParams());

    // IDataStorage ds = config.createDataStorage(simID.id);

    // Create list of all available resources (including this one)
    // FIXME: Ensure that all bookedResources are instances of ISimulationServer
    List<ISimulationServer> simResources = new ArrayList<>();
    simResources.add(this);
    if (bookedResources != null) {
      for (ISimulationHost simHost : bookedResources) {
        simResources.add((ISimulationServer) simHost);
      }
    }

    IInitializedComputationTask initTask =
        ComputationTaskHandler.initComputationTask(config, modelReader, result,
            simResources.size() == 1 ? null : simResources);

    // FIXME (the cast should not be here, i.e., the server needs to maintain a
    // list of tasks and not of simruns!)
    simulation = (ISimulationRun) initTask.getComputationTask();
    simulation.setName(simName);
    getTaskManager().addComputationTask(simulation, config);

    // result.setSimulationUID(simulation.getUid());

    return result;
  }

  @Override
  public final void register(String serverAdress) {
    // get a pointer to the server
    try {
      SimSystem.report(Level.INFO, "Used server: " + serverAdress);

      SimSystem.report(Level.INFO, "Simulation client " + getName()
          + " tries to connect to the server");

      server = (IMasterServer) Naming.lookup(serverAdress);
      SimSystem.report(Level.INFO, "Connected!");
      // Naming.rebind(serverAdress, server);
    } catch (Exception e) {
      SimSystem.report(Level.SEVERE,
          "Have you used a schema like \"rmi://localhost:"
              + MasterServer.DEFAULT_PORT + "/"
              + MasterServer.DEFAULT_BINDING_NAME + "\" ???", e);
      emergency = true;
      // addEvent("Exception occured while trying to connect to the server. Client is shutting down. Check the server adress.");
      throw new ComputationSetupException(
          "Exception occured while connecting to the server. Shutting down.", e);
    }
    // make myself known to the server

    SimSystem.report(Level.INFO, "Simulation client " + getName()
        + " tries to register at the server!");

    boolean registered = false;
    int tries = 0;
    while (!registered && (tries < 3)) {
      try {
        tries++;
        server.register(this);
        registered = true;
      } catch (RemoteException e) {

        if (tries < 3) {
          SimSystem.report(Level.WARNING,
              "Attempting to connect to server failed (Try: " + tries + ")");
          try {
            // wait at least a second, and at most 3 seconds - thereby
            // distributing wait times (to avoid further conflicts by two many
            // concurrent accesses)
            this.wait(1000 + Math.round(Math.random() * 2000));
          } catch (InterruptedException e1) {
          }
        } else {
          SimSystem.report(Level.SEVERE, "Failed on connecting to server!");
          SimSystem.report(e);
          emergency = true;
          // addEvent("unspecified exception occured attempting to register at the server");
          throw new ComputationSetupException(
              "Exception occured while registering at the server. Shutting down.",
              e);
        }
      }

    }

    SimSystem.report(Level.INFO, "Simulation client " + getName()
        + " is registered at the server!");

  }

  @Override
  public void serverAbort() throws RemoteException {
    SimSystem
        .report(
            Level.SEVERE,
            "Abort has been signalled (most likely by the master server). This simulation computation client will be shut down, too!");
    emergency = true;
    // addEvent("Server shut down.");

    if (ownVM) {
      SimSystem.shutDown(3);
    }
  }

  @Override
  public void stopProc(ComputationTaskIDObject uid) {
    ISimulationRun simulation = getSimulationByUID(uid);
    if (simulation != null) {
      simulation.stopProcessor();
      removeSimulation(simulation);
    } else {
      SimSystem.report(Level.INFO, "Attempted to stop Simulation '" + uid
          + "'. Simulation not found on Server.");
      throw new UnknownComputationTaskException("Simulation with UID: " + uid
          + " not located on this server");
    }
  }

  @Override
  public Partition getPartition(ComputationTaskIDObject uid) {
    ISimulationRun simulation = getSimulationByUID(uid);
    Partition partition = null;

    if (simulation != null) {
      partition = simulation.getPartition();
    } else {
      // addEvent("Simulation " + uid + " not found on Server.");
      throw new UnknownComputationTaskException(
          "Simulation not located on this server");
    }
    return partition;
  }

  @Override
  public void stopSimulationRuns() throws RemoteException {
    SimSystem.report(Level.INFO, "Computation stopped!");
    SimSystem.report(Level.INFO, "Cleaning up all computation tasks...");
    getTaskManager().cleanUp();
  }

  @Override
  public void startSimulationRun(ComputationTaskIDObject uid)
      throws RemoteException {
    ISimulationRun computation = getSimulationByUID(uid);
    if (computation != null) {

      computation.start();

      // clear the resource
      getTaskManager().removeComputationTask(computation);
    } else {
      SimSystem.report(Level.INFO,
          "Attempted to start Simulation '" + uid.toString()
              + "'. Simulation not found on Server.");
      throw new UnknownComputationTaskException("Simulation: " + uid.toString()
          + " not located on this server");

    }
  }

  @TriggerableByName()
  @Override
  public void unregister() {

    // make myself unknown to the server
    try {
      server.unregister(this);

      // addEvent("unreg");
    } catch (RemoteException e) {
      SimSystem.report(Level.SEVERE,
          "Shutting down the JVM due to the exception caught: ", e);
      SimSystem.shutDown(3);
    }
  }

  /**
   * saves the serverlog in the event of an abnormal shut down (due to an
   * exception for example).
   */
  void emergencyFileDump() {

    errorfilename = name + " errorlog " + ".log";
    errorlogfile = new File(errorfilename);
    checkfile(errorlogfile);
    emergency = false;
    // addEvent("Emergency File Dump initiated. Log written to: " +
    // errorfilename);
    try {
      FileWriter fo = new FileWriter(errorfilename);
      try (PrintWriter out = new PrintWriter(fo)) {
        for (int i = 0; i < log.size(); i++) {
          out.println(log.get(i));
        }
      }
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  /**
   * returns the server log. If there is an oldlog, meaning that more then
   * <logsize> entries have happened, the log is merged to the old log and
   * returned. If not, only the log is returned.
   * 
   * @return the server log
   */
  @Override
  public List<String> getServerLog() {
    List<String> returnlog = new ArrayList<>();

    if (!oldLogExists) {
      return log;
    }
    for (String event1 : oldlog) {
      returnlog.add(event1);
    }
    for (String event2 : log) {
      returnlog.add(event2);
    }
    return returnlog;
  }

  /**
   * checks if a file exists. If it does, the filename is altered for the new
   * file.
   * 
   * @param file
   *          the file
   */
  private void checkfile(File file) {
    if (file.exists()) {
      if (emergency) {
        errorfilename = name + " errorlog " + (++x) + ".log";
        errorlogfile = new File(filename);
        checkfile(errorlogfile);
      } else {
        filename = name + " log " + (++x) + ".log";
        logfile = new File(filename);
        checkfile(logfile);
      }
    }
  }

  @Override
  public IMasterServer getMasterServer() {
    return server;
  }

  /**
   * flag denoting the server status.
   * 
   * @return true, if checks if is busy
   */
  @Override
  public boolean isBusy() {
    return busy;
  }

  /**
   * Support for load balancing.
   * 
   * @param modelFullName
   *          the model full name
   * @param targetHost
   *          the target host
   * 
   * @return true, if migrate processor
   */
  @Override
  public boolean migrateProcessor(String modelFullName,
      ISimulationHost targetHost) {
    return true;
  }

  /**
   * Overridden method.
   * 
   * @param simulation
   *          the simulation
   * @param processor
   *          the processor
   * 
   * @return true, if receive simulation part
   * 
   * @throws RemoteException
   *           the remote exception
   * 
   * @see org.jamesii.core.distributed.simulationserver.ISimulationServer#receiveSimulationPart
   *      (org.jamesii.core.simulationrun.SimulationRun,
   *      org.jamesii.core.processor.IProcessor)
   */
  @Override
  public boolean receiveSimulationPart(SimulationRun simulation,
      IProcessor processor) throws RemoteException {

    // Add simulation to simulation parts
    simulation.setProcessorInfo(new ProcessorInformation(processor));
    getTaskManager().addComputationTask(simulation, null);

    return true;
  }

  @Override
  public void storeResilienceData(ResilienceSimulationInformation data)
      throws RemoteException {
  }

  @Override
  public String getMasterServerAdress() {
    return masterServerAddress;
  }

  @Override
  public Class<?> getServiceType() {
    return ISimulationServer.class;
  }

  @Override
  public boolean isRegistered() throws RemoteException {
    return (getMasterServer() != null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <D> D executeRunnableCommand(ComputationTaskIDObject simulationID,
      String command, Object[] args) {
    // the simulation run has to be a local simulation run object
    ISimulationRun simulation = getSimulationByUID(simulationID);

    if (simulation != null) {

      IRunnable runnable = (IRunnable) simulation.getProcessorInfo().getLocal();

      Object result =
          org.jamesii.core.util.Reflect.executeMethod(runnable, command, args);

      // special case: stop => we have to remove the simulation
      if (command.compareTo("stop") == 0) {
        removeSimulation(simulation);
      }

      return (D) result;
    }
    SimSystem.report(Level.INFO, "Attempted to stop Simulation '"
        + simulationID + "'. Simulation not found on Server.");
    throw new UnknownComputationTaskException("Simulation with UID: "
        + simulationID + " not located on this server");
  }

  @Override
  public <D> D getSimulationRunProperty(ComputationTaskIDObject simulationID,
      String property) throws RemoteException {
    // the simulation run has to be a local simulation run object
    ISimulationRun simulation = getSimulationByUID(simulationID);
    return simulation.<D> getProperty(property);
  }

  @Override
  public IRemoteCommunicationCenter getRemoteCommunicationCenter(
      IUniqueID uniqueID, ParameterBlock params) throws RemoteException {
    return new CommunicationCenterFactory().create(null).getFirstValue();
  }
}
