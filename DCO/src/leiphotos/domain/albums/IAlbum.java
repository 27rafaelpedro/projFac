package leiphotos.domain.albums;

import java.util.List;
import java.util.Set;

import leiphotos.domain.core.events.PhotoLibraryEvent;
import leiphotos.domain.facade.IPhoto;
import leiphotos.utils.Listener;
/**
 * An album of photos. Has a unique name and stores a set of photos. 
 * An album is a listener of photo library events, and
 * updates itself according to the events it receives.
 * Always updates itself when a photo is removed.
 * 
 * @author malopes
*/
public interface IAlbum extends Listener<PhotoLibraryEvent>{

	/**
	 * Returns the number of photos in the album.
	 * @return the number of photos in the album
	 * @ensures result >= 0
	 */
	int numberOfPhotos();

	/**
	 * Returns the name of the album.
	 * @return the name of the album
	 * @ensures result != null
	 */
	String getName();

	/**
	 * Returns the photos in the album in a given order.
	 * @return a list with the photos in the album
	 * @ensures result != null
	 */
	List<IPhoto> getPhotos();

	/**
	 * Adds, if possible, a set of photos to the album.
	 * @param photos the photos to be added
	 * @requires photos != null
	 * @return true if the operation was executed, false otherwise
	 */
	boolean addPhotos(Set<IPhoto> photos);

	/**
	 * Removes, if possible, a set of photos from the album.
	 * Photos that are not in the album are ignored.
	 * @param photos the photos to be removed
	 * @requires photos != null
	 * @return true if the operation was executed, false otherwise
	 */
	boolean removePhotos(Set<IPhoto> photos);

	/**
	 * Updates itself when a photo that it contains is removed.
	 * It might update itself in reaction to other events.
	 * 
	 * @param e the event to process
	 * @requires e != null
	 */
	@Override
	public void processEvent(PhotoLibraryEvent e);
}