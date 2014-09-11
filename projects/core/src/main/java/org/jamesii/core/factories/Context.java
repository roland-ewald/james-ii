/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * Contexts are used in factories to determine the context in which an object is
 * being created. This makes it easier to observe dependencies between objects
 * (which object created which).
 * 
 * @author Jan Himmelspach
 * @author Tobias Helms
 */
public class Context {

	/**
	 * The parent context.
	 */
	private final Context parent;

	/**
	 * Listeners of this context.
	 */
	private final List<IContextListener> listenerList = new ArrayList<>();

	/**
	 * A new context and object is created. Inform all listeners and notify the parent context.
	 */
	protected void createdEvent(Object object, List<Context> hierarchy) {
		hierarchy.add(this);
		for (IContextListener listener : listenerList) {
			listener.createdEvent(object, Collections.unmodifiableList(hierarchy));
		}
		if (parent != null) {
		  parent.createdEvent(object, hierarchy);
		}
	}
	
	/**
	 * Add a listener to the context. Return true, if the listener was added
	 * successfully. Return false, if the listener is already registered at this
	 * context.
	 */
	public boolean addListener(IContextListener listener) {
		if (!listenerList.contains(listener)) {
			listenerList.add(listener);
			return true;
		}
		return false;
	}

	/**
	 * Remove a listener from the context. Return true, if the listener was
	 * removed successfully. Return false, if the listener could not be removed
	 * because it is not registered at this context.
	 */
	public boolean removeListener(IContextListener listener) {
		return listenerList.remove(listener);
	}

	/**
	 * Create a new instance with the given factory and parameters and context.
	 */
	public static <O> O createInstance(Factory<O> factory, ParameterBlock params, Context context) {
	  O result = factory.create(params, context);
	  context.createdEvent(result, new ArrayList<Context>());
	  return result;
	}
	
	/**
	 * Create a sub context of this context and return it.
	 */
	public Context createSubContext() {
	  return new Context(this);
	}
	
	/**
	 * Constructor which sets the parent to null.
	 */
	public Context() {
	  this.parent = null;
	}

	/**
	 * Constructor with the parent of the new context as parameter.
	 */
	public Context(Context parent) {
	  this.parent = parent;
	}
	
}
