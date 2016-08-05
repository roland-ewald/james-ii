/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jamesii.core.math.geometry.GeoUtils;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.Sphere;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.eventrecord.IContSpaceEventRecord;
import simulator.mlspace.eventrecord.IContSpaceEventRecord.ICompChange;
import simulator.mlspace.eventrecord.IContSpaceEventRecord.ICompChange.Type;
import simulator.mlspace.observation.graphic.GraphicalOutputObserver.FrameProps;

/**
 * Container of graphical representations of {@link SpatialEntity}s.
 *
 * @author Arne Bittig
 */
public class GraphicsForComps
    implements IGraphicsProvider<IContSpaceEventRecord> {

  /** {@link GraphicObj} to draw for each {@link SpatialEntity} */
  private final Map<SpatialEntity, GraphicObj> compGraphics =
      new ConcurrentHashMap<>();

  private FrameProps fp;

  /** Stroke (incl., e.g., line width) for compartment & region boundaries */
  private final Stroke compStroke;

  private final Stroke regionStroke;

  private final IColorProvider<AbstractModelEntity> colorProvider;

  /**
   * {@link simulator.mlspace.observation.graphic.GraphicalOutputObserver}
   * specific panel that uses given transparency values
   * 
   * @param compBoundStroke
   *          Stroke to draw comp boundaries with
   * @param regionBoundStroke
   *          Stroke to draw region boundaries with
   * @param colorProvider
   *          Method for assigning colors to entities to be drawn
   */
  public GraphicsForComps(Stroke compBoundStroke, Stroke regionBoundStroke,
      IColorProvider<AbstractModelEntity> colorProvider) {
    this.compStroke = compBoundStroke;
    this.regionStroke = regionBoundStroke;
    this.colorProvider = colorProvider;
  }

  @Override
  public boolean isFilling() {
    return false;
  }

  @Override
  public IShape getRootShape(AbstractMLSpaceProcessor<?, ?> proc) {
    Collection<SpatialEntity> ctRoots = proc.getSpatialEntities().getRoots();
    switch (ctRoots.size()) {
    case 0:
      return null;
    case 1:
      return ctRoots.iterator().next().getShape();
    default:
      return GeoUtils.surroundingBox(ctRoots);
    }
  }

  @Override
  public void init(AbstractMLSpaceProcessor<?, ?> proc, FrameProps frameProps) {
    this.fp = frameProps;
    compGraphics.clear();
    for (SpatialEntity comp : proc.getSpatialEntities().getAllNodes()) {
      compGraphics.put(comp,
          new GraphicObj(comp.getShape(), colorProvider.getColor(comp), fp));
    }
  }

  @Override
  public void update(IContSpaceEventRecord effect) {
    if (!effect.getState().isSuccess()) {
      return;
    }
    for (Map.Entry<SpatialEntity, ICompChange> e : effect.getAllCompChanges()
        .entrySet()) {
      if (e.getValue().getType().getPriority() >= Type.DESTROYED.getPriority()
      // == ContSpaceEventRecord.DESTROYED
      ) {
        compGraphics.remove(e.getKey());
      } else {
        SpatialEntity comp = e.getKey();
        compGraphics.put(comp,
            new GraphicObj(comp.getShape(), colorProvider.getColor(comp), fp));
      }
    }
  }

  @Override
  public void paint(Graphics2D g2d) {
    for (Map.Entry<SpatialEntity, GraphicObj> e : compGraphics.entrySet()) {
      if (e.getKey().isHardBounded()) {
        g2d.setStroke(compStroke);
      } else {
        g2d.setStroke(regionStroke);
      }

      GraphicObj go = e.getValue();
      go.drawWithOffset(g2d, 0, 0);
      if (fp.isPeriodic()) {
        if (go.getX() < 0) {
          go.drawWithOffset(g2d, fp.getWidth(), 0);
        } else
          if (go.getX() + go.getWidth() > fp.getWidth() + fp.getXOffset()) {
          go.drawWithOffset(g2d, -fp.getWidth(), 0);
        }
        if (go.getY() < 0) {
          go.drawWithOffset(g2d, 0, fp.getHeight());
        } else
          if (go.getY() + go.getHeight() > fp.getHeight() + fp.getYOffset()) {
          go.drawWithOffset(g2d, 0, -fp.getHeight());
        }
        // TODO: combination cases (comp in corner!)
      }
    }
  }

  /**
   * Copy comp graphics provider (e.g. to draw different view of 3D system at
   * different position)
   * 
   * @return Comp graphics provider
   */
  @Override
  public GraphicsForComps copy() {
    return new GraphicsForComps(compStroke, regionStroke, colorProvider);
  }

  private static class GraphicObj {

    /** Flag whether to draw an oval (a circle) or a rectangle */
    private final boolean isOval;

    /** Coordinates where to draw */
    private final int x, y, width, height;

    /** Color to set before drawing */
    private final Color color;

    /**
     * GraphicObj determined from shape with given color
     * 
     * @param shape
     * @param color
     */
    GraphicObj(IShape shape, Color color, FrameProps fp) {
      this.isOval = shape instanceof Sphere;
      this.x = fp.getXCoord(shape);
      this.y = fp.getYCoord(shape);
      this.width = fp.getXExt(shape);
      this.height = fp.getYExt(shape);
      this.color = color;
    }

    /**
     * Draw object at its defined position and at other positions required by
     * periodic boundaries, if necessary
     * 
     * @param g
     */
    void drawWithOffset(Graphics2D g, int xOff, int yOff) {
      g.setColor(color);
      if (isOval) {
        g.drawOval(x + xOff, y + yOff, width, height);
      } else {
        g.drawRect(x + xOff, y + yOff, width, height);
      }
    }

    final int getX() {
      return x;
    }

    final int getY() {
      return y;
    }

    final int getWidth() {
      return width;
    }

    final int getHeight() {
      return height;
    }

    @Override
    public String toString() {
      return (isOval ? "oval" : "rect") + " from (" + x + ',' + y + ") ext ("
          + width + ',' + height + ") in " + color;
    }
  }
}