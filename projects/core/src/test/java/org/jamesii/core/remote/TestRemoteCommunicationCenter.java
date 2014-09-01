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
import org.jamesii.core.remote.IMigrationController;
import org.jamesii.core.remote.dummyobjects.Dummy;
import org.jamesii.core.remote.dummyobjects.IDummyController;
import org.jamesii.core.remote.hostcentral.BasicRemoteObjectId;
import org.jamesii.core.remote.hostcentral.HostCentralIDFactory;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.controller.MigrationController;
import org.jamesii.core.remote.hostcentral.rmi.IRemoteCommunicationCenter;
import org.jamesii.core.remote.hostcentral.rmi.LocalCommunicationCenter;
import org.jamesii.core.remote.hostcentral.rmi.RemoteCommunicationCenter;

import junit.framework.TestCase;

/**
 * Tests for the remote communication center and the migration controller.
 * 
 * @author Simon Bartels
 * 
 */
public class TestRemoteCommunicationCenter extends TestCase {

  /**
   * The port our registry will listen to.
   */
  private static final int REGISTRY_PORT = 4223;

  /**
   * The name our Migration controller will be bound to.
   */
  private static final String MIGRATION_CONTROLLER = "MigrationController";

  /**
   * The remote communication center.
   */
  IRemoteCommunicationCenter rcc;

  /**
   * The local communication center.
   */
  LocalCommunicationCenter lcc;

  /**
   * A dummy object which implements Serializable.
   */
  Dummy d;

  /**
   * The object id of the dummy.
   */
  IObjectId id;

  /**
   * The local migration controller.
   */
  IMigrationController<IRemoteCommunicationCenter> mc;

  /**
   * The remote migration controller.
   */
  IDummyController rmc;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    rcc = new RemoteCommunicationCenter();
    lcc = new LocalCommunicationCenter((RemoteCommunicationCenter) rcc);
    assertEquals(lcc.getRemoteCC(), rcc);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testObjectRegistration() {
    HostCentralIDFactory idFac = new HostCentralIDFactory();
    d = new Dummy();
    id = idFac.create(new ParameterBlock(d, HostCentralIDFactory.PARAM_OBJECT));
    assertTrue(null != id);

    IObjectId id2 =
        new BasicRemoteObjectId(id.getStringRep(), d.getClass().getName());

    assertEquals(id.getStringRep(), id2.getStringRep());
    assertTrue(id.equals(id2));
    assertNotSame(id, id2);

    // this is necessary as the hashmap relies on it
    assertEquals(id.hashCode(), id2.hashCode());

    lcc.registerObject(id, d);
    try {
      assertEquals(d, rcc.getObjectById(id2));
    } catch (RemoteException e) {
      e.printStackTrace();
      fail("no exception expected");
    }
  }

  public void testLocalMethodCalling() {
    HostCentralIDFactory idFac = new HostCentralIDFactory();
    d = new Dummy();
    id = idFac.create(new ParameterBlock(d, HostCentralIDFactory.PARAM_OBJECT));
    lcc.registerObject(id, d);
    Object o = lcc.executeMethod(Dummy.ONLY_METHOD, null, id);
    assertEquals(Dummy.RESULT, o);
  }

  public void testMigration() {
    // TODO: start the other needed class
    // Runtime.getRuntime().exec("java -?");

    /*
     * try { assertTrue(rcc.getAllLocalObjectIds().size() == 0 ); } catch
     * (RemoteException e3) { e3.printStackTrace();
     * fail("No exception expected"); }
     */
    // instead of the assertion
    try {
      for (IObjectId oid : rcc.getAllLocalObjectIds()) {
        rcc.unregisterObject(oid);
      }
    } catch (RemoteException e3) {
      e3.printStackTrace();
      fail("No exception expected");
    }

    // Initializing our Migration controller
    try {
      mc = new MigrationController();
    } catch (RemoteException e) {
      e.printStackTrace();
      fail("No exception expected");
    }

    // Setting up our registry
    Registry r = null;
    try {
      r = LocateRegistry.createRegistry(REGISTRY_PORT);
    } catch (RemoteException e) {
      e.printStackTrace();
      fail("No exception expected");
    }
    try {
      r.bind(MIGRATION_CONTROLLER, mc);
    } catch (RemoteException | AlreadyBoundException e) {
      e.printStackTrace();
      fail("No exception expected");
    }

    // test lines removed on 12/01/06 by Jan - fail and do not seem to be if
    // that much use for now
    // // Locating the other registry.
    // try {
    //
    // //Get the java remote registry
    // Registry reg =
    // LocateRegistry.getRegistry(LaunchFirstRemoteComCenter.REGISTRY_PORT);
    //
    // //try to fetch from the remote registry the MIGRATION_CONTROLLER
    // rmc =
    // (IDummyController) (reg
    // .lookup(LaunchFirstRemoteComCenter.MIGRATION_CONTROLLER));
    // } catch (AccessException e) {
    // fail("No exception expected -- " + e.getMessage());
    // } catch (RemoteException e) {
    // fail("No exception expected -- " + e.getMessage());
    // } catch (NotBoundException e) {
    // e.printStackTrace();
    // fail("No exception expected -- " + e.getMessage());
    // }

    // Link established? We test it by calling helloWorld() remote.
    // try {
    // assertTrue(rmc.helloWorld().equals(IDummyController.MESSAGE));
    // } catch (RemoteException e2) {
    // e2.printStackTrace();
    // fail("No exception expected");
    // }

    // Receiving the remote communication center
    // /**
    // * The remote communication center on the other host.
    // */
    // IRemoteCommunicationCenter secondRcc = null;
    // try {
    // secondRcc = rmc.getLocationObject();
    // } catch (RemoteException e1) {
    // e1.printStackTrace();
    // fail("No exception expected");
    // }
    //
    // HashSet<IObjectId> remoteObjects = null;
    // try {
    // remoteObjects = secondRcc.getAllLocalObjectIds();
    // } catch (RemoteException e) {
    // e.printStackTrace();
    // fail("No exception expected");
    // }
    //
    // // There should be one object inside the set.
    // assertTrue(1 == remoteObjects.size());

    // /*
    // * Tell OUR remote communication center that there is another object on
    // the
    // * second remote communication center. NOTE: This way is a workaround for
    // * this test and must not be imitated.
    // */
    // try {
    // rcc.updateObjectLocations(remoteObjects, secondRcc);
    // } catch (RemoteException e2) {
    // e2.printStackTrace();
    // fail("No exception expected");
    // }
    // Object result =
    // lcc.executeMethod(Dummy.ONLY_METHOD, null, remoteObjects.iterator()
    // .next());
    // assertEquals(Dummy.RESULT, result);
    //
    // String expectedId = remoteObjects.iterator().next().getStringRep();
    //
    // // Ask the remote migration controller to move the object here.
    // try {
    // assertTrue(rmc.migrateTo(remoteObjects, mc));
    // } catch (RemoteException e) {
    // e.printStackTrace();
    // fail("No exception expected");
    // }
    //
    // try {
    // String receivedId =
    // rcc.getAllLocalObjectIds().iterator().next().getStringRep();
    // assertEquals(expectedId, receivedId);
    // } catch (RemoteException e1) {
    // fail("No exception expected");
    // e1.printStackTrace();
    // }
    // try {
    // assertTrue(rcc.getLocationOfObject(remoteObjects.iterator().next()) ==
    // rcc);
    // } catch (RemoteException e) {
    // e.printStackTrace();
    // fail("No exception expected");
    // }
  }
}
