package leiphotos.domain.core;

import java.time.Duration;
import java.time.LocalDateTime; // para usar o isAfter, isBefore

import leiphotos.domain.facade.IPhoto;

/**
 * Represents a library for recently deleted photos, acting as a temporary trash bin.
 * This class extends ATrashLibrary and manages the lifecycle of deleted photos,
 * ensuring they are permently removed after a specified expiration period.
 */
public class RecentlyDeletedLibrary extends ATrashLibrary {
    
    private LocalDateTime lastCleaning;   
    private Duration expirationTime;        

   /**
    * 
    * Constructs a new RecentlyDeletedLibrary.
    * Inilitalizes the trash binwith a default expiration time of 24 hours for photos
    * and sets the last cleaning time to current time.
    */
   public RecentlyDeletedLibrary(){
     super();
     this.expirationTime = Duration.ofSeconds(15); 
     this.lastCleaning = LocalDateTime.now();    
   }
    
    @Override
    public boolean cleaningTime (){

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime proxLimpeza = lastCleaning.plusSeconds(2);
       
       return agora.isAfter (proxLimpeza); 
    }

     @Override
     public void clean (){
        trashContents.entrySet().removeIf (entry -> entry.getValue().plus(expirationTime).isBefore(LocalDateTime.now()));
        this.lastCleaning = LocalDateTime.now();
    }
    
    @Override
    public String toString(){

    StringBuilder sb = new StringBuilder();
    sb.append("***** TRASH PHOTO LIBRARY: ").append(getNumberOfPhotos()).append(" photos ****\n");
    
    for (IPhoto photo : getPhotos()) {
        sb.append(photo.toString()).append("\n");
    }
    return sb.toString();
    }

}
