package org.jamesii.core.math.shapes;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.geometry.shapes.Sphere;
import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;

/**
 * @author Arne Bittig
 * @date 05.11.2014
 */
public class SphereTest extends ChattyTestCase {

	/** rounding tolerance for comparisons */
	private static final double DELTA = 1e-12;

	/** Test volume calulation methods of Sphere */
	public void testSphereVolumeCalc() {
		for (double radius : new double[] { 1., Math.PI, 42.,
				Math.exp(Math.exp(Math.PI - 2)) }) {
			for (int dim = 1; dim <= 5; dim++) {
				assertEquals(
						radius,
						Sphere.calculateRadius(
								Sphere.calculateVolume(radius, dim), dim),
						DELTA);
				assertEquals(
						radius,
						Sphere.calculateVolume(
								Sphere.calculateRadius(radius, dim), dim),
						DELTA);
			}
		}
		
	}

	public void testSphereDisplacement() {
		AVectorFactory vecFac = new AVectorFactory(2);
		Sphere s2 = new Sphere(vecFac.origin(), 2);
		Sphere s3 = new Sphere(vecFac.newPositionVector(2., 3.), 3);
		IDisplacementVector dft1 = (s2.dispForTouchOutside(s3,
				vecFac.newDisplacementVector(-1., 0.)));
		assertEquals(2., dft1.get(1), DELTA);
		assertEquals(0., dft1.get(2), DELTA);
		IDisplacementVector dft2 = (s2.dispForTouchOutside(s3,
				vecFac.newDisplacementVector(1., 0.)));
		assertEquals(-6., dft2.get(1), DELTA);
		assertEquals(0., dft2.get(2), DELTA);
		IDisplacementVector dft3 = s2.dispForTouchOutside(s3,
				vecFac.newDisplacementVector(-1., -1.));
		assertEquals(1., dft3.get(1), DELTA);
		assertEquals(1., dft3.get(2), DELTA);
	}
}
