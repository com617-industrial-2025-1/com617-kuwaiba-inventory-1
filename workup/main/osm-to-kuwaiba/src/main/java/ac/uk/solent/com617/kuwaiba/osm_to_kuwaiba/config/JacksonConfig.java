package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * Configuration class removes the need for duplication of the object mapper
 * in different controllers or models.
 * 
 * 
 * 
 * 
 * 
 * Dependency Injection principle
 */

@Configuration
public class JacksonConfig {

	// Autowiring Spring boots existing object mapper
	@Autowired
	private ObjectMapper objectMapper;
	
	// Post Construct as then its guaranteed the mapper is available
	@PostConstruct
	public void addJtsModule() {
		objectMapper.registerModule(new JtsModule());
	}
}