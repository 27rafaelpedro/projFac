package leiphotos.domain.views;

import java.util.Comparator;
import java.util.List;
import leiphotos.domain.facade.IPhoto;



public interface ILibraryView {

  /**
   * Defines the sorting criteria for photos in this view.
   *  @param  c , The comparator that defines the photo order.
   *  @requires c != null
   */
  void setComparator (Comparator <IPhoto> c);

  /**
   * @return  The total number of photos present in this view after filtering.
   * @ensures \result >= 0
   */
  int numberOfPhotos();

  /**
   * Returns a list of photos from the view, filtered and sorted by current comparator. 
   * @return A list with all photos visible in this view.
   * @ensures \result != null
   * 
   */
  List<IPhoto> getPhotos();

  /**
   * Filters the photos in this view that match the provived expression.
   * @param regexp , The expression to be applied to the photo data.
   * @return List of photos that match the regexp.
   * @requires regexp != null
   * @ensures \result != null
   * 
   */
  List <IPhoto> getMatches( String regexp);


}