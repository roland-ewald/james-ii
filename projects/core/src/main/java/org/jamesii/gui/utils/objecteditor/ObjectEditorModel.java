/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.ListenerSupport;
import org.jamesii.gui.utils.objecteditor.property.IProperty;
import org.jamesii.gui.utils.objecteditor.property.IPropertyFilter;
import org.jamesii.gui.utils.objecteditor.property.provider.DefaultPropertyProvider;
import org.jamesii.gui.utils.objecteditor.property.provider.IPropertyProvider;
import org.jamesii.gui.utils.objecteditor.property.provider.ListPropertyProvider;
import org.jamesii.gui.utils.treetable.AbstractTreeTableModel;

/**
 * This class is for internal use only. It represents the
 * {@link org.jamesii.gui.utils.treetable.ITreeTableModel} which is used by the
 * {@link ObjectEditorComponent} to provide access to properties.
 * 
 * @author Stefan Rybacki
 */
final class ObjectEditorModel extends AbstractTreeTableModel {

  /**
   * The value which's properties are to be edited.
   */
  private Object value;

  /**
   * The registered property providers mapping.
   */
  private final Map<Class<?>, IPropertyProvider> propertyProviders =
      new HashMap<>();
  {
    // put in list property provider by default
    propertyProviders.put(List.class, new ListPropertyProvider());
  }

  /**
   * The registered property filters.
   */
  private final List<IPropertyFilter> propertyFilters = new ArrayList<>();

  /**
   * The registered property changed listeners. (Don't confuse it with
   * PropertyChangeListener)
   */
  private final ListenerSupport<IPropertyChangedListener> listeners =
      new ListenerSupport<>();

  /**
   * The name of column 1.
   */
  private String column1;

  /**
   * The name of column 2.
   */
  private String column2;

  /**
   * Instantiates a new object editor model.
   * 
   * @param toEdit
   *          the object to edit properties for
   */
  public ObjectEditorModel(Object toEdit) {
    this(toEdit, null, null);
  }

