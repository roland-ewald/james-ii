/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.tutorials;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.parameter.IModelParameterReader;
import org.jamesii.core.data.model.parameter.IModelParameterWriter;
import org.jamesii.core.data.model.parameter.read.plugintype.AbstractModelParameterReaderFactory;
import org.jamesii.core.data.model.parameter.read.plugintype.ModelParameterReaderFactory;
import org.jamesii.core.data.model.parameter.write.plugintype.AbstractModelParameterWriterFactory;
import org.jamesii.core.data.model.parameter.write.plugintype.ModelParameterWriterFactory;
import org.jamesii.core.factories.NoFactoryFoundException;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.model.parametersetup.plugintype.ModelParameterSetupWindowFactory;
import org.jamesii.gui.model.parametersetup.plugintype.ModelParameterWindow;
import org.jamesii.gui.model.windows.plugintype.AbstractModelWindowFactory;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.AbstractFactoryParameterDialogFactory;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;
import org.jamesii.gui.wizard.AbstractWizardPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;

// TODO: Auto-generated Javadoc
/**
 * This class provides a wizard page that is able to show a parameter editor for
 * a given {@link ISymbolicModel}.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
class ModelParameterEditor extends AbstractWizardPage {

  /**
   * The Constant MODEL_PARAMETERS.
   */
  public static final String MODEL_PARAMETERS = "model.parameters";

  /**
   * The Constant USER_PARAMETERS.
   */
  public static final String USER_PARAMETERS = "user.parameters";

  /**
   * The page.
   */
  private JPanel page;

  /**
   * The model.
   */
  private ISymbolicModel<?> model;

  /**
   * The model parameter window.
   */
  private ModelParameterWindow<? extends ISymbolicModel<?>> modelParameterWindow;

  /**
   * Instantiates a new model parameter editor.
   */
  public ModelParameterEditor() {
    super("Parameter setup for model", "Parameterize the created model");
  }

  /**
   * Creates the page.
   * 
   * @return the j component
   */
  @Override
  protected JComponent createPage() {
    page = new JPanel();
    page.setLayout(new BorderLayout());
    return page;
  }

  /**
   * Persist data.
   * 
   * @param wizard
   *          the wizard
   */
  @Override
  protected void persistData(IWizard wizard) {
    if (modelParameterWindow != null) {
      wizard.putValue(MODEL_PARAMETERS, modelParameterWindow.getParameters());
      wizard
          .putValue(USER_PARAMETERS, modelParameterWindow.getUserParameters());
    }
  }

  /**
   * Prepopulate page.
   * 
   * @param wizard
   *          the wizard
   */
  @Override
  protected void prepopulatePage(IWizard wizard) {
    // get model
    model = (ISymbolicModel<?>) wizard.getValue(FormalismChooser.MODEL);

    // find editors for model TODO use automatic factory filter here
    Registry registry = SimSystem.getRegistry();

    List<ModelParameterSetupWindowFactory> factories =
        registry.getFactories(ModelParameterSetupWindowFactory.class);

    if (factories == null) {
      BasicUtilities.invokeLaterOnEDT(new Runnable() {

        @Override
        public void run() {
          page.add(new JLabel("No Parameter Editor found!!!"),
              BorderLayout.CENTER);
          page.revalidate();
          page.repaint();
        }

      });
      return;
    }

    ParameterBlock amwfp =
        new ParameterBlock(model, AbstractModelWindowFactory.MODEL);

    ModelParameterSetupWindowFactory editor = null;
    for (ModelParameterSetupWindowFactory f : factories) {
      if (f != null && f.supportsParameters(amwfp) > 0) {
        editor = f;
        break;
      }
    }

    if (editor == null) {
      BasicUtilities.invokeLaterOnEDT(new Runnable() {

        @Override
        public void run() {
          page.add(new JLabel("No Parameter Editor found!!!"),
              BorderLayout.CENTER);
          page.revalidate();
          page.repaint();
        }

      });
      return;
    }

    modelParameterWindow = editor.create(amwfp);

    // TODO sr137: replace this by getWizardContent in a dedicated
    // interface
    final JComponent c = modelParameterWindow.getContent();

    final JPopupMenu loadingMenu = prepareParameterLoading(model);

    final JPopupMenu savingMenu = prepareParameterSaving(model);

    Action load = null;
    if (loadingMenu != null) {
      load =
          new AbstractAction("Load", IconManager.getIcon(
              IconIdentifier.OPEN_SMALL, "Load")) {
            /**
             * Serialization ID
             */
            private static final long serialVersionUID = 43294802L;

            @Override
            public void actionPerformed(ActionEvent e) {
              loadingMenu.show((Component) e.getSource(), 0,
                  ((Component) e.getSource()).getHeight());
            }
          };
    }

    Action save = null;

    if (savingMenu != null) {
      save =
          new AbstractAction("Save", IconManager.getIcon(
              IconIdentifier.SAVEAS_SMALL, null)) {

            /**
             * Serialization ID
             */
            private static final long serialVersionUID = 2L;

            @Override
            public void actionPerformed(ActionEvent e) {
              savingMenu.show((Component) e.getSource(), 0,
                  ((Component) e.getSource()).getHeight());
            }

          };
    }

    JToolBar toolBar = new JToolBar();
    if (load != null) {
      toolBar.add(load);
    }
    if (save != null) {
      toolBar.add(save);
    }
    page.add(toolBar, BorderLayout.NORTH);

    BasicUtilities.invokeLaterOnEDT(new Runnable() {

      @Override
      public void run() {
        page.add(c, BorderLayout.CENTER);
        page.revalidate();
        page.repaint();
      }

    });
  }

  /**
   * Can back.
   * 
   * @param wizard
   *          the wizard
   * @return true, if successful
   */
  @Override
  public boolean canBack(IWizard wizard) {
    return false;
  }

  /**
   * Can help.
   * 
   * @param wizard
   *          the wizard
   * @return true, if successful
   */
  @Override
  public boolean canHelp(IWizard wizard) {
    return false;
  }

  /**
   * Can next.
   * 
   * @param wizard
   *          the wizard
   * @return true, if successful
   */
  @Override
  public boolean canNext(IWizard wizard) {
    return true;
  }

  /**
   * Gets the help.
   * 
   * @return the help
   */
  @Override
  public IWizardHelpProvider getHelp() {
    return null;
  }

  /**
   * Helper method that prepares parameter loading.
   * 
   * @param mod
   *          the model
   * @return the j popup menu
   */
  private JPopupMenu prepareParameterLoading(ISymbolicModel<?> mod) {
    try {
      JPopupMenu result = new JPopupMenu("Loading");

      ParameterBlock params =
          new ParameterBlock().addSubBl(
              AbstractModelParameterReaderFactory.MODEL, mod);
      List<ModelParameterReaderFactory> readerFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractModelParameterReaderFactory.class, params);

      params =
          new ParameterBlock().addSubBl(
              AbstractFactoryParameterDialogFactory.ABSTRACT_FACTORY_CLASS,
              AbstractModelParameterReaderFactory.class).addSubBl(
              AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES,
              readerFactories);

      List<FactoryParameterDialogFactory<?, ?, ?>> dialogFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractFactoryParameterDialogFactory.class, params);

      for (FactoryParameterDialogFactory<?, ?, ?> d : dialogFactories) {
        try {
          final IFactoryParameterDialog<?> dialog = d.create(params);
          String description = dialog.getMenuDescription();

          Action l =
              new AbstractAction(description != null ? description
                  : dialog.toString()) {
                private static final long serialVersionUID = 134320804L;

                @Override
                public void actionPerformed(ActionEvent e) {
                  Pair<ParameterBlock, ?> parameters =
                      dialog.getFactoryParameter(null);

                  if (parameters == null || parameters.getFirstValue() == null
                      || parameters.getSecondValue() == null) {
                    return;
                  }

                  IModelParameterReader reader =
                      ((ModelParameterReaderFactory) parameters
                          .getSecondValue()).create(parameters.getFirstValue());

                  Map<String, ?> p =
                      reader.read((URI) parameters.getFirstValue()
                          .getSubBlockValue(IURIHandling.URI));
                  ModelParameterEditor.this.modelParameterWindow
                      .setParameters(p);
                }

              };

          result.add(l);
        } catch (Exception e) {
          SimSystem.report(e);
        }
      }

      return result;
    } catch (NoFactoryFoundException e) {
    } catch (Exception e) {
      SimSystem.report(e);
    }

    return null;
  }

  /**
   * Helper method that prepares parameter saving.
   * 
   * @param mod
   *          the model
   * @return the j popup menu
   */
  private JPopupMenu prepareParameterSaving(ISymbolicModel<?> mod) {
    try {
      JPopupMenu result = new JPopupMenu("Saving");

      ParameterBlock params =
          new ParameterBlock().addSubBl(
              AbstractModelParameterWriterFactory.MODEL, mod);
      List<ModelParameterWriterFactory> writerFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractModelParameterWriterFactory.class, params);

      params =
          new ParameterBlock().addSubBl(
              AbstractFactoryParameterDialogFactory.ABSTRACT_FACTORY_CLASS,
              AbstractModelParameterWriterFactory.class).addSubBl(
              AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES,
              writerFactories);

      List<FactoryParameterDialogFactory<?, ?, ?>> dialogFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractFactoryParameterDialogFactory.class, params);

      for (FactoryParameterDialogFactory<?, ?, ?> d : dialogFactories) {
        // TODO sr137: centralize the loading of dialogs for factories
        try {
          final IFactoryParameterDialog<?> dialog = d.create(params);
          String description = dialog.getMenuDescription();

          Action s =
              new AbstractAction(description != null ? description
                  : dialog.toString()) {
                private static final long serialVersionUID = 134320804L;

                @Override
                public void actionPerformed(ActionEvent e) {
                  Pair<ParameterBlock, ?> parameters =
                      dialog.getFactoryParameter(null);

                  if (parameters == null || parameters.getFirstValue() == null
                      || parameters.getSecondValue() == null) {
                    return;
                  }

                  IModelParameterWriter writer =
                      ((ModelParameterWriterFactory) parameters
                          .getSecondValue()).create(parameters.getFirstValue());

                  writer.write((URI) parameters.getFirstValue()
                      .getSubBlockValue(IURIHandling.URI), modelParameterWindow
                      .getParameters());
                }

              };

          result.add(s);
        } catch (Exception e) {
          SimSystem.report(e);
        }
      }

      return result;
    } catch (NoFactoryFoundException e) {
    } catch (Exception e) {
      SimSystem.report(e);
    }
    return null;
  }
}
