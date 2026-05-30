package leiphotos.utils;

/**
 * @param <E> The type of events that the listener is interested in
 * 
 * A listener of some sort of events
 * 
 *  @author malopes
 */
public interface Listener<E extends Event> {
	
	/**
	 * Process the given event 
	 * 
	 * @param e the event to process
	 * @requires e != null
	 */
	public void processEvent(E e);
	
}
