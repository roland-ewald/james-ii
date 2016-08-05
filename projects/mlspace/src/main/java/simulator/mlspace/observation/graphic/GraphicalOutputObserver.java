/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.TorusSurface;
import org.jamesii.core.util.logging.ApplicationLogger;

import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.eventrecord.IEventRecord;
import simulator.mlspace.observation.AbstractEffectObserver;
import simulator.mlspace.observation.graphic.output.IImageProcessor;

/**
 * ML-Space Observer producing graphical output. Uses (ML-Space-specific)
 * {@link IGraphicsProvider} to create images for current state of
 * continuous-space or/and NSM-aspect of a simulation, (combines them if needed)
 * and passes them to the given {@link IImageProcessor}s (NOT ML-Space-specific)
 * for displaying in a window and/or saving to image and/or video file.
 * 
 * Note: some aspects may be generalized further. Class may be a bit less
 * intuitive than possible after several refactorings (e.g. extracting the
 * displaying/saving to separate classes).
 * 
 * @author Arne Bittig
 * @param <R>
 *          Type of {@link IEventRecord} handled
 */
public class GraphicalOutputObserver<R extends IEventRecord>
    extends AbstractEffectObserver<R> {

  private final List<IGraphicsProvider<? extends IEventRecord>> gps =
      new LinkedList<>();

  private final Collection<IImageProcessor> imgProcessors;

  private final GraphicalOutputParameters params;

  private FrameProps fp;

  /**
   * Graphical output observer relying on given {@link IGraphicsProvider}s and
   * given parameters.
   * 
   * @param graphicProviders
   *          {@link IGraphicsProvider}s to use (in given order)
   * @param imageProcessors
   *          Image processors to pass composed graphics to
   * @param params
   *          Further parameters (see {@link GraphicalOutputParameters})
   */
  GraphicalOutputObserver(
      List<? extends IGraphicsProvider<? extends IEventRecord>> graphicProviders,
      Collection<IImageProcessor> imageProcessors,
      GraphicalOutputParameters params) {
    this.gps.addAll(graphicProviders);
    this.params = params;
    imgProcessors = imageProcessors;
  }

  /**
   * Helper method for subclassing (allows insertion of more graphics prodivers)
   * 
   * @return (modifiable) list of graphics providers
   */
  protected final List<IGraphicsProvider<? extends IEventRecord>> getGps() {
    return gps;
  }

  private boolean rootShapeInitFailed = false;

  private ImageComposer imageComposer;

  @Override
  protected boolean init(AbstractMLSpaceProcessor<?, R> proc) {
    IShape rootShape = getRootShape(proc);
    if (rootShape == null) {
      if (!rootShapeInitFailed) {
        rootShapeInitFailed = true;
        return false;
      } else {
        ApplicationLogger.log(Level.SEVERE, "No root shape could "
            + "be determined (for frame setup). No graphics.");
        return true;
      }
    } else if (rootShapeInitFailed) {
      rootShapeInitFailed = false;
    }

    fp = createFrameProps(rootShape, params.getMaxWidth(),
        params.getMaxHeight());

    imageComposer = new ImageComposer(fp.getWidth(), fp.getHeight(),
        params.getFadeAlpha(), gps);

    initGraphicProviders(proc);
    initImageProcessors(proc.getTime());
    return true;
  }

  protected FrameProps createFrameProps(IShape rootShape, int maxWidth,
      int maxHeight) {
    return new FrameProps(rootShape, maxWidth, maxHeight);
  }

  protected void initGraphicProviders(AbstractMLSpaceProcessor<?, R> proc) {
    for (IGraphicsProvider<? extends IEventRecord> gp : gps) {
      gp.init(proc, fp);
    }
  }

  private void initImageProcessors(Double time) {
    for (IImageProcessor imgProc : imgProcessors) {
      imgProc.init(fp.getWidth(), fp.getHeight());
    }
    BufferedImage image = imageComposer.getImage();
    notifyImageProcessors(imgProcessors, image, time);
  }

  private static void notifyImageProcessors(
      Collection<IImageProcessor> activeImageProcessors, BufferedImage image,
      Double time) {
    for (IImageProcessor imgProc : activeImageProcessors) {
      imgProc.processImage(image, time);
    }
  }

  /**
   * @param proc
   *          Simulator
   * @return Root shape
   */
  private IShape getRootShape(AbstractMLSpaceProcessor<?, R> proc) {
    IShape rootShape = null;
    for (IGraphicsProvider<? extends IEventRecord> gp : gps) {
      IShape rsCandidate = gp.getRootShape(proc);
      if (rsCandidate != null) {
        if (rootShape == null) {
          rootShape = rsCandidate;
        } else {
          if (!rootShape.equals(rsCandidate)) {
            ApplicationLogger.log(Level.WARNING,
                "More than one " + "potential root shape! Using " + rootShape
                    + "; also found " + rsCandidate);
          }
        }
      }
    }
    return rootShape;
  }

  @Override
  protected void recordEffect(Double time, R effect) {
    if (rootShapeInitFailed) {
      return;
    }
    Collection<IImageProcessor> activeImageProcessors = new LinkedList<>();
    for (IImageProcessor imgProc : imgProcessors) {
      if (imgProc.needsImageAtTime(time)) {
        activeImageProcessors.add(imgProc);
      }
    }
    updateGraphicsProviders(effect);
    if (activeImageProcessors.isEmpty()) {
      return;
    }
    notifyImageProcessors(activeImageProcessors, imageComposer.getImage(),
        time);
  }

  @SuppressWarnings("unchecked")
  private void updateGraphicsProviders(R effect) {
    Iterator<IGraphicsProvider<? extends IEventRecord>> it = gps.iterator();
    while (it.hasNext()) {
      IGraphicsProvider<?> gp = it.next();
      try {
        ((IGraphicsProvider<R>) gp).update(effect);
      } catch (ClassCastException ex) {
        it.remove();
        ApplicationLogger.log(Level.INFO, "Ignoring graphics provider " + gp
            + " as it failed to process " + effect.getClass().getSimpleName());
      }
    }
  }

  @Override
  protected void cleanUp(AbstractMLSpaceProcessor<?, R> proc) {
    if (rootShapeInitFailed) {
      return; // imageComposer == null then
    }
    BufferedImage image = imageComposer.getImage();
    Double time = proc.getTime();
    for (IImageProcessor imgProc : imgProcessors) {
      imgProc.processFinalImageAndCleanUp(image, time);
    }
  }

  /**
   * Container for properties of the frame to draw in: Position and extension of
   * the shape it represents (root shape) as well as actual width and height.
   * Provides convenience methods for converting coordinates from the same
   * system as the root shape to the respective position in the graphics
   * context.
   * 
   * Specific to the geometry package used in ML-Space (for converting
   * coordinates of a {@link IShape shape} to positions in the produced image),
   * but not other aspects of ML-Space.
   * 
   * @author Arne Bittig
   * @date 24.05.2012
   */
  protected static class FrameProps {

    private final double min1, min2, ext1, ext2;

    private final int dimX, dimY;

    private final boolean flipX, flipY;

    private final int xOffset, yOffset;

    private final int width, height;

    private final boolean isPeriodic;

    /** Constructor for internal use (i.e. in enclosing class) */
    FrameProps(IShape rootShape, int maxWidth, int maxHeight) {
      this(rootShape.getMin(1), rootShape.getMin(2),
          2. * rootShape.getMaxExtVector().get(1),
          2. * rootShape.getMaxExtVector().get(2), maxWidth, maxHeight, 1, 2, 0,
          0, rootShape instanceof TorusSurface);
      if (rootShape.getMaxExtVector().getDimensions() != 2) {
        ApplicationLogger.log(Level.SEVERE,
            "Graphical output made only for 2d models!");
      }

    }

    /** Constructor for chaining and subclasses of outer class */
    protected FrameProps(double rootXMin, double rootYMin, double rootXExt,
        double rootYExt, int maxWidth, int maxHeight, int dimX, int dimY,
        int xOffset, int yOffset, boolean isPeriodic) {

      if (rootXExt / rootYExt > (double) maxWidth / maxHeight) {
        width = maxWidth;
        height = (int) (rootYExt * maxWidth / rootXExt);
      } else {
        height = maxHeight;
        width = (int) (rootXExt * maxHeight / rootYExt);
      }
      this.flipX = dimX < 0;
      this.dimX = flipX ? -dimX : dimX;
      this.min1 = rootXMin;
      this.ext1 = rootXExt;
      this.xOffset = xOffset;
      this.flipY = dimY < 0;
      this.dimY = flipY ? -dimY : dimY;
      this.min2 = rootYMin;
      this.ext2 = rootYExt;
      this.yOffset = yOffset;
      this.isPeriodic = isPeriodic;
    }

    int getXCoord(IShape shape) {
      if (flipX) {
        return xOffset + width
            - (int) ((shape.getMax(dimX) - min1) / ext1 * width);
      } else {
        return xOffset + (int) ((shape.getMin(dimX) - min1) / ext1 * width);
      }
    }

    int getYCoord(IShape shape) {
      if (flipY) {
        return yOffset + height
            - (int) ((shape.getMax(dimY) - min2) / ext2 * height);
      } else {
        return yOffset + (int) ((shape.getMin(dimY) - min2) / ext2 * height);
      }

    }

    int getXExt(IShape shape) {
      return (int) (shape.getExtension(dimX) / ext1 * width);
    }

    int getYExt(IShape shape) {
      return (int) (shape.getExtension(dimY) / ext2 * height);
    }

    int getXOffset() {
      return xOffset;
    }

    int getYOffset() {
      return yOffset;
    }

    protected final int getWidth() {
      return width;
    }

    protected final int getHeight() {
      return height;
    }

    final boolean isPeriodic() {
      return isPeriodic;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("x==dim");
      builder.append(dimX);
      builder.append(", y==dim");
      builder.append(dimY);
      builder.append(", width=");
      builder.append(width);
      builder.append(", height=");
      builder.append(height);
      if (xOffset != 0) {
        builder.append(", xOffset=");
        builder.append(xOffset);
      }
      if (yOffset != 0) {
        builder.append(", yOffset=");
        builder.append(yOffset);
      }
      return builder.toString();
    }

  }

  /**
   * Container for parameters of a {@link GraphicalOutputObserver} and their
   * default values. All setters return the enclosing instance to allow
   * chaining. All values that can be set have default values defined, so there
   * is no single value that must be set.
   * 
   * @author Arne Bittig
   */
  public static class GraphicalOutputParameters {

    private static final int DEFAULT_FADE_ALPHA = 96;

    private static final int DEFAULT_HEIGHT = 600;

    private static final int DEFAULT_WIDTH = 900;

    /** window size upper limit */
    private int maxWidth = DEFAULT_WIDTH;

    /** window size upper limit */
    private int maxHeight = DEFAULT_HEIGHT;

    private int fadeAlpha = DEFAULT_FADE_ALPHA;

    /**
     * Default parameters for {@link GraphicalOutputObserver} -- set each
     * parameter individually with the respective setter
     */
    public GraphicalOutputParameters() {
    }

    /**
     * Copy constructor
     * 
     * @param gop
     *          Other {@link GraphicalOutputParameters} object
     */
    public GraphicalOutputParameters(GraphicalOutputParameters gop) {
      this.maxWidth = gop.maxWidth;
      this.maxHeight = gop.maxHeight;
      this.fadeAlpha = gop.fadeAlpha;
    }

    final int getMaxWidth() {
      return maxWidth;
    }

    final int getMaxHeight() {
      return maxHeight;
    }

    final int getFadeAlpha() {
      return fadeAlpha;
    }

    /**
     * @param maxWidth
     *          maximum frame width (actual frame width will be lower if the
     *          aspect ratio of the content is lower than the ratio of maxWidth
     *          and maxWidth)
     * @return this object for setter chaining
     */
    public final GraphicalOutputParameters setMaxWidth(int maxWidth) {
      this.maxWidth = maxWidth;
      return this;
    }

    /**
     * @param maxHeight
     *          maximum frame height (actual frame height will be lower if the
     *          aspect ratio of the content is higher than the ratio of maxWidth
     *          and maxWidth)
     * @return this object for setter chaining
     */
    public final GraphicalOutputParameters setMaxHeight(int maxHeight) {
      this.maxHeight = maxHeight;
      return this;
    }

    /**
     * @param fadeAlpha
     *          Fading factor for previous states (0 -- instant deletion; 255 --
     *          show full trace, no fading; setting is ignored if any part of
     *          the visualisation is space-filling)
     * @return this object for setter chaining
     */
    public final GraphicalOutputParameters setFadeAlpha(int fadeAlpha) {
      this.fadeAlpha = fadeAlpha;
      return this;
    }
  }

  private static class ImageComposer implements Serializable {

    private static final long serialVersionUID = -3801671755736732543L;

    private final List<? extends IGraphicsProvider<?>> gps;

    private final Map<IGraphicsProvider<?>, Image> images;

    private final int width, height;

    private final AlphaComposite fadeComposite;

    /**
     * @param height
     * @param width
     * @param gps
     */
    ImageComposer(int width, int height, int fadeAlpha,
        List<? extends IGraphicsProvider<?>> gps) {
      this.width = width;
      this.height = height;
      this.gps = gps;
      this.images = new LinkedHashMap<>(gps.size());
      this.fadeComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
          1f / 256 * fadeAlpha);
    }

    BufferedImage getImage() {
      BufferedImage img =
          new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
      Graphics2D imgGraphics = (Graphics2D) img.getGraphics();
      imgGraphics.setBackground(Color.WHITE);
      imgGraphics.clearRect(0, 0, width, height);

      for (IGraphicsProvider<?> gp : gps) {
        Image image =
            new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D gImg = (Graphics2D) image.getGraphics();
        gp.paint(gImg);
        if (!gp.isFilling()) {
          if (images.containsKey(gp)) {
            gImg.setComposite(fadeComposite);
            gImg.drawImage(images.get(gp), 0, 0, null);
          }
          images.put(gp, image);
        }
        // g.setComposite(AlphaComposite.SrcOver);
        imgGraphics.drawImage(image, 0, 0, null);
      }
      return img;
    }
  }
}