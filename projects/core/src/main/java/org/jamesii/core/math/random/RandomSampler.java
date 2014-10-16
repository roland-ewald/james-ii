/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.core.base.Entity;
import org.jamesii.core.math.random.distributions.IDistribution;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.misc.Generics;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;

/**
 * Class that facilitates random sampling from a collection or array of input
 * values. Use one of the <tt>sample</tt> methods for getting a multiset and
 * {@code sampleSet()} for a set, that is, an {@link ArrayList} without
 * duplicates.
 * 
 * @author Jan Himmelspach
 * @author Johannes Rössel
 * @author Roland Ewald
 * @author Arne Bittig (roulette wheel sampling)
 */
public final class RandomSampler extends Entity {

  /** Serial version UID. */
  private static final long serialVersionUID = 1984213367539936361L;

  /**
   * Private default constructor to ensure that no-one instantiates this class.
   */
  private RandomSampler() {
  }

  /**
   * Return a sample multiset. The resulting multiset may contain entries more
   * than once.
   * 
   * @param count
   *          Number of entries to be sampled
   * @param low
   *          Lower bound (do not take entries < low)
   * @param up
   *          Upper bound (do not take entries > up)
   * @param data
   *          The data the sample shall be sampled from
   * @param generator
   *          A random number generator that is used for sampling.
   * 
   * @return A multiset of samples.
   */
  public static <E> List<E> sample(Integer count, Integer low, Integer up,
      Collection<E> data, IRandom generator) {
    checkBoundsAndCount(low, up, count);
    checkDataSizeBounds(low, up, data.size());

    E[] e = (E[]) Arrays.copyOfRange(data.toArray(), low, up + 1); // NOSONAR:
    // no ClassCastException for generic types with erasure Object

    List<E> result = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      result.add(e[generator.nextInt(e.length)]);
    }

