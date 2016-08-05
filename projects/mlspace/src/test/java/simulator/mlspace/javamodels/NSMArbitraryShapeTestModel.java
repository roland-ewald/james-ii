/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.javamodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import model.mlspace.MLSpaceModel;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.Species;
import model.mlspace.subvols.Subvol;

import org.jamesii.core.util.collection.CollectionUtils;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * NSM test model with non-square subvol shapes
 *
 * @author Arne Bittig
 * @date some time in 2011
 */
public class NSMArbitraryShapeTestModel extends MLSpaceModel {

  private static final long serialVersionUID = -2047057271867887939L;

  private static final double SQRT3 = Math.sqrt(3.0);

  private static final int ENT_CONC = 20;

  private static final int NUM_ROWS = 10;

  // number of hex-triangle-columns, plus numCols+1
  private static final int NUM_COLS = 3;

  /**
   * NSM test model with non-square subvol shapes
   * 
   * @param ignored
   *          (no) model parameters
   */
  public NSMArbitraryShapeTestModel(Map<String, ?> ignored) { // NOSONAR:
    // external requirement

    super("NSM arb shape test", null, null);
    NSMEntity entity =
        new NSMEntity(new Species("S"), CollectionUtils.fillMap(
            new HashMap<String, Object>(1), "diffusion", 2.0));
    // pure square lattice of roughly equal size
    Integer numCols = (int) Math.round((SQRT3 + 1) * NUM_COLS) + 1;

    Integer initConc = NUM_ROWS * numCols * ENT_CONC;

    List<Subvol> subvols =
        createMixedGrid(NUM_ROWS, NUM_COLS, entity, initConc);

    subvols.addAll(createSquareGrid(NUM_ROWS, numCols, entity, initConc));

    subvols.addAll(createBrickGrid(NUM_ROWS, numCols, entity, initConc));
    // throw everything together and add to model
    setSubvolumes(subvols);
  }

  /**
   * @param numRows
   * @param numCols
   * @param contentEnt
   * @param contentAmount
   * @return
   */
  private static List<Subvol> createBrickGrid(Integer numRows, Integer numCols,
      NSMEntity contentEnt, Integer contentAmount) {
    Double totalVolume = 0.;

    final double diffCorrFacBigToSmall = 2. / numCols / (numCols - 1.);
    final double diffCorrFacSmallToBig = 2. / numCols / (numCols - 1.);
    final double nm2bynm1 = (-2. + numCols) / (-1. + numCols);

    // brick pattern square / rectangle lattice
    List<Subvol> subvolsbrick = new ArrayList<>();
    Subvol lastsmall = new Subvol(1.0);
    lastsmall.setName("bSQ-bl");
    totalVolume += 1.;
    lastsmall.updateState(contentEnt, contentAmount);
    Subvol lastbig = new Subvol(-1.0 + numCols);
    totalVolume += -1. + numCols;
    lastbig.addNeighborOneWay(lastsmall, diffCorrFacBigToSmall);
    lastsmall.addNeighborOneWay(lastbig, diffCorrFacSmallToBig);
    subvolsbrick.add(lastsmall);
    subvolsbrick.add(lastbig);
    for (Integer iRow = 1; iRow < numRows; iRow++) {
      totalVolume += 1.;
      Subvol nextsmall = new Subvol(1.0);
      if (iRow == numRows - 1 && numRows % 2 == 0) {
        nextsmall.setName("bSQ-tr");
      }
      totalVolume += -1. + numCols;
      Subvol nextbig = new Subvol(-1.0 + numCols);
      if (iRow == numRows - 1 && (numRows & 1) == 1) {
        nextbig.setName("bLO-tr");
      }
      nextbig.addNeighborOneWay(nextsmall, diffCorrFacBigToSmall);
      nextsmall.addNeighborOneWay(nextbig, diffCorrFacSmallToBig);
      nextbig.addNeighborOneWay(lastsmall, 1. / (numCols - 1.));
      lastsmall.addNeighborOneWay(nextbig, 1.);
      lastbig.addNeighborOneWay(nextsmall, 1. / (numCols - 1.));
      nextsmall.addNeighborOneWay(lastbig, 1.);
      nextbig.addNeighborOneWay(lastbig, nm2bynm1);
      lastbig.addNeighborOneWay(nextbig, nm2bynm1);
      subvolsbrick.add(nextsmall);
      subvolsbrick.add(nextbig);
    }

    ApplicationLogger.log(Level.INFO, "Brick lattice of height " + numRows
        + " created (even numbers are better; long brick side: "
        + (numCols - 1) + "). Total size: " + totalVolume);
    return subvolsbrick;
  }

  /**
   * @param numRows
   * @param numCols
   * @param contentEnt
   * @param contentAmount
   * @return
   */
  private static List<Subvol> createSquareGrid(Integer numRows,
      Integer numCols, NSMEntity contentEnt, Integer contentAmount) {
    Double totalVolume = 0.;
    List<Subvol> subvols = new ArrayList<>();
    for (Integer iRow = 0; iRow < numRows; iRow++) {
      for (Integer iCol = 0; iCol < numCols; iCol++) {
        totalVolume += 1.;
        Subvol next = new Subvol(1.0);
        next.setName(iRow == 0 && iCol == 0 ? "sSQ-bl" : iRow == numRows - 1
            && iCol == numCols - 1 ? "sSQ-tr"
        // : (iRow == numRows / 2) && (iCol == numCols /
        // 2) ? "sSQ-c"
            : "");
        if (iRow == 0 && iCol == 0) {
          next.updateState(contentEnt, contentAmount);
        } else {
          Subvol neigh = subvols.get(subvols.size() - 1);
          next.addNeighborOneWay(neigh, 1.0);
          neigh.addNeighborOneWay(next, 1.);
        }
        if (iRow > 0) {
          Subvol neigh = subvols.get((iRow - 1) * numCols + iCol);
          next.addNeighborOneWay(neigh, 1.0);
          neigh.addNeighborOneWay(next, 1.);
        }
        subvols.add(next);
      }
    }
    ApplicationLogger.log(Level.INFO, "Square lattice of size " + numRows + "x"
        + numCols + " created. Total size: " + totalVolume);
    return subvols;
  }

