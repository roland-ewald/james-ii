/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.resilience;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.distributed.simulationserver.ISimulationHost;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.ProcessorState;

/**
 * Container of data, which collects different necessary information for the
 * resilience of a simulation.
 * 
 * @author tn004 (Thomas Noesinger)
 * 
 */
public class ResilienceSimulationInformation implements Serializable {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -6828327033499708719L;

  /**
   * The simulation.
   */
  private ComputationTaskIDObject simulation = null;

  /**
   * The host of the simulation about which data should be collected.
   */
  private ISimulationHost host = null;

  /**
   * The name of the host.
   */
  private String hostname = null;

  /**
   * The name of the model of the given host.
   */
  private String modelname = null;

  /**
   * The model of the given host.
   */
  private IModel model = null;

  /**
   * Vector of connections to all children hosts of the given host.
   */
  private List<ResilienceFurtherSimulationInformation> children =
      new Vector<>();

  /**
   * Connection to the parent host of the given host.
   */
  private ResilienceFurtherSimulationInformation parent = null;

  /**
   * The state of the processor of the host.
   */
  private ProcessorState state = null;

  /**
   * The partition with the correct hostname.
   */
  private Partition partitionToHostname = null;

  /**
   * Flag, which indicates, that the partitionToHostname is found.
   */
  private boolean partitionFound = false;

  /**
   * The SimulationMasterServer identifier.
   */
  private String masterservername = null;

  /**
   * The current hostnumber.
   */
  private int currentHostNumber = 0;

  /**
   * The overall number of hosts of the simulation.
   */
  private int overallHostNumber = 0;

  /** The server. */
  private IMasterServer server = null;

  /**
   * The constructor of the class.
   * 
   * @param sim
   *          : The simulation.
   * @param hostofsim
   *          : The host of the simulation about which data should be collected
   * @param server
   *          the server
   */
  public ResilienceSimulationInformation(IMasterServer server,
      ComputationTaskIDObject sim, ISimulationHost hostofsim) {
    this.server = server;
    if (sim == null) {
      SimSystem.report(Level.WARNING, "No simulation given.");
    } else {
      if (hostofsim == null) {
        SimSystem.report(Level.WARNING, "No host given.");
      } else {
        this.simulation = sim;
        this.host = hostofsim;
      }
    }
  }

  /**
   * Get the simulation ID.
   * 
   * @return the simulation ID
   */
  public ComputationTaskIDObject getSimulation() {
    return simulation;
  }

  /**
   * The host of the simulation about which data should be collected.
   * 
   * @return the host
   */
  public ISimulationHost getHost() {
    return host;
  }

  /**
   * The name of the host of the simulation about which data should be
   * collected.
   * 
   * @return the name of the host.
   */
  public String getHostName() {
    return hostname;
  }

  /**
   * The name of the model of the given host.
   * 
   * @return the name of the model
   */
  public String getModelName() {
    return modelname;
  }

  /**
   * The model of the given host.
   * 
   * @return the model
   */
  public IModel getModel() {
    return model;
  }

  /**
   * Get the Vector of connections to all children hosts of the given host.
   * 
   * @return all children hosts
   */
  public List<ResilienceFurtherSimulationInformation> getChildren() {
    return children;
  }

  /**
   * Get the connection to the parent host of the given host.
   * 
   * @return the connection to the parent
   */
  public ResilienceFurtherSimulationInformation getParent() {
    return parent;
  }

  /**
   * Get the state of the processor of the host.
   * 
   * @return state of the processor
   */
  public ProcessorState getProcessorState() {
    return state;
  }

  /**
   * Get the corresponding partition to the name of a host.
   * 
   * @return the partition with the correct hostname.
   */
  public Partition getPartitionToHostname() {
    return partitionToHostname;
  }

  /**
   * The flag, which indicates that the partition retrieved by
   * partitionToHostname is found.
   * 
   * @return flag indicating the partition is found
   */
  public boolean getPartitionFounded() {
    return partitionFound;
  }

  /**
   * The method sets the SimulationMasterServer identifier (unique name).
   * 
   * @param name
   *          : The unique SimulationMasterServer identifier.
   * @return True: name != null <br>
   *         False: otherwise
   */
  public boolean setMasterServerName(String name) {
    boolean result = false;
    if (name != null) {
      masterservername = name;
      result = true;
    }
    return result;
  }

  /**
   * Get the name of the master server.
   * 
   * @return The SimulationMasterServer identifier.
   */
  public String getMasterServerName() {
    return masterservername;
  }

  /**
   * The method sets the current host number.
   * 
   * @param number
   *          : The current hostnumber.
   * @return True: name >= 0 <br>
   *         False: otherwise
   */
  public boolean setCurrentHostNumber(int number) {
    boolean result = false;
    if (number >= 0) {
      currentHostNumber = number;
      result = true;
    }
    return result;
  }

  /**
   * Get the number of the current host.
   * 
   * @return the current hostnumber.
   */
  public int getCurrentHostNumber() {
    return currentHostNumber;
  }

  /**
   * The method sets the overall number of hosts of the simulation.
   * 
   * @param number
   *          : The overall number of hosts of the simulation.
   * @return True: number > 0 <br>
   *         False: otherwise
   */
  public boolean setOverallHostNumber(int number) {
    boolean result = false;
    if (number > 0) {
      overallHostNumber = number;
      result = true;
    }
    return result;
  }

  /**
   * The number of hosts participating on the simulation.
   * 
   * @return the overall number of hosts of the simulation.
   */
  public int getOverallHostNumber() {
    return overallHostNumber;
  }

