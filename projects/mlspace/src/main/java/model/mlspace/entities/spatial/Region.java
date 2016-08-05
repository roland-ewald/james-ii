/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.spatial;

import java.util.Map;

import org.jamesii.core.math.geometry.shapes.IModifiableShape;

import model.mlspace.entities.Species;

/**
 * Regions are spatial entities that differ from {@link Compartment
 * compartments} in that they have soft boundaries, i.e. other spatial entities
 * can overlap their boundary (although regions overlapping regions may be
 * disallowed). They can also be used to cover properties of the simulation
 * space not directly related to the other model entities.
 * 
 * 
 * @author Arne Bittig
 * @date 28.06.2012 (date of separation of Compartment and Region)
 */
public class Region extends SpatialEntity {

  private static final long serialVersionUID = 6189136995239633829L;

  /**
   * @param shape
   *          Shape of the compartment
   * @param spec
   *          Species of the compartment
   * @param attributes
   *          Attributes (name->values map)
   * @param enclosingEntity
   *          SpatialEntity this one is situated in (null if none)
   */
  public Region(IModifiableShape shape, Species spec,
      Map<String, Object> attributes, SpatialEntity enclosingEntity) {
    super(shape, spec, attributes, enclosingEntity);
  }

  @Override
  public boolean isHardBounded() {
    return false;
  }

}
