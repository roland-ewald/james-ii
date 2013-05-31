/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.IDirectory;
import org.jamesii.gui.utils.ListenerSupport;
import org.jamesii.gui.utils.objecteditor.implementationprovider.IImplementationProvider;
import org.jamesii.gui.utils.objecteditor.property.IPropertyFilter;
import org.jamesii.gui.utils.objecteditor.property.editor.BooleanPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.ColorPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.DimensionPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.DirectoryPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.DoublePropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.FontPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.IPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.IntegerPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.LongPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.editor.StringPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.provider.IPropertyProvider;
import org.jamesii.gui.utils.treetable.TreeTable;

/**
 * Class which is used internally by {@link ObjectEditorComponent}. It provides
 * the tree table view used to present properties and subproperties of the
 * object to edit.
 * 
 * @author Stefan Rybacki
 */
final class ObjectEditorTreeTable extends TreeTable {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -5567888681568218305L;

  // TODO sr137: set explicit editors for special properties (paths?)
  // ->
  // DefaultCloseOperation property to restrict which integer values
  // are
  // allowed IEditorProviders?
  /**
   * The mapping between editor and class that the editor can edit.
   */
  private final Map<Class<?>, IPropertyEditor<?>> editorMap = new HashMap<>();
  {
    // adding default property editors
    editorMap.put(Integer.class, new IntegerPropertyEditor());
    editorMap.put(Double.class, new DoublePropertyEditor());
    editorMap.put(String.class, new StringPropertyEditor());
    editorMap.put(Font.class, new FontPropertyEditor());
    editorMap.put(Dimension.class, new DimensionPropertyEditor());
    editorMap.put(Boolean.class, new BooleanPropertyEditor());
    editorMap.put(Long.class, new LongPropertyEditor());
    editorMap.put(Color.class, new ColorPropertyEditor());
    editorMap.put(IDirectory.class, new DirectoryPropertyEditor());
  }

  /**
   * The registered property providers.
   */
  private final Map<Class<?>, IPropertyProvider> propertyProviders =
      new HashMap<>();

  /**
   * The list of registered implementation providers.
   */
  private final List<IImplementationProvider<?>> implementationProviders =
      new ArrayList<>();

  /**
   * The renderer cache.
   */
  private final Map<IPropertyEditor<?>, PropertyEditorTableCellEditorRenderer<?>> rendererCache =
      new HashMap<>();

  /**
   * The internally used model.
   */
  private ObjectEditorModel model;

  /**
   * The registered listeners.
   */
  private final ListenerSupport<IPropertyChangedListener> listeners =
      new ListenerSupport<>();

  /**
   * The registered filters.
   */
  private final List<IPropertyFilter> filters = new ArrayList<>();

  /**
   * The error icon.
   */
  private Image errorIcon;

  /**
   * Instantiates a new object editor tree table.
   * 
   * @param toEdit
   *          the object to edit
   * @param errorIcon
   *          the error icon to use in editors
   */
  public ObjectEditorTreeTable(Object toEdit, Image errorIcon) {
    super(new ObjectEditorModel(toEdit));
    init(errorIcon);
  }

  /**
   * Instantiates a new object editor tree table.
   * 
   * @param toEdit
   *          the object to edit
   * @param errorIcon
   *          the error icon used by editors
   * @param column1
   *          the name of column 1
   * @param column2
   *          the name of column 2
   */
  public ObjectEditorTreeTable(Object toEdit, Image errorIcon, String column1,
      String column2) {
    super(new ObjectEditorModel(toEdit, column1, column2));
    init(errorIcon);
  }

  /**
   * Helper method used by constructors.
   * 
   * @param eI
   *          the error icon used by editors
   */
  private void init(Image eI) {
    model = (ObjectEditorModel) getTreeTableModel();
    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    errorIcon = eI;
  }