  /**
   * The method collects all necessary data for the resilience of the given
   * simulation.
   * 
   * @return True: no errors occurred <br>
   *         False: otherwise
   */
  public boolean setFurtherInformation() {
    boolean result = false;

    if (!setHostName()) {
      SimSystem.report(Level.WARNING, "Failure by setting the host name.");
    } else {
      if (simulation == null) {
        SimSystem.report(Level.WARNING, "No simulation given.");
      } else {
        Partition partition = null;
        try {
          partition = server.getPartition(simulation);
        } catch (RemoteException e1) {
          SimSystem.report(e1);
        }
        if (partition != null) {
          try {
            if (partition.getHost().getName().equalsIgnoreCase(hostname)) {

              partitionToHostname = partition;
              partitionFound = true;

              if (setModelAndStateAndChildren(partitionToHostname)) {
                result = true;
              }

              // partition is not the correct one
            } else {
              if (!partition.hasSubPartitions()) {
                SimSystem
                    .report(Level.WARNING,
                        "Correct partition not found (no partitions available at all).");
              } else {
                // go through all subpartitions /subsubpartitions... and find
                // the
                // one with the correct hostname
                searchHost(partition);

                if (!partitionFound) {
                  SimSystem.report(Level.WARNING,
                      "Correct partition not found.");
                } else {
                  if (setModelAndStateAndChildren(partitionToHostname)) {
                    // next two value are at the moment null, because hostname
                    // and
                    // modelname weren't available by the
                    // making of parent
                    parent.setHostNameOfOther(hostname);
                    parent.setModelNameOfOther(modelname);

                    result = true;
                  }
                }
              }
            }
          } catch (RemoteException e) {
            SimSystem.report(Level.WARNING,
                "Error during getting further information: " + e);
          }
        }

      }
    }

    return result;
  }

  /**
   * The method gets the name of the given host and stores it.
   * 
   * @return True: no errors occurred <br>
   *         False: otherwise
   */
  private boolean setHostName() {
    boolean result = false;

    if (host != null) {
      try {
        hostname = host.getName();
        result = true;
      } catch (RemoteException e) {
        result = false;
        SimSystem.report(Level.WARNING,
            "Exception by getting name of the host: " + e);
      }
    } else {
      SimSystem.report(Level.WARNING, "No host != null is given.");
    }
    return result;
  }

  /**
   * The method stores the connections to the children of the given partition.
   * 
   * @param part
   *          : The partition of which the connections to the children should be
   *          stored.
   * @return True: no errors occurred <br>
   *         False: otherwise
   * @throws RemoteException
   *           exception on the remote side
   */
  private boolean makeChildren(Partition part) throws RemoteException {
    boolean result = false;

    if (part == null) {
      SimSystem.report(Level.WARNING, "No partition given.");
    } else {
      if (part.hasSubPartitions()) {
        for (int i = 0; i < part.getSubPartitionCount(); i++) {
          Partition childpart = part.getSubPartition(i);

          String childmodelname = childpart.getModel().getName();
          String childhostname = childpart.getHost().getName();

          ResilienceFurtherSimulationInformation child =
              new ResilienceFurtherSimulationInformation(childmodelname,
                  modelname, childhostname, hostname);

          children.add(child);
        }
      } else {
        children = null;
      }
      result = true;
    }
    return result;
  }

  /**
   * The method stores the connection to the parent node given by the partition.
   * 
   * @param parentpart
   *          : The parent partition.
   * @throws RemoteException
   *           exception on the remote side
   */
  private void makeParent(Partition parentpart) throws RemoteException {
    if (parentpart == null) {
      parent = null;
    } else {
      String parentmodelname = parentpart.getModel().getName();
      String parenthostname = parentpart.getHost().getName();

      parent =
          new ResilienceFurtherSimulationInformation(parentmodelname, null,
              parenthostname, null);
    }
  }

  /**
   * The method searches the partition where the name of the host is the
   * searched one (leftrecursive depth-first search).
   * 
   * @param partition
   *          : The current checked partition.
   * @throws RemoteException
   *           exception on the remote side
   */
  private void searchHost(Partition partition) throws RemoteException {
    if (partition.hasSubPartitions()) {
      for (int i = 0; i < partition.getSubPartitionCount(); i++) {
        if (partitionFound) {
          i = partition.getSubPartitionCount();
        } else {
          if (partition.getSubPartition(i).getHost().getName()
              .equalsIgnoreCase(hostname)) {
            this.partitionToHostname = partition.getSubPartition(i);
            makeParent(partition);
            partitionFound = true;
          } else {
            searchHost(partition.getSubPartition(i));
          }
        }
      }
    }
  }

  /**
   * The method sets the model, modelname and state appositely to the given
   * partition. It also calls makeChildren().
   * 
   * @param partition
   *          : The partition with the correct hostname.
   * @return True: no errors occurred <br>
   *         False: otherwise
   * @throws RemoteException
   *           on the remote side
   */
  private boolean setModelAndStateAndChildren(Partition partition)
      throws RemoteException {
    boolean result = false;

    try {
      state = partition.getProcessorInfo().getLocal().getState();
    } catch (java.lang.NullPointerException e) {
      SimSystem.report(Level.WARNING, "ProcessorState not available.", e);
    }

    model = partition.getModel();
    modelname = model.getName();

    if (!makeChildren(partition)) {
      SimSystem.report(Level.WARNING, "Failure by setting the children.");
    } else {
      result = true;
    }

    return result;
  }
}
