/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.computationserver;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.hosts.Host;
import org.jamesii.core.hosts.system.MSSystemHost;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.simulation.launch.DirectLauncher;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.info.JavaInfo;

/**
 * Server that executes
 * {@link org.jamesii.core.distributed.computationserver.IJob} objects.
 * 
 * @author Stefan Leye
 */
public class ComputationServer extends MSSystemHost implements
    IComputationServer {

  /** Serialization ID. */
  private static final long serialVersionUID = 8435058456232970391L;

  /** The manager managing the simulation runs. */
  private ComputationManagement compManager;

  /** Host name. */
  private String name;

  /** A reference to the server this client has itself registered to. */
  private IMasterServer server;

  /** The master server address. */
  private String masterServerAddress;

  protected ComputationServer(String[] args) throws RemoteException {
    super();
    Integer capacity = null;
    for (String s : args) {
      if (s.indexOf("-server=") == 0) {
        masterServerAddress = s.substring(8, s.length());
      } else if (s.indexOf("-name=") == 0) {
        name = s.substring(5, s.length());
      } else if (s.indexOf("-threads=") == 0) {
        s = s.substring(9, s.length());
        capacity = Integer.valueOf(s);
      }
    }
    if (name == null) {
      name = "CompServer" + Long.toString(System.currentTimeMillis());
      SimSystem.report(Level.WARNING, "WARNING: Execution would proceed with unnamed host, auto named the host as "
      + name);
    }
    if (masterServerAddress == null) {
      masterServerAddress =
          "rmi://localhost:" + MasterServer.DEFAULT_PORT + "/"
              + MasterServer.DEFAULT_BINDING_NAME;
    }
    if (capacity == null) {
      JavaInfo info = new JavaInfo();
      capacity = info.getCpus();
    }

    compManager = new ComputationManagement(capacity);

    SimSystem.report(Level.INFO, "Registering at master server: "
        + masterServerAddress);
    register(masterServerAddress);

    // System.out.println("Searching plugins!");
    SimSystem.getRegistry().getPlugins();
  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    try {
      LocateRegistry.createRegistry(MasterServer.DEFAULT_PORT);
    } catch (java.rmi.server.ExportException e1) {
      // let's assume that if we are here this means that there is already a
      // remote registry running at the given port which can be used by us as
      // well
      SimSystem.report(Level.WARNING,
          "Will continue and try to reuse the address ...", e1);
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
      SimSystem.report(Level.INFO, " --- Computation (client) server --- ");
      ComputationServer ser = new ComputationServer(args);

      Host.publish(ser, MasterServer.DEFAULT_PORT);

    } catch (Exception e) {
      SimSystem.report(e);
      SimSystem.shutDown(2);
    }
  }

  @Override
  public <V> void initializeJob(IJob<V> job, IUniqueID id)
      throws RemoteException {
    compManager.initializeJob(job, id);
  }

  @Override
  public <V> V executeJob(IUniqueID id, Serializable data)
      throws RemoteException {
    return compManager.executeJob(id, data);
  }

  @Override
  public void finalizeJob(IUniqueID id) throws RemoteException {
    compManager.finalizeJob(id);
  }

  /**
   * Internal method to report a problem on connecting to a server.
   * 
   * @param cause
   */
  private static void connectionException(Throwable cause) {
    SimSystem.report(Level.WARNING,
        "Have you used a schema like \"rmi://localhost:"
            + MasterServer.DEFAULT_PORT + "/"
            + MasterServer.DEFAULT_BINDING_NAME + "\" ???", cause);
    throw new ComputationSeverSetupException(
        "Exception occured while connecting to the server.", cause);

  }

  @Override
  public void register(String serverAdress) {
    // get a pointer to the server
    // try {
    SimSystem.report(Level.INFO,
        "Computation client tries to connect to the server " + serverAdress);

    try {
      server = (IMasterServer) Naming.lookup(serverAdress);
    } catch (MalformedURLException | RemoteException | NotBoundException e1) {
      connectionException(e1);
    }

    SimSystem.report(Level.INFO, "Client connected to server at "
        + serverAdress);

    //
    // make myself known to the server
    try {
      SimSystem.report(Level.INFO,
          "Computation client tries to register at the server " + serverAdress);

      server.register(this);

      SimSystem.report(Level.INFO,
          "Computation client is registered at the server!");

    } catch (RemoteException e) {
      SimSystem.report(e);

      throw new ComputationSeverSetupException(
          "Exception occured while registering at the server. Shutting down.",
          e);
    }
  }

  @Override
  public void setManagementMediator(IMediator mediator) throws RemoteException {
    compManager.setMediator(mediator);
  }

  @Override
  public int getMaxNumberOfConcurrentJobs() throws RemoteException {
    return -1;
  }

  @Override
  public String getName() throws RemoteException {
    return name;
  }

  @Override
  public String getServiceName() throws RemoteException {
    return "Computation server";
  }

  @Override
  public Class<?> getServiceType() throws RemoteException {
    return IComputationServer.class;
  }

}
