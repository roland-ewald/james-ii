/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.collision;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.geometry.GeoUtils;
import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.ShapeRelation;
import org.jamesii.core.math.geometry.spatialindex.ISpatialIndex;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Vectors;
import org.jamesii.core.math.random.distributions.IDistribution;
import org.jamesii.core.math.random.generators.IRandom;

import model.mlspace.entities.spatial.Compartment;
import model.mlspace.entities.spatial.SpatialEntity;
import simulator.mlspace.brownianmotion.IPositionUpdater;

/**
 * Place new spatial components and resolve collisions by
 * {@link IShape#dispForTouchOutside(IShape)}. Resolves bindings for entities
 * with multiple binding sites according to pre-defined angles in 2d only!! (and
 * in 3d iff binding site angle is 180°, i.e. for straight alignment).
 * 
 * @author Arne Bittig
 * @date 11.07.2012
 */
public class BindingCollisionResolver
    implements ICollisionResolver<SpatialEntity> {

  private static final long serialVersionUID = -5600372784753960554L;

  private final ISpatialIndex<SpatialEntity> spatialIndex;

  private final IPositionUpdater posUpdater;

  private final IVectorFactory vecFac;

  private final IRandom rand;

  private final double allowedTolerance;

  private IDistribution defaultFirstAngle = null;

  /**
   * Simple collision resolving & shape placement algorithm
   * 
   * @param spatialIndex
   *          Spatial index to use for collision checking
   * @param posUpdater
   *          Position update determination method
   * @param rand
   *          Random number generator
   * @param allowedTolerance
   *          Tolerance for moving colliding shapes farhter apart than actually
   *          calculated (to avoid immediate detection of collision again due to
   *          floating point inaccuracies; 0 for none)
   * @param vecFac
   */
  public BindingCollisionResolver(ISpatialIndex<SpatialEntity> spatialIndex,
      IPositionUpdater posUpdater, IRandom rand, // int maxAttempts,
      double allowedTolerance, IVectorFactory vecFac) {
    this.spatialIndex = spatialIndex;
    this.posUpdater = posUpdater;
    this.vecFac = vecFac;
    this.rand = rand;
    this.allowedTolerance = allowedTolerance;
  }

  /**
   * @param defaultFirstAngle
   *          Fixed angle for first binding (null for none)
   */
  public void setDefaultFirstAngle(IDistribution defaultFirstAngle) {
    this.defaultFirstAngle = defaultFirstAngle;
  }

  @Override
  public boolean placeNewCompNear(SpatialEntity newComp,
      SpatialEntity nearComp) {
    if (nearComp == null) {
      return placeNewCompNear0(newComp);
    }
    if (spatialIndex.getRegisteredPosition(nearComp) == null) {
      return placeNewCompAt(newComp, nearComp);
    }
    return placeNewCompNear1(newComp, nearComp);
  }

  /**
   * Place new comp replacing existing one
   * 
   * @param newComp
   * @param compToReplace
   * @return
   */
  private boolean placeNewCompAt(SpatialEntity newComp,
      SpatialEntity compToReplace) {
    newComp.move(
        newComp.getPosition().displacementTo(compToReplace.getPosition()));
    if (spatialIndex.isOutOfBounds(newComp)) {
      newComp.move(
          spatialIndex.getBoundaries().dispForTouchInside(newComp.getShape()));
    }
    List<SpatialEntity> collidingComps = spatialIndex.collidingComps(newComp);
    GeoUtils.removeEnclosingComps(newComp, collidingComps);
    if (collidingComps.isEmpty()) {
      return true;
    }
    if (collidingComps.size() == 1) {
      IDisplacementVector disp =
          resolveCollision(newComp, collidingComps.get(0));
      if (disp == null) {
        return false;
      }
      newComp.move(disp);
      return true;
    }
    return false;
  }

  /**
   * Place new component without restrictions on which components it should be
   * near/close to
   * 
   * @param compToPlace
   *          Component to place
   * @return success value (if false, compToPlace should not be added to si)
   */
  private boolean placeNewCompNear0(SpatialEntity compToPlace) {
    IShape contShape = compToPlace.getEnclosingEntity().getShape();

    IShape shape = compToPlace.getShape();

    GeoUtils.randomlyPlaceCompInside(compToPlace, contShape, rand);
    // check whether shape is still inside container
    if (contShape.getRelationTo(shape) != ShapeRelation.SUPERSET) {
      return false;
    }
    // out-of-bounds ruled out by superset check (if positions are consistent)
    List<SpatialEntity> collComps = spatialIndex.collidingComps(compToPlace);
    GeoUtils.removeEnclosingComps(compToPlace, collComps);
    return collComps.isEmpty();
  }

  /**
   * Place a new component near (single) given other component without overlap
   * 
   * The component is positioned such that it is close to, possibly touching a
   * given other component. All components shall be in the same enclosing
   * component (as returned by {@link IShapedComponent#getEnclosingEntity()}),
   * in which the newly positioned component shall still be entirely contained.
   * 
   * @param compToPlace
   *          Component to place
   * @param compNear
   *          Component to be next to compToPlace
   * @return success value (if false, compToPlace should not be added to si)
   */
  private boolean placeNewCompNear1(SpatialEntity compToPlace,
      SpatialEntity compNear) {
    // ignores current position of compToPlace
    IShapedComponent parent = compToPlace.getEnclosingEntity();
    if (parent != compNear.getEnclosingEntity()) {
      throw new IllegalArgumentException("Can only place comp near "
          + "another inside the same enclosing entity.");
    }
    IDisplacementVector positionUpdate = vecFac
        .newDisplacementVector(compNear.getShape().getMaxExtVector().toArray());
    Vectors.randomScaling(positionUpdate, rand);
    IDisplacementVector initDisp = compToPlace.getPosition()
        .displacementTo(compNear.getPosition()).plus(positionUpdate);
    compToPlace.move(initDisp);
    IDisplacementVector nonTouchDisp =
        compNear.getShape().dispForTouchOutside(compToPlace.getShape());
    compToPlace.move(initDisp.times(-1.));
    IDisplacementVector combinedDisp =
        posUpdater.adjustUpdateVector(initDisp.plus(nonTouchDisp));
    // posUpdater.adjustUpdateVector(initDisp).plus(posUpdater.adjustUpdateVector(nonTouchDisp));
    compToPlace.move(combinedDisp);
    if (parent.getShape()
        .getRelationTo(compToPlace.getShape()) != ShapeRelation.SUPERSET) {
      // attempt failed (not entirely in parent) => rollback
      compToPlace.move(combinedDisp.times(-1.));
      return false;
    }

    // out-of-bounds fix as in #placeNewCompAt
    // if (spatialIndex.isOutOfBounds(compToPlace)) {
    // compToPlace.move(spatialIndex.getBoundaries().dispForTouchInside(
    // compToPlace.getShape()));
    // }
    // // (uncomment above lines when Ex thrown at line below)

    List<SpatialEntity> collComps = spatialIndex.collidingComps(compToPlace);
    GeoUtils.removeEnclosingComps(compToPlace, collComps);
    if (!collComps.isEmpty()) {
      // attempt failed (collision with other comp) => rollback
      compToPlace.move(combinedDisp.times(-1.));
      return false;
    }
    return true;
  }

  @Override
  public IDisplacementVector resolveCollision(SpatialEntity movingComp,
      SpatialEntity collComp) {
    String bindingSiteOnCollComp =
        getBindingSiteOnCollComp(movingComp, collComp);
    if (bindingSiteOnCollComp == null) {
      return getDisplacementWithoutAngle(movingComp, collComp,
          allowedTolerance);
    } else {
      return getDisplacementForBinding(movingComp, collComp,
          bindingSiteOnCollComp);
    }
  }

  @Override
  public IDisplacementVector resolveCollision(SpatialEntity movingComp,
      SpatialEntity collComp, IDisplacementVector recentMove) {
    String bindingSiteOnCollComp =
        getBindingSiteOnCollComp(movingComp, collComp);
    if (bindingSiteOnCollComp == null) {
      return getDisplacementWithoutAngle(movingComp, collComp, recentMove,
          allowedTolerance);
    } else {
      return getDisplacementForBinding(movingComp, collComp,
          bindingSiteOnCollComp);
    }
  }

  /**
   * Get name of binding site on second entity where the first entity is bound,
   * unless the binding site is free-angle (so collision resolution need not
   * consider binding site angles)
   * 
   * @param movingComp
   *          first spatial entity
   * @param collComp
   *          second spatial entity
   * @return name of a binding site on collComp if there is a fixed-angle
   *         binding site where movingComp is bound, null otherwise
   */
  private static String getBindingSiteOnCollComp(SpatialEntity movingComp,
      SpatialEntity collComp) {
    String bindingSiteOnCollComp = null;
    if (movingComp instanceof Compartment && collComp instanceof Compartment) {
      bindingSiteOnCollComp =
          ((Compartment) collComp).getBindingSite((Compartment) movingComp);
      // (may yield null)
    }
    return bindingSiteOnCollComp;
  }

  private IDisplacementVector getDisplacementForBinding(
      SpatialEntity movingComp, SpatialEntity collComp,
      String bindingSiteOnCollComp) {
    Compartment cc = (Compartment) collComp;
    Map<Compartment, Double> angles =
        cc.getRelativeAnglesToOccupiedSites(bindingSiteOnCollComp);
    if (isSingletonAndStraight(angles.values())) {
      return getDisplacementStraight(movingComp, cc, angles);
    }
    if (angles.isEmpty()) {
      if (defaultFirstAngle == null) {
        // first direction is arbitrary
        return getDisplacementWithoutAngle(movingComp, cc, allowedTolerance);
      } else {
        return getDisplacementWithAbsoluteAngle(movingComp, cc);
      }
    }
    // else if (movingComp.getPosition().getDimensions() != 2) {
    // ApplicationLogger.log(Level.SEVERE,
    // "Cannot handle binding site angles in " // TODO
    // + movingComp.getPosition().getDimensions() + " dimensions.");
    // return getDisplacementWithoutAngle(movingComp, cc, allowedTolerance);}
    return getDisplacementWithRelativeAngle(movingComp, cc, angles);
  }

  /**
   * Check whether collection of (angle) values contains nothing but the
   * straight angle
   * 
   * @param values
   * @return
   */
  private static boolean isSingletonAndStraight(Collection<Double> values) {
    if (values.size() != 1) {
      return false;
    }
    double multOfPi = values.iterator().next() / Math.PI;
    final double delta = 1e-8;
    double diff = multOfPi - (int) multOfPi;
    if (diff < 0) {
      diff += 1.;
    }
    return diff < delta || diff > 1 - delta;
  }

  private static IDisplacementVector getDisplacementWithoutAngle(
      SpatialEntity movingComp, SpatialEntity collComp,
      double allowedTolerance) {
    IDisplacementVector disp =
        collComp.getShape().dispForTouchOutside(movingComp.getShape());
    disp.add(movingComp.getPosition().displacementTo(collComp.getPosition())
        .times(allowedTolerance));
    return disp;
  }

  /**
   * @param movingComp
   * @param collComp
   * @param recentMove
   * @param allowedTolerance
   * @return
   */
  private static IDisplacementVector getDisplacementWithoutAngle(
      SpatialEntity movingComp, SpatialEntity collComp,
      IDisplacementVector recentMove, double allowedTolerance) {
    IDisplacementVector disp;
    if (recentMove.isNullVector()) {
      disp = collComp.getShape().dispForTouchOutside(movingComp.getShape());
    } else {
      disp = collComp.getShape().dispForTouchOutside(movingComp.getShape(),
          recentMove);
    }
    disp.add(movingComp.getPosition().displacementTo(collComp.getPosition())
        .times(allowedTolerance));
    return disp;
  }

  private static IDisplacementVector getDisplacementStraight(
      SpatialEntity movingComp, Compartment collComp,
      Map<Compartment, Double> angles) {
    assert angles.size() == 1;
    Map.Entry<Compartment, Double> entry = angles.entrySet().iterator().next();
    Compartment refComp = entry.getKey();
    // Double angle = entry.getValue(); // 180° or some variation thereof
    IDisplacementVector initDisp =
        movingComp.getPosition().displacementTo(collComp.getPosition());
    initDisp.add(refComp.getPosition().displacementTo(collComp.getPosition()));
    movingComp.move(initDisp);
    IDisplacementVector touchDispAdd =
        collComp.getShape().dispForTouchOutside(movingComp.getShape());
    movingComp.move(initDisp.times(-1));
    return initDisp.plus(touchDispAdd);
  }

  private IDisplacementVector getDisplacementWithRelativeAngle(
      SpatialEntity movingComp, Compartment collComp,
      Map<Compartment, Double> angles) {
    // TODO: 3d case (requires looking at more than just 1 entry)
    Map.Entry<Compartment, Double> e = angles.entrySet().iterator().next();
    double angle = e.getValue();
    IDisplacementVector refDisp =
        collComp.getPosition().displacementTo(e.getKey().getPosition());
    double[] vec = refDisp.toArray();
    double r = Vectors.vecNormEuclid(vec);
    double newAngle = Math.atan2(vec[1], vec[0]) + angle;
    vec[vec.length - 1] = 0; // TODO/CHECK: flat displacement only in 3D
    vec[0] = r * Math.cos(newAngle);
    vec[1] = r * Math.sin(newAngle);
    IDisplacementVector rotDisp = vecFac.newDisplacementVector(vec);

    return getActualRelativeDisplacement(movingComp, collComp, rotDisp);
  }

  /**
   * @param movingComp
   * @param collComp
   * @return
   */
  private IDisplacementVector getDisplacementWithAbsoluteAngle(
      SpatialEntity movingComp, Compartment collComp) {

    double newAngle = defaultFirstAngle.getRandomNumber();
    double[] rotated = new double[vecFac.getDimension()];
    rotated[0] = Math.cos(newAngle);
    rotated[1] = Math.sin(newAngle);
    // TODO: only 2d so far, in 3d displacement is only in flat plane
    IDisplacementVector rot = vecFac.newDisplacementVector(rotated);
    return getActualRelativeDisplacement(movingComp, collComp, rot);
  }

  /**
   * Get non-collision displacement of a moving entity to be placed in given
   * direction of a fixed one
   * 
   * @param movingComp
   * @param fixedComp
   * @param dispDir
   * @return displacement
   */
  private static IDisplacementVector getActualRelativeDisplacement(
      SpatialEntity movingComp, Compartment fixedComp,
      IDisplacementVector dispDir) {
    IDisplacementVector initDisp =
        movingComp.getPosition().displacementTo(fixedComp.getPosition());
    initDisp.add(dispDir);
    movingComp.move(initDisp);
    IDisplacementVector nonCollDisp =
        fixedComp.getShape().dispForTouchOutside(movingComp.getShape());
    movingComp.move(initDisp.times(-1));
    return initDisp.plus(nonCollDisp);
  }
}
