package leiphotos.domain.views;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import leiphotos.domain.core.events.PhotoLibraryEvent;
import leiphotos.domain.facade.IPhoto;
import leiphotos.utils.Listener;
import leiphotos.domain.core.MainLibrary;

/**
 * Implementation of a main library view that supports cache and events.
 * This class extends ALibraryView and observes library changes through 
 * the Listener interface
 */
public class MainLibraryView extends ALibraryView implements Listener <PhotoLibraryEvent> {

   private List <IPhoto> cache;

    /**
     * MainLibraryView constructor.
     * Initializes the view with the provided library and filter.
     * @param lib ,  the main photo library.
     * @param filter , the filter to be applied to select the photos for this view.
     * @requires  lib != null && filter != null
     * @ensures this.cache != null
     */
    public MainLibraryView(MainLibrary lib, Predicate<IPhoto> filter){
     super(lib, filter); 
     this.cache = super.getPhotos();   
     lib.registerListener(this); 
    }

    @Override
    public void processEvent(PhotoLibraryEvent e){
        this.cache = super.getPhotos();
    }

    @Override
    public List<IPhoto> getPhotos(){
        return this.cache; 
    }


    @Override
    public void setComparator (Comparator <IPhoto> c ){
        super.setComparator(c);    
        this.cache = super.getPhotos(); 
    }      
}

