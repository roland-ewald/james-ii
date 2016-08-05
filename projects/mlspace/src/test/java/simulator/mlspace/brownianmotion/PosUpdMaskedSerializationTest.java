/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.brownianmotion;

import java.util.logging.Level;

import model.mlspace.entities.spatial.IMoveableEntity;

import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.generators.java.JavaRandom;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * @author Arne Bittig
 */
public class PosUpdMaskedSerializationTest extends
    SimpleSerializationTest<DiscretePositionUpdaterMasked> implements
    java.io.Serializable {

  private static final long serialVersionUID = -4399044313296018542L;

  private static final boolean SKIP_XMLBEAN_TEST = true;

  private static final boolean SKIP_RMI_TEST = false;

  @Override
  public void testSerializationXMLBean() throws Exception {
    if (SKIP_XMLBEAN_TEST) {
      return;
    }
    super.testSerializationXMLBean();
  }

  @Override
  public void testSerializationViaRMI() throws Exception {
    if (SKIP_RMI_TEST) {
      return;
    }
    super.testSerializationViaRMI();
  }

  private IMoveableEntity testEnt;

  @Override
  public DiscretePositionUpdaterMasked getTestObject() {
    IVectorFactory vecFac = new AVectorFactory(2);
    DiscretePositionUpdaterMasked pucmads =
        new DiscretePositionUpdaterMasked(vecFac.newDisplacementVector(1., 1.),
            new ContinuousPositionUpdaterPolar(new JavaRandom(), 2.0, vecFac));
    testEnt = new MoveableEntity(vecFac.newPositionVector(0.1, 0.1), 1.0);
    IDisplacementVector posUpd1 = pucmads.getPositionUpdate(0.5, testEnt);
    testEnt.move(posUpd1);
    IDisplacementVector posUpd2 = pucmads.getPositionUpdate(1.5, testEnt);
    testEnt.move(posUpd2);
    ApplicationLogger.log(Level.INFO,
    // "Step 1: " + posUpd1 +
    // // ", step 2: "+ posUpd2 +
        ", now at: " + testEnt.getPosition());
    return pucmads;
  }

  @Override
  public void assertEquality(DiscretePositionUpdaterMasked original,
      DiscretePositionUpdaterMasked deserialisedVersion) {
    assertEquals(original.getStepSize(), deserialisedVersion.getStepSize());
    IMoveableEntity deserTestEnt =
        deserialisedVersion.getRegisteredEntities().iterator().next();
    assertTrue(original.getContPos(testEnt).isEqualTo(
        deserialisedVersion.getContPos(deserTestEnt)));
    assertTrue(original.getDiscretePos(testEnt).isEqualTo(
        deserialisedVersion.getDiscretePos(deserTestEnt)));
  }

  /**
   * IMoveableEntity implementation for the above test
   * 
   * @author Arne Bittig
   */
  private final class MoveableEntity implements IMoveableEntity,
      java.io.Serializable {

    private static final long serialVersionUID = -1192158550222554160L;

    private final IPositionVector pos;

    private final double diff;

    public MoveableEntity(IPositionVector pos, double diff) {
      this.pos = pos;
      this.diff = diff;
    }

    @Override
    public void move(IDisplacementVector disp) {
      pos.add(disp);
    }

    @Override
    public IPositionVector getPosition() {
      return pos;
    }

    @Override
    public IDisplacementVector getDrift() {
      return null;
    }

    @Override
    public double getDiffusionConstant() {
      return diff;
    }
  }
}