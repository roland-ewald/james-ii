/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Class that can be used to decorate Swing components. This is done by offering
 * the ability to paint on top of the components, similar to the glass pane
 * concept. Just Define a Decoration by subclassing {@link DefaultDecoration}
 * and overriding
 * {@link DefaultDecoration#paintDecoration(Graphics2D, Decorator)}
 * <p>
 * <b>Example:</b>
 * <p>
 * <code><pre>
 *   JPanel panel=new JPanel(new GridBagLayout());
 *   Decorator d=new Decorator(new JButton("Hello World!"), new CrossOutDecoration());
 *   panel.add(d);
 * </pre>
 * </code>
 * 
 * @author Stefan Rybacki
 * 
 * @see DefaultDecoration
 * 
 */
public class Decorator extends JComponent implements PropertyChangeListener {
  /**
   * Custom layout manager internally used
   * 
   * @author Stefan Rybacki
   */
  private class Layout implements LayoutManager {

    @Override
    public final void addLayoutComponent(String name, Component comp) {
      // nothing to do
    }

    @Override
    public final void layoutContainer(Container parent) {
      if (!(parent instanceof Decorator)) {
        return;
      }
      Decorator d = (Decorator) parent;
      if (d.component != null) {
        Insets in = parent.getInsets();
        d.component.setBounds(in.left, in.top, getWidth() - in.left - in.right,
            getHeight() - in.bottom - in.top);
      }
      if (d.decoratorPanel != null) {
        d.decoratorPanel.setSize(getWidth(), getHeight());
        d.decoratorPanel.setLocation(0, 0);
      }
    }

    @Override
    public final Dimension minimumLayoutSize(Container parent) {
      return component.getMinimumSize();
    }

    @Override
    public final Dimension preferredLayoutSize(Container parent) {
      return component.getPreferredSize();
    }

    @Override
    public final void removeLayoutComponent(Component comp) {
      // nothing to do
    }

  }

  /**
   * Serialization proxy for this decorator
   * 
   * @author Stefan Rybacki
   */
  private static final class SerializationProxy implements Serializable {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 6474084293325040987L;

    /**
     * the currently used decoration
     */
    private final IDecoration decoration;

    /**
     * helper panel
     */
    private final DecoratorPanel decoratorPanel;

    /**
     * Creates a new serialization proxy
     * 
     * @param d
     *          the decorator to serialize
     */
    private SerializationProxy(Decorator d) {
      decoration = d.decoration;
      decoratorPanel = d.decoratorPanel;
    }

    /**
     * Reads and restores a previously serialized {@link Decorator}
     * 
     * @return deserialized {@link Decorator}
     */
    private Object readResolve() {
      Decorator d = new Decorator(null, decoration);
      d.decoratorPanel = decoratorPanel;
      return d;
    }

  }

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -232623799117164656L;

  /**
   * the component to decorate
   */
  private transient JComponent component;

  /**
   * just a helper panel that is used to place additional components on it that
   * "fly" above the decorated component)
   */
  private transient DecoratorPanel decoratorPanel = new DecoratorPanel();

  /**
   * the decoration
   */
  private transient IDecoration decoration = new DefaultDecoration();

  /**
   * flag that indicated whether the component is currently painting the
   * decoration rather than the encapsulated component
   */
  private transient boolean isDecorating = false;

  /**
   * margins of the decorator around the specified component
   */
  private transient Insets insets = new Insets(0, 0, 0, 0);

  /**
   * Defines a decorator for the specified document. The decorator can be used
   * to draw additional information, graphics or what ever on top of the
   * specified component. This is similar to the glass pane approach of JFrame,
   * but is restricted on the component rather than on the entire frame, this
   * way each component can have its own custom decoration. Decorations can be
   * used for a lot of things, for instance to show validation icons on input
   * fields.
   * 
   * @param c
   *          the component to decorate
   * @param decoration
   *          the decoration to use
   */
  public Decorator(JComponent c, IDecoration decoration) {
    super();
    setLayout(new Layout());
    setOpaque(false);

    super.addImpl(decoratorPanel, null, 0);
    setComponent(c);
    setDecoration(decoration);
  }

  @Override
  public void updateUI() {
    SwingUtilities.updateComponentTreeUI(component);
    SwingUtilities.updateComponentTreeUI(decoratorPanel);
    super.updateUI();
  }

  /**
   * Creates a new Decorator using {@link DefaultDecoration} and no set
   * {@link JComponent}
   */
  public Decorator() {
    this(null, new DefaultDecoration());
  }

  /**
   * sets the component to decorate
   * 
   * @param c
   *          the component to set
   */
  public final void setComponent(JComponent c) {
    if (component != null) {
      super.remove(getComponentCount() - 1);
      component.removePropertyChangeListener(this);
    }

    firePropertyChange("component", component, c);

    component = c;
    if (component != null) {
      super.addImpl(component, null, getComponentCount());
      component.addPropertyChangeListener(this);
    }
    revalidate();
    repaint();
  }

  @Override
  protected final void addImpl(Component comp, Object constraints, int index) {
    throw new UnsupportedOperationException("add() not supported!");
  }

  @Override
  public final void paint(Graphics g) {
    if (g == null) {
      return;
    }

    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }
    if (decoration != null && !isDecorating && component != null) {
      isDecorating = true;
      decoration.paint((Graphics2D) g, this);
      isDecorating = false;
    } else {
      super.paint(g);
    }
  }

  @Override
  public final boolean isOptimizedDrawingEnabled() {
    return false;
  }

  @Override
  public final Dimension getPreferredSize() {
    if (component != null) {
      Dimension d = component.getPreferredSize();
      d.width += getInsets().left + getInsets().right;
      d.height += getInsets().top + getInsets().bottom;
      return d;
    }
    return super.getPreferredSize();
  }

  @Override
  public final Insets getInsets() {
    return new Insets(insets.top, insets.left, insets.bottom, insets.right);
  }

  /**
   * Sets the margins of the decorator, this way it is possible to define a
   * margin around the component where the decoration still can draw on.
   * 
   * @param insets
   *          the margins defined in an insets object
   * @see #setInsets(int, int, int, int)
   */
  public final void setInsets(Insets insets) {
    if (insets == null) {
      throw new IllegalArgumentException("insets must be != null");
    }

    firePropertyChange("insets", this.insets, insets);

    this.insets = insets;
    revalidate();
    repaint();
  }

  /**
   * Sets the margins of the decorator, this way it is possible to define a
   * margin around the component where the decoration still can draw on.
   * 
   * @param top
   *          top margin
   * @param left
   *          left margin
   * @param bottom
   *          bottom margin
   * @param right
   *          right margin
   */
  public final void setInsets(int top, int left, int bottom, int right) {
    setInsets(new Insets(top, left, bottom, right));
  }

  @Override
  public final void remove(int index) {
    throw new UnsupportedOperationException("remove() not supported!");
  }

  /**
   * Sets the decoration used to decorate the component
   * 
   * @param decoration
   *          decoration used to decorate
   */
  public final void setDecoration(IDecoration decoration) {
    if (decoration == null) {
      throw new IllegalArgumentException("decoration can't be null");
    }
    if (this.decoration != null) {
      this.decoration.removeFrom(this);
    }

    firePropertyChange("decoration", this.decoration, decoration);

    this.decoration = decoration;
    decoration.setup(this);
    revalidate();
  }

  /**
   * @return the decoration used to decorate the component
   */
  public final IDecoration getDecoration() {
    return decoration;
  }

  /**
   * @return the component to decorate
   */
  public final JComponent getComponent() {
    return component;
  }

  /**
   * @return the decorator panel, that can be used to place components on top of
   *         the decorated component
   */
  public final DecoratorPanel getDecoratorPanel() {
    return decoratorPanel;
  }

  /**
   * Serialization method using {@link SerializationProxy}
   * 
   * @return the object to serialize
   */
  private Object writeReplace() {
    return new SerializationProxy(this);
  }

  /**
   * Serialization method
   * 
   * @param stream
   *          stream to read object from
   * @throws InvalidObjectException
   */
  private void readObject(ObjectInputStream stream) // NOSONAR
      throws InvalidObjectException {
    throw new InvalidObjectException("Proxy needed!");
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
  }

}
