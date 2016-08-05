/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.subvols;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import model.mlspace.entities.spatial.SpatialEntity;

import org.jamesii.core.math.geometry.GeoUtils;
import org.jamesii.core.math.geometry.shapes.ShapeUtils;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.collection.CollectionUtils;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * Static methods for dealing with subvolumes
 *
 * @author Arne Bittig
 */
public final class SubvolUtils {

  private SubvolUtils() {
  }

  /**
   * Calculate diffusion correction factor for subvol neighborhood, where the
   * "side length" of a subvol is considered to be the quotient of its volume
   * and the size of the intersection with the neighbor)
   * 
   * @param vol
   *          Volume of subvol
   * @param isect
   *          Size of intersection with neighbor (shared side length / shared
   *          surface)
   * @param centerDist
   *          Distance of the two subvol's centers (assumed to be orthogonal to
   *          intersection)
   * @return Diffusion constant scaling factor
   */
  public static double getDiffusionCorrectionFactor(double vol, double isect,
      double centerDist) {
    double sideLengthApprox = vol / isect;
    return 1. / sideLengthApprox / centerDist;
  }

  private static final double DELTA = 1e-15;

  /**
   * Add two subvol's to each other's neighborhood, automatically determining
   * the diffusion correction factor from their shapes
   * 
   * @param sv1
   *          One Subvol
   * @param sv2
   *          Other Subvol
   * @return Success value, i.e. whether sv1 and sv2 were found to be neighbors
   */
  public static boolean addNeighbors(Subvol sv1, Subvol sv2) {
    double[] nprops =
        ShapeUtils.getNeighborRelation(sv1.getShape(), sv2.getShape(), DELTA);
    if (nprops[0] <= DELTA) {
      return false;
    }
    sv1.addNeighborOneWay(sv2,
        getDiffusionCorrectionFactor(sv1.getVolume(), nprops[0], nprops[1]));
    sv2.addNeighborOneWay(sv1,
        getDiffusionCorrectionFactor(sv2.getVolume(), nprops[0], nprops[2]));
    return true;
  }

  /**
   * Move all entities in this subvolume into neighboring subvolumes,
   * proportional to the respective diffusion correction factors (distance of SV
   * centers and size of intersection area)
   * 
   * - only neighboring subvolumes that belong to the same compartment are
   * considered (as determined by {@link Subvol#getEnclosingEntity()})
   * 
   * @param sv
   *          Subvol to empty
   * @param rand
   *          Random number generator (to avoid rounding)
   * @return Subvols whose state has changed (i.e. into which something was
   *         pushed; not including sv)
   */
  public static List<Subvol> pushSubvolContentIntoNeighbors(Subvol sv,
      IRandom rand) {
    if (sv.getState().isEmpty()) {
      return Collections.emptyList();
    }
    List<Subvol> affectedSubvols = new ArrayList<>(); // return value
    double diffCorrFacSum = 0.0;
    Map<Subvol, Double> neighEntries = sv.getNeighborhoodMap();
    Set<Subvol> prevCheckedNeighs = new LinkedHashSet<>();
    prevCheckedNeighs.add(sv);
    while (diffCorrFacSum == 0.) {
      for (Map.Entry<Subvol, Double> neighE : neighEntries.entrySet()) {
        if (neighE.getKey().getEnclosingEntity() == sv.getEnclosingEntity()) {
          diffCorrFacSum += neighE.getValue();
        }
      }
      if (diffCorrFacSum == 0.) { // all neighbors have different parent
        neighEntries =
            getSubvolsNeighborsNeighbors(neighEntries, prevCheckedNeighs);
      }
    }
    for (Map.Entry<Subvol, Double> neighE : neighEntries.entrySet()) {
      Subvol neigh = neighE.getKey();
      if (neigh.getEnclosingEntity() == sv.getEnclosingEntity()) {
        neigh.updateState(sv.splitState(neighE.getValue() / diffCorrFacSum,
            rand));
        diffCorrFacSum -= neighE.getValue();
        affectedSubvols.add(neigh);
      }
    }
    return affectedSubvols;
  }

  /**
   * Get neighbors neighbors from given collection of neighbors of a subvol
   * 
   * @param neighEntries
   * @param prevCheckedNeighs
   * @return
   */
  private static Map<Subvol, Double> getSubvolsNeighborsNeighbors(
      Map<Subvol, Double> neighEntries, Collection<Subvol> prevCheckedNeighs) {
    // extend search: get all neighbor's neighbors
    Map<Subvol, Double> ne2 = new LinkedHashMap<>();
    for (Map.Entry<Subvol, Double> neighE : neighEntries.entrySet()) {
      prevCheckedNeighs.add(neighE.getKey());
      ne2.putAll(neighE.getKey().getNeighborhoodMap());
    }
    Iterator<Map.Entry<Subvol, Double>> it = ne2.entrySet().iterator();
    while (it.hasNext()) {
      if (prevCheckedNeighs.contains(it.next().getKey())) {
        it.remove();
      }
    }
    return ne2;
  }

