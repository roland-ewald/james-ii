/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules.attributemodification;

import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;

/**
 * Specification of hot to modify an attribute of an entity, e.g. when applying
 * a reaction rule
 * 
 * @author Arne Bittig
 */
public interface IAttributeModification extends java.io.Serializable {

  /**
   * The attribute to be modified
   * 
   * @return Name of the attribute to be modified
   */
  String getAttribute();

  /**
   * Perform modification on given entity
   * 
   * @param ent
   *          Entity whose respective attribute value will be modified
   * @param env
   *          Environment (local variables and values)
   * @return Previous attribute value (for the record)
   * @throws IllegalArgumentException
   *           if ent does not have the relevant attribute
   */
  Object modifyAttVal(AbstractModelEntity ent, Map<String, Object> env);
}