package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.OverpassBoundingBox;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models.OverpassImportResult;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services.OverpassImportService;

@RestController
@RequestMapping("/overpass")
public class OverpassImportController {

    @Autowired
    private OverpassImportService importService;

    @PostMapping("/import")
    public ResponseEntity<OverpassImportResult> importBoundingBox(
            @RequestParam double south,
            @RequestParam double west,
            @RequestParam double north,
            @RequestParam double east) throws Exception {

        OverpassBoundingBox bbox = new OverpassBoundingBox(south, west, north, east);
        OverpassImportResult result = importService.importBoundingBox(bbox);
        return ResponseEntity.ok(result);
    }
}
