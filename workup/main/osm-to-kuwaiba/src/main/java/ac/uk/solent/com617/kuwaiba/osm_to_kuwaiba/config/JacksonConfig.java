package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config;


import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


/*
 * Configuration class removes the need for duplication of the object mapper
 * in different controllers or models.
 */

@Configuration
public class JacksonConfig {
	
	@Bean
	@Order(1)
	public Module geometryModule() {
		SimpleModule module = new SimpleModule(
		        "GeometryModule",
		        new Version(1, 0, 0, null, null, null),
		        null,
		        null
		    );
		module.addSerializer(new PointSerializer());
        module.addSerializer(new LineStringSerializer());
        module.addSerializer(new MultiLineStringSerializer());
        return module;
	}
	
	/*
	@Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(new PointSerializer());
        module.addSerializer(new LineStringSerializer());
        module.addSerializer(new MultiLineStringSerializer());
        mapper.registerModule(module);
        return mapper;
    }
	*/
	
	
	
	/* 
	// Spring automaticallly uses new object mapper
	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JtsModule());
		return mapper;
	}
	*/
	
	/* DOES NOT AUTOMATICALLY REGISTER THE MODULE BEAN WITH OBJECT MAPPER
	// Spring boot may automatically register any module bean it finds with object mapper
	// Now spring boot should automatically serialize geometry objects
	@Bean
	public JtsModule jtsModule() {
		return new JtsModule();
	}
	*/
	
	
	/* OBJECT MAPPER CLASHES WITH DEFAULT SPRING BOOT OBJECT MAPPER
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JtsModule());
		return objectMapper;
	}
	*/
	
	/* Jackson2ObjectMapperBuilder is deprecated
	@Bean
    @Primary // tells spring that this is the object mapper to use everywhere
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper mapper = builder.build();
        mapper.registerModule(new JtsModule());
        return mapper;
    }
    */
    
    
	
	/* ObjectMapper may not be exposed as an injectable bean
	// Autowiring Spring boots existing object mapper
	@Autowired
	private ObjectMapper objectMapper;
	
	// Post Construct as then its guaranteed the mapper is available
	@PostConstruct
	public void addJtsModule() {
		objectMapper.registerModule(new JtsModule());
	}
	*/
}