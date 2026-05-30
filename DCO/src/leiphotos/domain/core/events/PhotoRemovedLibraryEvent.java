package leiphotos.domain.core.events;

import leiphotos.domain.facade.IPhoto;
import leiphotos.domain.core.Library;

/**
 * Event emitted by the library upon removing a photo
 */
public class PhotoRemovedLibraryEvent extends PhotoLibraryEvent{
    
    /**
     * Creates a PhotoRemovedLibraryEvent on a given photo in a given library
     * @param photo photo affected by the event
     * @param lib library affected by the event
     */
    public PhotoRemovedLibraryEvent(IPhoto photo, Library lib){
        super(photo, lib);
    }
    
}
