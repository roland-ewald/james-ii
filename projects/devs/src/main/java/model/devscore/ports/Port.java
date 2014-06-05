/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.ports;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.NamedEntity;

/**
 * A port class is used by any DEVS model. All communication between models is
 * done through ports - each port can thereby handle any number of incoming or
 * outgoing messages.
 * 
 * @author Jan Himmelspach
 */
public class Port extends NamedEntity implements IPort {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -7518539098203384880L;

  /**
   * Internally used counter This counter speeds up the check whether this port
   * holds data or not Assumptions: counter is not thread safe (not
   * synchronized), i.e. read and write operations cannot be executed
   * concurrently!!!
   */
  private int c = 0;

  /**
   * The valueClass is a class pointer containing the object class which can be
   * communicated by using this port - this class here is the most general port
   * - however it is quite easy to implement and use more specialized ones.
   */
  private Class<?> valueClass;

  /**
   * List of objects which are communicated through this port. The initial size
   * is set to 1. Thus memory consumption is low in the beginning, it will
   * automatically be adapted if needed later on.
   */
  private List<Object> values = new ArrayList<>(1);

  /**
   * Instantiates a new port.
   * 
   * @param name
   *          the name
   * @param valueClass
   *          the value class
   */
  public Port(String name, Class<?> valueClass) {
    super(name);
    this.valueClass = valueClass;
  }

  @Override
  public final void clear() {
    if (c == 0) {
      return;
    }
    values = new ArrayList<>();
    c = 0;
    // changed();
  }

  /**
   * Gets the value class. Returns the class of the objects which can be
   * transferred through this port.
   * 
   * @return the value class
   */
  public Class<?> getValueClass() {
    return valueClass;
  }

  @Override
  public final int getValuesCount() {
    return c;
  }

  @Override
  public final boolean hasValue() {
    return (c > 0);
  }

  @Override
  public final Object read() {
    if (!hasValue()) {
      return null;
    }
    Object temp = values.get(0);
    values.remove(0);
    --c;
    changed();
    return temp;
  }

  @Override
  public final Object read(int index) {
    return values.get(index);
  }

  @Override
  public final List<Object> readAll() {
    return values;
  }

  @Override
  @SuppressWarnings("all")
  public final <M> M readAs() {
    return (M) read();
  }

  @Override
  public final void write(Object o) {
    // System.out.println ("Writing on port "+getName()+"
    // "+o.getClass().getName());
    if (o.getClass() != valueClass) {
      throw new PortTypeMismatchException("Writing on port " + getName()
          + " with a wrong type " + o.getClass().getName() + " instead of "
          + valueClass.getName());
    }
    values.add(o);
    ++c;
    changed();
  }

  @Override
  public final void writeAll(List<Object> o) {
    // System.out.println ("Writing on port "+getName()+"
    // "+o.getClass().getName());

    /*
     * if (o.getClass().getC .getClass() != valueClass) { throw new
     * PortTypeMismatchException("Writing on port " + getName() + " with a wrong
     * type " + o.getClass().getName() + " instead of " + valueClass.getName());
     * }
     */
    values.addAll(o);
    c += o.size();
    changed();
  }

}
