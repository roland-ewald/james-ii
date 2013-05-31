package org.jamesii.gui.server;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jamesii.core.distributed.masterserver.MasterServer;
import org.jamesii.gui.application.ApplicationDialog;
import org.jamesii.gui.application.WindowManagerManager;

/**
 * The Class SimulationServerCreationDialog.
 * 
 * @author Jan Himmelspach
 */
public class SimulationServerCreationDialog extends ApplicationDialog implements
    ActionListener {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2519295164334541128L;

  /** The cancel button. */
  private JButton cancelButton;

  /** The ok button. */
  private JButton okButton;

  /** The base name edit. */
  private JTextField baseNameEdit;

  /** Text field containing location of master server. */
  private JTextField masterServerEdit;

  /** The number of servers spinner. */
  private JSpinner numberOfServersSpinner;

  /** The use extra vm check box. */
  private JCheckBox useExtraVMCheckBox;

  /** The Constant OK. */
  private static final String OK = "OK";

  /** The Constant CANCEL. */
  private static final String CANCEL = "CANCEL";

  /** The base name. */
  private String baseName;

  /** The master server adress. */
  private String serverAdress;

  /** The use extra vm. */
  private Boolean useExtraVM;

  /** The number of server. */
  private Integer numberOfServer;

  /** The ok. */
  private boolean ok;

  /**
   * Instantiates a new simulation server creation dialog.
   * 
   * @param title
   *          the title
   */
  public SimulationServerCreationDialog(String title) {
    super(WindowManagerManager.getWindowManager().getMainWindow());

    setModal(true);
    setResizable(false);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setSize(410, 200);
    setLocationRelativeTo(null);
    setTitle(title);

    getContentPane().setLayout(new BorderLayout());

    GridBagConstraints c;
    c = new GridBagConstraints();
    c.gridy = 0;
    c.anchor = GridBagConstraints.WEST;

    JPanel contentPanel = new JPanel(new GridBagLayout());

    JLabel numberLabel =
        new JLabel("Number of simulation servers to be created");
    numberLabel.setBorder(new EmptyBorder(2, 5, 2, 5));

    contentPanel.add(numberLabel, c);

    numberOfServersSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1));

    c = new GridBagConstraints();
    c.gridy = 1;
    c.anchor = GridBagConstraints.WEST;

    contentPanel.add(numberOfServersSpinner, c);

    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.gridy = 2;

    JLabel baseNameLabel =
        new JLabel("Base name to be used for naming the simulation servers");
    numberLabel.setBorder(new EmptyBorder(2, 5, 2, 5));

    baseNameEdit = new JTextField("SimServer");

    contentPanel.add(baseNameLabel, c);

    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridy = 3;

    contentPanel.add(baseNameEdit, c);

    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.gridy = 4;

    JLabel masterServerLabel = new JLabel("Adress of the Master Server");
    numberLabel.setBorder(new EmptyBorder(2, 5, 2, 5));

    masterServerEdit =
        new JTextField("rmi://localhost:" + MasterServer.DEFAULT_PORT + "/"
            + MasterServer.DEFAULT_BINDING_NAME);

    contentPanel.add(masterServerLabel, c);

    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridy = 5;

    contentPanel.add(masterServerEdit, c);

    useExtraVMCheckBox =
        new JCheckBox("Create servers in extra virtual machine instances");

    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.gridy = 6;

    contentPanel.add(useExtraVMCheckBox, c);

    okButton = new JButton("Ok");
    okButton.setActionCommand(OK);
    okButton.addActionListener(this);
    cancelButton = new JButton("Cancel");
    cancelButton.setActionCommand(CANCEL);
    cancelButton.addActionListener(this);

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);

    c = new GridBagConstraints();
    c.anchor = GridBagConstraints.WEST;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridy = 7;

    contentPanel.add(buttonPanel, c);

    getContentPane().add(contentPanel);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (OK.equals(e.getActionCommand())) {
      baseName = baseNameEdit.getText();
      serverAdress = masterServerEdit.getText();
      numberOfServer = (Integer) numberOfServersSpinner.getValue();
      useExtraVM = useExtraVMCheckBox.getModel().isSelected();
      ok = true;
      setVisible(false);
      dispose();
    }

    if (CANCEL.equals(e.getActionCommand())) {
      setVisible(false);
      ok = false;
      dispose();
    }

  }

  /**
   * Gets the base name.
   * 
   * @return the base name
   */
  public String getBaseName() {
    return baseName;
  }

  /**
   * Gets the use extra vm.
   * 
   * @return the use extra vm
   */
  public Boolean getUseExtraVM() {
    return useExtraVM;
  }

  /**
   * Gets the number of server.
   * 
   * @return the number of server
   */
  public Integer getNumberOfServer() {
    return numberOfServer;
  }

  /**
   * Checks if is cancelled.
   * 
   * @return true, if is cancelled
   */
  public boolean isCancelled() {
    return !ok;
  }

  /**
   * Get the address of the master server.
   * 
   * @return the master server address
   */
  public String getMasterServerAdress() {
    return serverAdress;
  }

}
