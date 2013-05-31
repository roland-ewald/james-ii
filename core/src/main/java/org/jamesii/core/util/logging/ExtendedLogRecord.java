/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class ExtendedLogRecord extends LogRecord {

  /**
   * The constant serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The parameter block to be recorded.
   */
  private ParameterBlock block;

  /**
   * The object identifier.
   */
  private Object identifier;

  /**
   * The type of the class.
   */
  private Class<?> type;

  public ExtendedLogRecord(Level level, String msg) {
    super(level, msg);
  }

  /**
   * 
   * @param level
   * @param identifier
   * @param type
   * @param block
   * @param msg
   */
  public ExtendedLogRecord(Level level, Object identifier, Class<?> type,
      ParameterBlock block, String msg) {
    super(level, msg);
    this.block = block;
    this.identifier = identifier;
    this.type = type;
  }

  /**
   * @return the block
   */
  public final ParameterBlock getBlock() {
    return block;
  }

  /**
   * @param block
   *          the block to set
   */
  public final void setBlock(ParameterBlock block) {
    this.block = block;
  }

  /**
   * @return the identifier
   */
  public final Object getIdentifier() {
    return identifier;
  }

  /**
   * @param identifier
   *          the identifier to set
   */
  public final void setIdentifier(Object identifier) {
    this.identifier = identifier;
  }

  /**
   * @return the type
   */
  public final Class<?> getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public final void setType(Class<?> type) {
    this.type = type;
  }

}
