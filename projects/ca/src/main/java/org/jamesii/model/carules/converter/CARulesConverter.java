/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.converter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.model.symbolic.convert.IConverter;
import org.jamesii.core.model.symbolic.convert.IDocument;
import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.cacore.neighborhood.MooreNeighborhood;
import org.jamesii.model.cacore.neighborhood.NeumannNeighborhood;
import org.jamesii.model.carules.CARule;
import org.jamesii.model.carules.CARulesAntlrDocument;
import org.jamesii.model.carules.ICACondition;
import org.jamesii.model.carules.reader.antlr.SymbolicCAModelInformation;
import org.jamesii.model.carules.reader.antlr.parser.AndExpression;
import org.jamesii.model.carules.reader.antlr.parser.CAProblemToken;
import org.jamesii.model.carules.reader.antlr.parser.CaruleLexer;
import org.jamesii.model.carules.reader.antlr.parser.CaruleParser;
import org.jamesii.model.carules.reader.antlr.parser.CurrentStateCondition;
import org.jamesii.model.carules.reader.antlr.parser.NotCondition;
import org.jamesii.model.carules.reader.antlr.parser.OrExpression;
import org.jamesii.model.carules.reader.antlr.parser.StateCondition;
import org.jamesii.model.carules.reader.antlr.parser.CaruleParser.camodel_return;
import org.jamesii.model.carules.symbolic.ISymbolicCAModelInformation;
import org.jamesii.model.carules.symbolic.SymbolicCAModel;

/**
 * The Class CARulesConverter. Converts a document from a textual representation
 * (created according to our CR rules grammar) into an symbolic model, and vice
 * versa.
 */
public class CARulesConverter implements IConverter {

  @Override
  public ISymbolicModel<?> fromDocument(IDocument<?> document) {
    if (!(document.getContent() instanceof String)) {
      return null;
    }

    String source = (String) document.getContent();

    camodel_return info = null;

    CaruleLexer lexer;
    // try to parse text to record of model information rules, states and so on
    CommonTokenStream tokenStream =
        new CommonTokenStream(lexer =
            new CaruleLexer(new ANTLRStringStream(source)));
    CaruleParser parser = new CaruleParser(tokenStream);
    try {
      info = parser.camodel();
    } catch (RecognitionException e) {
      info = new camodel_return();
    }

    // merge lexer problems with parser problems
    List<CAProblemToken> problems =
        new ArrayList<>(info.problems);
    for (CAProblemToken t : lexer.getProblemTokens()) {
      problems.add(t);
    }
    Collections.sort(problems);
    info.problems = problems;

    SymbolicCAModel result = new SymbolicCAModel();

    result.setFromDataStructure(new SymbolicCAModelInformation(info, source));

    return result;
  }

