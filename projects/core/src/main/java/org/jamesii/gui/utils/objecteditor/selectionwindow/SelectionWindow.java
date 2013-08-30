/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.selectionwindow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.utils.objecteditor.IPropertyChangedListener;
import org.jamesii.gui.utils.parameters.factories.FactoryParameterPanel;

/**
 * Helper window to select a factory and its parameters. It is used exclusively
 * through a set of static methods:
 * <dl>
 * <dt>{@link #getSelection(List, Window)}</dt>
 * <dd>Retrieves a new selection from a list of factories</dd>
 * <dt>{@link #editSelection(List, String, ParameterBlock, Window)}</dt>
 * <dd>Retrieves a selection with a certain factory and parameters pre-selected.
 * Usually for editing a previous choice.
 * </dl>
 * 
 * @author Johannes Rössel
 * 
 * @param <T>
 *          The type of factory that is used in this class.
 */
public final class SelectionWindow<T extends Factory> extends JDialog {
  /** Serialisation ID. */
  private static final long serialVersionUID = 4097586391034505718L;

  /** The OK button for closing the dialog and applying the changes. */
  private JButton okButton;

  /** The Cancel button for closing the dialog and reverting the changes. */
  private JButton cancelButton;

  /** The {@link FactoryComboBox} for selecting a factory. */
  private FactoryComboBox<T> factoryComboBox;

  /** The Parameter panel for editing the factory's parameters. */
  private FactoryParameterPanel parameterPanel;

  /**
   * The {@link JScrollPane} surrounding the {@link FactoryParameterPanel}.
   * Since said Panel has to be reconstructed every time the selection changes
   * we need a wrapper and since we also need a {@link JScrollPane} this comes
   * in handy.
   */
  private JPanel parameterPanelWrapper;

  /**
   * The dialog result, indicating whether the dialog was closed with the OK
   * button or not. Essentially this tells whether the new factory name and
   * parameters can be used or whether they were intended to be thrown away.
   */
  private boolean dialogResult;

  /**
   * The {@link IPropertyChangedListener} that will be attached to the
   * {@link FactoryParameterPanel}each time it is reconstructed. This causes the
   * parameter block in the {@link FactoryComboBox}'s model to be updated each
   * time a parameter value changes.
   */
  private transient IPropertyChangedListener parameterPanelPropertyChangedListener =
      new IPropertyChangedListener() {
        @Override
        public void propertyChanged(Object propertyParent, String propertyName,
            Object value) {
          if (factoryComboBox.getSelectedItem() != null) {
            factoryComboBox.setParameters(factoryComboBox.getSelectedItem(),
                parameterPanel.getParameterBlock());
          }
        }
      };

  /**
   * Private constructor to prevent instantiation.
   * 
   * @param factories
   *          A list of factories that can be selected.
   * @param parent
   *          The parent window.
   */
  private SelectionWindow(List<T> factories, Window parent,
      Component parentComponent) {
    super(parent, "Select factory", ModalityType.APPLICATION_MODAL);

    add(this.createContent(factories));
    this.setMinimumSize(new Dimension(350, 200));
    this.pack();
    this.setLocationRelativeTo(parentComponent);
  }

  /**
   * Creates and displays a {@link SelectionWindow} allowing for selecting a
   * factory along with its parameters.
   * 
   * @param <T>
   *          The factory type.
   * @param factories
   *          A list of factories.
   * @param parent
   *          The parent window.
   * @return Either {@code null} in case the dialog was cancelled or a pair of
   *         factory name and its parameters.
   */
  public static <T extends Factory> Pair<String, ParameterBlock> getSelection(
      List<T> factories, Window parent, Component parentComponent) {
    SelectionWindow<T> window =
        new SelectionWindow<>(factories, parent, parentComponent);
    window.setVisible(true);

    if (!window.getDialogResult()) {
      return null;
    }

    return new Pair<>(window.getSelectedFactory(), window.getParameters());
  }

  /**
   * Creates and displays a {@link SelectionWindow} allowing for selecting a
   * factory along with its parameters.
   * 
   * @param <T>
   *          The factory type.
   * @param factories
   *          A list of factories.
   * @param factoryName
   *          The currently selected factory.
   * @param parameters
   *          The currently set parameters for the factory.
   * @param parent
   *          The parent window.
   * @return Either a pair of the original factory name and its parameters in
   *         case the dialog was cancelled or a pair of a new factory name and
   *         its parameters.
   */
  public static <T extends Factory> Pair<String, ParameterBlock> editSelection(
      List<T> factories, String factoryName, ParameterBlock parameters,
      Window parent, Component parentComponent) {
    SelectionWindow<T> window =
        new SelectionWindow<>(factories, parent, parentComponent);
    window.setParameters(factoryName, parameters);
    window.setSelectedFactory(factoryName);
    window.setVisible(true);

    if (!window.getDialogResult()) {
      return new Pair<>(factoryName, parameters);
    }

    return new Pair<>(window.getSelectedFactory(), window.getParameters());
  }

