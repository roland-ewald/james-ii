/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.lcg;

import org.jamesii.core.math.random.generators.AbstractRandom;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.RNGInfo;
import org.jamesii.core.math.random.generators.RNGInfo.UsableBits;

/**
 * A general Linear Congruential Generator, adaptable for arbitrary parameters.
 * By default this class behaves the same as the Java {@link java.util.Random}
 * class. The formula used to compute the next pseudo-random number of an LCG is
 * as follows:
 * <p>
 * <i>x</i><sub><i>n</i> + 1</sub> = <i>a</i> ⋅ <i>x</i><sub><i>n</i></sub> + <i
 * >b</i>   (mod <i>c</i>)
 * <p>
 * For the default Java RNG the parameters <i>a</i>, <i>b</i> and <i>c</i> are
 * as follows:
 * <p>
 * <i>a</i> = 0x5DEECE66D, <i>b</i> = 11, <i>c</i> = 2<sup>48</sup>
 * <p>
 * The values can be changes by using the
 * {@link LCG#LCG(long, long, long, long)} constructor but extreme care is
 * advised as LCGs exhibit very non-random behavior with ill-chosen parameters.
 * <p>
 * This class is not recommended for use in simulation for the poor quality of
 * LCGs, other algorithms are better suited and should be used instead. It is
 * mainly intended for experimentation with older results and tests with known
 * bad generators.
 * 
 * @author Jan Himmelspach
 * @author Johannes Rössel
 */
public class LCG extends AbstractRandom implements IRandom {

  /** Constant RNG information field. */
  private static final RNGInfo RNGINFO = new RNGInfo("General LCG", "LCG",
      null, 48, 32, UsableBits.UPPER, 32, UsableBits.UPPER);

  /** Serial version UID. */
  private static final long serialVersionUID = 7097564786909653250L;

  /**
   * The default multiplier used if no special multiplier is given.
   */
  public static final long DEFAULTMULTIPLIER = 0x5DEECE66DL;

  /**
   * The default addend.
   */
  public static final long DEFAULTADDEND = 0xBL;

  /**
   * The default mask.
   */
  public static final long DEFAULTMASK = (1L << 48) - 1;

  /**
   * The multiplier <i>a.</i> If this is ill-chosen (i. e. 2) then the period of
   * the generator suffers greatly as well as the randomness of the generated
   * values.
   */
  private long multiplier = DEFAULTMULTIPLIER;

  /** The addend <i>b.</i> */
  private long addend = DEFAULTADDEND;

  /** The modulus <i>c.</i> */
  private long mask = DEFAULTMASK;

  /**
   * The generator's state, doubling as the most recently returned value of the
   * sequence.
   */
  private long x;

  /**
   * Initializes a new instance of this class.
   * <p>
   * <strong>Note:</strong> This does not set a useful seed and thus the seed
   * should <em>always</em> be set manually afterwards when using this
   * constructor.
   */
  public LCG() {
    this(0L);
  }

  /**
   * Initializes a new instance of this class, using the specified seed.
   * 
   * @param seed
   *          The seed for the generator.
   */
  public LCG(long seed) {
    super(seed);
    init(seed);
  }

  /**
   * Initializes a new instance of this class, using the specified seed,
   * multiplier, addend and bit mask.
   * <p>
   * The formula used to calculate subsequent values of the sequence is<br>
   * <i>x</i><sub><i>n</i> + 1</sub> = (<i>a</i><i>x<sub>n</sub></i> + <i>b</i>)
   * & <i>c</i><br>
   * where & denotes the bitwise <em>and</em> operation.
   * 
   * @param seed
   *          The seed for the generator.
   * @param multiplier
   *          The multiplier <i>a.</i> If this is ill-chosen (e. g. 2) then the
   *          period of the generator suffers greatly as well as the randomness
   *          of the generated values.
   * @param addend
   *          The addend <i>b.</i>
   * @param mask
   *          The bit mask <i>c.</i>
   */
  public LCG(long seed, long multiplier, long addend, long mask) {
    super(seed);
    this.multiplier = multiplier;
    this.addend = addend;
    this.mask = mask;
    setSeed(seed);
    init(seed);
  }

  @Override
  protected void init(long seed) {
    x = (seed ^ multiplier) & mask;
  }

  @Override
  public long next() {
    x = (x * multiplier + addend) & mask;
    return x;
  }

  @Override
  public RNGInfo getInfo() {
    return RNGINFO;
  }

}
