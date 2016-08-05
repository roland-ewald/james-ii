/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.shapes;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Vectors;

/**
 * Interface providing factory methods for creating shapes (e.g. for use in a
 * {@link java.util.Map}<String,ShapeCreator> linking shape-identifying keywords
 * with the respective factory method).
 *
 * @author Arne Bittig
 * @param <S>
 *          Type of returned shape
 * @date 16.01.2014
 */
public interface ShapeCreator<S extends IShape> {

  /**
   * Create shape
   *
   * @param center
   *          Coordinates/position of center
   * @param size
   *          Size
   * @param vecFac
   *          Vector factory for creation of additional relevant points
   * @param furtherInfo
   *          Additional needed information, if applicable (e.g. aspect ratio)
   * @return Shape
   */
  S create(IPositionVector center, Double size, IVectorFactory vecFac,
      IDisplacementVector... furtherInfo);

  public static class CuboidCreator implements ShapeCreator<AxisAlignedBox> {

    @Override
    public AxisAlignedBox create(IPositionVector center, Double size,
        IVectorFactory vecFac, IDisplacementVector... furtherInfo) {
      AxisAlignedBox box = new AxisAlignedBox(center, furtherInfo[0]);
      box.scaleToSize(size);
      return box;
    }
  }

  public static class CubeCreator implements ShapeCreator<AxisAlignedBox> {

    @Override
    public AxisAlignedBox create(IPositionVector center, Double size,
        IVectorFactory vecFac, IDisplacementVector... furtherInfo) {
      AxisAlignedBox box =
          new AxisAlignedBox(center, Vectors.allOnesVector(vecFac));
      box.scaleToSize(size);
      return box;
    }
  }

  public static class SphereCreator implements ShapeCreator<Sphere> {

    @Override
    public Sphere create(IPositionVector center, Double size,
        IVectorFactory vecFac, IDisplacementVector... furtherInfo) {
      Sphere sphere = new Sphere(center, 1.);
      sphere.scaleToSize(size);
      return sphere;
    }
  }

  public static class TorusCreator implements ShapeCreator<TorusSurface> {

    @Override
    public TorusSurface create(IPositionVector center, Double size,
        IVectorFactory vecFac, IDisplacementVector... furtherInfo) {
      assert vecFac.getPeriod() != null;
      assert vecFac.getPeriod().times(0.5).isEqualTo(center);
      if (furtherInfo.length > 0) {
        assert Vectors.getScalingFactor(furtherInfo[0].toArray(), vecFac
            .getPeriod().toArray(), 1e-10) != null;
      }
      return new TorusSurface(vecFac);
    }
  }

}
