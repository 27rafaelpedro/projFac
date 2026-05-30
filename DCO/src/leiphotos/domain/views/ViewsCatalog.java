package leiphotos.domain.views;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import leiphotos.domain.core.MainLibrary;
import leiphotos.domain.core.TrashLibrary;
import leiphotos.domain.facade.IPhoto;
import leiphotos.domain.facade.ViewsType;

/**
 * Catalog that stores and manages difeerent library viewsl
 * Mpas each ViewType to its respective ILibraryView implementation.
 * 
 */
public class ViewsCatalog implements IViewsCatalog {

    private Map <ViewsType , ILibraryView> views;  /// um para para gardar as vistas 

    /**
	 * ViewsCatalog constructor.
   * Initializes the system's view catalog, configuring different ways to visualize photos.
   * (Mian, Favourites, Trash and Recent).
	 * @param mainlib , The main Library containing all photos.
	 * @param trashlib , the trash Library  containing removed photos.
   * 
	 */
    public ViewsCatalog (MainLibrary mainlib, TrashLibrary trashlib){
        this.views = new HashMap<>();

        views.put(ViewsType.ALL_MAIN, new MainLibraryView (mainlib, p->true)); 

        //criar as vista dos favoritos
        views.put (ViewsType.FAVOURITES, new MainLibraryView (mainlib, p -> p.isFavourite())); 
    
        views.put(ViewsType.ALL_TRASH, new TrashLibraryView (trashlib));

        LocalDateTime limite = LocalDateTime.now().minusDays(1);
       views.put (ViewsType.MOST_RECENT, new MainLibraryView (mainlib, p -> p.addedDate().isAfter(limite)));  
    }

  @Override
  public ILibraryView getView(ViewsType t){
    return views.get(t);
  }

  @Override
  public String toString(){

     StringBuilder sb = new StringBuilder();
     sb.append("***** VIEWS *****\n");

    for (Map.Entry<ViewsType, ILibraryView> entry : views.entrySet()) {
        ViewsType type = entry.getKey();
        ILibraryView view = entry.getValue();

        sb.append("***** VIEW ").append(type.name())
          .append(": ").append(view.getPhotos().size()).append(" photos ***** \n");

        for (IPhoto photo : view.getPhotos()) {
            sb.append(photo.file().getPath()).append("\n"); 
        }
    }
    return sb.toString();
    }
}