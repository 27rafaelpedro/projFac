package leiphotos.domain.metadatareader;

import java.io.FileNotFoundException;
import java.io.File;
/**
 * Singleton responsible for creating the JpegMetadataReader object
 * in the JavaXTMetadatadaReaderAdapter class
 */
public enum JpegMetadataReaderFactory{

    INSTANCE;
    /**
     * Creates the metadata reader for future adaptation
     * @param file given file
     * @throws JpegMetadataException if there are metadata errors
     * @throws FileNotFoundException if file does not exist
     */
    public JpegMetadataReader createMetadataReader(File file) throws JpegMetadataException, FileNotFoundException{
        
        try{
            return new JavaXTMetadataReaderAdapter(file);

        }catch(FileNotFoundException e){
            throw new FileNotFoundException();

        }catch(JpegMetadataException e){
            throw new JpegMetadataException("Metadata Error");
        }
        
        }            
    }

