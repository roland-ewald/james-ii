package simulator.mlspace.observation;

import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * @author Arne Bittig
 */
public class ReactionCountStopFactory
    extends ComputationTaskStopPolicyFactory<ISimulationRun> {

  private static final long serialVersionUID = 3435392353691034498L;

  public static final String REACTION_COUNT_EXPRESSION =
      "ReactionCountExpression";

  public static final String REACTION_COUNT_THRESHOLD =
      "ReactionCountThreshold";

  @Override
  public IComputationTaskStopPolicy<ISimulationRun> create(
      ParameterBlock paramBlock, Context context) {
    return new ReactionCountStopPolicy(
        paramBlock.getSubBlockValue(REACTION_COUNT_EXPRESSION),
        paramBlock.getSubBlockValue(REACTION_COUNT_THRESHOLD));
  }

}
