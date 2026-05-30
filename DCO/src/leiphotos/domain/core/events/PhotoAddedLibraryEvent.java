package leiphotos.domain.core.events;

import leiphotos.domain.facade.IPhoto;
import leiphotos.domain.core.Library;

/**
 * Event emmited by the library upon adding a photo
 */
public class PhotoAddedLibraryEvent extends PhotoLibraryEvent{
    
    /**
     * Creates a PhotoAddedLibraryEvent on a given photo in a given library
     * @param photo photo affected by the event
     * @param lib library affected by the event
     */
    public PhotoAddedLibraryEvent(IPhoto photo, Library lib){
        super(photo, lib);
    }
    
}
