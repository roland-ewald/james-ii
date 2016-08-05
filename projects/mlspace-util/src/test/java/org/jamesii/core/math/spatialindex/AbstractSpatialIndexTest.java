/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.spatialindex;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.shapes.AxisAlignedBox;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.Sphere;
import org.jamesii.core.math.geometry.spatialindex.ISpatialIndex;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;

/**
 * Test spatial index methods
 *
 * @author Arne Bittig
 */
public abstract class AbstractSpatialIndexTest extends TestCase {

  private final IVectorFactory vecFac;

  private ISpatialIndex<SimpleShapedComponent> si;

  /**
   * Test given spatial index (must either cover a large enough area or support
   * lazy initialization--then it will be initialized automatically with a
   * suitable area, if required)
   * 
   * @param vecFac
   *          Factory for 3D vectors
   */
  public AbstractSpatialIndexTest(IVectorFactory vecFac) {
    this.vecFac = vecFac;
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    IShape surrounding =
        new AxisAlignedBox(vecFac.newPositionVector(-4., -2., -3.),
            vecFac.newPositionVector(9., 7., 8.));

    si = setUpSpatialIndex(surrounding);
  }

  /**
   * Create spatial index to test, using given boundaries if boundaries are
   * required
   * 
   * @param surrounding
   *          Boundaries of area to index
   * @return Spatial index
   */
  protected abstract ISpatialIndex<SimpleShapedComponent> setUpSpatialIndex(
      IShape surrounding);

  /**
   * Test shape relation methods for 3D shapes
   */
  public void testCollisions3D() {

    SimpleShapedComponent ws1 =
        new SimpleShapedComponent(new Sphere(vecFac.newPositionVector(-.1, -.2,
            -.3), 1.5));
    si.registerNewEntity(ws1);
    SimpleShapedComponent ws2 =
        new SimpleShapedComponent(new Sphere(vecFac.newPositionVector(2.1, .2,
            .3), 1.3));
    si.registerNewEntity(ws2);
    SimpleShapedComponent ws3 =
        new SimpleShapedComponent(new Sphere(vecFac.newPositionVector(3.5, 4.,
            4.5), 3.));
    si.registerNewEntity(ws3);

    List<SimpleShapedComponent> ws1coll =
        si.collidingComps(ws1, Arrays.asList(ws2, ws3));
    // System.out.println("Overlapping "+ ws1 + ": "
    // + Arrays.toString(ws1coll.toArray()));
    List<SimpleShapedComponent> ws2coll =
        si.collidingComps(ws2, Arrays.asList(ws1, ws3));
    // System.out.println("Overlapping " + ws2 + ": "
    // + Arrays.toString(ws2coll.toArray()));
    assertEquals(ws1coll.size(), ws2coll.size());
    assertEquals(ws1coll.size(), 1);
    assertTrue(ws1coll.contains(ws2));
    assertTrue(ws2coll.contains(ws1));

    SimpleShapedComponent ws4 =
        new SimpleShapedComponent(new AxisAlignedBox(vecFac.newPositionVector(
            1., 0., 0.), vecFac.newPositionVector(6., 5., 4.)));
    List<SimpleShapedComponent> ws4coll =
        si.collidingComps(ws4, Arrays.asList(ws1, ws2, ws3));
    // System.out.println("Overlapping " + ws4 + ": "
    // + Arrays.toString(ws4coll.toArray()));
    assertEquals(ws4coll.size(), 3);
    assertTrue(ws4coll.contains(ws1));
    assertTrue(ws4coll.contains(ws2));
    assertTrue(ws4coll.contains(ws3));
  }

  /**
   * Dummy wrapper class for using {@link IShape}s (almost) directly as
   * {@link IShapedComponent} in {@link ISpatialIndex}.
   * 
   * @author Arne Bittig
   * 
   */
  public static class SimpleShapedComponent implements IShapedComponent {
    private static final long serialVersionUID = 3946050440835707693L;

    private final IShape shape;

    /**
     * @param shape
     *          Shape
     */
    public SimpleShapedComponent(IShape shape) {
      this.shape = shape;
    }

    @Override
    public IPositionVector getPosition() {
      return shape.getCenter();
    }

    @Override
    public IShape getShape() {
      return shape;
    }

    @Override
    public String toString() {
      return shape.toString();
    }

    @Override
    public IShapedComponent getEnclosingEntity() {
      return null;
    }
  }
}
