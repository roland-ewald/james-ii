/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.id;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.jamesii.core.util.info.JavaInfo;

/**
 * Generator to create unique IDs.
 * 
 * A unique id is composed from the mac address of the machine this VM is
 * running on, the start time of the JVM, the current time in milliseconds, and
 * a counter to resolve multiple requests per millisecond. The resulting id is
 * of the form "macAddress" "started at" "time in millis" "-counterValue".
 * 
 * This might still fail if we have more than one VM started on a single machine
 * in a second.
 * 
 * 
 * @author Stefan Leye
 * 
 */
public final class UniqueIDGenerator {

  /**
   * Reference to singleton instance.
   */
  private static final UniqueIDGenerator INSTANCE = new UniqueIDGenerator();

  /**
   * Counter creates a new value for each ID.
   */
  private static AtomicLong counter = new AtomicLong();

  /** The mac address (cache of). */
  private List<byte[]> macAddresses = null;

  /** The JVM had been started at. */
  private Long startedAt = null;

  /**
   * Constructor.
   */
  private UniqueIDGenerator() {
    JavaInfo info = new JavaInfo();
    startedAt = info.getSessionStarted().getTime();
    macAddresses = info.getMacAddresses();
  }

  /**
   * Get the instance of the generator.
   * 
   * @return the ID generator
   */
  private static UniqueIDGenerator getInstance() {
    return INSTANCE;
  }

  /**
   * Returns a (hopefully) unique ID as a String.
   * 
   * @return the ID
   */
  public static synchronized IUniqueID createUniqueID() {
    return getInstance().createID();
  }

  /**
   * Create a unique ID. The String comprises the local mac-adress, the current
   * time and the value of the counter.
   * 
   * @return the ID as String.
   */
  public IUniqueID createID() {
    return new UniqueID(macAddresses.size() > 0 ? macAddresses.get(0)
        : new byte[] { 0 }, startedAt, System.nanoTime(),
        counter.incrementAndGet());
  }
}
