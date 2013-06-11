/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.modifier.IVariableModifier;
import org.jamesii.core.experiments.variables.modifier.IncrementModifierDouble;
import org.jamesii.core.experiments.variables.modifier.IncrementModifierInteger;
import org.jamesii.core.experiments.variables.modifier.MathModifierDouble;
import org.jamesii.core.experiments.variables.modifier.MathModifierInteger;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.objecteditor.IPropertyChangedListener;
import org.jamesii.gui.utils.objecteditor.ObjectEditorComponent;
import org.jamesii.gui.utils.objecteditor.implementationprovider.DefaultImplementationProvider;
import org.jamesii.gui.workflow.experiment.parameterexploration.propertyeditor.VariableModifierPropertyEditor;

/**
 * @author Stefan Rybacki
 * 
 */
public class ExperimentVariablesSetup extends JPanel {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -7640167545514428060L;

  private ObjectEditorComponent editor = null;

  private final List<ExperimentVariable<?>> variables = new ArrayList<>();

  // stubs
  private final Map<String, ExperimentVariable<?>> variableTypes =
      new HashMap<>();
  {
    variableTypes.put("Integer", new ExperimentVariable<>("STUB", 0));
    variableTypes.put("Double", new ExperimentVariable<>("STUB", 0d));
    variableTypes.put("String", new ExperimentVariable<>("STUB", ""));
  }

  private final Map<String, List<IVariableModifier<?>>> variableModifiers =
      new HashMap<>();

  {
    List<IVariableModifier<?>> list;
    list = new ArrayList<>();
    list.add(new IncrementModifierInteger(0, 1, 100));
    list.add(new SequenceModifier<>(new ArrayList<Integer>()));
    list.add(new MathModifierInteger(new ValueNode<>(0)));
    variableModifiers.put("Integer", list);

    list = new ArrayList<>();
    list.add(new IncrementModifierDouble(0d, 1d, 100d));
    list.add(new SequenceModifier<>(new ArrayList<Double>()));
    list.add(new MathModifierDouble(new ValueNode<>(0d)));
    variableModifiers.put("Double", list);

    list = new ArrayList<>();
    list.add(new SequenceModifier<>(new ArrayList<String>()));
    variableModifiers.put("String", list);
  }

  public ExperimentVariablesSetup() {
    setLayout(new BorderLayout());
    setOpaque(true);
    this.add(new JLabel("Parameter Exploration Setup"), BorderLayout.PAGE_START);

    final DefaultListModel listModel = new DefaultListModel();
    final JList list = new JList(listModel);
    list.setPreferredSize(new Dimension(200, 150));
    list.setCellRenderer(new DefaultListCellRenderer() {

      /**
       * Serialization ID
       */
      private static final long serialVersionUID = 2633776562358653131L;

      @Override
      public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
        // TODO Auto-generated method stub
        return super
            .getListCellRendererComponent(list,
                ((Entry<String, ExperimentVariable<?>>) value).getValue()
                    .getName(), index, isSelected, cellHasFocus);
      }
    });

    list.addListSelectionListener(new ListSelectionListener() {

      @Override
      public void valueChanged(ListSelectionEvent e) {
        BasicUtilities.invokeLaterOnEDT(new Runnable() {

          @SuppressWarnings("unchecked")
          @Override
          public void run() {
            Entry<String, ExperimentVariable<?>> entry =
                (Entry<String, ExperimentVariable<?>>) list.getSelectedValue();
            if (editor != null) {
              ExperimentVariablesSetup.this.remove(editor);
            }

            if (entry != null) {

              editor = new ObjectEditorComponent(entry.getValue());
              editor.addPropertyChangedListener(new IPropertyChangedListener() {

                @Override
                public void propertyChanged(Object propertyParent,
                    String propertyName, Object value) {
                  list.revalidate();
                  list.repaint();
                }
              });

              Map<String, IVariableModifier<?>> modifierMap = new HashMap<>();

              // copy by serialization available implementations
              for (IVariableModifier<?> m : variableModifiers.get(entry
                  .getKey())) {
                try {
                  byte[] s = SerialisationUtils.serialize(m);
                  m = (IVariableModifier<?>) SerialisationUtils.deserialize(s);
                  modifierMap.put(m.getClass().getSimpleName(), m);
                } catch (IOException | ClassNotFoundException e1) {
                  SimSystem.report(e1);
                }
              }

              editor.registerEditor(IVariableModifier.class,
                  new VariableModifierPropertyEditor());

              editor
                  .registerImplementationProvider(new DefaultImplementationProvider(
                      modifierMap, "modifier"));
              editor
                  .registerPropertyFilter(new ExperimentVariablePropertyFilter());
              ExperimentVariablesSetup.this.add(editor, BorderLayout.CENTER);
            }

            ExperimentVariablesSetup.this.validate();
            ExperimentVariablesSetup.this.revalidate();
            ExperimentVariablesSetup.this.repaint();
          }

        });
      }
    });

    JPanel controls = new JPanel(new FlowLayout());

    listModel.addListDataListener(new ListDataListener() {

      @Override
      public void intervalRemoved(ListDataEvent e) {
        contentsChanged(e);
      }

      @Override
      public void intervalAdded(ListDataEvent e) {
        contentsChanged(e);
      }

      @Override
      public void contentsChanged(ListDataEvent e) {
        variables.clear();
        for (int i = 0; i < listModel.getSize(); i++) {
          Entry<String, ExperimentVariable<?>> s =
              (Entry<String, ExperimentVariable<?>>) listModel.get(i);
          variables.add(s.getValue());
        }
      }
    });

    Action parameterAddAction =
        new ParameterAddAction(listModel, variableTypes);
    JButton addButton = new JButton(parameterAddAction);
    JButton removeButton = new JButton("-");
    removeButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        listModel.removeElement(list.getSelectedValue());
      }
    });

    controls.add(addButton);
    controls.add(removeButton);

    JPanel vBox = new JPanel(new BorderLayout());

    vBox.add(controls, BorderLayout.PAGE_END);
    vBox.add(new JScrollPane(list), BorderLayout.CENTER);

    this.add(vBox, BorderLayout.LINE_START);
  }

  public ExperimentVariables getExperimentVariables() {
    ExperimentVariables exp = new ExperimentVariables();
    for (ExperimentVariable<?> e : variables) {
      exp.addVariable(e);
    }
    return exp;
  }

  public void setExperimentVariables(ExperimentVariables vars) {
    // TODO sr137
    throw new UnsupportedOperationException("not yet implemented!");
  }
}
