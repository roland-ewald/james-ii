/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.remote.dummyobjects.Dummy;
import org.jamesii.core.remote.dummyobjects.DummyMigrationController;
import org.jamesii.core.remote.dummyobjects.DummyObjectReferrer;
import org.jamesii.core.remote.hostcentral.HostCentralIDFactory;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.rmi.RemoteCommunicationCenter;

/**
 * This class provides a second JVM for TestRemoteCommunicationCenter. Make sure
 * to launch this class before running the test.
 * 
 * @author Simon Bartels
 * 
 */
public class LaunchFirstRemoteComCenter {

  /**
   * The DUMMY migration controller running on this JVM.
   */
  private static DummyMigrationController mc;

  /**
   * The remote communication center.
   */
  private static RemoteCommunicationCenter rcc;

  /**
   * The name our migration controller will be bound to.
   */
  public static final String MIGRATION_CONTROLLER = "MigrationController";

  /**
   * A dummy object which implements Serializable.
   */
  private static Dummy d;

  /**
   * The object id of the dummy.
   */
  private static IObjectId id;

  /**
   * The port our registry will listen to.
   */
  public static final int REGISTRY_PORT = 2342;

  /**
   * @param args
   */
  public static void main(String[] args) {

    System.out.println("Launching...");
    // Initializing the RCC.
    try {
      rcc = new RemoteCommunicationCenter();
    } catch (RemoteException e1) {
      e1.printStackTrace();
      return;
    }
    System.out.println("Remote Communication Center initiliazed");

    // Initializing the migration controller.
    try {
      mc = new DummyMigrationController();
      mc.setObjectReferrer(new DummyObjectReferrer());
    } catch (RemoteException e) {
      e.printStackTrace();
      return;
    }
    System.out.println("Migration Controller initiliazed");

    // Setting up our registry.
    Registry r;
    try {
      r = LocateRegistry.createRegistry(REGISTRY_PORT);
    } catch (RemoteException e) {
      e.printStackTrace();
      return;
    }
    try {
      r.bind(MIGRATION_CONTROLLER, mc);
    } catch (AccessException e) {
      e.printStackTrace();
      return;
    } catch (RemoteException | AlreadyBoundException e) {
      e.printStackTrace();
      return;
    }
    System.out.println("Registry initiliazed");
    // Registering an object here.
    HostCentralIDFactory idFac = new HostCentralIDFactory();
    d = new Dummy();
    id = idFac.create(new ParameterBlock(d, HostCentralIDFactory.PARAM_OBJECT));
    try {
      rcc.registerLocalObject(id, d);
    } catch (RemoteException e) {
      e.printStackTrace();
      return;
    }
    System.out.println("Migration Controller is running - ready.");
    // TODO: Now we have to keep running until...?
  }

}
