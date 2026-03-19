package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.services;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository.NetworkConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoutingService {

    @Autowired
    private NetworkConnectionRepository connectionRepository;

    @Transactional
    public String runFullPrediction() {
        connectionRepository.deleteAll(); // clears all previous connections

        exchangeToAggregatorPrediction();
        aggregatorToCabinetPrediction();
        cabinetToPolePrediction();
        poleToBuildingPrediction();

        return "Routing Prediction Completed.";
    }

    public void exchangeToAggregatorPrediction() {
        connectionRepository.insertDropConnections();
    }

    public void aggregatorToCabinetPrediction() {
        connectionRepository.insertFeederConnections();
    }

    public void cabinetToPolePrediction() {
        connectionRepository.insertDistributionConnections();
    }

    public void poleToBuildingPrediction() {
        connectionRepository.insertTrunkConnections();
    }

}
