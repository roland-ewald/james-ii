package org.jamesii.gui.application.resource;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.Icon;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.resource.iconset.IIconSet;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.application.resource.iconset.IconSetManager;
import org.jamesii.gui.utils.ListenerSupport;

// TODO: Auto-generated Javadoc
/**
 * Convenience class that provides static access to icon and image loading
 * utilizing the {@link ApplicationResourceManager}. It can also handle
 * locations that come from {@link IIconSet}s and are encoded with domain and
 * have an additional parameter called <b>iconset</b> which should be set to
 * <b>true</b> if the currently active {@link IIconSet} should be provided as
 * requesting class to the {@link ApplicationResourceManager}.
 * 
 * @author Stefan Rybacki
 */
public final class IconManager {

  /**
   * Tries to load an icon using the {@link ApplicationResourceManager} that is
   * identified by the given identifier, where the identifier must be in the
   * format of a {@link ResourceURL} linking to the icon to load. This parameter
   * should be automatically encoded when using the {@link BasicResources}
   * resource urls.
   * 
   * @param identifier
   *          the identifier in {@link ResourceURL} format linking to the icon
   *          to load
   * @param cache
   *          if true the icon will be cached after loading
   * @return the icon if successfully loaded or {@code null} if not loaded
   */
  public static Icon getIcon(String identifier, boolean cache) {
    Icon icon = null;
    try {
      // check whether there is a parameter called "iconset" set to
      // "true"
      // in which case the iconset class is provided to
      // ApplicationResourceManager#getResource()
      Class<?> requestingClass = null;
      try {
        ResourceURL url = new ResourceURL(identifier);
        Map<String, String> parameters = url.getParameters();
        if (parameters != null && "true".equals(parameters.get("iconset"))) {
          requestingClass = IconSetManager.getIconSet().getClass();
        }
      } catch (Exception e) {
        SimSystem.report(e);
      }
      icon =
          ApplicationResourceManager.getResource(identifier, cache,
              requestingClass);
    } catch (ResourceLoadingException e2) {
      SimSystem.report(Level.WARNING, null, "Couldn't load icon (%s)",
          new Object[] { e2.getMessage() });
    }
    return icon;
  }

  /**
   * Tries to load an icon using the {@link ApplicationResourceManager} that is
   * identified by the given identifier, where the identifier must be in the
   * format of a {@link ResourceURL} linking to the icon to load.
   * <p>
   * Note: caching is automatically set to {@code true} when using this method
   * 
   * @param identifier
   *          the identifier in {@link ResourceURL} format linking to the icon
   *          to load
   * @return the icon if successfully loaded or {@code null} if not loaded
   */
  public static Icon getIcon(String identifier) {
    return getIcon(identifier, true);
  }

  /**
   * Tries to load an icon using the currently active {@link IIconSet} that is
   * identified by the given identifier.
   * <p>
   * Note: caching is automatically set to {@code true} when using this method
   * 
   * @param id
   *          the identifier specifying an id within an {@link IIconSet}
   * @return the icon if successfully loaded or {@code null} if not loaded
   */
  public static Icon getIcon(IconIdentifier id) {
    return getIcon(id, null);
  }

  /**
   * Tries to load an icon using the currently active {@link IIconSet} that is
   * identified by the given identifier.
   * <p>
   * Note: caching is automatically set to {@code true} when using this method
   * 
   * @param id
   *          the identifier specifying an id within an {@link IIconSet}
   * @param altText
   *          the alternative text, if specified is rendered into the icon in
   *          case no {@link Icon} could be retrieved using the specified
   *          identifier
   * @return the icon if successfully loaded or {@code null} if not loaded
   */
  public static Icon getIcon(IconIdentifier id, String altText) {
    // TODO sr137: also cache the icon proxy
    return new IconProxy(id, altText);
  }

  /**
   * Tries to load an image using the {@link ApplicationResourceManager} that is
   * identified by the given identifier, where the identifier must be in the
   * format of a {@link ResourceURL} linking to the image to load.
   * 
   * @param identifier
   *          the identifier in {@link ResourceURL} format linking to the image
   *          to load
   * @param cache
   *          if true image will be cached after loading
   * @return the image if successfully loaded or {@code null} if not loaded
   */
  public static Image getImage(String identifier, boolean cache) {
    Image image = null;
    try {
      // check whether there is a parameter called "imageset" set to
      // "true"
      // in which case the imageset class is provieded to
      // ApplicationResourceManager#getResource()
      Class<?> requestingClass = null;
      try {
        ResourceURL url = new ResourceURL(identifier);
        Map<String, String> parameters = url.getParameters();
        if (parameters != null && "true".equals(parameters.get("imageset"))) {
          requestingClass = IconSetManager.getIconSet().getClass();
        }
      } catch (Exception e) {
        SimSystem.report(e);
      }
      image =
          ApplicationResourceManager.getResource(identifier, cache,
              requestingClass);
    } catch (ResourceLoadingException e2) {
      SimSystem.report(Level.WARNING, null, "Couldn't load image (%s) - location tried: %s",
          new Object[] { e2.getMessage(), identifier });
    }
    return image;
  }

