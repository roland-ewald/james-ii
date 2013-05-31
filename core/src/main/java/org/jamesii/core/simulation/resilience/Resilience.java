/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.resilience;

import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.cmdparameters.InvalidParameterException;
import org.jamesii.core.distributed.masterserver.IMasterServer;
import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.core.distributed.simulationserver.ISimulationHost;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.util.Semaphore;

/**
 * The class realizes resilience support for a computation task. A necessary
 * condition is, that the list of hosts (working on a computation task) saved in
 * the MasterServer is at all time up to date (no crashed hosts).
 * 
 * @author Thomas Nösinger
 * 
 */
public final class Resilience implements Runnable {

  private final int SECONDS_PER_MINUTE = 60;

  private final int MS_PER_SECONDS = 1000;

  private final int DEFAULT_MINUTES = 20;

  /**
   * indicates how much hosts are tested concurrently
   */
  private int count = 1;

  /**
   * indicates how many millisecond the time between collecting is
   */
  private long distance = SECONDS_PER_MINUTE * MS_PER_SECONDS * DEFAULT_MINUTES;

  /**
   * indicates if all data are collected
   */
  private boolean finished = false;

  /**
   * indicates the next host of the vector watchedHosts
   */
  private int nextHost = 0;

  /**
   * reference to the server
   */
  private MasterServer server;

  /** The hosts. */
  private List<ISimulationHost> hosts = null;

  /**
   * reference to the computation task
   */
  private ComputationTaskIDObject computationTask = null;

  /**
   * controls the creation of new threads
   */
  private boolean stop = false;

  /**
   * a Semaphore for the waiting time of collecting data
   */
  private Semaphore wait;

  /**
   * a counter of finished ResilienceInfoCollect threads
   */
  private int infoCollect = 0;

  /**
   * help variable for the semaphore wait
   */
  private boolean goOn = false;

  /**
   * The constructor of the class. If no MasterServer is given, an
   * RemoteException is thrown. If no value for the count or distance is given,
   * default values are used.
   * 
   * @param server
   *          : a reference to the SimulationMasterServer <br>
   * @param compId
   *          : identification of the computation task <br>
   * @param count
   *          : indicates how much hosts are tested concurrently <br>
   * @param distance
   *          : indicates how many millisecond the time between collecting is <br>
   * @param hosts
   *          the hosts
   */
  public Resilience(IMasterServer server, ComputationTaskIDObject compId,
      List<ISimulationHost> hosts, int count, long distance) {
    if (server != null) {
      this.server = (MasterServer) server;
      this.hosts = hosts;
      this.computationTask = compId;
      if (count > 1) {
        this.count = count;
      }
      if (distance > 1) {
        this.distance = distance;
      }
      new Thread(this).start();
    } else {
      throw new InvalidParameterException("No SimulationMasterServer given.");
    }
  }

  /**
   * The method collects all necessary data and stores them.
   * 
   */
  private void collect() {
    try {

      if (hosts != null) {
        server.executeRunnableCommand(computationTask, "pause", null);

        int countofhosts = hosts.size();

        wait = new Semaphore(1);
        wait.p();

        if (countofhosts > count) {
          countofhosts = count;
        }

        new ResilienceInfoCollect(this, computationTask, server, hosts, wait,
            countofhosts);

        wait.p();

        if (!stop) {
          server.executeRunnableCommand(computationTask, "pause", null);
        }
      } else {
        SimSystem.report(Level.WARNING,
            "No host given to the computation task.");
      }
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  /**
   * The method indicates if all hosts were checked.
   * 
   * @return true: all host of a computation task were checked / false:
   *         otherwise
   */
  protected synchronized boolean getFinished() {
    return finished;
  }

  /**
   * The method returns if all hosts were tested (for the semaphore wait).
   * 
   * @return true: collect() can go on/ false: otherwise
   */
  protected boolean getGoOn() {
    return goOn;
  }

  /**
   * The method returns the next host.
   * 
   * @return the next hostnumber
   */
  public synchronized int getNextHost() {
    int next = nextHost;

    if (computationTask != null) {
      if (hosts != null) {
        if (!finished) {
          nextHost++;
        }

        if (nextHost > (hosts.size() - 1)) {
          nextHost = -1;
          finished = true;

        }
      } else {
        SimSystem.report(Level.WARNING,
            "No hosts for the given computation task available.");
      }
    } else {
      SimSystem.report(Level.WARNING,
          "No computation task given. Cannot find a host for null.");
    }
    return next;
  }

  @Override
  public void run() {
    while (!stop) {
      try {
        Thread.sleep(distance);
      } catch (InterruptedException e1) {
        // ignore
      }

      synchronized (this) {
        // collect data and store them
        nextHost = 0;
        this.collect();

        finished = false;
      }
      goOn = false;
      infoCollect = 0;

    }
  }

  /**
   * The method stops the creation of new threads.
   */
  public void setStop() {
    this.stop = true;
  }

  /**
   * The method counts finished threads of ResilienceInfoCollect.
   */
  protected void infoCollectTerminates() {
    this.infoCollect++;
    if (infoCollect == count) {
      this.goOn = true;
    }
  }

}
