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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.FactoryListCellRenderer;
import org.jamesii.gui.utils.parameters.factories.FactoryParameterPanel;

/**
 * Panel that allows to configure a factory of an arbitrary type.
 * 
 * @param <F>
 *          the type of the base factory
 * @author Roland Ewald
 */
public class ConfigureFactoryPanel<F extends Factory<?>> extends JPanel {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7856502303107826383L;

  // /** Default size to start with */
  // private static final int MIN_SIZE = 100;

  /** The combo box for selecting a factory. */
  private JComboBox<?> factoryComboBox = new JComboBox<>();

  /** The panel on which the parameters of the factory are shown. */
  private JPanel parameterPanel = new JPanel(new BorderLayout());

  /** List of factories from which shall be selected. */
  private List<F> factories;

  /** Map to store the parameters for each factory. */
  private Map<String, ParameterBlock> factoryParameters = new HashMap<>();

  /** Factory parameter panel. */
  private FactoryParameterPanel fpPanel = null;

  /** Name of currently selected data storage factory. */
  private String currentFactory = null;

  /** The message to be displayed to the user. */
  private String userMessage;

  /**
   * Determines whether the option '--' should be displayed as well, or if the
   * user should be *forced* to select a factory.
   */
  private final boolean nullAllowed;

  /**
   * Instantiates a new configure factory panel.
   * 
   * @param choosableFactories
   *          the factories that can be chosen
   * @param userMsg
   *          the user msg
   * @param oldFactory
   *          the old factory
   * @param oldParameters
   *          the old parameters
   */
  public ConfigureFactoryPanel(List<F> choosableFactories, String userMsg,
      F oldFactory, ParameterBlock oldParameters) {
    this(choosableFactories, userMsg, oldFactory, oldParameters, true);
  }

  /**
   * Default constructor.
   * 
   * @param choosableFactories
   *          list of choosable factories
   * @param userMsg
   *          the message to be displayed to the user
   * @param oldFactory
   *          the old factory that was selected
   * @param oldParameters
   *          the parameters of the old factory that was chosen
   * @param allowNull
   *          if true, user is not obliged to select a factory an null might be
   *          returned.
   */
  public ConfigureFactoryPanel(List<F> choosableFactories, String userMsg,
      F oldFactory, ParameterBlock oldParameters, boolean allowNull) {
    factories = choosableFactories;
    userMessage = userMsg;
    nullAllowed = allowNull;
    init(oldFactory, oldParameters);
  }

  /**
   * Default constructor.
   * 
   * @param choosableFactories
   *          list of choosable factories
   * @param userMsg
   *          the message to be displayed to the user
   * @param oldFactory
   *          the old parameterized factory that was selected / edited / set
   *          before
   * @param allowNull
   *          if true, user is not obliged to select a factory an null might be
   *          returned.
   */
  public ConfigureFactoryPanel(List<F> choosableFactories, String userMsg,
      ParameterizedFactory<F> oldFactory, boolean allowNull) {
    factories = choosableFactories;
    userMessage = userMsg;
    nullAllowed = allowNull;
    if (oldFactory != null) {
      init(oldFactory.getFactoryInstance(), oldFactory.getParameter());
    } else {
      init(null, null);
    }
  }

