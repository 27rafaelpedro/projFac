package leiphotos.domain.albums;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import leiphotos.domain.core.MainLibrary;
import leiphotos.domain.core.events.PhotoLibraryEvent;
import leiphotos.domain.core.events.PhotoRemovedLibraryEvent;
import leiphotos.domain.facade.IPhoto;

/**
 * The abstract album class represents a generic album type
 * that allows the user to get information relevant to the album
 * such as name, number of photos. Includes operations for adding,
 * removing, getting photos. As well as processing events from
 * the main library and a textual representation.
 */
public abstract class AAlbum implements IAlbum{

 protected ArrayList <IPhoto> gallery;
 protected String albumName;
 protected MainLibrary library;
    
    /**
     * Creates a generic album
     * @param name name of the album
     * @param lib main library
     * @ensures this album is a listener of the given main library
     * @requires name != null && lib != null
     */
    public AAlbum(String name, MainLibrary lib){
        this.albumName = name;
        this.library = lib;
        this.gallery = new ArrayList<>();

        library.registerListener(this); 
    }

    @Override
    public int numberOfPhotos(){
        return gallery.size();
    }

    @Override
    public String getName(){
        return albumName;
    }

    @Override
    public List <IPhoto> getPhotos(){
         return new ArrayList<IPhoto>(this.gallery);  
    }

    @Override
    public boolean addPhotos(Set<IPhoto> photos){

        boolean wasAdded = false;
        
        for(IPhoto p : photos){
            
            if(!gallery.contains(p)){
                gallery.add(p);
                wasAdded = true;
            }
        }
        return wasAdded;
    }
    
    @Override
    public boolean removePhotos(Set<IPhoto> photos){

        boolean wasRemoved = false;

        for(IPhoto p : photos){

            if(gallery.contains(p)){
               gallery.remove(p);
               wasRemoved = true;
            }
        }
        return wasRemoved;
    }
    
    @Override
    public void processEvent(PhotoLibraryEvent e){

        if(e instanceof PhotoRemovedLibraryEvent){ 
            gallery.remove(e.getPhoto());          
        }
    }

    @Override
public String toString() {
    
    StringBuilder sb = new StringBuilder();
    List<IPhoto> photos = getPhotos();
    
    sb.append("***** Album ").append(this.albumName).append(": ")
      .append(photos.size()).append(" photos *****\n");
    
    for (IPhoto p : photos) {
        sb.append(p.file()).append("\n");
    }
    
    return sb.toString();
}
       
 }