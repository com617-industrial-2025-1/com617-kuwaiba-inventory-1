package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.services;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository.CleanedBuildingRepository;
import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository.NetworkConnectionRepository;
import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository.NetworkPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoutingService {

    @Autowired
    private NetworkConnectionRepository connectionRepository;

    @Autowired
    private NetworkPointRepository pointRepository;

    @Autowired
    private CleanedBuildingRepository buildingRepository;

    @Transactional
    public String runFullPrediction() {
        connectionRepository.deleteAll();

        exchangeToAggregatorPrediction();
        aggregatorToCabinetPrediction();
        cabinetToPolePrediction();
        poleToBuildingPrediction();

        return "Routing Prediction Completed.";
    }

    public void exchangeToAggregatorPrediction() {

    }

    public void aggregatorToCabinetPrediction() {

    }

    public void cabinetToPolePrediction() {

    }

    public void poleToBuildingPrediction() {

    }

}
