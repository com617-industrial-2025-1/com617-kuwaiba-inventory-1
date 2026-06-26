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
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/overpass")
public class OverpassImportController {

    @Autowired
    private OverpassImportService importService;

    @Operation(summary = "Import OSM data from Overpass API for a given bounding box. Used by bounding box page to import data for the selected area.")
    @PostMapping("/import")
    public ResponseEntity<OverpassImportResult> importBoundingBox(
            @RequestParam Double south,
            @RequestParam Double west,
            @RequestParam Double north,
            @RequestParam Double east) throws Exception {

        OverpassBoundingBox bbox = new OverpassBoundingBox();
        bbox.setNorth(north);
        bbox.setSouth(south);
        bbox.setEast(east);
        bbox.setWest(west);
        
        OverpassImportResult result = importService.importBoundingBox(bbox);
        return ResponseEntity.ok(result);
    }
}
