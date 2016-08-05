package model.mlspace.entities.values;

import java.util.Iterator;
import java.util.Map;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.nullary.VariableNode;

/**
 * Workaround for single value resulting from calculation with initially unknown
 * local variables
 * 
 * @author Arne Bittig
 * @date 28.07.2014
 */
public class VariableValue extends AbstractValueRange<Double> {

  private static final long serialVersionUID = 3073751336880871448L;

  private final DoubleNode node;

  public VariableValue(DoubleNode node) {
    this.node = node;
  }

  /**
   * @param environment
   * @return Value
   * @see org.jamesii.core.math.simpletree.DoubleNode#calculateValue(java.util.Map)
   */
  public Object calculateValue(Map<String, ? extends Number> environment) {
    if (node instanceof VariableNode) {
      // for pass-through of non-numeric attribute values
      return environment.get(((VariableNode) node).getVarName());
    }
    return node.calculateValue(environment);
  }

  @Override
  public Iterator<Double> iterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(Object obj) {
    throw new UnsupportedOperationException();
  }

  @Override
  public double size() {
    return 1;
  }

  @Override
  public Double getRandomValue(IRandom rand) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return node.toString();
  }
}
