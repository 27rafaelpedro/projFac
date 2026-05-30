package leiphotos.domain.core;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import leiphotos.domain.facade.IPhoto;

/**
 * Abstract class for libraries that manage deleted photos.
 * Provides the core insfrastructure for a trash system, mapping deleted photos to 
 * their respective deletion timetamps. SubClasses must implement specific cleaning 
 * and timing logic.
 */
public abstract class ATrashLibrary implements TrashLibrary {

    protected abstract void clean();

    protected abstract boolean cleaningTime();

    protected Map <IPhoto, LocalDateTime> trashContents;

    /**
     * Constructs a new ATrashLibrary.
     * Inilitalizes the internal mao that tracks deleted photos and their
     * deletion dates using a HashMap.
     * 
     */
    public ATrashLibrary(){

        this.trashContents = new HashMap<>();

    }

    @Override
    public Collection<IPhoto> getPhotos(){

        if (cleaningTime()){     
            clean();        
        }
        return new ArrayList <> (trashContents.keySet());
    }
    
    @Override
    public boolean deleteAll(){

        if (this.trashContents.isEmpty()){        
            return false;                         
        }
        this.trashContents.clear();      
        return true;     
    }
    
    @Override
    public boolean addPhoto(IPhoto photo){

      if(!trashContents.containsKey(photo)){
        trashContents.put(photo, LocalDateTime.now());
        return true;
      }
        return false;
    }
    
    @Override
    public boolean deletePhoto(IPhoto photo){

        if(trashContents.containsKey(photo)){
            trashContents.remove(photo);
         return true;
        }
         return false;
    }

    @Override
    public int getNumberOfPhotos(){
      return getPhotos().size();
    }
    
    @Override
    public Collection<IPhoto> getMatches(String regexp){

        ArrayList<IPhoto> matches = new ArrayList<>();

        for(IPhoto p : trashContents.keySet()){

            if(p.matches(regexp)){
                matches.add(p);
            }
        }
        return matches;
    }
 
    @Override
    public String toString() {

        String
        return "***** TRASH LIBRARY: " + getNumberOfPhotos() + " Photos *****";
    }
}