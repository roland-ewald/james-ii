/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.subvols;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.geometry.shapes.AxisAlignedBox;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.TorusSurface;
import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Periodic2DVectorFactory;

/**
 * @author Arne Bittig
 * @date Apr 19, 2012
 */
public class SubvolTest extends ChattyTestCase {
  
  /** Test {@link SubvolUtils#addNeighbors(Subvol, Subvol)} */
  public final void testNeighborDetermination() {
    IVectorFactory vecFac = new Periodic2DVectorFactory(0, 0., 3., 3.);

    IPositionVector p1a = vecFac.newPositionVector(2.0, 2.0);
    IPositionVector p1b = vecFac.newPositionVector(3.0, 3.0);
    IShape s1 = new AxisAlignedBox(p1a, p1b);
    Subvol sv1 = new Subvol(s1, null);
    sv1.setName("1");
    IPositionVector p2a = vecFac.newPositionVector(0.0, 2.0);
    IPositionVector p2b = vecFac.newPositionVector(1.0, 3.0);
    IShape s2 = new AxisAlignedBox(p2a, p2b);
    Subvol sv2 = new Subvol(s2, null);
    sv2.setName("2");
    Subvol sv3 =
        new Subvol(new AxisAlignedBox(vecFac.newPositionVector(0.0, 0.0),
            vecFac.newPositionVector(1.0, 1.0)), null);
    sv3.setName("3");
    SubvolUtils.addNeighbors(sv1, sv2);
    SubvolUtils.addNeighbors(sv1, sv3);
    SubvolUtils.addNeighbors(sv2, sv3);

    assertTrue(sv1.getNeighbors().contains(sv2));
    assertEquals(sv2.getNeighbors().size(), 2);
  }


  private static final double LOW_X = -10.;

  private static final double LOW_Y = -12.;

  private static final double HIGH_X = 12.;

  private static final double HIGH_Y = 11.;

  private final IVectorFactory normalVecFac2d = new AVectorFactory(2);

  private final IVectorFactory periodicVecFac2d = new Periodic2DVectorFactory(
      LOW_X, LOW_Y, HIGH_X, HIGH_Y);

  /**
   * Test
   * {@link SubvolInitializer#splitSubvol(Subvol, double, org.jamesii.core.math.random.generators.IRandom)}
   * (without state) and assert that "corner" sv in periodic boundary case has
   * twice as many neighbors as the equivalent in same-size case with normal
   * (open) boundaries for more than two subvols in each direction
   */
  public void testSubvolSplit() {
    IShape normalShape =
        new AxisAlignedBox(normalVecFac2d.newPositionVector(LOW_X, LOW_Y),
            normalVecFac2d.newPositionVector(HIGH_X, HIGH_Y));
    IShape periodicShape = new TorusSurface(periodicVecFac2d);

    // case "2 svs in each direction"
    double splitSize = 12.;
    List<Subvol> normalSplit =
        SubvolInitializer.splitSubvol(new Subvol(normalShape, null), splitSize,
            null);
    List<Subvol> periodicSplit =
        SubvolInitializer.splitSubvol(new Subvol(periodicShape, null),
            splitSize, null);
    assertEquals(normalSplit.size(), periodicSplit.size());
    assertEquals(4, periodicSplit.size());
    assertSame(1, getDifferentNumbersOfNeighbors(normalSplit).size());
    assertSame(1, getDifferentNumbersOfNeighbors(periodicSplit).size());
    assertSame(normalSplit.get(0).getNeighbors().size(), periodicSplit.get(0)
        .getNeighbors().size());

    // case "2 svs in one, more than two in another direction"
    splitSize = 11.;
    normalSplit =
        SubvolInitializer.splitSubvol(new Subvol(normalShape, null), splitSize,
            null);
    periodicSplit =
        SubvolInitializer.splitSubvol(new Subvol(periodicShape, null),
            splitSize, null);
    assertEquals(normalSplit.size(), periodicSplit.size());
    assertEquals(6, periodicSplit.size());
    assertNotSame(1, getDifferentNumbersOfNeighbors(normalSplit).size());
    assertSame(1, getDifferentNumbersOfNeighbors(periodicSplit).size());
    assertTrue(normalSplit.get(0).getNeighbors().size() < periodicSplit.get(0)
        .getNeighbors().size());
    assertTrue(2 * normalSplit.get(0).getNeighbors().size() > periodicSplit
        .get(0).getNeighbors().size());

    // case "more than two svs in each direction"
    splitSize = 10.5;
    normalSplit =
        SubvolInitializer.splitSubvol(new Subvol(normalShape, null), splitSize,
            null);
    periodicSplit =
        SubvolInitializer.splitSubvol(new Subvol(periodicShape, null),
            splitSize, null);
    assertEquals(normalSplit.size(), periodicSplit.size());
    assertNotSame(1, getDifferentNumbersOfNeighbors(normalSplit).size());
    assertSame(1, getDifferentNumbersOfNeighbors(periodicSplit).size());
    assertSame(2 * normalSplit.get(0).getNeighbors().size(),
        periodicSplit.get(0).getNeighbors().size());

  }

  private static Collection<Integer> getDifferentNumbersOfNeighbors(
      Collection<Subvol> svs) {
    Collection<Integer> rv = new LinkedHashSet<>(svs.size());
    for (Subvol sv : svs) {
      rv.add(sv.getNeighbors().size());
    }
    return rv;
  }

}
