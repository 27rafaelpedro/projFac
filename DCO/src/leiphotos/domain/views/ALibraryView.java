package leiphotos.domain.views;

import java.util.Comparator;
import java.util.function.Predicate;         // filtro da vista 
import java.util.stream.Collectors;          // o stream resolve muitos problemas nas coleções, ele agrupa uma coleção e assim conseguimos filtrar                                            // para nao fazermos sempre loops de for para percorrer uma *vista toda
import leiphotos.domain.core.Photo;
import leiphotos.domain.facade.IPhoto;
import leiphotos.domain.core.Library;
import java.util.List;

/**
 * Abstract  clss that serves as the base for the different library views.
 * Implements the common logic for library storage, photo selection filtering
 * and sorting criteria.
 */
public abstract class ALibraryView implements ILibraryView{


    protected Library library;     // a biblioteca das fotos

    protected Comparator <IPhoto> comparator;  //

    protected Predicate <IPhoto> filter;   // o filtro ( serve principalmente para filtrar as fotos de uma vista )


 /**
  * ALibraryView constructor
  * Initializes the view with a library and specific filter, setting photo size 
  * as the default sorting order.
  * @param library , the library from which photos will be obtained
  * @param filter , the predicate used to filter photos for this view.
  * @requires   library != null && filter != null
  * 
  */
 public ALibraryView (Library library, Predicate<IPhoto> filter){

    this.library = library;
    this.filter = filter;
    this.comparator =  Comparator.comparingLong(IPhoto :: size);
 }

 @Override
 public void setComparator (Comparator <IPhoto> c){
    this.comparator = c;
 }
 
 @Override
 public List <IPhoto> getPhotos(){
    return library.getPhotos().stream()
                  .map (p-> (Photo) p )    
                  .filter(filter)                    
                  .sorted (comparator)              
                  .map (p-> (IPhoto) p )             
                  .collect (Collectors.toList());    
 }

@Override
 public int numberOfPhotos(){
    return (int)  library.getPhotos().stream().map (p-> (Photo) p ).filter(filter).count();
 }

 @Override
 public List <IPhoto> getMatches (String regexp){
    return getPhotos().stream()
                      .filter ( p -> p.matches(regexp))
                      .collect (Collectors.toList());
 }
}