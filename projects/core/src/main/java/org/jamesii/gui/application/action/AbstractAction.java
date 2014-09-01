/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import org.jamesii.gui.application.IWindow;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract class implementing basic functionality of {@link IAction}. This
 * class should be the base class for future {@link IAction} implementations.
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class AbstractAction implements IAction {
  /**
   * the action's id
   */
  private final String id;

  /**
   * the action's label
   */
  private String label;

  /**
   * the action's paths
   */
  private final List<String> paths = new ArrayList<>();

  /**
   * the action's short description
   */
  private String shortDescription;

  /**
   * the action's icon
   */
  private Icon icon;

  /**
   * the action's description
   */
  private String description;

  /**
   * property change support for action properties
   */
  private final PropertyChangeSupport changeSupport =
      new PropertyChangeSupport(this);

  /**
   * flag indicating whether action is enabled or not
   */
  private boolean enabled = true;

  /**
   * the action's mnemonic
   */
  private Integer mnemonic = null;

  /**
   * the action's key stroke string
   */
  private final String keyStroke;

  /**
   * toggle on flag
   */
  private boolean toggle = false;

  /** The window. */
  private IWindow window;

  // private final Map<PropertyChangeListener, WeakPropertyChangeListenerProxy>
  // listenerMapping = new WeakHashMap<PropertyChangeListener,
  // WeakPropertyChangeListenerProxy>();

  /**
   * Creates a new {@link AbstractAction} instance with the given parameters
   * prepopulated.
   * 
   * @param id
   *          the action's id
   * @param label
   *          the action's label
   * @param shortDescription
   *          the action's short description
   * @param description
   *          the action's description
   * @param icon
   *          the action's icon
   * @param paths
   *          the action's paths
   * @param keyStroke
   *          the action's key stroke
   * @param mnemonic
   *          the action's mnemonic
   * @param window
   *          the window the action is used in
   */
  public AbstractAction(String id, String label, String shortDescription,
      String description, Icon icon, String[] paths, String keyStroke,
      Integer mnemonic, IWindow window) {
    this.id = id;
    this.label = label;
    this.description = description;
    this.icon = icon;
    this.shortDescription = shortDescription;
    if (paths == null) {
      throw new IllegalArgumentException("paths can't be null");
    }
    Collections.addAll(this.paths, paths);
    this.keyStroke = keyStroke;
    this.mnemonic = mnemonic;

    if (shortDescription == null) {
      // in case there is no short description use the label instead
      this.shortDescription = label;
    }

    this.window = window;
  }

  @Override
  public IWindow getWindow() {
    return window;
  }

  /**
   * Creates a new {@link AbstractAction} instance with the given parameters
   * prepopulated.
   * 
   * @param id
   *          the action's id
   * @param label
   *          the action's label
   * @param paths
   *          the action's paths
   * @param window
   *          the window the action belongs to
   */
  public AbstractAction(String id, String label, String[] paths, IWindow window) {
    this(id, label, null, null, null, paths, null, null, window);
  }

  /**
   * Creates a new {@link AbstractAction} instance with the given parameters
   * prepopulated.
   * 
   * @param id
   *          the action's id
   * @param label
   *          the action's label
   * @param paths
   *          the action's paths
   * @param keyStroke
   *          the action's key stroke
   * @param mnemonic
   *          the action's mnemonic
   * @param window
   *          the window the action belongs to
   */
  public AbstractAction(String id, String label, String[] paths,
      String keyStroke, Integer mnemonic, IWindow window) {
    this(id, label, null, null, null, paths, keyStroke, mnemonic, window);
  }

  /**
   * Creates a new {@link AbstractAction} instance with the given parameters
   * prepopulated.
   * 
   * @param id
   *          the action's id
   * @param label
   *          the action's label
   * @param icon
   *          the action's icon
   * @param paths
   *          the action's paths
   * @param keyStroke
   *          the action's key stroke
   * @param mnemonic
   *          the action's mnemonic
   * @param window
   *          the window the action belongs to
   */
  public AbstractAction(String id, String label, Icon icon, String[] paths,
      String keyStroke, Integer mnemonic, IWindow window) {
    this(id, label, null, null, icon, paths, keyStroke, mnemonic, window);
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public final boolean equals(Object obj) {
    if (obj instanceof IAction) {
      return ((IAction) obj).getId().equals(getId())
          && ((IAction) obj).getWindow() == getWindow();
    }
    return super.equals(obj);
  }

  @Override
   public final int hashCode() {
    return id.hashCode() * 31
        + (getWindow() == null ? 0 : getWindow().hashCode());
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public String[] getPaths() {
    return paths.toArray(new String[paths.size()]);
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Icon getIcon() {
    return icon;
  }

  @Override
  public String getShortDescription() {
    return shortDescription;
  }

  @Override
  public String toString() {
    return getId();
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener l) {
    // WeakPropertyChangeListenerProxy p = new
    // WeakPropertyChangeListenerProxy(l);
    // listenerMapping.put(l, p);
    changeSupport.addPropertyChangeListener(l);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener l) {
    changeSupport.removePropertyChangeListener(l);
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public void setEnabled(boolean e) {
    boolean old = enabled;
    enabled = e;
    firePropertyChange("enabled", old, e);
  }

  /**
   * Notifies the registered {@link PropertyChangeListener}s about the specified
   * property change event.
   * 
   * @param valueName
   *          the property that changed
   * @param oldValue
   *          the old property value
   * @param newValue
   *          the new property value
   */
  protected final synchronized void firePropertyChange(String valueName,
      Object oldValue, Object newValue) {
    changeSupport.firePropertyChange(valueName, oldValue, newValue);
  }

  @Override
  public void setLabel(String label) {
    String old = this.label;
    this.label = label;
    firePropertyChange("label", old, label);
  }

  @Override
  public String getKeyStroke() {
    return keyStroke;
  }

  @Override
  public Integer getMnemonic() {
    return mnemonic;
  }

  @Override
  public ActionType getType() {
    return ActionType.ACTION;
  }

  @Override
  public boolean isToggleOn() {
    return toggle;
  }

  @Override
  public void setToggleOn(boolean toggleState) {
    boolean old = toggle;
    if (old != toggleState) {
      toggle = toggleState;
      toggleChanged(old);
      firePropertyChange("toggleOn", old, toggle);
    }
  }

  /**
   * Override this method if you want to react an a toggle state changes
   * 
   * @param previousState
   *          the previous toggle state
   */
  protected void toggleChanged(boolean previousState) {
  }

  @Override
  public void setIcon(Icon icon) {
    Icon old = this.icon;
    this.icon = icon;
    firePropertyChange("icon", old, icon);
  }
}
