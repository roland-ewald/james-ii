/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.core.math.geometry.GeoUtils;
import org.jamesii.core.math.geometry.shapes.IShape;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.subvols.ISubvol;
import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.eventrecord.ISubvolEventRecord;
import simulator.mlspace.observation.graphic.GraphicalOutputObserver.FrameProps;
import simulator.mlspace.util.Predicate;

/**
 * Container of graphical representations of {@link ISubvol Subvolumes}.
 *
 * @author Arne Bittig
 */
public class GraphicsForSubvols
    implements IGraphicsProvider<ISubvolEventRecord> {

  /** {@link GraphicObj} to draw for each {@link ISubvol} */
  private final Map<ISubvol, GraphicObj> svGraphics = new HashMap<>();

  /**
   * Color provider for drawing sv boundaries corresponding to enclosing
   * compartments
   */
  private final IColorProvider<AbstractModelEntity> compColProv;

  /** Stroke (incl., e.g., line width) for ISubvol boundaries */
  private final Stroke boundStroke;

  private final int xBlocks, yBlocks;

  /** Saturation scaling factor, 1 by "amount needed for max sat" */
  private final float satScal;

  /** Default color for drawing sv boundaries if not given by enclosing comp */
  static final Color DEFAULT_BOUND_COLOR = Color.BLACK;

  /** Color for subvols (or subblocks) containing nothing */
  static final Color COLOR_FOR_NOTHING = Color.WHITE;

  private Predicate<? super ISubvol> svFilter = new Predicate.True<>();

  /**
   * 
   * @param boundStroke
   *          Stroke to draw subvol boundaries with
   * @param compColorProvider
   *          SpatialEntity-to-Color mapper
   * @param blocks
   *          Subdivision of subvol viz for different entity types in x- and
   *          y-direction
   * @param amountForMaxIntensity
   *          Amount of entities threshold for max color intensity
   */
  public GraphicsForSubvols(Stroke boundStroke,
      final IColorProvider<AbstractModelEntity> compColorProvider, int[] blocks,
      int amountForMaxIntensity) {
    this(boundStroke, blocks[0], blocks[1], 1f / amountForMaxIntensity,
        getOrCreateCompColProvider(compColorProvider));
  }

  private static IColorProvider<AbstractModelEntity> getOrCreateCompColProvider(
      final IColorProvider<AbstractModelEntity> compColorProvider) {
    if (compColorProvider == null) {
      return new IColorProvider<AbstractModelEntity>() {
        @Override
        public Color getColor(AbstractModelEntity obj) {
          return DEFAULT_BOUND_COLOR;
        }
      };
    }

    return new IColorProvider<AbstractModelEntity>() {
      @Override
      public Color getColor(AbstractModelEntity obj) {
        if (obj == null) {
          return DEFAULT_BOUND_COLOR;
        }
        return compColorProvider.getColor(obj);
      }
    };
  }

  /**
   * Constructor for internal use (chaining and {@link #copy()}ing)
   * 
   * @param boundStroke
   * @param x
   * @param y
   * @param saturationScalingFactor
   * @param compColProvider
   */
  private GraphicsForSubvols(Stroke boundStroke, int x, int y,
      float saturationScalingFactor,
      IColorProvider<AbstractModelEntity> compColProvider) {
    this.boundStroke = boundStroke;
    this.xBlocks = x;
    this.yBlocks = y;
    this.satScal = saturationScalingFactor;
    this.compColProv = compColProvider;
  }

  @Override
  public boolean isFilling() {
    return true;
  }

  @Override
  public IShape getRootShape(AbstractMLSpaceProcessor<?, ?> proc) {
    if (proc.getSpatialEntities() != null
        && !proc.getSpatialEntities().isEmpty()) {
      return null; // rely on comp graphics provider to return root shape
    }
    return GeoUtils.surroundingBox(proc.getSubvols()); // may be null...
  }

  @Override
  public void init(AbstractMLSpaceProcessor<?, ?> proc, FrameProps frameProps) {
    svGraphics.clear();
    for (ISubvol sv : proc.getSubvols()) {
      if (svFilter.apply(sv)) {
        svGraphics.put(sv, new GraphicObj(sv, frameProps));
      }
    }
  }

  @Override
  public void update(ISubvolEventRecord effect) {
    for (ISubvol sv : effect.getSubvolChanges().keySet()) {
      if (svFilter.apply(sv)) {
        // CHECK: record whether we get here at all during update call?
        // (for later check whether image to be generated has changed)
        svGraphics.get(sv).changed();
      }
    }
  }

  @Override
  public void paint(Graphics2D g2d) {
    boolean drawBoundary = boundStroke instanceof BasicStroke
        && ((BasicStroke) boundStroke).getLineWidth() > 0.;
    if (drawBoundary) {
      g2d.setStroke(boundStroke);
    }
    for (GraphicObj go : svGraphics.values()) {
      go.draw(g2d, drawBoundary);
    }
  }

  @Override
  public GraphicsForSubvols copy() {
    return new GraphicsForSubvols(boundStroke, xBlocks, yBlocks, satScal,
        compColProv);
  }

  /**
   * @param svFilter
   *          Predicate to indicate svs to be shown
   */
  void setSvFilter(Predicate<? super ISubvol> svFilter) {
    this.svFilter = svFilter;
  }

  Color getCompCol(SpatialEntity comp) {
    return compColProv.getColor(comp);
  }

  /**
   * @return the xBlocks
   */
  int getXBlocks() {
    return xBlocks;
  }

  int getYBlocks() {
    return yBlocks;
  }

  float getSatScaling() {
    return satScal;
  }

  private class GraphicObj {

    /** Subvol this obj belongs to */
    private final ISubvol sv;

    /** Coordinates where to draw */
    private final int x, y, width, height;

    private boolean changed;

    private final Color[][] colors = new Color[getXBlocks()][getYBlocks()];

    /**
     * GraphicObj for given {@link ISubvol subvolume}, with given boundary
     * bColor, in frame of given properties
     * 
     * @param subvol
     * @param fp
     */
    GraphicObj(ISubvol subvol, FrameProps fp) {
      this.sv = subvol;
      IShape shape = subvol.getShape();
      this.x = fp.getXCoord(shape);
      this.y = fp.getYCoord(shape);
      this.width = fp.getXExt(shape);
      this.height = fp.getYExt(shape);
      this.changed = true;
    }

    void changed() {
      this.changed = true;
    }

    private void updateColors() {
      Set<Entry<NSMEntity, Integer>> svState = sv.getState().entrySet();
      Map.Entry<NSMEntity, Integer>[] svSArr =
          svState.toArray(new Map.Entry[svState.size()]);
      Arrays.sort(svSArr, INT_VAL_ENTRY_COMPARATOR);
      int iarr = 0;
      for (int ix = 0; ix < getXBlocks(); ix++) {
        for (int iy = 0; iy < getYBlocks(); iy++) {
          if (iarr == svSArr.length) {
            colors[ix][iy] = COLOR_FOR_NOTHING;
            continue;
          }
          float sat = Math.min(1f, getSatScaling() * svSArr[iarr].getValue());
          NSMEntity nsmEntity = svSArr[iarr].getKey();
          float hue = getHue(compColProv.getColor(nsmEntity));
          colors[ix][iy] = Color.getHSBColor(hue, sat, 1f);
          iarr++;
        }
      }
    }

    private float getHue(Color color) {
      return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(),
          null)[0];
    }

    /**
     * Attempt to get different color values for similar NSM entities (i.e. with
     * hash code codes differing only little)
     * 
     * @param entity
     *          Entity to calculate float value in [0,1) for
     * @return value in [0,1)
     */
    private float getHueFromHash(Object entity) {
      int entHashCode = entity.hashCode();
      final int secondLargestPrimeOffset = 18;
      // 2^31-1 is Integer.MAX_VALUE, and according to
      // http://primes.utm.edu/lists/2small/0bit.html, 2^31-1 and 2^31-19
      // are the two largest primes < 2^31
      final int largePrime = Integer.MAX_VALUE - secondLargestPrimeOffset;
      return ((float) (entHashCode * largePrime) / Integer.MAX_VALUE + 1f) / 2f;
    }

    /**
     * Draw object at its defined position and at other positions required by
     * periodic boundaries, if necessary
     * 
     * @param g
     * @param drawBoundary
     */
    void draw(Graphics2D g, boolean drawBoundary) {
      if (changed) {
        updateColors();
      }
      int stepX = width / getXBlocks();
      int stepY = height / getYBlocks();
      for (int ix = 0; ix < getXBlocks(); ix++) {
        int lowerX = ix * width / getXBlocks();
        for (int iy = 0; iy < getYBlocks(); iy++) {
          g.setColor(colors[ix][iy]);
          g.fillRect(x + lowerX, y + iy * height / getYBlocks(), stepX, stepY);
        }
      }
      if (drawBoundary) {
        g.setColor(getCompCol(sv.getEnclosingEntity()));
        g.drawRect(x, y, width, height);
      }
    }
  }

  static final MapEntryByDescendingValueComparator<Integer> INT_VAL_ENTRY_COMPARATOR =
      new MapEntryByDescendingValueComparator<>();

  static class MapEntryByDescendingValueComparator<V extends Comparable<V>>
      implements Comparator<Map.Entry<?, ? extends V>>, java.io.Serializable {

    private static final long serialVersionUID = 859025542438072486L;

    @Override
    public int compare(Map.Entry<?, ? extends V> o1,
        Map.Entry<?, ? extends V> o2) {
      return -o1.getValue().compareTo(o2.getValue());
    }
  }
}