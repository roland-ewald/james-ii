/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.ports;


import java.util.List;

import org.jamesii.core.base.INamedEntity;

/**
 * The Interface IPort.
 * 
 * @author Jan Himmelspach
 * @version 1.0
 */
public interface IPort extends INamedEntity {

  /**
   * Removes all objects from the port. This method is not thread safe, and it
   * is not synchronized with the read/write operations!
   */
  void clear();

  /**
   * The number of objects (messages, events) which are written, but not already
   * read, on this port.
   * 
   * @return number of pending objects
   */
  int getValuesCount();

  /**
   * Is there anything on this port?
   * 
   * @return true if there are values on this port
   */
  boolean hasValue();

  /**
   * Reads an object from a port and removes the object from the internal list.
   * This method is not thread safe! And it is not synchronized with the write
   * operation!!
   * 
   * @return the first object stored in the port
   * 
   */
  Object read();

  /**
   * Reads the object at position index from the list and returns it. In
   * contrast to the "simple" {@link #read()} this method does not remove the
   * read object!!
   * 
   * @param index
   *          the index
   * 
   * @return the object stored at the given index
   */
  Object read(int index);

  /**
   * Reads list and returns a pointer to it. In contrast to the "simple"
   * {@link #read()} this method does not remove the object read. Thus
   * {@link #hasValue()} returns true afterwards.
   * 
   * @return all objects stored at this port
   */
  List<Object> readAll();

  /**
   * Reads the object from the port and thereby directly converts it to type M.
   * 
   * Reads an object from a port and removes the object from the internal list
   * (and thus can be based on the {@link #read()} method). This method is not
   * thread safe! And it is not synchronized with the write operation!!
   * 
   * @param <M>
   *          the type the value read shall be converted to.,
   * @return the first object stored in the port as M
   */
  <M> M readAs();

  /**
   * Write the object to the port. This method is not thread safe! And it is not
   * synchronized with the read operation!!
   * 
   * @param o
   *          Object to be written
   * @throws PortTypeMismatchException
   */
  void write(Object o);

  /**
   * Write all passed objects to the port.
   * 
   * @param o
   *          the o
   */
  void writeAll(List<Object> o);

}
