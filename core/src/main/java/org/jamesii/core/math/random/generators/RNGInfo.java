package org.jamesii.core.math.random.generators;

import java.io.Serializable;

/**
 * A data structure that contains meta-information about random number
 * generators. This data can be used to judge generators for applicability to
 * certain problems or get a continuous stream of random bits exactly the way
 * the generator yields them.
 * 
 * @author Johannes Rössel
 * 
 */
public class RNGInfo implements Serializable {

  /** The serialisation ID. */
  private static final long serialVersionUID = 1403489944268664650L;

  /**
   * Simple enumeration that allows for specifying an end of the bit range.
   * Values are either {@code UPPER} or {@code LOWER}.
   */
  public enum UsableBits {
    /** Specifies the upper end of the bit range. */
    UPPER,
    /** Specifies the lower end of the bit range. */
    LOWER
  }

  /** The period. */
  private RNGPeriod period;

  /**
   * The number of bits the generator produces in total. This is <em>not</em>
   * the number of bits that may be used for generating random numbers.
   */
  private final int numberOfBits;

  /** The number of bits that may be used for random numbers. */
  private final int usableBits;

  /**
   * The end of the bit range from which the number of usable bits should be
   * taken. Some generators yield non-random low bits (i. e. LCGs), others yield
   * not-so-random high bits (i. e. MWCs), so this specifies which bits are best
   * to use for generating numbers.
   */
  private final UsableBits usableBitsEnd;

  /**
   * The number of bits that may be used to build a combined generator with
   * another RNG. This may be different from the number of bits for producing
   * single random numbers.
   */
  private final int usableBitsForCombinedGenerators;

  /**
   * The end of the bit range from which the number of bits for combined
   * generators may be taken.
   */
  private final UsableBits usableBitsForCombinedGeneratorsEnd;

  /**
   * The “family” of the RNG. This is a simple string, preferably an acronym. It
   * may be used as a safeguard against combining two generators from the same
   * family.
   */
  private String rngFamily;

  /**
   * The name of the RNG, will probably not be used for anything but having all
   * available information in one place is a good thing. Plus, it enhances
   * human-readability of the data in this class.
   */
  private String name;

  private final boolean sampleFromUpperRange;

  /**
   * Returns the name of the RNG.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Creates a new {@code RNGInfo} object from the given parameters.
   * 
   * @param name
   *          The name of the RNG, only for human consumption.
   * @param rngFamily
   *          The family of the RNG, partially for human consumption but may be
   *          used in future to run sanity checks on combined generators. It is
   *          advised to use common abbreviations like LCG here and group
   *          similar RNGs together by family (i. e. the Recursion with Carry
   *          generator was flagged with a family of MWC, since RWC is a variant
   *          of the Multiply with Carry generator.
   * @param period
   *          Period of the RNG. Can be null if the period is unknown.
   * @param numberOfBits
   *          The number of bits the generator produces. This is specifically
   *          the number of produced bits in each iteration, <em>not</em> the
   *          number of bits that will be used to generate pseudo-random
   *          numbers. This value may be used to test the entire bit stream that
   *          the generator produces.
   * @param usableBits
   *          The number of bits that may be used to construct pseudo-random
   *          numbers. May be −1 to indicate that the value is unknown.
   * @param usableBitsEnd
   *          The end of the bit range from where the bits for constructing
   *          numbers may be taken. This can be either the upper or the lower
   *          end. For example, LCGs usually have less random lower bits so they
   *          should be taken from the upper end.
   * @param usableBitsForCombinedGenerators
   *          The usable bits for constructing combined generators. There are
   *          generators that have flaws in the upper or lower bits but they
   *          might still be usable when combining them with another generator.
   *          May be −1 to indicate an unknown value, although the number of
   *          bits that are used to construct pseudo-random numbers should be a
   *          safe bet in any case.
   * @param usableBitsForCombinedGeneratorsEnd
   *          The end of the bit range from where the bits for combined
   *          generators may be taken.
   */
  public RNGInfo(String name, String rngFamily, RNGPeriod period,
      int numberOfBits, int usableBits, UsableBits usableBitsEnd,
      int usableBitsForCombinedGenerators,
      UsableBits usableBitsForCombinedGeneratorsEnd) {
    super();
    this.period = period;
    this.numberOfBits = numberOfBits;
    this.usableBits = usableBits;
    this.usableBitsEnd = usableBitsEnd;
    this.usableBitsForCombinedGenerators = usableBitsForCombinedGenerators;
    this.usableBitsForCombinedGeneratorsEnd =
        usableBitsForCombinedGeneratorsEnd;
    this.rngFamily = rngFamily;
    this.name = name;
    sampleFromUpperRange = (getUsableBitsEnd() == UsableBits.UPPER);
  }

