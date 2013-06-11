/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.jamesii.SimSystem;
import org.jamesii.core.math.parsetree.print.DefaultPrintManager;
import org.jamesii.core.math.parsetree.print.IPrintManager;

/**
 * A factory for creating Node objects.
 * 
 * @author Jan Himmelspach
 * @author Oliver Röwer
 */
public class NodeFactory {

  /**
   * Gets the constructor mathcing to the given set of parameters. In contrast
   * to Java's getConstructir method this one checks for supertypes as
   * parameters as well.
   * 
   * @param <N>
   *          the node type to be returned
   * 
   * @param cls
   *          the cls
   * @param parameterTypes
   *          the parameter types
   * 
   * @return the constructor
   * 
   * @throws NoSuchMethodException
   *           the no such method exception
   */
  @SuppressWarnings("unchecked")
  private static <N extends INode> Constructor<N> getConstructor(Class<N> cls,
      Class<?>[] parameterTypes) throws NoSuchMethodException {
    Constructor<?>[] constructors = cls.getConstructors();
    for (int i = 0; i < constructors.length; i++) {
      Class<?>[] currentParameterTypes = constructors[i].getParameterTypes();
      if (parameterTypes.length == currentParameterTypes.length) {
        boolean test = true;
        for (int j = 0; j < parameterTypes.length; j++) {
          test = currentParameterTypes[i].isAssignableFrom(parameterTypes[j]);
          if (!test) {
            break;
          }
        }
        if (test) {
          return (Constructor<N>) constructors[i];
        }
      }
    }
    throw new NoSuchMethodException(cls.getName() + ".<init>"
        + Arrays.toString(parameterTypes));
  }

  /**
   * Creates a new Node object.
   * 
   * NodeFactory.createNode (AddNode.class, new DefaultPrintManager(), new
   * Node[] { left, right });
   * 
   * @param <N>
   *          the node type to be returned
   * 
   * @param nodeClass
   *          the node class
   * @param printManager
   *          the print manager
   * @param params
   *          the params
   * 
   * @return the node
   */
  @SuppressWarnings("unchecked")
  public static <N extends INode> N createNode(Class<N> nodeClass,
      IPrintManager printManager, Object... params) {

    Class<?>[] parameterTypes = null;
    if (params != null) {
      parameterTypes = new Class<?>[params.length];
      for (int i = 0; i < params.length; i++) {
        parameterTypes[i] = params[i].getClass();
      }
    }

    Constructor<N> cons = null;
    try {
      cons = getConstructor(nodeClass, parameterTypes);
    } catch (SecurityException | NoSuchMethodException e1) {
      SimSystem.report(e1);
      return null;
    }

    INode node = createNode(cons, params);

    if (node == null) {
      return null;
    }

    node.setPrintManager(printManager);

    return (N) node;
  }

  /**
   * Create the node using the constructor passed. Will return null in case that
   * this fails.
   * 
   * @param cons
   * @param params
   * @return
   */
  private static <N extends INode> N createNode(Constructor<N> cons,
      Object... params) {
    try {
      return cons.newInstance(params);
    } catch (IllegalArgumentException | InstantiationException
        | IllegalAccessException | InvocationTargetException e) {
      SimSystem.report(e);
    }
    return null;
  }

  /** The current print manager. */
  private IPrintManager currentPrintManager = new DefaultPrintManager();

  /**
   * Node factory. Use Default print manager.
   */
  public NodeFactory() {
    super();
  }

  /**
   * Node factory.
   * 
   * @param printManager
   *          the print manager
   */
  public NodeFactory(IPrintManager printManager) {
    super();
    currentPrintManager = printManager;
  }

  /**
   * Creates a new Node object.
   * 
   * @param <N>
   *          the node type to be returned
   * 
   * @param nodeClass
   *          the node class
   * @param params
   *          the params
   * 
   * @return the node
   */
  public final <N extends INode> N createNode(Class<N> nodeClass,
      Object... params) {
    return NodeFactory.createNode(nodeClass, currentPrintManager, params);
  }

}
