/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.id.UniqueIDGenerator;

/**
 * Factory to create new ObjectIDs.
 * 
 * @fixme Organize Factory as plugin?
 * @author Simon Bartels
 * 
 */
public class HostCentralIDFactory extends Factory<IObjectId> {

  /**
   * The serial version id.
   */
  private static final long serialVersionUID = 3068247690589305634L;

  /**
   * The name the sub block, the parameter block has to have for the create
   * method.
   */
  public static final String PARAM_OBJECT = "Object";

  /**
   * Default constructor.
   */
  public HostCentralIDFactory() {
    super();
  }

  /**
   * @param parameter
   *          parameter has to contain a sub block
   *          HostCentralIDFactory.PARAM_OBJECT which holds an object at will.
   * 
   * @return A new ObjectId fitting to the object that has been part of the
   *         parameterblock. The ID contains MAC, PID, HashCode and a time
   *         stamp.
   */
  @Override
  public IObjectId create(ParameterBlock parameter) {
    Object value = parameter.getSubBlock(PARAM_OBJECT).getValue();
    /*
     * JavaInfo info = new JavaInfo(); String mac = ""; for( byte b :
     * info.getMacAddress() ) mac+=b; //String mac =
     * info.getMacAddress().toString(); // Strings.dispArray(...) //String pid =
     * info.getName().split("@")[0]; //seems that it doesn't work String pid =
     * ManagementFactory.getRuntimeMXBean().getName().split("@")[0]; String hash
     * = ((Integer) value.hashCode()).toString(); String id = mac + pid + hash;
     */
    String id = UniqueIDGenerator.createUniqueID().asString();
    return new BasicRemoteObjectId(id, value.getClass().getName());
  }

}
