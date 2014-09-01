/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.history;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.jamesii.core.serialization.IEncoderCompatible;
import org.jamesii.core.serialization.XMLEncoderFactory;
import org.jamesii.core.util.Reflect;

/**
 * The HistoryItem contains a certain value, the time of the last usage and a
 * counter counting the usages. Two {@link HistoryItem}s are considered equally
 * if their value, counter and lastUsage is equal.
 * 
 * @author Enrico Seib
 */
final class HistoryItem implements IEncoderCompatible {
  static {
    PersistenceDelegate delegate = new DefaultPersistenceDelegate() {
      @Override
      protected Expression instantiate(Object oldInstance, Encoder out) {
        HistoryItem item = (HistoryItem) oldInstance;

        return new Expression(oldInstance, Reflect.class, "instantiate",
            new Object[] {
                HistoryItem.class,
                new Object[] { item.getValue(), item.getCounter(),
                    item.getLastUsage() } });
      }

      @Override
      protected void initialize(Class<?> type, Object oldInstance,
          Object newInstance, Encoder out) {
      }
    };

    XMLEncoderFactory.registerDelegate(HistoryItem.class, delegate);
  }

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7906501351754569567L;

  /**
   * path where the file is located
   */
  private String value;

  /**
   * counts how often a value is used
   */
  private AtomicInteger counter;

  /**
   * Date of last usage
   */
  private Date lastUsage;

  /**
   * Constructor with given value
   * 
   * @param value
   *          value
   */
  public HistoryItem(String value) {
    this(value, 1, new Date(System.currentTimeMillis()));
  }

  /**
   * Default constructor.
   * 
   * @param value
   *          the value
   * @param counter
   *          the counter
   * @param lastUsage
   *          the last usage
   */
  private HistoryItem(String value, Integer counter, Date lastUsage) {
    this.value = value;
    this.counter = new AtomicInteger(counter);
    this.lastUsage = lastUsage;
  }

  /**
   * Increments the counter by 1
   */
  public void incrementCounter() {
    counter.incrementAndGet();
  }

  /**
   * Returns the number of usages of this HistoryItem
   * 
   * @return Counts how often a file is/was accessed the past
   */
  public int getCounter() {
    return counter.get();
  }

  /**
   * Returns the value.
   * 
   * @return value
   */
  public String getValue() {
    return value;
  }

  /**
   * Returns the last usage of the item.
   * 
   * @return Date of last usage of the item
   */
  public Date getLastUsage() {
    return new Date(this.lastUsage.getTime());
  }

  /**
   * Updates last Usage of the item by the given date.
   * 
   * @param date
   */
  public void setLastUsage(Date date) {
    lastUsage = new Date(date.getTime());
  }

  /**
   * Updates the item (increment counter, update last usage)
   */
  public void update() {
    this.incrementCounter();
    this.lastUsage.setTime(System.currentTimeMillis());
  }

  /**
   * Sets counter of the HistoryItem
   * 
   * @param c
   *          new counter
   */
  public void setCounter(int c) {
    this.counter.set(c);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof HistoryItem)) {
      return false;
    }

    HistoryItem other = (HistoryItem) obj;

    if (other == this) {
      return true;
    }

    return other.getValue().equals(value)
        && other.getCounter() == counter.get()
        && other.getLastUsage().equals(lastUsage);
  }

  @Override
  public int hashCode() {
    int hashCode = 31 * getClass().hashCode() + value.hashCode();
    hashCode = 31 * hashCode + counter.get();
    hashCode = 31 * hashCode + (int) lastUsage.getTime();
    return hashCode;
  }
}
