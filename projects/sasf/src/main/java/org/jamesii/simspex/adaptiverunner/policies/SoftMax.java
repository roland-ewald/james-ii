/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner.policies;

/**
 * First Version of SoftMax strategy. Each arm is chosen with a (calculated)
 * probability depending on the estimated mean of reward. See:
 * "Multi-Armed Bandit Algorithms and Empirical Evaluation" by Joann`es Vermorel
 * and Mehryar Mohri.
 * 
 * If no pull is finished, a random arm will be pulled. Not pulled arms get a
 * average probability (2*(finished pulls)/(sum of all rewards)).
 * 
 * Uses the RNG of RandomSelection.
 * 
 * @see RandomSelection
 * 
 * @author Rene Schulz
 * 
 */
public class SoftMax extends RandomSelection {

  /** Serialisation ID. */
  private static final long serialVersionUID = 931847101330340586L;

  /**
   * List containing the probabilities for each arm to be chosen.
   */
  protected double[] probs;

  /** Sum of all probabilities. */
  protected double sumProbs = 0.0;

  /**
   * Parameter for exploration called temperature. The higher the temperature
   * the more exploration takes place.
   */
  private double temperature = 0.1;

  @Override
  // Initialization of the random-generator and the array for the probabilities
  public void init(int numberOfArms, int horizonSize) {
    super.init(numberOfArms, horizonSize);
    probs = new double[numberOfArms];
  }

  @Override
  public int nextChoice() {
    int choice = 0;
    // If no pull finished yet, pull random arm.
    if (getOverallPullCount() == 0) {
      choice = getRandomIndex();
    } else {
      // Calculate the probability for each arm and the sum of all
      // probabilities.
      calcProbs();
      double probability = probs[0];
      // Fix random number for randomly selection of an arm.
      double k = getRandom().nextDouble();
      // Choose an arm with the calculated probability for each arm.
      while (k < (1 - probability / sumProbs)) {
        choice++;
        // Because of approximation errors its possible
        // to chose an arm greater than numOfArms.
        if (choice == getNumOfArms()) {
          choice = getNumOfArms() - 1;
          probability = sumProbs;
        } else {
          probability += probs[choice];
        }
      }
    }

    changed(choice);
    return choice;
  }

  /**
   * Calculates the probabilities for each arm and stores them in probs and
   * calculates sumProbs. Arms in quarantine get a probability of zero. Arms
   * which weren't pulled yet get a average probability 2*(number of finished
   * pulls/sum of all received rewards).
   */
  protected void calcProbs() {
    sumProbs = 0.0;
    double sumOfAllRewards = 0;
    // Calculate sum of all rewards received so far.
    for (int i = 0; i < getNumOfArms(); i++) {
      if (!isQuarantined(i)) {
        sumOfAllRewards += getRewardSum(i);
      }
    }
    // Calculated probability for each arm.
    for (int i = 0; i < getNumOfArms(); i++) {
      if (isQuarantined(i)) {
        probs[i] = 0.0;
      } else {
        if (getPullCount(i) == 0) {
          probs[i] =
              Math.exp((getOverallPullCount() / sumOfAllRewards)
                  / currentTemperature());
        } else {
          probs[i] =
              Math.exp((getPullCount(i) / getRewardSum(i))
                  / currentTemperature());
        }
      }
      // Add new probability to sumProbs.
      sumProbs += probs[i];
    }
    if (sumProbs == 0.0) {
      throw new IllegalStateException("Probabilities weren't calculated correctly "
          + "(sumProbs=0.0; all arms in quarantine?)!");
    }
  }

  /**
   * Returns the CURRENT parameter for exploration (called temperature).
   * 
   * @return Current parameter for exploration.
   */
  protected double currentTemperature() {
    return temperature;
  }

  /**
   * Returns the initial value for the exploration parameter called temperature.
   * 
   * @return The exploration parameter.
   */
  public Double getTemperature() {
    return temperature;
  }

  /**
   * Sets the exploration parameter called temperature.
   * 
   * @param temperature
   *          the exploration parameter.
   */
  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }

}
