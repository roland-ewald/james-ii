/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.jamesii.gui.validation.IValidator;
import org.jamesii.gui.validation.IValidatorListener;

/**
 * Decoration that uses a specified validator to determine whether to display a
 * specified icon at one of the four corners of the decorated component. The
 * icon is displayed if the content of the component (whatever the content is)
 * is not specified valid by the given validator.
 * 
 * @author Stefan Rybacki
 * 
 */
public class ValidatorDecoration extends DefaultDecoration implements
    IValidatorListener {
  /**
   * Serialization proxy for this decoration
   * 
   * @author Stefan Rybacki
   */
  private static final class SerializationProxy implements Serializable {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -3104630341128445002L;

    /**
     * position to store
     */
    private int pos;

    /**
     * validator to store
     */
    private IValidator validator;

    /**
     * icon to store
     */
    private transient Image icon = null;

    /**
     * Instantiates a new serialization proxy.
     * 
     * @param v
     *          the validator decoration instance to serialize
     */
    private SerializationProxy(ValidatorDecoration v) {
      pos = v.position;
      validator = v.validator;
      icon = v.icon;
    }

    /**
     * Read resolve.
     * 
     * @return the object
     */
    private Object readResolve() {
      return new ValidatorDecoration(validator, icon, pos);
    }

  }

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -2368241387612624957L;

  /**
   * indicates the north west position
   */
  public static final int NORTH_WEST = 0;

  /**
   * indicates the north west position
   */
  public static final int NORTH_EAST = 1;

  /**
   * indicates the north west position
   */
  public static final int SOUTH_WEST = 2;

  /**
   * indicates the north west position
   */
  public static final int SOUTH_EAST = 3;

  /**
   * Default icon width.
   */
  private static final int ICON_WIDTH = 11;

  /**
   * Default icon height.
   */
  private static final int ICON_HEIGHT = 11;

  /**
   * the validator attached to the decoration
   */
  private transient IValidator validator;

  /**
   * flag indicating whether to show the icon at specified position
   */
  private transient boolean showIcon = false;

  /**
   * the icon to show if showIcon is true
   */
  private transient Image icon;

  /**
   * the position of the icon (can be {@link #NORTH_WEST}, {@link #NORTH_EAST},
   * {@link #SOUTH_WEST} or {@link #SOUTH_EAST})
   */
  private transient int position;

  /**
   * Creates a new validator decoration that can be used to mark invalid content
   * with a specified icon at one of the four corners of the the decorated
   * component.
   * 
   * @param validator
   *          the validator used to validate the components content
   * @param icon
   *          the icon to show if not valid
   * @param position
   *          the position where to show the content (can be {@link #NORTH_WEST}
   *          , {@link #NORTH_EAST}, {@link #SOUTH_WEST} or {@link #SOUTH_EAST})
   */
  public ValidatorDecoration(IValidator validator, Image icon, int position) {
    this.validator = validator;
    this.icon = icon;
    this.position = position;
    showIcon = !validator.isValid();
    validator.addValidatorListener(this);
  }

  /**
   * helper function that calculates the rectangle in which the icon is to draw
   * in case the content is not valid.
   * 
   * @param pos
   * @return the icon rectangle
   */
  private Rectangle calcIconRect(int pos) {

    Rectangle res = new Rectangle(0, 0, ICON_WIDTH, ICON_HEIGHT);
    switch (pos) {
    case NORTH_EAST:
      res.setLocation(getDecorator().getWidth() - ICON_WIDTH, 0);
      break;
    case SOUTH_EAST:
      res.setLocation(getDecorator().getWidth() - ICON_WIDTH, getDecorator()
          .getHeight() - ICON_HEIGHT);
      break;
    case SOUTH_WEST:
      res.setLocation(0, getDecorator().getHeight() - ICON_HEIGHT);
      break;
    case NORTH_WEST:
    default:
      res.setLocation(0, 0);
    }
    return res;
  }

  @Override
  protected void paintDecoration(Graphics2D g, Decorator d) {
    super.paintDecoration(g, d);
    // if icon is to show and icon is not null
    if (showIcon && icon != null) {
      // determine icon rectangle
      Rectangle r = calcIconRect(position);
      // use rectangle to draw icon in
      g.drawImage(icon, r.x, r.y, r.width, r.height, getDecorator());
    }
  }

  @Override
  public final void validated(IValidator src) {
    if (src == validator) { // NOSONAR: need identity equals here
      showIcon(validator.isValid());
    }
  }

  /**
   * helper function that sets the {@link #showIcon} flag and issues an update
   * for the region the icon should be painted at
   * 
   * @param valid
   *          specifies whether the content is valid
   */
  private void showIcon(boolean valid) {
    showIcon = !valid;

    // calculate icon rectangle and only repaint that area
    Rectangle r = calcIconRect(position);
    getDecorator().repaint(r.x, r.y, r.width, r.height);
  }

  /**
   * Write replace.
   * 
   * @return the object
   */
  private Object writeReplace() {
    return new SerializationProxy(this);
  }

  /**
   * Read object.
   * 
   * @param stream
   *          the stream
   * @throws InvalidObjectException
   *           the invalid object exception
   */
  private void readObject(ObjectInputStream stream) // NOSONAR
      throws InvalidObjectException {
    throw new InvalidObjectException("Proxy needed!");
  }
}
