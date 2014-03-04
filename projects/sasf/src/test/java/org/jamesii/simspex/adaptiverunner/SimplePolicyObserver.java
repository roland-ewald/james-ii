/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;

/**
 * Simple observer for {@link IMinBanditPolicy} instances. Stores results into
 * file and keeps history for post-run analysis.
 * 
 * @author Roland Ewald
 */
public class SimplePolicyObserver implements IObserver<IMinBanditPolicy> {

  /** The number of the parameter setup. */
  int number = 0;

  /** The writers for the selection history. */
  Map<IMinBanditPolicy, FileWriter> selHistoryWriters =
      new HashMap<>();

  /** The writers for the received reward. */
  Map<IMinBanditPolicy, FileWriter> rewardWriters =
      new HashMap<>();

  /** The policy to be used. */
  IMinBanditPolicy policy;

  /** The selection history. */
  List<Integer> selectionHistory = new ArrayList<>();

  /** The reward history. */
  List<Pair<Integer, Double>> rewardHistory =
      new ArrayList<>();

  /**
   * This method is called when information about an SimplePolicy which was
   * previously requested using an asynchronous interface becomes available.
   */
  public SimplePolicyObserver() {
    // TODO add prefix to describe model parameters here!
  }

  @Override
  public void update(IMinBanditPolicy policy) {
    try {
      number++;
      String policyName = Strings.dispClassName(policy.getClass());
      selHistoryWriters.put(policy, new FileWriter(new File(policyName + number
          + ".sel.csv")));
      rewardWriters.put(policy, new FileWriter(new File(policyName + number
          + ".rew.dat")));
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, "Update failed.", ex);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public void update(IMinBanditPolicy policy, Object hint) {
    try {
      if (hint instanceof Pair) {
        storeReward(policy, (Pair<Integer, Double>) hint);
      } else {
        storeSelection(policy, (Integer) hint);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * This method is called when information about an SimplePolicy which was
   * previously requested using an asynchronous interface becomes available.
   * 
   * @param pol
   *          the pol
   * @param hint
   *          the hint
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void storeSelection(IMinBanditPolicy pol, int hint)
      throws IOException {
    FileWriter shw = selHistoryWriters.get(pol);
    shw.write(hint + "\n");
    shw.flush();
    selectionHistory.add(hint);
  }

  /**
   * This method is called when information about an SimplePolicy which was
   * previously requested using an asynchronous interface becomes available.
   * 
   * @param pol
   *          the pol
   * @param reward
   *          the reward
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void storeReward(IMinBanditPolicy pol, Pair<Integer, Double> reward)
      throws IOException {
    FileWriter rew = rewardWriters.get(pol);
    rew.write(reward.getFirstValue() + "\t" + reward.getSecondValue() + "\n");
    rew.flush();
    rewardHistory.add(reward);
  }

  /**
   * This method is called when information about an SimplePolicy which was
   * previously requested using an asynchronous interface becomes available.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void reset() throws IOException {

    for (FileWriter shw : selHistoryWriters.values()) {
      shw.close();
    }
    for (FileWriter rew : rewardWriters.values()) {
      rew.close();
    }

    selectionHistory.clear();
    rewardHistory.clear();
  }

  /**
   * This method is called when information about an SimplePolicy which was
   * previously requested using an asynchronous interface becomes available.
   * 
   * @return the selection history
   */
  public List<Integer> getSelectionHistory() {
    return selectionHistory;
  }

  /**
   * This method is called when information about an SimplePolicy which was
   * previously requested using an asynchronous interface becomes available.
   * 
   * @return the reward history
   */
  public List<Pair<Integer, Double>> getRewardHistory() {
    return rewardHistory;
  }

  /**
   * This method is called when information about an SimplePolicy which was
   * previously requested using an asynchronous interface becomes available.
   * 
   * @return the policy
   */
  public IMinBanditPolicy getPolicy() {
    return policy;
  }
}
