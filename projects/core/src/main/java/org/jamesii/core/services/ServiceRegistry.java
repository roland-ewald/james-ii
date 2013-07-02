/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.observe.Mediator;
import org.jamesii.core.services.availability.Availability;
import org.jamesii.core.services.management.IServiceManagement;
import org.jamesii.core.services.management.ServiceManagement;

/**
 * The Class ServiceRegistry. Manages services of different types. Besides the
 * type grouped lists of services the service registry manages an availability
 * mechanism per type as well.<br/>
 * 
 * For each availability object a thread is required, however the list of
 * different service types should not get too large. If it once, completely
 * unexpectedly, is too large than we'll have to redesign this: in principal a
 * limited number of threads could be used instead, as well.<br/>
 * 
 * In addition to registering/unregistering services this class allows to
 * retrieve the services, too.
 * 
 * @author Jan Himmelspach
 */
public class ServiceRegistry extends Entity {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8731749457758133576L;

  /**
   * Default waiting time between two trials.
   */
  private static final int DISTANCE = 10000;

  /**
   * Default number of trials.
   */
  private static final int TRIALS = 3;

  /**
   * Default number of hosts to be checked concurrently.
   */
  private static final int CONCURRENT_HOSTS = 4;

  /** The service types. */
  private transient Map<Class<?>, ServiceManagement> serviceTypes =
      new HashMap<>();

  /**
   * The service availability. Services are "independent units" which might stop
   * to exist.
   */
  private transient Map<Class<?>, Availability> serviceAvailability =
      new HashMap<>();

  /** The resource availability. */
  private transient Map<IService, Integer> resourceAvailability =
      new HashMap<>();

  /**
   * The (local) resource usage. The value in here might not reflect the actual
   * service usage - if it is registered in more than one registry!
   */
  private transient Map<IService, Integer> resourceUsage = new HashMap<>();

  /** The service informations. */
  private transient Map<IService, ServiceInfo> serviceInfos = new HashMap<>();

  /** The local IDs of the services. */
  private transient Map<Integer, IService> localServiceID = new HashMap<>();

  /** The usage purpose, thus for what has a service been registered. */
  private transient Map<Object, List<IService>> usagePurpose = new HashMap<>();

  /** The management mediator. */
  private Mediator managementMediator = new Mediator();

  /** The management observer. */
  private ManagementObserver managementObserver;

  /** The service id counter. */
  private int serviceIDCounter = 0;

  /**
   * Instantiates a new service registry.
   */
  public ServiceRegistry() {
    super();
    managementObserver = new ManagementObserver(this);
  }

  /**
   * Register a new service type.
   * 
   * @param serviceType
   *          the service type
   */
  private void registerNewServiceType(Class<?> serviceType) {
    // currently we only create general management classes here. However,
    // once upon a time it might be necessary to exploit the plug-in mechanism
    // here, and create service specific instances instead.
    ServiceManagement newSM = new ServiceManagement();
    serviceTypes.put(serviceType, newSM);

    // enable forwarding of observer notifications from the management classes
    newSM.setMediator(managementMediator);
    newSM.registerObserver(managementObserver);

    // enable availability checking for the new service type, i.e., availability
    // is enabled per service type, and tus it could be configured per service
    // type (by exploiting the plug-in mechanism as well).
    serviceAvailability.put(serviceType, new Availability(newSM, null,
        CONCURRENT_HOSTS, TRIALS, DISTANCE));
  }

  /**
   * Register a server. The service will automatically be registered in the list
   * of services it belongs to.
   * 
   * @param service
   *          the service
   */
  public synchronized void register(IService service) {
    try {
      Class<?> serviceType = service.getServiceType();
      if (serviceTypes.get(serviceType) == null) {
        registerNewServiceType(serviceType);
      }
      serviceTypes.get(serviceType).register(service);
      // backup the maximum number of jobs concurrently to be handled by the
      // service
      resourceAvailability.put(service, service.getMaxNumberOfConcurrentJobs());
      resourceUsage.put(service, 0);

      int id = serviceIDCounter++;
      localServiceID.put(id, service);
      ServiceInfo info = new ServiceInfo(service);
      info.setLocalID(id);
      serviceInfos.put(service, info);
      // let's log the registering
      SimSystem.report(Level.INFO, "A service has been registered: " + service.getName()
      + " of type " + service.getServiceType() + " a "
      + service.getServiceName());
      changed(info);
    } catch (RemoteException e) {
      SimSystem.report(e);
    }

  }

