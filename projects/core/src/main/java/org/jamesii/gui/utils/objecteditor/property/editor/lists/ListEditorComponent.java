/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor.lists;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.gui.utils.factories.FactorySelectionDialog;
import org.jamesii.gui.utils.parameters.list.ConfigurationListModel;
import org.jamesii.gui.utils.parameters.list.Entry;
import org.jamesii.gui.utils.parameters.list.ParametrizedList;

/**
 * The list editor component can be used from within an object editor component
 * to modify lists. It supports adding and removing items and it allows to
 * change the order of the items in the list. <br/>
 * It allows to modify a list of (factories, factory parameters) tuples.
 * 
 * 
 * @author Jan Himmelspach
 * 
 */
public class ListEditorComponent extends JComponent {

  /**
   * The model to be used in the editing list control ({@link #list()}).
   */
  private ConfigurationListModel listModel;

  private int current = -1;

  /**
   * The base factory which can instantiate objects of the type stored in the
   * list.
   */
  private String baseFactory;

  /**
   * Create a new instance of the list editor component. If the entries list is
   * empty the editor will start with an empty list, otherwise the entries
   * passed will be added to the list before displaying the component. The list
   * of entries can be extended by adding entries representing factories of the
   * passed baseFactory type.
   * 
   * @param entries
   *          list of entries to start the editor with
   * @param baseFactory
   *          type of factories to be added to the list of entries
   */
  public ListEditorComponent(List<Entry> entries, String baseFactory) {

    this.baseFactory = baseFactory;

    ArrayList<Entry> newEntries = new ArrayList<>(entries);

    listModel = new ConfigurationListModel(newEntries);

    list =
        new org.jamesii.gui.utils.parameters.list.ParametrizedList(listModel);
    list.setMinimumSize(new Dimension(200, 200));

    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
          int index = list.locationToIndex(e.getPoint());
          Rectangle cellBounds = list.getCellBounds(index, index);
          if (cellBounds != null && cellBounds.contains(e.getPoint())) {
            editItem();
          }
        }
      }
    });

    JScrollPane listPanel = new JScrollPane(list);
    setLayout(new BorderLayout(0, 0));
    add(listPanel);

    JPanel controlPanel = new JPanel();
    add(controlPanel, BorderLayout.EAST);
    controlPanel.setLayout(new GridLayout(0, 1, 0, 0));

    JPanel buttonPanel = new JPanel();
    controlPanel.add(buttonPanel);
    buttonPanel.setLayout(new GridLayout(0, 1, 0, 0));

    JButton addButton = new JButton("Add");
    addButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        addNewItem();

      }
    });
    addButton.setToolTipText("Add new item");
    buttonPanel.add(addButton);

    JButton deleteButton = new JButton("Delete");
    deleteButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        // delete selected items
        list.deleteSelectedEntries();

      }
    });

    deleteButton.setToolTipText("Delete selected item(s)");
    buttonPanel.add(deleteButton);

    JButton upButton = new JButton("Up");
    upButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        list.moveSelectedEntriesUp();

      }
    });
    upButton.setToolTipText("Move item up");
    buttonPanel.add(upButton);

    JButton downButton = new JButton("Down");
    downButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        list.moveSelectedEntriesDown();
      }
    });
    downButton.setToolTipText("Move item down");
    buttonPanel.add(downButton);

    panel = new JPanel();
    controlPanel.add(panel);
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

    this.setPreferredSize(new Dimension(600, 200));
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4833597508226293576L;

  private JPanel panel;

  /**
   * The list control for editing.
   */
  private ParametrizedList list;

  @SuppressWarnings("unchecked")
  protected void addNewItem() {

    // create a list of all of the right type (abstract factory)
    // show a popup for selection if size > 1
    // add new factory

    Class<AbstractFactory<Factory<?>>> af =
        (Class<AbstractFactory<Factory<?>>>) SimSystem.getRegistry()
            .getAbstractFactoryForBaseFactory(baseFactory);

    ParameterizedFactory<Factory<?>> selection = null;

    FactorySelectionDialog<Factory<?>> dialog =
        new FactorySelectionDialog<>(null, SimSystem.getRegistry()
            .getFactories(af, null), null, "", true);
    dialog.setVisible(true);
    if (dialog.isOkButtonPressed()) {
      List<ParameterizedFactory<Factory<?>>> selected =
          dialog.getSelectedFactoriesAndParameters();
      if (selected.size() > 0) {
        selection = selected.get(0);
      }
      if (selection == null) {
        return;
      }

      listModel.addElement(selection.getFactory().getClass().getName(),
          selection.getParameters());
    }
  }

  protected void editItem() {

    // create a list of all of the right type (abstract factory)
    // show a popup for selection if size > 1
    // add new factory

    @SuppressWarnings("unchecked")
    Class<AbstractFactory<Factory<?>>> af =
        (Class<AbstractFactory<Factory<?>>>) SimSystem.getRegistry()
            .getAbstractFactoryForBaseFactory(baseFactory);

    if (list.getSelectedIndex() == -1) {
      return;
    }

    ParameterizedFactory<Factory<?>> selection = null;

    FactorySelectionDialog<Factory<?>> dialog =
        new FactorySelectionDialog<>(null, SimSystem.getRegistry()
            .getFactories(af, null), null, "", true);
    dialog.setSelectedFactory(list.getSelectedElement().getFactoryName(), list
        .getSelectedElement().getParameters());
    dialog.setVisible(true);
    if (dialog.isOkButtonPressed()) {
      List<ParameterizedFactory<Factory<?>>> selected =
          dialog.getSelectedFactoriesAndParameters();
      if (selected.size() > 0) {
        selection = selected.get(0);
      }

      if (selection == null) {
        return;
      }

      list.editElement(list.getSelectedIndex(), selection.getFactory()
          .getClass().getName(), selection.getParameters());
    }
  }

  public List<Entry> getResult() {
    List<Entry> result = new ArrayList<>();
    for (int i = 0; i < listModel.getSize(); i++) {
      result.add((Entry) listModel.getElementAt(i));
    }
    return result;
  }

}
