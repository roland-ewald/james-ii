/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.AbstractFactoryParameterDialogFactory;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogParameter;
import org.jamesii.gui.wizard.AbstractWizardPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;

/**
 * The Class ModelLoader.
 * 
 * @author Jan Himmelspach
 */
public class ModelLoader extends AbstractWizardPage {

  /**
   * The Constant MODEL_URI used to store the selected model URI when persisting
   * this page. Use this ID to retrieve the selected model on the next page.
   */
  public static final String MODEL_URI = ModelReaderFactory.URI;

  /**
   * The Constant MODEL used to store the selected and read symbolic model when
   * persisting this page. Use this ID to retrieve the selected symbolic model
   * on the next page.
   */
  public static final String MODEL = AbstractModelReaderFactory.MODEL;

  /**
   * The Constant MODELREADER_FACTORY used to store the selected model reader
   * factory when persisting this page. Use this ID to retrieve the selected
   * model reader factory on the next page.
   */
  public static final String MODELREADER_FACTORY = "ModelReaderFactory";

  /** The page. */
  private JPanel page;

  /** The model. */
  private JTextField modelLocation;

  /**
   * The model selection dialogs.
   */
  private List<IFactoryParameterDialog<ModelReaderFactory>> selectionDialogs;

  /**
   * The selected model uri.
   */
  private URI selectedModelURI = null;

  /**
   * The selected model reader factory.
   */
  private ModelReaderFactory modelReaderFactory = null;

  /**
   * The model.
   */
  private IModel model;

  /**
   * Instantiates a new model loader editor.
   */
  public ModelLoader() {
    super("Load a model", "Select a model to simulate (to experiment with)");
  }

  @Override
  protected JComponent createPage() {
    page = new JPanel();
    page.setLayout(new BorderLayout());

    JPanel top = new JPanel(new BorderLayout());
    page.add(top, BorderLayout.PAGE_START);

    top.add(new JLabel("Model to use: "), BorderLayout.LINE_START);
    top.add(modelLocation = new JTextField(), BorderLayout.CENTER);
    modelLocation.setEditable(false);

    JPanel bottom = new JPanel();
    page.add(bottom, BorderLayout.PAGE_END);

    JButton select;

    bottom.add(select = new JButton("Select model"));

    // init model selection options
    initModelSelectionOptions();

    select.setEnabled(selectionDialogs != null && !selectionDialogs.isEmpty());

    // create a popup menu from selection dialogs
    final JPopupMenu menu = new JPopupMenu("Selection Options");
    for (final IFactoryParameterDialog<ModelReaderFactory> dialog : selectionDialogs) {
      menu.add(new AbstractAction(dialog.getMenuDescription()) {

        /**
         * Serialization URI
         */
        private static final long serialVersionUID = -7067447316354832902L;

        @Override
        public void actionPerformed(ActionEvent e) {
          Pair<ParameterBlock, ModelReaderFactory> parameters =
              dialog.getFactoryParameter(SwingUtilities.getWindowAncestor(page));
          if (parameters != null) {
            IModel oldModel = model;
            try {
              // try to load model
              ParameterBlock readerParams =
                  new ParameterBlock(parameters.getFirstValue()
                      .getSubBlockValue(IURIHandling.URI), IURIHandling.URI);
              IModelReader reader =
                  parameters.getSecondValue().create(readerParams);
              model =
                  reader.read((URI) parameters.getFirstValue()
                      .getSubBlockValue(IURIHandling.URI),
                      new HashMap<String, Object>());

              selectedModelURI =
                  parameters.getFirstValue().getSubBlockValue(IURIHandling.URI);
              modelReaderFactory = parameters.getSecondValue();
              modelLocation.setText(selectedModelURI.toString());
            } catch (Exception ex) {
              SimSystem.report(ex);
              JOptionPane.showMessageDialog(page,
                  "Couldn't not create executeable model from selected URI",
                  "Error", JOptionPane.ERROR_MESSAGE);
              model = oldModel;
            }

          }
          fireNextChanged();
        }
      });
    }

    select.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        Component c = (Component) arg0.getSource();
        menu.show(c, 0, c.getHeight());
      }

    });

    return page;
  }

  /**
   * Helper method that loads all available factory parameter dialogs that are
   * able to provide model locations and appropriate reader factories. Those are
   * used as selection option for models.
   */
  @SuppressWarnings("unchecked")
  private void initModelSelectionOptions() {
    ParameterBlock params =
        FactoryParameterDialogParameter.getParameterBlock(
            ModelReaderFactory.class, null);

    selectionDialogs = new ArrayList<>();

    try {
      List<FactoryParameterDialogFactory<?, ?, ?>> factoryList =
          SimSystem.getRegistry().getFactoryOrEmptyList(
              AbstractFactoryParameterDialogFactory.class, params);

      for (FactoryParameterDialogFactory<?, ?, ?> dialogFactory : factoryList) {
        try {
          IFactoryParameterDialog<ModelReaderFactory> dialog =
              (IFactoryParameterDialog<ModelReaderFactory>) dialogFactory
                  .create(params);
          selectionDialogs.add(dialog);
        } catch (Exception e) {
          SimSystem.report(e);
        }
      }
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  @Override
  protected void persistData(IWizard wizard) {
    wizard.putValue(MODEL_URI, selectedModelURI);
    wizard.putValue(MODELREADER_FACTORY, modelReaderFactory);
    wizard.putValue(MODEL, model);
  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean canBack(IWizard wizard) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean canHelp(IWizard wizard) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean canNext(IWizard wizard) {
    return selectedModelURI != null && modelReaderFactory != null
        && model != null;
  }

  @Override
  public IWizardHelpProvider getHelp() {
    // TODO Auto-generated method stub
    return null;
  }

}
