package leiphotos.domain.core;

import leiphotos.utils.AbsSubject;
import leiphotos.domain.core.events.PhotoAddedLibraryEvent;
import leiphotos.domain.core.events.PhotoChangedLibraryEvent;
import leiphotos.domain.core.events.PhotoLibraryEvent;
import leiphotos.domain.core.events.PhotoRemovedLibraryEvent;
import leiphotos.domain.facade.IPhoto;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the primary photo storage of the system.
 * This class manages a collection of photos and notifies observers of library
 * events by extending AbsSubject.  It implements the basic funcionality defined
 *  in the Library Interface.
 */
public class MainLibrary extends AbsSubject<PhotoLibraryEvent> implements Library {

    private ArrayList <IPhoto> collection;
    
    /**
     * Inilitalizes a new empty MainLibrary.
     * Sets up the internal list to sotre photo objects.
     */
    public MainLibrary(){
        this.collection = new ArrayList<>();
    }

    @Override
    public int getNumberOfPhotos(){
        return collection.size();
    }
    
    @Override
    public boolean addPhoto(IPhoto photo){

        if(!collection.contains(photo)){
            collection.add(photo);
            emitEvent(new PhotoAddedLibraryEvent(photo, this));
            return true;
        }
        return false;
    }

    /**
     * Added method for PhotoChangedLibraryEvent emission
     * @param photo photo that was changed
     * @requires photo != null
     */
    public void toggleFavourite(IPhoto photo){

        if(!photo.isFavourite()){
           photo.toggleFavourite();
           emitEvent(new PhotoChangedLibraryEvent(photo, this));
        }
    }

    @Override
    public boolean deletePhoto(IPhoto photo){

        if(collection.contains(photo)){
            collection.remove(photo);
            emitEvent(new PhotoRemovedLibraryEvent(photo,this)); // Se a foto foi removida, avisar listeners
            return true;
        }
        return false;
    }
    
    @Override
    public Collection<IPhoto> getPhotos(){
        return new ArrayList<IPhoto>(this.collection);
    }
    
    @Override
    public Collection<IPhoto> getMatches(String regexp){

        ArrayList<IPhoto> matches = new ArrayList<>();

        for(IPhoto p : collection){

            if(p.matches(regexp)){
                matches.add(p);
            }
        }
       return matches;
    }
    
    @Override
    public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("***** MAIN PHOTO LIBRARY: ").append(getNumberOfPhotos()).append(" photos *****\n");
    
    for (IPhoto photo : getPhotos()) {
        sb.append(photo.toString()).append("\n");
    }
    return sb.toString();
    }
}