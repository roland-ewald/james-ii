/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts;

import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

import org.jamesii.SimSystem;

/**
 * The Class Host. The class has been created to provide a base for all host
 * based services which shall be accessible via the IHost interface and
 * descendants thereof.
 * 
 * @author Jan Himmelspach
 */
public abstract class Host extends UnicastRemoteObject implements IHost,
    IHostInformation {

  /**
   * Instantiates a new host.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  protected Host() throws RemoteException {
    super();
  }

  /**
   * See {@link UnicastRemoteObject}.
   * 
   * @param port
   * @param csf
   * @param ssf
   * @throws RemoteException
   */
  public Host(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf)
      throws RemoteException {
    super(port, csf, ssf);
  }

  /**
   * See {@link UnicastRemoteObject}.
   * 
   * @param port
   * @throws RemoteException
   */
  public Host(int port) throws RemoteException {
    super(port);
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4294472376661981404L;

  @Override
  public java.net.InetAddress getHostAddress() throws RemoteException {

    try {
      return java.net.InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      SimSystem.report(e);
    }
    return null;
  }

  /**
   * Publish the passed host using a local rmi registry and the port given. If
   * this method is not called, and if the host (remote object) is not published
   * elsewhere the it it not accessible from a remote site using Java RMI.
   * 
   * If successful true is returned, otherwise the exception gets logged, and
   * false is returned.
   * 
   * @param host
   *          the host to be published
   * @param port
   *          the port to be used (object is accessible through this port later
   *          on).
   * 
   * @return true, if successful
   */
  public static boolean publish(IHost host, Integer port) {
    try {
      String s =
          "rmi://" + java.net.InetAddress.getLocalHost().getHostName() + ":"
              + port + "/" + host.getName();
      // host.port = port;
      Naming.rebind(s, host);
      return true;
    } catch (Exception e) {
      SimSystem.report(e);
    }
    return false;
  }

}
