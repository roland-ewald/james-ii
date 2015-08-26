/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui.dialogs;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.recording.selectiontrees.FactoryVertex;
import org.jamesii.perfdb.recording.selectiontrees.ParameterVertex;
import org.jamesii.perfdb.recording.selectiontrees.SelTreeSetVertex;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.Set;

/**
 * Renderer for the table displaying the
 * {@link org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet}.
 * 
 * @author Roland Ewald
 * 
 */
public class STSetTreeRenderer extends DefaultTreeCellRenderer {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4886028584218164716L;

  @Override
  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {

    JLabel label =
        (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded,
            leaf, row, focus);

    SelTreeSetVertex vertex =
        (SelTreeSetVertex) ((DefaultMutableTreeNode) value).getUserObject();

    if (vertex instanceof FactoryVertex<?>) {
      return facVertexLabel(label, (FactoryVertex<?>) vertex);
    }

    if (vertex instanceof ParameterVertex) {
      return paramVertexLabel(label, (ParameterVertex) vertex);
    }

    label.setText("Selection Tree Set");
    return label;
  }

  /**
   * Prepares label for {@link FactoryVertex}.
   * 
   * @param label
   *          the label to be used
   * @param vertex
   *          the current {@link FactoryVertex}
   * @return label with updated text
   */
  protected JLabel facVertexLabel(JLabel label, FactoryVertex<?> vertex) {
    Set<? extends Factory<?>> factories = vertex.getFactories();
    Factory<?> firstFac = factories.iterator().next();
    @SuppressWarnings("unchecked")
    // getClass() call for wild-card type
    Class<? extends AbstractFactory<?>> absFacClass =
        SimSystem.getRegistry().getAbstractFactoryForFactory(
            (Class<? extends Factory<?>>) firstFac.getClass());
    if (absFacClass == null) {
      label.setText("Unknown factory type");
      return label;
    }
    int facNum = vertex.getFactoryCount();
    label.setText(Strings.dispClassName(absFacClass) + " ["
        + (facNum - vertex.getConstraints().getIgnoreList().size()) + "/"
        + facNum + "]");
    return label;
  }

  /**
   * Prepares label for {@link ParameterVertex}.
   * 
   * @param label
   *          the label to be used
   * @param vertex
   *          the {@link ParameterVertex}
   * @return label with updated text
   */
  protected Component paramVertexLabel(JLabel label, ParameterVertex vertex) {
    label.setText(vertex.getParameterCount() + " Parameters");
    return label;
  }

}
