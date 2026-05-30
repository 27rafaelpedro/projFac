package leiphotos.domain.metadatareader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import leiphotos.services.JavaXTJpegMetadataReader;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * The JavaXTMetadataReaderAdapter class is an adaption of the
 * JpegMetadataReader interface.
 */
public class JavaXTMetadataReaderAdapter implements JpegMetadataReader{

    private JavaXTJpegMetadataReader reader;
    
    /**
     * Creates a JavaXTMetadataReaderAdapter object
     * @param file given
     * @throws FileNotFoundException if file is null
     * @throws JpegMetadataException if file does not exist
     * @ensures exceptions are handled, returning appropriate types
     */
    public JavaXTMetadataReaderAdapter(File file) throws FileNotFoundException, JpegMetadataException{

        if(file == null){
            throw new FileNotFoundException();
        }
        
        try{
            reader = new JavaXTJpegMetadataReader(file);
        }catch(IllegalArgumentException e){
            throw new JpegMetadataException("Metadata Error");
        }
}

    @Override
    public String getCamera(){
        return this.reader.getCamera();    
    }
    
    @Override
    public String getManufacturer(){
        return this.reader.getManufacturer();
    }

    @Override
    public LocalDateTime getDate(){
        
        if(this.reader.getDate() != null){
        return LocalDateTime.parse(this.reader.getDate(), DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss" ));
        }
        return LocalDateTime.now();
    }
    
    @Override
    public String getAperture(){
        return this.reader.getAperture();
    }

    @Override
    public double[] getGpsLocation(){
        return this.reader.getGPS();
    }
}