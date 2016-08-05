/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.spatial;

import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.math.geometry.IModifiableShapedComponent;
import org.jamesii.core.math.geometry.shapes.IModifiableShape;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.Vectors;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.Species;

/**
 * A spatial entity is a spatial model entity with shape and size (extension).
 * 
 * They may contain other spatial entities (or dimensionless entities, handled
 * elsewhere), and may themselves be part of reactions like other entities.
 * 
 * @author Arne Bittig
 */
public abstract class SpatialEntity extends AbstractModelEntity
    implements IMoveableEntity, IModifiableShapedComponent {

  /** Serialization ID */
  private static final long serialVersionUID = 9122649997846761041L;

  /** shape of the compartment */
  private final IModifiableShape shape;

  /** parent, i.e. compartment that contains this one; null for root */
  private SpatialEntity enclosingEntity;

  /**
   * Full constructor with attribute values given within an entity instance
   * 
   * @param shape
   *          Shape of the compartment
   * @param spec
   *          Species of the compartment
   * @param attributes
   *          Attributes (name->values map)
   * @param enclosingEntity
   *          SpatialEntity this one is situated in (null if none)
   */
  public SpatialEntity(IModifiableShape shape, Species spec,
      Map<String, Object> attributes, SpatialEntity enclosingEntity) {
    super(spec, attributes);
    this.shape = shape;
    this.enclosingEntity = enclosingEntity;
  }

  /**
   * Does the spatial entity have hard boundaries or soft ones? (Collision of
   * entities with hard boundaries must be resolved and may trigger reactions,
   * collision of a soft and a hard bounded entity may result in overlap.
   * Collisions between soft boundary entities are rather tricky, but only
   * because of unclear implication for hard-bounded entities on the potential
   * overlap of two soft entities.)
   * 
   * @return true if entity's boundaries are to be considered hard (see above
   *         and implementing classes' comments for details)
   */
  public abstract boolean isHardBounded();

  @Override
  public IShape getShape() {
    return shape;
  }

  @Override
  public SpatialEntity getEnclosingEntity() {
    return enclosingEntity;
  }

  /**
   * Set the enclosing compartment (formerly known as parent comp)
   * 
   * 
   * @param parent
   *          New parent comp
   */
  public void setEnclosingEntity(SpatialEntity parent) {
    this.enclosingEntity = parent;
  }

  @Override
  public Object getAttribute(String attName) {
    SpatialAttribute spa = SpatialAttribute.forName(attName);
    if (spa == null) {
      return super.getAttribute(attName);
    }
    switch (spa) {
    case DIFFUSION:
      return getDiffusionConstant();
    case SIZE:
      return shape.getSize();
    case SHAPE:
      return shape.getClass().getSimpleName();
    case DRIFT:
      return getDrift();
    // case BOUNDARIES:
    // return hasSoftBoundaries();
    case POSITION: // position attribute should only be accessed this
      // way by an observer (which only needs a string
      // representation)
      return getPosition().toString();
    case VELOCITY:
      IDisplacementVector drift = getDrift();
      return drift == null ? 0. : drift.length();
    case DIRECTION:
      IDisplacementVector drift2 = getDrift();
      return drift2 == null ? null
          : Vectors.getAnsoluteAngle2d(drift2.toArray());
    default:
      throw new IllegalStateException();
      // return super.getAttribute(attName);
    }
  }

  private static boolean direction3DWarningDisplayed = false;

  @Override
  public Object setAttribute(String name, Object value) {
    if (name.equals(SpatialAttribute.VELOCITY.toString())) {
      IDisplacementVector drift = getDrift();
      double length = drift.length();
      if (length == 0.) {
        drift.set(1, (Double) value);
      } else {
        drift.scale((Double) value / length);
      }
      return length;
    } else if (name.equals(SpatialAttribute.DIRECTION.toString())) {
      IDisplacementVector drift = getDrift();
      if (drift.getDimensions() != 2) {
        if (!direction3DWarningDisplayed) {
          SimSystem.report(Level.SEVERE,
              "Direction change for 3D entity!\n"
                  + "Using current \"phi\" attribute value and "
                  + "given direction as \"theta\" in spherical coordinates.");
          direction3DWarningDisplayed = true;
        }
        double[] oldDir = Vectors.cartesianToSpherical(drift.toArray());
        Vectors.rotate3dToAngle(drift, (Double) value,
            (Double) this.getAttribute(
                SpatialAttribute.SPERICAL_COORDINATES_ADDITIONAL_ATT_NAME));
        return oldDir;
      }
      return Vectors.rotate2dToAngle(drift, (Double) value);
    } else {
      return super.setAttribute(name, value);
    }
  }

  @Override
  public IPositionVector getPosition() {
    return shape.getCenter();
  }

  /**
   * {@inheritDoc}. Returns null if drift attribute is not defined. This should
   * be treated like the null vector.
   */
  @Override
  public IDisplacementVector getDrift() {
    IDisplacementVector drift = (IDisplacementVector) getAttributes()
        .get(SpatialAttribute.DRIFT.toString());
    return drift;
  }

  @Override
  public void move(IDisplacementVector disp) {
    shape.move(disp);
  }

  @Override
  public void moveAlongDimTo(int dim, double coord) {
    shape.getCenterForModification().set(dim, coord);
  }

  /**
   * 
   * @return Short string representation of species and Object's hash
   *         code/memory address for (hopefully) unique identification
   */
  public String idString() {
    return this.getSpecies().toString() + " ("
        + Integer.toHexString(System.identityHashCode(this)) + ")";
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(super.toString());
    stringBuilder.append('_');
    stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    // stringBuilder.append('-s');
    if (shape == null) {
      stringBuilder.append(" (unknown position)");
    } else {
      stringBuilder.append(" at ");
      stringBuilder.append(shape.getCenter().toString());
    }
    if (this.enclosingEntity != null) {
      stringBuilder.append(" in ");
      stringBuilder.append(this.enclosingEntity.getSpecies());
    }
    return stringBuilder.toString();
  }
}