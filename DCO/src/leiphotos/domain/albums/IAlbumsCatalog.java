package leiphotos.domain.albums;

import java.util.List;
import java.util.Set;
import leiphotos.domain.facade.IPhoto;

/**
 * The interface for AlbumsCatalog in the LeiPhotos application.
 * This interface provides methods to create, delete, search and 
 * get the name of albums as well as methods to add, remove and 
 * search photos from albums.
 */
public interface IAlbumsCatalog{

 /**
  * Creates, if possible, an album for the catalog with a given name
  * @param albumName name of the created album
  * @requires albumName != null && !this.containsAlbum(albumName)
  * @return true if the operation was executed, false otherwise
  */
 boolean createAlbum(String albumName);
 
 /**
  * Deletes, if possible, an album from the catalog with a given name
  * @param albumName name of the album to delete
  * @requires albumName != null && this.containsAlbum(albumName)
  * @return true if the operation was executed, false otherwise
  */
 boolean deleteAlbum(String albumName);

 /**
  * Searches the catalog for the album with a given name
  * @param albumName searching for album with this name
  * @return true if the operation was executed, false otherwise
  */
 boolean containsAlbum(String albumName);
 
 /**
  * Adds photos to an album of the catalog
  * @param albumName name of the album to store photos
  * @param selectedPhotos collection of photos to add to the album
  * @requires selectedPhotos != null && albumName != null && this.containsAlbum(albumName)
  * @return true if the operation was executed, false otherwise
  */
 boolean addPhotos(String albumName, Set<IPhoto> selectedPhotos);

 /**
  * Removes photos from an album of the catalog
  * @param albumName name of the album from where the photos are removed
  * @param selectedPhotos collection of photos to remove from the album
  * @requires selectedPhotos != null && albumName != null && this.containsAlbum(albumName)
  * @return true if the operation was executed, false otherwise
  */
 boolean removePhotos(String albumName, Set<IPhoto> selectedPhotos);
 
 /**
  * Returns a collection of photos from a given album
  * @param albumName name of the album from where the photos are taken
  * @requires albumName != null
  * @return List of photos from the album with given name or empty list if album not found
  */
 List <IPhoto> getPhotos(String albumName);

 /**
  * Returns a collection of album names from the catalog
  * @return Set of album names from the catalog
  */
 Set<String> getAlbumsNames();
 
}