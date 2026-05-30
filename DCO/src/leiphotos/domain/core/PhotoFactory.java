package leiphotos.domain.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import leiphotos.domain.metadatareader.*;

/**
 * Factory enum used to create Photo objects.
 * Implements the singleton pattern to provide a centralized point for instantiating 
 * Photos while automatically extracting metadata from JPEG files.
 */
public enum PhotoFactory{

    INSTANCE;

    private JpegMetadataReader read;
    private GPSLocation loc;
    
    /**
     * Creates a Photo object by extracting metadata from a file.
     * Uses a metadata reader to retrieve GPS coordinates and captured dates from 
     * the specified file path.
     * @param title , The title to be assigned to the new photo.
     * @param pathToPhotoFile , the system path to the image file.
     * @return A new Photo instance populated with extracted metadata.
     * @throws FileNotFoundException , If the file at the given path does not exist.
     * 
     */
    public Photo createPhoto(String title, String pathToPhotoFile) throws FileNotFoundException, JpegMetadataException{

        try{
            read = JpegMetadataReaderFactory.INSTANCE.createMetadataReader(new File(pathToPhotoFile));

            if(read.getGpsLocation() != null){
                double[] cords = read.getGpsLocation();
                loc = new GPSLocation(cords[0],cords[1], "");
            }
            else{
                loc = new GPSLocation(0.0, 0.0, "");
            }
            
            return new Photo(title, read.getDate(), new PhotoMetadata(Optional.of(loc), read.getDate(), read.getCamera(), read.getManufacturer()), new File(pathToPhotoFile));
        
        }catch(JpegMetadataException e){
            throw new JpegMetadataException("Metadata Error");
        }catch(FileNotFoundException e){
            throw new FileNotFoundException();
        }
}
}
