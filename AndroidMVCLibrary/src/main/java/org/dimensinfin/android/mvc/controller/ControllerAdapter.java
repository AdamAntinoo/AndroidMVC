package org.dimensinfin.android.mvc.controller;

import android.support.annotation.NonNull;
import org.dimensinfin.android.mvc.events.EventEmitter;
import org.dimensinfin.android.mvc.interfaces.ICollaboration;
import org.dimensinfin.android.mvc.interfaces.IEventEmitter;

import java.beans.PropertyChangeListener;
import java.util.Objects;

/**
 * Delegate to be used on the controllers to isolate from the model class to be used on the controller. With this
 * delegate we are able to isolate the model and be able to use inheritance for development of controllers.
 * @author Adam Antinoo
 */

public class ControllerAdapter<M extends ICollaboration> implements IModelContainer<M>, Comparable<M> {
	// - F I E L D - S E C T I O N
	/** Reference to the Model node. */
	private final M model; // Holds the model node.
	/** This is the delegate instance to be used to handle the events and the listener chain. */
	private IEventEmitter eventController = new EventEmitter();

	// - C O N S T R U C T O R - S E C T I O N
	public ControllerAdapter(@NonNull final M model) {
		Objects.requireNonNull(model);
		this.model = model;
	}

	// - D E L E G A T E - I E V E N T E M I T T E R
	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		eventController.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		eventController.removePropertyChangeListener(listener);
	}

	@Override
	public boolean sendChangeEvent(final String eventName) {
		return eventController.sendChangeEvent(eventName);
	}

	// - C O M P A R A B L E   I N T E R F A C E
	@Override
	public int compareTo(@NonNull final M target) {
		return this.getModel().compareTo(target);
	}

	// - G E T T E R S   &   S E T T E R S
	@Override
	public M getModel() {
		return model;
	}
}
