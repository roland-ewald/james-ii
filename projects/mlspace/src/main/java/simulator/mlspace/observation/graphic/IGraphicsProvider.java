/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic;

import java.awt.Graphics2D;

import org.jamesii.core.math.geometry.shapes.IShape;

import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.eventrecord.IEventRecord;

/**
 * Interface for classes that extract information on what to draw/paint from an
 * {@link IEventRecord} and do the drawing. In {@link GraphicalOutputObserver} ,
 * calls to {@link javax.swing.JPanel#paint(java.awt.Graphics)} will be
 * delegated to the paint method of each {@link IGraphicsProvider} known to it.
 *
 * At least one of the {@link IGraphicsProvider}s known to the
 * {@link GraphicalOutputObserver} must be able to extract the root shape for
 * the latter (for initialization of the frame, e.g. with appropriate aspect
 * ratio) and provide it as return value of its init method.
 *
 * @author Arne Bittig
 * @param <E>
 *          Type of {@link IEventRecord} handled
 * @date 22.05.2012
 */
public interface IGraphicsProvider<E extends IEventRecord> {

  /**
   * Indicate whether the {@link #paint(Graphics2D)} method fills the available
   * area such that no other area-filling graphics provider should reasonably
   * paint on top of it, or whether only lines are drawn, which is also taken as
   * an indicator that showing slightly transparent previous versions of what
   * was painted is useful for indicating trajectories.
   * 
   * @return true if the whole area and not is affected by
   *         {@link #paint(Graphics2D)}
   */
  boolean isFilling();

  /**
   * If the graphics provider can reasonably be used as the only provider for a
   * {@link GraphicalOutputObserver}, it must be able to tell the latter what
   * the root shape is, i.e. the area in which all entities to be shown are. If
   * the simulator given contains no components relevant to the graphics
   * provider, or if the graphics provider should only be used in conjunction
   * with others (e.g. for hybrid simulation) and one of these others is known
   * to be able to extract a root shape, this one may simply return null
   * instead.
   * 
   * @param proc
   *          Simulator
   * @return root shape, or null if it could not be determined
   */
  IShape getRootShape(AbstractMLSpaceProcessor<?, ?> proc);

  /**
   * Initialize graphics provider with information to be extracted from
   * simulator.
   * 
   * @param proc
   *          Simulator
   * @param fp
   *          Properties of the frame in which to draw/paint
   */
  void init(AbstractMLSpaceProcessor<?, ?> proc,
      GraphicalOutputObserver.FrameProps fp);

  /**
   * Update internal information in response to changes of the components to be
   * displayed.
   * 
   * @param effect
   *          Changes to consider
   */
  void update(E effect);

  /**
   * Do the drawing
   * 
   * @param g
   *          Graphics context
   */
  void paint(Graphics2D g);

  /**
   * @return Copy of instance (e.g. to be used with different
   *         {@link GraphicalOutputObserver.FrameProps})
   */
  IGraphicsProvider<?> copy();

}