  /**
   * Unregister the passed service. Afterwards the service will no longer be
   * available.
   * 
   * @param service
   *          the service
   */
  public synchronized void unregister(IService service) {
    try {
      Class<?> serviceType = service.getServiceType();
      if (serviceTypes.get(serviceType) != null) {
        serviceTypes.get(serviceType).unregister(service);
        resourceAvailability.remove(service);
        resourceUsage.remove(service);
      }
      ServiceInfo info = serviceInfos.remove(service);
      // let's log the unregistering
      SimSystem.report(Level.INFO, "A service has been unregistered: " + service.getName()
      + " of type " + service.getServiceType() + " a "
      + service.getServiceName());
      changed(info);
    } catch (RemoteException e) {
      SimSystem.report(e);
    }
  }

  /**
   * Gets the service list. The list is a copy of the list in the corresponding
   * management class, and thus any modifications to this list are ignored. If
   * you want to register/unregister services solely use the corresponding
   * methods provided by the registry.
   * 
   * @param serviceType
   *          the service type the list of services shall be retrieved for
   * 
   * @return the service list
   */
  public synchronized List<IService> getServiceList(Class<?> serviceType) {
    IServiceManagement sm = serviceTypes.get(serviceType);
    return new ArrayList<>(sm.getList());
  }

  /**
   * Gets the service infos.
   * 
   * @return the service infos
   */
  public synchronized List<ServiceInfo> getServiceInfos() {
    return new ArrayList<>(serviceInfos.values());
  }

  /**
   * Gets the list un booked services.
   * 
   * Please be aware that due to concurrent access to the registry the services
   * contained in this list might be booked in between by different threads.
   * 
   * @param <T>
   * 
   * @param serviceType
   *          the service type
   * 
   * @return the un booked service list
   */
  @SuppressWarnings("unchecked")
  public synchronized <T> List<T> getUnBookedServiceList(Class<?> serviceType) {
    IServiceManagement sm = serviceTypes.get(serviceType);
    List<IService> services = new ArrayList<>(sm.getList());
    Iterator<IService> itService = services.iterator();
    while (itService.hasNext()) {
      IService service = itService.next();
      // get rid of all services of the type looked for which do not have any
      // free resources
      if (!hasAvailableResources(service)) {
        itService.remove();
      }
    }
    return (List<T>) services;
  }

  /**
   * Book service. Try to get the given service. Will return false if the
   * service is not available. <br/>
   * Don't forget to free booked services afterwards!
   * 
   * @param service
   *          the service
   * @param purpose
   *          the purpose the service is registered for, should not be null
   * 
   * @return true, if successful
   */
  public synchronized boolean bookService(IService service, Object purpose) {
    int rA = resourceAvailability.get(service);
    int rU = resourceUsage.get(service);
    // let's see if we already have registered services for this purpose, if
    // not, create
    List<IService> purposeServices = usagePurpose.get(purpose);
    if (purposeServices == null) {
      purposeServices = new ArrayList<>();
      usagePurpose.put(purpose, purposeServices);
    }
    // now let's try to book
    switch (rA) {
    case -1: { // unlimited, just count
      resourceUsage.put(service, rU + 1);
      purposeServices.add(service);
      return true;
    }
    case 0: { // just one
      if (rU == 0) {
        resourceUsage.put(service, rU + 1);
        purposeServices.add(service);
        return true;
      }
    }
    default: {
      if (rU < rA) {
        resourceUsage.put(service, rU + 1);
        purposeServices.add(service);
        return true;
      }
    }
    }
    return false;
  }

  /**
   * Get and book a free service. Tries to get the given service. Will return
   * false if no service of the given type is available. <br/>
   * Don't forget to free a booked services afterwards!
   * 
   * @param <T>
   * 
   * @param serviceType
   *          the service type
   * @param purpose
   *          the purpose the service is booked for, should not be null
   * 
   * @return the booked service, if successful, null if not
   */
  @SuppressWarnings("unchecked")
  public synchronized <T extends IService> T getAndBookService(
      Class<?> serviceType, Object purpose) {
    IServiceManagement sm = serviceTypes.get(serviceType);
    if (sm != null) {
      List<IService> services = sm.getList();
      for (int i = 0; i < services.size(); i++) {
        IService service = services.get(i);
        if (bookService(service, purpose)) {
          return (T) service;
        }
      }
    }
    return null;
  }

