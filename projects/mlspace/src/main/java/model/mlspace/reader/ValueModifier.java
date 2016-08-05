/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.reader;

import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.Vectors;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.nullary.VariableNode;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.entities.values.AbstractValueRange;
import model.mlspace.rules.attributemodification.AdditionModification;
import model.mlspace.rules.attributemodification.CalcAttributeModification;
import model.mlspace.rules.attributemodification.ChangeToModification;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.attributemodification.MultiplicationModification;
import model.mlspace.rules.attributemodification.RandomAttributeModification;
import model.mlspace.rules.attributemodification.VarAssignmentAttributeModification;

/**
 * Workaround for parsing of simple attribute modifications (to be used in
 * parser only)
 * 
 * @author Arne Bittig
 * @date 22.11.2012
 */
public abstract class ValueModifier implements Serializable {

  private static final long serialVersionUID = 9038730683241335071L;

  abstract IAttributeModification getAttMod(String attName);

  /**
   * Complex arithmetic modification
   * 
   * @author Arne Bittig
   */
  public static class TreeValueModifier extends ValueModifier {

    private static final long serialVersionUID = 673336780391329579L;

    private final DoubleNode tree;

    private final String op;

    /**
     * @param tree
     *          Expression tree for calculation operations
     */
    public TreeValueModifier(DoubleNode tree) {
      this.op = "";
      this.tree = tree;
    }

    public TreeValueModifier(String op, DoubleNode tree) {
      this.op = op;
      this.tree = tree;
    }

    @Override
    IAttributeModification getAttMod(String attName) {
      switch (op) {
      case "":
        if (tree instanceof VariableNode) {
          return new VarAssignmentAttributeModification(attName,
              ((VariableNode) tree).getVarName());
        }
        return new CalcAttributeModification.Assign(attName, tree);
      case "*":
        return new CalcAttributeModification.Multiply(attName, tree);
      case "/":
        return new CalcAttributeModification.Divide(attName, tree);
      case "+":
        return new CalcAttributeModification.Add(attName, tree);
      case "-":
        return new CalcAttributeModification.Subtract(attName, tree);
      default:
        throw new IllegalArgumentException(
            "Only basic arithmetic ops supported, not " + op);
      }
    }

    /**
     * Helper method for ModEntity to RuleEntity conversion
     * 
     * @return node tree
     */
    public DoubleNode getTree() {
      return tree;
    }

  }

  public interface IRandomModifier {
    /**
     * @param rand
     *          Random number generator to use when determining new attribute
     *          value
     */
    public void setRand(IRandom rand);
  }

  /**
   * Random value from a given range
   * 
   * @author Arne Bittig
   */
  public static class RangeValueModifier extends ValueModifier
      implements IRandomModifier {

    private static final long serialVersionUID = -5826442458941729795L;

    private final AbstractValueRange<?> range;

    private IRandom rand = null;

    /**
     * @param range
     *          Value range
     */
    public RangeValueModifier(AbstractValueRange<?> range) {
      this.range = range;
    }

    @Override
    public void setRand(IRandom rand) {
      this.rand = rand;
    }

    @Override
    IAttributeModification getAttMod(String attName) {
      if (range.size() == 1) {
        return new ChangeToModification(attName, range.getRandomValue(null));
      }
      return new RandomAttributeModification(attName, range, rand);
    }

    /**
     * Helper method for ModEntity to RuleEntity conversion
     * 
     * @return value range
     */
    public AbstractValueRange<?> getRange() {
      return range;
    }
  }

  /**
   * Basic arithmetic value modification
   * 
   * @author Arne Bittig
   */
  public static class SimpleValueModifier extends ValueModifier {

    private static final long serialVersionUID = -7191573168964696964L;

    private final String op;

    private final Double changeBy;

    /**
     * @param op
     *          Operation (as single character as would be used in Java)
     * @param changeBy
     *          Value after operand
     */
    public SimpleValueModifier(String op, Double changeBy) {
      this.op = op;
      this.changeBy = changeBy;
    }

