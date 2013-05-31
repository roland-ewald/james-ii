/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.tutorials;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamesii.SimSystem;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.gui.model.windows.plugintype.AbstractModelWindowFactory;
import org.jamesii.gui.model.windows.plugintype.ModelWindowFactory;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.wizard.AbstractWizardPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;

/**
 * Starting wizard page for the "Tutorial" workflow. Where this page provides
 * the ability to select a formalism for which is a model created and
 * parameterized in the steps afterwards.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
class FormalismChooser extends AbstractWizardPage implements
    ListSelectionListener {

  /**
   * The Constant FORMALISM.
   */
  public static final String FORMALISM = "formalism";

  /**
   * The Constant MODEL.
   */
  public static final String MODEL = "model";

  /**
   * list of formalisms
   */
  private JList list;

  /**
   * flag activatingg/deactivating next button
   */
  private boolean canNext = false;

  /**
   * info label showing informations about the selected formalism
   */
  private JTextPane infoLabel;

  /**
   * a temporary model instance of the selected formalism
   */
  private ISymbolicModel<?> model = null;

  /**
   * list of editor factories for the selected formalism / model
   */
  private List<ModelWindowFactory> modelWindowFactories;

  /**
   * caches previously created models so that it is not necessary to create a
   * model for a specific formalism over and over again
   */
  private final Map<ModelFactory, ISymbolicModel<?>> modelCache =
      new HashMap<>();

  /**
   * caches previously determined model window factories
   */
  private final Map<ISymbolicModel<?>, List<ModelWindowFactory>> modelWindowFactoryCache =
      new HashMap<>();

  /**
   * Instantiates a new formalism chooser.
   */
  public FormalismChooser() {
    super("Formalism selection",
        "Select a formalism you want to create a model for");
  }

  @Override
  protected JComponent createPage() {
    // create a list with available formalisms
    list = new JList(new FormalismListModel());
    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
            && list.getSelectedValue() != null) {
          fireNext();
        }
      }
    });
    list.setCellRenderer(new DefaultListCellRenderer() {
      private static final long serialVersionUID = -7780744852192174586L;

      @Override
      public Component getListCellRendererComponent(JList l, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {

        if (value instanceof ModelFactory) {
          value = ((ModelFactory) value).getFormalism().getName();
        }

        return super.getListCellRendererComponent(list, value, index,
            isSelected, cellHasFocus);
      }
    });
    list.addListSelectionListener(this);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    infoLabel = new JTextPane();
    infoLabel.setEditable(false);

    JPanel panel = new JPanel(new GridLayout(0, 2));
    panel.add(new JScrollPane(list));
    panel.add(new JScrollPane(infoLabel));

    JPanel header = new JPanel(new GridLayout(0, 2));
    header.add(new JLabel("Available Formalisms:"));
    header.add(new JLabel("Formalism Information:"));

    JPanel page = new JPanel(new BorderLayout());
    page.add(header, BorderLayout.NORTH);
    page.add(panel, BorderLayout.CENTER);

    return page;
  }

  @Override
  protected void persistData(IWizard wizard) {
    // store selected formalism
    wizard.putValue(FORMALISM, list.getSelectedValue());
    wizard.putValue(MODEL, model);
    if (modelWindowFactories.size() == 1) {
      wizard.putValue(EditorSelector.EDITOR, modelWindowFactories.get(0));
    }
  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
    // nothing to do here
  }

  @Override
  public boolean canBack(IWizard wizard) {
    return false;
  }

  @Override
  public boolean canHelp(IWizard wizard) {
    return false;
  }

  @Override
  public boolean canNext(IWizard wizard) {
    return canNext;
  }

  @Override
  public IWizardHelpProvider getHelp() {
    return null;
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    try {
      final ModelFactory value = (ModelFactory) list.getSelectedValue();
      canNext = value != null;

      final StringBuilder editors = new StringBuilder();

      model = modelCache.get(value);
      // try to determine available editors
      if (value != null) {
        if (model == null) {
          model = value.create();
          modelCache.put(value, model);
        }

        canNext = canNext && model != null;

        modelWindowFactories = modelWindowFactoryCache.get(model);

        if (modelWindowFactories == null) {
          // get editors available for formalism (if none also disable
          // next
          // button)
          ParameterBlock amwfp =
              new ParameterBlock(model, AbstractModelWindowFactory.MODEL);

          modelWindowFactories =
              SimSystem.getRegistry().getFactoryOrEmptyList(
                  AbstractModelWindowFactory.class, amwfp);

          modelWindowFactoryCache.put(model, modelWindowFactories);
        }

        if (modelWindowFactories.size() == 0) {
          // no editors
          canNext = false;
          editors
              .append("<blockquote><b><font color=\"#CC0000\">None</font></b></blockquote><br />This must not be due to an error.<br/>You can still (at least) use Java to create your model!");
        } else {
          editors.append("<ul>");
          for (ModelWindowFactory f : modelWindowFactories) {
            IFactoryInfo info =
                SimSystem.getRegistry().getFactoryInfo(f.getClass().getName());
            editors.append(String.format(
                "<li>%s %s</li>",
                f.getReadableName(),
                info.getDescription() != null
                    && info.getDescription().length() > 0 ? "("
                    + info.getDescription() + ")" : ""));
          }
          editors.append("</ul>");
        }
      }
      // show editor info

      // also update information about selected model factory
      BasicUtilities.invokeLaterOnEDT(new Runnable() {

        @Override
        public void run() {
          if (value != null) {
            String info =
                String
                    .format(
                        "<html><h1>%s (%s)</h1><h2>Comment</h2>%s<h2>Available Editors</h2>%s</html>",
                        value.getFormalism().getName(), value.getFormalism()
                            .getAcronym(), value.getFormalism().getComment(),
                        editors.toString());
            infoLabel.setContentType("text/html");
            infoLabel.setText(info);
            infoLabel.setCaretPosition(0);
          } else {
            infoLabel.setText("");
          }
        }

      });

      fireStatesChanged();
    } catch (Exception ex) {
      infoLabel.setText("");
      SimSystem.report(ex);
    }
  }

}