  /**
   * @param startSv
   *          Subvol where to start search
   * @param p
   *          Point to find
   * @return Subvol including p, null if none found
   */
  public static Subvol findSubvolIncludingPoint(Subvol startSv,
      IPositionVector p) {
    if (startSv.getShape().includesPoint(p)) {
      return startSv;
    }
    NavigableSet<Subvol> svsToCheck =
        new TreeSet<>(GeoUtils.distanceToPointComparator(p));
    // TODO: in strange situations, distance equality may do harm here!
    svsToCheck.addAll(startSv.getNeighbors());
    Collection<Subvol> svsChecked = new LinkedHashSet<>();
    svsChecked.add(startSv);
    while (!svsToCheck.isEmpty()) {
      Subvol head = svsToCheck.first();
      if (head.getShape().includesPoint(p)) {
        return head;
      }
      svsChecked.add(head);
      svsToCheck.remove(head);
      for (Subvol headNeigh : head.getNeighbors()) {
        if (!svsChecked.contains(headNeigh) && !svsToCheck.contains(headNeigh)) {
          svsToCheck.add(headNeigh);
        }
      }
    }
    return null;
  }

  /**
   * @param subvols
   *          Subvols
   * @return Map of compartments and the subvols that have them as their parents
   */
  public static Map<SpatialEntity, Collection<Subvol>> createCompSubvolMap(
      Collection<Subvol> subvols) {
    Map<SpatialEntity, Collection<Subvol>> map = new LinkedHashMap<>();
    for (Subvol sv : subvols) {
      CollectionUtils.putIntoSetMultiMap(map, sv.getEnclosingEntity(), sv);
    }
    return map;
  }

  /**
   * Check subvols for whether they all have the same extension
   * 
   * @param subvols
   *          Subvols to check
   * @return Extension of subvols if the same for all, null if different
   */
  public static IDisplacementVector getCommonSubvolExt(
      Collection<Subvol> subvols) {
    final double delta = 1e-12; // CHECK: more elegant?!
    IDisplacementVector firstSvExt = null;
    int dim = 0;
    for (Subvol sv : subvols) {
      if (firstSvExt == null) {
        firstSvExt = sv.getShape().getMaxExtVector();
        dim = firstSvExt.getDimensions();
      } else {
        IDisplacementVector currSvExt = sv.getShape().getMaxExtVector();
        for (int d = 1; d <= dim; d++) {
          double diffInD = firstSvExt.get(d) - currSvExt.get(d);
          if (Math.abs(diffInD) > delta) {
            ApplicationLogger.log(Level.SEVERE, "Unequal subvol sizes: "
                + firstSvExt + " and " + currSvExt + " differ by " + diffInD
                + " in dimension " + d);
            return null;
          }
        }

      }
    }
    if (firstSvExt == null) {
      throw new IllegalArgumentException("No subvols given");
    }
    return firstSvExt.times(2.);
  }

  /**
   * Check whether the given SpatialEntity->Subvols map is correct for all given
   * compartments, i.e. whether the associated subvol's centers are indeed
   * covered by the associated comp. (Does not check whether there are subvols
   * that should be associated with a given comp, but are not and neither with
   * any other.)
   * 
   * Note that if the subvol overlap of dissolving compartments has already been
   * processed, i.e. they have been removed from the map, they must not be
   * contained in the given compartment collection.
   * 
   * @param comps
   *          Compartments whose subvols to check
   * @param map
   *          SpatialEntity->Subvols map
   * @return Collection of compartments for which the map is incorrect, empty if
   *         it is correct for all
   */
  public static Map<SpatialEntity, Collection<Subvol>> getCompSvMapErrors(
      Collection<SpatialEntity> comps,
      Map<SpatialEntity, Collection<Subvol>> map) {
    Map<SpatialEntity, Collection<Subvol>> errors = new LinkedHashMap<>();
    for (SpatialEntity comp : comps) {
      Collection<Subvol> compSvs = map.get(comp);
      if (compSvs == null || compSvs.isEmpty()) {
        errors.put(comp, compSvs);
        continue;
      }
      for (Subvol sv : compSvs) {
        if (!comp.getShape().includesPoint(sv.getPosition())) {
          CollectionUtils.putIntoListMultiMap(errors, comp, sv);
        }
      }
    }
    return errors;
  }

  /**
   * Determine difference between defines compartment volume and the volume
   * covered by the subvols it overlaps -- mostly for informative / debugging
   * purposes
   * 
   * @param map
   *          SpatialEntity->Subvols map
   * @return Volume difference between compartment and sum of overlapped
   *         subvols' volume
   */
  public static Map<SpatialEntity, Double> getVolumeDeviation(
      Map<SpatialEntity, Collection<Subvol>> map) {
    Map<SpatialEntity, Double> rv = new LinkedHashMap<>(map.size());
    for (Map.Entry<SpatialEntity, Collection<Subvol>> e : map.entrySet()) {
      double svVolSum = 0.;
      for (Subvol sv : e.getValue()) {
        svVolSum += sv.getVolume();
      }
      SpatialEntity comp = e.getKey();
      rv.put(comp, comp.getShape().getSize() - svVolSum);
    }
    return rv;
  }
}