    return result;
  }

  /**
   * Return a sample multiset. The resulting multiset may contain entries more
   * than once.
   * 
   * @param count
   *          number of elements to be drawn
   * @param data
   *          the data from which to sample
   * @param generator
   *          the RNG to be used
   * 
   * @return list of sampled entities
   */
  public static <E> List<E> sample(Integer count, Collection<E> data,
      IRandom generator) {
    return sample(count, 0, data.size() - 1, data, generator);
  }

  /**
   * Return a list of random numbers between within the bounds of {@code low}
   * and {@code up}. Numbers may appear more than once. All numbers <i>n</i>
   * will adhere to the condition <i>low</i> ≤ <i>n</i> ≤ <i>up</i>.
   * 
   * @param count
   *          The count of numbers to sample.
   * @param low
   *          Lower bound for sampling values.
   * @param up
   *          Upper bound for sampling values.
   * @param generator
   *          A random number generator that is used for sampling.
   * 
   * @return An {@link ArrayList} containing numbers from the sample range.
   */
  public static List<Long> sample(Integer count, Integer low, Integer up,
      IRandom generator) {
    checkBoundsAndCount(low, up, count);

    Integer range = up - low;

    List<Long> result = new ArrayList<>(count);

    if (range == 0) {
      for (int i = 0; i < count; i++) {
        result.add(Long.valueOf(low));
      }
    } else {
      for (int i = 0; i < count; i++) {
        result.add(Long.valueOf(generator.nextInt(range) + low));
      }
    }
    return result;
  }

  /**
   * Draws sample of unique integers in the interval [0,upper-1].
   * 
   * @param sampleSize
   *          the number of integers to be sampled
   * @param upperBound
   *          the upper limit of the interval
   * @param random
   *          the random number generator to be used
   * 
   * @return the int[]
   */
  public static int[] sampleUnique(int sampleSize, int upperBound,
      IRandom random) {

    if (sampleSize > upperBound) {
      throw new IllegalArgumentException("Cannot sample " + sampleSize
          + " unique integers from interval [0," + (upperBound - 1) + "].");
    }

    int[] sample = new int[sampleSize];

    List<Integer> indexList = new ArrayList<>(upperBound);
    for (int i = 0; i < upperBound; i++) {
      indexList.add(i);
    }

    for (int i = 0; i < sampleSize; i++) {
      sample[i] =
          indexList.remove((int) (random.nextDouble() * indexList.size()));
    }

    return sample;
  }

  /**
   * Randomly samples a number of entries from a collection. The result is a set
   * and thus cannot contain entries more than once.
   * 
   * @param count
   *          Number of entries to be sampled. If higher than the number of
   *          items in the originating collection, {@code count} will be capped
   *          there.
   * @param low
   *          Lower bound (do not take entries with index < low).
   * @param up
   *          Upper bound (do not take entries with index > up).
   * @param data
   *          The data the sample shall be sampled from.
   * @param generator
   *          The random number generator to use.
   * 
   * @return A set of samples.
   */
  public static <E> List<E> sampleSet(Integer count, Integer low, Integer up,
      Collection<E> data, IRandom generator) {
    checkBoundsAndCount(low, up, count);
    checkDataSizeBounds(low, up, data.size());

    E[] e = (E[]) Arrays.copyOfRange(data.toArray(), low, up + 1); // NOSONAR:
    // no ClassCastException for generic types with erasure Object

    // random number
    int r;
    // temporary storage space for swapping
    E t;
    for (int i = e.length - 1; i > 0; i--) {
      r = generator.nextInt(i + 1);
      // swap
      t = e[i];
      e[i] = e[r];
      e[r] = t;
    }

    return new ArrayList<>(Arrays.asList(e).subList(0,
        Math.min(count, e.length)));
  }

  /**
   * Throw {@link IllegalArgumentException} if lower bound is larger than upper
   * bound or if count is negative
   * 
   * @param low
   * @param up
   * @param count
   */
  private static void checkBoundsAndCount(Integer low, Integer up, Integer count) {
    if (low > up) {
      throw new IllegalArgumentException(
          "Lower bound must be smaller than upper bound.");
    }
    if (count < 0) {
      throw new IllegalArgumentException("Count must be non-negative.");
    }
  }

  /**
   * Throw {@link IllegalArgumentException} if bounds are not consistent with
   * size of data
   * 
   * @param low
   * @param up
   * @param dataSize
   */
  private static <E> void checkDataSizeBounds(Integer low, Integer up,
      int dataSize) {
    if (low > dataSize || up > dataSize || low < 0 || up < 0) {
      throw new IllegalArgumentException(
          "Bounds must not exceed the size of the dataset");
    }
  }

  /**
   * Randomly samples a number of entries from a collection.
   * 
   * @param count
   *          the number of entities to be sampled
   * @param data
   *          the data from which to be sampled
   * @param generator
   *          the RNG
   * 
   * @return sampled entities
   */
  public static <E> List<E> sampleSet(Integer count, Collection<E> data,
      IRandom generator) {
    return sampleSet(count, 0, data.size() - 1, data, generator);
  }

  /**
   * Return a list of long values between low and up. Values only appear once!
   * 
   * @param count
   *          Number of entries to be sampled. If larger than the range between
   *          low and up, {@code count} will be capped there.
   * @param low
   *          Lower bound (do not take entries < low).
   * @param up
   *          Upper bound (do not take entries > up).
   * @param generator
   *          The random number generator to use.
   * 
   * @return A set of samples.
   */
  public static List<Long> sampleSet(Integer count, Integer low, Integer up,
      IRandom generator) {
    List<Long> data = new ArrayList<>(up - low + 1);
    for (long i = low; i <= up; i++) {
      data.add(i);
    }
    return sampleSet(count, 0, data.size() - 1, data, generator);
  }

  /**
   * Sample point cloud of {@link Double} arrays. The points are distributed
   * uniformly.
   * 
   * @param generator
   *          the generator
   * @param desiredAmount
   *          the desired amount
   * @param bounds
   *          the bounds
   * 
   * @return the list of unique points, uniformly distributed
   */
  public static List<Double[]> samplePointCloud(IRandom generator,
      int desiredAmount, List<Pair<Double, Double>> bounds) {

    Set<ComparableNumberPoint> uniquePoints = new HashSet<>();
    List<ComparableNumberPoint> uniquePointList = new ArrayList<>();
    UniformDistribution[] distributions =
        createDistributions(generator, bounds);

    while (uniquePoints.size() < desiredAmount) {
      ComparableNumberPoint comparablePoint = sampleDoublePoint(distributions);
      if (uniquePoints.contains(comparablePoint)) {
        continue;
      }
      uniquePoints.add(comparablePoint);
      uniquePointList.add(comparablePoint);
    }

    return createPointList(uniquePointList, new Double[0]);
  }

  /**
   * Sample point cloud of {@link Integer} arrays.
   * 
   * @param generator
   *          the generator
   * @param desiredAmount
   *          the desired amount
   * @param bounds
   *          the bounds
   * @throws IllegalArgumentException
   *           if boundaries define a subspace that contain too few elements
   *           (less than twice the desired amount, as this method takes a long
   *           time otherwise and you would be better of sampling the points you
   *           *don't* want)
   * @return the list of points, uniformly distributed
   */
  public static List<Integer[]> sampleIntegerPointCloud(IRandom generator,
      int desiredAmount, List<Pair<Integer, Integer>> bounds) {
    checkSubspaceSize(desiredAmount, bounds);

    Set<ComparableNumberPoint> uniquePoints = new HashSet<>();
    List<ComparableNumberPoint> uniquePointList = new ArrayList<>();
    UniformDistribution[] distributions =
        createDistributions(generator, bounds);

    while (uniquePoints.size() < desiredAmount) {
      ComparableNumberPoint comparablePoint = sampleIntegerPoint(distributions);
      if (uniquePoints.contains(comparablePoint)) {
        continue;
      }
      uniquePoints.add(comparablePoint);
      uniquePointList.add(comparablePoint);
    }

    return createPointList(uniquePointList, new Integer[0]);
  }

  /**
   * Checks subspace size and compares with the desired amount of unique points.
   * 
   * @param desiredAmount
   *          the desired amount
   * @param bounds
   *          the bounds
   * 
   * 
   * @throws IllegalArgumentException
   *           if boundaries define a subspace that contain too few elements if
   *           boundaries define a subspace that contain too few elements (less
   *           than twice the desired amount)
   */
  private static void checkSubspaceSize(int desiredAmount,
      List<Pair<Integer, Integer>> bounds) {
    int numCombinations = 1;
    for (Pair<Integer, Integer> bound : bounds) {
      numCombinations *= (bound.getSecondValue() - bound.getFirstValue());
    }
    if (desiredAmount > 0.5 * numCombinations) {
      throw new IllegalArgumentException(
          "Sampling of integer point with bounds '"
              + Strings.dispIterable(bounds) + "' can never yield "
              + desiredAmount
              + " unique points. The number needs to be reduced to "
              + numCombinations + ".");
    }
  }

  /**
   * Samples a point of type {@link Double} from an array of distributions.
   * 
   * @param distributions
   *          the distributions
   * 
   * @return the comparable point
   */
  private static ComparableNumberPoint sampleDoublePoint(
      IDistribution[] distributions) {
    Double[] sampledPoint = new Double[distributions.length];
    for (int i = 0; i < distributions.length; i++) {
      sampledPoint[i] = distributions[i].getRandomNumber();
    }
    return new ComparableNumberPoint(sampledPoint);
  }

  /**
   * Samples a point of type {@link Integer} from an array of distributions.
   * 
   * @param distributions
   *          the distributions
   * 
   * @return the comparable point
   */
  private static ComparableNumberPoint sampleIntegerPoint(
      IDistribution[] distributions) {
    Integer[] sampledPoint = new Integer[distributions.length];
    for (int i = 0; i < distributions.length; i++) {
      sampledPoint[i] = (int) distributions[i].getRandomNumber();
    }
    return new ComparableNumberPoint(sampledPoint);
  }

  /**
   * Creates the uniform distributions according to bounds.
   * 
   * @param generator
   *          the generator
   * @param bounds
   *          the bounds
   * 
   * @return the uniform distribution[]
   */
  private static <X extends Number> UniformDistribution[] createDistributions(
      IRandom generator, List<Pair<X, X>> bounds) {
    UniformDistribution[] distributions =
        new UniformDistribution[bounds.size()];
    for (int i = 0; i < bounds.size(); i++) {
      double v1 = bounds.get(i).getFirstValue().doubleValue();
      double v2 = bounds.get(i).getSecondValue().doubleValue();
      double lowBorder = Math.min(v1, v2);
      double upBorder = Math.max(v1, v2);
      distributions[i] =
          new UniformDistribution(generator, lowBorder, upBorder);
    }
    return distributions;
  }

  /**
   * Creates the point set.
   * 
   * @param points
   *          the points
   * 
   * @return the point set
   */
  private static <X extends Number> List<X[]> createPointList(
      List<ComparableNumberPoint> points, X[] typeArray) {
    List<X[]> pointSet = new ArrayList<>();
    for (ComparableNumberPoint p : points) {
      pointSet.add(Generics.autoCastArray(p.getPoint(), typeArray));
    }
    return pointSet;
  }

  /**
   * Roulette wheel selection, a.k.a. fitness proportionate selection on arrays
   * 
   * - iterates twice over the given array, once to sum up the weight values and
   * then again to determine when the cumulative sum of weights exceeds the
   * threshold ( = U(0,1) * weightsum).
   * 
   * @param weights
   *          double array of weights
   * @param generator
   *          Random number generator
   * @return Index to the array selected in proportion to the associated values
   */
  public static int sampleRouletteWheel(double[] weights, IRandom generator) {
    double thresh = 0.0;
    for (double v : weights) {
      thresh += v;
    }
    thresh *= generator.nextDouble();
    double cumSum = 0.0;
    int index = 0;
    for (double v : weights) {
      cumSum += v;
      if (cumSum >= thresh) {
        return index;
      }
      index++;
    }
    return index;
  }

  /**
   * Roulette wheel selection, a.k.a. fitness proportionate selection on maps
   * 
   * - iterates twice over the given map, once to sum up the weight values and
   * then again to determine when the cumulative sum of weights exceeds the
   * threshold ( = U(0,1) * weightsum).
   * 
   * @param weights
   *          Map of (item,weight) pairs (weight: usually double values)
   * @param generator
   *          Random number generator
   * @return Key from the map selected in proportion to the associated values
   *         (Note that if the order of items in the map can change from one run
   *         to the next, the return value may be different even if the random
   *         number is the same.)
   */
  public static <E, N extends Number> E sampleRouletteWheel(Map<E, N> weights,
      IRandom generator) {
    Double thresh = 0.0;
    for (N v : weights.values()) {
      thresh += v.doubleValue();
    }
    thresh *= generator.nextDouble();
    Double cumSum = 0.0;
    for (Map.Entry<E, N> e : weights.entrySet()) {
      cumSum += e.getValue().doubleValue();
      if (cumSum >= thresh) {
        return e.getKey();
      }
    }
    // if weight vector contains negative values or is empty
    throw new IllegalArgumentException("Illegal weight vector: " + weights);
  }

  private static class EntryValueInverseComparator<T extends Comparable<T>>
      implements Comparator<Map.Entry<?, T>> {

    @Override
    public int compare(Entry<?, T> e1, Entry<?, T> e2) {
      return e2.getValue().compareTo(e1.getValue()); // note inverse order
    }

  }

  /**
   * Permutation of the {@link Map#keySet() key set} of a {@link Map}, where map
   * values are (numerical) weights and keys associated with higher values are
   * more likely to be selected earlier.
   * 
   * @param weights
   *          Map keys->weights (not modified)
   * @param generator
   *          Random number generator
   * @return List with all keys from the map in random order
   */
  public static <E, N extends Number & Comparable<N>> List<E> permuteRouletteWheel(
      Map<E, N> weights, IRandom generator) {
    List<Map.Entry<E, N>> weightList = new ArrayList<>(weights.entrySet());
    Collections.sort(weightList, new EntryValueInverseComparator<N>());
    double weightSum = 0;
    int numEl = weightList.size();
    for (int i = numEl - 1; i >= 0; i--) {
      weightSum += weightList.get(i).getValue().doubleValue();
    }
    List<E> retVal = new ArrayList<>(numEl);
    for (int iRV = 0; iRV < numEl; iRV++) {
      double thr = weightSum * generator.nextDouble();
      int iWL = 0;
      double cumSum = 0;
      double lastVal = 0;
      while (thr > cumSum) {
        lastVal = weightList.get(iWL++).getValue().doubleValue();
        cumSum += lastVal;
      }
      retVal.add(weightList.remove(iWL - 1).getKey());
      weightSum -= lastVal;
    }
    return retVal;
  }
}