  /**
   * Creates the main content panel and all necessary controls.
   * 
   * @param factories
   *          The list of factories to initialise {@link #factoryComboBox}.
   * @return The laid-out panel.
   */
  private JPanel createContent(List<T> factories) {
    JPanel panel = new JPanel(new BorderLayout());

    factoryComboBox = new FactoryComboBox<>(factories);
    panel.add(factoryComboBox, BorderLayout.PAGE_START);

    // Set up the buttons at the bottom of the dialog
    {
      JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

      okButton = new JButton("OK");
      okButton.setEnabled(false);
      cancelButton = new JButton("Cancel");

      buttonPanel.add(okButton);
      buttonPanel.add(cancelButton);

      this.getRootPane().setDefaultButton(okButton);

      panel.add(buttonPanel, BorderLayout.PAGE_END);
    }

    parameterPanelWrapper = new JPanel(new BorderLayout());
    parameterPanelWrapper.setPreferredSize(new Dimension(150, 100));

    panel.add(parameterPanelWrapper, BorderLayout.CENTER);

    updateParameterPanel();

    // === Listeners ===

    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // dialog result is true when clicking the OK button, so the selected
        // factory and parameters are fine to use
        SelectionWindow.this.dialogResult = true;
        SelectionWindow.this.setVisible(false);
      }
    });

    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // dialog result is false, since the dialog was cancelled
        SelectionWindow.this.dialogResult = false;
        SelectionWindow.this.setVisible(false);
      }
    });

    factoryComboBox.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        // update the parameters for the item just de-selected
        if (e.getStateChange() == ItemEvent.DESELECTED) {
          factoryComboBox.setParameters(e.getItem(),
              parameterPanel.getParameterBlock());
        }

        // update the parameter panel in case the selected item in the combo box
        // changes
        updateParameterPanel();

        // enable the OK button if an element was selected
        okButton.setEnabled(factoryComboBox.getSelectedIndex() != -1);
      }
    });

    return panel;
  }

  /** Updates the parameter panel if another factory was selected. */
  private void updateParameterPanel() {
    String factory = getSelectedFactory();
    ParameterBlock parameters = getParameters();

    if (factory == null || parameters == null) {
      parameterPanelWrapper.removeAll();
      parameterPanelWrapper.add(new JLabel("No factory selected.",
          SwingConstants.CENTER));
      parameterPanelWrapper.revalidate();
      return;
    }

    IFactoryInfo factoryInfo = SimSystem.getRegistry().getFactoryInfo(factory);

    parameterPanel = new FactoryParameterPanel(factoryInfo, parameters);

    parameterPanel
        .addPropertyChangedListener(parameterPanelPropertyChangedListener);

    if (factoryInfo.getParameters().size() == 0) {
      parameterPanelWrapper.removeAll();
      parameterPanelWrapper.add(new JLabel("No configurable parameters.",
          SwingConstants.CENTER));
      parameterPanelWrapper.revalidate();
      return;
    }

    setParameters(factory, parameterPanel.getParameterBlock());

    parameterPanelWrapper.removeAll();
    parameterPanelWrapper.add(parameterPanel);
    parameterPanelWrapper.revalidate();
  }

  /**
   * Retrieves the selected factory from the dialog.
   * 
   * @return The factory that was selected or {@code null} if there was no
   *         selection.
   */
  public String getSelectedFactory() {
    return factoryComboBox.getSelectedFactory();
  }

  /**
   * Sets the selected factory for the dialog.
   * 
   * @param factoryName
   *          The class name of the factory to select. If it can't be found then
   *          the selection doesn't change.
   */
  public void setSelectedFactory(String factoryName) {
    factoryComboBox.setSelectedFactory(factoryName);
  }

  /**
   * Sets the {@link ParameterBlock} for the specified factory.
   * 
   * @param factoryName
   *          The class name of the factory.
   * @param parameters
   *          The {@link ParameterBlock} to set.
   */
  public void setParameters(String factoryName, ParameterBlock parameters) {
    factoryComboBox.setParameters(factoryName, parameters);
  }

  /**
   * Retrieves the {@link ParameterBlock} for the currently selected factory in
   * the dialog.
   * 
   * @return The {@link ParameterBlock} associated with the selected factory.
   */
  public ParameterBlock getParameters() {
    return factoryComboBox.getSelectedParameters();
  }

  /**
   * Retrieves the dialog result, which indicates how the dialog was closed.
   * 
   * @return A value indicating whether the dialog was closed with the "OK"
   *         button.
   */
  public boolean getDialogResult() {
    return dialogResult;
  }
}
