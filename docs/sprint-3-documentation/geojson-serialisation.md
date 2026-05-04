# GeoJSON Serialisation Documentation

Initial Notes:

This is how the geometry (spatial) data from the database is serialised into GeoJSON format.

Relevant files are in the [config folder](../../osm-to-kuwaiba/osm-to-kuwaiba-spring-boot/src/main/java/ac/uk/solent/com617/kuwaiba/osm_to_kuwaiba/config/)

## The Problem

The database stores geometry in PostGIS geometry format. When Hibernate loads a row from the
database, it maps the PostGIS geometry column to a JTS geometry object in Java. As an example
a point becomes `org.locationtech.jts.geom.Point` When the REST API returns data, we need to
serialise the JTS objects into JSON.

The problem is that by default the application does not know how to serialise JTS geometry types
into GeoJSON format. 

Multiple approaches to solving this problem was attempted before arriving at the final solution.

## Attempt 1 - Forking `jackson-datatype-jts`

[This](https://github.com/bedatadriven/jackson-datatype-jts) repository provides a JTS module that
can serialise JTS Geometry into GeoJSON format. The problem with this is that it was written for
the older `com.vividsolutions.jts` package and does not support `org.locationtech.jts`. Due to this
the repository was forked and package references were updated to `org.locationtech.jts`. The fork
was then pulled in as a dependency using jitpack.io. 

The problem here was Hibernates own Jackson module was being prioritised and the custom module was
never invoked. 

The forked repository can be found [here](https://github.com/45inertia/jackson-datatype-jts)

## Attempt 2 - Overriding Hibernates Jackson Module

Once the problem of hibernates jackson module being prioritised was identified. There were attempts
to overcome this problem:

**Defining a replacement `ObjectMapper`**

This was done by using the annotations `@Bean @Primary ObjectMapper` but this caused other issues
elsewhere as the application required Spring Boots default configuration. 

**`Jackson2ObjectMapperBuilderCustomizer`**

This was the recommended approach for customising the mapper without replacing it. However the
import could not be resolved at runtime and therefore this approach was abandoned. 

**`SimpleModule` as `@Bean`**

This approach was to get Spring Boot to auto detect module beans and register them but in practice
Hibernates module still had priority. 

**`@JsonSerialise` on model getters**

This annotation was used in an attempt to serialise the data and override Hibernates module but it
was discovered that Hibernate's interference occured at a lower level than Jacksons annotation 
processing.

## Attempt 3 - Custom Serialisers

Three custom serialiser classes were written for each of the geometry types that need to be exported
from the application. These were:
- `Point`: handled by `PointSerializer.java`
- `LineString`: handled by `LineStringSerializer.java`
- `MultiLineString` handled by `MultiLineStringSerializer.java`

Each serialiser extends `StdSerializer` from the Jackson library and writes the GeoJSON geometry
format using a JsonGenerator. The output follows the GeoJSON specification defined
[here](https://datatracker.ietf.org/doc/html/rfc7946#autoid-41).

The reason the problem was hard to solve was that Hibernate's Jackson module is registered with
Spring Boot's `ObjectMapper`. This module was taking precedence over any custom configuration
regardless of registration order or `@Order` annotations.

The final solution was to construct a separate, dedicated `ObjectMapper` inside the 
`NetworkController` that only registers the three custom serialisers and nothing else. As
Hibernate's module is never registered on this mapper, there is no conflict.