  /**
   * Returns the Period of the RNG.
   * 
   * @return the period
   */
  public RNGPeriod getPeriod() {
    return period;
  }

  /**
   * Returns the number of generated bits of the RNG.
   * 
   * @return the number of bits
   */
  public final int getNumberOfBits() {
    return numberOfBits;
  }

  /**
   * Returns the number of bits marked as safe for use in pseudo-random numbers
   * of the RNG.
   * 
   * @return the usable bits
   */
  public final int getUsableBits() {
    return usableBits;
  }

  /**
   * Returns the end of the bit range (upper or lower) from which the bits for
   * use in numbers should be sampled.
   * 
   * @return the usable bits end
   */
  public final UsableBits getUsableBitsEnd() {
    return usableBitsEnd;
  }

  /**
   * Returns the number of usable bits marked as safe for combined generators.
   * 
   * @return the usable bits for combined generators
   */
  public int getUsableBitsForCombinedGenerators() {
    return usableBitsForCombinedGenerators;
  }

  /**
   * Returns the end of the bit range (upper or lower) from which the bits for
   * combined generators should be sampled.
   * 
   * @return the usable bits for combined generators end
   */
  public UsableBits getUsableBitsForCombinedGeneratorsEnd() {
    return usableBitsForCombinedGeneratorsEnd;
  }

  /**
   * Returns the family of the RNG.
   * 
   * @return the rng family
   */
  public String getRngFamily() {
    return rngFamily;
  }

  /**
   * Returns true if the upper bits have to be used for sampling.
   * 
   * @return
   */
  public final boolean sampleFromUpperRange() {
    return sampleFromUpperRange;
  }

  @Override
  public String toString() {
    String s = "";
    s += "Name:                        " + name + "\n";

    s += "Family:                      " + rngFamily + "\n";

    s += "Period:                      ";
    if (period == null) {
      s += "(unknown)\n";
    } else {
      s += period + "\n";
    }

    s += "Generated bits:              " + numberOfBits + "\n";

    s += "Usable bits for numbers:     ";
    if (usableBits == -1) {
      s += "(unknown)\n";
    } else if (usableBits == 0) {
      s += "none\n";
    } else {
      if (usableBits == numberOfBits) {
        s += "all ";
      } else if (usableBitsEnd == UsableBits.UPPER) {
        s += "upper ";
      } else {
        s += "lower ";
      }
      s += usableBits + " bits" + "\n";
    }

    s += "Usable bits for combination: ";
    if (usableBitsForCombinedGenerators == -1) {
      s += "(unknown)\n";
    } else if (usableBitsForCombinedGenerators == 0) {
      s += "none\n";
    } else {
      if (usableBitsForCombinedGenerators == numberOfBits) {
        s += "all ";
      } else if (usableBitsForCombinedGeneratorsEnd == UsableBits.UPPER) {
        s += "upper ";
      } else {
        s += "lower ";
      }
      s += usableBitsForCombinedGenerators + " bits" + "\n";
    }

    return s;
  }
}