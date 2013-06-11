/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.utils.objecteditor.implementationprovider.DoubleImplementationProvider;
import org.jamesii.gui.utils.objecteditor.implementationprovider.IImplementationProvider;
import org.jamesii.gui.utils.objecteditor.property.IPropertyFilter;
import org.jamesii.gui.utils.objecteditor.property.editor.IPropertyEditor;
import org.jamesii.gui.utils.objecteditor.property.provider.IPropertyProvider;
import org.jamesii.gui.utils.parameters.factories.FactoryParameters;

/**
 * This is the base class when using a tree based property editor for any kind
 * of objects. Those objects can go from simple beans to {@link ParameterBlock}s
 * used to parameterize a factory. The editor provides easy access to arbitrary
 * properties of a given object and its properties and subproperties. It also
 * provides the option to provide custom editors for special properties and/or
 * special values. It also provides the option to provide custom implementations
 * for specific properties which are shown in a dropdown for selection. The
 * entire property determination process is transparent in a way that it is
 * possible to provide arbitrary properties for an object, property or
 * subproperty using {@link IPropertyProvider} implementations.
 * <p/>
 * To use this component you can just pass an object which's properties you want
 * to edit. If no special {@link IPropertyProvider} is provided only write and
 * readable bean properties are displayed. Also only a bunch of standard editors
 * are integrated by default this include editors for numbers, Strings, Fonts
 * and booleans. Any other editor should be custom registered using
 * {@link #registerEditor(Class, IPropertyEditor)}. If you want to provide more
 * or different properties than bean properties you can register a custom
 * property provider using
 * {@link #registerPropertyProvider(Class, IPropertyProvider)}. In case a
 * property provider provides unwanted or to many properties you could also use
 * a custom {@link IPropertyFilter} implementation with
 * {@link #registerPropertyFilter(IPropertyFilter)} instead of implementing
 * another custom {@link IPropertyProvider}.
 * <p/>
 * To provide special values for properties you can provide a list of assignable
 * values by providing a {@link IImplementationProvider} using
 * {@link #registerImplementationProvider(IImplementationProvider)}.
 * <p/>
 * An example usage could look like this: <br/>
 * <code>
 * <pre>
 * ...
 * ObjectEditorComponent c=ObjectEditorComponent(object);
 * c.registerEditor(Double.class, new DoubleImplementationProvider());
 * panel.add(c, ...);
 * </pre>
 * </code> The example creates an editor component for a given object. Assuming
 * the object has a {@link Double} property a
 * {@link DoubleImplementationProvider} is provided to be able to set the
 * property to {@link Double#POSITIVE_INFINITY} or
 * {@link Double#NEGATIVE_INFINITY}.
 * <p/>
 * E.g., {@link FactoryParameters} makes heavy use of this component with custom
 * {@link IPropertyProvider}s as well as {@link IImplementationProvider} for
 * plugin types. There this component is used to fill a {@link ParameterBlock}
 * for a specified {@link Factory}, including plugin type assignment and sub
 * {@link ParameterBlock}s for sub plugin type parameters.
 * 
 * @author Stefan Rybacki
 */
public class ObjectEditorComponent extends JComponent implements Scrollable {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 1667554005998313663L;

  /**
   * The tree table.
   */
  private ObjectEditorTreeTable treeTable;

  /**
   * Instantiates a new object editor component.
   * 
   * @param toEdit
   *          the object to edit
   * @param errorIcon
   *          the icon to be shown in editors when the entered value is not
   *          valid
   */
  public ObjectEditorComponent(Object toEdit, Image errorIcon) {
    this(toEdit, errorIcon, null, null);
  }

  /**
   * Instantiates a new object editor component.
   * 
   * @param toEdit
   *          the object to edit
   */
  public ObjectEditorComponent(Object toEdit) {
    this(toEdit, IconManager.getImage(IconIdentifier.ERROR_SMALL));
  }

