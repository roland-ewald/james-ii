/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts.system;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.base.Entity;
import org.jamesii.core.hosts.Host;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.services.TriggerableByNameFilter;
import org.jamesii.core.util.info.JavaInfo;

/**
 * The Class SimulationSystemHost. Encapsulates standard methods useful for all
 * service hosts which are directly based on the core of the simulation system.
 * 
 * @author Jan Himmelspach
 */
public abstract class MSSystemHost extends Host implements IMSSystemHost,
    IMSSystemHostInformation {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3626859497362564948L;

  /** The registry. */
  private Registry registry = SimSystem.getRegistry();

  /**
   * Instantiates a new simulation system host.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  protected MSSystemHost() throws RemoteException {
    super();
  }

  /**
   * See {@link Host}.
   * 
   * @param port
   * @param csf
   * @param ssf
   * @throws RemoteException
   */
  public MSSystemHost(int port, RMIClientSocketFactory csf,
      RMIServerSocketFactory ssf) throws RemoteException {
    super(port, csf, ssf);
  }

  /**
   * See {@link Host}.
   * 
   * @param port
   * @throws RemoteException
   */
  public MSSystemHost(int port) throws RemoteException {
    super(port);
  }

  @Override
  public boolean registerRemoteObserver(IRemoteObserver observer)
      throws RemoteException {
    // System.out.println("A remote observer is registered");
    // System.out.println(observer);
    // System.out.println(observer.getRegistererClass());

    // try {
    // IObserverRegisterer registerer = (IObserverRegisterer)
    // observer.getRegistererClass().newInstance();
    // return registerer.register(this, observer);
    // } catch (Exception e) {
    // SimSystem.report (e);
    // }
    // return false;
    // System.out.println(observer.getRegisterer());
    IObserverRegisterer registerer = observer.getRegisterer();
    return registerer.register(this, observer);

  }

  @Override
  public List<IPluginData> getPluginInfo() throws RemoteException {
    return SimSystem.getRegistry().getPlugins();
  }

  @Override
  public String getSimSystemVersion() throws RemoteException {
    return SimSystem.SIMSYSTEM + " " + SimSystem.VERSION;
  }

  @Override
  public JavaInfo getVMinfo() throws RemoteException {
    return new JavaInfo();
  }

  @Override
  public long getStartUpTime() throws RemoteException {
    return SimSystem.getRegistry().getStartUpTime();
  }

  /**
   * Inform attached listeners of something, print on console (if global
   * parameters allows to).
   * 
   * @param s
   *          - summarized information, ready to print
   * @param info
   *          - further information
   */
  public void report(String s, Object info) {
    SimSystem.report(Level.INFO, s);
  }

  /**
   * Inform attached listeners of something, print on console (if global
   * parameters allows to).
   * 
   * @param s
   *          - summarized information, ready to print
   */
  public void report(String s) {
    report(s, null);
  }

  @Override
  public void callMethodByName(String method, List<String[]> params)
      throws RemoteException {
    try {
      // List of parameters for the call
      Object[] finalParameters = new Object[params.size()];
      Class<?>[] paramClasses = new Class<?>[params.size()];
      // iterate the parameters
      for (int i = 0; i < params.size(); i++) {
        // get the class of the parameter
        Class<?> paramClass = Class.forName(params.get(i)[0]);
        paramClasses[i] = paramClass;
        // get the constructor
        Constructor<?> constr = paramClass.getConstructor(String.class);
        // create the parameter object
        finalParameters[i] = constr.newInstance(params.get(i)[1]);
      }
      // method class to be called
      Method methodClass = this.getClass().getMethod(method, paramClasses);
      methodClass.invoke(this, finalParameters);
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  @Override
  public Map<String, List<String[]>> getTriggerableMethods()
      throws RemoteException {
    return TriggerableByNameFilter.filter(this.getClass());
  }
}
