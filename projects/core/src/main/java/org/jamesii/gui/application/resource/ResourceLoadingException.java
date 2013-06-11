/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Exception that is thrown if a resource could not been loaded. It encapsulates
 * the original thrown message while trying to load the resource.
 * 
 * @author Stefan Rybacki
 */
public class ResourceLoadingException extends Exception {

  /**
   * The Class SerializationProxy.
   */
  private static class SerializationProxy implements Serializable {

    /** Serialization ID. */
    private static final long serialVersionUID = -9160228107120694127L;

    /**
     * The exception message.
     */
    private final String msg;

    /**
     * The original {@link Throwable}.
     */
    private final Throwable original;

    /**
     * Creates a new serialization proxy for {@link ResourceLoadingException}
     * 
     * @param e
     *          the exception to serialize
     */
    public SerializationProxy(ResourceLoadingException e) {
      msg = e.getMessage();
      original = e.original;
    }

    /**
     * Helper method that restores a serialized exception
     * 
     * @return the restored exception
     */
    private Object readResolve() {
      return new ResourceLoadingException(msg, original);
    }
  }

  /** Serialization ID. */
  private static final long serialVersionUID = -2335910353207512540L;

  /**
   * The original {@link Throwable}
   */
  private Throwable original;

  /**
   * Creates a new exception indicating that a resource could not been loaded.
   * 
   * @param msg
   *          the message to show
   * @param originalThrowable
   *          the original occurred exception while loading the resource
   */
  public ResourceLoadingException(String msg, Throwable originalThrowable) {
    super(msg);
    original = originalThrowable;
  }

  /**
   * @return the originally thrown exception
   */
  public Throwable getOriginalThrowable() {
    return original;
  }

  /**
   * A serialization method.
   * 
   * @return The object to serialize.
   */
  private Object writeReplace() {
    return new SerializationProxy(this);
  }

  /**
   * A deserialization method.
   * 
   * @param stream
   *          The stream to deserialize from.
   * @throws InvalidObjectException
   *           Is thrown when the object to deserialize is not compatible
   */
  private void readObject(ObjectInputStream stream)
      throws InvalidObjectException {
    throw new InvalidObjectException("Proxy needed!");
  }

  @Override
  public StackTraceElement[] getStackTrace() {
    StackTraceElement[] o = original.getStackTrace();
    StackTraceElement[] t = super.getStackTrace();

    StackTraceElement[] stackTrace = new StackTraceElement[o.length + t.length];

    int l = 0;

    for (StackTraceElement e : t) {
      stackTrace[l] = e;
      l++;
    }

    for (StackTraceElement e : o) {
      stackTrace[l] = e;
      l++;
    }

    return stackTrace;
  }

}
