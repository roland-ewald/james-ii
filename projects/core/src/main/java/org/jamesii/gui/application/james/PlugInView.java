/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.plugins.IPluginTypeData;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.utils.FilteredTreeModel;
import org.jamesii.gui.utils.SortedTreeModel;
import org.jamesii.gui.utils.TextFilter;

/**
 * Basic view that displays information about the in JAMES II installed
 * plug-ins.
 * 
 * @author Stefan Rybacki
 */
public class PlugInView extends DefaultTreeView {
  /**
   * A tree model providing the plug-ins as tree like structure for use in a
   * {@link JTree}.
   * 
   * @author Stefan Rybacki
   */
  private static class PlugInTreeModel extends DefaultTreeModel {
    /**
     * Map that maps ids to plug-in type data.
     */
    private Map<String, IPluginTypeData> namePTDmapping;

    /**
     * Root node for tree model.
     */
    private DefaultMutableTreeNode root;

    /**
     * Creates a new instance.
     * 
     * @param plugins
     *          a list of plugin data to be represented as {@link TreeModel}
     */
    public PlugInTreeModel(List<IPluginData> plugins) {
      super(
          new DefaultMutableTreeNode("<html><b>Installed PlugIns</b></html>"),
          true);
      root = (DefaultMutableTreeNode) getRoot();
      if (plugins != null && plugins.size() > 0) {
        for (IPluginData pd : plugins) {
          DefaultMutableTreeNode node2;
          root.add(node2 = new DefaultMutableTreeNode(pd));

          node2.add(new DefaultMutableTreeNode(String.format(
              "<html>Name: <i>%s</i></html>", pd.getId().getName()), false));
          node2.add(new DefaultMutableTreeNode(String.format(
              "<html>Version: <i>%s</i></html>", pd.getId().getVersion()),
              false));

          DefaultMutableTreeNode node3;
          node2.add(node3 = new DefaultMutableTreeNode("Factories"));
          for (IFactoryInfo fi : pd.getFactories()) {
            node3.add(new DefaultMutableTreeNode(
                String.format(
                    "<html>%s <i>(%s)</i></html>",
                    fi.getClassname(),
                    fi.getDescription() != null
                        && fi.getDescription().length() > 0 ? fi
                        .getDescription() : "no Description"), false));
          }
        }
      }
    }

    /**
     * Creates a new instance.
     */
    public PlugInTreeModel() {
      super(new DefaultMutableTreeNode(
          "<html><b>Installed PlugIn types and PlugIns</b></html>"), true);
      root = (DefaultMutableTreeNode) getRoot();

      Registry registry = SimSystem.getRegistry();

      namePTDmapping = new HashMap<>();

      List<Class<? extends Factory<?>>> factories =
          registry.getKnownFactoryClasses();

      for (Class<? extends Factory<?>> c : factories) {
        Class<? extends AbstractFactory<?>> af =
            registry.getAbstractFactoryForBaseFactory(c);

        if (af != null) {
          IPluginTypeData ptd = registry.getPluginType(af);

          if (namePTDmapping.containsKey(ptd.getId().getName())) {
            continue;
          }

          namePTDmapping.put(ptd.getId().getName(), ptd);

          DefaultMutableTreeNode node;
          root.add(node =
              new DefaultMutableTreeNode(String.format(
                  "<html><b>%s</b> <i>(%s)</i></html>", ptd.getId().getName(),
                  ptd.getDescription())));

          List<IPluginData> plugins = registry.getPlugins(af);

          if (plugins != null && plugins.size() > 0) {
            for (IPluginData pd : plugins) {
              DefaultMutableTreeNode node2;
              node.add(node2 = new DefaultMutableTreeNode(pd));

              node2
                  .add(new DefaultMutableTreeNode(String.format(
                      "<html>Name: <i>%s</i></html>", pd.getId().getName()),
                      false));
              node2.add(new DefaultMutableTreeNode(String.format(
                  "<html>Version: <i>%s</i></html>", pd.getId().getVersion()),
                  false));

              node2.add(new DefaultMutableTreeNode(String.format(
                  "<html>License URI: <i>%s</i></html>",
                  pd.getLicenseURI() == null ? "none" : pd.getLicenseURI()
                      .toString()), false));

              DefaultMutableTreeNode node3;
              node2.add(node3 = new DefaultMutableTreeNode("Factories"));
              for (IFactoryInfo fi : pd.getFactories()) {
                node3.add(new DefaultMutableTreeNode(String.format(
                    "<html>%s <i>(%s)</i></html>",
                    fi.getClassname(),
                    fi.getDescription() != null
                        && fi.getDescription().length() > 0 ? fi
                        .getDescription() : "no Description"), false));
              }
            }
          }

        } else {
          root.add(new DefaultMutableTreeNode("No AbstractFactory found for "
              + c));
        }
      }
    }

    /**
     * Serialization ID.
     */
    private static final long serialVersionUID = -7755537722696968072L;

  }

  /**
   * Tree model able to filter another tree model.
   */
  private FilteredTreeModel<String> model;

  /**
   * Creates a new plugin view
   * 
   * @param contribution
   *          the vies's contribution
   */
  public PlugInView(Contribution contribution) {
    super("PlugIn Inspector", new DefaultTreeModel(null), contribution, null);
    model = new FilteredTreeModel<>(new PlugInTreeModel(), new TextFilter());
    setTreeModel(new SortedTreeModel(model));
  }

  /**
   * Creates a new plug-in view.
   * 
   * @param subject
   *          the place where the plug-ins are installed
   * @param plugins
   *          the list of installed plug-ins
   * @param contribution
   *          the vies's contribution
   */
  public PlugInView(String subject, List<IPluginData> plugins,
      Contribution contribution) {
    super(subject + ":PlugIn Inspector", new DefaultTreeModel(null),
        contribution, null);
    model =
        new FilteredTreeModel<>(new PlugInTreeModel(plugins), new TextFilter());
    setTreeModel(new SortedTreeModel(model));
  }

  @Override
  public JComponent createContent() {
    JPanel panel = new JPanel(new BorderLayout());

    JTextField filterTextField = new JTextField(30);

    Box filterTextBox = Box.createHorizontalBox();

    filterTextField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
        try {
          ((TextFilter) model.getFilter()).setFilterValue(e.getDocument()
              .getText(0, e.getDocument().getLength()).toLowerCase());
        } catch (BadLocationException e1) {
          SimSystem.report(e1);
        }
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        changedUpdate(e);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        changedUpdate(e);
      }
    });

    filterTextBox.add(Box.createHorizontalStrut(10));
    filterTextBox.add(new JLabel("Filter tree by text:"));
    filterTextBox.add(Box.createHorizontalStrut(5));
    filterTextBox.add(filterTextField);
    filterTextBox.add(Box.createHorizontalStrut(10));

    panel.add(filterTextBox, BorderLayout.NORTH);
    panel.add(super.createContent(), BorderLayout.CENTER);

    return panel;
  }

  @Override
  public String getWindowID() {
    return "org.jamesii.view.plugin";
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(500, 600);
  }
}
