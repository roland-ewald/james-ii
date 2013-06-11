/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.jamesii.gui.utils.parameters.editable.IEditable;
import org.jamesii.gui.utils.parameters.editable.IEditableSet;
import org.jamesii.gui.utils.parameters.editable.IEditorTableModel;

/**
 * Just a small structure to manage all data needed by {@link NameCellRenderer}.
 * 
 * @author Roland Ewald
 */
class InformationForParameter {

  /**
   * Flag that indicates if Parameter is 'expanded' (only possible for complex
   * parameters).
   */
  private boolean isOpened = false;

  /**
   * Level in the hierarchy.
   */
  private int level = 0;

  /**
   * Default constructor.
   * 
   * @param lvl
   *          the level of the parameter
   */
  InformationForParameter(int lvl) {
    setLevel(lvl);
  }

  /**
   * @return the isOpened
   */
  protected final boolean isOpened() {
    return isOpened;
  }

  /**
   * @param isOpened
   *          the isOpened to set
   */
  protected final void setOpened(boolean isOpened) {
    this.isOpened = isOpened;
  }

  /**
   * @return the level
   */
  protected final int getLevel() {
    return level;
  }

  /**
   * @param level
   *          the level to set
   */
  protected final void setLevel(int level) {
    this.level = level;
  }

}

/**
 * Delivers a table model to display the parameters correctly.
 * 
 * Created on June 8, 2004
 * 
 * @author Roland Ewald
 */
