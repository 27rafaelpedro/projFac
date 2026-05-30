package leiphotos.domain.core.events;

import leiphotos.domain.core.Library;
import leiphotos.domain.facade.IPhoto;
import leiphotos.utils.Event;

/**
 * An abstract photo library event, with the photo and library upon 
 * which the event has happened
 * 
 * @author malopes
 */
public abstract class PhotoLibraryEvent implements Event {
	
	/**
	 * The photo upon which the event has happened 
	 * and the library upon which the event has happened
	 */
	private IPhoto photo;
	private Library lib;
	
	
	/**
	 * @param photo the photo upon which the event has happened
	 * @param lib the library upon which the event has happened
	 */
	PhotoLibraryEvent (IPhoto photo, Library lib) {
		this.photo = photo;
		this.lib = lib;
	}

	/**
	 * @return the photo upon which the event has happened
	 */
	public IPhoto getPhoto() {
		return photo;
	}
	
	/**
	 * @return the library upon which the event has happened
	 */
	public Library getLibrary() {
		return lib;
	}
}
