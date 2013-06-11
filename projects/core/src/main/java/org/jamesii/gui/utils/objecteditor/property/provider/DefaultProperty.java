/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.provider;

import java.lang.reflect.Method;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.objecteditor.property.AbstractProperty;
import org.jamesii.gui.utils.objecteditor.property.IProperty;

/**
 * The default implementation of the {@link IProperty} interface used by
 * {@link DefaultPropertyProvider} to provide bean properties.
 * 
 * @author Stefan Rybacki
 */
class DefaultProperty extends AbstractProperty {

  /**
   * The write method.
   */
  private Method writeMethod;

  /**
   * The read method.
   */
  private Method readMethod;

  /**
   * The writable.
   */
  private boolean writable;

  /**
   * Instantiates a new default property.
   * 
   * @param name
   *          the name
   * @param type
   *          the type
   * @param readMethod
   *          the read method
   * @param writeMethod
   *          the write method
   * @param writable
   *          the writable
   */
  public DefaultProperty(String name, Class<?> type, Method readMethod,
      Method writeMethod, boolean writable) {
    super(name, type);
    this.writeMethod = writeMethod;
    this.readMethod = readMethod;
    this.writable = writable;
  }

  @Override
  public Object getValue(Object parent) {
    try {
      return readMethod.invoke(parent, new Object[] {});
    } catch (Exception e) {
      SimSystem.report(e);
    }

    return null;
  }

  @Override
  public void setValue(Object parent, Object value) {
    if (!writable) {
      return;
    }
    try {
      writeMethod.invoke(parent, value);
    } catch (Exception e) {
      SimSystem.report(e);
    }
    return;
  }

  @Override
  public boolean isWritable() {
    return writable;
  }
}
