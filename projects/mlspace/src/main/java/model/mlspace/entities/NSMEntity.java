/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Model entity without shape and extension, to be used exclusively within
 * {@link model.mlspace.subvols.Subvol Subvolumes}, more specifically in subvol
 * {@link model.mlspace.subvols.Subvol#getState() state} vectors.
 * 
 * @author Arne Bittig
 */
public class NSMEntity extends AbstractModelEntity {

  private static final long serialVersionUID = -3961763248098692076L;

  /**
   * Full constructor
   * 
   * @param spec
   * @param attributes
   */
  public NSMEntity(Species spec, Map<String, Object> attributes) {
    super(spec, attributes);
  }

  /**
   * Copy constructor
   * 
   * @param e
   */
  public NSMEntity(NSMEntity e) {
    super(e.getSpecies(), new LinkedHashMap<>(e.getAttributes()));
  }

  @Override
  public int hashCode() {
    final int prime = 23;
    return prime * getAttributesHashCode() + getSpecies().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    return sameClassEquals((NSMEntity) obj);
  }

  private boolean sameClassEquals(NSMEntity ent2) {
    return this.getSpecies().equals(ent2.getSpecies())
        && this.getAttributes().equals(ent2.getAttributes());
    // if (getAttributes() == null) {
    // if (ent2.getAttributes() != null) {
    // return false;
    // }
    // } else if (!getAttributes().equals(ent2.getAttributes())) {
    // return false;
    // }
    // if (getSpecies() == null) {
    // if (ent2.getSpecies() != null) {
    // return false;
    // }
    // } else if (!getSpecies().equals(ent2.getSpecies())) {
    // return false;
    // }
    // return true;
  }

}
