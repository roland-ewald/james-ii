/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.base.NamedEntity;
import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.math.random.distributions.AbstractDistribution;
import org.jamesii.core.math.random.distributions.BimodalDistribution;
import org.jamesii.core.math.random.distributions.BimodalDistributionFactory;
import org.jamesii.core.math.random.distributions.CamelDistribution;
import org.jamesii.core.math.random.distributions.CamelDistributionFactory;
import org.jamesii.core.math.random.distributions.NegativeTriangularDistribution;
import org.jamesii.core.math.random.distributions.NegativeTriangularDistributionFactory;
import org.jamesii.core.math.random.distributions.NormalDistribution;
import org.jamesii.core.math.random.distributions.NormalDistributionFactory;
import org.jamesii.core.math.random.distributions.TriangularDistribution;
import org.jamesii.core.math.random.distributions.TriangularDistributionFactory;
import org.jamesii.core.math.random.distributions.plugintype.DistributionFactory;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandom;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.eventset.plugintype.EventIdentityBehavior;
import org.jamesii.core.util.eventset.plugintype.EventOrderingBehavior;

/**
 * Tests for most {@link IEventQueue} methods.
 * 
 * @author Jan Himmelspach
 * @author Arne Bittig
 */
public abstract class EventQueueTest extends ChattyTestCase {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(EventQueueTest.class);
	}

	/** The start time. */
	private final long startTime = System.currentTimeMillis();

	/** The number of test elements. */
	private int testElements = 100;

	private boolean debug = false;

	private IRandom random;

	/**
	 * The event queue the test is run on.
	 */
	private IEventQueue<Object, Double> eventQueue = null;

	/**
	 * Instantiates a new event queue test.
	 * 
	 * @param name
	 *            the name
	 */
	public EventQueueTest(String name) {
		super(name);
	}

	/**
	 * Creates the queue.
	 * 
	 * @return A new empty event queue
	 */
	public abstract IEventQueue<Object, Double> create();

	private IEventQueue<Object, Double> internalCreate() {
		IEventQueue<Object, Double> result = create();
		setEventQueue(result);
		return result;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		Long seed = Math.round(Math.random() * (Long.MAX_VALUE - 1));

		addParameter("seed", seed);

		setRandom(new JavaRandom(seed));

	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test dequeue.
	 */
	public void testDequeue() {
		testElements = 10;

		IEventQueue<Object, Double> myQueue = internalCreate();
		// check whether dequeue using an empty queue is causing a problem
		assertEquals(true, myQueue.dequeue() == null);
		Entry<Object, Double> testEntry = new Entry<>(new Object(), 0.00000001);

		int repeatCount = 2;
		for (int i = 0; i < testElements; i++) {
			Double r = testEntry.getTime() + 0.1 + getRandom().nextDouble();
			for (int j = 0; j < repeatCount; j++) {
				myQueue.enqueue(new Object(), r);
			}
		}
		// check whether all elements have been enqueued
		assertTrue(
				"Queue lost elements! contains:should contain = "
						+ myQueue.size() + ":" + testElements * 2,
				myQueue.size() == testElements * 2);

		myQueue.enqueue(testEntry.getEvent(), testEntry.getTime());

		// one more element
		assertTrue("Queue ignored an enqueue op ... ",
				myQueue.size() == testElements * 2 + 1);

		Entry<Object, Double> compareTestEntry = myQueue.dequeue();

		// which should be gone again
		assertTrue(myQueue.size() == testElements * 2);

		assertTrue("Got " + compareTestEntry.getTime() + " as min instead of "
				+ testEntry.getTime(),
				compareTestEntry.getTime().compareTo(testEntry.getTime()) == 0);

		int oldsize = myQueue.size();
		int size = oldsize;

		print();

		// check time stamp order
		Entry<Object, Double> theLastEntry = null;
		Entry<Object, Double> theEntry = new Entry<>(null, -1.);
		for (int i = 0; i < testElements * 2; i++) {
			theLastEntry = theEntry;
			oldsize = size;
			theEntry = myQueue.dequeue();
			size = myQueue.size();
			assertNotNull("The value returned is null but it shouldn't!",
					theEntry);
			assertTrue("Dequeued " + theEntry.getTime() + " after "
					+ theLastEntry.getTime() + " in the " + i
					+ ". dequeue operation",
					theLastEntry.getTime().compareTo(theEntry.getTime()) <= 0);
			assertTrue("The size of the queue " + size
					+ " has not changed properly after dequeue. It should be "
					+ (oldsize - 1), size + 1 == oldsize);
		}

		assertEquals(true, myQueue.isEmpty());

		// check whether reinsertion of element extracted last is fine
		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), theLastEntry.getTime());
		}
		assertTrue("Queue size is not fine ... ",
				myQueue.size() == testElements);
		for (int i = 0; i < testElements; i++) {
			myQueue.dequeue();
		}
		assertTrue("Queue size is not fine ... ", myQueue.size() == 0);

		// Add a number of element with one time stamp, dequeue 1/2 of them, add
		// the
		// same number again, and repeat this 3 times: check whether the
		// handling of
		// time stamps is fine.
		double[] timeStamps = new double[] { 1.0, 10e9 + .1,
				0.00000000000000001 };
		for (int z = 0; z < timeStamps.length; z++) {
			// *** NEW QUEUE CREATED !!!!! ***
			myQueue = internalCreate();
			for (int i = 0; i < testElements; i++) {
				myQueue.enqueue(new Object(), timeStamps[z]);
			}
			assertTrue("Queue size is not fine (pass z:" + z + "). It is "
					+ myQueue.size() + " instead of " + (testElements),
					myQueue.size() == testElements);

			for (int j = 1; j < 3; j++) {
				for (int i = 0; i < testElements / 2; i++) {
					assertTrue(myQueue.dequeue() != null);
					assertTrue("Queue size is not fine (pass z:" + z + " j:"
							+ j + "). It is " + myQueue.size() + " instead of "
							+ (testElements - i - 1),
							myQueue.size() == testElements - i - 1);
				}
				for (int i = 0; i < testElements / 2; i++) {
					myQueue.enqueue(new Object(), timeStamps[z]);
				}
				assertTrue("Queue size is not fine (pass z:" + z + " j:" + j
						+ "). It is " + myQueue.size() + " instead of "
						+ testElements, myQueue.size() == testElements);
			}
		}
	}

	/**
	 * Test {@link IEventQueue#dequeueAll()}. must return all elements having
	 * the least time stamp.
	 * 
	 * Also tests {@link IEventQueue#requeue(Object, Comparable)} to some
	 * extent.
	 */
	public final void testDequeueAll() {

		IEventQueue<Object, Double> myQueue = internalCreate();
		List<Object> objectList = myQueue.dequeueAll();
		assertNotNull(objectList);
		assertTrue(objectList.isEmpty());

		objectList = myQueue.dequeueAll();
		assertNotNull(objectList);
		assertTrue(objectList.isEmpty());

		myQueue.enqueue(new Object(), 1000.);
		objectList = myQueue.dequeueAll();
		assertNotNull("The object list should not be null", objectList);
		assertTrue(
				"The queue should be empty but isEmpty does not return true. According to size the queue contains "
						+ myQueue.size() + " element(s).", myQueue.isEmpty());
		assertFalse(
				"The object list has to contain one element and should thus not be empty.",
				objectList.isEmpty());
		assertTrue(
				"The list should only contain a single elements. But it contains "
						+ myQueue.size(), objectList.size() == 1);

		myQueue.enqueue(new Object(), 1000.);
		myQueue.enqueue(new Object(), 10000.);

		objectList = myQueue.dequeueAll();

		assertFalse(objectList.isEmpty());
		assertEquals(1, objectList.size());
		assertFalse(myQueue.isEmpty());
		assertNotNull(objectList);

		myQueue.enqueue(new Object(), 1000.);
		objectList = myQueue.dequeueAll();

		assertNotNull(objectList);

		assertFalse(myQueue.isEmpty());
		assertFalse(objectList.isEmpty());
		assertEquals(1, objectList.size());

		objectList = myQueue.dequeueAll();
		assertNotNull(objectList);
		assertFalse(objectList.isEmpty());

		// tests with more elements
		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), 0.0);
		}

		List<Object> t = myQueue.dequeueAll();

		assertTrue("Queue is not empty but should be!", myQueue.isEmpty());
		assertTrue(t.size() == testElements);
		// t = myQueue.dequeueAll();
		// assertTrue (t != null);

		int zeros = 0;

		for (int i = 0; i < testElements; i++) {
			if (getRandom().nextDouble() > 0.5) {
				myQueue.enqueue(new Object(), 0.001 + getRandom().nextDouble());
			} else {
				myQueue.enqueue(new Object(), 0.0);
				zeros++;
			}
		}

		t = myQueue.dequeueAll();
		// if (t == null) System.out.println("dequeueAll not implemented????");
		// System.out.println(t.size());
		assertTrue(t.size() == zeros);
		assertTrue("Extracted eles " + t.size() + " Queue still contains "
				+ myQueue.size() + " together it should be " + testElements,
				t.size() + myQueue.size() == testElements);
		// check whether the remaining elements in the queue are not 0
		while (!myQueue.isEmpty()) {
			Entry<Object, Double> e = myQueue.dequeue();
			assertTrue(e.getTime().compareTo(0.0) != 0);
		}
		// long elapsedTime = System.currentTimeMillis() - startTime;
		// System.out.println("DequeueAll - first Set completed at " +
		// elapsedTime);
		int eles = 10000;

		for (int i = 0; i < eles; i++) {
			myQueue.enqueue(new Object(), getRandom().nextDouble());
		}
		// elapsedTime = System.currentTimeMillis() - startTime;
		// System.out.println("DequeueAll - enqueue completed at " +
		// elapsedTime);
		for (int i = 0; i < 100; i++) {
			Double time = myQueue.getMin();
			List<Object> list = myQueue.dequeueAll();
			for (Object o : list) {
				myQueue.enqueue(o, time + getRandom().nextDouble());
			}
			assertTrue(myQueue.size() == eles);
		}
		// elapsedTime = System.currentTimeMillis() - startTime;
		// System.out.println("DequeueAll - dequeue/enqueue completed at "
		// + elapsedTime);
		for (int i = 0; i < 100; i++) {
			Double time = myQueue.getMin();
			List<Object> list = myQueue.dequeueAll();
			for (Object o : list) {
				myQueue.requeue(o, time + getRandom().nextDouble());
			}
			assertTrue(myQueue.size() == eles);
		}
		// elapsedTime = System.currentTimeMillis() - startTime;
		// System.out.println("DequeueAll - dequeue/requeue completed at "
		// + elapsedTime);
		for (int i = 0; i < 100; i++) {
			Double time = myQueue.getMin();
			List<Object> list = myQueue.dequeueAll();
			for (Object o : list) {
				myQueue.requeue(o, time + getRandom().nextDouble());
			}
			assertTrue(myQueue.size() == eles);
		}

		double tim = 0;
		while (!myQueue.isEmpty()) {
			tim = myQueue.dequeue().getTime();
		}

		// myQueue = internalCreate();
		eles = 1000;
		// The seed -7538847116595691071l caused a problem in one of the queues
		// (with at least 1000 and 10000 eles)
		IRandom rand = new JavaRandom(-7538847116595691071l);
		for (int i = 0; i < eles; i++) {
			myQueue.enqueue(new Object(), tim + rand.nextDouble());
		}

		// get the min
		Double ti = myQueue.getMin();
		// remember the queue size
		long st = myQueue.size();
		// take away t entries from the queue (all those with the time ti)
		t = myQueue.dequeueAll();

		assertTrue(st == myQueue.size() + t.size());

		for (Object o : t) {
			myQueue.enqueue(o, ti);
		}

		assertTrue("Size is " + myQueue.size() + " but should be " + st
				+ ". Time value of elements " + ti
				+ ".  The random seed used: " + rand.getSeed(),
				myQueue.size() == st);

		int rememberSize = t.size();

		assertTrue("The queue should have as min time " + ti + " but it is "
				+ myQueue.getMin(), myQueue.getMin().compareTo(ti) == 0);
		// assertTrue(myQueue.size() == eles);
		// Queue has eles elements in it

		// add 10 elements with time time, thus together with the one already in
		// there should be 11 at all
		for (int i = 0; i < 10; i++) {
			myQueue.enqueue(new NamedEntity("" + i), ti);
			assertTrue("Size should be " + (eles + i + rememberSize)
					+ " but is " + myQueue.size(), myQueue.size() == eles + i
					+ rememberSize);
		}

		assertTrue("Size is " + myQueue.size() + " instead of " + eles + 10,
				eles + 10 == myQueue.size());

		// System.out.println("enqueued 10 elements with time "+ti+"\n"+
		// myQueue+". Now there are "+myQueue.size()+" elements in the queue.");
		t = myQueue.dequeueAll();
		assertTrue(
				"Size should be " + (eles - rememberSize) + " but is "
						+ myQueue.size() + "\nThe min extracted:" + ti
						+ "\nExtracted " + t.size() + " elements (" + t
						+ ") with that time\n"
						+ "The queue contains the following elements\n\n"
						+ myQueue.toString(), myQueue.size() == eles
						- rememberSize);

		assertTrue("dequeueAll should return " + (rememberSize + 10)
				+ " elements but the method returned " + t.size()
				+ ".\n The min value should be " + ti
				+ "\nThe random seed used: " + rand.getSeed()
				+ ". The queue contains \n" + myQueue.toString(),
				t.size() == rememberSize + 10);

		// elapsedTime = System.currentTimeMillis() - startTime;
		// System.out.println("dequeueAll finished at " + elapsedTime);
	}

	/**
	 * Test {@link IEventQueue#dequeueAllHashed()}. must return all elements
	 * having the least time stamp.
	 */
	public final void testDequeueAllHashed() {
		IEventQueue<Object, Double> myQueue = internalCreate();
		assertNotNull(myQueue.dequeueAllHashed());

		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), 0.0);
		}
		Map<Object, Object> t = myQueue.dequeueAllHashed();
		assertEquals(
				"Not empty! Still contains some elements: " + myQueue.size(),
				true, myQueue.isEmpty());
		assertEquals(true, t.size() == testElements);

		// t = myQueue.dequeueAll();
		// assertTrue (t != null);

		int zeros = 0;

		for (int i = 0; i < testElements; i++) {
			if (getRandom().nextDouble() > 0.5) {
				myQueue.enqueue(new Object(), 0.001 + getRandom().nextDouble());
			} else {
				myQueue.enqueue(new Object(), 0.0);
				zeros++;
			}
		}

		t = myQueue.dequeueAllHashed();
		// if (t == null) System.out.println("dequeueAll not implemented????");
		// System.out.println(t.size());
		assertTrue(t.size() == zeros);
		assertTrue("Extracted eles " + t.size() + " Queue still contains "
				+ myQueue.size() + " together it should be " + testElements,
				t.size() + myQueue.size() == testElements);
		double tim = 0;
		// check whether the remaining elements in the queue are not 0
		while (!myQueue.isEmpty()) {
			Entry<Object, Double> e = myQueue.dequeue();
			tim = e.getTime();
			assertTrue(e.getTime().compareTo(0.0) != 0);
		}
		// long elapsedTime = System.currentTimeMillis() - startTime;
		// System.out.println("dequeueAllHashed - first Set completed at " +
		// elapsedTime);
		int eles = 10000;

		for (int i = 0; i < eles; i++) {
			myQueue.enqueue(new Object(), tim + getRandom().nextDouble());
		}

		Double ti = myQueue.getMin();
		long st = myQueue.size();
		t = myQueue.dequeueAllHashed();
		// System.out.println("first deq all "+t.keySet().size());
		for (Object o : t.keySet()) {
			myQueue.enqueue(o, ti);
		}
		int rememberSize = t.size();
		assertTrue(myQueue.size() == st);
		assertTrue("The queue should have as min time " + ti + " but it is "
				+ myQueue.getMin(), myQueue.getMin().compareTo(ti) == 0);
		assertTrue(myQueue.size() == eles);

		// Queue has eles elements in it

		// add 10 elements with time time, thus together with the one already in
		// there should be 11 at all
		for (int i = 0; i < 10; i++) {
			// System.out.println("added ele with time "+ti);
			myQueue.enqueue(new Object(), ti);
			assertTrue("Size should be " + (eles + i + rememberSize)
					+ " but is " + myQueue.size(), myQueue.size() == eles + i
					+ rememberSize);
		}
		t = myQueue.dequeueAllHashed();
		assertTrue("dequeue all should be " + (rememberSize + 10) + " but is "
				+ t.size() + ".", t.size() == rememberSize + 10);

		myQueue.enqueue(new Object(), ti);

		// elapsedTime = System.currentTimeMillis() - startTime;
		// System.out.println("dequeueAllHashed - enqueue completed at " +
		// elapsedTime);
		for (int i = 0; i < 100; i++) {
			Double time = myQueue.getMin();
			Map<Object, Object> list = myQueue.dequeueAllHashed();
			for (Object o : list.keySet()) {
				myQueue.enqueue(o, time + getRandom().nextDouble());
			}
			assertTrue("Queue contains wrong number of elements! --- "
					+ myQueue.size() + " instead of " + eles,
					myQueue.size() == eles);
		}
		// elapsedTime = System.currentTimeMillis() - startTime;
		// System.out.println("dequeueAllHashed - dequeue/enqueue completed at "
		// + elapsedTime);
		for (int i = 0; i < 100; i++) {
			Double time = myQueue.getMin();
			Map<Object, Object> list = myQueue.dequeueAllHashed();
			for (Object o : list.keySet()) {
				myQueue.requeue(o, time + getRandom().nextDouble());
			}
			assertTrue(myQueue.size() == eles);
		}
		// elapsedTime = System.currentTimeMillis() - startTime;
		// System.out.println("dequeueAllHashed - dequeue/requeue completed at "
		// + elapsedTime);
		for (int i = 0; i < 100; i++) {
			Double time = myQueue.getMin();
			Map<Object, Object> list = myQueue.dequeueAllHashed();
			for (Object o : list.keySet()) {
				myQueue.requeue(o, time + getRandom().nextDouble());
			}
			assertTrue(myQueue.size() == eles);
		}
		//
		// elapsedTime = System.currentTimeMillis() - startTime;
		// System.out.println("dequeueAllHashed finished at " + elapsedTime);
	}

	/**
	 * Test {@link IEventQueue#dequeueAll(Comparable)}.
	 */
	public final void testDequeueAllTimed() {
		IEventQueue<Object, Double> myQueue = internalCreate();
		int num = 5;
		for (int i = 0; i < testElements / 2; i++) {
			myQueue.enqueue(new Object(), 4.0 + getRandom().nextDouble());
		}
		for (int i = 0; i < num; i++) {
			myQueue.enqueue(new Object(), 3.0);
		}
		for (int i = testElements / 2; i < testElements; i++) {
			myQueue.enqueue(new Object(), 2.0 + getRandom().nextDouble());
		}
		assertEquals(testElements + num, myQueue.size());

		List<Object> list = myQueue.dequeueAll(3.0);
		assertEquals("Incorrect number of dequeued elements with time 3.0",
				num, list.size());
		// number of elements after dequeueAll should be equal to testElements
		assertEquals("Number of elements in the queue should be "
				+ testElements + " but is " + myQueue.size(), testElements,
				myQueue.size());

		myQueue = internalCreate();

		myQueue.enqueue(new Object(), 0.0);
		list = myQueue.dequeueAll(0.0);
		assertEquals(list.size(), 1);

		myQueue = internalCreate();
		myQueue.enqueue(new Object(), 3.0);
		list = myQueue.dequeueAll(myQueue.getMin());
		assertEquals(1, list.size());

		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("DequeueMDouble finished at " + elapsedTime);

		List<Object> absent = myQueue.dequeueAll(Double.MAX_VALUE);
		assertNotNull("Dequeue with absent time stamp must not return null",
				absent);
		assertTrue("Dequeue with absent time stamp must return empty list",
				absent.isEmpty());
	}

	/**
	 * Test {@link IEventQueue#dequeue(Object)}.
	 */
	public final void testDequeueEvent() {
		Object testObject = new Object();
		IEventQueue<Object, Double> myQueue = internalCreate();
		assertTrue(myQueue.dequeue(testObject) == null);

		Double testVal = new Double(0.1);
		myQueue.enqueue(testObject, testVal);
		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), testVal + getRandom().nextDouble());
		}
		myQueue.dequeue(testObject);
		assertNull(myQueue.dequeue(testObject));

		for (int i = 0; i < testElements; i++) {
			Entry<Object, Double> testIt = myQueue.dequeue();
			assertTrue("Dequeued element found again in the queue !! " + i,
					testIt.getEvent() != testObject);
		}
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("dequeue finished at " + elapsedTime);

	}

	/**
	 * Test enqueue.
	 */
	public final void testEnqueue() {
		IBasicEventQueue<Object, Double> myQueue = internalCreate();

		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), 0.0);
		}
		assertTrue(myQueue.size() == testElements);

		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), Double.POSITIVE_INFINITY);
		}
		assertTrue(myQueue.size() == testElements * 2);

		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), 4.0 + getRandom().nextDouble());
		}
		assertTrue(myQueue.size() == testElements * 3);

		// large size
		for (int i = 0; i < 10000; i++) {
			myQueue.enqueue(new Object(), getRandom().nextDouble());
		}
		assertTrue("Number of entries in queue is " + myQueue.size()
				+ ". That's not ok.",
				myQueue.size() == testElements * 3 + 10000);

		for (int i = 0; i < 10000; i++) {
			try {
				assertTrue(
						"dequeued "
								+ i
								+ " entries - there should still be something in the queue",
						myQueue.dequeue() != null);
			} catch (Exception e) {
				System.out
						.println("On removing the " + i + "th element ("
								+ myQueue.size()
								+ " remaining) an exception occured: ");
				e.printStackTrace();
				fail();
			}
		}
		assertEquals(testElements * 3, myQueue.size());
		assertTrue(myQueue.size() == testElements * 3);

		myQueue = internalCreate();

		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), Double.POSITIVE_INFINITY);
		}

		myQueue.enqueue(new Object(), 2.);
		assertTrue(myQueue.getMin().compareTo(2.) == 0);
		assertTrue(myQueue.getMin().compareTo(2.) == 0);

		myQueue.enqueue(new Object(), 1.);
		assertTrue(myQueue.getMin().compareTo(1.) == 0);

		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("enqueue finished at " + elapsedTime);

		// enqueue - dequeue test

		myQueue = internalCreate();

		for (int i = 0; i < testElements * 4; i++) {
			myQueue.enqueue(new Object(), getRandom().nextDouble() * 1000);
		}

		assertEquals(testElements * 4, myQueue.size());

		for (int j = 3; j > 0; j--) {

			for (int i = 0; i < testElements; i++) {
				myQueue.dequeue();
			}

			assertEquals(testElements * j, myQueue.size());
		}

		// mixed enqueue - dequeue test

		myQueue = internalCreate();

		// t is used to make sure that after a dequeue only time stamps > the
		// last
		// time dequeued are enqueued.
		Double t = 0.;

		while (myQueue.size() < testElements * 4) {

			int s = myQueue.size();

			if ((getRandom().nextDouble() < .4) && (s > 0)) {
				Entry<Object, Double> e = myQueue.dequeue();
				if (e != null) {
					t = e.getTime();
				}
				assertEquals(s - 1, myQueue.size());
			} else {
				myQueue.enqueue(new Object(), t + getRandom().nextDouble()
						* 100);
				assertEquals(s + 1, myQueue.size());
			}
		}
	}

	/**
	 * Test {@link IEventQueue#enqueue(Object, Comparable)} again.
	 */
	public final void testEnqueueAgain() {
		IBasicEventQueue<Object, Double> myQueue = internalCreate();

		int oldSize = 0;
		int size = 0;
		Entry<Object, Double> testEntry = new Entry<>(new Object(), 0.00000001);

		// should be > 1!!!
		int repeatCount = 2;

		for (int i = 0; i < testElements; i++) {
			oldSize = size;
			Double r = testEntry.getTime() + getRandom().nextDouble();
			// enqueue time stamps more than once - in a queue stamps may appear
			// more than once!
			for (int j = 0; j < repeatCount; j++) {
				// System.out.println(i);//"enqueuing "+r+" - "+j+".");
				myQueue.enqueue(new Object(), r);
				assertTrue(/*
							 * "Failed on inserting "+r+" one more time
							 * oldsize:size "+oldSize+":"+myQueue.size()+" on
							 * adding the "+(j+1)+".th item
							 * "+"\n"+((CalendarQueue)myQueue).getString(),
							 */(oldSize + j + 1) == myQueue.size());
			}

			size = myQueue.size();
			// we have added repeatCount entries, thus there should be
			// repeatCount entries more in the queue
			assertTrue("Failed on inserting " + r + " " + repeatCount
					+ " times oldsize:size " + oldSize + ":" + size, oldSize
					+ repeatCount == size);
		}

		// ((CalendarQueue)myQueue).print(System.out);

		/*
		 * Object testobject = new Object();
		 * 
		 * myQueue.enqueue(testobject, 0.1); size = myQueue.size();
		 * myQueue.enqueue(testobject, 0.2); assertTrue("Events can be added
		 * more than once!", size+1 == myQueue.size());
		 */

		// enqueue an element having a lower time as the last one dequeued!
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("enqueue finished at " + elapsedTime);
	}

	/**
	 * Test {@link IEventQueue#getMin()}.
	 * 
	 * NOTE: this test assumes that enqueue, requeue and dequeue are working!!!
	 */
	public final void testGetMin() {

		Object testObject = new Object();
		Object testObject1_0 = new Object();

		IEventQueue<Object, Double> myQueue = internalCreate();

		assertTrue(myQueue.getMin() == null);
		assertTrue(myQueue.getMin() == null);

		myQueue.enqueue(testObject1_0, 1.0);
		assertTrue(myQueue.getMin().compareTo(1.0) == 0);

		myQueue.enqueue(new Object(), 0.5);
		assertTrue(myQueue.getMin().compareTo(0.5) == 0);
		myQueue.enqueue(new Object(), 3.0);
		assertTrue(myQueue.getMin().compareTo(0.5) == 0);
		myQueue.enqueue(new Object(), 14.0);
		assertTrue(myQueue.getMin().compareTo(0.5) == 0);
		myQueue.enqueue(new Object(), 2.0);
		assertTrue(myQueue.getMin().compareTo(0.5) == 0);
		myQueue.enqueue(testObject, Double.POSITIVE_INFINITY);

		myQueue.requeue(testObject, 4.0);
		System.out.println(myQueue);
		Double d = myQueue.dequeue().getTime();
		assertTrue("is " + d + " but should be .5", d.compareTo(.5) == 0);
		// System.out.println(myQueue);
		d = myQueue.getMin();
		assertTrue("is " + d + " but should be 1.0", d.compareTo(1.0) == 0);
		myQueue.requeue(testObject1_0, 1.0);
		assertTrue(myQueue.getMin().compareTo(1.0) == 0);

		// ((MListRe)myQueue).print();

		myQueue.requeue(testObject1_0, 2.5);
		System.out.println(myQueue);
		// ((MListRe)myQueue).print();
		d = myQueue.getMin();
		assertTrue("Expected 2.0 but got " + d, d.compareTo(2.0) == 0);
		// myQueue.dequeue();

		Entry<Object, Double> e;

		d = myQueue.getMin();
		assertTrue("Got wrong value (instead of 2.0): " + d,
				d.compareTo(2.0) == 0);
		e = myQueue.dequeue();
		assertTrue(e.getTime().compareTo(2.0) == 0);

		d = myQueue.getMin();
		assertTrue("Got wrong value (instead of 2.5): " + d,
				d.compareTo(2.5) == 0);
		e = myQueue.dequeue();
		assertTrue(e.getTime().compareTo(2.5) == 0);

		d = myQueue.getMin();
		assertTrue("Got wrong value (instead of 3.0): " + d,
				d.compareTo(3.0) == 0);
		e = myQueue.dequeue();
		assertTrue(e.getTime().compareTo(3.0) == 0);

		d = myQueue.getMin();
		assertTrue("Got wrong value (instead of 4.0): " + d,
				d.compareTo(4.0) == 0);
		e = myQueue.dequeue();
		assertTrue(e.getTime().compareTo(4.0) == 0);

		myQueue = internalCreate();

		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), Double.POSITIVE_INFINITY);
		}

		myQueue.enqueue(new Object(), 2.);

		assertTrue(myQueue.getMin().compareTo(2.) == 0);

		myQueue.dequeue();

		d = myQueue.getMin();

		assertNotNull(d);

		assertTrue("Expected infinity but got " + d,
				d.compareTo(Double.POSITIVE_INFINITY) == 0);

		myQueue.enqueue(new Object(), 1.);
		assertTrue(myQueue.getMin().compareTo(1.) == 0);

		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("getmin finished at " + elapsedTime);
	}

	/**
	 * Test {@link IEventQueue#getTime(Object)}.
	 */
	public final void testGetTime() {
		Object testObject = new Object();
		IEventQueue<Object, Double> myQueue = internalCreate();
		Double testVal = new Double(0.1);
		assertTrue(myQueue.getTime(testObject) == null);

		myQueue.enqueue(testObject, testVal);
		// test: only this entry has been added ...
		for (int i = 0; i < 3; i++) {
			// compare the time of the getMin (testobject) to it's time stamp
			// (testVal)
			assertTrue(testVal.compareTo(myQueue.getTime(testObject)) == 0);
		}
		// add more entries
		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), testVal + getRandom().nextDouble());
		}
		// now retest
		for (int i = 0; i < 3; i++) {
			// compare the time of the getMin (testobject) to it's time stamp
			// (testVal)
			assertTrue(testVal.compareTo(myQueue.getTime(testObject)) == 0);
		}

		testObject = new Object();
		testVal += 20000;
		myQueue.enqueue(testObject, testVal);
		for (int i = 0; i < 3; i++) {
			// compare the time of the getMin (testobject) to it's time stamp
			// (testVal)
			assertTrue(testVal.compareTo(myQueue.getTime(testObject)) == 0);
		}

		// TODO getTime of a not existing object!
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("getTime finished at " + elapsedTime);
	}

	/**
	 * assert that getTime(event) after event was dequeue()d does not return
	 * time
	 */
	public final void testDequeueGetTime() {
		IEventQueue<Object, Double> q = internalCreate();

		Object ev1 = new Object();
		Object ev2 = new Object();
		q.enqueue(ev2, 0.2);
		q.enqueue(ev1, 0.1);

		assertNotNull(q.getTime(ev1));
		q.dequeue();
		assertNull(q.getTime(ev1));
		assertNotNull(q.getTime(ev2));
		q.dequeue();
		assertNull(q.getTime(ev1));
		assertNull(q.getTime(ev2));
	}

	/**
	 * Test {@link IEventQueue#isEmpty()}.
	 */
	public final void testIsEmpty() {

		// System.out.print("Emp:");

		IBasicEventQueue<Object, Double> myQueue = internalCreate();

		assertEquals(true, myQueue.size() == 0);
		assertEquals(true, myQueue.isEmpty());

		for (int i = 0; i < testElements; i++) {
			myQueue.enqueue(new Object(), getRandom().nextDouble());
			assertEquals(false, myQueue.isEmpty());
		}

		assertEquals(true, myQueue.size() == testElements);

		for (int i = 0; i < testElements; i++) {
			assertEquals(false, myQueue.isEmpty());
			// int j = myQueue.size();
			// System.out.println("deq "+i+" --- "+j);
			myQueue.dequeue();
		}

		assertEquals("Queue size is not zero, but " + myQueue.size(), true,
				myQueue.size() == 0);
		assertEquals(true, myQueue.isEmpty());
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("isEmpty finished at " + elapsedTime);
	}

	/**
	 * Test {@link IEventQueue#requeue(Object, Comparable)}.
	 */
	public final void testRequeue() {

		Object testObject = new Object();

		IEventQueue<Object, Double> myQueue = internalCreate();

		myQueue.enqueue(testObject, 0.0);

		assertTrue(myQueue.getMin().compareTo(0.0) == 0);

		myQueue.requeue(testObject, 0.1);

		assertTrue(myQueue.getMin().compareTo(0.1) == 0);

		myQueue.requeue(testObject, 0.5);

		assertTrue(myQueue.getMin().compareTo(0.5) == 0);

		Entry<Object, Double> testIt = myQueue.dequeue();

		assertTrue((testIt.getEvent() == testObject)
				&& (testIt.getTime().compareTo(0.5) == 0));

		for (int i = 0; i < testElements; i++) {
			testObject = new Object();
			myQueue.enqueue(testObject, getRandom().nextDouble());
			int size = myQueue.size();
			Double t = getRandom().nextDouble();
			myQueue.requeue(testObject, t);
			assertTrue(myQueue.getTime(testObject).compareTo(t) == 0);
			assertTrue(size == myQueue.size());
		}

		// the queue should contain testElements elements
		assertTrue(
				"The queue size after enqueuing and requeuing is not ok: (size:expected size) "
						+ myQueue.size() + ":" + testElements,
				myQueue.size() == testElements);

		testIt = myQueue.dequeue();

		myQueue = internalCreate();
		Object o1 = new Object();
		Object o2 = new Object();
		Object o3 = new Object();
		Object o4 = new Object();

		myQueue.enqueue(o1, 0.);
		myQueue.enqueue(o2, Double.POSITIVE_INFINITY);
		myQueue.enqueue(o3, 0.);
		myQueue.enqueue(o4, Double.POSITIVE_INFINITY);

		myQueue.requeue(o1, 1.);
		myQueue.requeue(o3, 1.);
		myQueue.requeue(o1, 1.);

		myQueue.requeue(o3, Double.POSITIVE_INFINITY);
		myQueue.requeue(o2, 1.);

		myQueue.requeue(o3, 2.);

		assertTrue(myQueue.size() == 4);

		myQueue = internalCreate();

		Object g = new Object();
		Object b = new Object();
		Object p = new Object();
		Object gbp = new Object();

		myQueue.requeue(g, 1.);
		myQueue.requeue(b, Double.POSITIVE_INFINITY);
		myQueue.requeue(p, Double.POSITIVE_INFINITY);
		myQueue.requeue(gbp, 1.);

		myQueue.requeue(g, 1.);
		myQueue.requeue(b, Double.POSITIVE_INFINITY);
		myQueue.requeue(p, Double.POSITIVE_INFINITY);
		myQueue.requeue(gbp, 1.);

		assertTrue(myQueue.size() == 4);

		myQueue.requeue(gbp, 1.);

		myQueue.requeue(g, 2.);
		myQueue.requeue(b, 1.);
		myQueue.requeue(gbp, 1.);

		assertTrue(myQueue.size() == 4);

		myQueue.requeue(b, Double.POSITIVE_INFINITY);
		myQueue.requeue(p, 2.);
		myQueue.requeue(gbp, 2.);

		assertTrue(myQueue.size() == 4);

		// assertTrue((testIt.getEvent() == testObject) &&
		// (testIt.getTime().compareTo(0.5)== 0));
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("requeueMDouble finished at " + elapsedTime);

	}

	/**
	 * Test {@link IEventQueue#requeue(Object, Comparable, Comparable)}.
	 */
	public final void testRequeueWithOldTime() {
		Object testObject = new Object();
		IEventQueue<Object, Double> myQueue = internalCreate();
		myQueue.enqueue(testObject, 0.0);
		myQueue.requeue(testObject, 0.0, 0.1);
		myQueue.requeue(testObject, 0.1, 0.5);
		Object testObject2 = new Object();
		// this line will cause an error if the event has already been scheduled
		// and if the list implementation does not check this!!
		myQueue.enqueue(testObject2, 0.6);
		myQueue.requeue(testObject2, 0.6, 0.4);
		// the least time stamp should be 0.4
		Entry<Object, Double> testIt = myQueue.dequeue();
		assertTrue((testIt.getEvent() == testObject2)
				&& (testIt.getTime().compareTo(0.4) == 0));

		// the least time stamp should now be 0.5
		testIt = myQueue.dequeue();
		assertTrue((testIt.getEvent() == testObject)
				&& (testIt.getTime().compareTo(0.5) == 0));

		// after en/re/de queueing two elements the queue should be empty again
		assertTrue(myQueue.isEmpty());

		for (int i = 0; i < testElements; i++) {
			testObject = new Object();
			Double r = getRandom().nextDouble();
			myQueue.enqueue(testObject, r);
			int size = myQueue.size();
			myQueue.requeue(testObject, r, 0.5);
			// by requeueing the queue size should not change
			assertTrue(size == myQueue.size());
		}

		// the queue should contain testElements elements
		assertTrue(
				"The queue size after enqueuing and requeuing is not ok: (size:expected size) "
						+ myQueue.size() + ":" + testElements,
				myQueue.size() == testElements);

		testIt = myQueue.dequeue();

		// all entries should have 0.5 as time stamp, thus getting the first
		// should reveal an entry with this time stamp
		assertTrue("First element should have a time stamp of 0.5", (testIt
				.getTime().compareTo(0.5) == 0));
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("requeueWithOldTime finished at " + elapsedTime);

		Double newEventTime = null;
		// try {
		Object newTestEvent = new Object();
		newEventTime = 0.25;
		myQueue.requeue(newTestEvent, 42., newEventTime);
		assertEquals(
				"Event passed to requeue should have been enqueued at time "
						+ newEventTime, newEventTime, myQueue.getMin());
		newEventTime = 0.125;
		myQueue.requeue(newTestEvent, 0.1, newEventTime);

		// uncomment the following three statements if null should be an
		// allowed value for oldTime (the doc is unclear on which wrong
		// values to cover)

		// assertEquals("Event passed to requeue should have been enqueued at time "
		// + newEventTime, newEventTime, myQueue.getMin());
		// newEventTime = 0.75;
		// myQueue.requeue(newTestEvent, null, newEventTime);

		Entry<Object, Double> testEntry = myQueue.dequeue();
		boolean isNewTestEvent = testEntry.getEvent().equals(newTestEvent);
		boolean isNewTestTime = testEntry.getTime().equals(newEventTime);
		// if event added in this block is only entry, the time should match,
		// otherwise neither should (previously used test time stamps are
		// different from this one)
		assertEquals(isNewTestEvent, isNewTestTime);

		// } catch (RuntimeException ex) {
		// fail("requeue(event,oldTime,newTime) should work like enqueue(event,newTime) "
		// +
		// "if oldTime is wrong (i.e. not equal to getTime(event)), but failed (@t="
		// + newEventTime + "):" + ex);
		// }
	}

	/**
	 * Test correct return values for operations on empty queues.
	 */
	public final void testEmptyList() {
		IEventQueue<Object, Double> myQueue = internalCreate();
		assertEquals(myQueue.isEmpty(), true);
		assertEquals(null, myQueue.getMin());
		assertEquals(null, myQueue.getTime(new Object()));
		assertEquals(null, myQueue.dequeue(new Object()));
	}

	/**
	 * Test whether the number of elements returned by
	 * {@link IBasicEventQueue#size()} can actually be dequeued.
	 */
	public final void testSize() {
		IEventQueue<Object, Double> myQueue = internalCreate();
		myQueue.enqueue(1, 1.0);
		myQueue.requeue(1, 2.0);
		myQueue.requeue(new Integer(1), 3.0);
		myQueue.enqueue(2, 1.0);
		myQueue.requeue(new Integer(2), 2.0);
		myQueue.enqueue(3, 2.5);
		int sizeBefore = myQueue.size();
		int numDequeued = 0;
		while (!myQueue.isEmpty() && numDequeued <= 6) {
			Entry<Object, Double> entry = myQueue.dequeue();
			assertNotNull(
					"Queue supposedly nonempty, but returned null entry: "
							+ myQueue + " (" + myQueue.getClass() + ")", entry);
			numDequeued++;
		}
		assertTrue(
				"Queue allowed more dequeue operations than elements were enqueued before: "
						+ myQueue + " (" + myQueue.getClass()
						+ "); alleged size now: " + myQueue.size(),
				numDequeued <= 5);
		assertEquals(
				"Queue reported size of " + sizeBefore
						+ ", but allowed dequeue of " + numDequeued
						+ " elements before reporting empty: " + myQueue + " ("
						+ myQueue.getClass() + "); alleged size now: "
						+ +myQueue.size(), sizeBefore, numDequeued);
	}

	/**
	 * Up/down test using different event distributions. This test tests whether
	 * the random event times are correctly retrieved (in ascending order). If
	 * this test fails the test log should be checked as it contains the seed
	 * used. The seed can be fixed for regression testing in the getDistrib
	 * method of this class.
	 */
	public void testUpDown() {
		CamelDistribution cd = getDistrib(new CamelDistributionFactory());
		upDown(cd);

		NegativeTriangularDistribution nd = getDistrib(new NegativeTriangularDistributionFactory());
		upDown(nd);

		TriangularDistribution td = getDistrib(new TriangularDistributionFactory());
		upDown(td);

		BimodalDistribution md = getDistrib(new BimodalDistributionFactory());
		upDown(md);

		NormalDistribution nod = getDistrib(new NormalDistributionFactory());
		upDown(nod);
	}

	private <I extends AbstractDistribution> I getDistrib(
			DistributionFactory<I> dFac) {
		Long seed = Math.round(Math.random() * (Long.MAX_VALUE - 1));

		addParameter("seed_" + dFac.getClass().getCanonicalName(), seed);
		setRandom(new JavaRandom(seed));

		return dFac.create(new ParameterBlock(), getRandom());
	}

	private void upDown(AbstractDistribution distribution) {
		IEventQueue<Object, Double> queue = internalCreate();
		// enqueue numOfInitialElements and get the time per enqueue operation
		for (int i = 1; i <= testElements; i++) {
			double time = Math.max(0, distribution.getRandomNumber());
			Object o = new Object();
			queue.enqueue(o, time);
		}

		Double t = -1.;
		// dequeue all elements
		for (int i = 1; i <= testElements; i++) {
			Double val = queue.dequeue().getTime();
			// check whether they are properly ordered
			assertTrue("Values have to be dequeued in ascending order. But "
					+ val + " is smaller than " + t
					+ ". This has been found using the "
					+ distribution.getClass().getName() + " distribution.",
					Double.compare(val, t) >= 0);
			t = val;
		}
	}

	/**
	 * Test event queue behavior when confronted with events that are equal but
	 * not identical
	 * 
	 * TODO: This method does not assert anything and only logs the result of
	 * the test, since the event queue's expected behavior is specified in its
	 * factory, not the event queue class itself.
	 */
	public final void testEventIdentityBehavior() {
		List<EventIdentityBehavior> eqIdBs = new LinkedList<>();

		IEventQueue<Object, Double> queue = internalCreate();
		Integer firstOne = new Integer(1);
		Integer secondOne = new Integer(1);
		queue.enqueue(firstOne, 1.0);

		Double equalNonIdenticalLookupResult = queue.getTime(secondOne);
		if (equalNonIdenticalLookupResult == null) {
			eqIdBs.add(EventIdentityBehavior.IDENTITY);
		} else {
			assertEquals(1.0, equalNonIdenticalLookupResult);
			eqIdBs.add(EventIdentityBehavior.EQUALITY);
		}

		queue.enqueue(secondOne, 2.0);

		int sizeAfterTwoIdEnq = queue.size();
		if (sizeAfterTwoIdEnq == 1) {
			eqIdBs.add(EventIdentityBehavior.EQUALITY);
		} else {
			eqIdBs.add(EventIdentityBehavior.IDENTITY);
		}

		Integer thirdOne = new Integer(1);
		queue.requeue(thirdOne, 1.0);

		if (queue.size() - sizeAfterTwoIdEnq == 0) {
			eqIdBs.add(EventIdentityBehavior.EQUALITY);
		} else {
			eqIdBs.add(EventIdentityBehavior.IDENTITY);
		}

		queue.enqueue(2, 2.0);
		List<Object> rslt2 = queue.dequeueAll(2.0);
		List<Object> rslt1 = queue.dequeueAll();
		assertTrue(rslt2.contains(2));

		if (rslt2.size() == 1) {
			eqIdBs.add(EventIdentityBehavior.EQUALITY);
		} else {
			eqIdBs.add(EventIdentityBehavior.IDENTITY);
		}

		assertTrue(rslt1.contains(1));
		if (rslt1.size() == 1) {
			eqIdBs.add(EventIdentityBehavior.EQUALITY);
		} else {
			eqIdBs.add(EventIdentityBehavior.IDENTITY);
		}
		EventIdentityBehavior eqIdB = EventIdentityBehavior.and(eqIdBs);

		System.out.println("equality/identity-behavior of " + queue.getClass()
				+ " was " + eqIdB + ": " + rslt2 + "/" + rslt1
				+ "\n(found with a simple test, a more complex one"
				+ " may still reveal inconsistencies)");
	}

	/**
	 * Test return order of events with the same time stamp; NOTE: Uses
	 * {@link Collections#shuffle(List, java.util.Random))} internally and is
	 * thus non-deterministic (but should be reproducible if same seed is used).
	 * 
	 * TODO: This method does not assert anything and only logs the result of
	 * the test, since the event queue's expected behavior is specified in its
	 * factory, not the event queue class itself.
	 */
	public final void testEventOrderingBehavior() {
		final int blockAmount = 5;
		final int blockSize = 6;

		IEventQueue<Object, Double> eq1 = internalCreate();
		IEventQueue<Object, Double> eq2 = internalCreate();
		NavigableMap<Double, List<Long>> enqOrders = addSameTimeEventsToQueues(
				eq1, eq2, getRandom(), blockAmount, blockSize);
		EventOrderingBehavior orderBehavior = checkOrderBehavior(eq1, eq2,
				enqOrders);
		System.out.println(orderBehavior + " behavior of "
				+ eq1.getClass().getSimpleName() + " observed");

	}

	private EventOrderingBehavior checkOrderBehavior(
			IEventQueue<Object, Double> eq1, IEventQueue<Object, Double> eq2,
			NavigableMap<Double, List<Long>> enqOrders) {
		int numBlocks = 0;
		int fifos = 0;
		int lifos = 0;
		int lexicals = 0;

		while (!eq1.isEmpty() && !eq2.isEmpty()) {
			Double time1 = eq1.getMin();
			Double time2 = eq2.getMin();
			assertSame(time1, time2);
			List<Object> dequeueAll1 = eq1.dequeueAll();
			List<Object> dequeueAll2 = eq2.dequeueAll();
			numBlocks++;
			List<Long> enqOrder = enqOrders.get(time1);

			assertSame(dequeueAll1.size(), dequeueAll2.size());
			assertSame(enqOrder.size(), dequeueAll1.size());
			// System.err.println(eq1.getClass().getSimpleName() + "\n" +
			// enqOrder + "\n" + dequeueAll1 + "\n" + dequeueAll2);
			if (!dequeueAll1.equals(dequeueAll2)) {
				// assuming List#equals(Object) works correctly for the returned
				// list type...
				return EventOrderingBehavior.UNREPRODUCIBLE;
			}
			List<Long> enqOrderCopy = new ArrayList<>(enqOrder);
			Collections.sort(enqOrderCopy);
			if (enqOrderCopy.equals(dequeueAll2)) {
				lexicals++;
			}
			if (enqOrder.equals(dequeueAll1)) {
				fifos++;
				continue;
			}
			Collections.reverse(dequeueAll1);
			if (enqOrder.equals(dequeueAll1)) {
				lifos++;
			}
		}
		assertTrue("One eq is not empty (not an ordering problem)",
				eq1.isEmpty() && eq2.isEmpty());
		if (numBlocks == fifos) {
			return EventOrderingBehavior.FIFO;
		} else if (numBlocks == lifos) {
			return EventOrderingBehavior.LIFO;
		} else if (numBlocks == lexicals) {
			return EventOrderingBehavior.NATURAL;
		} else {
			return EventOrderingBehavior.UNORDERED_REPRODUCIBLE;
		}
	}

	private NavigableMap<Double, List<Long>> addSameTimeEventsToQueues(
			IEventQueue<Object, Double> eq1, IEventQueue<Object, Double> eq2,
			IRandom rand, int blockAmount, int blockSize) {
		NavigableMap<Double, List<Long>> enqOrder = new TreeMap<>();
		List<Double> timeStamps = new ArrayList<>(blockAmount);
		for (double d = 1; d <= blockAmount; d++) {
			timeStamps.add(d);
		}
		timeStamps = RandomSampler.sampleSet(blockAmount, timeStamps, rand);

		List<Long> allEventIDs = RandomSampler.sampleSet(blockSize
				* blockAmount, 1, blockAmount * blockSize, rand);
		// enqueue #blockSize events with the same time stamp for #blockAmount
		// time stamps
		for (int i = 0; i < blockAmount; i++) {
			Double timeStamp = timeStamps.get(i);
			List<Long> eventIDs = allEventIDs.subList(i * blockSize, (i + 1)
					* blockSize - 1);
			enqOrder.put(timeStamp, eventIDs);
			for (Long id : eventIDs) {
				eq1.enqueue(id, timeStamp);
				eq2.enqueue(id, timeStamp);
			}
		}
		return enqOrder;
	}

	/**
	 * @return the random
	 */
	public IRandom getRandom() {
		return random;
	}

	/**
	 * @param random
	 *            the random to set
	 */
	public void setRandom(IRandom random) {
		this.random = random;
	}

	@Override
	protected String createMessageProlog() {
		String result = super.createMessageProlog();
		// append the message by adding the result of a acall to toString on the
		// eventQueue queue
		result += getEventQueue();
		return result;
	}

	/**
	 * Print the eventQueue queue using the toString method. This method should
	 * only print something if {@link #isDebug()} returns true.
	 */
	@Deprecated
	protected void print() {
		if (debug) {
			System.out.println(getEventQueue());
		}
	}

	/**
	 * @return the eventQueue
	 */
	protected final IEventQueue<Object, Double> getEventQueue() {
		return eventQueue;
	}

	/**
	 * @param eventQueue
	 *            the eventQueue to set
	 */
	protected final void setEventQueue(IEventQueue<Object, Double> current) {
		this.eventQueue = current;
	}

	/**
	 * @return the debug
	 */
	@Deprecated
	protected boolean isDebug() {
		return debug;
	}

	/**
	 * Some test methods may contain a call to the {@link #print()} method. This
	 * method will print the content of the queue in these cases if the debug
	 * flag is set to true.
	 * 
	 * @param debug
	 *            the debug to set
	 */
	@Deprecated
	protected void setDebug(boolean debug) {
		this.debug = debug;
	}

}
