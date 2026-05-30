package leiphotos.domain.core;

import java.time.LocalDateTime;
import java.util.Optional;
import leiphotos.utils.RegExpMatchable;

/**
 * Represents the metadata associated with a photograph.
 * This record stores technical and contextual imformation such as GPS Location,
 * the time and date the photo was captured, the camera model used, and the manufacturer.
 * It provides a funcionality to check for a pattern matches within its filds and offers a formatted String
 * representation of the metadata.
 * @param  location , An Optional containing the GPSLocation if available.
 * @param  date , The LocalDateTime when the photo was taken.
 * @param camModel , The model of the camera used.
 * @param maker , The name of the camera manufacturer.
 * 
 */
public record PhotoMetadata(Optional<GPSLocation> location, LocalDateTime date, String camModel, String maker) implements RegExpMatchable{

    @Override
    public boolean matches(String regexp){ // Emparelhamento com base na localização, cam e no maker
        return (location != null) && this.location.get().matches(regexp) || camModel != null  && this.camModel.matches(regexp)|| maker != null && this.maker.matches(regexp);
    }

    @Override
    public String toString(){

    String locStr = location.isPresent() ? location.get().toString() : "No Location";
    java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String dateStr = date().format(fmt);

    return "[" + locStr + ", " + dateStr + ", " + camModel() + ", " + maker() + "]";
    }
}