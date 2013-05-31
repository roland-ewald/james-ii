package org.jamesii.core.util.misc;

import org.jamesii.core.distributed.allocation.ConstantResourceAllocatorFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * Class that contains some auxiliary, static methods for parameter handling.
 * 
 * @author Roland Ewald
 */
public final class ParameterUtils {

  /**
   * Name of the parameter sub-block in which the simulation start time resides.
   */
  public static final String SIM_START_TIME = "simStartTime";

  /** Name of the parameter sub-block in which the simulation end time resides. */
  public static final String SIM_STOP_TIME = "simStopTime";

  /**
   * Copy messages from a port to a target port at once and not one by one. By
   * default false.
   */
  public static final String COPY_AT_ONCE = "copyAtOnce";

  /** Flag to activate interactive execution. Default is false. */
  public static final String INTERACTIVE = "interactive";

  /**
   * If true a file containing the time which was needed for running the
   * simulation will be stored on the disk. Default is false.
   */
  public static final String LOG_TIME = "logTime";

  /** If true, per thread monitoring is switched on. Default is false. */
  public static final String MONITORING_ENABLED = "monitoringEnabled";

  /** Activates resilience support. Default is false. */
  public static final String RESILIENT = "resilient";

  /**
   * If silent is true nothing will be printed (as long as verbose or the
   * monitoring are false). Default is false.
   */
  public static final String SILENT = "silent";

  /**
   * Name of the master server (to be looked up). Default is empty
   * {@link String}.
   */
  public static final String MASTER_SERVER_NAME = "masterServerName";

  /**
   * Name of the parameter block that defines the resource allocation factory
   * and its parameters. Default is {@link ParameterBlock} setting up the
   * {@link ConstantResourceAllocatorFactory}.
   */
  public static final String SIM_RESOURCE_ALLOCATION = "resourceAllocation";

  /** Set to true if the simulation shall be started in paused mode. */
  public static final String START_PAUSED = "startPaused";

  /**
   * Set to any integer value for waiting value milliseconds before the next
   * step will be computed.
   */
  public static final String INTER_STEP_DELAY = "interStepDelay";

  /**
   * The separator to distinguish sub-block names for distinct factories
   * configurations that have the same base factory. For example, the second
   * event queue for a processor relying on two event queues can be found at
   * [FQCN BaseFactory] + CARDINALITY_SEPARATOR + "2".
   */
  public static final String CARDINALITY_SEPARATOR = "$";

  /**
   * Hidden constructor.
   */
  private ParameterUtils() {
  }

  /**
   * Gets the default exec param block.
   * 
   * @return the default exec param block
   */
  public static ParameterBlock getDefaultExecParamBlock() {
    ParameterBlock execParams = new ParameterBlock();
    execParams.addSubBlock(COPY_AT_ONCE, false);
    execParams.addSubBlock(INTERACTIVE, false);
    execParams.addSubBlock(LOG_TIME, false);
    execParams.addSubBlock(MONITORING_ENABLED, false);
    execParams.addSubBlock(RESILIENT, false);
    execParams.addSubBlock(SILENT, false);
    execParams.addSubBlock(MASTER_SERVER_NAME, "");
    execParams.addSubBlock(SIM_START_TIME, 0.0);
    execParams.addSubBlock(SIM_STOP_TIME, Double.POSITIVE_INFINITY);
    execParams.addSubBlock(INTER_STEP_DELAY, 0L);
    execParams.addSubBl(START_PAUSED, false);
    execParams.addSubBl(SIM_RESOURCE_ALLOCATION, new ParameterBlock(
        ConstantResourceAllocatorFactory.class.getCanonicalName()));

    return execParams;
  }

  /**
   * Retrieves parameter sub-block for a given {@link AbstractFactory}.
   * 
   * @param block
   *          the parameter block
   * @param baseFactoryClass
   *          the class of the base factory
   * @param number
   *          the number
   * @param defaultFactoryClass
   *          the default factory (may be null)
   * 
   * @return the found parameter block, or an empty one
   * 
   *         number of the parameters to be retrieved (in case more entities as
   *         managed by the abstract factory are stored on one level of the
   *         parameter block)
   */
  public static ParameterBlock getFactorySubBlock(ParameterBlock block,
      Class<? extends Factory> baseFactoryClass, int number,
      Class<? extends Factory> defaultFactoryClass) {
    String blockName =
        number > 1 ? baseFactoryClass.getName() + CARDINALITY_SEPARATOR
            + number : baseFactoryClass.getName();
    String defaultValue =
        defaultFactoryClass != null ? defaultFactoryClass.getName() : null;
    return ParameterBlocks.getSBOrDefault(block, blockName, defaultValue);
  }

  /**
   * Retrieves first parameter sub-block for a given {@link Factory}.
   * 
   * @param block
   *          the parameter block
   * @param baseFacClass
   *          the class of the base factory
   * 
   * @return the found parameter block, or an empty one
   */
  public static ParameterBlock getFactorySubBlock(ParameterBlock block,
      Class<? extends Factory> baseFacClass) {
    return getFactorySubBlock(block, baseFacClass, 1, null);
  }

  /**
   * Gets the factory sub block.
   * 
   * @param block
   *          the block from which the sub-block shall be read
   * @param baseFactoryClass
   *          the base factory class
   * @param defaultFactory
   *          the default factory
   * 
   * @return the factory sub block
   */
  public static <F extends Factory> ParameterBlock getFactorySubBlock(
      ParameterBlock block, Class<F> baseFactoryClass,
      Class<? extends F> defaultFactory) {
    return getFactorySubBlock(block, baseFactoryClass, 1, defaultFactory);
  }
}
