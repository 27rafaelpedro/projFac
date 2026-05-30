package leiphotos.domain.views;

import leiphotos.domain.facade.ViewsType;
/**
 * A views catalog, which provides access to the different library views 
 * supported by the application. A view is obtained from the catalog by 
 * asking for it by its type,as defined by facade.ViewsType. 
 * 
 * @authors malopes
 */
public interface IViewsCatalog {

	/**
	 * Returns a library view of the given type.
	 * 
	 * @param type The type of the view to be returned
	 * @return The view of the library of the given type
	 */
	ILibraryView getView(ViewsType t);

}