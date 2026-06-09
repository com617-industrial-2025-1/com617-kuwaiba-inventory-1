package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfiguration {

   @Bean
   public OpenAPI defineOpenApi() {
       Server server = new Server();
       server.setUrl("http://localhost:8080");
       server.setDescription("OSM to Kuwaiba API Server");

       Contact myContact = new Contact();
       myContact.setName("Craig Gallen");
       myContact.setUrl("https://github.com/gallenc");


       Info information = new Info()
               .title("Open Street Map to Kuwaiba Importer")
               .version("0.1")
               .description("This API exposes endpoints to generate a Kuwaiba configuration.")
               .contact(myContact);
       return new OpenAPI().info(information).servers(List.of(server));
   }
}