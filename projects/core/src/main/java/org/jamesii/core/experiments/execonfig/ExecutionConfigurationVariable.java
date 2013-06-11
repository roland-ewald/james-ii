/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.execonfig;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * This is an {@link ExperimentVariable} that shall modify the configuration of
 * the model execution. It therefore contains a {@link IParamBlockUpdate} as a
 * value and needs modification by a {@link ParamBlockUpdateModifier}.
 * 
 * @author Roland Ewald
 * @author Arne Bittig
 * @param <R>
 *          Type of {@link IParamBlockUpdate}
 */
public class ExecutionConfigurationVariable<R extends IParamBlockUpdate>
    extends ExperimentVariable<R> {

  /**
   * Convenience method creating sequence modifier equivalent for parameter
   * block. The first argument contains the full path to the identifier where a
   * value shall be modified down to the relevant identifier.
   * 
   * For processor factory parameters, the path will usually consist of
   * "ProcessorFactory.class.getName()" (not in quotes) followed by the
   * identifier (of the value directly or the sub-block(s) first if there is
   * further nesting).
   * 
   * @param fullTargetPath
   *          full path to the place of modification, including identifier
   * @param vals
   *          values to put there
   * @return modifier to create {@link ExecutionConfigurationVariable} from
   */
  public static ParamBlockUpdateModifier<IParamBlockUpdate> createParamBlockSequenceModifier(
      String[] fullTargetPath, Object... vals) {
    List<IParamBlockUpdate> pbUpdateMods = new ArrayList<>(vals.length);
    int shortTargetPathLength = fullTargetPath.length - 2;
    String[] targetPath =
        new String[shortTargetPathLength < 0 ? 0 : shortTargetPathLength];
    System.arraycopy(fullTargetPath, 0, targetPath, 0, targetPath.length);
    String targetName =
        fullTargetPath[shortTargetPathLength < 0 ? 0 : shortTargetPathLength];
    String ident = fullTargetPath[shortTargetPathLength + 1];
    for (Object val : vals) {
      if (val instanceof ParameterBlock) {
        pbUpdateMods.add(new SingularParamBlockUpdate(targetPath, targetName,
            (ParameterBlock) val));
      } else {
        pbUpdateMods.add(new SingularParamBlockUpdate(targetPath, targetName,
            new ParameterBlock(val, ident)));
      }
    }
    ParamBlockUpdateModifier<IParamBlockUpdate> modifier =
        new ParamBlockUpdateModifier<>(pbUpdateMods);
    return modifier;
  }

  static {
    SerialisationUtils.addDelegateForConstructor(
        ExecutionConfigurationVariable.class,
        new IConstructorParameterProvider<ExecutionConfigurationVariable<?>>() {
          @Override
          public Object[] getParameters(
              ExecutionConfigurationVariable<?> execConfVar) {
            Object[] params =
                new Object[] { execConfVar.getName(), execConfVar.getModifier() };
            return params;
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = 6908334019009149033L;

  /**
   * Default constructor.
   * 
   * @param name
   *          name of the variable
   * @param modifier
   *          the modifier to be used to generate updates for the
   *          {@link org.jamesii.core.parameters.ParameterBlock} that will be
   *          send inside the
   *          {@link org.jamesii.core.experiments.TaskConfiguration}
   */
  public ExecutionConfigurationVariable(String name,
      ParamBlockUpdateModifier<R> modifier) {
    super(name, modifier);
  }

}
