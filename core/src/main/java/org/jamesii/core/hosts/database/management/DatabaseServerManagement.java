/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts.database.management;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jamesii.core.hosts.database.DBServerInfo;
import org.jamesii.core.services.IService;
import org.jamesii.core.services.management.ServiceManagement;
import org.jamesii.core.util.misc.Pair;

/**
 * Manages database servers that are registered at the master server.
 * 
 * @author Roland Ewald
 */
public class DatabaseServerManagement extends ServiceManagement {

  /** The serialization ID. */
  private static final long serialVersionUID = 5244653539973011634L;

  /**
   * Contains a mapping between database servers and the simulations where they
   * are used as sinks. So far, a db server is only used for a single
   * simulation.
   */
  private Map<IService, String> usedServers = new HashMap<>();

  /**
   * Mark the DB server's information as used by the given simulation.
   * 
   * @param simName
   *          the name of the simulation that uses the DB server described in
   *          the information
   * @param dbInfo
   *          the information
   */
  public synchronized void useFor(String simName, DBServerInfo dbInfo) {
    usedServers.put(dbInfo, simName);
    changed(new Pair<>(simName, dbInfo));
  }

  /**
   * Frees the database server identified by the information.
   * 
   * @param simName
   *          the name of the simulation
   */
  public synchronized void freeHost(String simName) {
    for (Entry<IService, String> entry : usedServers.entrySet()) {
      if (entry.getValue().compareTo(simName) == 0) {
        usedServers.remove(entry.getKey());
        return;
      }
    }
  }

  /**
   * Checks whether there are free DB servers.
   * 
   * @return true if free DB servers exist, otherwise false
   */
  public boolean existsFreeDBServer() {
    return getFreeServers().size() > 0;
  }

  /**
   * Returns all database servers that are not currently used.
   * 
   * @return set of DB informations on free DB servers
   */
  public synchronized Set<IService> getFreeServers() {
    HashSet<IService> free = new HashSet<>();
    Set<IService> used = usedServers.keySet();
    for (IService dbInf : getElements()) {
      if (!used.contains(dbInf)) {
        free.add(dbInf);
      }
    }
    return free;
  }

  /**
   * Gets the free server.
   * 
   * @return the free server
   */
  public synchronized IService getFreeServer() {
    Set<IService> used = usedServers.keySet();
    for (IService dbInf : getElements()) {
      // System.out.println(dbInf);
      if (!used.contains(dbInf)) {
        return dbInf;
      }
    }
    return null;
  }

}