    @Override
    IAttributeModification getAttMod(String attName) {
      switch (op) {
      case "*":
        return new MultiplicationModification(attName, changeBy);
      case "/":
        return new MultiplicationModification(attName, 1. / changeBy);
      case "+":
        return new AdditionModification(attName, changeBy);
      case "-":
        return new AdditionModification(attName, -changeBy);
      default:
        throw new IllegalArgumentException(
            "Only basic arithmetic ops supported, not " + op);
      }
    }

    @Override
    public String toString() {
      return op + "=" + changeBy;
    }
  }

  public static class StringAssignmentModifier extends ValueModifier {
    private static final long serialVersionUID = 3884354955259947521L;

    private final String val;

    public String getValue() {
      return val;
    }

    public StringAssignmentModifier(String val) {
      this.val = val;
    }

    @Override
    IAttributeModification getAttMod(String attName) {
      return new AssignString(attName, val);
    }

    private static class AssignString implements IAttributeModification {

      private static final long serialVersionUID = 5119412073653924874L;

      private final String attName;

      private final Object value;

      public AssignString(String attName, Object value) {
        this.attName = attName;
        this.value = value;
      }

      @Override
      public String getAttribute() {
        return attName;
      }

      @Override
      public Object modifyAttVal(AbstractModelEntity ent,
          Map<String, Object> env) {
        Object prevVal = ent.getAttribute(attName);
        ent.setAttribute(attName, value);
        return prevVal;
      }

      @Override
      public String toString() {
        return attName + "=" + value;
      }
    }

  }

  /**
   * Complex arithmetic modification
   * 
   * @author Arne Bittig
   */
  public static class RandomVectorModifier extends ValueModifier
      implements IRandomModifier {

    private static final long serialVersionUID = 673336780391329579L;

    private final DoubleNode length;

    private IRandom rand;

    /**
     * @param lengthExpr
     *          Expression tree for calculation operations
     */
    public RandomVectorModifier(DoubleNode lengthExpr) {
      this.length = lengthExpr;
    }

    @Override
    IAttributeModification getAttMod(String attName) {
      if (SpatialAttribute.forName(attName) != SpatialAttribute.DRIFT) {
        throw new IllegalStateException("Random vector modifier used with "
            + attName + " attribute. Only valid for drift!");
      }
      return new RandomVectorModification(length, rand);
    }

    @Override
    public void setRand(IRandom rand) {
      this.rand = rand;
    }

    /**
     * Helper method for ModEntity to RuleEntity conversion
     * 
     * @return node tree
     */
    public DoubleNode getTree() {
      return length;
    }

    private static class RandomVectorModification
        implements IAttributeModification {

      private static final long serialVersionUID = -906490572681380227L;

      private final DoubleNode lengthExpr;

      private final IRandom rand;

      public RandomVectorModification(DoubleNode length, IRandom rand) {
        this.lengthExpr = length;
        this.rand = rand;
      }

      @Override
      public String getAttribute() {
        return SpatialAttribute.DRIFT.toString();
      }

      private static final double TAU = Math.PI * 2.;

      @Override
      public Object modifyAttVal(AbstractModelEntity ent,
          Map<String, Object> env) {
        IDisplacementVector drift = ((SpatialEntity) ent).getDrift();
        double length = lengthExpr.calculateValue((Map) env);
        double[] newCoords;
        if (drift.getDimensions() == 2) {
          newCoords =
              Vectors.polarToCartesian(length, rand.nextDouble() * TAU);
        } else if (drift.getDimensions() == 3) {
          newCoords = Vectors.sphericalToCartesian(length,
              rand.nextDouble() * TAU, Math.acos(2. * rand.nextDouble() - 1));
        } else {
          throw new IllegalStateException(
              "Random vector only for 2 or 3 dimensions");
        }
        IDisplacementVector newDrift = drift.copy();
        for (int d = newCoords.length; d > 0;) {
          newDrift.set(d, newCoords[--d]);
        }
        ent.setAttribute(SpatialAttribute.DRIFT.toString(), newDrift);
        return drift;
      }

    }
  }
}