package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services.ClusteringService;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services.RoutingService;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services.UprnService;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/predict")
public class PredictionController {
	
	@Autowired
	private ClusteringService clusteringService;
	
	@Autowired
	private RoutingService routingService;
	
   @Operation(summary = "Run the full pipeline (clustering and routing)")
	@PostMapping("/all")
	public String runAll() {
		clusteringService.runFullPrediction();
		return routingService.runFullPrediction();
	}
	
	@Operation(summary = "Runs clustering service (placing poles, cabinets, aggregators, exchanges)" )
	@PostMapping("/clustering")
	public String runClustering() {
		return clusteringService.runFullPrediction();
	}
	
	@Operation(summary = "Runs the routing service (draws connections between points)")
	@PostMapping("/routing")
	public String runRouting() {
		return routingService.runFullPrediction();
	}
	
	
	
	
}