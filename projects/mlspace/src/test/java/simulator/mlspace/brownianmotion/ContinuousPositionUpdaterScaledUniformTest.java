package simulator.mlspace.brownianmotion;

import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.distributions.AbstractNormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * @author Arne Bittig
 * @date 14.10.2014
 */
public class ContinuousPositionUpdaterScaledUniformTest extends
    AbstractPositionUpdaterTest {

  @Override
  protected IPositionUpdater getPositionUpdater(IVectorFactory vecFac,
      double avgDist, IRandom rand, AbstractNormalDistribution normDist) {
    if (normDist != null) {
      return new ContinuousPositionUpdaterScaledUniform(normDist, avgDist,
          vecFac);
    }
    return new ContinuousPositionUpdaterScaledUniform(rand, avgDist, vecFac);
  }

}
