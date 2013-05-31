/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.dummyobjects;

import java.rmi.RemoteException;

/**
 * This class just adds only another method to signal that the migration
 * controller can be triggered.
 * 
 * @author Simon Bartels
 * 
 */
public class DummyMigrationController extends
    org.jamesii.core.remote.hostcentral.controller.MigrationController
    implements IDummyController {

  /**
   * The serial version id.
   */
  private static final long serialVersionUID = 5406082180440878323L;

  /**
   * Creates a new instance.
   * 
   * @param rmCenter
   *          The associated remote communication center.
   * @throws RemoteException
   *           May be caused by RMI.
   */
  public DummyMigrationController() throws RemoteException {
    super();
  }

  @Override
  public String helloWorld() throws RemoteException {
    System.out.println(MESSAGE);
    return MESSAGE;
  }

}
