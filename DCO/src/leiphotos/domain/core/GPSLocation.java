package leiphotos.domain.core;

import leiphotos.domain.facade.GPSCoordinates;
import leiphotos.utils.RegExpMatchable;

/**
 * Represents a geographical location defined by latitude, longitude, and a description.
 * This record stores precise GPS coordinates and allows for pattern matching against
 * its description.
 * @param latitude , The geographical latitude coordinate.
 * @param longitude , The geographical longitude coordinate.
 * @param desc , A text description or name of the location.
 */
public record GPSLocation(double latitude, double longitude, String desc) implements RegExpMatchable, GPSCoordinates{
    
    @Override
    public boolean matches(String regexp){
        return (desc != null) && this.desc.matches(regexp);
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }
    @Override
    public String toString(){
        return String.format(java.util.Locale.US, "{Lat:%.2f Long:%.2f Desc:%s}", getLatitude(), getLongitude(), desc());
    }

} 