/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.brownianmotion;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import model.mlspace.entities.spatial.IMoveableEntity;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.Vectors;

/**
 * @author Arne Bittig
 *
 */
public class DiscretePositionUpdaterMasked
// extends AbstractPositionUpdater
    implements IDiscretePositionUpdater {

  private static final double STEP_THRESHOLD_FACTOR = 0.5;

  private static final long serialVersionUID = -685637533571880185L;

  // parameters
  /** Position updater in continuous space whose steps to mask */
  private final IContinuousPositionUpdater posUpdTimeSteps;

  /** Desired distance per step in each dimension */
  private IDisplacementVector steps;

  private final double halfStepMinSq;

  // internal:
  /**
   * internal maps associating moving entities (IMovableEntitys) with their
   * "real" coordinates and previous "actual" positions and update vectors
   * 
   * Used maps must be based on object reference identities, not object hash
   * code identities if the hash codes change when the object's member variables
   * change, because keys (IMovableEntitys) will, in fact, change during the
   * simulation, but their associated vectors here need not change at the same
   * time. Also, they should use weak references for the keys because if key
   * objects become obsolete during the simulation (e.g. destroyed
   * IMoveableEntity entities), a reference to them would otherwise be kept here
   * needlessly, preventing them from being garbage collected (causing a memory
   * leak).
   */
  private transient Map<IMoveableEntity, IPositionVector> lastMaskedContPos =
      new WeakHashMap<>();

  private transient Map<IMoveableEntity, IPositionVector> lastActualDiscPos =
      new WeakHashMap<>();

  private transient Map<IMoveableEntity, IPositionVector> shouldBeDiscretePosAfterUpd =
      new WeakHashMap<>();

  private transient Map<IMoveableEntity, IDisplacementVector> lastContUpd =
      new WeakHashMap<>();

  /*
   * For invariable key types with properly implemented equals() methods and
   * variable key types with invariable hash codes (including those using object
   * identity & hashcode), WeakHashMaps should suffice here.
   * WeakIdentityHashMaps are available, e.g., from gnu.kawa.util (X11/MIT
   * license) or plume (license: SUN PROPRIETARY/CONFIDENTIAL). Some classes
   * with the same name do not fit (the one in com.sun.jmx.mbeanserver does not
   * implement Map and the one in org.jruby.util is not generic).
   */

  /**
   * Position update in discrete steps calculated from internal continuous
   * updates -- full constructor
   * 
   * @param steps
   *          Update step lengths (in each direction)
   * @param posUpdTimeSteps
   *          Internally used time-step based position updater
   */
  public DiscretePositionUpdaterMasked(IDisplacementVector steps,
      IContinuousPositionUpdater posUpdTimeSteps) {
    this.posUpdTimeSteps = posUpdTimeSteps;
    this.steps = steps;
    double halfStepMin = Vectors.vecNormMin(steps.toArray()) / 2;
    this.halfStepMinSq = halfStepMin * halfStepMin;
  }

  @Override
  public IDisplacementVector getPositionUpdate(double deltaT,
      IMoveableEntity ent) {

    IPositionVector currPos = ent.getPosition().copy();
    IPositionVector ladPos = lastActualDiscPos.get(ent);
    IPositionVector lmcPos = lastMaskedContPos.get(ent);
    IPositionVector sbdPos = shouldBeDiscretePosAfterUpd.get(ent);
    IDisplacementVector lUpd = lastContUpd.get(ent);

    if (allNull4(ladPos, lmcPos, sbdPos, lUpd)) {
      lastMaskedContPos.put(ent, currPos);
      lmcPos = currPos;
    } else if (anyNull4(ladPos, lmcPos, sbdPos, lUpd)) {
      // should always be all null or all non-null
      throw new IllegalStateException("Programmer error");
    } else if (sbdPos.isEqualTo(currPos)) {
      lastMaskedContPos.get(ent).add(lUpd); // updates lmcPos in place!
    } else if (!ladPos.isEqualTo(currPos) && !ladPos.isEqualTo(sbdPos)) {
      // ent has been moved by update not returned directly from here...
      // We have no way of knowing whether last returned update was
      // actually applied. We assume it was NOT.
      IDisplacementVector extUpd = ladPos.displacementTo(currPos);
      lastMaskedContPos.get(ent).add(extUpd); // updates lmcPos in place!
    }

    IDisplacementVector upd = posUpdTimeSteps.getPositionUpdate(deltaT, ent);

    IDisplacementVector newDiscreteDisp =
        currPos.displacementTo(lmcPos.plus(upd));
    discretizeVector(newDiscreteDisp);

    lastContUpd.put(ent, upd);
    lastActualDiscPos.put(ent, currPos);
    shouldBeDiscretePosAfterUpd.put(ent, currPos.plus(newDiscreteDisp));
    return newDiscreteDisp;
  }

  /**
   * Check whether all 4 given objects are null
   * 
   * @param o1
   * @param o2
   * @param o3
   * @param o4
   * @return true iff all os are null
   */
  private static boolean allNull4(Object o1, Object o2, Object o3, Object o4) {
    return o2 == null && o1 == null && o3 == null && o4 == null;
  }

  /**
   * Check whether any of 4 given objects is null
   * 
   * @param o1
   * @param o2
   * @param o3
   * @param o4
   * @return true iff at least one of the os is null
   */
  private static boolean anyNull4(Object o1, Object o2, Object o3, Object o4) {
    return o2 == null || o1 == null || o3 == null || o4 == null;
  }

  /**
   * If one can move in each dimension only one step of length given in
   * {@link #steps}, but in either direction, where should one go to get as
   * close to given vector as possible? (not to be confused with
   * {@link #adjustUpdateVector(IDisplacementVector)})
   * 
   * @param disp
   *          Displacement vector
   */
  private void discretizeVector(IDisplacementVector disp) {
    for (int d = 1; d <= disp.getDimensions(); d++) {
      double v = disp.get(d);
      double step = steps.get(d);
      if (v > step * STEP_THRESHOLD_FACTOR) {
        disp.set(d, step);
      } else if (v < -step * STEP_THRESHOLD_FACTOR) {
        disp.set(d, -step);
      } else {
        disp.set(d, 0);
      }
    }
  }

  @Override
  public double getReasonableTimeToNextUpdate(IMoveableEntity ent) {
    return halfStepMinSq / ent.getDiffusionConstant();
  }

  @Override
  public IDisplacementVector adjustUpdateVector(IDisplacementVector contV) {
    return adjustUpdateVector(contV, steps);
  }

  /**
   * Increase given vector along each coordinate to next integer multiple of
   * given step size
   * 
   * @param contV
   *          continuous space vector
   * @param steps
   *          Step size (may differ between dimensions)
   * @return Vector slightly longer than contV
   */
  public static IDisplacementVector adjustUpdateVector(
      IDisplacementVector contV, IDisplacementVector steps) {
    IDisplacementVector rv = contV.copy();
    for (int d = 1; d <= contV.getDimensions(); d++) {
      double ratio = contV.get(d) / steps.get(d);
      int rint = (int) ratio;
      // rint needs to be ratio rounded away from 0, actually
      double rem = ratio - rint;
      if (rem < 0) {
        rint--;
      } else if (rem > 0) {
        rint++;
      }

      rv.set(d, steps.get(d) * rint);
    }
    return rv;
  }

  @Override
  public IDisplacementVector getStepSize() {
    return steps;
  }

  @Override
  public boolean overrideStepSize(IDisplacementVector newStep) {
    if (lastMaskedContPos.isEmpty() && lastActualDiscPos.isEmpty()
        && shouldBeDiscretePosAfterUpd.isEmpty() && lastContUpd.isEmpty()) {
      this.steps = newStep;
      return true;
    }
    return false;
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException {
    try {
      in.defaultReadObject();
      lastMaskedContPos =
          new WeakHashMap<>(
              (Map<IMoveableEntity, IPositionVector>) in.readObject());
      shouldBeDiscretePosAfterUpd =
          new WeakHashMap<>(
              (Map<IMoveableEntity, IPositionVector>) in.readObject());
      lastContUpd =
          new WeakHashMap<>(
              (Map<IMoveableEntity, IDisplacementVector>) in.readObject());
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  // TODO: test this! ^ and v
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeObject(new LinkedHashMap<>(lastMaskedContPos));
    out.writeObject(new LinkedHashMap<>(shouldBeDiscretePosAfterUpd));
    out.writeObject(new LinkedHashMap<>(lastContUpd));
  }

  /**
   * Get known entities (method to allow testing of serialization)
   * 
   * @return Entities
   */
  Collection<IMoveableEntity> getRegisteredEntities() {
    assert lastMaskedContPos.keySet().equals(
        shouldBeDiscretePosAfterUpd.keySet());
    assert lastMaskedContPos.keySet().equals(lastContUpd.keySet());
    return lastMaskedContPos.keySet();
  }

  /**
   * Get last masked continuous-space position (method to allow testing of
   * serialization)
   * 
   * @param ent
   *          Moveable entity
   * @return position
   */
  IPositionVector getContPos(IMoveableEntity ent) {
    return lastMaskedContPos.get(ent);
  }

  /**
   * Discretized-space position where entity should be (method to allow testing
   * of serialization)
   * 
   * @param ent
   *          Moveable entity
   * @return position
   */
  IPositionVector getDiscretePos(IMoveableEntity ent) {
    return shouldBeDiscretePosAfterUpd.get(ent);
  }
}
