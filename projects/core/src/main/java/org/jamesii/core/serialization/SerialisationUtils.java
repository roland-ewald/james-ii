/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.serialization;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jamesii.core.util.Reflect;
import org.jamesii.core.util.misc.Base64;

/**
 * 
 * Class providing static utility methods for serialization.
 * 
 * @author Roland Ewald
 * 
 */
public final class SerialisationUtils {

  /**
   * Hidden constructor.
   */
  private SerialisationUtils() {
  }

  /**
   * Serializes an object to a number of bytes.
   * 
   * @param o
   *          the object
   * @return a byte array
   * @throws IOException
   *           if serialization went wrong
   */
  public static byte[] serialize(Serializable o) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(o);
    oos.flush();
    return baos.toByteArray();
  }

  /**
   * Serialises an object to a base64 string.
   * 
   * @param o
   *          the object
   * @return the string
   * @throws IOException
   *           when object cannot be encoded
   */
  public static String serializeToB64String(Serializable o) throws IOException {
    byte[] object = serialize(o);
    return Base64.encode(object);
  }

  /**
   * De-serializes an object from a number of bytes.
   * 
   * @param b
   *          the byte array
   * @return the object
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  public static Object deserialize(byte[] b) throws IOException,
      ClassNotFoundException {
    ByteArrayInputStream bais = new ByteArrayInputStream(b);
    ObjectInputStream ois = new ObjectInputStream(bais);
    return ois.readObject();
  }

  /**
   * De-serializes an object from a Base64-decoded string.
   * 
   * @param string
   *          the string
   * @return the de-serialized object
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  public static Object deserializeFromB64String(String string)
      throws IOException, ClassNotFoundException {
    return deserialize(Base64.decode(string));
  }

  /**
   * Adds a {@link PersistenceDelegate} that uses the {@link ObjectManipulator}
   * to instantiate the object by a constructor with limited visibility.
   * 
   * @param classToBePersisted
   *          the class to be persisted
   * @param parameterProvider
   *          the parameter provider
   */
  public static <C> void addDelegateForConstructor(
      Class<? super C> classToBePersisted,
      final IConstructorParameterProvider<C> parameterProvider) {

    PersistenceDelegate delegate = new DefaultPersistenceDelegate() {

      @Override
      @SuppressWarnings("unchecked")
      // no way of checking it here
      protected Expression instantiate(Object oldInstance, Encoder out) {
        try {
          C instance = (C) oldInstance;
          return new Expression(oldInstance, Reflect.class, "instantiate",
              new Object[] { instance.getClass(),
                  parameterProvider.getParameters(instance) });
        } catch (SecurityException e) {
          throw new RuntimeException(e);
        }
      }
    };

    XMLEncoderFactory.registerDelegate(classToBePersisted, delegate);
  }

}