  /**
   * Tries to load an image using the {@link ApplicationResourceManager} that is
   * identified by the given identifier, where the identifier must be in the
   * format of a {@link ResourceURL} linking to the image to load.
   * <p>
   * Note: caching is automatically set to {@code true} when using this method
   * 
   * @param identifier
   *          the identifier in {@link ResourceURL} format linking to the image
   *          to load
   * @return the image if successfully loaded or {@code null} if not loaded
   */
  public static Image getImage(String identifier) {
    return getImage(identifier, true);
  }

  /**
   * /** Tries to load an icon using the {@link ApplicationResourceManager} that
   * is identified by the given identifier, where the identifier must be in the
   * format of a {@link ResourceURL} linking to the icon to load. This parameter
   * should be automatically encoded when using the {@link BasicResources}
   * resource urls.
   * 
   * @param identifier
   *          the identifier in {@link ResourceURL} format linking to the icon
   *          to load
   * @param cache
   *          if true icon will be cached
   * @param requestingClass
   *          the class requesting the resource, this is important to provide if
   *          you load a resource from a separate plugin and the calling class's
   *          classloader should be used to load the resource or if the location
   *          to the resource is provided in a relative to the requesting class
   *          form
   * @return the icon if it could be loaded and existed, {@code null} else
   */
  public static Icon getIcon(String identifier, boolean cache,
      Class<?> requestingClass) {
    Icon icon = null;
    try {
      icon =
          ApplicationResourceManager.getResource(identifier, cache,
              requestingClass);
    } catch (ResourceLoadingException e2) {
      SimSystem.report(Level.WARNING, null, "Couldn't load icon (%s)",
          new Object[] { e2.getMessage() });
    }
    return icon;
  }

  /**
   * /** Tries to load an icon using the {@link ApplicationResourceManager} that
   * is identified by the given identifier, where the identifier must be in the
   * format of a {@link ResourceURL} linking to the icon to load. This parameter
   * should be automatically encoded when using the {@link BasicResources}
   * resource urls.
   * 
   * @param identifier
   *          the identifier in {@link ResourceURL} format linking to the icon
   *          to load
   * @param cache
   *          if true icon will be cached
   * @param requestingClass
   *          the class requesting the resource, this is important to provide if
   *          you load a resource from a separate plugin and the calling class's
   *          classloader should be used to load the resource or if the location
   *          to the resource is provided in a relative to the requesting class
   *          form
   * @return the icon if it could be loaded and existed, {@code null} else
   */
  public static Image getImage(String identifier, boolean cache,
      Class<?> requestingClass) {
    Image image = null;
    try {
      image =
          ApplicationResourceManager.getResource(identifier, cache,
              requestingClass);
    } catch (ResourceLoadingException e2) {
      SimSystem.report(Level.WARNING, null, "Couldn't load image (%s)",
          new Object[] { e2.getMessage() });
    }
    return image;
  }

  /**
   * Tries to load an image using the currently active {@link IIconSet} that is
   * identified by the given identifier.
   * <p>
   * Note: caching is automatically set to {@code true} when using this method
   * 
   * @param id
   *          the identifier specifying an id within an {@link IIconSet}
   * @return the image if successfully loaded or {@code null} if not loaded
   */
  public static Image getImage(IconIdentifier id) {
    return new ImageProxy(id);
  }

