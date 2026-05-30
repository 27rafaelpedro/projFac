package leiphotos.domain.controllers;

import java.util.Comparator;
import java.util.List;
import leiphotos.domain.facade.IPhoto;
import leiphotos.domain.facade.IViewsController;
import leiphotos.domain.facade.ViewsType;
import leiphotos.domain.views.IViewsCatalog;

/**
 * Controller responsible for managing library views.
 * Acts as an intermediary between the user interface and the views catalog,
 * handling photo retrieval and filtering logic.
 */
public class  ViewsController implements IViewsController{

    private IViewsCatalog catalog;

    /**
     * ViewsController constructor.
     * Inilitalizes the controller with a views catalog.
     *  @param catalog , The catalog where all views are sorted and managed.
     */
    public ViewsController (IViewsCatalog catalog){
        this.catalog = catalog;          
    }

   @Override
   public List<IPhoto>  getPhotos(ViewsType viewType){
    return catalog.getView(viewType).getPhotos();    
   }
   
   @Override
   public List<IPhoto> getMatches(ViewsType viewType, String regexp){
    return catalog.getView(viewType).getMatches(regexp); 
   }
   
   @Override
   public void setSortingCriteria(ViewsType v, Comparator<IPhoto> criteria){
      catalog.getView(v).setComparator(criteria);
    

   }

   @Override
   public String toString(){
    
    return this.catalog.toString();
   }
}