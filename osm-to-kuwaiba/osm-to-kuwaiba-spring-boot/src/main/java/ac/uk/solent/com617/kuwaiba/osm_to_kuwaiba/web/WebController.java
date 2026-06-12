package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Web UI controller for the Overpass importer map interface.
 */
@Controller
public class WebController {

    /**
     * Serves the interactive map UI for bbox-based Overpass import.
     *
     * @return Redirect to the map HTML file.
     */
    @RequestMapping("/")
    public String index() {
        return "redirect:/map.html";
    }

    /**
     * Redirect to Swagger API documentation for reference.
     *
     * @return Redirect to Swagger UI.
     */
    @RequestMapping("/api-docs")
    public String apiDocs() {
        return "redirect:/swagger-ui.html";
    }

}