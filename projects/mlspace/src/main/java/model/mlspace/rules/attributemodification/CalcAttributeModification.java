/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules.attributemodification;

import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;

import org.jamesii.core.math.simpletree.DoubleNode;

/**
 * 
 * @author Arne Bittig
 * @date 07.04.2014
 */
public abstract class CalcAttributeModification implements
    IAttributeModification {

  private static final long serialVersionUID = 5777842775073238782L;

  private final DoubleNode node;

  private final String attName;

  public CalcAttributeModification(String attName, DoubleNode tree) {
    this.attName = attName;
    this.node = tree;
  }

  @Override
  public String getAttribute() {
    return attName;
  }

  @Override
  public Object modifyAttVal(AbstractModelEntity ent, Map<String, Object> env) {
    Object prevVal = ent.getAttribute(attName);
    ent.setAttribute(attName, calcNewValue(prevVal, node, env));
    // CHECK: type safety?
    return prevVal;
  }

  protected abstract double calcNewValue(Object prevVal, DoubleNode node2,
      Map<String, Object> env);

  @Override
  public final String toString() {
    return attName + opStr() + '=' + node.toString();
  }

  protected abstract String opStr();

  public static class Assign extends CalcAttributeModification {

    /**
     * @param attName
     * @param tree
     */
    public Assign(String attName, DoubleNode tree) {
      super(attName, tree);
    }

    private static final long serialVersionUID = 1178248872368696481L;

    @Override
    protected double calcNewValue(Object prevVal, DoubleNode node,
        Map<String, Object> env) {
      return node.calculateValue((Map) env);
    }

    @Override
    protected String opStr() {
      return "";
    }
  }

  public static class Add extends CalcAttributeModification {

    private static final long serialVersionUID = 7815370002794328245L;

    public Add(String attName, DoubleNode tree) {
      super(attName, tree);
    }

    @Override
    protected double calcNewValue(Object prevVal, DoubleNode node,
        Map<String, Object> env) {
      return ((Number) prevVal).doubleValue() + node.calculateValue((Map) env);
    }

    @Override
    protected String opStr() {
      return "+";
    }
  }

  public static class Subtract extends CalcAttributeModification {

    private static final long serialVersionUID = 474654845685507455L;

    /**
     * @param attName
     * @param tree
     */
    public Subtract(String attName, DoubleNode tree) {
      super(attName, tree);
    }

    @Override
    protected double calcNewValue(Object prevVal, DoubleNode node,
        Map<String, Object> env) {
      return ((Number) prevVal).doubleValue() - node.calculateValue((Map) env);
    }

    @Override
    protected String opStr() {
      return "-";
    }
  }

  public static class Multiply extends CalcAttributeModification {

    private static final long serialVersionUID = -2884389738444761347L;

    public Multiply(String attName, DoubleNode tree) {
      super(attName, tree);
    }

    @Override
    protected double calcNewValue(Object prevVal, DoubleNode node,
        Map<String, Object> env) {
      return ((Number) prevVal).doubleValue() * node.calculateValue((Map) env);
    }

    @Override
    protected String opStr() {
      return "*";
    }
  }

  public static class Divide extends CalcAttributeModification {

    private static final long serialVersionUID = 6990270914111583618L;

    public Divide(String attName, DoubleNode tree) {
      super(attName, tree);
    }

    @Override
    protected double calcNewValue(Object prevVal, DoubleNode node,
        Map<String, Object> env) {
      return ((Number) prevVal).doubleValue() / node.calculateValue((Map) env);
    }

    @Override
    protected String opStr() {
      return "/";
    }
  }
}
