/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.tutorials;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.data.model.IModelWriter;
import org.jamesii.core.data.model.ModelFileWriterFactory;
import org.jamesii.core.data.model.parameter.read.plugintype.AbstractModelParameterReaderFactory;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.data.model.write.plugintype.AbstractModelWriterFactory;
import org.jamesii.core.data.model.write.plugintype.ModelWriterFactory;
import org.jamesii.core.factories.NoFactoryFoundException;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.model.ModelPerspective;
import org.jamesii.gui.model.windows.plugintype.AbstractModelWindowFactory;
import org.jamesii.gui.model.windows.plugintype.ModelWindow;
import org.jamesii.gui.model.windows.plugintype.ModelWindowFactory;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.AbstractFactoryParameterDialogFactory;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;
import org.jamesii.gui.utils.factories.FactorySelectionDialog;
import org.jamesii.gui.utils.factories.IFactoryDescriptionRenderer;
import org.jamesii.gui.utils.history.History;
import org.jamesii.gui.wizard.AbstractWizardPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;

/**
 * Implements a wizard page that provides the ability to edit a supplied model
 * during the "Tutorial" workflow. It additionally provides the option to open a
 * previously stored model of the same formalism and is also able to save the
 * created model after proceeding to the next wizard page.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
class ModelEditor extends AbstractWizardPage {

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class FactoryDescriptionRenderer implements
      IFactoryDescriptionRenderer<ModelFileWriterFactory> {
    @Override
    public String getDescription(ModelFileWriterFactory factory) {
      return factory.getDescription();
    }
  }

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class SetupUI implements Runnable {
    @Override
    public void run() {
      page.removeAll();
      JComponent content = window.getContent();

      toolBar = new JToolBar();

      loading = prepareLoading(model);
      saving = prepareSaving(model);

      Icon openIcon =
          IconManager.getIcon(IconIdentifier.OPEN_SMALL, "Open Model...");
      Icon saveIcon =
          IconManager.getIcon(IconIdentifier.SAVE_SMALL, "Save Model...");

      final Action saveAction = new AbstractAction("Save Model...", saveIcon) {
        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 2044546915919840389L;

        @Override
        public void actionPerformed(ActionEvent e) {
          saving.show((Component) e.getSource(), 0,
              ((Component) e.getSource()).getHeight());
        }

      };

      final Action openAction = new AbstractAction("Open Model...", openIcon) {

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
          loading.show((Component) e.getSource(), 0,
              ((Component) e.getSource()).getHeight());
        }

      };

      if (loading != null) {
        toolBar.add(openAction);
      }
      if (saving != null) {
        toolBar.add(saveAction);
      }
      page.add(toolBar, BorderLayout.NORTH);

      page.add(content, BorderLayout.CENTER);
      page.revalidate();
      page.repaint();
    }
  }

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class LoadAction extends AbstractAction {
    /**
     * 
     */
    private final IFactoryParameterDialog<?> dialog;

    private static final long serialVersionUID = 134320804L;

    /**
     * @param name
     * @param dialog
     */
    private LoadAction(String name, IFactoryParameterDialog<?> dialog) {
      super(name);
      this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Pair<ParameterBlock, ?> parameters = dialog.getFactoryParameter(null);

      if (parameters == null || parameters.getFirstValue() == null
          || parameters.getSecondValue() == null) {
        return;
      }

      IModelReader reader =
          ((ModelReaderFactory) parameters.getSecondValue()).create(parameters
              .getFirstValue());

      URI uri =
              parameters.getFirstValue().getSubBlockValue(IURIHandling.URI);

      model = reader.read(uri);

      if (model != null) {
        History.putValueIntoHistory(ModelPerspective.RECENTLY_USED_ID
            + History.SEPARATOR + "workflow", uri.toString());

        ParameterBlock pa =
            new ParameterBlock(model, AbstractModelWindowFactory.MODEL);

        ModelWindow<? extends ISymbolicModel<?>> w = editor.create(pa, null);
        JComponent jamesWin = w.getContent();

        window = w;

        page.removeAll();
        page.add(jamesWin, BorderLayout.CENTER);
        page.add(toolBar, BorderLayout.NORTH);
        page.revalidate();
      }
    }
  }

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class SaveAction extends AbstractAction {
    /**
     * 
     */
    private final IFactoryParameterDialog<?> dialog;

    private static final long serialVersionUID = 134320804L;

    /**
     * @param name
     * @param dialog
     */
    private SaveAction(String name, IFactoryParameterDialog<?> dialog) {
      super(name);
      this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Pair<ParameterBlock, ?> parameters = dialog.getFactoryParameter(null);

      if (parameters == null || parameters.getFirstValue() == null
          || parameters.getSecondValue() == null) {
        return;
      }

      window.prepareModelSaving();

      IModelWriter writer =
          ((ModelWriterFactory) parameters.getSecondValue()).create(parameters
              .getFirstValue());

      writer.write(ModelEditor.this.model, (URI) parameters.getFirstValue()
          .getSubBlockValue(IURIHandling.URI));
    }
  }

  /**
   * The Constant MODEL_LOCATION.
   */
  public static final String MODEL_LOCATION = "modelLocation";

  /**
   * the model to edit
   */
  private ISymbolicModel<?> model = null;

  /**
   * the actual SWING wizard page
   */
  private JPanel page = new JPanel();

  /**
   * the editor used to edit the model
   */
  private ModelWindow<? extends ISymbolicModel<?>> window;

  /**
   * {@link URI} to the stored instance of the symbolic model
   */
  private URI modelURI;

  /**
   * The editor.
   */
  private ModelWindowFactory editor;

  /**
   * The tool bar.
   */
  private JToolBar toolBar;

  /**
   * loading popup menu
   */
  private JPopupMenu loading;

  /**
   * saving popup menu
   */
  private JPopupMenu saving;

  /**
   * Instantiates a new model editor.
   */
  public ModelEditor() {
    super("Model Creation", "Create a model for the selected formalism");
  }

  @Override
  protected JComponent createPage() {
    page.setLayout(new BorderLayout());
    return page;
  }

  @Override
  protected void persistData(IWizard wizard) {
    page.removeAll();

    // remember the model (it might have been loaded and thus it might
    // have got a new pos in memory)
    wizard.putValue(FormalismChooser.MODEL, model);

    window.prepareModelSaving();
    // TODO sr137: give user the option to save model prior next step

    modelURI = null;
    if (modelURI == null) {
      try {
        // get all file factories that support the given symbolic
        // model
        ParameterBlock block =
            new ParameterBlock().addSubBl(AbstractModelWriterFactory.MODEL,
                model);

        List<ModelWriterFactory> factoryList =
            SimSystem.getRegistry().getFactoryList(
                AbstractModelWriterFactory.class, block);
        // no select only ModelFileWriterFactories
        List<ModelFileWriterFactory> list = new ArrayList<>();
        for (ModelWriterFactory f : factoryList) {
          if (f instanceof ModelFileWriterFactory) {
            list.add((ModelFileWriterFactory) f);
          }
        }

        ModelFileWriterFactory selected = list.get(0);

        if (list.size() > 1) {
          // store model as temporary file
          FactorySelectionDialog<ModelFileWriterFactory> dialog =
              new FactorySelectionDialog<>(null, list, new JLabel(
                  "Select format to use."), "Select Format to store model in",
                  true, new FactoryDescriptionRenderer());

          dialog.setVisible(true);

          if (dialog.isOkButtonPressed()) {
            List<ModelFileWriterFactory> selectedFactories =
                dialog.getSelectedFactories();
            if (selectedFactories != null && selectedFactories.size() > 0) {
              selected = selectedFactories.get(0);
            }
          }
        }

        if (selected != null) {
          File file =
              File.createTempFile("jamesmodel", "." + selected.getFileEnding());
          file.deleteOnExit();

          modelURI = Files.getURIFromFile(file);

          block.addSubBl(IURIHandling.URI, modelURI);
          IModelWriter writer = selected.create(block);

          writer.write(model, modelURI);
        }
      } catch (Throwable t) {
        modelURI = null;
        SimSystem.report(t);
      }
    }

    if (modelURI == null) {
      SimSystem.report(Level.SEVERE, null, "Could not save model!", null);
    }

    wizard.putValue(MODEL_LOCATION, modelURI);
  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
    // only do this if the model is null, otherwise we have loaded a
    // model and are rebuilding the front end!
    if (model == null) {
      model = wizard.getValue(FormalismChooser.MODEL);
    }
    editor = wizard.getValue(EditorSelector.EDITOR);

    ParameterBlock amwfp =
        new ParameterBlock(model, AbstractModelWindowFactory.MODEL);

    window = editor.create(amwfp, null);

    BasicUtilities.invokeLaterOnEDT(new SetupUI());
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
    return true;
  }

  @Override
  public IWizardHelpProvider getHelp() {
    return null;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(640, 400);
  }

  /**
   * Helper method that prepares parameter loading.
   * 
   * @param mod
   *          the model
   * @return the j popup menu
   */
  private JPopupMenu prepareLoading(ISymbolicModel<?> mod) {
    try {
      JPopupMenu result = new JPopupMenu("Loading");

      ParameterBlock params =
          new ParameterBlock().addSubBl(
              AbstractModelParameterReaderFactory.MODEL, mod);
      List<ModelReaderFactory> readerFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractModelReaderFactory.class, params);

      params =
          new ParameterBlock().addSubBl(
              AbstractFactoryParameterDialogFactory.ABSTRACT_FACTORY_CLASS,
              AbstractModelReaderFactory.class).addSubBl(
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
              new LoadAction(description != null ? description
                  : dialog.toString(), dialog);

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
  private JPopupMenu prepareSaving(ISymbolicModel<?> mod) {
    try {
      JPopupMenu result = new JPopupMenu("Saving");

      ParameterBlock params =
          new ParameterBlock().addSubBl(AbstractModelWriterFactory.MODEL, mod);
      List<ModelWriterFactory> writerFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractModelWriterFactory.class, params);

      params =
          new ParameterBlock().addSubBl(
              AbstractFactoryParameterDialogFactory.ABSTRACT_FACTORY_CLASS,
              AbstractModelWriterFactory.class).addSubBl(
              AbstractFactoryParameterDialogFactory.CONCRETE_FACTORIES,
              writerFactories);

      List<FactoryParameterDialogFactory<?, ?, ?>> dialogFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractFactoryParameterDialogFactory.class, params);

      for (FactoryParameterDialogFactory<?, ?, ?> d : dialogFactories) {
        try {
          final IFactoryParameterDialog<?> dialog = d.create(params);
          String description = dialog.getMenuDescription();

          Action s =
              new SaveAction(description != null ? description
                  : dialog.toString(), dialog);

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
