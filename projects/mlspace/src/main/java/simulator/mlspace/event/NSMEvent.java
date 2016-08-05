/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.event;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jamesii.core.math.random.generators.IRandom;

import model.mlspace.entities.NSMEntity;
import model.mlspace.rules.NSMReactionRule;
import model.mlspace.rules.populationmatching.PopulationMatchStrategy;
import model.mlspace.rules.populationmatching.NSMMatch;
import model.mlspace.subvols.Subvol;

/**
 * This NSMEvent class encapsulates the reactions applicable to one subvol and
 * the possible diffusion to its neighbors. Rates are stored in a map here and
 * updated with every call to {@link #getSumOfReactionRates(Collection)} and
 * {@link #getSumOfDiffusionRates()}, respectively. These methods must be called
 * after every update to the related subvol's state (changed independently) for
 * correctness of the internal rate maps.
 * 
 * TODO: this should probably be split into two classes, one for reaction, the
 * other for diffusion (although instanceof checks would be needed in simulator)
 * 
 * @author Arne Bittig
 * @date 01.06.2012
 */
public class NSMEvent implements IMLSpaceEvent<Subvol> {

  /**
   * Type of NSM event - diffusion or reaction
   * 
   * @author Arne Bittig
   * @date 0x.06.2012
   */
  public enum NSMEventType {
    /** diffusion */
    NSMDIFFUSION, /** reaction */
    NSMREACTION
  }

  private final Subvol sv;

  private NSMEventType type;

  /** Diffusion rate for each contained entity group */
  private final Map<NSMEntity, Double> diffusionRates = new LinkedHashMap<>();

  /** Reaction matches and rates */
  private final Map<NSMMatch<NSMEntity>, Double> matchesAndRates =
      new LinkedHashMap<>();

  /**
   * @param sv
   */
  public NSMEvent(Subvol sv) {
    this.sv = sv;
    // assert type.isNSMEvent();
    // this.type = type;
  }

  @Override
  public Subvol getTriggeringComponent() {
    return sv;
  }

  /**
   * @return Type of event (see {@link NSMEventType})
   */
  public NSMEventType getType() {
    return type;
  }

  /**
   * @param type
   */
  public void setNSMType(NSMEventType type) {
    this.type = type;
  }

  /**
   * Get a random reaction match
   * 
   * @param rand
   *          Random number generator
   * @return Match
   */
  public NSMMatch<NSMEntity> getReactionMatch(IRandom rand) {
    NSMMatch<NSMEntity> match = org.jamesii.core.math.random.RandomSampler
        .sampleRouletteWheel(matchesAndRates, rand);
    return match;
  }

  /**
   * Calculate sum of reaction rates according to the reaction-diffusion master
   * equation as in the NSM, using the given set of reactions (e.g. because the
   * subvol's context has changed)
   * 
   * @param reactionRules
   *          Collection of reactions to evaluate
   * @return Sum of reaction rates
   */
  public Double getSumOfReactionRates(
      Collection<NSMReactionRule> reactionRules) {
    matchesAndRates.clear();
    // if (sv.getState().isEmpty()) { return 0.0; } // not a good idea if there
    // are zero-order rules
    double sumR = 0.0;
    for (NSMReactionRule rule : reactionRules) {
      Collection<NSMMatch<NSMEntity>> matches = PopulationMatchStrategy
          .match(rule, sv.getEnclosingEntity(), sv.getState(), sv.getVolume());
      for (NSMMatch<NSMEntity> match : matches) {
        double rate = match.getRate();
        matchesAndRates.put(match, rate);
        sumR += rate;
      }
    }
    return sumR;
  }

  /**
   * Calculate sum of diffusion rates as in the NSM, but corrected for
   * differences in shared area with neighbors and different neighbor sizes
   * 
   * @return Sum of diffusion rates
   */
  public double getSumOfDiffusionRates() {
    // originally: #sides*sum[j=1..#entities]*D_j/l�*X_j; here: l� replaced
    // by side length of this and distance of centers of this and neighbor;
    // calculated over all neighbors (because sides may differ in size)
    double sumDX = 0.0;
    diffusionRates.clear();
    // first, determine sum[j=1..#entities]*D_j/*X_j (uncorrected)
    for (Map.Entry<NSMEntity, Integer> e : sv.getState().entrySet()) {
      NSMEntity ent = e.getKey();
      Integer amount = e.getValue();
      double prodDjXj = ent.getDiffusionConstant() * amount;
      diffusionRates.put(ent, prodDjXj);
      sumDX += prodDjXj;
    }
    // multiply with sum of correction factors
    return sumDX * sv.getSumOfNeighCorrFac();
  }

  /**
   * Get random entity for diffusion (proportional to previously calculated
   * diffusion rates -- after a state update, getSumOfDiffusionRates() should be
   * called before this function)
   * 
   * @param rand
   *          random number generator
   * @return Entity for diffusion event according to diffusion rate vector
   */
  public NSMEntity getDiffusingEntity(IRandom rand) {
    return org.jamesii.core.math.random.RandomSampler
        .sampleRouletteWheel(diffusionRates, rand);
  }

  @Override
  public String toString() {
    return (type == NSMEventType.NSMDIFFUSION ? "diff from " : "react in ")
        + sv;
  }

}
