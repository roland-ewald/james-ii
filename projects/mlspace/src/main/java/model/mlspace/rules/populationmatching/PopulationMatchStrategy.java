package model.mlspace.rules.populationmatching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.RuleEntity;
import model.mlspace.rules.NonTransferRule;

public class PopulationMatchStrategy {

  public static <E extends AbstractModelEntity> Collection<NSMMatch<E>> match(
      NonTransferRule rule, AbstractModelEntity context,
      Map<E, Integer> stateVec, double volume) {
    Map<String, Object> vars = rule.matchContext(context);
    if (vars == null) {
      return Collections.emptyList();
    }

    if (rule.getOrder() == 0) { // shortcut (method should also work without any
                                // shortcuts)
      return Collections
          .singletonList(new NSMMatch<>(volume * rule.getRate((Map) vars), vars,
              Collections.emptyList(), rule));
    }

    if (rule.getOrder() == 1) {
      return matchFirstOrder(rule, stateVec, vars);
    }

    return matchHigherOrder(rule, stateVec, vars, volume);
  }

  private static <E extends AbstractModelEntity> Collection<NSMMatch<E>> matchFirstOrder(
      NonTransferRule rule, Map<E, Integer> stateVec,
      Map<String, Object> vars) {
    Collection<NSMMatch<E>> rv = new ArrayList<>(stateVec.size());
    RuleEntity reactant = rule.getLeftHandSide().getReactants().get(0);
    boolean changesEnv = !reactant.getVarNames().isEmpty();
    Map<String, Object> env = changesEnv ? new LinkedHashMap<>(vars) : vars;
    for (Map.Entry<E, Integer> e : stateVec.entrySet()) {
      if (reactant.matches(e.getKey(), env)) {
        rv.add(new NSMMatch<>(e.getValue() * rule.getRate((Map) env), env,
            Collections.singletonList(
                Collections.singletonMap(e.getKey(), e.getValue())),
            rule));
        if (changesEnv) {
          env = new LinkedHashMap<>(vars);
        }
      }
    }
    return rv;
  }

  private static <E extends AbstractModelEntity> Collection<NSMMatch<E>> matchHigherOrder(
      NonTransferRule rule, Map<E, Integer> stateVec, Map<String, Object> vars,
      double volume) {
    int stateVecLength = stateVec.size();
    if (stateVecLength == 0) { // shortcut (method should work also without
                               // this, which however relies on the previous
                               // 0-order-rule shortcut)
      return Collections.emptyList();
    }

    E[] stateVecEnts = (E[]) new AbstractModelEntity[stateVecLength];
    int[] stateVecAmounts = new int[stateVecLength];
    {
      int i = 0;
      for (Map.Entry<E, Integer> entry : stateVec.entrySet()) {
        stateVecEnts[i] = entry.getKey();
        stateVecAmounts[i] = entry.getValue();
        i++;
      }
    }
    List<int[]> matchedBefore = new ArrayList<int[]>();
    matchedBefore.add(new int[stateVecLength]);
    List<List<Map<E, Integer>>> mEntsBefore = new ArrayList<>();
    mEntsBefore.add(new ArrayList<>());
    List<Map<String, Object>> envsBefore = new ArrayList<>();
    envsBefore.add(vars);

    for (RuleEntity reactant : rule.getLeftHandSide().getReactants()) {

      List<int[]> matchedAfter = new ArrayList<>();
      List<List<Map<E, Integer>>> mEntsAfter = new ArrayList<>();
      List<Map<String, Object>> envsAfter = new ArrayList<>();

      matchSingleReactant(reactant, stateVecEnts, stateVecAmounts,
          stateVecLength, matchedBefore, mEntsBefore, envsBefore, matchedAfter,
          mEntsAfter, envsAfter);
      if (matchedAfter.isEmpty()) {
        return Collections.emptyList();
      }
      matchedBefore = matchedAfter;
      mEntsBefore = mEntsAfter;
      envsBefore = envsAfter; // after the match is before the match
    }

    int numMatches = envsBefore.size();
    Collection<NSMMatch<E>> rv = new ArrayList<>(numMatches);

    double volCorr = 1. / volume;
    for (int i = rule.getOrder(); i > 2; i--) {
      volCorr /= volume;
    }
    for (int i = 0; i < numMatches; i++) {
      Map<String, Object> env = envsBefore.get(i);
      int[] matchedAmounts = matchedBefore.get(i);
      // TODO: volume and binomial adjustment
      rv.add(new NSMMatch<>(
          volCorr * rule.getRate((Map) env)
              * combinations(stateVecAmounts, matchedAmounts),
          env, mEntsBefore.get(i), rule));
    }
    return rv;
  }

  private static <E extends AbstractModelEntity> void matchSingleReactant(
      RuleEntity reactant, E[] stateVecEnts, int[] stateVecAmounts,
      int stateVecLength, List<int[]> matchedBefore,
      List<List<Map<E, Integer>>> mEntsBefore,
      List<Map<String, Object>> envsBefore, List<int[]> matchedAfter,
      List<List<Map<E, Integer>>> mEntsAfter,
      List<Map<String, Object>> envsAfter) {
    boolean changesEnv = !reactant.getVarNames().isEmpty();
    for (int iMatchBefore = 0; iMatchBefore < envsBefore
        .size(); iMatchBefore++) {
      int[] matchBefore = matchedBefore.get(iMatchBefore);
      List<Map<E, Integer>> mEnts = mEntsBefore.get(iMatchBefore);
      Map<String, Object> env = envsBefore.get(iMatchBefore);
      for (int iStateVec = 0; iStateVec < stateVecEnts.length; iStateVec++) {
        if (stateVecAmounts[iStateVec] <= matchBefore[iStateVec]) {
          assert stateVecAmounts[iStateVec] == matchBefore[iStateVec];
          continue; // all respective present entities already matched, e.g.
                    // when matching S+S->... with only 1 S present
        }
        Map<String, Object> newEnv =
            changesEnv ? new LinkedHashMap<>(env) : env;
        if (reactant.matches(stateVecEnts[iStateVec], newEnv)) {
          int[] newMatch = Arrays.copyOf(matchBefore, stateVecLength);
          newMatch[iStateVec]++;
          matchedAfter.add(newMatch);
          List<Map<E, Integer>> newMEnts = new ArrayList<>(mEnts);
          newMEnts.add(Collections.singletonMap(stateVecEnts[iStateVec], 1));
          mEntsAfter.add(newMEnts);
          envsAfter.add(newEnv);
        }
      }
    }
  }

  private static long combinations(int[] amounts, int[] toChoose) {
    long rv = 1l;
    for (int i = amounts.length - 1; i >= 0; i--) {
      rv *= nChooseK(amounts[i], toChoose[i]);
    }
    return rv;
  }

  /**
   * n choose k assuming 0<=k<=n, best used with k<=n/2.
   * 
   * @param n
   * @param k
   * @return (n k)
   */
  private static int nChooseK(int n, int k) {
    if (k == 0) {
      return 1;
    }
    if (k == 1) {
      return n;
    }
    int rv = n;
    int i = 1;
    while (i < k) {
      rv *= n - i;
      i++;
      rv /= i;
    }
    return rv;
  }
}
