/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.resilience;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IDataBase;
import org.jamesii.core.data.resilience.IDataResilience;
import org.jamesii.core.data.resilience.plugintype.AbstractDataResilienceFactory;
import org.jamesii.core.data.resilience.plugintype.DataResilienceFactory;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * The class is the connection between the datastorage and the
 * SimulationMasterServer. It provides methods for storing and getting
 * resilience information.
 * 
 * @author Thomas Noesinger
 */
public class ResilienceStorageConnection implements
    IResilienceStorageConnection {

  /** The error message to display if no data storage is given. */
  private static final String NO_DATASTORAGE_GIVEN_ERROR_MSG =
      "No datastorage given.";

  /**
   * Indicates if the setDataStorage() was called.
   */
  private boolean dataStorageCalled = false;

  /**
   * The SimulationMasterServer.
   */
  private MasterServer server = null;

  /**
   * Unique name of the SimulationMasterServer.
   */
  private String servername = null;

  /**
   * The data storage.
   */
  private IDataResilience storage = null;

  /**
   * HashMap of stored checkpoint information (for validation).
   */
  private Map<ComputationTaskIDObject, Map<Double, SortedSet<Integer>>> storedCheckpoint =
      new HashMap<>();

  /**
   * The constructor of the class (it initialize a default data storage).
   * 
   * @param server
   *          : The SimulationMasterServer.
   */
  public ResilienceStorageConnection(IMasterServer server) {
    if (server == null) {
      SimSystem.report(Level.SEVERE, "No SimulationMasterServer given.");
    } else {
      this.server = (MasterServer) server;
      this.servername = SimSystem.getUniqueName();

      // default values used...a server.getDataStorageValues() is better
      try {
        DataResilienceFactory factory =
            SimSystem.getRegistry().getFactory(
                AbstractDataResilienceFactory.class, null);

        ParameterBlock parameter = new ParameterBlock();
        parameter.addSubBlock(factory.USER, "root");
        parameter.addSubBlock(factory.PASSWORD, "");
        parameter.addSubBlock(factory.DRIVER, "com.mysql.jdbc.Driver");
        parameter.addSubBlock(factory.DATABASEURL,
            "jdbc:mysql://localhost/test");
        setDataStorage(factory.create(parameter));
      } catch (Exception ex) {
        SimSystem.report(Level.SEVERE,
            "It was not possible to establish the Resilience data connection! "
                + "Will continue without resilience support!", ex);
      }
    }
  }

  /**
   * The method sets the datastorage, opens a connection and creates the
   * MasterServer table.
   * 
   * @param dataStorage
   *          : The datastorage, which should be used.
   */
  @Override
  public final void setDataStorage(IDataResilience dataStorage) {
    if (dataStorage == null) {
      SimSystem.report(Level.SEVERE, NO_DATASTORAGE_GIVEN_ERROR_MSG);
    } else {

      if (!(dataStorage instanceof IDataBase)) {
        SimSystem.report(Level.SEVERE,
            "Datastorage does not provide the required interface: "
                + IDataBase.class.getName());
      } else {
        storage = dataStorage;
        try {
          if (storage instanceof IDataBase) {
            ((IDataBase) storage).openBase();
            ((IDataBase) storage).createBase(null);
          }
        } catch (ClassNotFoundException e) {
          SimSystem.report(e);
        } catch (SQLException e) {
          SimSystem.report(e);
        } catch (Exception e) {
          SimSystem.report(e);
        }

        dataStorageCalled = true;

      }

    }
  }

  /**
   * The method returns the used datastorage.
   * 
   * @return The datastorage or NULL, if no storage is available.
   */
  @Override
  public IDataResilience getDataStorage() {
    return storage;
  }

  /**
   * The method closes the connection to the given datastorage.
   */
  @Override
  public void shutDown() {
    if (dataStorageCalled) {
      try {
        ((IDataBase) storage).closeBase();
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }
  }

  /**
   * The method sets the resilience information, which have to be stored in the
   * datastorage. It also adds information about the simulation (dataid, time)
   * and server (name).
   * 
   * @param data
   *          : The data, which should be stored.
   */
  @Override
  public synchronized void setResilienceInformation(
      ResilienceSimulationInformation data) {
    boolean store = true;
    ComputationTaskIDObject dataid = null;
    Double time = 0.0;

    if (data.getSimulation() == null) {
      SimSystem.report(Level.SEVERE, "No simulation given.");
      store = false;
    } else {
      dataid = data.getSimulation();
      try {
        time = server.getSimulationRunProperty(dataid, "TIME");
      } catch (RemoteException e) {
        SimSystem.report(e);
      }
    }

    if (!data.setMasterServerName(servername)) {
      SimSystem.report(Level.SEVERE,
          "Error in setting the SimulationMasterServer.");
    }

    if (store) {
      storeResilienceInfo(dataid, time, data);

      int currenthost = data.getCurrentHostNumber();
      int allhost = data.getOverallHostNumber();

      addCheckpointToMap(dataid, time, currenthost, allhost);
    } else {
      SimSystem.report(Level.SEVERE, "No enough information given.");
    }
  }

  /**
   * The method stores the given information into the given datastorage.
   * 
   * @param dataid
   *          : The ID of the simulation.
   * @param time
   *          : The time of the simulation.
   * @param data
   *          : The data, which should be stored.
   */
  private void storeResilienceInfo(ComputationTaskIDObject dataid, double time,
      ResilienceSimulationInformation data) {
    if (dataStorageCalled) {
      storage.writeData((Long) dataid.getExternalID(), time, data);
    } else {
      SimSystem.report(Level.SEVERE, NO_DATASTORAGE_GIVEN_ERROR_MSG);
    }
  }

  /**
   * The method checks, if a checkpoint is available for the given simulation
   * (it calls getResilienceInformation() and analyses the given result).
   * 
   * @param simulation
   *          : The simulation run, which should be checked.
   * @return True: a checkpoint is available <br>
   *         False: otherwise
   */
  @Override
  public boolean checkIfCheckpointIsAvailable(ISimulationRun simulation) {
    boolean result = false;

    if (this.getResilienceInformation(simulation) != null) {
      result = true;
    }

    return result;
  }

  /**
   * The method gets information about the given simulation from the
   * datastorage.
   * 
   * @param simulation
   *          : The simulation, which should be started again.
   * @return The last valid checkpoint or NULL, if no data are available.
   */
  @Override
  public synchronized List<ResilienceCheckpointInformation> getResilienceInformation(
      ISimulationRun simulation) {
    List<ResilienceCheckpointInformation> data = null;

    if (!dataStorageCalled) {
      SimSystem.report(Level.SEVERE, NO_DATASTORAGE_GIVEN_ERROR_MSG);
    } else {
      if (simulation == null) {
        SimSystem.report(Level.SEVERE, "No simulation given.");
      } else {
        Long dataid = simulation.getUniqueIdentifier().getExternalID();
        data = storage.getLastCheckpoint(servername, dataid);
      }
    }

    // Just for testing purposes
    /*
     * if (data != null) for (ResilienceCheckpointInformation check: data){
     * System.out.println(check.toString()); }
     */

    return data;
  }

  /**
   * The method collects information about the stored checkpoints and validate
   * them, if data are collected from all hosts (associated with a simulation).
   * 
   * @param dataid
   *          : The ID of the simulation.
   * @param time
   *          : The time of the simulation.
   * @param currenthost
   *          : The number of the current host.
   * @param allhost
   *          : The overall number of hosts associated with a simulation.
   */
  private void addCheckpointToMap(ComputationTaskIDObject dataid, double time,
      int currenthost, int allhost) {
    boolean testValidation = false;

    if (!storedCheckpoint.containsKey(dataid)) {
      TreeSet<Integer> hostnumbers = new TreeSet<>();
      hostnumbers.add(currenthost);
      Map<Double, SortedSet<Integer>> times = new HashMap<>();
      times.put(time, hostnumbers);
      storedCheckpoint.put(dataid, times);
    } else {

      if (!storedCheckpoint.get(dataid).containsKey(time)) {
        TreeSet<Integer> hostnumbers = new TreeSet<>();
        hostnumbers.add(currenthost);
        Map<Double, SortedSet<Integer>> times = storedCheckpoint.get(dataid);
        if (times == null) {
          times = new HashMap<>();
        }
        times.put(time, hostnumbers);
        storedCheckpoint.put(dataid, times);
      } else {
        SortedSet<Integer> hostnumbers = storedCheckpoint.get(dataid).get(time);
        if (hostnumbers == null) {
          hostnumbers = new TreeSet<>();
        }
        hostnumbers.add(currenthost);

        Map<Double, SortedSet<Integer>> times = storedCheckpoint.get(dataid);
        times.put(time, hostnumbers);
        storedCheckpoint.put(dataid, times);

        testValidation = true;
      }
    }
    if (allhost == 1) {
      testValidation = true;
    }

    if (testValidation) {
      SortedSet<Integer> hostnumbers = storedCheckpoint.get(dataid).get(time);
      int count = hostnumbers.size();

      if (allhost == count) {
        boolean result = validateCheckpoint(dataid, time);

        if (result) {
          SimSystem.report(Level.SEVERE, "Checkpoint validated for simulation "
              + dataid + " and time " + time + ".");
        } else {
          SimSystem.report(Level.SEVERE,
              "Checkpoint validation for simulation " + dataid + " and time "
                  + time + " failed.");
        }
      }
    }
  }

  /**
   * The method validates a checkpoint (identified by the
   * SimulationMasterServer, ID of simulation and the time).
   * 
   * @param dataid
   *          : The ID of the simulation.
   * @param time
   *          : The time of the checkpoint.
   * @return True: no errors occurred <br>
   *         False: otherwise
   */
  private boolean validateCheckpoint(ComputationTaskIDObject dataid, double time) {
    boolean result = false;

    if (!dataStorageCalled) {
      SimSystem.report(Level.SEVERE, NO_DATASTORAGE_GIVEN_ERROR_MSG);
    } else {
      storage.setStatusOfCheckpoint(servername, (Long) dataid.getExternalID(),
          time);
      result = true;
    }

    return result;
  }

  /**
   * The method validates a checkpoint (identified by the
   * SimulationMasterServer, ID of simulation and the time).
   * 
   * @param simulation
   *          : The simulation, from which a checkpoint should be validated.
   * @param time
   *          : The time of the checkpoint.
   * @return True: no errors occurred <br>
   *         False: otherwise
   */
  @Override
  public boolean validateCheckpoint(ISimulationRun simulation, double time) {
    boolean result = false;

    if (!dataStorageCalled) {
      SimSystem.report(Level.SEVERE, NO_DATASTORAGE_GIVEN_ERROR_MSG);
    } else {
      if (simulation == null) {
        SimSystem.report(Level.SEVERE, "No simulation given.");
      } else {
        Long dataid = simulation.getUniqueIdentifier().getExternalID();
        result = storage.setStatusOfCheckpoint(servername, dataid, time);
      }
    }

    return result;
  }
}
