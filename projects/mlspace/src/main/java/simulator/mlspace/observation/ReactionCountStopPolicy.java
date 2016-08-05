/**
 * 
 */
package simulator.mlspace.observation;

import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * @author Arne Bittig
 *
 */
public class ReactionCountStopPolicy
    implements IComputationTaskStopPolicy<ISimulationRun> {

  private final String rcExpr;

  private final int threshold;

  private boolean rcObsInitFailed = false;

  private ReactionObserver rcObs = null;

  public ReactionCountStopPolicy(String rcExpr, int threshold) {
    this.rcExpr = rcExpr;
    this.threshold = threshold;
  }

  @Override
  public boolean hasReachedEnd(ISimulationRun task) {
    if (rcObsInitFailed) {
      return false;
    }
    if (rcObs == null) {
      initRcObs(task);
      if (rcObsInitFailed) {
        return false;
      }
    }
    return rcObs.evaluateRuleCountByNameExpression(rcExpr) >= threshold;
  }

  private void initRcObs(ISimulationRun task) {
    List<IObserver<? extends IObservable>> observers =
        task.getConfig().getComputationTaskObservers();
    for (IObserver<? extends IObservable> obs : observers) {
      if (obs instanceof ReactionObserver) {
        rcObs = (ReactionObserver) obs;
        return;
      }
    }
    SimSystem.report(Level.SEVERE,
        "No reaction count observer found. Reaction count stop inactive. You hopefully used it in disjunction with another criterion.");
    rcObsInitFailed = true;
  }

  @Override
  public String toString() {
    return "ReactionCountStop " + rcExpr + ">=" + threshold;
  }

}
