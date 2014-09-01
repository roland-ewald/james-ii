/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jamesii.SimSystem;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.ModelFileReaderFactory;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.application.ApplicationDialog;
import org.jamesii.gui.application.preferences.config.ConfFile;
import org.jamesii.gui.model.dialogs.BrowseModelsFSDialog;
import org.jamesii.gui.model.dialogs.OpenModelData;
import org.jamesii.gui.model.dialogs.OpenModelFromFileSystem;
import org.jamesii.gui.utils.dialogs.IBrowseFSDialogEntry;
import org.jamesii.gui.utils.dialogs.IBrowseFSDialogListener;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogParameter;
import org.jamesii.gui.utils.history.History;

/**
 * This dialog can be used to choose from a set of benchmark models.
 * 
 * @author Roland Ewald
 */
public class CreateNewExperimentDialog extends ApplicationDialog {

  /** Serialisation ID. */
  private static final long serialVersionUID = -5273431621702677003L;

  /** Panel for controls. */
  private JPanel buttonPanel = new JPanel(new BorderLayout());

  /** Button to clear old model URIs. */
  private JButton clearButton = new JButton("Clear");

  /** Configuration file. */
  private ConfFile configFile = null;

  /** Reference to created experiment. */
  private BaseExperiment experiment = null;

  /** Text field to enter experiment name. */
  private JTextField experimentNameField = new JTextField(30);

  /** Button to open 'browse for model' - dialog. */
  private JButton getURIFromBrowsingFSButton = new JButton(
      "Browse File System...");

  /** Button to get a URI from the open model form FS dialog. */
  private JButton getURIFromFileSystemButton = new JButton("From File...");

  /** Combobox to enter a model URI. */
  private JComboBox modelSelectionBox = new JComboBox();

  /** List of model URIs. */
  private List<String> modelURIs = History.getValues(getClass().getName(),
      true, History.LATEST, 10);

  /** OK button. */
  private JButton okButton = new JButton("OK");

