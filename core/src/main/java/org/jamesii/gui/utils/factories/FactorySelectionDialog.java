/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.factories;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.gui.application.ApplicationDialog;
import org.jamesii.gui.utils.parameters.factories.FactoryParameterPanel;

/**
 * This dialog displays all available factories for a type, to be chosen by the
 * user.
 * 
 * @param <F>
 *          the type of the factory
 * 
 * @author Roland Ewald
 */
public class FactorySelectionDialog<F extends Factory<?>> extends
    ApplicationDialog implements ActionListener {

  /** Serialisation ID. */
  private static final long serialVersionUID = -8671542378093619857L;

  /** Holds additional content from the API user. */
  private final JPanel addContentPanel = new JPanel();

  /**
   * Configuration panel (holds the OK button and additional components from the
   * API user).
   */
  private JPanel configPanel = new JPanel(new BorderLayout(10, 10));

  /** Will be set true if dialog should proceed. */
  private boolean okButtonPressed = false;

  /** List of selection elements (one for each factory). */
  private List<IFactorySelector<F>> selectionElements = new ArrayList<>();

  private List<F> factories;

  private boolean singleSelection;

  private ConfigureFactoryPanel<F> confPanel;

  private IFactoryDescriptionRenderer<F> descRenderer;

  /**
   * Default constructor.
   * 
   * @param owner
   *          owner component of this dialog
   * @param factories
   *          list of factories from which sth has to be selected
   * @param info
   *          component containing additional information
   * @param title
   *          title of the dialog
   * @param singleSelection
   *          decides whether multiple selection is allowed or not
   */
  public FactorySelectionDialog(Window owner, Collection<F> factories,
      JComponent info, String title, boolean singleSelection) {
    this(owner, factories, info, title, singleSelection,
        new IFactoryDescriptionRenderer<F>() {
          @Override
          public String getDescription(F factory) {
            return factory.getReadableName();
          }
        });
  }

  /**
   * Constructor to define an additional factory description renderer.
   * 
   * @param owner
   *          owner of the dialog
   * @param factories
   *          factories from which to select
   * @param info
   *          information component to facilitate selection
   * @param title
   *          title of the dialog
   * @param singleSelection
   *          decides whether multiple selection is allowed or not
   * @param descriptionRenderer
   *          renders the description of a factory (in case toString() does not
   *          suffice)
   */
  public FactorySelectionDialog(Window owner, Collection<F> factories,
      JComponent info, String title, boolean singleSelection,
      IFactoryDescriptionRenderer<F> descriptionRenderer) {
    super(owner);
    setModal(true);
    setTitle(title);

    this.descRenderer = descriptionRenderer;
    this.factories = new ArrayList<>(factories);
    this.singleSelection = singleSelection;

    for (F f : factories) {
      selectionElements
          .add(new CheckListItem<>(f, new ParameterBlock(), false));
    }

    if (singleSelection) {
      initUISingle(info);
    } else {
      initUIMultiple(info);
    }

    initRest(info);
  }

  private void initUIMultiple(JComponent info) {
    final JPanel multiSelection = new JPanel(new BorderLayout());
    final JLabel ns = new JLabel("Nothing selected");
    ns.setPreferredSize(new Dimension(250, 15));

    final JList list = new JList(selectionElements.toArray());
    list.addListSelectionListener(new ListSelectionListener() {
      private JComponent fpane = ns;

      private JLabel ninfo = new JLabel("No Parameter Information found!");

      @Override
      public void valueChanged(ListSelectionEvent e) {
        @SuppressWarnings("unchecked")
        IFactorySelector<F> v = (IFactorySelector<F>) list.getSelectedValue();

        if (fpane != null) {
          multiSelection.remove(fpane);
        }

        if (v != null) {
          IFactoryInfo info =
              SimSystem.getRegistry().getFactoryInfo(
                  v.getFactory().getClass().getName());
          if (info != null) {
            FactoryParameterPanel p =
                new FactoryParameterPanel(info, v.getParameters());
            v.setParameters(p.getParameterBlock());

            multiSelection.add(fpane = p, BorderLayout.CENTER);
          } else {
            multiSelection.add(fpane = ninfo, BorderLayout.CENTER);
          }
        } else {
          multiSelection.add(fpane = ns, BorderLayout.CENTER);
        }

        multiSelection.revalidate();
        multiSelection.repaint();
      }
    });

    list.setCellRenderer(new ListCellRenderer() {
      private final JCheckBox cb = new JCheckBox();

      private final JLabel label = new JLabel();

      @Override
      public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
        if (value instanceof IFactorySelector<?>) {
          IFactorySelector<F> v = (IFactorySelector<F>) value;
          cb.setText(descRenderer.getDescription(v.getFactory()));
          cb.setSelected(v.isSelected());
          cb.setBackground(isSelected ? list.getSelectionBackground() : list
              .getBackground());
          cb.setForeground(isSelected ? list.getSelectionForeground() : list
              .getForeground());
          return cb;
        }

        label.setText(value == null ? "null" : value.toString());
        return label;
      }
    });

    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int index = list.locationToIndex(e.getPoint());

        if (index < 0 || index >= list.getModel().getSize()) {
          return;
        }

        // TODO sr137: check whether the click is actually on top of the check
        // box icon (remember a check for e.getPoint()<32 would only work for
        // standard checkboxes but not e.g. for arabic oritented ones etc.)
        CheckListItem<F> item =
            (CheckListItem<F>) list.getModel().getElementAt(index);
        item.setSelected(!item.isSelected());

        list.repaint();
      }
    });

    JScrollPane scroll;
    multiSelection.add(scroll = new JScrollPane(list), BorderLayout.LINE_START);
    // scroll.setPreferredSize(new Dimension(150, 100));
    multiSelection.add(ns, BorderLayout.CENTER);

    add(multiSelection, BorderLayout.CENTER);
  }

  /**
   * Gets the adds the content panel.
   * 
   * @return the adds the content panel
   */
  public JPanel getAddContentPanel() {
    return addContentPanel;
  }

  /**
   * Get list of selected factories.
   * 
   * @return list of selected factories
   * @deprecated use {@link #getSelectedFactoriesAndParameters()} instead
   */
  @Deprecated
  public List<F> getSelectedFactories() {
    List<F> selectedFactories = new ArrayList<>();
    for (IFactorySelector<F> checkBox : selectionElements) {
      if (checkBox.isSelected()) {
        selectedFactories.add(checkBox.getFactory());
      }
    }
    return selectedFactories;
  }

  /**
   * Get list of selected factories including parameters.
   * 
   * @return list of selected factories and their parameters
   */
  public List<ParameterizedFactory<F>> getSelectedFactoriesAndParameters() {
    List<ParameterizedFactory<F>> selectedFactories = new ArrayList<>();
    for (IFactorySelector<F> check : selectionElements) {
      if (check.isSelected()) {
        selectedFactories.add(new ParameterizedFactory<>(check.getFactory(),
            check.getParameters()));
      }
    }

    return selectedFactories;
  }

  /**
   * Initialise user interface for single selection.
   * 
   * @param info
   *          the component containing additional information to facilitate the
   *          selection for the user
   */
  protected final void initUISingle(JComponent info) {
    confPanel =
        new ConfigureFactoryPanel<>(factories, getTitle(), null, null, false);
    add(confPanel, BorderLayout.CENTER);
  }

  private void initRest(JComponent info) {
    setSize(400, 400);
    JPanel buttonPanel = new JPanel(new BorderLayout());

    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);

    buttonPanel.add(okButton, BorderLayout.LINE_END);

    configPanel.add(addContentPanel, BorderLayout.CENTER);
    configPanel.add(buttonPanel, BorderLayout.PAGE_END);
    getContentPane().add(configPanel, BorderLayout.PAGE_END);

    if (info != null) {
      getContentPane().add(info, BorderLayout.PAGE_START);
    }
  }

  /**
   * Checks if is ok button pressed.
   * 
   * @return true, if is ok button pressed
   */
  public boolean isOkButtonPressed() {
    return okButtonPressed;
  }

  /**
   * Sets the ok button pressed.
   * 
   * @param pressed
   *          the new ok button pressed
   */
  public void setOkButtonPressed(boolean pressed) {
    okButtonPressed = pressed;
  }

  @Override
  public void setVisible(boolean b) {
    if (b) {
      pack();
      setLocationRelativeTo(getOwner());
    }
    super.setVisible(b);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    okButtonPressed = true;
    if (singleSelection) {
      finishSelectionSingle();
    } else {
      finishSelectionMulti();
    }
    setVisible(false);
  }

  private void finishSelectionMulti() {
  }

  private void finishSelectionSingle() {
    ParameterizedFactory<F> f = confPanel.getSelectedFactoryAndParameter();
    selectionElements.clear();
    selectionElements.add(new CheckListItem<>(f.getFactory(),
        f.getParameters(), true));
  }

  public void setSelectedFactory(String factoryName, ParameterBlock parameters) {
    confPanel.setSelectedFactory(factoryName, parameters);
  }

}

/**
 * The Interface IFactorySelector.
 * 
 * @param <F>
 *          the type of the factory
 */
interface IFactorySelector<F extends Factory<?>> {

  /**
   * Gets the factory.
   * 
   * @return the factory
   */
  F getFactory();

  /**
   * Sets the parameters.
   * 
   * @param parameterBlock
   *          the new parameters
   */
  void setParameters(ParameterBlock parameterBlock);

  /**
   * Gets the parameters.
   * 
   * @return the parameters
   */
  ParameterBlock getParameters();

  /**
   * Checks if is selected.
   * 
   * @return true, if is selected
   */
  boolean isSelected();
}