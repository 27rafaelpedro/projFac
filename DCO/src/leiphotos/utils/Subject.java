package leiphotos.utils;

/**
 *
 * @param <E> The type of events that the subject is observed for
 * 
 * Represents objects that are observed by listeners of events of type E
 * 
 * @author malopes
 */

public interface Subject<E extends Event> {

	/**
	 * Emits a given event to the listeners
	 * 
	 * @param e event to be emitted
	 * @requires e != null
	 */
	void emitEvent(E e);

	/**
	 * Registers the given listener
	 * 
	 * @param obs listener to be added 
	 * @requires obs != null
	 */
	void registerListener(Listener<E> obs);

	/**
	 * Removes the registry of the given listener
	 * 
	 * @param obs listener to be removed
	 * @requires obs != null
	 */
	void unregisterListener(Listener<E> obs);

}