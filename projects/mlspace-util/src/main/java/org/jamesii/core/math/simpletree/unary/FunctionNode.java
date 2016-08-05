package org.jamesii.core.math.simpletree.unary;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jamesii.core.math.simpletree.DoubleNode;

public class FunctionNode extends AbstractUnaryNode {

  private final Method function;

  protected FunctionNode(DoubleNode sub, Method function) {
    super(sub);
    this.function = function;
  }

  @Override
  protected double performOperation(double val) {
    try {
      return (Double) function.invoke(val);
    } catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode sub) {
    return new FunctionNode(sub, function);
  }

}
