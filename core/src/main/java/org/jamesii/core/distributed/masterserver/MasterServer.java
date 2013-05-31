/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.masterserver;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.distributed.computationserver.IComputationServer;
import org.jamesii.core.distributed.computationserver.IJob;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.distributed.simulationserver.ISimulationHost;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.distributed.simulationserver.ServerSimulationManagement;
import org.jamesii.core.distributed.simulationserver.UnknownComputationTaskException;
import org.jamesii.core.experiments.IComputationTaskConfiguration;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.taskrunner.IRemoteComputationTaskRunner;
import org.jamesii.core.experiments.taskrunner.InitializedComputationTask;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.hosts.Host;
import org.jamesii.core.hosts.system.IMSSystemHostInformation;
import org.jamesii.core.hosts.system.MSSystemHost;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.services.IService;
import org.jamesii.core.services.ServiceInfo;
import org.jamesii.core.services.ServiceRegistry;
import org.jamesii.core.services.availability.IAvailabilityProspect;
import org.jamesii.core.simulation.launch.DirectLauncher;
import org.jamesii.core.simulation.resilience.ResilienceManagement;
import org.jamesii.core.simulation.resilience.ResilienceSimulationInformation;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.id.UniqueIDGenerator;

/**
 * The simulation master server is the centralised server class for the
 * distributed usage of James II.
 * 
 * Services, e.g., simulation servers, have to register here before they can be
 * used.
 * 
 * Services are registered and hold in a "service registry"
 * {@link ServiceRegistry}). This class provides the management of the services
 * as such a "service resource management", and can provide service availability
 * checking functionality.
 * 
 * The master server cannot be directly used for executing simulations -- it can
 * just be used for service registering, and simulation instance creation and
 * delegated control.
 * 
 * @author Jan Himmelspach
 */
