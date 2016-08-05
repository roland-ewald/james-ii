/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jamesii.core.observe.csv.SnapshotCSVObserver;
import org.jamesii.core.util.collection.CombinedIterator;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.logging.ApplicationLogger;

import model.mlspace.IMLSpaceModel;
import model.mlspace.rules.MLSpaceRule;
import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.eventrecord.IContSpaceEventRecord;
import simulator.mlspace.eventrecord.IEventRecord;
import simulator.mlspace.util.ExtendableList;

/**
 * Observer counting firing times of reactions
 * 
 * @author Arne Bittig
 * @date 17.01.2014
 */
public class ReactionObserver extends AbstractEffectObserver<IEventRecord>
    implements
    SnapshotCSVObserver.SnapshotPlugin<AbstractMLSpaceProcessor<?, ?>> {

  private final boolean recordNoReact;

  private final boolean initForAll;

  private OutputStreamWriter fw;

  /** Properties of produced csv file */
  protected static final char CSV_SEP = ',';

  protected static final char NEW_LINE = '\n';

  private final IUpdateableMap<MLSpaceRule, Integer> ruleCount =
      new UpdateableAmountMap<>();

  private final Map<String, List<Integer>> ruleCountSnapshots =
      new LinkedHashMap<>();

  private final Map<String, MLSpaceRule> ruleNameMap = new LinkedHashMap<>();

  /**
   * @param recordNoReact
   *          Flag whether to record number of events without reaction
   * @param initForAll
   *          Flag whether to extract reactions from model and initialize
   *          columns for each (instead of on-the-fly at first application)
   * @param filename
   *          Filename for csv output file with exact reaction times
   */
  public ReactionObserver(boolean recordNoReact, boolean initForAll,
      String filename) {
    this.recordNoReact = recordNoReact;
    this.initForAll = initForAll;
    if (filename == null) {
      fw = null;
    } else {
      try {
        fw = new FileWriter(filename);
        fw.write("Time" + CSV_SEP + "Reaction" + NEW_LINE);
      } catch (IOException e) {
        fw = null;
        ApplicationLogger.log(e);
      }
    }
  }

  @Override
  protected boolean init(AbstractMLSpaceProcessor<?, IEventRecord> proc) {
    IMLSpaceModel model = proc.getModel();
    if (initForAll) {
      Iterator<MLSpaceRule> ruleIt =
          new CombinedIterator<>(model.getTimedReactionRules(),
              model.getCollisionTriggeredRules(), model.getTransferInRules(),
              model.getTransferOutRules(), model.getNSMReactionRules());
      while (ruleIt.hasNext()) {
        ruleCountSnapshots.put(ruleIt.next().getName(),
            new ExtendableList<Integer>());
      }
    }
    return true;
  }

  @Override
  protected void recordEffect(Double time, IEventRecord effect) {
    if (!effect.isSuccess()) {
      return;
    }
    Collection<? extends MLSpaceRule> rules;
    if (effect instanceof IContSpaceEventRecord) {
      rules = ((IContSpaceEventRecord) effect).getAllRules();
    } else {
      rules = effect.getRules();
    }
    for (MLSpaceRule r : rules) {
      Integer newValue = ruleCount.add(r);
      if (newValue == 1) {
        ruleNameMap.put(r.getName(), r);
      }
      if (fw != null) {
        try {
          fw.write("" + time + CSV_SEP + "\"" + r + "\"" + NEW_LINE);
        } catch (IOException e) {
          ApplicationLogger.log(e);
        }
      }
    }
    if (recordNoReact && rules.isEmpty()) {
      ruleCount.add(null);
    }
  }

  @Override
  protected void cleanUp(AbstractMLSpaceProcessor<?, IEventRecord> proc) {
    if (fw == null) {
      return; // reaction counts only for snapshot plugin, not for own file
    }
    try {
      fw.close();
    } catch (IOException e) {
      ApplicationLogger.log(e);
    }
  }

  @Override
  public void updateState(AbstractMLSpaceProcessor<?, ?> proc, int i) {
    for (Map.Entry<MLSpaceRule, Integer> e : ruleCount.entrySet()) {
      String key =
          e.getKey() == null ? "no-reaction events" : e.getKey().getName();
      if (ruleCountSnapshots.containsKey(key)) {
        ruleCountSnapshots.get(key).set(i, e.getValue());
      } else {
        // assert !initForAll;
        List<Integer> lst = new ExtendableList<>();
        lst.set(i, e.getValue());
        ruleCountSnapshots.put(key, lst);
      }
    }
  }

  @Override
  public Map<String, ? extends List<?>> getObservationData() {
    return ruleCountSnapshots;
  }

  Pattern pattern = Pattern.compile("(\\+|-)[^+-]*");

  /**
   * Evaluate addition/subtraction expression based on rule names, e.g. R1-R2
   * would give the difference between executions of R1 and R2
   * 
   * @param expr
   *          Expression involving rule names
   * @return Integer value
   * @throws IllegalArgumentException
   *           if any rule name not found
   */
  public int evaluateRuleCountByNameExpression(String expr) {
    if (ruleCountSnapshots.isEmpty()) {
      return Integer.MIN_VALUE; // no reactions happened yet
    }
    int result = 0;

    char firstChar = expr.charAt(0);
    Matcher matcher = pattern
        .matcher(firstChar == '+' || firstChar == '-' ? expr : "+" + expr);
    while (matcher.find()) {
      String group = matcher.group();
      String ruleName = group.substring(1, group.length());
      MLSpaceRule rule = ruleNameMap.get(ruleName);
      if (rule == null) {
        if (!ruleCountSnapshots.containsKey(ruleName)) {
          throw new IllegalArgumentException("Unknown rule name " + ruleName
              + "; known rule names: " + ruleCountSnapshots.keySet());
        } else {
          continue;// treat as 0 (rule has just not fired yet)
        }
      }

      Integer count = ruleCount.get(rule);
      if (group.charAt(0) == '-') {
        result -= count;
      } else {
        result += count;
      }
    }
    return result;
  }

}
