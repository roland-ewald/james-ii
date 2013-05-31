/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.dummyobjects;

import java.rmi.RemoteException;

import org.jamesii.core.remote.IMigrationController;
import org.jamesii.core.remote.hostcentral.rmi.IRemoteCommunicationCenter;

/**
 * The interface Dummy controller.
 * 
 * @author Simon Bartels
 * 
 */
public interface IDummyController extends
    IMigrationController<IRemoteCommunicationCenter> {

  public static final String MESSAGE = "hello World";

  /**
   * Prints a String and returns it.
   * 
   * @return MESSAGE
   * @throws RemoteException
   */
  public String helloWorld() throws RemoteException;
}
