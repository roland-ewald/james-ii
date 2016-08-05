/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import model.mlspace.entities.values.AbstractValueRange;

import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;

/**
 * ML-Space entity produced by parser for the initial model state -- similar to
 * a {@link RuleEntity}, but with a link to nested entities (see
 * {@link #getSubEntities()}), thus forming a tree-like structure.
 * 
 * @author Arne Bittig
 */
public class InitEntity extends AbstractEntity<AbstractValueRange<?>> {

  private static final long serialVersionUID = 1451919354644587468L;

  private IUpdateableMap<InitEntity, Integer> subEntities;

  /**
   * @param species
   * @param atts
   */
  public InitEntity(Species species, Map<String, AbstractValueRange<?>> atts) {
    this(species, atts, new UpdateableAmountMap<InitEntity>());
  }

  /**
   * Full constructor
   * 
   * @param spec
   *          Species
   * @param attributes
   *          Attribute map
   * @param subEntities
   *          Entities nested inside this one
   */
  public InitEntity(Species spec,
      Map<String, AbstractValueRange<?>> attributes,
      IUpdateableMap<InitEntity, Integer> subEntities) {
    super(spec, attributes);
    this.subEntities = subEntities;
  }

  protected InitEntity(InitEntity r, boolean noCheckForBindings) {
    this(r.getSpecies(), r.getAttributes(), null);
    assert noCheckForBindings;
  }

  /**
   * 
   * @return Entities nested inside this one (and the amount of each)
   */
  public IUpdateableMap<InitEntity, Integer> getSubEntities() {
    return subEntities != null ? subEntities : UpdateableAmountMap
        .wrap(Collections.<InitEntity, Integer> emptyMap());
  }

  /**
   * Update entities nested inside this one
   * 
   * @param map
   *          Other entities nested inside this one, and/or additional amount
   */
  public void updateSubEntities(Map<InitEntity, Integer> map) {
    if (subEntities == null) {
      subEntities = UpdateableAmountMap.wrap(new LinkedHashMap<>(map));
    } else {
      subEntities.updateAll(map);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = getAttributes().hashCode();
    result =
        prime * result + (getSpecies() == null ? 0 : getSpecies().hashCode());
    result =
        prime * result + (subEntities == null ? 0 : subEntities.hashCode());
    return result;
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
    return equalsSameClass((InitEntity) obj);
  }

  private boolean equalsSameClass(InitEntity other) {
    if (!getAttributes().equals(other.getAttributes())) {
      return false;
    }
    if (getSpecies() == null && other.getSpecies() != null) {
      return false;
    } else if (!getSpecies().equals(other.getSpecies())) {
      return false;
    }
    if (subEntities == null) {
      return other.subEntities == null;
    }
    return subEntities.equals(other.subEntities);
  }

  @Override
  public String toString() {
    StringBuilder str = absEntStr();
    if (subEntities != null) {
      str.append("[");
      for (Map.Entry<InitEntity, Integer> subE : subEntities.entrySet()) {
        str.append(subE.getValue() + " " + subE.getKey() + ",");
      }
      if (str.charAt(str.length() - 1) == ',') {
        str.deleteCharAt(str.length() - 1);
      }
      str.append("]");
    }
    return str.toString();
  }

}
