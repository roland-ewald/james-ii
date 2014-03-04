/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui.dialogs;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jamesii.core.plugins.IParameter;
import org.jamesii.perfdb.recording.selectiontrees.DoubleParameterBounds;
import org.jamesii.perfdb.recording.selectiontrees.IParameterBounds;
import org.jamesii.perfdb.recording.selectiontrees.IntParameterBounds;
import org.jamesii.perfdb.recording.selectiontrees.ParameterVertex;

/**
 * Panel to edit the constraints of {@link ParameterVertex}. Until now, only
 * parameters with types {@link Integer} and {@link Double} are supported.
 * 
 * @author Roland Ewald
 * 
 */
public class ParamVertexConstraintsPanel extends
    VertexConstraintsPanel<ParameterVertex> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 9179151145312263219L;

  /** Width of text fields to edit bounds. */
  private static final int TEXT_FIELD_WIDTH = 20;

  /** Set of available parameters. */
  private final Set<IParameter> parameters;

  /** Map from parameters to their bounds. */
  private final Map<IParameter, IParameterBounds<?>> paramBounds;

  /** Map from parameters to the input fields for their lower bound. */
  private Map<IParameter, JTextField> lowerBoundFields =
      new HashMap<>();

  /** Map from parameters to the input fields for their upper bound. */
  private Map<IParameter, JTextField> upperBoundFields =
      new HashMap<>();

  /**
   * Default constructor.
   * 
   * @param vertex
   *          the {@link ParameterVertex} of which the constraints shall be
   *          edited
   */
  public ParamVertexConstraintsPanel(ParameterVertex vertex) {
    super(vertex);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    parameters = vertex.getParameters();
    paramBounds = vertex.getConstraints().getParamBunds();

    for (IParameter parameter : parameters) {

      ParamType type = ParamType.getType(parameter.getType());
      if (type == null) {
        continue;
      }
      if (!paramBounds.containsKey(parameter)) {
        paramBounds.put(parameter,
            type == ParamType.Integer ? new IntParameterBounds()
                : new DoubleParameterBounds());
      }

      IParameterBounds<?> bounds = paramBounds.get(parameter);
      JTextField lowerBoundField = new JTextField(TEXT_FIELD_WIDTH);
      lowerBoundField.setText(bounds.getLowerBound().toString());
      lowerBoundFields.put(parameter, lowerBoundField);
      JTextField upperBoundField = new JTextField(TEXT_FIELD_WIDTH);
      upperBoundField.setText(bounds.getUpperBound().toString());
      upperBoundFields.put(parameter, upperBoundField);

      JPanel paramPanel = new JPanel();
      JLabel paramName = new JLabel(parameter.getName() + ":");
      paramName.setToolTipText(parameter.getDescription());
      paramPanel.add(paramName);
      paramPanel.add(lowerBoundField);
      paramPanel.add(new JLabel(" - "));
      paramPanel.add(upperBoundField);
      paramPanel
          .add(new JLabel(" (default value: " + parameter.getDefaultValue()
              + ", type:" + parameter.getType() + ")"));
      add(paramPanel);
    }
  }

  @Override
  public void save() {
    for (IParameter parameter : parameters) {
      ParamType type = ParamType.getType(parameter.getType());
      if (type == null) {
        continue;
      }
      switch (type) {
      case Integer: {
        IntParameterBounds bounds =
            (IntParameterBounds) paramBounds.get(parameter);
        bounds.setLowerBound(Integer.parseInt(lowerBoundFields.get(parameter)
            .getText()));
        bounds.setUpperBound(Integer.parseInt(upperBoundFields.get(parameter)
            .getText()));
      }
        break;
      case Double: {
        DoubleParameterBounds bounds =
            (DoubleParameterBounds) paramBounds.get(parameter);
        bounds.setLowerBound(Double.parseDouble(lowerBoundFields.get(parameter)
            .getText()));
        bounds.setUpperBound(Double.parseDouble(upperBoundFields.get(parameter)
            .getText()));
      }
        break;
      default:
        throw new IllegalArgumentException(
            "Only double and integer are supported.");
      }
    }
  }
}

/**
 * Enumerates all supported types.
 * 
 * @author Roland Ewald
 * 
 */
enum ParamType {
  Integer, Double;

  public static ParamType getType(String type) {
    if (Integer.class.getName().equals(type)) {
      return Integer;
    }
    if (Double.class.getName().equals(type)) {
      return Double;
    }

    return null;
  }
}
