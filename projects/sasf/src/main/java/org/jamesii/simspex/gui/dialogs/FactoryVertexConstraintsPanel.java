/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui.dialogs;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.recording.selectiontrees.FactoryVertex;
import org.jamesii.perfdb.recording.selectiontrees.FactoryVertexConstraints;

/**
 * Panel to edit {@link FactoryVertexConstraints}.
 * 
 * @author Roland Ewald
 * 
 */
public class FactoryVertexConstraintsPanel extends
    VertexConstraintsPanel<FactoryVertex<?>> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8375511071876504573L;

  /** Panel to edit constraints. */
  private final JPanel editConstraintsPanel = new JPanel(new BorderLayout());

  /** Mapping from factory names to their instances. */
  private final Map<String, Factory<?>> nameMap = new HashMap<>();

  /** List model for available factories. */
  private DefaultListModel<String> avFacListModel = new DefaultListModel<>();

  /** List model for ignored factories. */
  private DefaultListModel<String> igFacListModel = new DefaultListModel<>();

  /** List of available factories. */
  private final JList<String> availableFactories = new JList<>(avFacListModel);

  /** List of ignored factories. */
  private final JList<String> ignoredFactories = new JList<>(igFacListModel);

  /** Button to add a factory to the ignore list. */
  private final JButton ignoreButton = new JButton("->");
  {
    ignoreButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        ignoreFactory();
      }
    });
  }

  /**
   * Button to remove a factory from the ignore list.
   */
  private final JButton reconsiderButton = new JButton("<-");
  {
    reconsiderButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        reconsiderFactory();
      }
    });
  }

  /**
   * Default constructor.
   * 
   * @param vertex
   *          the vertex of which the constraints shall be edited
   */
  public FactoryVertexConstraintsPanel(FactoryVertex<?> vertex) {
    super(vertex);
    setLayout(new BorderLayout());

    Pair<Set<String>, Set<String>> listContent =
        getListContent(vertex.getFactories(), vertex.getConstraints()
            .getIgnoreList());

    for (Factory<?> factory : vertex.getFactories()) {
      nameMap.put(factory.getName(), factory);
    }

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    buttonPanel.add(ignoreButton);
    buttonPanel.add(reconsiderButton);

    for (String facName : listContent.getFirstValue()) {
      avFacListModel.addElement(facName);
    }
    for (String facName : listContent.getSecondValue()) {
      igFacListModel.addElement(facName);
    }

    editConstraintsPanel.add(new JScrollPane(availableFactories),
        BorderLayout.WEST);
    editConstraintsPanel.add(new JScrollPane(ignoredFactories),
        BorderLayout.EAST);
    editConstraintsPanel.add(buttonPanel, BorderLayout.CENTER);

    add(new JLabel("Edit factory constraints"), BorderLayout.NORTH);
    add(editConstraintsPanel, BorderLayout.CENTER);
  }

  /**
   * Ignore the currently selected factory.
   */
  protected void ignoreFactory() {
    moveFactory(availableFactories, avFacListModel, igFacListModel);
  }

  /**
   * Re-consider the currently selected factory.
   */
  protected void reconsiderFactory() {
    moveFactory(ignoredFactories, igFacListModel, avFacListModel);
  }

  /**
   * Move the selected factory around.
   * 
   * @param sourceFactoryList
   *          the list in which the factory is currently residing
   * @param sourceFacList
   *          the list model of the source factory list
   * @param targetListModel
   *          the model of the list to which the factory shall be moved
   */
  protected void moveFactory(JList<String> sourceFactoryList,
      DefaultListModel<String> sourceFacList, DefaultListModel<String> targetListModel) {
    int[] indices = sourceFactoryList.getSelectedIndices();
    if (indices.length == 0) {
      return;
    }

    for (int i = 0; i < indices.length; i++) {
      int selectedFactoryIndex = indices[i] - i;
      String selectedFactory =
              sourceFacList.getElementAt(selectedFactoryIndex);
      sourceFacList.removeElementAt(selectedFactoryIndex);
      targetListModel.addElement(selectedFactory);
    }
  }

  @Override
  public void save() {
    FactoryVertexConstraints constraints = getVertex().getConstraints();
    Set<Factory<?>> ignoreList = constraints.getIgnoreList();
    ignoreList.clear();
    for (int i = 0; i < igFacListModel.getSize(); i++) {
      String facName = igFacListModel.getElementAt(i);
      ignoreList.add(nameMap.get(facName));
    }
  }

  /**
   * Fill lists with the names (FQCNs) of the factories to considered/ignored.
   * 
   * @param factories
   *          all factories
   * @param ignFactories
   *          the factories to be ignored
   * @return pair (consider, ignore) of factory names
   */
  final Pair<Set<String>, Set<String>> getListContent(
      Set<? extends Factory<?>> factories, Set<? extends Factory<?>> ignFactories) {
    Set<String> ignore = new HashSet<>();
    for (Factory<?> factory : ignFactories) {
      ignore.add(factory.getName());
    }
    Set<String> consider = new HashSet<>();
    for (Factory<?> factory : factories) {
      if (!ignore.contains(factory.getName())) {
        consider.add(factory.getName());
      }
    }
    return new Pair<>(consider, ignore);
  }

}
