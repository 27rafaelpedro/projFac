package leiphotos.domain.albums;

import leiphotos.domain.core.MainLibrary;

/**
 * The Album class represents a generic album
 * that inherits methods from its super class
 */
public class Album extends AAlbum{
   
   /**
    * Builds a generic album with super class constructor
    * @param name given name for the album
    * @param lib main library
    */
   public Album(String name, MainLibrary lib){
      super(name,lib);
   }
}