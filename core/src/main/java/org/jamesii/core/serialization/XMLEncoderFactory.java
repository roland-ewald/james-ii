/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.serialization;

import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.PersistenceDelegate;
import java.beans.XMLEncoder;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.SimSystem;
import org.jamesii.gui.base.WeakArrayList;

/**
 * Static factory class that creates {@link XMLEncoder} instances for a given
 * {@link OutputStream} setting registered custom {@link PersistenceDelegate}s
 * for specific classes. This way there is a centralized way of registering
 * delegates for individual classes rather than registering all delegates for
 * any {@link XMLEncoder} created. As long as this factory is used for the
 * creation of {@link XMLEncoder} instances there is no further need of
 * delegates registering besides once at this factory.
 * <p/>
 * For a tutorial on how to write custom {@link PersistenceDelegate}s refer to
 * <a href= "http://java.sun.com/products/jfc/tsc/articles/persistence4/"
 * >http://java.sun.com/products/jfc/tsc/articles/persistence4/</a>
 * 
 * @author Stefan Rybacki
 */
public final class XMLEncoderFactory {

  /**
   * Hidden constructor.
   */
  private XMLEncoderFactory() {
  }

  /**
   * A map storing class delegates mappings.
   */
  private static final Map<Class<?>, PersistenceDelegate> DELEGATES =
      new HashMap<>();

  /**
   * A weak list of all generated encoders so that generated encoders also
   * benefit of delegates registered after they were created.
   */
  private static final List<Encoder> ENCODERS = new WeakArrayList<>();

  /**
   * Registers a specific delegate for a specific class. Be aware that
   * registrations that occur after an {@link XMLEncoder} is already created via
   * {@link #createXMLEncoder(OutputStream)} also affects this encoder. If this
   * not your intention use {@link #createXMLEncoder(OutputStream, boolean)}.
   * 
   * @param forClass
   *          the class the delegate is registered for
   * @param delegate
   *          the delegate to register
   */
  public static synchronized void registerDelegate(Class<?> forClass,
      PersistenceDelegate delegate) {
    if (delegate == null) {
      DELEGATES.remove(forClass);
    } else {
      DELEGATES.put(forClass, delegate);
    }

    // for all created xml encoders set delegate
    for (Encoder e : ENCODERS) {
      if (e != null) {
        e.setPersistenceDelegate(forClass, delegate);
      }
    }
  }

  /**
   * Creates a new XMLEncoder instance with the option that all delegates
   * registered at this factory afterwards are also registered for the created
   * encoder. If this is not your intention then use
   * {@link #createXMLEncoder(OutputStream, boolean)}.
   * 
   * @param out
   *          the output stream the encoder should write to
   * @return the created XML encoder with all registered delegates set
   */
  public static XMLEncoder createXMLEncoder(OutputStream out) {
    return createXMLEncoder(out, true);
  }

  /**
   * Creates a new XMLEncoder instance.
   * 
   * @param out
   *          the output stream the encoder should write to
   * @param keepUpdated
   *          if <code>true</code> created encoders will still be notified if a
   *          new delegate is registered after their creation (
   *          <code>false</code> only works on Java 7 and up)
   * @return the created XML encoder with all registered delegates set
   */
  public static synchronized XMLEncoder createXMLEncoder(OutputStream out,
      boolean keepUpdated) {
    // TODO sr137: this is a workaround, usually the xmlencoder should have a
    // parameter for the classloader to use
    Thread.currentThread().setContextClassLoader(
        SimSystem.getRegistry().getClassLoader());

    XMLEncoder xmlEncoder = new XMLEncoder(out);

    for (Entry<Class<?>, PersistenceDelegate> entry : DELEGATES.entrySet()) {
      xmlEncoder.setPersistenceDelegate(entry.getKey(), entry.getValue());
    }

    xmlEncoder.setExceptionListener(new ExceptionListener() {

      @Override
      public void exceptionThrown(Exception e) {
        SimSystem.report(e);
      }

    });

    if (keepUpdated) {
      ENCODERS.add(xmlEncoder);
    }

    return xmlEncoder;
  }

}
