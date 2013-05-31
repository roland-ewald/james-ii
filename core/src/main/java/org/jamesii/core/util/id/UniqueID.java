/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.id;

import java.util.Arrays;

import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.IEncoderCompatible;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * The Class UniqueID. A UniqueID is an ID which can be used to identify objects
 * across the borders of a single VM.
 * 
 * It contains the mac address to make the ids unique per machine and it
 * contains the startup time to make it unique per JVM - which works as long not
 * two JVMs are stated at the same time.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 */
public class UniqueID implements IUniqueID, IEncoderCompatible {
  static {
    SerialisationUtils.addDelegateForConstructor(UniqueID.class,
        new IConstructorParameterProvider<UniqueID>() {
          @Override
          public Object[] getParameters(UniqueID id) {
            return new Object[] { id.getMacAddress(), id.getStartedAt(),
                id.getTime(), id.getCount() };
          }
        });
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7207240211262402989L;

  /** Counter creates a new value for each ID. */
  private final long count;

  /** The mac address (cache of). */
  private final byte[] macAddress;

  /** The JVM had been started at. */
  private final long startedAt;

  /** The time. */
  private final long time;

  /** The string representation. */
  private final String stringRepresentation;

  /**
   * Instantiates a new unique id.
   * 
   * @param mac
   *          the mac address to be used to identify the machine the id is
   *          generated on
   * @param startedAt
   *          the time the JVM instance the unique ID is generated within has
   *          been started
   * @param time
   *          the current time
   * @param count
   *          the counter (to differ the id if the before given attributes are
   *          identical)
   */
  UniqueID(byte[] mac, Long startedAt, Long time, Long count) {
    super();
    macAddress = new byte[mac.length];
    System.arraycopy(mac, 0, macAddress, 0, mac.length);
    this.startedAt = startedAt;
    this.time = time;
    this.count = count;

    StringBuilder result = new StringBuilder();
    // unique for each machine
    for (int i = 0; i < mac.length; i++) {
      result.append(Integer.toHexString(mac[i]));
    }
    result.append('-');
    // add the time the jvm had been started at
    result.append(startedAt + "-");
    // constantly increasing number
    result.append(time);
    // if more than one number per nano second the counter will help
    result.append("-" + count);

    stringRepresentation = result.toString();
  }

  @Override
  public String asString() {
    return stringRepresentation;
  }

  @Override
  public String toString() {
    return asString();
  }

  /**
   * Gets the count.
   * 
   * @return the count
   */
  public long getCount() {
    return count;
  }

  /**
   * Gets the mac address.
   * 
   * @return the mac address
   */
  public byte[] getMacAddress() {
    return Arrays.copyOf(macAddress, macAddress.length);
  }

  /**
   * Gets the started at.
   * 
   * @return the started at
   */
  public long getStartedAt() {
    return startedAt;
  }

  /**
   * Gets the time.
   * 
   * @return the time
   */
  public long getTime() {
    return time;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof UniqueID) {
      UniqueID uid = (UniqueID) obj;
      return time == uid.time && startedAt == uid.startedAt
          && count == uid.count && checkBytes(macAddress, uid.macAddress);
    }
    return false;
  }

  /**
   * Check if two byte arrays are equivalent.
   * 
   * @param array1
   *          the mac address2
   * @param array2
   *          the mac address3
   * @return true, if successful
   */
  private boolean checkBytes(byte[] array1, byte[] array2) {
    if (array1.length != array2.length) {
      return false;
    }
    for (int i = 0; i < array1.length; i++) {
      if (array1[i] != array2[i]) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return stringRepresentation.hashCode();
  }

  @Override
  public int compareTo(IUniqueID o) {
    return this.stringRepresentation.compareTo(o.asString());
  }

}