  @SuppressWarnings("unchecked")
  @Override
  public TableCellEditor getCellEditor(int row, int column) {
    column = this.convertColumnIndexToModel(column);
    if (column == 1) {
      Object o = getModel().getValueAt(row, column);

      Object node = getNode(row);
      if (node instanceof PropertyHelper) {
        PropertyHelper p = (PropertyHelper) node;

        Class<?> c = o == null ? p.getDescriptor().getType() : o.getClass();

        // use sorted list (later group by implementation provider)
        Map<String, Object> list = new TreeMap<>();

        for (IImplementationProvider<?> ai : implementationProviders) {
          try {
            Map<String, ?> map =
                ai.getImplementations(p.getParent(), p.getDescriptor()
                    .getName(), p.getDescriptor().getType());
            if (map != null) {
              list.putAll(map);
            }
          } catch (Exception e) {
            SimSystem.report(e);
          }
        }

        if (!p.isOmmited()) {
          for (Entry<Class<?>, IPropertyEditor<?>> e : editorMap.entrySet()) {
            if (e.getKey().isAssignableFrom(c)) {
              PropertyEditorTableCellEditorRenderer tableCellEditor =
                  new PropertyEditorTableCellEditorRenderer(e.getValue(),
                      new HashMap<String, Object>(), p.getDescriptor()
                          .isWritable(), errorIcon);

              tableCellEditor.setImplementations(list);

              // TODO sr137: if there are more editors for
              // propertyType + actual implementation currently
              // set (o) also display them
              return tableCellEditor;
            }
          }
        }

        if (!list.isEmpty() && p.getDescriptor().isWritable()) {
          return new PropertyEditorTableCellEditorRenderer(
              new FakePropertyEditor(), list, true, errorIcon);
        }
      }
    }
    return null;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    column = this.convertColumnIndexToModel(column);
    Object node = getNode(row);
    if (node instanceof PropertyHelper) {
      return ((PropertyHelper) node).getDescriptor().isWritable()
          && column == 1;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected TableCellRenderer getCellRendererFor(int row, int column) {
    column = this.convertColumnIndexToModel(column);
    if (column == 1) {
      Object o = getModel().getValueAt(row, column);
      if (o != null) {
        Class<?> c = o.getClass();

        for (Entry<Class<?>, IPropertyEditor<?>> e : editorMap.entrySet()) {
          if (e.getKey().isAssignableFrom(c)) {
            PropertyEditorTableCellEditorRenderer tableCellEditor =
                rendererCache.get(e.getValue());
            if (tableCellEditor == null) {
              tableCellEditor =
                  new PropertyEditorTableCellEditorRenderer(e.getValue(),
                      new HashMap<String, Object>(), false, null);
              rendererCache.put(e.getValue(), tableCellEditor);
            }
            return tableCellEditor;
          }
        }
      }

    }
    return super.getCellRendererFor(row, column);
  }

  /**
   * Registers an editor for the specified class.
   * 
   * @param forClass
   *          the class to register for
   * @param editor
   *          the editor to register
   */
  public synchronized void addEditor(Class<?> forClass,
      IPropertyEditor<?> editor) {
    editorMap.put(forClass, editor);
  }

  /**
   * Adds an implementation provider.
   * 
   * @param provider
   *          the provider to add
   */
  public synchronized void addImplementationProvider(
      IImplementationProvider<?> provider) {
    implementationProviders.add(provider);
  }

  /**
   * Adds a property filter.
   * 
   * @param f
   *          the filter to add
   */
  public synchronized void addPropertyFilter(IPropertyFilter f) {
    model.addPropertyFilter(f);
    filters.add(f);
  }

  /**
   * Adds a property changed listener.
   * 
   * @param listener
   *          the listener to add
   */
  public void addPropertyChangedListener(IPropertyChangedListener listener) {
    model.addPropertyChangedListener(listener);
    listeners.addListener(listener);
  }

  /**
   * Removes property changed listener.
   * 
   * @param listener
   *          the listener to remove
   */
  public void removePropertyChangedListener(IPropertyChangedListener listener) {
    model.removePropertyChangedListener(listener);
    listeners.removeListener(listener);
  }

  /**
   * Sets the value to edit.
   * 
   * @param value
   *          the new value to edit
   */
  public void setValueToEdit(Object value) {
    // remove listeners, filters and property providers
    for (IPropertyChangedListener l : listeners) {
      model.removePropertyChangedListener(l);
    }

    for (IPropertyFilter f : filters) {
      model.removePropertyFilter(f);
    }

    for (IPropertyProvider p : propertyProviders.values()) {
      model.removePropertyProvider(p);
    }

    model = new ObjectEditorModel(value);
    for (IPropertyChangedListener l : listeners) {
      model.addPropertyChangedListener(l);
    }

    for (IPropertyFilter f : filters) {
      model.addPropertyFilter(f);
    }

    for (Entry<Class<?>, IPropertyProvider> e : propertyProviders.entrySet()) {
      model.addPropertyProvider(e.getKey(), e.getValue());
    }

    setTreeTableModel(model);
  }

  /**
   * Adds a custom property provider for a specific type and all subtypes if
   * there is no more concrete mapping.
   * 
   * @param forType
   *          the for type
   * @param provider
   *          the provider
   */
  public void addPropertyProvider(Class<?> forType, IPropertyProvider provider) {
    model.addPropertyProvider(forType, provider);
    propertyProviders.put(forType, provider);
  }

}
