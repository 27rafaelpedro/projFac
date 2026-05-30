package leiphotos.domain.core.events;

import leiphotos.domain.facade.IPhoto;
import leiphotos.domain.core.Library;

/**
 * Event emitted by the library upon a change of state in a photo
 * in this case we consider a photo changing state if it was
 * favourited and it isn't anymore, vice-versa.
 */
public class PhotoChangedLibraryEvent extends PhotoLibraryEvent{
    
    /**
     * Creates a PhotoChangedLibraryEvent on a given photo in a given library
     * @param photo photo affected by the event
     * @param lib library affected by the event
     */
    public PhotoChangedLibraryEvent(IPhoto photo, Library lib){
        super(photo, lib);
    }
    
}
