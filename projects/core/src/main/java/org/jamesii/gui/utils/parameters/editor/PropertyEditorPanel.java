/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IEditableSet;
import org.jamesii.gui.utils.parameters.editable.IEditor;
import org.jamesii.gui.utils.parameters.editable.IEditorTableModel;
import org.jamesii.gui.utils.parameters.editable.IPropertyEditor;
import org.jamesii.gui.utils.parameters.editor.plugintype.AbstractParamEditorFactory;
import org.jamesii.gui.utils.parameters.editor.plugintype.ParamEditorFactory;

/**
 * Dialog for graphical editing of parameters.
 * 
 * Created on June 7, 2004
 * 
 * @author Roland Ewald
 */
// TODO sr137: high! rework this component make use of table cell editors
// (combine it with IEditor<D>) don't
// rely on setRowHeight(23) and so on make it faster?!
public class PropertyEditorPanel extends JPanel implements IPropertyEditor {

  /** Serialisation ID. */
  static final long serialVersionUID = 8048335751165184012L;

  /** Flag to define if the combo-box should be shown. */
  private boolean showComboBox = true;

  /** TreeModel of parameters that is mapped to a combo-box-list. */
  private ParameterComboBoxCellRenderer comboRenderer =
      new ParameterComboBoxCellRenderer();

  /** List of all parameters visible in the table by now. */
  private List<IEditable<?>> currentParameters = new ArrayList<>();

  /** List of all existing parameters. */
  private List<IEditable<?>> parameters = null;

  /**
   * Flag to determine whether the parameter selection of the user is enabled.
   * In some cases the parameter selection box detects an action event but
   * should _not_ answer it with refreshing the table (=>if the box is updated).
   */
  private boolean parameterSelectionEnabled = true;

  /** Tree-Model of parameters that is mapped to a table. */
  private ParameterTableModel parameterTableModel = null;

  /** Box to select the parameter to display. */
  private JComboBox paramSelection = new JComboBox();

  /** The parameter table. */
  private JTable table = null;

  /** Storage for created editors. */
  private Map<IEditable<?>, IEditor<?>> createdEditors = new HashMap<>();

  /**
   * Create Dialog from existing parameters.
   * 
   * @param params
   *          the parameters to be displayed
   * @param displayComboBox
   *          flag to decide whether the combo-box shall be displayed
   */
  public PropertyEditorPanel(List<IEditable<?>> params, boolean displayComboBox) {
    parameters = params;
    showComboBox = displayComboBox;
    init();
  }

  /**
   * Instantiates a new property editor panel.
   * 
   * @param params
   *          the params
   */
  public PropertyEditorPanel(List<IEditable<?>> params) {
    this(params, true);
  }

  /**
   * Instantiates a new property editor panel.
   */
  public PropertyEditorPanel() {
    this(new ArrayList<IEditable<?>>(), true);
  }

  /**
   * Initializes panel.
   */
  protected void init() {
    initializeUIComponents();

    currentParameters.clear();
    for (IEditable<?> p : parameters) {
      currentParameters.add(p);
    }

    showParameter(currentParameters);
    refreshParameterTable();
    refreshParameterChooser();
  }

  /**
   * Adds a parameter (recursively) to the selection combobox.
   * 
   * @param parameter
   *          the parameter
   * @param indent
   *          number of indentation
   */
  public void addParameterToComboBox(IEditable<?> parameter, int indent) {

    paramSelection.addItem(parameter);
    comboRenderer.getIndentions().put(parameter, Integer.valueOf(indent));

    if (!parameter.hasSubVariable()) {
      return;
    }

    IEditable<?> content = parameter.getSubVariable();
    if (content instanceof IEditableSet) {
      List<IEditable<?>> elems = ((IEditableSet) content).getParameters();
      for (int i = 0; i < elems.size(); i++) {
        addParameterToComboBox(elems.get(i), indent + 1);
      }
    }
  }

  /**
   * Gets the parameters.
   * 
   * @return the parameters
   */
  public List<IEditable<?>> getParameters() {
    return parameters;
  }

  @Override
  public IEditorTableModel getTableModel() {
    return parameterTableModel;
  }

