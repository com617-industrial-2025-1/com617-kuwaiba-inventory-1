# Geometry Serialization

This document outlines the approaches tried to get the Geometry serialization to work for the data
export.

## Problem

To export the data through the rest API it was necessary to serialize the JTS geometry Objects in a
GeoJSON format. 

The project uses Hibernate Spatial to map PostGIS geometry columns to JTS geometry objects in Java.
Hibernate bundles its own Jackson module with serializers for JTS types automatically with Spring
Boot's Object Mapper. This module does not correctly handle `org.locationtech.jts` geometry types.
It attempts to serialize them as raw Java Objects which caused infinite recursion which threw an
Exception. 

## Approaches Attempted

### 1. Forking `jackson-datatype-jts`
This library provides a `JtsModule` that teaches jackson how to serialize JTS geometry as GeoJSON.
However this library was written for the `com.vividsolutions.jts` package and does not support
`org.locationtech.jts`. Therefore this project was forked and the package references were changed
from `com.vividsolutions.jts` to `org.locationtech.jts` and can be viewed [here](https://github.com/45inertia/jackson-datatype-jts).
The fork compiled correctly and using [jitpack.io](https://jitpack.io/#45inertia/jackson-datatype-jts)
was used as a dependecy in the main `osm-to-kuwiaba` project.

It was discovered that hibernate's own jackson module was taking precedence over the forked project
regardless of the registration order. 

### 2. `@Bean @Primary ObjectMapper`
An ObjectMapper was defined as a bean in a `@Configuration` class. This contained the new custom
serializers. This approach attempted to replace Spring Boots ObjectMapper with our own defined
ObjectMapper. This caused other issues as it lacked Spring Boots default configuration.

### 3. `Jackson2ObjectMapperBuilderCustomizer`
This was the recommended approach for customizing the Object Mapper without replacing it. To do this
a `Jackson2ObjectMapperBuilderCustomizer` bean was defined. This approach did not work as the import
`org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer` couldn't be
resolved in development despite the dependency being present on the class path.

### 4. `SimpleModule` as `Bean`
Spring boot auto detects Beans of type `com.fasterxml.jackson.databind.Module` and registers it with
the auto configured ObjectMapper. A `SimpleModule` was containing the custom geometry serializers
was defined as a `Bean` 

The problem was that Hibernate's module consistently took precedence over our Module at runtime
leading to continual serialization errors. An attempt to change the precedence was made using 
`@Order` priorities, however the issue could not be resolved.

### 5. `@JsonSerialize` on Model Getters
The `@JsonSerialize` annotation was used in an attempt to use the right serializer as the annotation
instructs Jackson to use a specific serializer regardless of which module is registered. 

e.g.
```java
@JsonSerialize(using = PointSerializer.class)
```

It was discovered that Hibernate's interference occured at a lower level than Jackson's annotation.
processing meaning the annotation was ignored.

### Final Solution

The solution was creating a dedicated ObjectMapper instance within the NetworkController that only
registers the custom serializers. The mapper is created via `createGeometryMapper()` and reused
across all endpoints. 

```java
private ObjectMapper createGeometryMapper() {
    ObjectMapper mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule();
    module.addSerializer(Point.class, new PointSerializer());
    module.addSerializer(LineString.class, new LineStringSerializer());
    module.addSerializer(MultiLineString.class, new MultiLineStringSerializer());
    mapper.registerModule(module);
    return mapper;
}
```

As this mapper is constructed without Hibernate's module being registered, there is no conflict. 
The mapper handles all standard Java Types and three custom serializers. 

The custom serializers extend StdSerializer and produce a GeoJSON compliant output. The appendix A 
of [this](https://datatracker.ietf.org/doc/html/rfc7946#appendix-A) outlines the format.
