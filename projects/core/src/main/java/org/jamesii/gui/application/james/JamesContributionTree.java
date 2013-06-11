/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.swing.tree.TreePath;

import org.jamesii.core.util.graph.trees.binary.BinaryTree;
import org.jamesii.core.util.graph.trees.binary.BinaryTreeVertex;
import org.jamesii.gui.utils.AbstractTreeModel;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.IFormatter;

/**
 * @author Stefan Rybacki
 */
public class JamesContributionTree extends BinaryTree<Boolean> {

  /**
   * Instantiates a new contribution tree. With border layout pre initialized to
   * always fit {@link JamesContribution} preset layout.
   */
  public JamesContributionTree() {
    this(null);
    getNodeFor(JamesContribution.parsePosition(JamesContribution.BOTTOM_VIEW
        .getPosition()));
    getNodeFor(JamesContribution.parsePosition(JamesContribution.TOP_VIEW.getPosition()));
    getNodeFor(JamesContribution.parsePosition(JamesContribution.LEFT_VIEW.getPosition()));
    getNodeFor(JamesContribution.parsePosition(JamesContribution.EDITOR.getPosition()));
    getNodeFor(JamesContribution
        .parsePosition(JamesContribution.RIGHT_VIEW.getPosition()));
  }

  /**
   * Instantiates a new contribution tree.
   * 
   * @param parent
   *          the parent
   */
  private JamesContributionTree(JamesContributionTree parent) {
    super();
    setValue(null);
    setParent(parent);
  }

  private Object getNodeFor(List<JamesContributionNode> parsed) {
    if (parsed.size() > 0) {
      JamesContributionNode n = parsed.remove(0);
      if (getValue() == null) {
        setValue(n.isHorizontal());
      } else if (getValue().booleanValue() != n.isHorizontal()) {
        System.out.println("WARNING: Parsed Contribution " + parsed.toString()
            + " does not fit current tree layout!");
        // ignore this node and try to continue with next
        return getNodeFor(parsed);
      }

      if (parsed.size() > 0) {
        if (n.isLeft()) {
          BinaryTreeVertex<Boolean> left = getLeft();
          if (left == null) {
            setLeft(left = new JamesContributionTree(this));
          }

          return ((JamesContributionTree) left).getNodeFor(parsed);
        } else {
          BinaryTreeVertex<Boolean> right = getRight();
          if (right == null) {
            setRight(right = new JamesContributionTree(this));
          }

          return ((JamesContributionTree) right).getNodeFor(parsed);
        }
      }
    }

    return this;
  }

  public static void main(String[] args) {
    JamesContributionTree tree = new JamesContributionTree();

    System.out.println(tree);
  }

  @Override
  public String toString() {

    StringWriter output = new StringWriter();

    try {
      BasicUtilities.printTree(new AbstractTreeModel() {

        @Override
        public Object getChild(Object parent, int index) {
          if (parent instanceof BinaryTreeVertex<?>) {
            return index == 0 ? ((BinaryTreeVertex<?>) parent).getLeft()
                : ((BinaryTreeVertex<?>) parent).getRight();
          }

          return null;
        }

        @Override
        public int getChildCount(Object parent) {
          if (parent instanceof BinaryTreeVertex<?>) {
            if (((BinaryTreeVertex<?>) parent).getLeft() != null
                || ((BinaryTreeVertex<?>) parent).getRight() != null) {
              return 2;
            }
          }
          return 0;
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
          return 0;
        }

        @Override
        public Object getRoot() {
          return JamesContributionTree.this;
        }

        @Override
        public boolean isLeaf(Object node) {
          return false;
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
        }

      }, output, new IFormatter<BinaryTreeVertex<Boolean>>() {

        @Override
        public String format(BinaryTreeVertex<Boolean> t) {
          if (t != null && t.getValue() != null) {
            return t.getValue() ? "HORIZONTAL" : "VERTICAL";
          }
          return null;
        }

      });
    } catch (IOException e) {
      return super.toString();
    }

    return output.toString();
  }

}
