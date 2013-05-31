/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor.extensions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jamesii.SimSystem;
import org.jamesii.core.data.DBConnectionData;
import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IEditor;
import org.jamesii.gui.utils.parameters.editable.IPropertyEditor;

/**
 * An editor for {@link DBConnectionData}.
 * 
 * @author Gabriel Blum
 */
public class DBConnectionDataEditor extends JPanel implements
    IEditor<DBConnectionData> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -893664591774512300L;

  /** The connect. */
  private JButton connect = new JButton("try to connect to Database");
  {
    connect.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        tryConnection();
      }
    });
  }

  /** The login. */
  private JButton login = new JButton("Login to Database");
  {
    login.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        connectToDB();
      }
    });
  }

  /** The save. */
  private JButton save = new JButton("Save changes");
  {
    save.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        connectionUpdate();
      }
    });
  }

  /** The button panel. */
  private JPanel buttonPanel = new JPanel();

  /** The text panel. */
  private JPanel textPanel = new JPanel();

  /** The label panel. */
  private JPanel labelPanel = new JPanel();

  /** The connection. */
  private DBConnectionData connection = new DBConnectionData();

  /** The driver. */
  private JTextField driver = new JTextField(connection.getDriver());

  /** The url. */
  private JTextField url = new JTextField(connection.getURL());

  /** The user. */
  private JTextField user = new JTextField(connection.getUser());

  /** The pw. */
  private JPasswordField pw = new JPasswordField(connection.getPassword());

  /**
   * Instantiates a new dB connection data editor.
   */
  public DBConnectionDataEditor() {
    Dimension dim = new Dimension(550, 150);
    this.setPreferredSize(dim);
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
    textPanel.add(driver);
    textPanel.add(url);
    textPanel.add(user);
    textPanel.add(pw);
    buttonPanel.add(connect);
    buttonPanel.add(login);
    buttonPanel.add(save);
    labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
    labelPanel.add(new JLabel("SQL Driver"));
    labelPanel.add(Box.createRigidArea(new Dimension(0, 7)));
    labelPanel.add(new JLabel("DB URL"));
    labelPanel.add(Box.createRigidArea(new Dimension(0, 6)));
    labelPanel.add(new JLabel("User Name"));
    labelPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    labelPanel.add(new JLabel("Password"));
    this.setLayout(new BorderLayout());
    this.add(buttonPanel, BorderLayout.SOUTH);
    this.add(labelPanel, BorderLayout.WEST);
    this.add(textPanel, BorderLayout.CENTER);
    driver.setText("com.mysql.jdbc.Driver");
    this.setVisible(true);
  }

  /**
   * Try connection.
   */
  // TODO: This code appears seems redundant
  private void tryConnection() {
    connectionUpdate();
    try {
      connection.createNewConnection();
    } catch (SQLException sqle) {
      SimSystem.report(Level.SEVERE,
          "SQL Error: Something went wrong with the SQL connection.", sqle);
    } catch (ClassNotFoundException clnfe) {
      SimSystem.report(Level.SEVERE,
          "Class not found error: A class is missing. Check your classpath. ",
          clnfe);
    }
  }

  /**
   * Connect to db.
   */
  private void connectToDB() {
    connectionUpdate();
    try {
      connection.createNewConnection();
    } catch (SQLException sqle) {
      SimSystem.report(Level.SEVERE,
          "SQL Error: Something went wrong with the SQL connection: ", sqle);
    } catch (ClassNotFoundException clnfe) {
      SimSystem.report(Level.SEVERE,
          "Class not found error: A class is missing. Check your classpath. ",
          clnfe);
    }
  }

  /** The variable. */
  private IEditable<DBConnectionData> variable;

  @Override
  public void configureEditor(IEditable<DBConnectionData> var,
      IPropertyEditor peController) {
    variable = var;

  }

  @Override
  public IEditable<DBConnectionData> getEditable() {
    return variable;
  }

  @Override
  public JComponent getEmbeddedEditorComponent() {
    if (connection.getUser() == null || connection.getURL() == null) {
      return new JLabel("No connection specified");
    }
    return new JLabel(connection.getUser() + "@" + connection.getURL());
  }

  @Override
  public JComponent getSeparateEditorComponent() {
    return this;
  }

  @Override
  public DBConnectionData getValue() {
    // TODO Auto-generated method stub
    return connection;
  }

  @Override
  public void setEditing(boolean editing) {
    // TODO Auto-generated method stub

  }

  /**
   * Connection update.
   */
  private void connectionUpdate() {
    connection.setDriver(driver.getText());
    connection.setURL(url.getText());
    connection.setUser(user.getText());
    connection.setPassword(String.valueOf(pw.getPassword())); // very
                                                              // safe,
                                                              // this
  }

}
