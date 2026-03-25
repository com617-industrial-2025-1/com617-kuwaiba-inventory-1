package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.services.ClusteringService;
import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.services.RoutingService;
import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.services.UprnService;

@RestController
@RequestMapping("/predict")
public class PredictionController {
	
	@Autowired
	private ClusteringService clusteringService;
	
	@Autowired
	private RoutingService routingService;
	
	@Autowired
	private UprnService uprnService;
	
	// run the full pipeline
	@PostMapping("/all")
	public String runAll() {
		clusteringService.runFullPrediction();
		return routingService.runFullPrediction();
	}
	
	// runs clustering service (placing poles, cabinets, aggregators, exchanges)
	@PostMapping("/clustering")
	public String runClustering() {
		return clusteringService.runFullPrediction();
	}
	
	// runs routing service (draws connections between points)
	@PostMapping("/routing")
	public String runRouting() {
		return routingService.runFullPrediction();
	}
	
	@PostMapping("/uprns")
	public String linkUprns() {
		return uprnService.linksUprns();
	}
	
	
	
}