/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.subvols;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Local neighborhood for subvolumes (1-1 mapping between them)
 *
 * @author Arne Bittig
 */
public class SimpleLocalNeighborhood implements ISvNeighborhood {

  private static final long serialVersionUID = -9173689707259043321L;

  /**
   * Subvol this neighborhood belongs to
   */
  private final Subvol sv;

  /** central data structure */
  private final Map<Subvol, Double> neighbors = new LinkedHashMap<>();

  /**
   * flag whether to check whether each method call really refers to this
   * neighborhood
   */
  private final boolean checkForCorrectness;

  /**
   * diffusion correction factor sum (combination of distance to neighbors and
   * shared area/side length)
   */
  private double sumCorrFac = Double.NaN;

  /**
   * Construct new neighborhood structure for a subvolume
   * 
   * @param sv
   *          Subvolume for which this will be the local neighborhood
   */
  public SimpleLocalNeighborhood(Subvol sv) {
    this(sv, false);
  }

  /**
   * Construct new neighborhood structure for a subvolume
   * 
   * @param sv
   *          Subvolume for which this will be the local neighborhood
   * @param checkEnabled
   *          Whether to check each call for correctness (one sv param must
   *          always be equal to sv given now)
   */
  public SimpleLocalNeighborhood(Subvol sv, boolean checkEnabled) {
    this.sv = sv;
    this.checkForCorrectness = checkEnabled;
  }

  /**
   * Throw an error if {@link #checkForCorrectness} is true and a method call
   * does not apply to this subvol
   * 
   * @param sv2
   */
  private void checkForCorrectness(Subvol sv2) {
    if (checkForCorrectness && !sv.equals(sv2)) {
      throw new IllegalArgumentException("Local neighborhood of " + sv
          + " accessed with argument " + sv2);
    }
  }

  @Override
  public void addNeighborOneWay(Subvol sv2, Subvol neigh, double diffCorrFac) {
    checkForCorrectness(sv2);
    this.neighbors.put(neigh, diffCorrFac);
  }

  @Override
  public boolean removeNeighborBothWays(Subvol sv2, Subvol neigh) {
    checkForCorrectness(sv2);
    if (neighbors.remove(neigh) != null) {
      return false;
    }
    if (neigh.getNeighbors().contains(this.sv)) {
      neigh.removeNeighbor(this.sv);
    }
    return true;
  }

  @Override
  public Collection<Subvol> getNeigbors(Subvol sv2) {
    checkForCorrectness(sv2);
    return neighbors.keySet();
  }

  @Override
  public Map<Subvol, Double> getNeighborhood(Subvol sv2) {
    checkForCorrectness(sv2);
    return neighbors;
  }

  @Override
  public Double sumNeighCorrFac(Subvol sv2) {
    if (sumCorrFac >= 0) { // CHECK: =?
      return sumCorrFac;
    }
    checkForCorrectness(sv2);
    double scf = 0;
    for (double cf : neighbors.values()) {
      scf += cf;
    }
    this.sumCorrFac = scf;
    return sumCorrFac;
  }

  @Override
  public String toString() {
    return sv.getName() + ": " + neighbors.toString();
  }
}