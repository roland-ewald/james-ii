package org.jamesii.simspex.exploration.ils.observation;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * This class is used by {@link org.jamesii.simspex.exploration.ils.algorithm.ParamILS}
 * implementations to pass information on their current performance to
 * observers.
 * 
 * @see ParamILSObserver
 * 
 * @author Robert Engelke
 */
public class ParamILSMessage {

  /** The performance estimation. */
  private final Double performanceEstimation;

  /** The referenced block. */
  private final ParameterBlock referencedBlock;

  /** The message type. */
  private final ParamILSMessageType messageType;

  /** The information. */
  private final String information;

  /** The compared value. */
  private final Double comparedValue;

  /**
   * The default constructor.
   * 
   * @param type
   *          the type
   * @param paramBlock
   *          the parameter block
   * @param performance
   *          the performance
   * @param compared
   *          the value the performance was compared to
   * @param information
   *          the information
   */
  public ParamILSMessage(ParamILSMessageType type, ParameterBlock paramBlock,
      Double performance, Double compared, String information) {
    this.messageType = type;
    this.referencedBlock = paramBlock;
    this.performanceEstimation = performance;
    this.comparedValue = compared;
    this.information = information;
  }

  /**
   * The Constructor.
   * 
   * @param type
   *          the type
   * @param paramBlock
   *          the param block
   * @param performance
   *          the performance
   */
  public ParamILSMessage(ParamILSMessageType type, ParameterBlock paramBlock,
      Double performance) {
    this(type, paramBlock, performance, 0.0, null);
  }

  /**
   * The Constructor.
   * 
   * @param type
   *          the type
   * @param information
   *          the information
   */
  public ParamILSMessage(ParamILSMessageType type, String information) {
    this(type, null, null, 0.0, information);
  }

  /**
   * The Constructor.
   * 
   * @param type
   *          the type
   * @param paramBlock
   *          the parameter block
   * @param performance
   *          the performance
   * @param compared
   *          the value the performance was compared to
   */
  public ParamILSMessage(ParamILSMessageType type, ParameterBlock paramBlock,
      Double performance, Double compared) {
    this(type, paramBlock, performance, compared, null);
  }

  /**
   * Gets the performance estimation.
   * 
   * @return the performance estimation
   */
  public Double getPerformanceEstimation() {
    return performanceEstimation;
  }

  /**
   * Gets the referenced block.
   * 
   * @return the referenced block
   */
  public ParameterBlock getReferencedBlock() {
    return referencedBlock;
  }

  /**
   * Gets the message type.
   * 
   * @return the message type
   */
  public ParamILSMessageType getMessageType() {
    return messageType;
  }

  /**
   * Gets the information.
   * 
   * @return the information
   */
  public String getInformation() {
    return information;
  }

  /**
   * Gets the compared value.
   * 
   * @return the compared value
   */
  public Double getComparedValue() {
    return comparedValue;
  }
}
