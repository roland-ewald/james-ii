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
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Default implementation of the {@link IContext} interface to be used for
 * delegation.
 * 
 * 
 * @author Jan Himmelspach
 * 
 */
public class Context {

	/**
	 * The parent context.
	 */
	private Context parent;

	/**
	 * The child contexts.
	 */
	private List<Context> children;

	/**
	 * Listeners of this context.
	 */
	private List<IContextListener> listenerList = new ArrayList<>();

	/**
	 * A new context and object is created. Inform all listeners and notify the parent context.
	 */
	protected void createdEvent(Object object, List<Context> hierarchy) {
		hierarchy.add(this);
		for (IContextListener listener : listenerList) {
			listener.createdEvent(object, Collections.unmodifiableList(hierarchy));
		}
		parent.createdEvent(object, hierarchy);
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

	@SuppressWarnings("unchecked")
	public static <O> O createInstance(String pluginType, ParameterBlock block,
			Context context) {

		Class<? extends AbstractFactory<Factory<?>>> abstractFactory = SimSystem
				.getRegistry().getFactoryType(pluginType);

		Factory<?> factory = SimSystem.getRegistry().getFactory(
				abstractFactory, block);

		O result = null;
		if (factory != null) {
			result = (O) factory.create(block, SimSystem.getRegistry().createContext());
		}

		SimSystem.report(Level.FINEST, "Created an instance of " + pluginType
				+ " in the context " + context);

		return result;
	}

	public <O> O create(String pluginType, ParameterBlock block) {
		return createInstance(pluginType, block, this);
	}

}