/**
 * Multi-dimensional point defined by {@link Double} elements that is
 * comparable.
 * 
 * @author Roland Ewald
 */
class ComparableNumberPoint {

  /** The point. */
  private Number[] point;

  /**
   * Instantiates a new comparable point.
   * 
   * @param point
   *          the point
   */
  ComparableNumberPoint(Number[] point) {
    this.setPoint(point);
  }

  @Override
  public boolean equals(Object obj) {

    if (!(obj instanceof ComparableNumberPoint)) {
      return false;
    }

    ComparableNumberPoint comp = (ComparableNumberPoint) obj;
    if (getPoint().length != comp.getPoint().length
        || !getPoint().getClass().equals(comp.getPoint().getClass())) {
      return false;
    }

    for (int i = 0; i < getPoint().length; i++) {
      if (Double.compare(getPoint()[i].doubleValue(),
          comp.getPoint()[i].doubleValue()) != 0) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int code = 0;
    for (int i = 0; i < getPoint().length; i++) {
      code += getPoint()[i].intValue() * Math.pow(10, i);
    }
    return code;
  }

  /**
   * @return the point
   */
  public final Number[] getPoint() {
    return point;
  }

  /**
   * @param point
   *          the point to set
   */
  public final void setPoint(Number[] point) {
    this.point = point;
  }
}
