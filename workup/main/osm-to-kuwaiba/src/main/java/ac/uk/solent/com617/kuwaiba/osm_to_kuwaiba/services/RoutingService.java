package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkConnectionRepository;

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
        connectionRepository.insertTrunkConnections();
    }

    public void aggregatorToCabinetPrediction() {
        connectionRepository.insertDistributionConnections();
    }

    public void cabinetToPolePrediction() {
        connectionRepository.insertFeederConnections();
    }

    public void poleToBuildingPrediction() {
        connectionRepository.insertDropConnections();
    }

}