  /**
   * Free a previously booked service.
   * 
   * @param service
   *          the service
   * @param purpose
   *          the purpose
   */
  public synchronized void freeService(IService service, Object purpose) {
    int rU = resourceUsage.get(service);
    // only free if it had been booked ---
    if (rU > 0) {
      resourceUsage.put(service, rU - 1);
      usagePurpose.get(purpose).remove(service);
    }
  }

  /**
   * Free all services booked for the given purpose.
   * 
   * Please note that these services are afterwards considered to be reusable by
   * other purposes, and thus if the purpose has not really finished this might
   * create serious problems.
   * 
   * @param purpose
   *          the purpose
   */
  public synchronized void freeServices(Object purpose) {
    List<IService> services = new ArrayList<>(usagePurpose.get(purpose));
    if (services != null) {
      for (IService service : services) {
        freeService(service, purpose);
      }
    }
    usagePurpose.remove(purpose);
  }

  /**
   * Gets the service usage.
   * 
   * @param service
   *          the service
   * 
   * @return the usage of the service, a value > 0 means that it is currently in
   *         use
   */
  public synchronized int getServiceUsage(IService service) {
    return resourceUsage.get(service);
  }

  /**
   * Checks for available resources at the given service.
   * 
   * Please be aware of the fact that due to concurrent access to the service
   * registry you might not be able to book a service even though this method
   * returned true. If you want to book the service if it is available you
   * should use the {@link #bookService} method instead.
   * 
   * @param service
   *          the service
   * 
   * @return true, if successful
   */
  public synchronized boolean hasAvailableResources(IService service) {
    int rA = resourceAvailability.get(service);
    int rU = resourceUsage.get(service);
    switch (rA) {
    case -1: { // unlimited, just count
      return true;
    }
    case 0: { // just one
      if (rU == 0) {
        return true;
      }
    }
    default: {
      if (rU < rA) {
        return true;
      }
    }
    }
    return false;
  }

  /**
   * Checks if the given service is registered.
   * 
   * <br/>
   * The return method of this method might be invalidated shortly afterwards.
   * 
   * @param service
   *          the service
   * 
   * @return true, if is service registered
   */
  public synchronized boolean isServiceRegistered(IService service) {
    return resourceAvailability.containsKey(service);
  }

  /**
   * Signal abort to all services.
   */
  public void signalAbort() {

    // stop all availability checking threads
    for (Availability avail : serviceAvailability.values()) {
      avail.setStop();
    }

    // TODO inform all services
  }

  /**
   * Gets the number of services of the given type.
   * 
   * @param serviceType
   *          the service type
   * 
   * @return the size
   */
  public int size(Class<?> serviceType) {
    ServiceManagement sm = serviceTypes.get(serviceType);
    if (sm != null) {
      return sm.size();
    }
    return 0;
  }

  /**
   * Gets the overall number of registered services.
   * 
   * 
   * @return the number of registered services
   */
  public int size() {
    int count = 0;
    for (ServiceManagement sm : serviceTypes.values()) {
      count += sm.size();
    }
    return count;
  }

  /**
   * Gets the services for a given purpose.
   * 
   * @param <T>
   * 
   * @param purpose
   *          the purpose
   * 
   * @return the services for purpose
   */
  @SuppressWarnings("unchecked")
  public <T extends IService> List<T> getServicesForPurpose(Object purpose) {
    return new ArrayList<>((Collection<T>) usagePurpose.get(purpose));
  }

  /**
   * Gets the registered service types.
   * 
   * @return the service types
   */
  public List<Class<?>> getServiceTypes() {
    return new ArrayList<>(serviceTypes.keySet());
  }

  /**
   * Gets the services.
   * 
   * @return the services
   */
  public List<IService> getServices() {
    List<IService> result = new ArrayList<>();
    for (ServiceManagement tm : serviceTypes.values()) {
      result.addAll(tm.getList());
    }
    return result;
  }
}
