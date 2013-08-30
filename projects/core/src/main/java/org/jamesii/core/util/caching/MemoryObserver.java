/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.caching;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;

import org.jamesii.SimSystem;

/**
 * Caching usually means to trade memory usage for speed. Not only if this is
 * done extensively the problem might occur that during runtime an
 * OutOfMemoryError is generated although sufficient memory would be available
 * without an too extensive caching. This class will be instantiated
 * automatically by the system and makes usage of the memory mx bean to get
 * notified if the memory usage threshold is reached. If this happens all here
 * registered listeners will get notified so that they can free memory. Whether
 * this means that the complete cache is discarded or whether the cache is just
 * shrinked depends on the implementation. The general design concept is that
 * any cache has to react to this problem.<br/>
 * Please note: this relies on internals of the JVM used and the methods it uses
 * have not to be supported by all JVMs. However, if supported, this class can
 * be used to avoid out of memory errors. Tests have revealed that it will only
 * work if there is something to garbage collect, i.e., a series of new objects
 * without any remove in between can still cause out of memory errors. <br/>
 * This class uses weak references! This means that you need a reference to the
 * listener somewhere else - or the listener will be garbage collected on the
 * next gc run!
 * 
 * @author Jan Himmelspach
 * 
 */
public final class MemoryObserver {

  /**
   * The default threshold. If less memory is available the low memory
   * information will be spread.
   */
  private static final int DEFAULTTHRESHOLD = 131072;

  /**
   * The list of listeners.
   */
  private List<WeakReference<ILowMemoryListener>> listeners = new ArrayList<>();

  private ReferenceQueue<ILowMemoryListener> listenersToBeRemoved =
      new ReferenceQueue<>();

  private MemoryNotificationListener listener;

  /**
   * Singleton.
   */
  public static final MemoryObserver INSTANCE = new MemoryObserver();

  static {
    Runtime.getRuntime().addShutdownHook(
        new Thread("shutdown-hook-memory-observation") {
          @Override
          public void run() {
            // SimSystem.report (Level.INFO, "done");
            MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
            NotificationEmitter emitter = (NotificationEmitter) mbean;
            try {
              emitter.removeNotificationListener(INSTANCE.listener);
            } catch (ListenerNotFoundException e) {
              SimSystem.report(e);
            }
          }
        });
  }

  /**
   * Hidden constructor. The class is a classical singleton as we need only one
   * instance per JVM instance.
   */
  private MemoryObserver() {
    MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
    NotificationEmitter emitter = (NotificationEmitter) mbean;
    listener = new MemoryNotificationListener();
    emitter.addNotificationListener(listener, null, null);
    List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
    // activate threshhold for all those of interest
    for (MemoryPoolMXBean pool : pools) {
      // if (pool.getType() == MemoryType.HEAP) {
      if (pool.isCollectionUsageThresholdSupported()) {
        // if (pool.getName().toUpperCase().contains("EDEN")) {
        // System.out.println("Setting the threshold for " + pool.getName());
        pool.setCollectionUsageThreshold(DEFAULTTHRESHOLD); // in bytes!!!
        // }
      }
      // }
    }
  }

  /**
   * Informs all registered listeners that we are running low on memory.
   */
  private synchronized void inform() {

    // clean up
    Reference<? extends ILowMemoryListener> ref = listenersToBeRemoved.poll();
    while (ref != null) {
      listeners.remove(ref);
      ref = listenersToBeRemoved.poll();
    }
    // now we only have those left we have to work on
    for (WeakReference<ILowMemoryListener> l : listeners) {
      ILowMemoryListener listener = l.get();
      if (listener != null) {
        listener.lowMemory();
      }
    }
  }

  /**
   * Register a listener for a low of memory event. Please note: every listener
   * registered should be unregistered as soon as possible.
   * 
   * @param listener
   */
  public synchronized void register(ILowMemoryListener listener) {
    listeners.add(new WeakReference<>(listener, listenersToBeRemoved));
  }

  /**
   * Unregister the listener given. The listener will no longer be informed
   * about low memory.
   * 
   * @param listener
   */
  public synchronized void unregister(ILowMemoryListener listener) {
    Iterator<WeakReference<ILowMemoryListener>> it = listeners.iterator();
    while (it.hasNext()) {
      if (it.next().get() == listener) {
        it.remove();
        return;
      }
    }
  }

  /**
   * Simple helper listener class.
   * 
   * @author Jan Himmelspach
   * 
   */
  class MemoryNotificationListener implements
      javax.management.NotificationListener {
    @Override
    public void handleNotification(Notification notification, Object handback) {
      String notificationType = notification.getType();
      if (notificationType
          .equals(MemoryNotificationInfo.MEMORY_COLLECTION_THRESHOLD_EXCEEDED)) {
        inform();
      }
    }
  }

}
