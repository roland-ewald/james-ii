/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.data.model.ModelFileReaderFactory;
import org.jamesii.core.data.model.read.plugintype.IMIMEType;
import org.jamesii.core.factories.Context;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.model.carules.CARule;
import org.jamesii.model.carules.reader.antlr.parser.AndExpression;
import org.jamesii.model.carules.reader.antlr.parser.BooleanCondition;
import org.jamesii.model.carules.reader.antlr.parser.CurrentStateCondition;
import org.jamesii.model.carules.reader.antlr.parser.StateCondition;
import org.jamesii.model.carules.symbolic.ISymbolicCAModel;

/**
 * The Class CAModelReaderWriterFactory.
 * 
 * @author Stefan Rybacki
 */
@Plugin(description = "Reader Factory for CA models.")
public class CAModelReaderFactory extends ModelFileReaderFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 6193249743262345127L;

  /** The GRID factory to be used. */
  public static final String CGRID = "GRID";

  @Override
  public String getDescription() {
    return "Rule based Cellular Automata files";
  }

  @Override
  public String getFileEnding() {
    return "car";
  }

  @Override
  public IModelReader create(ParameterBlock params, Context context) {
    return new CAModelFileReader(params);
  }

  @Override
  public boolean supportsModel(IModel model) {
    return false;
  }

  @Override
  public boolean supportsModel(ISymbolicModel<?> model) {
    return model instanceof ISymbolicCAModel;
  }

  /**
   * Creates a {@link CARule} using the specified parameters. This method is
   * only for convenience and does not provide full capability of expressing
   * rules which can be expressed by {@link CARule}.
   * 
   * @param currentState
   *          the current state
   * @param targetState
   *          the target state
   * @param neighborStates
   *          the neighbor states
   * @param allStates
   *          all possible states in correct order
   * @return the generated CA rule
   */
  public static CARule createSingleRule(String currentState,
      String targetState, String[] neighborStates, String[] allStates) {
    Map<String, Integer> map = new HashMap<>();
    for (String s : neighborStates) {
      Integer integer = map.get(s);
      if (integer == null) {
        integer = 1;
      } else {
        integer++;
      }
      map.put(s, integer);
    }

    AndExpression a = new AndExpression(new BooleanCondition(true));
    List<String> states = Arrays.asList(allStates);
    for (Entry<String, Integer> e : map.entrySet()) {
      int i = states.indexOf(e.getKey());
      a.addCondition(new StateCondition(i, e.getValue(), e.getValue()));
    }

    return new CARule(new CurrentStateCondition(states.indexOf(currentState)),
        a, states.indexOf(targetState), 1d);
  }

  @Override
  public boolean supportsMIMEType(IMIMEType mime) {
    // TODO Auto-generated method stub
    return false;
  }
}
