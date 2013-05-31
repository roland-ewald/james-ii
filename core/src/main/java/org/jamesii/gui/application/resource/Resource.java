/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.lang.ref.SoftReference;
import java.util.concurrent.atomic.AtomicLong;

// TODO: Auto-generated Javadoc
/**
 * Stores an object using a soft reference. It also keeps the {@link #lastUsage}
 * flag up to date whenever {@link #getResource()} is called.
 * 
 * @author Stefan Rybacki
 */
public class Resource implements Comparable<Resource> {

  /** counts the invocations of new resource plus getObject. */
  private static AtomicLong resourceCount = new AtomicLong(0);

  /** SoftReference holding the actual resource object. */
  private SoftReference<Object> object;

  /** flag keeping track on access times. */
  private long lastUsage;

  /** the resource URL to identify this resource. */
  private String id;

  /**
   * Creates a new {@link Resource} from {@code res}.
   * 
   * @param res
   *          the resource
   * @param resourceURL
   *          the resource url
   */
  public Resource(String resourceURL, Object res) {
    object = new SoftReference<>(res);
    lastUsage = resourceCount.incrementAndGet();
    id = resourceURL;
  }

  /**
   * Gets the resource.
   * 
   * @return the resource
   */
  public final Object getResource() {
    lastUsage = resourceCount.incrementAndGet();

    Object o = object.get();

    return o;
  }

  /**
   * Gets the last usage.
   * 
   * @return the position in cache the resource was added or lastly used if the
   *         cache is full the resources with the least lastUsage value are
   *         deleted
   */
  public final long getLastUsage() {
    return lastUsage;
  }

  @Override
  public String toString() {
    return id + " last Usage: " + lastUsage;
  }

  @Override
  public int compareTo(Resource o) {
    if (o.equals(this) || (o.isNull() && isNull())) {
      return 0;
    }
    if (o.isNull() && !isNull() || (o.getLastUsage() < lastUsage)) {
      return 1;
    }
    if (!o.isNull() && isNull() || (o.getLastUsage() > lastUsage)) {
      return -1;
    }

    return 0;
  }

  /**
   * Gets the id.
   * 
   * @return the id of this resource (equals the resource url)
   */
  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Resource) {
      return ((Resource) obj).getId().compareTo(id) == 0;
    }

    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  /**
   * Method used to check the resource for null without using
   * {@link #getResource()} and therefore without updating {@link #lastUsage}.
   * 
   * @return true, if checks if is null
   */
  public boolean isNull() {
    return object.get() == null;
  }

}
