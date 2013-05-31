/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.resilience;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.ProcessorState;

/**
 * Container of data, which collects different information of a host for the
 * resilience of a simulation.
 * 
 * @author tn004 (Thomas Noesinger)
 * 
 */
public class ResilienceCheckpointInformation implements Serializable {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -5024780994116027469L;

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
   * The SimulationMasterServer identifier.
   */
  private String masterservername = null;

  /**
   * The time of the simulation.
   */
  private double time = 0;

  /**
   * The ID of the simulation.
   */
  private long dataid = 0;

  /**
   * The constructor of the class.
   */
  public ResilienceCheckpointInformation() {
  }

  /**
   * The method sets the name of the model of the given host.
   * 
   * @param name
   *          : The name of the host.
   * @return True : name != null <br>
   *         False: otherwise
   */
  public boolean setHostName(String name) {
    boolean result = false;
    if (name != null) {
      hostname = name;
      result = true;
    }
    return result;
  }

  /**
   * @return The name of the host.
   */
  public String getHostName() {
    return hostname;
  }

  /**
   * The method sets the name of the model of the given host.
   * 
   * @param name
   *          : The name of the model of the given host.
   * @return True: name != null <br>
   *         False: otherwisern
   */
  public boolean setModelName(String name) {
    boolean result = false;
    if (name != null) {
      modelname = name;
      result = true;
    }
    return result;
  }

  /**
   * @return The name of the model of the given host.
   */
  public String getModelName() {
    return modelname;
  }

  /**
   * The method sets the model of the given host.
   * 
   * @param data
   *          : The model of the given host.
   * @return True: data != null <br>
   *         False: otherwise
   */
  public boolean setModel(IModel data) {
    boolean result = false;
    if (data != null) {
      model = data;
      result = true;
    }
    return result;
  }

  /**
   * @return The model of the given host.
   */
  public IModel getModel() {
    return model;
  }

  /**
   * The method sets a vector of connections to all children hosts of the given
   * host.
   * 
   * @param data
   *          : Vector of connections to all children hosts of the given host.
   * @return True: data != null <br>
   *         False: otherwise
   */
  public boolean setChildren(List<ResilienceFurtherSimulationInformation> data) {
    boolean result = false;
    if (data != null) {
      children = data;
      result = true;
    }
    return result;
  }

  /**
   * @return Vector of connections to all children hosts of the given host.
   */
  public List<ResilienceFurtherSimulationInformation> getChildren() {
    return children;
  }

  /**
   * The method sets the connection to the parent host of the given host.
   * 
   * @param data
   *          : Connection to the parent host of the given host.
   * @return True: data != null <br>
   *         False: otherwise
   */
  public boolean setParent(ResilienceFurtherSimulationInformation data) {
    boolean result = false;
    if (data != null) {
      parent = data;
      result = true;
    }
    return result;
  }

  /**
   * @return Connection to the parent host of the given host.
   */
  public ResilienceFurtherSimulationInformation getParent() {
    return parent;
  }

  /**
   * The method sets the state of the processor of the host.
   * 
   * @param data
   *          : The state of the processor of the host.
   * @return True: data != null <br>
   *         False: otherwise
   */
  public boolean setProcessorState(ProcessorState data) {
    boolean result = false;
    if (data != null) {
      state = data;
      result = true;
    }
    return result;
  }

  /**
   * @return The state of the processor of the host.
   */
  public ProcessorState getProcessorState() {
    return state;
  }

  /**
   * The method sets the partition with the correct hostname.
   * 
   * @param data
   *          : The partition with the correct hostname.
   * @return True: data != null <br>
   *         False: otherwise
   */
  public boolean setPartitionToHostname(Partition data) {
    boolean result = false;
    if (data != null) {
      partitionToHostname = data;
      result = true;
    }
    return result;
  }

  /**
   * @return The partition with the correct hostname.
   */
  public Partition getPartitionToHostname() {
    return partitionToHostname;
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
   * @return The SimulationMasterServer identifier.
   */
  public String getMasterServerName() {
    return masterservername;
  }

  /**
   * The method sets the ID of the simulation.
   * 
   * @param value
   *          : The ID of the simulation.
   */
  public void setDataID(long value) {
    dataid = value;
  }

  /**
   * @return The ID of the simulation.
   */
  public long getdataID() {
    return dataid;
  }

  /**
   * The method sets the time of the simulation.
   * 
   * @param value
   *          : The time of the simulation.
   */
  public void setTime(double value) {
    time = value;
  }

  /**
   * @return The time of the simulation.
   */
  public double getTime() {
    return time;
  }

  @Override
  public String toString() {
    String result = "";

    result =
        "MSS: " + masterservername + " / ID: " + dataid + " / Time: " + time
            + " / Modelname: " + modelname + " / Model: ";

    if (model == null) {
      result = result + "NULL";
    } else {
      result = result + model.toString();
    }
    result = result + " / Hostname: " + hostname + " / Children: ";

    if (children == null) {
      result = result + "NULL";
    } else {
      result = result + children.toString();
    }
    result = result + " / Parent: ";

    if (parent == null) {
      result = result + "NULL";
    } else {
      result = result + parent.toString();
    }
    result = result + " / State: ";

    if (state == null) {
      result = result + "NULL";
    } else {
      result = result + children.toString();
    }
    result = result + " / Partition: ";

    if (partitionToHostname == null) {
      result = result + "NULL";
    } else {
      result = result + children.toString();
    }

    return result;

  }
}