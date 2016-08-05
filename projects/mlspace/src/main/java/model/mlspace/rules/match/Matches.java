/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules.match;

import java.util.Collection;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.rules.MLSpaceRule;

/**
 * Rule matching result (was a boolean (success value) or double (rate value, 0
 * if no success) when rate expression depending on entity attributes were not
 * supported. Extracted to separate interface & classes to allow that. Grouped
 * in one utility class initially, may be refactored to separate classes.)
 * 
 * @author Arne Bittig
 * @date 07.11.2012
 */
public final class Matches {

  private Matches() {
  }

  /**
   * @return Failed match (singleton)
   */
  @SuppressWarnings("unchecked")
  public static <E extends AbstractModelEntity> Match<E> failure() {
    return FAILED_MATCH;
  }

  @SuppressWarnings({ "rawtypes", "synthetic-access" })
  private static final Match FAILED_MATCH = new FailedMatch();

  public static class FailedMatch implements Match<AbstractModelEntity> {

    @Override
    public boolean isSuccess() {
      return false;
    }

    @Override
    public Map<String, Object> getEnv() {
      throw newIlStEx();
    }

    // @Override
    // public Map<AbstractModelEntity, List<IAttributeModification>> getMods() {
    // throw newIlStEx();
    // }

    @Override
    public double getRate() {
      return 0;
    }

    @Override
    public SuccessfulMatch.ModRecord<AbstractModelEntity> apply() {
      throw newIlStEx();
    }

    @Override
    public SuccessfulMatch.ModRecord<AbstractModelEntity> applyReplacing(
        model.mlspace.rules.match.Match.IReplacer<? super AbstractModelEntity> rep) {
      throw newIlStEx();
    }

    private static IllegalStateException newIlStEx() {
      return new IllegalStateException("Not applicable for failures.");
    }

    // Preserves singleton property
    @SuppressWarnings({ "synthetic-access", "static-method" })
    private Object readResolve() {
      return FAILED_MATCH;
    }

    @Override
    public MLSpaceRule getRule() {
      throw newIlStEx();
    }

    @Override
    public Collection<AbstractModelEntity> getConsumed() {
      throw newIlStEx();
    }
  }
}
