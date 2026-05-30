package leiphotos.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <E> The type of events that the subject is observed for
 * 
 * This class provides a default implementation of the Subject interface.
 * It is meant to be extended by classes that want to be observed by listeners 
 * of events of type E.	
 * 
 * @author malopes
 */
public abstract class AbsSubject<E extends Event> implements Subject<E> {
	
	protected List<Listener<E>> listeners = new ArrayList<>();
	
	protected  AbsSubject() {}
	
	/**
	 * Emits a given event to the listeners
	 * 
	 * @param e event that occurred
	 * @requires e != null
	 */
	@Override
	public void emitEvent(E e) {
		for (Listener<E> o : listeners) {
			o.processEvent(e);
		}
	}
	
	/**
	 * Registers a new listener
	 * 
	 * @param obs listener to be added
	 * @requires obs != null
	 */
	@Override
	public void registerListener(Listener<E> obs) {
		listeners.add(obs);
	}
	
	/**
	 * Removes the registry of the given listener
	 * 
	 * @param obs listener to be removed
	 * @requires obs != null
	 */
	@Override
	public void unregisterListener(Listener<E> obs) {
		listeners.remove(obs);
	}

}