  /**
   * Create the elements of the user interface.
   * 
   * @param oldFactory
   *          the old factory that was selected
   * @param oldParameters
   *          the parameters of the old factory that was chosen
   */
  private void init(F oldFactory, ParameterBlock oldParameters) {
    setLayout(new BorderLayout());
    JPanel factorySelectionPanel = new JPanel(new BorderLayout());

    // Fill combo box
    JLabel typeLabel = new JLabel(userMessage);
    if (factories != null) {
      ArrayList<Object> factoryList = new ArrayList<>();
      if (nullAllowed) {
        factoryList.add("---");
      }
      for (F f : factories) {
        factoryList.add(f);
      }
      factoryComboBox = new JComboBox<>(factoryList.toArray());
      factoryComboBox.setRenderer(new FactoryListCellRenderer());
    }

    // Select old factory, if there was any:
    if (oldFactory != null) {
      String dsName = oldFactory.getClass().getName();
      F selected = oldFactory;

      for (F f : factories) {
        if (f.getClass().equals(oldFactory.getClass())) {
          selected = f;
        }
      }
      factoryComboBox.setSelectedItem(selected);
      factoryParameters.put(dsName, oldParameters);
    }

    factorySelectionPanel.add(typeLabel, BorderLayout.PAGE_START);
    factorySelectionPanel.add(factoryComboBox, BorderLayout.PAGE_END);
    // factorySelectionPanel.setMinimumSize(new Dimension(MIN_SIZE, MIN_SIZE));

    add(factorySelectionPanel, BorderLayout.PAGE_START);

    factoryComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        refreshFactoryPanel(getSelectedIndex());
      }
    });

    add(parameterPanel, BorderLayout.CENTER);
    refreshFactoryPanel(getSelectedIndex());
  }

  /**
   * Select a factory (has to be in the list of available factories)
   * 
   * @param factoryClassName
   */
  public void setSelectedFactory(String factoryClassName, ParameterBlock block) {
    int index = -1;
    for (int i = 0; i < factoryComboBox.getItemCount(); i++) {
      if (factoryComboBox.getItemAt(i).getClass().getName()
          .compareTo(factoryClassName) == 0) {
        index = i;
      }
    }
    factoryComboBox.setSelectedIndex(-1);
    factoryParameters.put(factoryClassName, block.getCopy());
    factoryComboBox.setSelectedIndex(index);
  }

  /**
   * Refreshes parameter panel.
   * 
   * @param selectedIndex
   *          the index of the selected element
   */
  protected final void refreshFactoryPanel(int selectedIndex) {
    parameterPanel.removeAll();

    if (currentFactory != null) {
      factoryParameters.put(currentFactory, getParameterBlock());
    }

    if ((selectedIndex < 0)) {
      currentFactory = null;
      BasicUtilities.redrawComp(this);
      return;
    }

    currentFactory = factories.get(selectedIndex).getName();

    IFactoryInfo factoryInfo =
        SimSystem.getRegistry().getFactoryInfo(currentFactory);
    if (factoryInfo != null) {
      ParameterBlock fParameters = factoryParameters.get(currentFactory);
      if (fParameters == null) {
        fParameters = new ParameterBlock();
        factoryParameters.put(currentFactory, fParameters);
      }
      fpPanel = new FactoryParameterPanel(factoryInfo, fParameters);
      parameterPanel.add(fpPanel, BorderLayout.CENTER);
    }
    BasicUtilities.redrawComp(this);
  }

  /**
   * Gets the parameter block.
   * 
   * @return the parameter block
   */
  protected ParameterBlock getParameterBlock() {
    return fpPanel == null ? new ParameterBlock() : fpPanel.getParameterBlock();
  }

  /**
   * Gets the selected index.
   * 
   * @return the selected index
   */
  protected int getSelectedIndex() {
    return nullAllowed ? factoryComboBox.getSelectedIndex() - 1
        : factoryComboBox.getSelectedIndex();
  }

  /**
   * Get pair of selected factory and the parameters to be used.
   * 
   * @return pair of selected factory and the parameters to be used
   */
  public ParameterizedFactory<F> getSelectedFactoryAndParameter() {
    int index = getSelectedIndex();
    if (index < 0) {
      return new ParameterizedFactory<>(null, null);
    }
    return new ParameterizedFactory<>(factories.get(index), getParameterBlock());
  }

  /**
   * Checks if is null allowed.
   * 
   * @return true, if is null allowed
   */
  public boolean isNullAllowed() {
    return nullAllowed;
  }

}
