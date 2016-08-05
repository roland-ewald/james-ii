/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.simpletree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.math.simpletree.binary.AddNode;
import org.jamesii.core.math.simpletree.binary.DivNode;
import org.jamesii.core.math.simpletree.binary.MultNode;
import org.jamesii.core.math.simpletree.binary.PowerNode;
import org.jamesii.core.math.simpletree.binary.SubtractNode;
import org.jamesii.core.math.simpletree.unary.InverseNode;
import org.jamesii.core.math.simpletree.unary.MinusNode;

/**
 * Helper class mostly for {@link DoubleNode#toString()}.
 *
 * @author Arne Bittig
 * @date 11.03.2014
 */
public final class DoubleNodes {

  private DoubleNodes() {
  }

  public static boolean strictlyLowerPriority(DoubleNode operand,
      DoubleNode parent) {
    return priorities.get(operand.getClass()) < priorities.get(parent
        .getClass());
  }

  public static boolean lowerPrioritySecond(DoubleNode secondOperand,
      DoubleNode parent) {
    if (associative.contains(parent.getClass())) {
      return priorities.get(secondOperand.getClass()) < priorities.get(parent
          .getClass());
    } else {
      return priorities.get(secondOperand.getClass()) <= priorities.get(parent
          .getClass());
    }
  }

  public static final Set<Class<? extends DoubleNode>> associative =
      new HashSet<Class<? extends DoubleNode>>() {

        private static final long serialVersionUID = 7303611363726608279L;

        {
          add(AddNode.class);
          add(MultNode.class);
        }
      };

  public static final Map<Class<? extends DoubleNode>, Integer> priorities =
      new HashMap<Class<? extends DoubleNode>, Integer>() {

        private static final long serialVersionUID = 6706320228124473958L;

        {
          put(MinusNode.class, 0);
          put(AddNode.class, 0);
          put(SubtractNode.class, 0);
          put(MultNode.class, 1);
          put(DivNode.class, 1);
          put(InverseNode.class, 1);
          put(PowerNode.class, 2);
          // put(IntNode.class, Integer.MAX_VALUE);
          // put(SquareNode.class, Integer.MAX_VALUE);
          // put(FixedValueNode.class, Integer.MAX_VALUE);
          // put(VariableNode.class, Integer.MAX_VALUE);
        }

        @Override
        public Integer get(Object arg0) {
          if (!this.containsKey(arg0)) {
            // variable or fixed value that do not need parentheses or
            // function node that comes with its own parentheses
            return Integer.MAX_VALUE;
          }
          return super.get(arg0);
        }

      };

}
