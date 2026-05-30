package leiphotos.domain.views;

import leiphotos.domain.core.TrashLibrary;
import leiphotos.domain.facade.IPhoto;

/**
 * Specific view for the trash library (TrashLibrary).
 * This class extends ALibraryView to visualize  photos that have been removed 
 * from the main Library.
 */
public class TrashLibraryView extends ALibraryView{

    /**
     * TrashLibraryView constructor.
     * Uses the ALibraryView constructor to associate the trash library
     * and defines a filter that accepts all photos present in that library.
     *  @param trashlib , the trash library containing the removed photos.
     */
    public  TrashLibraryView (TrashLibrary  trashlibrary){
        super (trashlibrary, p -> true);   
    }
}