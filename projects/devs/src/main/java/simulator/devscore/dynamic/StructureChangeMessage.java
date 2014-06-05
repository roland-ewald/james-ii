/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devscore.dynamic;


import java.util.List;
import java.util.Map;

import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.messages.Message;

import model.devscore.IBasicDEVSModel;
import model.devscore.dynamic.ChangeRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class StructureChangeMessage.
 * 
 * @author Jan Himmelspach
 */
public class StructureChangeMessage extends Message<IProcessor> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -6698201633681061443L;

  /** The change requests. */
  private Map<IBasicDEVSModel, List<ChangeRequest<?>>> changeRequests;

  /**
   * The Constructor.
   * 
   * @param sender
   *          the sender
   * @param changeRequests
   *          the change requests
   */
  public StructureChangeMessage(IProcessor sender,
      Map<IBasicDEVSModel, List<ChangeRequest<?>>> changeRequests) {
    super(sender);
    this.changeRequests = changeRequests;
  }

  /**
   * Gets the change requests.
   * 
   * @return the change requests
   */
  public Map<IBasicDEVSModel, List<ChangeRequest<?>>> getChangeRequests() {
    return changeRequests;
  }
}
