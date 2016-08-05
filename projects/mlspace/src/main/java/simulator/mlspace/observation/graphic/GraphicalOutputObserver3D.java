/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.TorusSurface;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Pair;

import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.eventrecord.IEventRecord;
import simulator.mlspace.observation.graphic.output.IImageProcessor;
import simulator.mlspace.util.Predicate;

/**
 * @author Arne Bittig
 * @param <R>
 * @date 18.10.2012
 */
public class GraphicalOutputObserver3D<R extends IEventRecord> extends
    GraphicalOutputObserver<R> {

  private final Map<IGraphicsProvider<? extends IEventRecord>, FrameProps> gps3D =
      new LinkedHashMap<>();

  private IShape rootShape;

  private boolean periodic;

  private int frameWidth;

  private int frameHeight;

  /**
   * Graphical output observer relying on given {@link IGraphicsProvider}s and
   * given parameters.
   * 
   * @param gps
   *          {@link IGraphicsProvider}s to use (in given order)
   * @param imageProcessors
   *          Image processors to pass composed graphics to
   * @param params
   *          Further parameters (see
   *          {@link GraphicalOutputObserver.GraphicalOutputParameters})
   */
  public GraphicalOutputObserver3D(
      List<? extends IGraphicsProvider<? extends IEventRecord>> gps,
      Collection<IImageProcessor> imageProcessors,
      GraphicalOutputParameters params) {
    super(gps, imageProcessors, params);
  }

  @Override
  protected FrameProps createFrameProps(IShape initRootShape, int maxWidth,
      int maxHeight) {
    if (initRootShape.getCenter().getDimensions() != 3) {
      throw new IllegalStateException("3D graphical output attempted with "
          + initRootShape.getCenter().getDimensions() + " model.");
    }
    this.rootShape = initRootShape;
    this.periodic = initRootShape instanceof TorusSurface;
    if (periodic) {
      ApplicationLogger.log(Level.INFO, "3D (projection) output with periodic"
          + " boundaries may look a little strange around the edges.");
    }
    IDisplacementVector extVect = initRootShape.getMaxExtVector();
    double xTot = 2 * (extVect.get(1) + extVect.get(3));
    double yTot = 2 * (extVect.get(2) + extVect.get(3));
    FrameProps fp =
        new FrameProps(0, 0, xTot, yTot, maxWidth, maxHeight, 0, 0, 0, 0,
            periodic);
    this.frameWidth = fp.getWidth();
    this.frameHeight = fp.getHeight();
    return fp;
  }

  private Pair<FrameProps[], Predicate<IShapedComponent>[]> createFramePropsFor3D() {
    FrameProps[] fps = new FrameProps[3];
    @SuppressWarnings("unchecked")
    Predicate<IShapedComponent>[] filters = new Predicate[3];

    IDisplacementVector extVect = rootShape.getMaxExtVector();
    final double xMin = rootShape.getMin(1);
    final double yMin = rootShape.getMin(2);
    final double zMin = rootShape.getMin(3);
    double xExt = 2 * extVect.get(1);
    double yExt = 2 * extVect.get(2);
    double zExt = 2 * extVect.get(3);
    int oriViewWidth = (int) (xExt / (xExt + zExt) * frameWidth);
    int oriViewHeight = (int) (yExt / (yExt + zExt) * frameHeight);

    fps[0] =
        new FrameProps(xMin, yMin, xExt, yExt, oriViewWidth, oriViewHeight, 1,
            2, 0, frameHeight - oriViewHeight, periodic);
    fps[1] =
        new FrameProps(zMin, yMin, zExt, yExt, frameWidth - oriViewWidth,
            oriViewHeight, 3, 2, oriViewWidth, frameHeight - oriViewHeight,
            periodic);
    fps[2] =
        new FrameProps(xMin, zMin, xExt, zExt, oriViewWidth, frameHeight
            - oriViewHeight, 1, -3, 0, 0, periodic);

    filters[0] = new Predicate<IShapedComponent>() {

      @Override
      public boolean apply(IShapedComponent sc) {
        return doubleEquals(sc.getShape().getMin(3), zMin);
      }
    };
    final double xMax = rootShape.getMax(1);
    filters[1] = new Predicate<IShapedComponent>() {

      @Override
      public boolean apply(IShapedComponent sc) {
        return doubleEquals(sc.getShape().getMax(1), xMax);
      }
    };
    filters[2] = new Predicate<IShapedComponent>() {

      @Override
      public boolean apply(IShapedComponent sc) {
        return doubleEquals(sc.getShape().getMin(2), yMin);
      }
    };

    return new Pair<>(fps, filters);
  }

  private static final double DELTA = 1e-12;

  // TODO: use guava's fuzzyEquals ?!
  static boolean doubleEquals(double d1, double d2) {
    double diff = d1 - d2;
    return diff < 0 ? -DELTA < diff : diff < DELTA;

  }

  @Override
  protected void initGraphicProviders(AbstractMLSpaceProcessor<?, R> proc) {
    if (gps3D.isEmpty()) {
      init3DGraphics();
    }
    for (Map.Entry<IGraphicsProvider<? extends IEventRecord>, FrameProps> gpe : gps3D
        .entrySet()) {
      gpe.getKey().init(proc, gpe.getValue());
    }
  }

  private void init3DGraphics() {
    ListIterator<IGraphicsProvider<? extends IEventRecord>> gpIt =
        getGps().listIterator();
    Pair<FrameProps[], Predicate<IShapedComponent>[]> pair3D =
        createFramePropsFor3D();
    FrameProps[] fpsFor3D = pair3D.getFirstValue();
    Predicate<IShapedComponent>[] filters = pair3D.getSecondValue();
    while (gpIt.hasNext()) {

      IGraphicsProvider<? extends IEventRecord> gp = gpIt.next();
      // if (gp instanceof GraphicsForComps) {
      gpIt.remove();
      // IGraphicsProvider<?> gpc = (GraphicsForComps) gp;
      // for (FrameProps fp3D : fpsFor3D) {
      // IGraphicsProvider<?> copy = gp.copy();
      // gpIt.add(copy);
      // gps3D.put(copy, fp3D);
      // }
      // } else {
      // ApplicationLogger.log(Level.SEVERE, "No provisions for "
      // + gp.getClass().getSimpleName()
      // + " in 3d. Using flat x-y view only, "
      // + "and even that one may look strange.");
      // gps3D.put(gp, fpsFor3D[0]);
      // }
      for (int i = 0; i < 3; i++) {
        IGraphicsProvider<?> copy = gp.copy();
        gpIt.add(copy);
        gps3D.put(copy, fpsFor3D[i]);
        if (copy instanceof GraphicsForSubvols) {
          ((GraphicsForSubvols) copy).setSvFilter(filters[i]);
        }

      }
    }
  }
}
