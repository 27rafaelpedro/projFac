package leiphotos.domain.controllers;

import leiphotos.domain.albums.IAlbumsCatalog;
import leiphotos.domain.facade.IAlbumsController;
import leiphotos.domain.facade.IPhoto;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;
/**
 * 
 * Controller responsible for managing photo albums.
 * Acts as an intermediary between the user interface and the albuns catalog,
 * handling logic for creating, selecting, and manipulating photo collections.
 */
public class AlbumsController implements IAlbumsController{

    private IAlbumsCatalog catalog;
    private String selected;

    /**
     * Constructs a new  ALbunsCOnstroller with the specific catalog.
     * Inilitalizes the controller to interact the provided albuns catalog and sets
     * the currently selected album to null.
     * 
     * @param c , The catalog used to store and manage the albums.
     */
    public AlbumsController(IAlbumsCatalog c){
        this.catalog = c;
        selected = null;
    }
    @Override
    public boolean createAlbum(String name){
        return catalog.createAlbum(name);
    }
    
    @Override
    public void removeAlbum(){

        if(isSelected()){
           catalog.deleteAlbum(selected);
           selected = null;
        }
    }
    
    @Override
    public void selectAlbum(String name){
        
        if(catalog.containsAlbum(name)){
            selected = name;
        }
    }
    
    @Override
    public void addPhotos(Set<IPhoto> selectedPhotos){

        if(isSelected()){
            catalog.addPhotos(selected, selectedPhotos);
        }
    } 
    
    @Override
    public void removePhotos(Set<IPhoto> selectedPhotos){

        if(isSelected()){
            catalog.removePhotos(selected, selectedPhotos);
        }
    }

    @Override
    public List <IPhoto> getPhotos(){

        if(isSelected()){
            return catalog.getPhotos(selected);
        }
        return new ArrayList<>();
    }

    @Override
    public Optional<String> getSelectedAlbum(){

        if(isSelected()){
            return Optional.of(selected);
        }
        return Optional.empty();
    }

    public boolean createSmartAlbum(String name, Predicate<IPhoto> criteria){ // Este método não é para ser feito nesta etapa
    }

    @Override
    public Set<String> getAlbumNames(){
       return catalog.getAlbumsNames();
    }
    
    /**
     * Checks if there's currently an album that is selected
     * @return true if there is a selected album, false otherwise
     */
    private boolean isSelected(){
       return selected != null;
    }
    
    @Override
    public String toString() {
       return catalog.toString();
    }

}