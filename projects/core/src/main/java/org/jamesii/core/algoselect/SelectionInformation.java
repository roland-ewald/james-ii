/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.algoselect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.data.storage.plugintype.AbstractDataStorageFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.generators.plugintype.AbstractRandomGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Represents information regarding a selection. Used to reconstruct the
 * selection procedure.
 * 
 * 08.05.2007
 * 
 * TODO: Change paramBlackList to non-static and allow all (concrete) factories
 * to register parameters that need to be filtered out.
 * 
 * @author Roland Ewald
 * 
 * @param <F>
 *          the type of the selected factory
 * 
 */
public class SelectionInformation<F extends Factory<?>> implements Serializable {
  static {
    SerialisationUtils.addDelegateForConstructor(SelectionInformation.class,
        new IConstructorParameterProvider<SelectionInformation<?>>() {
          @Override
          public Object[] getParameters(SelectionInformation<?> selInfo) {
            Object[] params =
                new Object[] { selInfo.getAbstractFactory(),
                    selInfo.getParameter(), selInfo.getFactory() };
            return params;
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = -3935775463872891839L;

  /**
   * This maps filters out any parameters that should not be stored in the
   * selection tree.
   */
  private static final Map<String, Object> PARAM_BLACK_LIST = new HashMap<>();
  static {
    // The partition is a complex object storing the model etc.
    PARAM_BLACK_LIST.put(AbstractProcessorFactory.PARTITION, new Object());
    // The rand seed is stored somewhere else in the database.
    PARAM_BLACK_LIST.put(AbstractRandomGeneratorFactory.SEED, new Object());
    // The directory managers for data storages should not be stored
    PARAM_BLACK_LIST
        .put(AbstractDataStorageFactory.PARAM_MANAGER, new Object());
  }

  /** Reference to class definition of abstract factory. */
  private final Class<? extends AbstractFactory<F>> abstractFactory;

  /** Selected factory. */
  private final F factory;

  /** Parameter used for selection. */
  private final ParameterBlock parameter;

  /** String representation of parameter block (with pre-defined order). */
  private final String paramString;

  /** The string representation of the whole object. */
  private final String stringRepresentation;

  /**
   * Default constructor.
   * 
   * @param af
   *          abstract factory
   * @param fParams
   *          factory parameter
   * @param fac
   *          the factory
   */
  public SelectionInformation(Class<? extends AbstractFactory<F>> af,
      ParameterBlock fParams, F fac) {
    abstractFactory = af;
    factory = fac;
    ParameterBlock cleanedParameters = getCleanedParameter(fParams);
    parameter = cleanedParameters == null ? null : cleanedParameters.getCopy();
    paramString = createParamString(parameter);
    stringRepresentation = createStringRepresentation();
  }

  /**
   * Gets the abstract factory.
   * 
   * @return the abstract factory
   */
  public Class<? extends AbstractFactory<F>> getAbstractFactory() {
    return abstractFactory;
  }

  /**
   * Gets the factory.
   * 
   * @return the factory
   */
  public F getFactory() {
    return factory;
  }

  /**
   * Gets the factory class.
   * 
   * @return the factory class
   */
  @SuppressWarnings("unchecked")
  // getClass(...) call to wild-card type
  public Class<? extends Factory<?>> getFactoryClass() {
    return (Class<? extends Factory<?>>) factory.getClass();
  }

  /**
   * Gets the parameter.
   * 
   * @return the parameter
   */
  public ParameterBlock getParameter() {
    return parameter;
  }

  /**
   * Copies parameter block, but (recursively) filters all sub-blocks from the
   * black list.
   * 
   * @param parameters
   *          the parameter to be copied
   * @return copy of the parameter block
   */
  private ParameterBlock getCleanedParameter(ParameterBlock parameters) {
    if (parameters == null) {
      return null;
    }
    ParameterBlock pb = new ParameterBlock(parameters.getValue());
    for (Map.Entry<String, ParameterBlock> subBlock : parameters.getSubBlocks()
        .entrySet()) {
      String ident = subBlock.getKey();
      if (PARAM_BLACK_LIST.containsKey(ident)) {
        continue;
      }
      pb.addSubBlock(ident, getCleanedParameter(parameters.getSubBlock(ident)));
    }
    return pb;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SelectionInformation<?>)) {
      return false;
    }
    return stringRepresentation
        .equals(((SelectionInformation<?>) o).stringRepresentation);
  }

  @Override
  public int hashCode() {
    return stringRepresentation.hashCode();
  }

  /**
   * Gets the param string.
   * 
   * @return the param string
   */
  public String getParamString() {
    return paramString;
  }

  /**
   * Creates a string representation of the parameter block.
   * 
   * @param parameter
   *          the parameter block
   * @return the string
   */
  private String createParamString(ParameterBlock parameter) {
    if (parameter == null) {
      return "";
    }
    StringBuilder subContent = new StringBuilder();
    Map<String, ParameterBlock> subBlocks = parameter.getSubBlocks();
    List<String> keys = new ArrayList<>(subBlocks.keySet());
    Collections.sort(keys);
    for (String key : keys) {
      if (subContent.length() != 0) {
        subContent.append(";");
      }
      subContent.append(key);
      subContent.append("( ");
      subContent.append(" )");
    }

    String result = "val = " + parameter.getValue() + "|sub content: ";
    return subContent.length() == 0 ? result + "none" : result + subContent;
  }

  /**
   * Creates the string representation of the object.
   * 
   * @return the string representation
   */
  private String createStringRepresentation() {
    StringBuilder result = new StringBuilder();
    result.append(abstractFactory == null ? "null" : abstractFactory.getName());
    result.append('/');
    result.append(factory == null ? "null" : factory.getClass().getName());
    result.append('/');
    result.append(createParamString(parameter));
    return result.toString();
  }

}