package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import java.io.IOException;

public class MultiLineStringSerializer extends StdSerializer<MultiLineString> {

    public MultiLineStringSerializer() {
        super(MultiLineString.class);
    }

    @Override
    public void serialize(MultiLineString mls, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("type", "MultiLineString");
        gen.writeArrayFieldStart("coordinates");
        for (int i = 0; i < mls.getNumGeometries(); i++) {
            LineString line = (LineString) mls.getGeometryN(i);
            gen.writeStartArray();
            for (int j = 0; j < line.getNumPoints(); j++) {
                Point p = line.getPointN(j);
                
                double[] latLon = CoordinateTranslator.metersToLatLon(p.getX(), p.getY());

                gen.writeStartArray();
                gen.writeNumber(latLon[0]); // Latitude
                gen.writeNumber(latLon[1]); // Longitude
                gen.writeEndArray();
            }
            gen.writeEndArray();
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