  /**
   * Instantiates a new object editor model.
   * 
   * @param toEdit
   *          the object to edit properties for
   * @param column1
   *          the column1 the name of column 1
   * @param column2
   *          the column2 the name of column 2
   */
  public ObjectEditorModel(Object toEdit, String column1, String column2) {
    value = toEdit;
    this.column1 = column1 != null ? column1 : "Property";
    this.column2 = column2 != null ? column2 : "Value";
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  @Override
  public String getColumnName(int columnIndex) {
    switch (columnIndex) {
    case 0:
      return column1;
    case 1:
      return column2;
    }
    return null;
  }

  @Override
  public Object getValueAt(Object node, int column) {
    PropertyHelper p = (PropertyHelper) node;

    switch (column) {
    case 0:
      return p.getDescriptor().getName();
    case 1:
      return p.isOmmited() ? "#######" : p.getValue();
    }
    return null;
  }

  @Override
  public Object getChild(Object parent, int index) {
    if (parent == null) {
      return null;
    }

    Object v = parent;

    Class<?> parentClass;
    if (parent instanceof PropertyHelper) {
      parentClass = ((PropertyHelper) parent).getDescriptor().getType();
      v = ((PropertyHelper) parent).getValue();
      if (v != null) {
        parentClass = v.getClass();
      }
    } else {
      parentClass = parent.getClass();
    }

    List<IProperty> list = checkClass(parentClass, v);

    if (list == null) {
      return null;
    }
    try {
      IProperty descriptor = list.get(index);

      PropertyHelper p =
          new PropertyHelper(v == null ? null : descriptor.getValue(v),
              descriptor, parent);

      // also sort out properties that could cause loops in the tree
      // introduce special property flag saying Ommited which can have
      // no
      // children and is not editable
      PropertyHelper op = p;
      if (op.getValue() == null) {
        return op;
      }

      while (p.getParent() instanceof PropertyHelper) {
        p = (PropertyHelper) p.getParent();
        if (op.getValue().equals(p.getValue())) {
          op.setOmmited(true);
          return op;
        }
      }
      return op;
    } catch (Exception e) {
      SimSystem.report(e);
    }
    return null;
  }

  @Override
  public int getChildCount(Object parent) {
    if (parent == null) {
      return 0;
    }

    Object v = parent;

    Class<?> parentClass;
    if (parent instanceof PropertyHelper) {
      parentClass = ((PropertyHelper) parent).getDescriptor().getType();
      v = ((PropertyHelper) parent).getValue();
      if (v != null) {
        parentClass = ((PropertyHelper) parent).getValue().getClass();
      }
    } else {
      parentClass = parent.getClass();
    }

    List<IProperty> checkClass = checkClass(parentClass, v);

    return checkClass == null ? 0 : checkClass.size();
  }

  /**
   * Helper method that checks a class for available property providers. It
   * determines the properties for the given parentClass and applies registered
   * {@link IPropertyFilter}s and returns the left properties.
   * 
   * @param parentClass
   *          the parent class properties are to determine for
   * @param v
   *          the value the actual value of parent class
   * @return the determined properties
   */
  private List<IProperty> checkClass(Class<?> parentClass, Object v) {
    try {
      IPropertyProvider provider = getPropertyProvider(parentClass);

      List<IProperty> result = new ArrayList<>();
      List<IProperty> descriptors = provider.getPropertiesFor(parentClass, v);
      if (descriptors == null) {
        return result;
      }
      for (IProperty pd : descriptors) {
        // check property filters also
        boolean in = true;
        for (IPropertyFilter f : propertyFilters) {
          in &= f.isPropertyVisible(parentClass, pd.getName(), pd.getType());
        }
        if (in) {
          result.add(pd);
        }

      }

      return result;
    } catch (Exception e) {
      SimSystem.report(e);
    }

    return null;
  }

  /**
   * Gets the property provider. This is a helper method that determines the
   * property provider to use for a specific parentClass, this is done by
   * finding the closest property provider in class hierarchy that can handle
   * this class.
   * 
   * @param parentClass
   *          the parent class
   * @return the property provider
   */
  private IPropertyProvider getPropertyProvider(Class<?> parentClass) {
    // FIXME sr137: this should also be done for the propertyeditors
    // find the provider with the closest supporting super class in
    // class hierarchy
    // e.g. if there is an Integer property provider it is preferred
    // before a Number property provider for Integer classes.

    Class<?> supportedClass = Object.class;
    IPropertyProvider result = new DefaultPropertyProvider();

    for (Entry<Class<?>, IPropertyProvider> e : propertyProviders.entrySet()) {
      // check if provider is capable of handling parentClass
      if (e.getValue().supportsClass(parentClass)
          && e.getKey().isAssignableFrom(parentClass)) {
        // check if supported class is a subclass of already found
        // supported class
        if (supportedClass.isAssignableFrom(e.getKey())) {
          supportedClass = e.getKey();
          result = e.getValue();
        }
      }
    }

    return result;
  }

  @Override
  public int getIndexOfChild(Object parent, Object child) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Object getRoot() {
    return value;
  }

  @Override
  public boolean isLeaf(Object node) {
    if (node == null) {
      return true;
    }

    Class<?> parentClass;
    if (node instanceof PropertyHelper) {
      PropertyHelper p = (PropertyHelper) node;

      if (p.isOmmited()) {
        return true;
      }

      parentClass = p.getDescriptor().getType();
      node = p.getValue();
      if (node != null) {
        parentClass = node.getClass();
      }
    } else {
      parentClass = node.getClass();
    }

    if (node == null) {
      return true;
    }

    List<IProperty> checkClass = checkClass(parentClass, node);

    return checkClass == null ? true : checkClass.size() == 0;
  }

  @Override
  public void setValueAt(Object aValue, Object node, int columnIndex) {
    if (columnIndex != 1) {
      return;
    }

    if (node instanceof PropertyHelper) {
      PropertyHelper p = (PropertyHelper) node;

      try {
        if (p.getParent() instanceof PropertyHelper) {
          p.getDescriptor().setValue(
              ((PropertyHelper) p.getParent()).getValue(), aValue);
        } else {
          p.getDescriptor().setValue(p.getParent(), aValue);
        }
        // TODO sr137: in might be necessary to fire a structure
        // change of parent instead
        fireTreeNodesChanged(this, getPathToNode(getRoot()), null, null);
        firePropertySet(p, aValue);
      } catch (Exception e) {
        SimSystem.report(e);
      }
    }
  }

  /**
   * Adds a property filter.
   * 
   * @param f
   *          the filter to add
   */
  public synchronized void addPropertyFilter(IPropertyFilter f) {
    propertyFilters.add(f);
    fireTreeStructureChanged(this, getPathToNode(getRoot()), null, null);
  }

  /**
   * Removes a property filter.
   * 
   * @param f
   *          the filter to remove
   */
  public synchronized void removePropertyFilter(IPropertyFilter f) {
    propertyFilters.remove(f);
    fireTreeStructureChanged(this, getPathToNode(getRoot()), null, null);
  }

  /**
   * Adds property changed listener.
   * 
   * @param listener
   *          the listener to register
   */
  public void addPropertyChangedListener(IPropertyChangedListener listener) {
    listeners.addListener(listener);
  }

  /**
   * Removes property changed listener.
   * 
   * @param listener
   *          the listener to remove
   */
  public void removePropertyChangedListener(IPropertyChangedListener listener) {
    listeners.removeListener(listener);
  }

  /**
   * Notifies registered {@link IPropertyChangedListener}s that the passed
   * property changed.
   * 
   * @param p
   *          the property helper that contains property information
   * @param v
   *          the value the specified property changed to
   */
  protected void firePropertySet(PropertyHelper p, Object v) {
    for (IPropertyChangedListener l : listeners) {
      if (l != null) {
        l.propertyChanged(p.getParent(), p.getDescriptor().getName(), v);
      }
    }
  }

  /**
   * Adds a property provider for the specified class and subclasses (if no more
   * specific mapping exists for a sub class)
   * 
   * @param forType
   *          the for type to register the provider for
   * @param provider
   *          the provider to register
   */
  public void addPropertyProvider(Class<?> forType, IPropertyProvider provider) {
    propertyProviders.put(forType, provider);
    fireTreeStructureChanged(this, getPathToNode(getRoot()), null, null);
  }

  /**
   * Removes a previously added property provider.
   * 
   * @param p
   *          the provider to remove
   */
  public void removePropertyProvider(IPropertyProvider p) {
    List<Class<?>> keys = new ArrayList<>();
    for (Entry<Class<?>, IPropertyProvider> e : propertyProviders.entrySet()) {
      if (e.getValue() == p) {
        keys.add(e.getKey());
      }
    }

    for (Class<?> c : keys) {
      propertyProviders.remove(c);
    }

    fireTreeStructureChanged(this, getPathToNode(getRoot()), null, null);
  }
}
