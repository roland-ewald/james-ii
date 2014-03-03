package org.jamesii.core.util.eventset.plugintype;

import org.jamesii.core.util.eventset.IEventQueue;

/**
 * Describes how an {@link IEventQueue} implementation returns events with
 * identical time stamps. This is important when using methods like
 * {@link IEventQueue#dequeueAll(Comparable)}, as it defines which event order
 * to expect there, but also for reproducibility of experiments using
 * {@link IEventQueue#dequeue()}.
 * 
 * @author Arne Bittig
 * @author Roland Ewald
 * 
 */
public enum EventOrderingBehavior {

	/**
	 * First in, first out. This means that the order in which events are
	 * enqueued is preserved if their sorting criterion (here, sorting by time
	 * stamps) considers them to be equal, i.e., the sorting is stable.
	 */
	FIFO(true, true),

	/**
	 * Last in, first out. This means that the order in which events are
	 * enqueued is <b>reversed</b>. While not stable (see
	 * {@link EventOrderingBehavior#FIFO}), the event ordering is still
	 * deterministic.
	 */
	LIFO(false, true),

  // /** Natural order of the event type, regardless of enqueue order */
  // NATURAL(false, true),

	/**
	 * Same-time events are not returned in first-in-first-out,
	 * last-in-first-out or natural order, but the return order is reproducible
	 * for the same events and same sequence of enqueue operations.
	 */
	UNORDERED_REPRODUCIBLE(false, true),
	/**
	 * No assumptions can be made regarding the order in which events with the
	 * same time stamp are returned.
	 */
	UNREPRODUCIBLE(false, false);

	/** Flag for ordering behavior stability, see {@link #isStable()}. */
	private final boolean stable;

	/**
	 * Flag for ordering behavior is reproducibility, see
	 * {@link #isReproducible()}
	 */
	private final boolean reproducible;

	private EventOrderingBehavior(boolean stable, boolean reproducible) {
		this.stable = stable;
		this.reproducible = reproducible;
	}

	/**
	 * Indicate whether the sequence in which events with the same time stamp
	 * are returned corresponds to the behavior of a stable sorting algorithm.
	 * 
	 * This is a strictly stronger condition than {@link #isReproducible()
	 * reproducibility}.
	 * 
	 * @return true iff event queue preserves insertion order of same-time-stamp
	 *         events
	 */
	public boolean isStable() {
		return stable;
	}

	/**
	 * Indicate whether event queue can be used to reproduce experiments that
	 * include events with the same time stamp, or whether the event queue
	 * introduces indeterminism in this case. (The latter can, for example, be
	 * the result of using HashMaps or HashSets internally for these events.)
	 * 
	 * @return true iff return order of same-time-stamp is reproducible
	 */
	public boolean isReproducible() {
		return reproducible;
	}
}
