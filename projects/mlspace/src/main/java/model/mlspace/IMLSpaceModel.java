/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace;

import java.util.Collection;
import java.util.Map;

import model.mlspace.entities.ModelEntityFactory;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.CollisionReactionRule;
import model.mlspace.rules.NSMReactionRule;
import model.mlspace.rules.TimedReactionRule;
import model.mlspace.rules.TransferInRule;
import model.mlspace.rules.TransferOutRule;
import model.mlspace.subvols.Subvol;

import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.model.IModel;
import org.jamesii.core.util.hierarchy.IHierarchy;

/**
 * @author Arne Bittig
 */
public interface IMLSpaceModel extends IModel {

  /**
   * Initialize subvolumes if necessary
   * 
   * @param rand
   *          Random number generator (for initialization, if required)
   * @param minSvSize
   *          Lower bound for target subvol size (null for none/auto)
   * @param maxSvSize
   *          Upper bound for target subvol size (null for none/auto)
   * @return true if model has subvolumes
   */
  boolean initSubvolumes(IRandom rand, Double minSvSize, Double maxSvSize);

  /**
   * Get compartment hierarchy
   * 
   * @return the compartments
   */
  IHierarchy<SpatialEntity> getCompartments();

  /**
   * Subvolumes in the model, grouped by the compartments they belong to. Empty
   * map if no compartments are present (i.e. the model is NSM-only and
   * {@link #getSubvolumes()} should be used), null if subvolumes were not
   * initalized yet (see {@link #initSubvolumes(IRandom, Double, Double)}).
   * 
   * @return Subvolumes grouped by the compartment directly "above" them
   */
  Map<SpatialEntity, Collection<Subvol>> getCompSvMap();

  /**
   * Subvolumes in the model. Empty collection if none, null if they have not
   * been initialized yet.
   * 
   * @return the subvolumes
   * @see #getCompSvMap()
   */
  Collection<Subvol> getSubvolumes();

  /**
   * Reaction rules in the model
   * 
   * @return Rules triggered by collisions
   */
  Collection<CollisionReactionRule> getCollisionTriggeredRules();

  /**
   * (Spatial) Reaction rules not triggered by collisions (i.e. zero- or first
   * order rules for spatial entities)
   * 
   * @return Reaction rules in the model
   */
  Collection<TimedReactionRule> getTimedReactionRules();

  /**
   * NSM reaction rules (also time-triggered)
   * 
   * @return NSM rules in the model
   */
  Collection<NSMReactionRule> getNSMReactionRules();

  /**
   * Transfer rules
   * 
   * @return Transfer rules in the model
   */
  Collection<TransferInRule> getTransferInRules();

  /**
   * Transfer rules
   * 
   * @return Transfer rules in the model
   */
  Collection<TransferOutRule> getTransferOutRules();

  /**
   * @return Factory to produce vectors (includes definition of number of
   *         dimensions and periodic boundaries, if present)
   */
  IVectorFactory getVectorFactory();

  /**
   * 
   * @return Factory to produce model entities (contains species definitions)
   */
  ModelEntityFactory getModelEntityFactory();

}