  @Override
  public IDocument<?> toDocument(ISymbolicModel<?> data) {
    StringBuilder builder = new StringBuilder();
    ISymbolicCAModelInformation info =
        (ISymbolicCAModelInformation) data.getAsDataStructure();

    if (info instanceof SymbolicCAModelInformation
        && info.getModelSource() != null) {
      return new CARulesAntlrDocument(info.getModelSource());
    }

    // if not try to create model source from model information

    if (info.getModelComment() != null) {
      builder.append("/**\n");
      builder.append(info.getModelComment() + "\n");
      builder.append("\n*/\n");
    }

    builder.append("@caversion 1;\n");

    if (info.isWolfram()) {
      builder.append("wolframrule " + info.getWolframRule() + ";\n");
    } else {
      builder.append("dimensions " + info.getDimensions() + ";\n");
      if (info.getNeighborhood().getComment() != null) {
        builder.append("/**\n");
        builder.append(info.getNeighborhood().getComment());
        builder.append("\n*/\n");
      }
      if (info.getNeighborhood() instanceof MooreNeighborhood) {
        builder.append("neighborhood moore;\n");
      } else if (info.getNeighborhood() instanceof NeumannNeighborhood) {
        builder.append("neighborhood neumann;\n");
      } else {
        builder.append("neighborhood free {\n");
        INeighborhood n = info.getNeighborhood();
        for (int i = 0; i < n.getCellCount(); i++) {
          int[] cell = n.getCell(i);
          builder.append("                   [");
          for (int d = 0; d < n.getDimensions(); d++) {
            if (d > 0) {
              builder.append(", ");
            }
            builder.append(cell[d]);
          }
          builder.append("]\n");
        }
        builder.append("                  };\n");
      }

      String st = null;
      for (String s : info.getStates()) {
        if (st != null) {
          st += ", ";
        } else {
          st = "";
        }
        st += s;
      }

      builder.append("state " + st + ";\n\n");

      for (CARule rule : info.getRules()) {

        String c = conditionToString(rule.getCurrentCondition(), info);

        String ruleStr = "";

        if (c.length() > 0) {

          ruleStr += "{";

          ruleStr += c;

          ruleStr += "}";
        }

        if (Double.compare(rule.getProbability(), 1.0) != 0) {
          NumberFormat nFormat = NumberFormat.getNumberInstance();
          nFormat.setGroupingUsed(false);
          nFormat.setMaximumFractionDigits(Integer.MAX_VALUE);
          ruleStr +=
              "[" + nFormat.format(rule.getProbability()).replace(',', '.')
                  + "]";
        }

        ruleStr += " : ";

        ruleStr += conditionToString(rule.getPreCondition(), info);

        ruleStr +=
            " -> " + info.getStates().get(rule.getDestinationState()) + ";\n";

        if (rule.getComment() != null) {
          builder.append("/**\n");
          builder.append(rule.getComment());
          builder.append("\n*/\n");
        }

        builder.append("rule " + ruleStr + "\n");

      }

    }

    return new CARulesAntlrDocument(builder.toString());

  }

  /**
   * Condition to string.
   * 
   * @param condition
   *          the condition
   * @param info
   *          the info for the symbolic ca model
   * @return the string
   */
  private String conditionToString(ICACondition condition,
      ISymbolicCAModelInformation info) {

    StringBuilder result = new StringBuilder();

    if (condition instanceof CurrentStateCondition) {
      for (Integer st : ((CurrentStateCondition) condition).getStateList()) {
        if (result.length() != 0) {
          result.append(", ");
        }
        result.append(info.getStates().get(st));
      }
      return result.toString();
    }

    if (condition instanceof StateCondition) {
      StateCondition cond = ((StateCondition) condition);
      if (cond.getMin() == cond.getMax() && cond.getMax() == 1) {
        result = new StringBuilder();
      } else if (cond.getMin() == cond.getMax()) {
        result = new StringBuilder("{" + cond.getMin() + "}");
      } else if (cond.getMax() == Integer.MAX_VALUE) {
        result = new StringBuilder("{" + cond.getMin() + ",}");
      } else if (cond.getMin() <= 0) {
        result = new StringBuilder("{," + cond.getMax() + "}");
      } else {
        result =
            new StringBuilder("{" + cond.getMin() + "," + cond.getMax() + "}");
      }
      return info.getStates().get(cond.getState()) + result;
    }

    if (condition instanceof NotCondition) {
      NotCondition cond = (NotCondition) condition;
      result.append("!" + conditionToString(cond.getCondition(), info));
    }

    String tempResult = "";

    String concat = "";

    if (condition instanceof AndExpression) {
      concat = " and ";
    }

    if (condition instanceof OrExpression) {
      concat = " or ";
      if (condition.getConditionCount() > 1) {
        tempResult = "(";
      }
    }

    for (int i = 0; i < condition.getConditionCount(); i++) {

      ICACondition cond = condition.getCondition(i);

      if (i > 0) {
        tempResult += concat;
      }

      tempResult += conditionToString(cond, info);

    }

    if (condition instanceof OrExpression && condition.getConditionCount() > 1) {
      tempResult += ")";
    }

    result.append(tempResult);

    return result.toString();

  }

}
