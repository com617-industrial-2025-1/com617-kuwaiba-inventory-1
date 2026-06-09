package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config;

public class CoordinateTranslator {
   
   // working without a coordinate conversion library - probably less accurate
   // see https://community.esri.com/t5/arcgis-online-ideas/covert-point-geometry-to-latitude-and-longitude/idi-p/1477369
   /**
    * Converts Web Mercator (EPSG:3857) coordinates to Latitude and Longitude (EPSG:4326).
    * @param x  (corresponds to Longitude in Web Mercator)
    * @param y  (corresponds to Latitude in Web Mercator)
    * @return an array where index 0 is Latitude and index 1 is Longitude
    */
   public static double[] metersToLatLon(double x, double y) {
      double originShift = 2 * Math.PI * 6378137.0 / 2.0; // Earth radius in meters
      
      double lon = (x / originShift) * 180.0;
      double lat = (y / originShift) * 180.0;
      
      lat = 180.0 / Math.PI * (2.0 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
      
      return new double[]{lat, lon}; // Returns [Latitude, Longitude]
  }

}
