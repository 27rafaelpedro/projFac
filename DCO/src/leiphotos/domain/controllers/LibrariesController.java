package leiphotos.domain.controllers;

import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;

import leiphotos.domain.facade.IPhoto;
import leiphotos.domain.metadatareader.JpegMetadataException;
import leiphotos.domain.core.MainLibrary;
import leiphotos.domain.core.TrashLibrary;
import leiphotos.domain.core.PhotoFactory;
import leiphotos.domain.facade.ILibrariesController;

/**
 * Controller responsible for managing the main and trash libraries.
 * Handles the high-level opeations between the two library types, 
 * coordinating photo photo management at the storage. 
 */
public class LibrariesController implements ILibrariesController {
     
     private MainLibrary mainLib;
     private TrashLibrary trashLib;

    /**
     * LibrariesController constructor.
     * Initializes the controller with references to main and trash libraries.
     * @param mainlib , The primary library containing active photos.
     * @param trashlib , The library containing removed photos (the trash).
     */
    public LibrariesController ( MainLibrary mainLib , TrashLibrary trashLib){

        this.mainLib = mainLib;
        this.trashLib = trashLib;
    }

     @Override 
    public Optional<IPhoto> importPhoto(String title, String pathToPhotoFile){

        try {
            IPhoto novaPhoto = PhotoFactory.INSTANCE.createPhoto(title, pathToPhotoFile);
            this.mainLib.addPhoto(novaPhoto);
            return Optional.of(novaPhoto);

        } catch (FileNotFoundException e){
            return Optional.empty();

        } catch (JpegMetadataException e) {
            return Optional.empty();
        }

    }

    @Override
    public void deletePhotos(Set<IPhoto> selectedPhotos){

        for ( IPhoto photo : selectedPhotos){   

            boolean removed = this.mainLib.deletePhoto(photo);   
            
            if (removed){     
                this.trashLib.addPhoto(photo);     
            }
        }
    }

     @Override 
    public void emptyTrash(){

        this.trashLib.deleteAll();   // chamamos a função deleteAll para deixar o lixo vazio
    
    }

    @Override 
    public void toggleFavourite(Set<IPhoto> selectedPhotos){

        for (IPhoto photo : selectedPhotos){   
            photo.toggleFavourite();      
        }

    }

    @Override
    public Iterable<IPhoto> getMatches(String regExp){

        List <IPhoto> matchingPhotos = new ArrayList<>();  
        
        for (IPhoto photo : this.mainLib.getPhotos()){    
        
         if (photo.matches(regExp)){    
            matchingPhotos.add(photo);
         }

        }
        return matchingPhotos;
    }

    @Override
    public String toString() {
    return this.mainLib.toString() + "\n" + this.trashLib.toString();
    }

}
