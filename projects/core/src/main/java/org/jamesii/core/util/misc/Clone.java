/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.IOException;
import java.io.Serializable;

import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Simple utility class for cloning. E.g., to avoid type 'unsafe type' warnings
 * due to cloning generic objects, this utility should be enhanced if a new
 * cloning method for a certain type - e. g., ArrayList - is needed.
 * 
 * Date: 11:56:44 01.05.2005
 * 
 * @author Roland Ewald
 */

public final class Clone {

  /**
   * Hidden constructor.
   */
  private Clone() {
  }

  /**
   * Clones a serialisable object by serialising it and then de-serialising it
   * again. Not beautiful, but effective :)
   * 
   * @param object
   *          the object to be cloned
   * @param <D>
   *          the type of the object to be cloned
   * @return the clone
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws ClassNotFoundException
   *           the class not found exception
   */
  @SuppressWarnings("unchecked")
  public static <D extends Serializable> D cloneSerializable(D object)
      throws IOException, ClassNotFoundException {
    return (D) SerialisationUtils.deserialize(SerialisationUtils
        .serialize(object));
  }

  /**
   * Risky version of {@link Clone#cloneSerializable(Serializable)}, throws
   * {@link RuntimeException} if something happens.
   * 
   * @param object
   *          the object to be cloned
   * @param <D>
   *          type of the object to be cloned
   * @return cloned object
   */
  public static <D extends Serializable> D riskyCloneSerializable(D object) {
    D result = null;
    try {
      result = cloneSerializable(object);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return result;
  }

}