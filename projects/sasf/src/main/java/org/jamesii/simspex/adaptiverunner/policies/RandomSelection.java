/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;


import java.util.HashMap;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * This policy selects each arm with the same random probability. If an arm has
 * been quarantined, it will be ignored for the rest of the selection process.
 * 
 * @author Roland Ewald
 * 
 */
public class RandomSelection extends AbstractMinBanditPolicy {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1311479127256802114L;

  /** Random number generator to be used. */
  private IRandom random;

  @Override
  public void init(int numberOfArms, int horizonSize) {
    super.init(numberOfArms, horizonSize);
    random = SimSystem.getRNGGenerator().getNextRNG();
  }

  @Override
  public int nextChoice() {
    int choice = getRandomIndex();
    changed(choice);
    return choice;
  }

  /**
   * Selects a random arm.
   * 
   * @return index of the selected arm
   */
  protected int getRandomIndex() {
    int countOKArms = 0;
    Map<Integer, Integer> indexMap = new HashMap<>();
    for (int i = 0; i < getNumOfArms(); i++) {
      if (isQuarantined(i)) {
        continue;
      }
      indexMap.put(countOKArms, i);
      countOKArms++;
    }
    Integer okArmIndex = (int) (getRandom().nextDouble() * indexMap.size());
    return indexMap.get(okArmIndex);
  }

  public IRandom getRandom() {
    return random;
  }

}
