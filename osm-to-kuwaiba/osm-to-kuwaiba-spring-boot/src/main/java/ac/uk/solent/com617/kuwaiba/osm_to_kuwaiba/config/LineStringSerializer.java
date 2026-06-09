package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import java.io.IOException;

public class LineStringSerializer extends StdSerializer<LineString> {

    public LineStringSerializer() {
        super(LineString.class);
    }

    @Override
    public void serialize(LineString line, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "LineString");
        gen.writeArrayFieldStart("coordinates");
        for (int i = 0; i < line.getNumPoints(); i++) {
            Point p = line.getPointN(i);
            
            double[] latLon = CoordinateTranslator.metersToLatLon(p.getX(), p.getY());

            gen.writeStartArray();
            gen.writeNumber(latLon[0]); // Latitude
            gen.writeNumber(latLon[1]); // Longitude
            gen.writeEndArray();
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}