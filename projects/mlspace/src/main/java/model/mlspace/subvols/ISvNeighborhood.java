/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.subvols;

import java.util.Collection;
import java.util.Map;

/**
 * Interface for subvol neighborhood relation handler
 *
 * @author Arne Bittig
 */
public interface ISvNeighborhood extends java.io.Serializable {

  /**
   * @param sv
   *          Subvol whose neighbors to get
   * @return Collection of known neighbors
   */
  Collection<Subvol> getNeigbors(Subvol sv);

  /**
   * Neighbors and the associated diffusion correction factors (i.e. a view of
   * the core neighborhood data)
   * 
   * @param sv
   *          Subvol whose neighbors to get
   * @return Map Subvol -> Double
   */
  Map<Subvol, Double> getNeighborhood(Subvol sv);

  /**
   * @param sv
   *          Subvol whose neighbors to check
   * @return Sum of diffusion correction factors of all neighbors
   */
  Double sumNeighCorrFac(Subvol sv);

  /**
   * Add neighborhood relation between one subvol and another, using the given
   * correction factor (by which the diffusion constant of each entity in each
   * subvol is to be scaled when calculating diffusion propensities; for
   * square/cubic subvols with side length l, for example, this factor 1/l^2.)
   * The factor for the relation from the other to the first subvol may differ
   * (e.g. if the shared side/surface of the subvols is not in the middle
   * between the two subvol centers)!
   * 
   * @param sv
   *          One half of pair of neighboring subvols
   * @param neigh
   *          Other half of pair of neighboring subvols
   * @param diffCorrFac
   *          diffusion constant scaling factor related to subvol size
   */
  void addNeighborOneWay(Subvol sv, Subvol neigh, double diffCorrFac);

  /**
   * Remove neighbor from neighborhood map (optional operation, not if
   * neighborhood is global and fixed, e.g. in case of a regular grid with no
   * subvol shape changes)
   * 
   * @param sv
   *          One half of pair of no-longer-neighboring subvols
   * @param exneigh
   *          Other half of pair of no-longer-neighboring subvols
   * @return TODO
   */
  boolean removeNeighborBothWays(Subvol sv, Subvol exneigh);
}