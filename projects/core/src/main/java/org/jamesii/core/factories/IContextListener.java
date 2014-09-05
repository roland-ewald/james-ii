package org.jamesii.core.factories;

import java.util.List;

/**
 * A context listener can register at any {@see Context} to get notified about
 * created sub objects and sub contexts etc.
 * 
 * @author Tobias Helms
 * 
 */
public interface IContextListener {

	/**
	 * A sub context and object has been created. The created object is given
	 * and the path of the context tree from the context of the created object
	 * to this context is given. The first element in the list is the context of
	 * the created object. The last element must be one of the contexts this
	 * listener listens to. If a listener listens to several contexts in one
	 * hierarchy, it is possible that this method will be called several times
	 * if these contexts are part of the same path!
	 */
	void createdEvent(Object object, List<Context> hierarchy);

}