  private static class ImageProducerProxy implements ImageProducer,
      ImageConsumer {
    private ListenerSupport<ImageConsumer> consumers = new ListenerSupport<>();

    private ImageProducer ip;

    /**
     * Create an image producer proxy.
     * 
     * @param ip
     *          the image producer we provide access to
     */
    public ImageProducerProxy(ImageProducer ip) {
      this.ip = ip;
    }

    /**
     * Set the image producer this instance provides access to.
     * 
     * @param ip
     *          the image producer to provide access to
     */
    public void setImageProducer(ImageProducer ip) {
      this.ip.removeConsumer(this);
      this.ip = ip;
      ip.addConsumer(this);
      for (ImageConsumer c : consumers) {
        c.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
      }
    }

    @Override
    public void addConsumer(ImageConsumer ic) {
      consumers.addListener(ic);
    }

    @Override
    public boolean isConsumer(ImageConsumer ic) {
      return consumers.contains(ic);
    }

    @Override
    public void removeConsumer(ImageConsumer ic) {
      consumers.removeListener(ic);
    }

    @Override
    public void requestTopDownLeftRightResend(ImageConsumer ic) {
      ip.requestTopDownLeftRightResend(ic);
    }

    @Override
    public void startProduction(ImageConsumer ic) {
      ip.startProduction(ic);
    }

    @Override
    public void imageComplete(int status) {
      for (ImageConsumer c : consumers) {
        c.imageComplete(status);
      }
    }

    @Override
    public void setColorModel(ColorModel model) {
      for (ImageConsumer c : consumers) {
        c.setColorModel(model);
      }
    }

    @Override
    public void setDimensions(int width, int height) {
      for (ImageConsumer c : consumers) {
        c.setDimensions(width, height);
      }
    }

    @Override
    public void setHints(int hintflags) {
      for (ImageConsumer c : consumers) {
        c.setHints(hintflags);
      }
    }

    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model,
        byte[] pixels, int off, int scansize) {
      for (ImageConsumer c : consumers) {
        c.setPixels(x, y, w, h, model, pixels, off, scansize);
      }
    }

    @Override
    public void setPixels(int x, int y, int w, int h, ColorModel model,
        int[] pixels, int off, int scansize) {
      for (ImageConsumer c : consumers) {
        c.setPixels(x, y, w, h, model, pixels, off, scansize);
      }
    }

    @Override
    public void setProperties(Hashtable<?, ?> props) {
      for (ImageConsumer c : consumers) {
        c.setProperties(props);
      }
    }

  }

  /**
   * The Class ImageProxy.
   */
  private static class ImageProxy extends Image implements
      PropertyChangeListener {

    /**
     * The id.
     */
    private IconIdentifier id;

    /** The img. */
    private Image img;

    private ImageProducerProxy ip;

    private boolean fake;

    /**
     * Instantiates a new image proxy.
     * 
     * @param id
     *          the id
     */
    public ImageProxy(IconIdentifier id) {
      super();
      this.id = id;
      img = IconSetManager.getIconSet().getImage(id);
      fake = false;
      if (img == null) {
        img = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
        fake = true;
      }
      ip = new ImageProducerProxy(img.getSource());
      IconSetManager.addPropertyChangeListener(this);
    }

    @Override
    public Graphics getGraphics() {
      return img.getGraphics();
    }

    @Override
    public int getHeight(ImageObserver observer) {
      return fake ? 0 : img.getHeight(observer);
    }

    @Override
    public Object getProperty(String name, ImageObserver observer) {
      return fake ? null : img.getProperty(name, observer);
    }

    @Override
    public ImageProducer getSource() {
      return ip;
    }

    @Override
    public int getWidth(ImageObserver observer) {
      return fake ? 0 : img.getWidth(observer);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      img = IconSetManager.getIconSet().getImage(id);
      fake = false;
      if (img == null) {
        fake = true;
        img = new BufferedImage(0, 0, BufferedImage.TYPE_3BYTE_BGR);
      }
      ip = new ImageProducerProxy(img.getSource());
    }

  }

  /**
   * The Class IconProxy.
   */
  private static class IconProxy implements Icon, PropertyChangeListener {

    /**
     * The id.
     */
    private final IconIdentifier id;

    /** The icon. */
    private Icon icon;

    /**
     * The alternative text rendered on icon in case no icon is retrieved using
     * the {@link IconIdentifier}.
     */
    private String altText;

    /**
     * Instantiates a new icon proxy.
     * 
     * @param id
     *          the icon identifier
     * @param altText
     *          the alternative text if there is no icon retrieved using the
     *          identifier
     */
    public IconProxy(IconIdentifier id, String altText) {
      this.id = id;
      this.altText = altText;
      icon = IconSetManager.getIconSet().getIcon(id);
      IconSetManager.addPropertyChangeListener(this);
    }

    @Override
    public int getIconHeight() {
      return icon == null ? 16 : icon.getIconHeight();
    }

    @Override
    public int getIconWidth() {
      if (icon == null) {
        if (altText != null) {
          Font font = new Font(Font.DIALOG, Font.PLAIN, 12);
          FontRenderContext frc = new FontRenderContext(null, false, false);
          return (int) font.getStringBounds(altText, frc).getWidth() + 2;
        } else {
          return 16;
        }
      } else {
        return icon.getIconWidth();
      }
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      if (icon != null) {
        icon.paintIcon(c, g, x, y);
      } else if (altText != null) {
        // render alternative text
        g.setColor(Color.black);
        g.drawString(altText, x + 1, y + getIconHeight()
            - g.getFontMetrics().getDescent());
      }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      icon = IconSetManager.getIconSet().getIcon(id);
    }

  }

  private IconManager() {
  }
}
