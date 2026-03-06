package com.com617.spring.osm;


import com.bedatadriven.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//https://stackoverflow.com/questions/29884324/geometry-from-vividsolutions-jts-fails-when-creating-json
// We need to register the JtsModule to handle JTS geometry types in JSON serialization
// this is not working 
@Configuration
public class JacksonConfig {
    @Bean
    public JtsModule jtsModule() {
        return new JtsModule();
    }
}


