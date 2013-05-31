/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.factories;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;

/**
 * Dialog to select an appropriate dialog for parameter input via GUI.
 * 
 * @param <F>
 *          the type of the base factory
 * @author Roland Ewald
 */
public class SelectFactoryParamDialog<F extends Factory> extends JDialog {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6706647927351637289L;

  /** Map to store result look-up into. */
  private final Map<String, Pair<F, IFactoryParameterDialog<?>>> resultLookUp =
      new HashMap<>();

  /** Content panel. */
  private final JPanel content = new JPanel(new BorderLayout());

  /** Combobox containing eligible dialogs. */
  private JComboBox facComboBox;

  /**
   * Default constructor.
   * 
   * @param factoryClass
   *          the factory class of the desired kind
   * @param title
   *          the title
   */
  public SelectFactoryParamDialog(String title, Class<F> factoryClass) {
    setTitle(title);
    setModal(true);
    Map<F, IFactoryParameterDialog<?>> factories =
        BasicUtilities.getGUIFactories(factoryClass, null, null);
    for (Entry<F, IFactoryParameterDialog<?>> entry : factories.entrySet()) {
      resultLookUp.put(
          entry.getValue().getMenuDescription(),
          new Pair<F, IFactoryParameterDialog<?>>(entry.getKey(), entry
              .getValue()));
    }
    initUI();
    getContentPane().add(content);
    pack();
    setLocationRelativeTo(null);
  }

  /**
   * Initialises user interface.
   */
  private void initUI() {
    facComboBox = new JComboBox(resultLookUp.keySet().toArray());
    content.add(facComboBox, BorderLayout.CENTER);
    JButton okButton = new JButton("OK");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    content.add(okButton, BorderLayout.EAST);
  }

  /**
   * Gets the content.
   * 
   * @return the content
   */
  public JComponent getContent() {
    return content;
  }

  /**
   * Gets the selected factory.
   * 
   * @return the selected factory
   */
  public Pair<F, IFactoryParameterDialog<?>> getSelectedFactory() {
    return resultLookUp.get(facComboBox.getSelectedItem());
  }

}
