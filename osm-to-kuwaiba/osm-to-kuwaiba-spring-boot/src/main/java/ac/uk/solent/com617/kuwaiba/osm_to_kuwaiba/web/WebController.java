package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Root context redirection to OpenAPI API Specification (OAS) documentation.
 */
@Controller
public class WebController {

    /**
     * Sets the index page mapping to point to the Swagger UI.
     *
     * @return A redirect to the Swagger UI.
     */
    @RequestMapping("/")
    public String index() {
        return "redirect:swagger-ui.html";
    }

}