/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.base.NamedEntity;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Base factory class. Each factory can be parameterized (have a list of custom
 * parameters). All parameters which are not understood at the level of this
 * factory must be forwarded to the objects they create.
 * 
 * @author Jan Himmelspach
 */
public abstract class Factory<I> extends NamedEntity implements IFactory<I> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -566466464342451827L;

  /**
   * a human readable version of the factory
   */
  private String readableName = null;

  /**
   * Instantiates a new factory.
   */
  public Factory() {
    super();
  }

  /**
   * Gets a human readable name of the factory. This is the name given in the
   * xml file, or if none is specified there, as a fallback, it returns a camel
   * case parsed value of the current simple factory class name excluding any
   * factory present in the class name. Override this to provide an even more
   * readable name if the auto generated name doesn't do. Please note that
   * changing the source (factory information) later on will not change the
   * return value of this method as the readable name is cached.
   * 
   * @return the readable name
   */
  public String getReadableName() {
    if (readableName == null) {

      IFactoryInfo info =
          SimSystem.getRegistry().getFactoryInfo(getClass().getName());

      if (info != null) {
        readableName = info.getName();
        if (readableName != null && readableName.trim().length() > 0) {
          return readableName;
        }
      }

      readableName = BasicUtilities.makeFactoryClassNameReadable(this);
    }

    return readableName;
  }

  /**
   * Set the context of the instance, if the instance implements the
   * {@link IContext} interface, and if the block passed contains an
   * {@link IContext} object with the fully qualified class name of
   * {@link IContext} as ident.
   * 
   * By using this it is possible to create a hierarchy of contexts, i.e., to
   * assign newly created objects to a context. Instead of using this methods
   * callers of create can set themself as context of the object just created.
   * 
   * This method can be called from implementations of the
   * {@link #create(ParameterBlock)} method, e.g., wrapping the last return
   * statements of such a method.
   */
  protected I setInstanceContext(I instance, ParameterBlock block) {
    if (instance instanceof IContext) {
      IContext context =
          ParameterBlocks.getSubBlockValue(block, IContext.class.getName());
      ((IContext) instance).setContext(context);
      SimSystem.report(Level.CONFIG, " The object " + instance
          + " has been assigned to the context " + context);
    }

    return instance;
  }

  /**
   * Extracts a sub-block for the given base factory, retrieves the
   * corresponding abstract factory from the registry, and returns a concrete
   * factory that was selected based on the contents of the parameters
   * sub-block.
   * 
   * @param <T>
   *          the type of the factory which shall be returned
   * @param params
   *          the parameter-block that contains a sub-block with factory
   *          parameters
   * @param baseFactory
   *          the class of the base factory
   * @return an instantiated factory, selection based on the parameters
   *         sub-block with the FQCN of the base factory
   */
  @SuppressWarnings("unchecked")
  // The registry does not ensure that getAbstractFactory(...) will
  // actually return an abstract factory for type T
  public <T extends Factory<?>> T getSubAlgoByParams(ParameterBlock params,
      Class<T> baseFactory) {
    Registry reg = SimSystem.getRegistry();
    Class<? extends AbstractFactory<?>> abstFac =
        reg.getAbstractFactoryForBaseFactory(baseFactory);
    return reg.getFactory((Class<? extends AbstractFactory<T>>) abstFac,
        getSubAlgoParams(params, baseFactory));
  }

  /**
   * Extracts a parameter sub-block for a sub-algorithm identified by a certain
   * factory.
   * 
   * @param <T>
   *          the type of the factory which shall be returned
   * @param params
   *          the parameter block
   * @param baseFactory
   *          the base factory defining the type of the sub-algorithm
   * @return the parameter block to initialise/select the sub-algorithm
   */
  public <T extends Factory<?>> ParameterBlock getSubAlgoParams(
      ParameterBlock params, Class<T> baseFactory) {
    return params.getSubBlock(baseFactory.getName());
  }

  @Override
  public String toString() {
    return getReadableName();
  }

  public <V> V getParameter(String ident, ParameterBlock parameter) {
    if (parameter.hasSubBlock(ident)) {
      return parameter.getSubBlockValue(ident);
    }
    return null; // throw exception
  }

}
