/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.subvols;

import java.util.Collection;
import java.util.Map;

import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;

import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;

/**
 * Subvolumes in an MLSpace model
 * 
 * 
 * 
 * Creation date: 06.01.2011
 * 
 * @author Arne Bittig
 */
public class Subvol implements ISubvol {

  private static final long serialVersionUID = 616981324238728846L;

  /**
   * Optional ID (name), used in toString()
   */
  private String name = "";

  /**
   * Shape of the subvolume (used to calculate volume and neighborhood
   * properties automatically, may be empty if those are given each time;
   * subvolume cannot be split if null)
   */
  private final IShape shape;

  /** Volume (determined from boundaries) */
  private final double volume;

  /** Parent compartment (may be null for root compartment) */
  private SpatialEntity enclosingEntity = null;

  /** Neighbors of Subvol, and relation properties */
  private final ISvNeighborhood neighborhood;

  /** Internal state of Subvol */
  private IUpdateableMap<NSMEntity, Integer> state =
      new UpdateableAmountMap<>();

  /**
   * NSM constructor -- subvolume can be used for NSM-based simulation only, no
   * hybrid space/ multi-resolution simulation (as overlap with anything in
   * continuous space cannot be known); also, when adding neighbors, distance of
   * the centroids and size of the border has to be given always
   * 
   * @param volume
   *          Volume of the subvolume
   */
  public Subvol(double volume) {
    this(null, volume, null);
  }

  /**
   * Full constructor
   * 
   * @param shape
   *          Shape of the Subvol
   * @param neighborhood
   *          neigborhood (e.g. pointer to global handling mechanism; null for
   *          default (local))
   */
  public Subvol(IShape shape, ISvNeighborhood neighborhood) {
    this(shape, shape.getSize(), neighborhood);
  }

  /**
   * Private constructor called by the two public ones (used because final shape
   * and volume can be set only once, so neither public constructor can
   * elegantly call the other)
   * 
   * @param shape
   *          Shape of the Subvol
   * @param volume
   *          Volume of the Subvol
   * @param neighborhood
   *          neigborhood (e.g. pointer to global handling mechanism; null for
   *          default (local))
   */
  private Subvol(IShape shape, double volume, ISvNeighborhood neighborhood) {
    this.shape = shape;
    this.volume = volume;
    this.neighborhood =
        neighborhood != null ? neighborhood : new SimpleLocalNeighborhood(this);
  }

  /**
   * Get the volume of the subvol. If a shape is defined, this method and
   * {@link #getShape()}.getVolume() (see {@link IShape#getSize()}) should
   * return the same value. However, NSM simulation requires only subvols'
   * volume and neighbors (see {@link #addNeighborOneWay(Subvol, double)}), not
   * explicit definition of their shapes, and the volume is cached in an
   * internal field anyway.
   * 
   * Unlike other {@link org.jamesii.core.math.geometry.IShapedComponent}s, a
   * subvol's volume is immutable.
   * 
   * @return Volume of this subvolume
   */
  public double getVolume() {
    return volume;
  }

  /** @return Shape of the subvolume */
  @Override
  public IShape getShape() {
    return shape;
  }

  @Override
  public IPositionVector getPosition() {
    return shape.getCenter();
  }

  @Override
  public SpatialEntity getEnclosingEntity() {
    return enclosingEntity;
  }

  /**
   * Set the enclosing compartment (formerly known as parent comp)
   * 
   * @param enclosingEntity
   *          the enclosingEntity to set
   */
  public void setEnclosingEntity(SpatialEntity enclosingEntity) {
    this.enclosingEntity = enclosingEntity;
  }

  /**
   * @return Neighbors of this Subvol
   * @see model.mlspace.subvols.SimpleLocalNeighborhood#getNeigbors(Subvol)
   */
  public Collection<Subvol> getNeighbors() {
    return neighborhood.getNeigbors(this);
  }

  /**
   * @return Neighborhood map (Subvols and diffusion correction factors)
   */
  public Map<Subvol, Double> getNeighborhoodMap() {
    return neighborhood.getNeighborhood(this);
  }

  /**
   * Add neighbor with given diffusion correction factor (one way only, as the
   * factor is direction dependent)
   * 
   * @param neigh
   *          Neighboring Subvol
   * @param diffCorrFac
   *          Diffusion constant scaling factor
   * @see ISvNeighborhood#addNeighborOneWay(Subvol, Subvol, double)
   */
  public void addNeighborOneWay(Subvol neigh, double diffCorrFac) {
    neighborhood.addNeighborOneWay(this, neigh, diffCorrFac);
  }

  /**
   * Remove given neighbor of this subvol, and remove this subvol as neighbor of
   * given subvol
   * 
   * @param neigh
   *          Neigboring subvol to remove
   * @return true if this sv was a neighbor of neigh (and was successfully
   *         removed)
   */
  public boolean removeNeighbor(Subvol neigh) {
    return neighborhood.removeNeighborBothWays(this, neigh);
  }

  /**
   * Get sum of neighborhood correction factors, i.e. a measure of surface area
   * through which entities from this Subvol can diffuse and the distance of the
   * center of the respective neighbor (which influences diffusion throughput)
   * 
   * @return Sum of neighborhood correction factors
   */
  public Double getSumOfNeighCorrFac() {
    return neighborhood.sumNeighCorrFac(this);
  }

  /**
   * Get the state "vector" as entity->amount map. The returned map should not
   * be mutated directly (and will not support most mutating methods) -- use
   * {@link #clearState()}, {@link #splitState(double, IRandom)},
   * {@link #updateState(NSMEntity, Integer)} or {@link #updateState(Map)} for
   * manipulating a subvol's state vector.
   * 
   * @return Map of entities -> total amount
   */
  @Override
  public Map<NSMEntity, Integer> getState() {
    return state;
  }

  @Override
  public IUpdateableMap<NSMEntity, Integer> clearState() {
    IUpdateableMap<NSMEntity, Integer> prevState = state;
    state = new UpdateableAmountMap<>();
    return prevState;
  }

  /**
   * Eject a given fraction of entities from subvol
   * 
   * @param frac
   *          Fraction to split off
   * @param rand
   *          Random number generator for marginal entities
   * @return Fraction actually split off (as state map)
   * @see IUpdateableMap#split(double, IRandom)
   */
  public Map<NSMEntity, Integer> splitState(double frac, IRandom rand) {
    return state.split(frac, rand);
  }

  /**
   * Update amount of single entity (delegate)
   * 
   * @param ent
   *          Entity whose amount to change
   * @param uval
   *          How much to change it
   * @return new amount
   * 
   * @see UpdateableAmountMap#update(Object, Integer)
   */
  @Override
  public Integer updateState(NSMEntity ent, Integer uval) {
    return state.update(ent, uval);
  }

  /**
   * Update state (delegate)
   * 
   * @param updVec
   *          Map of entities -> amount of change
   * @see UpdateableAmountMap#updateAll(Map)
   */
  @Override
  public void updateState(Map<NSMEntity, Integer> updVec) {
    state.updateAll(updVec);
  }

  /**
   * 
   * @return Subvolume ID/name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public final void setName(String name) {
    this.name = name == null ? "" : name;
  }

  @Override
  public String toString() {
    String parCompStr =
        enclosingEntity != null ? " (in " + enclosingEntity.idString() + ")" : "";
    return "Subvolume " + name + parCompStr + ": " + state;
  }
}