  {
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // modelSelectionBox.commit();
        createExperiment();
      }
    });
  }
  {
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // modelSelectionBox.removeAllItems();
      }
    });
  }

  {
    buttonPanel.add(clearButton, BorderLayout.WEST);
    buttonPanel.add(okButton, BorderLayout.EAST);
  }
  {
    modelSelectionBox.setEditable(true);
  }

  {
    getURIFromFileSystemButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        OpenModelFromFileSystem opffs =
            new OpenModelFromFileSystem(FactoryParameterDialogParameter
                .getParameterBlock(ModelFileReaderFactory.class, null));

        // TODO sr137: provide model loading as in the rest of gui
        // (IFactoryParameterDialog)
        Pair<ParameterBlock, ModelFileReaderFactory> amrwfp =
            opffs.getFactoryParameter(getOwner());
        if (amrwfp != null) {
          addModelURI((URI) ParameterBlocks.getSubBlockValue(
              amrwfp.getFirstValue(), IURIHandling.URI));
          // TODO: Allow parameter block editing
        }
      }
    });
  }
  {
    getURIFromBrowsingFSButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        /*
         * BrowseModelsFSDialog browseModelFSDialog = new BrowseModelsFSDialog(
         * getOwner()) { /** Serialization ID.
         * 
         * private static final long serialVersionUID = 57159494364531977L;
         * 
         * @Override protected void executeModelSelection(OpenModelData
         * modelData) { addModelURI((URI)
         * modelData.getParameters().getSubBlockValue("URI"));
         * setVisible(false); } };
         * 
         * browseModelFSDialog.setModal(true);
         * browseModelFSDialog.setVisible(true);
         */

        // FIXME sr137: use IFactoryParameterDialog instead
        List<ModelFileReaderFactory> list =
            SimSystem.getRegistry().getFactories(ModelFileReaderFactory.class);

        final BrowseModelsFSDialog browseModelFSDialog =
            new BrowseModelsFSDialog(getOwner(), list);
        browseModelFSDialog
            .addBrowseFSDialogListener(new IBrowseFSDialogListener() {

              @Override
              public void elementChosen(IBrowseFSDialogEntry e1) {

                File file = e1.getFile();

                Map<String, ModelFileReaderFactory> factoryMap =
                    new HashMap<>();

                List<ModelReaderFactory> mrwFactories =
                    SimSystem.getRegistry().getFactories(
                        ModelReaderFactory.class);
                for (ModelReaderFactory mrwFactory : mrwFactories) {
                  if (mrwFactory instanceof ModelFileReaderFactory) {
                    ModelFileReaderFactory fileFactory =
                        (ModelFileReaderFactory) mrwFactory;
                    factoryMap.put(fileFactory.getFileEnding(), fileFactory);
                  }
                }

                String ending = Files.getFileEnding(file).toLowerCase();

                ParameterBlock pb = new ParameterBlock();
                try {
                  pb.addSubBlock("URI",
                      new ParameterBlock(Files.getURIFromFile(file)));
                } catch (URISyntaxException e2) {
                  ApplicationLogger
                      .log(
                          Level.SEVERE,
                          "Was not able to load the model. Please check your classpath settings.",
                          e2);
                }

                OpenModelData modelData =
                    new OpenModelData(file.getName(), factoryMap.get(ending),
                        pb);
                addModelURI((URI) modelData.getParameters().getSubBlockValue(
                    "URI"));
                browseModelFSDialog.setVisible(false);

              }
            });

        browseModelFSDialog.setModal(true);
        // browseModelFSDialog.pack();
        browseModelFSDialog.setVisible(true);
      }
    });
  }

  /**
   * Default constructor.
   * 
   * @param owner
   *          owner of the window
   * @param cFile
   *          configuration file (to load/store formerly used URIs)
   */
  public CreateNewExperimentDialog(Window owner, ConfFile cFile) {
    super(owner);
    configFile = cFile;
    this.setTitle("Choose Model:");
    this.setModal(true);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(new JLabel("Please enter a valid Model URI:"),
        BorderLayout.NORTH);

    for (String modelURI : modelURIs) {
      modelSelectionBox.addItem(modelURI);
    }

    JPanel experimentNamePanel = new JPanel();
    experimentNamePanel.add(new JLabel("Experiment name:"));
    experimentNamePanel.add(experimentNameField);

    JPanel modelSelectionPanel = new JPanel();
    modelSelectionPanel.setLayout(new BoxLayout(modelSelectionPanel,
        BoxLayout.Y_AXIS));
    // sr137: little hack to avoid combobox stretching, entire dialog
    // needs
    // recreation anyways
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(modelSelectionBox, BorderLayout.NORTH);
    JPanel modelSelectPanel = embedToPanel(panel);

    JPanel uriHelperButtons = new JPanel();
    uriHelperButtons.add(getURIFromFileSystemButton);
    uriHelperButtons.add(getURIFromBrowsingFSButton);

    modelSelectPanel.add(uriHelperButtons, BorderLayout.EAST);
    modelSelectionPanel.add(modelSelectPanel);
    modelSelectionPanel.add(embedToPanel(experimentNamePanel));

    getContentPane().add(modelSelectionPanel, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(null);
  }

  /**
   * Adds a model URI to dialog components.
   * 
   * @param uri
   *          the URI to be added
   */
  protected void addModelURI(URI uri) {
    String uriString = uri.toString();
    modelSelectionBox.addItem(uriString);
    modelSelectionBox.setSelectedItem(uriString);
  }

  /**
   * Creates Experiment.
   */
  protected void createExperiment() {

    if (modelSelectionBox.getSelectedItem() == null) {
      setVisible(false);
      return;
    }

    String uriString = modelSelectionBox.getSelectedItem().toString();
    History.putValueIntoHistory(getClass().getName(), uriString);

    URI uriToUse = null;
    try {
      uriToUse = new URI(uriString);
    } catch (URISyntaxException ex) {
      SimSystem.report(Level.SEVERE, "Error: Malformed URI. The URI '"
          + uriString + "' is malformed.");
      return;
    }

    experiment = new BaseExperiment();
    experiment.setName(experimentNameField.getText());
    experiment.setModelLocation(uriToUse);
    // TODO: Let model reading be parameterisable
    // experiment.setModelRWParameters();
    this.setVisible(false);
  }

  /**
   * Embed to panel.
   * 
   * @param component
   *          the component
   * @return the panel
   */
  private JPanel embedToPanel(Component component) {
    JPanel retPanel = new JPanel(new BorderLayout());
    retPanel.add(new JPanel(), BorderLayout.NORTH);
    retPanel.add(new JPanel(), BorderLayout.WEST);
    retPanel.add(new JPanel(), BorderLayout.EAST);
    retPanel.add(new JPanel(), BorderLayout.SOUTH);
    retPanel.add(component, BorderLayout.CENTER);
    return retPanel;
  }

  /**
   * Gets the experiment.
   * 
   * @return the experiment
   */
  public BaseExperiment getExperiment() {
    return experiment;
  }

  /**
   * Shows dialog.
   * 
   * @return a new instance of a benchmark model
   */
  public BaseExperiment showDialog() {
    this.setVisible(true);
    return experiment;
  }

}
