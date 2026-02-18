# 1. Physical Layer

 - OSM polygons (like building footprints for house locations) can
   contain self-intersections. `ST_MakeValid()` can fix this
   (https://postgis.net/docs/ST_MakeValid.html) so they can be indexed
   and queried correctly.
 - There may be overlapping line segements and duplicates when importing. Cleaning queries can be used to identify and remove identical geometries.

# 2. Network Layer

 - Street networks must be split at intersections so that a manhole or cabinate can be placed at the junction `ST_Intersction`can be used (https://postgis.net/docs/ST_Intersection.html).
 - PostGIS can be used to remove segments of the networks not connected to the main grid, these are called "islands". This is usually caused by mising data or drawing errors.
 - Curved roads can have lots of unnecessary points, creating a large amount of nodes. The function `ST_Simplify` can be used to reduce bloat in the database whilst maintiaining the essential paths (https://postgis.net/docs/ST_Simplify.html). 

# 3. Attributes & Tags

 - Similar tags must be mapped into a single category, e.g., utility poles and telecom poles being a single pole tag.
 - If any entires have missing metadata they must be filled with sensible defaults. For example residential street cabinetes are assumed to be the same type if blank.

# 4. Strategy

 1. Remove irrelevant data (e.g., trees, benches) during the initial import.
 2. Remove duplicate data. 
 3. Fix geometries like house footprints.
 4. Simplify highly detailed data which is not required.
 5. Simplify tags and attributes.
 6. Export cleaned and structured data.
