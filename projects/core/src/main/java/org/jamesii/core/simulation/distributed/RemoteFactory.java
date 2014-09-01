/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.distributed;

import java.util.List;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The remote factory provides the basic methods needed for working with an
 * unknown remote communication scheme. The three generic parameters have to be
 * used for adapting a concrete factory to the instances it can work on. Most
 * likely the methods working on remote references will not be used from the
 * "outside".
 * 
 * @author Jan Himmelspach
 * 
 * @param <R>
 *          The remote accessible interface (e.g. IModelRef, IProcesorRef)
 * @param <L>
 *          The local interface (e.g. IModel, IProcessor)
 * @param <I>
 *          The class of the information object to be used (e.g.
 *          ModelInformation, ProcessorInformation)
 */
public abstract class RemoteFactory<R, L, I> extends Factory<R> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5950231551121008374L;

  /**
   * Create a remote accessible instance for the given localRef. The remote
   * accessible variant is encapsulated in a Information - class instance.
   * 
   * @param localRef
   *          a local reference
   * @param params
   *          TODO
   * @return an information instance containing the remote accessible of the
   *         localRef param
   */
  public abstract I createRemoteInformation(L localRef, ParameterBlock params);

  /**
   * Create an information instance containing the local reference of an object
   * as well as the remote accessible.
   * 
   * @param localRef
   *          the local ref
   * @param remoteRef
   *          the remote ref
   * 
   * @return the information
   */
  public abstract I getInformation(L localRef, R remoteRef);

  /**
   * Return a (local) proxy for the given remote reference.
   * 
   * @param remoteRef
   *          the remote ref
   * 
   * @return the proxy
   */
  public abstract L getProxy(R remoteRef);

  /**
   * Return a (local) proxy for the remote reference passed within the given
   * information instance.
   * 
   * @param information
   *          the information
   * 
   * @return the proxy from info
   */
  public abstract L getProxyFromInfo(I information);

  /**
   * Get the remote accessible of the given localRef.
   * 
   * @param localRef
   *          the local ref
   * 
   * @return the remote
   */
  public abstract R getRemote(L localRef);

  /**
   * Return the list of supported interfaces (e.g. of supported IModel extending
   * interfaces)
   * 
   * @return the supported interfaces
   */
  public abstract List<Class<? extends L>> getSupportedInterfaces();

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(super.toString());
    result.append("\n Supported interfaces");
    if (getSupportedInterfaces() != null) {
      for (Class<?> c : getSupportedInterfaces()) {
        result.append("\n  - " + c.getName());
      }
    } else {
      result.append("\n  No supported interfaces");
    }

    // s += "\n Ordering efficiency index: "+getEfficencyIndex();
    return result.toString();
  }

  @Override
  public R create(ParameterBlock parameters) {
    return getRemote((L) parameters.getSubBlockValue("LOCAL"));
  }

}