public class MasterServer extends MSSystemHost implements IMasterServer,
    IAvailabilityProspect<IService> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -2229092953158479223L;

  /** The name. */
  private String name = "unnamed master server";

  /** The default binding name. */
  public static final String DEFAULT_BINDING_NAME = "MSMASTERSERVER";

  /** The default port of the master server. */
  public static final int DEFAULT_PORT = 10992;

  /**
   * The service registry. Any service which shall be available for whatever
   * purpose needs to be registered in the service registry.
   */
  private ServiceRegistry serviceRegistry = new ServiceRegistry();

  /**
   * Resilience - management of simulation checkpoints for restoring crashed
   * simulations.
   */
  private ResilienceManagement resilienceManagement =
      new ResilienceManagement();

  /** Simulations - management of simulation runs (mappings, etc). */
  private ServerSimulationManagement simulationManagement =
      new ServerSimulationManagement();

  /**
   * The constructor of the SimulationMasterServer. Starts the availability test
   * with default values.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public MasterServer() throws RemoteException {
    super();

    report("Services can now be registered at this server ...");
  }

  /**
   * The constructor of the SimulationMasterServer. Starts the availability test
   * with default values.
   * 
   * 
   * @param name
   *          name of the server
   * @throws RemoteException
   *           the remote exception
   */
  public MasterServer(String name) throws RemoteException {
    super();
    this.name = name;
  }

  // /**
  // * Create a simulation whereby the model is created on the server (thus the
  // * model has not to be created on the calling host).
  // *
  // * @param reader The reader to be used to read the model
  // * @param modelPath The path where the reader shall read the model from
  // * @param modelParameters The parameters of the model
  // *
  // * @return an instantiated simulation object
  // *
  // * @throws RemoteException the remote exception
  // */
  // public synchronized ISimulationRun createSimulation(IModelReader reader,
  // URI modelPath, HashMap<String, ?> modelParameters) throws RemoteException {
  // return createSimulation(reader.read(modelPath, modelParameters));
  // }

  // // @Override
  // public synchronized InitializedSimulation createSimulation(IModel model,
  // SimulationConfiguration simConfig) throws RemoteException {
  //
  // // System.out.println("Simulation masterserver (" + getName()
  // // + ") create simulation");
  //
  // // get a list of available simulation hosts
  // List<ISimulationHost> resources = serviceRegistry
  // .getUnBookedServiceList(SimulationHost.class);
  //
  // // System.out.println("Having " + resources.hosts.size() + " resources.");
  //
  // IModel localModel = null;
  //
  // localModel = model;
  //
  // SimulationRun simulation = new SimulationRun("Sim", localModel, resources,
  // simConfig);
  //
  // /*
  // * it = clients.iterator(); while (it.hasNext()) { host = (SimulationHost)
  // * it.next(); if ((!usedClients.contains(host)) &&
  // * (!resources.contains(host))) { usedClients.add(host); } }
  // */
  //
  // for (IService service : resources) {
  // serviceRegistry.bookService(service, simulation.getUid());
  // }
  //
  // // simulationManagement.addSimulation(simulation);
  //
  // if (simConfig.isResilient())
  // this.startResilience(simulation);
  //
  // return new InitializedSimulation(simulation, simConfig.getConfigNumber(),
  // null);
  // }

  @Override
  public synchronized InitializedComputationTask executeSimulationConfiguration(
      IComputationTaskConfiguration runConfig,
      IRemoteComputationTaskRunner simRunner) throws RemoteException {

    ISimulationHost host = null;

    List<ISimulationHost> resources = null;

    // if we do not get resources right now we'll wait a bit and retry
    // afterwards for a couple of times
    int tries = 0;
    while (tries < 10) {
      resources =
          serviceRegistry.getUnBookedServiceList(ISimulationServer.class);
      tries++;
      if ((resources != null) && (!resources.isEmpty())) {
        break;
      } else {
        try {
          wait(10000);
        } catch (InterruptedException e) {
        }
      }
    }

    // create a unique name (as long as there is only one server with the name
    // of this server!!!)
    String simname = getName() + " " + SimSystem.getUId();

    report("MasterServer (" + getName() + ") create simulation (" + simname
        + ")");

    if ((resources == null) || (resources.isEmpty())) {
      Entity.report(Level.SEVERE,
          "No free host found! Cannot create the simulation!");
      return null;
    }

    // Let sim config query any parameters that need to be given by the master
    // server (e.g., available DB hosts, etc.)
    // TODO uncomment this method call when the method is actually working!
    // config.getResourcesFromMasterServer(this, simname);

    InitializedComputationTask initSim = null;

    try {

      List<ISimulationHost> bookedResources =
          bookResources(
              runConfig,
              runConfig.getComputationTaskID(),
              ((SimulationRunConfiguration) runConfig)
                  .getSimResourceAllocator().estimateRequiredResources(
                      (SimulationRunConfiguration) runConfig, resources));
      host = bookedResources.remove(0);

      // System.out.println("Try to execute on host: " + host);
      // remember configuration / sim id mapping
      simulationManagement.addConfig((SimulationRunConfiguration) runConfig);

      // initialize the simulation on the remote host
      RunInformation rInfo =
          ((ISimulationServer) host).initializeSimulationRun(
              (SimulationRunConfiguration) runConfig, simname,
              bookedResources.isEmpty() ? null : bookedResources);

      // create a new initialized simulation object, linking the dummy
      // simulation and the run information
      // FIXME jh194: The sequential task runner expects a non null value here,
      // the parallel seems to ignore it.
      initSim = new InitializedComputationTask(null, rInfo);

      if (((SimulationRunConfiguration) runConfig).isResilient()) {
        startResilience(runConfig.getComputationTaskID());
      }

    } catch (Exception ex) {
      // if (host != null) //FIXME don't see why the exception shall be on an
      // external host just because host is not null?
      // report("Master server, exception on host " + host.getName(), ex);
      SimSystem.report(ex);
    }

    return initSim;
  }

  @Override
  public <V> IUniqueID initializeJob(IJob<V> job) throws RemoteException {
    List<IComputationServer> resources =
        serviceRegistry.getUnBookedServiceList(IComputationServer.class);
    IComputationServer server = null;
    server = resources.get(0);
    IUniqueID id = UniqueIDGenerator.createUniqueID();
    serviceRegistry.bookService(server, id);
    server.initializeJob(job, id);
    return id;
  }

  @Override
  public <V> V executeJob(IUniqueID id, Serializable data)
      throws RemoteException {
    List<IComputationServer> resources =
        serviceRegistry.getServicesForPurpose(id);
    IComputationServer server = resources.get(0);
    return server.<V> executeJob(id, data);
  }

  @Override
  public void finalizeJob(IUniqueID id) throws RemoteException {
    List<IComputationServer> resources =
        serviceRegistry.getServicesForPurpose(id);
    IComputationServer server = resources.get(0);
    server.finalizeJob(id);
    serviceRegistry.freeServices(id);
  }

  /**
   * This method books a set of resources for a simulation. The first resource
   * in the returned list shall be used as host for the creation of the
   * simulation.
   * 
   * @param config
   *          the simulation configuration to be executed (may be used for more
   *          sophisticated implementation)
   * @param simID
   *          the ID of the simulation
   * @param resourceCount
   *          the count of resources which shall be used for the simulation
   * @param resources
   *          the available resources
   * @return the list of resources which shall be used for the simulation
   */
  private List<ISimulationHost> bookResources(
      IComputationTaskConfiguration config, ComputationTaskIDObject simID,
      List<ISimulationHost> requiredResources) {

    List<ISimulationHost> bookedResources = new ArrayList<>();

    if (requiredResources == null) {
      return bookedResources;
    }

    // Try to allocate hosts for the new simulation
    for (ISimulationHost simHost : requiredResources) {
      if (serviceRegistry.bookService(simHost, simID)) {
        bookedResources.add(simHost);
      }
    }
    return bookedResources;
  }

  @Override
  public void shutDown() throws RemoteException {
    report("MasterServer is going to shut down ... sending cancellation messages");

    report(" ... informing registered services ...");
    serviceRegistry.signalAbort();

    report(" ... stopping resilience ...");
    resilienceManagement.shutDown();

    report("MasterServer has cleaned up infrastructure usage ...");

    report("Simulation server (" + getName() + ") has been shut down");

  }

  @Override
  public String getName() {
    return name;
  }

  /**
   * The method shut down the simulation and initiates a restart, if the
   * resilience was activates for the simulation.
   * 
   * @param brokenHost
   *          the broken host
   * @param simUID
   *          the sim uid
   */
  protected void handleSimulationBreakDown(ComputationTaskIDObject simUID,
      ISimulationHost brokenHost) {

    // get a list of all those services which have been booked by the broken
    // simulation run
    // List<IService> list = serviceRegistry.getServicesForPurpose(simUID);

    // try to stop the simulation (it might still be running, depending on the
    // resource crashed)
    try {
      stop(simUID);
    } catch (Exception ex) {
      // be silent
    }

    // the simulation execution broke down, thus we should free all resources
    // booked by the simulation execution on this master server
    serviceRegistry.freeServices(simUID);

    // if (resilienceManagement.hasResilience(simulation)) {
    // resilienceManagement.cancelResilience(simulation);
    //
    // IRemoteSimulationRunner simulationRunner = simulationManagement
    // .getRunner(simUID);
    //
    // System.out.println("Resilience available for simulation " + simUID);
    //
    // if (!resilienceManagement.hasCheckPoint(simulation)) {
    // System.out
    // .println("No checkpoint for simulation " + simUID + " found.");
    // try {
    // // fallback: completely restart simulation
    // simulationRunner.restartSimulation(simUID);
    // } catch (RemoteException re) {
    // SimSystem.report (re);
    // }
    //
    // } else {
    // System.out.println("Checkpoint for simulation " + simUID + " found.");
    //
    // if (simulationManagement.getConfig(simUID) != null) {
    //
    // // IModel recoveredModel =
    // // resilienceManagement.retrieveModel(this.getName(), simUID);
    // IModel recoveredModel = null;
    //
    // // For testing which data are stored
    // // resilienceManagement.getDataStorage().getLastCheckpoint(this.getName
    // // (),
    // // simUID);
    //
    // SimulationConfiguration config = simulationManagement
    // .getConfig(simUID);
    // config.instrumentModel(recoveredModel);
    // SimulationRuntimeInformation simRunInfo = null;
    //
    // /*
    // * try { ISimulation simRecover = (ISimulation)
    // * this.initializeSimulation(config, simulationRunner); simRunInfo =
    // * new SimulationRuntimeInformation(config, config
    // * .getModelObservers(), config.getSimulationObservers(), simRecover); }
    // * catch (RemoteException e) { SimSystem.report (e); }
    // */
    //
    // try {
    // simulationRunner.recoverSimulation(simUID, simRunInfo);
    // } catch (RemoteException re) {
    // SimSystem.report (er);
    // }
    // } else {
    // System.out.println("No SimulationConfiguration for simulation "
    // + simUID + " found.");
    // try {
    // // fallback: completely restart simulation
    // simulationRunner.restartSimulation(simUID);
    // } catch (RemoteException re) {
    // SimSystem.report (re);
    // }
    // }
    // }
    //
    // } else {
    // System.out.println("No resilience available for simulation " + simUID);
    // }
    //
    // try {
    // serviceRegistry.freeServices(simulation);
    // } catch (Exception ex) {
    // System.err
    // .println("Resilience: Clean-up of simulation resources could not be
    // finished.");
    // }
    // // System.out.println("Finished handleSimulationBreakDown() for
    // simulation "
    // // + simUID);
  }

  @Override
  public void execute(ComputationTaskIDObject simulationId,
      IRemoteComputationTaskRunner simRunner) throws RemoteException {
    // execute the garbage collector FIXME This call should be removed,
    // otherwise the gc call will be made per replication!
    System.gc();

    report("Starting simulation (" + simulationId.getId() + ") ...");

    // remember the runner for the current simulation run, if the simulation run
    // execution fails
    // this reference can be used for recovering / information
    simulationManagement.addRunner(simulationId, simRunner);

    try {
      start(simulationId);
    } catch (Exception ex) {
      SimSystem.report(ex);
    }

    //
    // resilienceManagement.cancelResilience(simulation);
    //

    // clean up managemant
    simulationManagement.removeConfig(simulationId);
    simulationManagement.removeRunner(simulationId);

    // synchronized (this) {
    // // if this line is removed the simulation will remain in memory!!!!
    // simulationManagement.removeSimulation(simulation);
    // }
    //
    try {
      serviceRegistry.freeServices(simulationId);
    } catch (Exception ex) {
      SimSystem.report(ex);
    }

    report("Master server: Resources ready for reuse ... ");
  }

  /**
   * Clean processor info.
   * 
   * @param partition
   *          the partition
   */
  protected void cleanProcessorInfo(Partition partition) {
    if (partition.getParentProcessorInfo() != null) {
      partition.getParentProcessorInfo().setLocal(null);
    }
    partition.setParentProcessorInfo(null);
    if (partition.getProcessorInfo() != null) {
      partition.getProcessorInfo().setLocal(null);
    }
    partition.setProcessorInfo(null);
    for (int i = 0; i < partition.getSubPartitionCount(); i++) {
      cleanProcessorInfo(partition.getSubPartition(i));
    }
  }

  /**
   * Gets the simulation run host.
   * 
   * WARNING. This is a preliminary method. It is based on the implicit
   * assumption that resource 0 will contain the runnable processor!
   * 
   * @param simulationRunId
   *          the simulation run id
   * 
   * @return the simulation run host
   */
  private ISimulationServer getHostOfSimulationRun(
      ComputationTaskIDObject simulationRunId) {
    ISimulationServer host =
        (ISimulationServer) serviceRegistry.getServicesForPurpose(
            simulationRunId).get(0);
    if (host == null) {
      report("Simulation with ID " + simulationRunId
          + " not found on this server!!!");
      throw new UnknownComputationTaskException("Simulation with ID "
          + simulationRunId + " not found on this server!!!");
    }
    return host;
  }

  /**
   * Start a simulation which resides on a remote side. The host the simulation
   * is on is automatically determined by the simulation id.
   * 
   * @param simulationID
   *          the simulation id
   * 
   * @throws RemoteException
   *           the remote exception
   */
  protected void start(ComputationTaskIDObject simulationID)
      throws RemoteException {

    ISimulationServer host = getHostOfSimulationRun(simulationID);

    report("Starting Simulation " + simulationID + " on remote host: " + host);

    host.startSimulationRun(simulationID);
  }

  /**
   * Stop remote.
   * 
   * @param simulationID
   *          the simulation id
   * 
   * @throws RemoteException
   *           the remote exception
   */
  @Override
  public void stop(ComputationTaskIDObject simulationID) throws RemoteException {

    ISimulationServer host = getHostOfSimulationRun(simulationID);

    report("Stopping Simulation run " + simulationID + " on remote host: "
        + host);

    host.stopProc(simulationID);
  }

  @Override
  public <D> D getSimulationRunProperty(ComputationTaskIDObject simulationID,
      String property) throws RemoteException {
    ISimulationServer host = getHostOfSimulationRun(simulationID);
    return host.<D> getSimulationRunProperty(simulationID, property);
  }

  @Override
  public Partition getPartition(ComputationTaskIDObject simulationID)
      throws RemoteException {

    ISimulationServer host = getHostOfSimulationRun(simulationID);

    return host.getPartition(simulationID);
  }

  /**
   * Starts the resilience.
   * 
   * @param simulationID
   *          the simulation id
   * 
   * @throws RemoteException
   *           the remote exception
   */
  private void startResilience(ComputationTaskIDObject simulationID)
      throws RemoteException {
    // TODO no dummy simulations anymore - so resilience should work with the
    // simulationID
    List<ISimulationHost> hosts =
        serviceRegistry.getServicesForPurpose(simulationID);
    resilienceManagement.addSimulation(this, simulationID, hosts);
  }

  /**
   * Stores the given data into a data storage.
   * 
   * @param data
   *          the data
   */
  public synchronized void storeResilienceData(
      ResilienceSimulationInformation data) {
    resilienceManagement.storeData(data);
  }

  /**
   * Register an observer for watching changes in the resilience management.
   * 
   * @param observer
   *          the observer
   */
  public void registerResilienceManagementObserver(IObserver observer) {
    resilienceManagement.registerObserver(observer);
  }

  /**
   * Set the mediator for the registry, and the simulation + resilience
   * management classes.
   * 
   * @param mediator
   *          the mediator
   * 
   * @throws RemoteException
   *           the remote exception
   */
  @Override
  public void setManagementMediator(IMediator mediator) throws RemoteException {
    serviceRegistry.setMediator(mediator);
    simulationManagement.setMediator(mediator);
    resilienceManagement.setMediator(mediator);
  }

  /**
   * Start master server.
   * 
   * @param args
   *          command line arguments, first argument defines the name of the
   *          server
   */
  public static void main(String[] args) {

    String name = args.length > 0 ? args[0] : DEFAULT_BINDING_NAME;

    try {
      LocateRegistry.createRegistry(DEFAULT_PORT);

      StringBuilder builder = new StringBuilder();
      builder.append(DirectLauncher.getSimulationFrameworkHeader(null) + "\n");
      builder
          .append("************************************************************************************\n");
      builder.append("\n");
      builder.append("A simulation master server (on host "
          + java.net.InetAddress.getLocalHost().getHostName() + " named "
          + name + ") is beeing started ...\n");
      builder.append("\n");
      builder
          .append("************************************************************************************\n");
      builder.append("\n");
      builder.append("\n");
      SimSystem.report(Level.INFO, builder.toString());
      MasterServer server = new MasterServer(name);
      Entity.report("Master server (" + server.getName()
          + ") has been started on host "
          + java.net.InetAddress.getLocalHost().getHostName() + " ip: "
          + java.net.InetAddress.getLocalHost().getHostAddress()
          + ", listening at port " + DEFAULT_PORT + ".");

      // MasterServerWindow msw = new MasterServerWindow(server);
      // msw.setupGUI();

      Host.publish(server, MasterServer.DEFAULT_PORT);

    } catch (Exception e) {
      SimSystem.report(e);
      SimSystem.shutDown(2);
    }
  }

  @Override
  public int getNumberOfRegisteredServices(Class<?> serviceType) {
    return serviceRegistry.size(serviceType);
  }

  @Override
  public void serviceUnreachable(IService service) {
    unregister(service);
  }

  @Override
  public void register(IService service) {
    serviceRegistry.register(service);

    // if the service is based on the modeling and simulation framework we'll
    // check the version and issue a warning if they are not equal.
    if (service instanceof IMSSystemHostInformation) {
      try {
        if (((IMSSystemHostInformation) service).getSimSystemVersion()
            .compareTo(SimSystem.SIMSYSTEM + " " + SimSystem.VERSION) != 0) {

          SimSystem
              .report(
                  Level.WARNING,
                  "WARNING!!! The version of the m&s system of the just connected service differs from the servers version!!!\n Server: "
                      + SimSystem.SIMSYSTEM
                      + " "
                      + SimSystem.VERSION
                      + "\n Client ("
                      + service.getName()
                      + "): "
                      + ((IMSSystemHostInformation) service)
                          .getSimSystemVersion());
        }
      } catch (RemoteException e) {
        SimSystem.report(Level.SEVERE,
            "Could not query version from system host information service.", e);
      }
    }

  }

  @Override
  public void unregister(IService service) {
    serviceRegistry.unregister(service);

    // for (String simUid : simulationHostManagement.computedOn(client)) {
    //
    // handleSimulationBreakDown(this.getSimulationByName(simUid).getUid(),
    // client);
    // } FIXME: we need to handle the problem that a service currently used is
    // unregistered

  }

  /**
   * Sets the resilience to "active" or "inactive".
   * 
   * @param active
   *          the active
   */
  public void setResilienceActive(boolean active) {
    if (active) {
      resilienceManagement.createResilienceStorageConnection(this);
    }
  }

  @Override
  public List<Class<?>> getRegisteredServiceTypes() throws RemoteException {
    return serviceRegistry.getServiceTypes();
  }

  @Override
  public int getMaxNumberOfConcurrentJobs() throws RemoteException {
    return -1; // unlimited
  }

  @Override
  public String getServiceName() throws RemoteException {
    return "Master server (M&S System: " + SimSystem.SYSTEMNAME + ")";
  }

  @Override
  public Class<?> getServiceType() throws RemoteException {
    return IMasterServer.class;
  }

  @Override
  public List<ServiceInfo> getRegisteredServices() throws RemoteException {
    return serviceRegistry.getServiceInfos();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <D> D executeRunnableCommand(ComputationTaskIDObject simulationID,
      String command, Object[] args) {
    ISimulationServer host = getHostOfSimulationRun(simulationID);
    try {
      return (D) host.executeRunnableCommand(simulationID, command, args);
    } catch (RemoteException re) {
      SimSystem.report(re);
    }
    return null;
  }

  /**
   * @return the serviceRegistry
   */
  public ServiceRegistry getServiceRegistry() {
    return serviceRegistry;
  }
}
