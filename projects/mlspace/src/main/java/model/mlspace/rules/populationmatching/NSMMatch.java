package model.mlspace.rules.populationmatching;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.rules.NonTransferRule;
import model.mlspace.rules.match.Match;
import model.mlspace.rules.match.SuccessfulMatch;

/**
 * @author Arne Bittig
 * @date 02.07.2014
 * 
 * @param <E>
 *          Entity type
 */
public class NSMMatch<E extends AbstractModelEntity>
    implements Match<E> {

  private final double rate;

  private final Map<String, Object> env;

  private final List<Map<E, Integer>> matched;

  private final NonTransferRule rule;

  /**
   * @param rate
   *          calculated rate constant
   * @param env
   *          Extracted variables (for potential modifications)
   * @param matched
   *          Matched entities and amounts
   * @param rule
   *          Matched rule
   */
  public NSMMatch(double rate, Map<String, Object> env,
      List<Map<E, Integer>> matched, NonTransferRule rule) {
    this.rate = rate;
    this.env = env;
    this.matched = matched;
    this.rule = rule;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public SuccessfulMatch.ModRecord<E> apply() {
    throw newUnsupObsEx();
  }

  private static UnsupportedOperationException newUnsupObsEx() {
    return new UnsupportedOperationException("Special handling required"
        + " (entities cannot be modified directly)");
  }

  @Override
  public Map<String, Object> getEnv() {
    return env;
  }

  @Override
  public double getRate() {
    return rate;
  }

  /**
   * @return matched entities and amounts for each element of rule left hand
   *         side
   */
  public List<Map<E, Integer>> getMatched() {
    return matched;
  }

  /**
   * @return matched rule
   */
  @Override
  public NonTransferRule getRule() {
    return rule;
  }

  @Override
  public String toString() {
    return "NSMMatch [matched=" + matched + ", rule=" + rule + ", rate="
        + rate + ", env=" + env + "]";
  }

  @Override
  public SuccessfulMatch.ModRecord<E> applyReplacing(
      model.mlspace.rules.match.Match.IReplacer<? super E> rep) {
    throw newUnsupObsEx();

  }

  @Override
  public Collection<E> getConsumed() {
    throw newUnsupObsEx();
  }

}