public class ParameterTableModel extends AbstractTableModel implements
    IEditorTableModel {

  /** Serialisation ID. */
  static final long serialVersionUID = 753798164739487804L;

  /** Holds additional information about the visible Parameters. */
  private Map<IEditable<?>, InformationForParameter> infoForParameters =
      new Hashtable<>();

  /** Holds original parameter list. */
  private List<IEditable<?>> parameters = null;

  /** Holds list with currently visible Parameters. */
  private List<IEditable<?>> visibleParameters = new ArrayList<>();

  /**
   * Standard constructor.
   * 
   * @param params
   *          the parameters
   */
  public ParameterTableModel(List<IEditable<?>> params) {
    parameters = params;
    for (IEditable<?> parameter : parameters) {
      visibleParameters.add(parameter);
      infoForParameters.put(parameter, new InformationForParameter(0));
    }
  }

  /**
   * Closes a sub-parameter recursively.
   * 
   * @param parameter
   *          the parameter to be closed
   */
  @Override
  public void closeSubParam(IEditable<?> parameter) {

    InformationForParameter pInfo = infoForParameters.get(parameter);

    if (parameter instanceof IEditableSet) {

      IEditableSet ps = (IEditableSet) parameter;
      List<IEditable<?>> subParams = ps.getParameters();

      for (int i = 0; i < subParams.size(); i++) {
        closeSubParam(subParams.get(i));
      }

    } else {

      if (pInfo.isOpened()) {

        Map<String, IEditable<?>> attributes = parameter.getAllAttributes();
        Iterator<String> enumA = attributes.keySet().iterator();

        // first delete the attributes:
        int lengthOfDeletedElements = 0;
        while (enumA.hasNext()) {
          IEditable<?> attr = attributes.get(enumA.next());
          visibleParameters.remove(visibleParameters.indexOf(attr));
          lengthOfDeletedElements++;
        }

        if (parameter.hasSubVariable()
            && (parameter.getSubVariable() instanceof IEditableSet)) {
          closeSubParam(parameter.getSubVariable());
        }

      }

    }

    if (!(parameter instanceof IEditableSet)) {
      visibleParameters.remove(visibleParameters.indexOf(parameter));
    }

    return;
  }

  @Override
  public int getColumnCount() {
    return 2;
  }

  /**
   * Just two columns with fixed names.
   * 
   * @param column
   *          column index
   * 
   * @return name of the column
   */
  @Override
  public String getColumnName(int column) {
    switch (column) {
    case 0:
      return "Parameter";
    case 1:
      return "Value";
    }
    return "";
  }

  /**
   * Checks whether parameter is already opened.
   * 
   * @param param
   *          parameter to be tested
   * 
   * @return the level of the parameter
   */
  @Override
  public int getParamLevel(IEditable<?> param) {
    return infoForParameters.get(param).getLevel();
  }

  @Override
  public int getRowCount() {
    return visibleParameters.size();
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    IEditable<?> selectedParameter = visibleParameters.get(rowIndex);
    return selectedParameter;
  }

  @Override
  public boolean isCellEditable(int row, int col) {
    // Only second column can be editable.
    return (col == 1);
  }

  /**
   * Checks whether parameter is already opened.
   * 
   * @param param
   *          parameter to be tested
   * 
   * @return true, if parameter is open
   */
  @Override
  public boolean isParamOpened(IEditable<?> param) {
    InformationForParameter ip = infoForParameters.get(param);
    if (ip == null) {
      ip = new InformationForParameter(0);
      infoForParameters.put(param, ip);
    }
    return ip.isOpened();
  }

  /**
   * Attempts to 'open' a parameter (display all sub-elements and attributes).
   * 
   * @param param
   *          the parameter to be opened
   */
  @Override
  public void openParam(int param) {

    IEditable<?> p = visibleParameters.get(param);

    if (!p.isComplex() && !(p instanceof IEditableSet)) {
      // only complex parameters and parameter sets can have sub-elements
      return;
    }

    InformationForParameter pInfo = infoForParameters.get(p);

    if (pInfo.isOpened()) {
      openParam(param, false);
    } else {
      openParam(param, true);
    }

  }

  /**
   * Attempts to 'open' a parameter (display all sub-elements and attributes).
   * 
   * @param param
   *          parameter to be opened
   * @param openNode
   *          true if it should not be opened, otherwise false
   */
  @Override
  public void openParam(int param, boolean openNode) {

    IEditable<?> parameter = visibleParameters.get(param);

    // Only complex parameters and parameter sets can have sub-elements
    if (!parameter.isComplex()) {
      return;
    }

    InformationForParameter pInfo = infoForParameters.get(parameter);

    Map<String, IEditable<?>> attributes = parameter.getAllAttributes();
    Iterator<String> enumA = attributes.keySet().iterator();

    // Open parameter
    if (openNode) {

      int numOfInsertedAttrs = 0;

      // First add the attributes:
      while (enumA.hasNext()) {
        IEditable<?> attr = attributes.get(enumA.next());

        numOfInsertedAttrs++;

        visibleParameters.add(param + numOfInsertedAttrs, attr);

        // update attribute info, if not existing
        if (infoForParameters.get(attr) == null) {
          infoForParameters.put(attr,
              new InformationForParameter(pInfo.getLevel() + 1));
        }

      }

      // then add the content
      if (parameter.hasSubVariable()) {
        IEditable<?> content = parameter.getSubVariable();
        if (content instanceof IEditableSet) {
          List<IEditable<?>> elements =
              ((IEditableSet) content).getParameters();

          for (int i = elements.size() - 1; i >= 0; i--) {
            openSubParam(elements.get(i), param + 1 + numOfInsertedAttrs,
                pInfo.getLevel() + 1);
          }
        }
      }

      pInfo.setOpened(true);

      this.fireTableRowsInserted(param + 1, param
          + recalculateLength(parameter) - 1);

    } else { // close parameter

      int oldLength = recalculateLength(parameter);

      // first delete the attributes:
      while (enumA.hasNext()) {
        IEditable<?> attr = attributes.get(enumA.next());
        visibleParameters.remove(visibleParameters.indexOf(attr));
      }

      // then delete the elements
      if (parameter.hasSubVariable()) {
        IEditable<?> content = parameter.getSubVariable();
        if (content instanceof IEditableSet) {
          List<IEditable<?>> elements =
              ((IEditableSet) content).getParameters();
          for (IEditable<?> element : elements) {
            closeSubParam(element);
          }
        }
      }

      pInfo.setOpened(false);
      this.fireTableRowsDeleted(param + 1, param + oldLength);
    }
  }

  /**
   * Opens a sub-parameter recursively.
   * 
   * @param parameter
   *          the parameter to be opened
   * @param startRow
   *          the start row
   * @param level
   *          the level
   */
  @Override
  public void openSubParam(IEditable<?> parameter, int startRow, int level) {

    if (!(parameter instanceof IEditableSet)) {
      visibleParameters.add(startRow, parameter);
    }

    // Test whether parameter info already exists:
    InformationForParameter pInfo = infoForParameters.get(parameter);

    // Test if there is something more to do
    if (pInfo == null) {
      pInfo = new InformationForParameter(level);
      infoForParameters.put(parameter, pInfo);
    }

    if (!(parameter instanceof IEditableSet) && !pInfo.isOpened()) {
      return;
    }

    if (parameter instanceof IEditableSet) {

      // recursively add all parameters
      IEditableSet ps = ((IEditableSet) parameter);
      List<IEditable<?>> subParams = ps.getParameters();

      for (int i = subParams.size() - 1; i >= 0; i--) {
        openSubParam(subParams.get(i), startRow + 1, level);
      }

    } else {

      Map<String, IEditable<?>> attributes = parameter.getAllAttributes();
      Iterator<String> enumA = attributes.keySet().iterator();

      int numOfInsertedAttrs = 0;

      // first add the attributes:
      while (enumA.hasNext()) {
        numOfInsertedAttrs++;
        IEditable<?> attr = attributes.get(enumA.next());
        visibleParameters.add(startRow + numOfInsertedAttrs, attr);
      }

      // then add the sub-elements
      if (parameter.hasSubVariable()
          && (parameter.getSubVariable() instanceof IEditableSet)) {
        this.openSubParam(parameter.getSubVariable(), startRow + 1, level + 1);
      }

    }

    return;
  }

  /**
   * Recalculates row length (recursively) for a given parameter.
   * 
   * @param parameter
   *          the parameter
   * 
   * @return row length of the parameter
   */
  @Override
  public int recalculateLength(IEditable<?> parameter) {

    InformationForParameter pInfo = infoForParameters.get(parameter);

    if (pInfo != null && !pInfo.isOpened()) {
      return 1;
    }

    if (parameter instanceof IEditableSet) {
      IEditableSet ps = (IEditableSet) parameter;
      List<IEditable<?>> params = ps.getParameters();

      int length = 0;

      for (IEditable<?> subparam : params) {
        length += recalculateLength(subparam);
      }

      return length;
    }

    Map<String, IEditable<?>> attributes = parameter.getAllAttributes();

    int elemLength = 0;

    if (parameter.hasSubVariable()) {
      IEditable<?> content = parameter.getSubVariable();
      if (content instanceof IEditableSet) {
        elemLength = recalculateLength(content);
      }
    }

    return 1 + attributes.size() + elemLength;

  }

  /**
   * Refreshes model structure.
   */
  @Override
  public void refreshStructure() {

    ArrayList<IEditable<?>> oldVisibleParameters = new ArrayList<>();
    for (IEditable<?> p : visibleParameters) {
      oldVisibleParameters.add(p);
    }

    visibleParameters.clear();

    int length = 0;

    // Add all parameters under root
    for (IEditable<?> p : oldVisibleParameters) {
      InformationForParameter ip = infoForParameters.get(p);

      if (ip.getLevel() == 0) {
        visibleParameters.add(p);
      } else {
        continue;
      }

      if (ip.isOpened()) {
        openParam(length, true);
        length += recalculateLength(p);
      } else {
        length += 1;
      }

    }

  }

  @Override
  @SuppressWarnings("unchecked")
  // TODO(re027): this needs refactoring anyway
  public void setValueAt(Object value, int row, int col) {
    if (value != null) {
      @SuppressWarnings("rawtypes")
      IEditable parameter = visibleParameters.get(row);
      if (!parameter.hasSubVariable()) {
        parameter.setValue(value);
      } else if (!(parameter.getSubVariable() instanceof IEditableSet)) {
        parameter.getSubVariable().setValue(value);
      }
    }
  }

}
