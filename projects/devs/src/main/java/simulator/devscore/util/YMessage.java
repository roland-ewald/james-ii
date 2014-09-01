/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devscore.util;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.messages.Message;

/**
 * In this DEVS implementation there is no distinction between x and y messages
 * as it is made in DEVS. So the y-message is used for sending input and output
 * messages.
 * 
 * @author Jan Himmelspach
 */
public class YMessage extends Message<IProcessor> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -7133967587670570453L;

  /** The time. */
  private double time;

  /** Map containing port names and associated values list. */
  private Map<String, List<Object>> values;

  /**
   * Creates a new YMessage.
   * 
   * @param sender
   *          (sending processor) of this message
   * @param values
   *          the values
   */
  public YMessage(IProcessor sender, Map<String, List<Object>> values) {
    super(sender);
    this.values = values;
  }

  /**
   * Add a value to the message.
   * 
   * @param name
   *          name of the port the values shall be added to
   * @param o
   *          list of values which should be added to the message
   */
  public void addValue(String name, List<Object> o) {
    if (values.containsKey(name)) {
      values.get(name).addAll(o);
    } else {
      values.put(name, o);
    }
  }

  /**
   * Adds the specified parcels to this <tt>y</tt>-message. A parcel is thereby
   * composed of a name and a list of values associated with that name. The
   * method guarantees that no values get lost while adding them to the message.
   * 
   * @see #addValue(String, List)
   * @param parcels
   *          a map containing the parcels that shall be added to this
   *          <tt>y</tt>-message:
   *          <ul>
   *          <li>key -> name</li>
   *          <li>value -> list of values associated with the corresponding name
   *          </li>
   *          </ul>
   */
  public final void addValues(Map<String, List<Object>> parcels) {
    // iterate all parcels
    for (Entry<String, List<Object>> parcel : parcels.entrySet()) {
      addValue(parcel.getKey(), parcel.getValue());
    }
  }

  /**
   * Returns the time of this y message.
   * 
   * @return time value of this message
   */
  public double getTime() {
    return this.time;
  }

  /**
   * Returns an Iterator for the values of this message.
   * 
   * @return iterator for the values of this message
   */
  public Iterator<String> getValueIterator() {
    return values.keySet().iterator();
  }

  /**
   * Returns a hashmap with the values of this y message.
   * 
   * @return a reference to the hashmap of ports and values of this y message
   */
  public Map<String, List<Object>> getValues() {
    return values;
  }

  /**
   * Returns true if the message is not empty.
   * 
   * @return true if y-Message not empty
   */
  public boolean hasValues() {
    return ((values != null) && (values.size() > 0));
  }

  /**
   * Set the time of the y message.
   * 
   * @param time
   *          of the message
   */
  public void setTime(double time) {
    this.time = time;
  }

  /**
   * Set the content of this message.
   * 
   * @param values
   *          the values
   */
  public void setValue(Map<String, List<Object>> values) {
    this.values = values;
  }

}