  /**
   * @param numRows
   * @param numCols
   * @param contentEnt
   * @param contentAmount
   * @return
   */
  private static List<Subvol> createMixedGrid(int numRows, int numCols,
      NSMEntity contentEnt, int contentAmount) {
    Double totalVolume = 0.;

    // mixed square-hex-triangle lattice
    List<Subvol> subvolsq = new ArrayList<>();
    List<Subvol> subvoltri = new ArrayList<>();
    List<Subvol> subvolhex = new ArrayList<>();

    // initial square column
    Subvol next = new Subvol(1.0);
    next.setName("mSQ-bl");
    totalVolume += 1.;
    next.updateState(contentEnt, contentAmount);
    subvolsq.add(next);
    for (Integer iRow = 1; iRow < numRows; iRow++) {
      next = new Subvol(1.0);
      totalVolume += 1.;
      Subvol neigh = subvolsq.get(iRow - 1);
      next.addNeighborOneWay(neigh, 1.0);
      neigh.addNeighborOneWay(next, 1.0);
      subvolsq.add(next);
    }

    // TODO!! adapt to new neighbor addition methods

    // for (Integer iCol = 0; iCol < numCols; iCol++) {
    //
    // // hex-triangle column
    // next = new Subvol(3.0 / 2.0 * SQRT3, null, "");
    // totalVolume += 3.0 / 2.0 * SQRT3;
    // next.addNeighborOneWay(subvolsq.get(iCol * numRows),
    // (SQRT3 + 1.0) / 2.0, 1.0);
    // subvolhex.add(next);
    // for (Integer iRow = 1; iRow < numRows; iRow++) {
    // if (iRow % 2 == 1) {// add 2 triangles
    // next = new Subvol(SQRT3 / 4.0, null, "");
    // totalVolume += SQRT3 / 4.;
    // next.addNeighborOneWay(
    // subvolhex.get(iCol * numRows / 2 + iRow / 2),
    // 2.0 / SQRT3, 1.0);
    // next.addNeighborOneWay(subvolsq.get(iCol * numRows + iRow),
    // 0.5 + 0.5 / SQRT3, 1.0);
    // subvoltri.add(next);
    // next = new Subvol(SQRT3 / 4.0, null, "");
    // totalVolume += SQRT3 / 4.;
    // next.addNeighborOneWay(
    // subvolhex.get(iCol * numRows / 2 + iRow / 2),
    // 2.0 / SQRT3, 1.0);
    // subvoltri.add(next);
    // } else { // add hex
    // totalVolume += 3.0 / 2.0 * SQRT3;
    // next = new Subvol(3.0 / 2.0 * SQRT3, null,
    // // (iCol != numCols / 2) || (iRow != numRows / 2) ? "mHEX-c"
    // // :
    // "");
    // next.addNeighborOneWay(subvolsq.get(iCol * numRows + iRow),
    // (SQRT3 + 1.0) / 2.0, 1.0);
    // next.addNeighborOneWay(
    // subvoltri.get(iCol * numRows + iRow - 2),
    // 2.0 / SQRT3, 1.0);
    // next.addNeighborOneWay(
    // subvoltri.get(iCol * numRows + iRow - 1),
    // 2.0 / SQRT3, 1.0);
    // subvolhex.add(next);
    // }
    // }
    //
    // // square column
    // next = new Subvol(1.0, null, "");
    // totalVolume += 1.;
    // next.addNeighborOneWay(subvolhex.get(iCol * numRows / 2),
    // (SQRT3 + 1.0) / 2.0, 1.0);
    // subvolsq.add(next);
    // for (Integer iRow = 1; iRow < numRows; iRow++) {
    // totalVolume += 1.;
    // next = new Subvol(1.0, null, iRow == numRows - 1
    // && iCol == numCols - 1 ? "mSQ-tr"
    // // : (iRow == numRows / 2) && (iCol == numCols / 2) ?
    // // "mSQ-c"
    // : "");
    // next.addNeighborOneWay(subvolsq.get(iCol * numRows + iRow - 1),
    // 1.0, 1.0);
    // if (iRow % 2 == 1) {
    // next.addNeighborOneWay(
    // subvoltri.get(iCol * numRows + iRow),
    // 0.5 + 0.5 / SQRT3, 1.0);
    // } else {
    // next.addNeighborOneWay(
    // subvolhex.get(iCol * numRows / 2 + iRow / 2),
    // 2.0 / SQRT3, 1.0);
    // }
    //
    // subvolsq.add(next);
    // }
    // }
    ApplicationLogger.log(Level.INFO,
        "Mixed square/hex-triangle lattice of size " + numRows + "x" + numCols
            + "+" + (numCols + 1) + " created. Total size: " + totalVolume);

    List<Subvol> subvols = subvolsq;
    subvols.addAll(subvolhex);
    subvols.addAll(subvoltri);
    return subvols;
  }
}
