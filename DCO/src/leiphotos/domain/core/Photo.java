package leiphotos.domain.core;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import leiphotos.domain.facade.GPSCoordinates;
import leiphotos.domain.facade.IPhoto;
import leiphotos.utils.RegExpMatchable;

/**
 * Represents a photograph in the system with associated metadata and file imformation.
 * This class stores th photo's title, file properties, captured/addition dates, and 
 * technical metadata. It also tracks whether the photo is marked as a favorite .
 */
public class Photo implements IPhoto, RegExpMatchable {

    private String title;                       // titulo da foto 
    private long size;                          // tamanho da foto             
    private boolean favourite;                  //boleano para saber é ou nao favorita a foto
    private LocalDateTime addedDate;           // data em que foi adicionada á biblioteca
    private LocalDateTime capturedDate;         // data em que a foto foi tirada
    private PhotoMetadata metadata;             // imformações sobre a foto?
    private File pathToFile;                    // caminho para a foto 

 /**
  * Constructs a new Photo.
  * Inilitalizes the photo with its title, the date is was added to the library,
  * its technical metadata, and the reference to the actual file.
  * The size is automatically calculated from the file, and the favorite status is 
  * set to false by default.
  * @param title , The descriptive title of the Photo,
  * @param  dateAddedToLib , The timetamp when the photo was imported.
  * @param  metadata  , The technical metadata.
  * @param  pathToFile , The File object pointing to the image on disk.
  */
 public Photo (String title, LocalDateTime dateAddedToLib, PhotoMetadata metadata, File pathToFile){
    
    this.title = title;
    this.addedDate = dateAddedToLib;
    this.capturedDate = metadata.date();
    this.metadata = metadata;
    this.pathToFile = pathToFile;
    this.favourite = false;      
    this.size = pathToFile.length();    
 }

 @Override
 public String title(){
    return this.title;   
 }
 
 @Override
 public LocalDateTime capturedDate(){
    return this.capturedDate;     
 }
 @Override
 public LocalDateTime addedDate(){
    return this.addedDate;           
 }

 @Override
 public boolean isFavourite(){
    return this.favourite;     
 }
 
 @Override
 public void toggleFavourite(){
   this.favourite = !this.favourite;
}

 @Override
 public long size(){
   return this.size;  
 }
 
 @Override
 public File file(){
    return this.pathToFile; 
 }
 
 @Override
 public boolean matches(String regexp){
    return (title != null) && this.title.matches(regexp) || (metadata != null) && this.metadata.matches(regexp) || 
    (pathToFile != null) && this.pathToFile.getName().matches(regexp);
 }


@Override
 public Optional<? extends GPSCoordinates> getPlace(){
   return Optional.ofNullable(this.metadata.location().get());
 }

 @Override 
 public String toString(){
   
   DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String sizeFormatted = String.format("%,d", this.size());
    
    StringBuilder sb = new StringBuilder();
    sb.append("File:").append(pathToFile).append("\n");
    sb.append("Title:").append(title).append(" Added:").append(addedDate.format(fmt))
      .append(" Size:").append(sizeFormatted).append("\n");
    sb.append(metadata.toString());
    
    if (this.isFavourite()) {
        sb.append(" FAV");
    }
    return sb.toString();
 }
 
 /**
  * Implementation of the equals method from the Object class
  * @param p photo to compare
  * @return true if this photo is equal to the given photo, false otherwise
  */
 public boolean equals(IPhoto p){
   return this.file().equals(p.file());
 }
 }