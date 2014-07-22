/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.reproducible;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandom;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * 
 * Helper class that can manage a hierarchy of seeds and the generation of
 * random number generators in a reproducible way.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class RandomNumberGeneratorManager {
  public static final String RNG_SEED_GROUP_ID = "rng_seed_group_id";

  private static final String NAME_CAN_T_BE_NULL = "name can't be null!";

  private static final String NO_GROUP_FOUND_FOR = "No group found for ";

  private final AtomicLong counter = new AtomicLong();

  private final Map<Long, IRandom> groups = new WeakHashMap<>();

  private final Map<Long, Seed> seeds = new WeakHashMap<>();

  private final static RandomNumberGeneratorManager instance =
      new RandomNumberGeneratorManager();

  private ISeedPolicy policy = new SimpleSeedPolicy();

  /**
   * Hidden Constructor
   */
  private RandomNumberGeneratorManager() {
  }

  public static synchronized long createSeedGroup(Seed seed) {
    IRandom rng = new JavaRandom(Seed.asLong(seed));
    long id = instance.counter.incrementAndGet();
    instance.groups.put(id, rng);
    instance.seeds.put(id, seed);
    return id;
  }

  public static synchronized long createGroup(long inGroup, String name) {
    if (name == null) {
      throw new IllegalArgumentException(NAME_CAN_T_BE_NULL);
    }

    Seed seed = instance.seeds.get(inGroup);
    if (seed == null) {
      throw new IllegalArgumentException(NO_GROUP_FOUND_FOR + inGroup);
    }

    return createSeedGroup(instance.policy.createSeed(seed, name));
  }

  public static synchronized IRandom getRng(long ofGroup,
      RandomGeneratorFactory usingFactory) {
    long seed = getNextSeed(ofGroup);
    IRandom rng = usingFactory.create(seed);
    return rng;
  }

  public static synchronized long getNextSeed(long ofGroup) {
    IRandom seedGen = instance.groups.get(ofGroup);
    if (seedGen == null) {
      throw new IllegalArgumentException(NO_GROUP_FOUND_FOR + ofGroup);
    }

    long seed = seedGen.nextLong();
    return seed;
  }

  public static synchronized void deleteGroup(long group) {
    instance.groups.remove(group);
    instance.seeds.remove(group);
  }

  public static ParameterBlock addGroupId(ParameterBlock to, long id) {
    if (instance.seeds.get(id) == null) {
      throw new IllegalArgumentException(NO_GROUP_FOUND_FOR + id);
    }

    to.addSubBl(RNG_SEED_GROUP_ID, id);
    return to;
  }

  public static ParameterBlock removeGroupId(ParameterBlock from) {
    from.removeSubBlock(RNG_SEED_GROUP_ID);
    return from;
  }

}
