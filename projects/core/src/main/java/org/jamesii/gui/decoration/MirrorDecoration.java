/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.decoration;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;

import org.jamesii.gui.utils.BasicUtilities;

/**
 * This decoration places a mirrored image of the decorated component at the
 * components bottom. This can be used to enhance the visual experience of
 * elements like images or headers.
 * 
 * @author Stefan Rybacki
 */
// TODO sr137: use VolatileImage instead of BufferedImage
public class MirrorDecoration extends DefaultDecoration {
  /**
   * Serialization proxy for this decoration
   * 
   * @author Stefan Rybacki
   */
  private static final class SerializationProxy implements Serializable {

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -7221222832041644502L;

    /**
     * gap between component and mirror image of component
     */
    private final int gap;

    /**
     * height of mirror image
     */
    private final int mirrorSize;

    /**
     * Internally used serialization proxy
     * 
     * @param m
     *          the mirror decoration to serialize
     */
    private SerializationProxy(MirrorDecoration m) {
      gap = m.gap;
      mirrorSize = m.mirrorSize;
    }

    /**
     * Deserializes a mirror decoration using the proxy
     * 
     * @return a deserialized mirror decoration
     */
    private Object readResolve() {
      return new MirrorDecoration(gap, mirrorSize);
    }

  }

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2598157012289128054L;

  /**
   * gap between mirrored image and component
   */
  private int gap = 5;

  /**
   * cache for the component rendered to a buffer
   */
  private transient SoftReference<BufferedImage> bufferCache =
      new SoftReference<>(null);

  /**
   * cache for the reflection image
   */
  private transient SoftReference<BufferedImage> reflectionCache =
      new SoftReference<>(null);

  /**
   * flag that indicates whether we are in reflection repaint pass
   */
  private transient boolean isRepaint = false;

  /**
   * size of mirrored image
   */
  private int mirrorSize;

  /**
   * flag indicating whether reflection should fall off at its height or
   * mirrorSize (in case mirrorSize is > imageHeight)
   */
  private transient boolean cutToImageHeight = false;

  /**
   * Constructs a mirror decoration that renders a reflection image of the
   * component at the bottom of the component using {@code gap} as distance from
   * component to component. {@code mirrorSize} specifies the size under the
   * component used for the reflection image
   * 
   * @param mirrorSize
   * @param gap
   */
  public MirrorDecoration(int mirrorSize, int gap) {
    super();
    setMirrorSize(mirrorSize);
    setGap(gap);
  }

  @Override
  protected final void paintDecoration(Graphics2D g, Decorator d) {
    int imageWidth = d.getWidth();
    int imageHeight = d.getComponent().getHeight() + d.getInsets().top;

    if (imageWidth <= 0 || imageHeight <= 0) {
      return;
    }

    Rectangle clipRect = g.getClipBounds();

    BufferedImage buffer = bufferCache.get();
    Graphics2D gr;
    // try to use the buffer cache
    if (buffer != null && buffer.getWidth() == imageWidth
        && buffer.getHeight() == imageHeight && d.getComponent().isOpaque()) {
      // reuse buffer and just update non clipped regions
      gr = buffer.createGraphics();
      gr.setClip(clipRect);
    } else {
      // render component into BufferedImage
      buffer =
          BasicUtilities.createCompatibleImage(imageWidth, imageHeight,
              Transparency.TRANSLUCENT);
      gr = buffer.createGraphics();
      bufferCache = new SoftReference<>(buffer);
    }
    // paint normal component
    super.paintDecoration(gr, d);

    // paint normal component to canvas
    g.translate(0, 0);
    g.drawRenderedImage(buffer, null);

    // try to reuse cache
    BufferedImage reflection = reflectionCache.get();
    Graphics2D rg;
    if (reflection != null && reflection.getWidth() == imageWidth
        && reflection.getHeight() == imageHeight) {
      // reuse reflection
      rg = (Graphics2D) reflection.getGraphics();
      rg.setClip(clipRect);
    } else {
      reflection =
          BasicUtilities.createCompatibleImage(imageWidth, imageHeight,
              Transparency.TRANSLUCENT);
      reflectionCache = new SoftReference<>(reflection);
      rg = reflection.createGraphics();
    }

    // draw gradiented image
    rg.drawRenderedImage(buffer, null);
    rg.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
    rg.setPaint(new GradientPaint(0, imageHeight
        - (cutToImageHeight ? Math.min(imageHeight, mirrorSize) : mirrorSize)
        + gap, new Color(0.0f, 0.0f, 0.0f, 0.0f), 0, imageHeight, new Color(
        0.0f, 0.0f, 0.0f, 0.5f)));
    rg.fillRect(0, 0, imageWidth, imageHeight);

    rg.dispose();
    gr.dispose();

    // draw reflection
    g.translate(0, imageHeight * 2 + gap);
    g.scale(1, -1);
    g.drawRenderedImage(reflection, null);

    if (isRepaint) {
      isRepaint = false;
    } else {
      // only call repaint if mirrored image was clipped before
      if (g.getClipBounds() != null
          && !clipRect.contains(new Rectangle(0, imageHeight, imageWidth,
              mirrorSize + gap))) {
        isRepaint = true;
        d.repaint(clipRect.x, imageHeight, clipRect.width, mirrorSize + gap);
      }
    }
  }

  @Override
  public final void setup(Decorator d) {
    super.setup(d);
    d.setInsets(new Insets(0, 0, mirrorSize + gap, 0));
  }

  /**
   * Sets the mirror height
   * 
   * @param size
   *          the height
   */
  public final void setMirrorSize(int size) {
    mirrorSize = size;
    if (getDecorator() != null) {
      getDecorator().setInsets(new Insets(0, 0, mirrorSize + gap, 0));
    }
  }

  /**
   * Sets the gap between mirror and actual component
   * 
   * @param gap
   *          the gap between mirror and component
   */
  public final void setGap(int gap) {
    this.gap = gap;
    if (getDecorator() != null) {
      getDecorator().setInsets(new Insets(0, 0, mirrorSize + gap, 0));
    }
  }

  /**
   * @return deserialized object using proxy
   */
  private Object writeReplace() {
    return new SerializationProxy(this);
  }

  /**
   * Replacing default deserialization method
   * 
   * @param stream
   *          the stream to deserialize from
   * @throws InvalidObjectException
   */
  private void readObject(ObjectInputStream stream) // NOSNOAR
      throws InvalidObjectException {
    throw new InvalidObjectException("Proxy needed!");
  }
}
