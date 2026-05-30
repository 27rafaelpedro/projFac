package leiphotos.domain.albums;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;

import leiphotos.domain.facade.IPhoto;
import leiphotos.domain.core.MainLibrary;

/**
 * The AlbumsCatalog class represents a catalog containing albums that
 * allows the user to get information of the catalog, such as the name of
 * the albums in the catalog or if a certain album is in the catalog. As well
 * as operations for creating, adding, deleting albums, operations for
 * adding or deleting photos from albums and a textual representation.
 */
public class AlbumsCatalog implements IAlbumsCatalog {

    private HashMap <String, IAlbum> nameAlbum;
    private MainLibrary library;
 /**
  * Creates a catalog of albums
  * @param lib main library
  * @requires lib != null
  */
 public AlbumsCatalog(MainLibrary lib){
    this.library = lib;
    nameAlbum = new HashMap<>();
 }
 
 @Override
 public boolean createAlbum(String albumName){

    if(containsAlbum(albumName)){
        return false;
    }
    
    nameAlbum.put(albumName, new Album(albumName, library));
    return true;
 }

 @Override
 public boolean deleteAlbum(String albumName){

     if(nameAlbum.containsKey(albumName)){
        nameAlbum.remove(albumName);
        return true;
     }
     return false;
 }
 
 @Override
 public boolean containsAlbum(String albumName){

     return nameAlbum.containsKey(albumName);
 }
 
 @Override
 public boolean addPhotos(String albumName, Set<IPhoto> selectedPhotos){  
   return nameAlbum.get(albumName).addPhotos(selectedPhotos);
 }
 
 @Override
 public boolean removePhotos(String albumName, Set<IPhoto> selectedPhotos){
    return nameAlbum.get(albumName).removePhotos(selectedPhotos);
 }
 
 @Override
 public List <IPhoto> getPhotos(String albumName){

   if(albumName != null){
   return nameAlbum.get(albumName).getPhotos();
   }
    return new ArrayList<>();
 }
 
 @Override
 public Set<String> getAlbumsNames(){
    return new HashMap<String,IAlbum>(nameAlbum).keySet();
 }

 @Override
public String toString() {
    StringBuilder sb = new StringBuilder("***** ALBUMS *****\n");
    
    for (IAlbum a : this.nameAlbum.values()) {
        sb.append(a.toString()); 
    }
    
    return sb.toString();
}

}