  /**
   * Initialises all UI components.
   */
  public void initializeUIComponents() {
    // Configure combo-box
    if (showComboBox) {
      showParameterComboBox(parameters);
      paramSelection.setRenderer(comboRenderer);
      paramSelection.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (parameterSelectionEnabled) {
            refreshParameterList();
          }
        }
      });
    }

    table = new JTable();
    table.setFillsViewportHeight(true);
    setLayout(new BorderLayout(5, 5));
    add(new JScrollPane(table), BorderLayout.CENTER);

    table.setRowHeight(23);
    table.setRowSelectionAllowed(true);
    table.setColumnSelectionAllowed(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Add event handlers
    table.addMouseListener(new MouseAdapter() {
      // try to 'open' a parameter if user clicks on the left column

      @Override
      public void mouseClicked(MouseEvent e) {
        JTable jt = null;
        try {
          jt = (JTable) e.getSource();
        } catch (Exception ex) {
          return;
        }
        if (jt == null) {
          return;
        }
        if (jt.getSelectedColumn() == 0) {
          parameterTableModel.openParam(jt.getSelectedRow());
        }
        jt.repaint();
      }
    });

    if (showComboBox) {
      add(paramSelection, BorderLayout.NORTH);
    }

    // Show initial parameter list and selection combo-box
    showParameter(parameters, false);
  }

  @Override
  public void refreshParameterChooser() {
    if (showComboBox) {
      showParameterComboBox(parameters);
    }
  }

  /**
   * Refreshes the parameter list (sets the selected parameter as the root
   * parameter).
   */
  public void refreshParameterList() {
    Object o = paramSelection.getSelectedItem();

    if (o instanceof String) {
      showParameter(parameters, false);
    } else if (o instanceof IEditable<?>) {

      List<IEditable<?>> al = new ArrayList<>();
      al.add((IEditable<?>) o);
      showParameter(al, false);

      if (((IEditable<?>) o).isComplex()) {
        parameterTableModel.openParam(0);
      }
    }

  }

  @Override
  public void refreshParameterTable() {
    showParameter(currentParameters, true);
  }

  @Override
  public void showParameter(List<IEditable<?>> params) {
    showParameter(params, false);
  }

  /**
   * Shows list of parameters in the parameter table.
   * 
   * @param params
   *          list of parameters
   * @param keepNodeInfo
   *          if true, opened nodes remain open, etc.
   */
  public void showParameter(List<IEditable<?>> params, boolean keepNodeInfo) {

    // cloning the parameter array list
    currentParameters = new ArrayList<>();
    for (IEditable<?> p : params) {
      currentParameters.add(p);
    }

    if (parameterTableModel == null || !keepNodeInfo) {
      parameterTableModel = new ParameterTableModel(params);
    } else {
      parameterTableModel.refreshStructure();
    }

    // Create new view
    table.setModel(parameterTableModel);
    table.getColumnModel().getColumn(0)
        .setCellRenderer(new NameCellRenderer(parameterTableModel));
    table.getColumnModel().getColumn(1)
        .setCellRenderer(new ValueCellRenderer(this));
    table.getColumnModel().getColumn(1)
        .setCellEditor(new ParameterValueEditor(this));

    /*
     * // Configure objects table.setBackground(Color.LIGHT_GRAY);
     * table.setGridColor(Color.black);
     * table.setSelectionForeground(Color.white);
     * table.setSelectionBackground(Color.blue);
     */
  }

  /**
   * Initialises parameter combo box.
   * 
   * @param params
   *          the parameters to be shown
   */
  public void showParameterComboBox(List<IEditable<?>> params) {

    // :tricky: parameter selection has to be deactivated, otherwise the
    // selection of the user will be lost, due to refresh
    parameterSelectionEnabled = false;

    paramSelection.removeAllItems();
    paramSelection.addItem("---");
    for (IEditable<?> param : params) {
      addParameterToComboBox(param, 1);
    }

    parameterSelectionEnabled = true;
  }

  /**
   * Creates the editor.
   * 
   * @param absParam
   *          the parameters containing the class of the object to be edited
   * @param editorFactory
   *          the editor factory
   * @param editable
   *          the editable of type X
   * 
   * @param <X>
   *          the type of the object to be edited
   * 
   * @return a suitable editor
   */
  @SuppressWarnings("unchecked")
  // It is asserted by the factories that the types are compatible
  protected <X> IEditor<X> createEditor(ParameterBlock absParam,
      ParamEditorFactory editorFactory, IEditable<X> editable) {
    IEditor<X> editor = (IEditor<X>) editorFactory.create(absParam, SimSystem.getRegistry().createContext());
    try {
      editor.configureEditor(editable, this);
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE,
          "Editor configuration problem:" + ex.getMessage(), ex);
    }
    return editor;
  }

  @Override
  public IEditor<?> getEditor(IEditable<?> editable) {

    IEditor<?> editor = createdEditors.get(editable);
    if (editor != null) {
      return editor;
    }

    ParameterBlock apefp =
        new ParameterBlock(editable.getValue().getClass(),
            AbstractParamEditorFactory.TYPE);

    editor =
        createEditor(
            apefp,
            SimSystem.getRegistry().getFactory(
                AbstractParamEditorFactory.class, apefp), editable);
    createdEditors.put(editable, editor);

    return editor;
  }

}
