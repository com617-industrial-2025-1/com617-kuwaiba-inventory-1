package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.locationtech.jts.geom.Point;
import java.io.IOException;

public class PointSerializer extends StdSerializer<Point> {
	
	public PointSerializer() {
        super(Point.class);
    }

    @Override
    public void serialize(Point p, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "Point");
        gen.writeArrayFieldStart("coordinates");
        
        double[] latLon = CoordinateTranslator.metersToLatLon(p.getX(), p.getY());
        gen.writeNumber(latLon[0]); // Latitude
        gen.writeNumber(latLon[1]); // Longitude
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