  /**
   * Instantiates a new object editor component for the given object to edit. It
   * also offers the option to provide custom column names that are displayed.
   * 
   * @param toEdit
   *          the object which's properties to be edited
   * @param column1
   *          the column name for the property name column
   * @param column2
   *          the column name for the property value column
   */
  public ObjectEditorComponent(Object toEdit, String column1, String column2) {
    this(toEdit, IconManager.getImage(IconIdentifier.ERROR_SMALL), column1,
        column2);
  }

  /**
   * Instantiates a new object editor component for the given object to edit. It
   * also offers the option to provide custom column names that are displayed.
   * 
   * @param toEdit
   *          the object which's properties to be edited
   * @param errorIcon
   *          the icon to be displayed if a editor can't accept an incompatible
   *          value
   * @param column1
   *          the column name for the property name column
   * @param column2
   *          the column name for the property value column
   */
  public ObjectEditorComponent(Object toEdit, Image errorIcon, String column1,
      String column2) {
    super.setLayout(new BorderLayout());

    treeTable = new ObjectEditorTreeTable(toEdit, errorIcon, column1, column2);
    super.addImpl(new JScrollPane(treeTable), BorderLayout.CENTER, 0);
  }

  /**
   * Registers an editor for the specified class.
   * 
   * @param forClass
   *          the for class
   * @param editor
   *          the editor
   */
  public synchronized void registerEditor(Class<?> forClass,
      IPropertyEditor<?> editor) {
    treeTable.addEditor(forClass, editor);
  }

  /**
   * Registers a custom implementation provider.
   * 
   * @param p
   *          the p
   */
  public synchronized void registerImplementationProvider(
      IImplementationProvider<?> p) {
    treeTable.addImplementationProvider(p);
  }

  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    throw new UnsupportedOperationException(
        "Adding a component is not supported!");
  }

  @Override
  public void setLayout(LayoutManager mgr) {
    throw new UnsupportedOperationException(
        "Setting the layout of this component is not supported!");
  }

  @Override
  public void remove(int index) {
    throw new UnsupportedOperationException(
        "Removing a component is not supported!");
  }

  /**
   * Registers a custom property filter.
   * 
   * @param f
   *          the f
   */
  public synchronized void registerPropertyFilter(IPropertyFilter f) {
    treeTable.addPropertyFilter(f);
  }

  /**
   * Add property changed listener. Listeners are notified as soon as a property
   * of the currently edited object was changed through this component. This
   * includes any sub property.
   * 
   * @param listener
   *          the listener to register
   */
  public final synchronized void addPropertyChangedListener(
      IPropertyChangedListener listener) {
    treeTable.addPropertyChangedListener(listener);
  }

  /**
   * Removes a previously registered property changed listener.
   * 
   * @param listener
   *          the listener to remove
   */
  public final synchronized void removePropertyChangedListener(
      IPropertyChangedListener listener) {
    treeTable.removePropertyChangedListener(listener);
  }

  /**
   * Sets the value to edit.
   * 
   * @param value
   *          the new value to edit
   */
  public void setValueToEdit(Object value) {
    treeTable.setValueToEdit(value);
  }

  /**
   * Registers a custom property provider for the specified class. Registered
   * property providers are also applied for all "forType" subclassing classes
   * if there is no more concrete mapping. E.g., if there is a property provider
   * registered for {@link Number} it will automatically apply to {@link Double}
   * , {@link Integer} and so on. In case there is another provider mapping for
   * {@link Double} this one is prioritized for {@link Double} properties since
   * it is more concrete.
   * 
   * @param forType
   *          the for type
   * @param provider
   *          the provider
   */
  public void registerPropertyProvider(Class<?> forType,
      IPropertyProvider provider) {
    treeTable.addPropertyProvider(forType, provider);
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return treeTable.getPreferredScrollableViewportSize();
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle visibleRect,
      int orientation, int direction) {
    return treeTable.getScrollableBlockIncrement(visibleRect, orientation,
        direction);
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return treeTable.getScrollableTracksViewportHeight();
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return treeTable.getScrollableTracksViewportWidth();
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation,
      int direction) {
    return treeTable.getScrollableUnitIncrement(visibleRect, orientation,
        direction);
  }

}