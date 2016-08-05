package simulator.mlspace.brownianmotion;

import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.distributions.AbstractNormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * @author Arne Bittig
 * @date 14.10.2014
 */
public class DiscretePositionUpdaterMaskedTest extends
    AbstractPositionUpdaterTest {

  @Override
  protected IPositionUpdater getPositionUpdater(IVectorFactory vecFac,
      double avgDist, IRandom rand, AbstractNormalDistribution normDist) {
    int dim = vecFac.getDimension();
    double[] vec = new double[dim];
    // double length = avgDist / Math.sqrt(dim);
    for (int i = 0; i < dim; i++) {
      vec[i] = avgDist; // length
    }
    AbstractContinuousPositionUpdater contPosUpd =
        normDist != null ? new ContinuousPositionUpdaterScaledUniform(normDist,
            avgDist, vecFac) : new ContinuousPositionUpdaterScaledUniform(rand,
            avgDist, vecFac);
    return new DiscretePositionUpdaterMasked(vecFac.newDisplacementVector(vec),
        contPosUpd);
  }

}
