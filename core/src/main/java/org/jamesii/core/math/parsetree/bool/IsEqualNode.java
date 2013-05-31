/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.bool;

import org.jamesii.core.math.parsetree.BinaryNode;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;

/**
 * Node that represents an "is-equal" relation.
 * 
 * @author Jan Himmelspach
 */
public class IsEqualNode extends BinaryNode {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -2067437081228443383L;

  /**
   * Instantiates a new checks if is equal node.
   * 
   * @param left
   *          the left
   * @param right
   *          the right
   */
  public IsEqualNode(INode left, INode right) {
    super(left, right);
  }

  @SuppressWarnings("unchecked")
  @Override
  public INode calc(ValueNode<?> l, ValueNode<?> r) {
    // now check whether both values are Comparable if so check
    // whether one
    // is assignable to the other if not we can't use Comparable
    if (l.getValue() instanceof Comparable<?>
        && r.getValue() instanceof Comparable<?>) {
      Comparable<Object> lv = (Comparable<Object>) l.getValue();
      Comparable<Object> rv = (Comparable<Object>) r.getValue();

      // check for assignable
      if (lv.getClass().isAssignableFrom(rv.getClass())) {
        return new ValueNode<>(lv.compareTo(rv) == 0);
      }
      if (rv.getClass().isAssignableFrom(lv.getClass())) {
        return new ValueNode<>(rv.compareTo(lv) == 0);
      }
    }

    // check for numbers
    if (l.getValue() instanceof Number && r.getValue() instanceof Number) {
      return new ValueNode<>(Double.compare(
          ((Number) l.getValue()).doubleValue(),
          ((Number) r.getValue()).doubleValue()) == 0);
    }

    // use simple equals
    if (l.getValue() != null) {
      return new ValueNode<>(l.getValue().equals(r.getValue()));
    }
    if (r.getValue() != null) {
      return new ValueNode<>(r.getValue().equals(l.getValue()));
    }

    // return false
    return new ValueNode<>(false);

  }

  @Override
  public String getName() {
    return "=";
  }

}
