package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.BuildingDropPointRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.CleanedBuildingRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.CleanedRoadRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkPointRepository;

@Service
public class ClusteringService {

    @Autowired
    private BuildingDropPointRepository dropPointRepository;

    @Autowired
    private CleanedRoadRepository roadRepository;

    @Autowired
    private CleanedBuildingRepository buildingRepository;

    @Autowired
    private NetworkPointRepository pointRepository;

    @Transactional // all methods are treated as an all or nothing incase things go wrong.
    public String runFullPrediction() {
        pointRepository.deleteAll(); // deleting for fresh start

        predictPoles();
        predictCabinets();
        predictAggregators();
        predictExchanges();

        return "Clustering Prediction Completed.";
    }

    public void predictPoles() {
        dropPointRepository.insertPoleClusters();
        dropPointRepository.updateBuildingParents();
    }

    public void predictCabinets() {
        pointRepository.insertCabinetClusters();
        pointRepository.updatePoleParents();
    }

    public void predictAggregators() {
        pointRepository.insertAggregatorClusters();
        pointRepository.updateCabinetParents();
    }

    public void predictExchanges() {
        pointRepository.insertExchangeClusters();
        pointRepository.updateAggregatorParents();
    }
}
