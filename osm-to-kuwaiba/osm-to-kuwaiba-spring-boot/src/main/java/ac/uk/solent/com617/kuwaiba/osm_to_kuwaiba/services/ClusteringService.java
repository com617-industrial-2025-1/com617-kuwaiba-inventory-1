package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services;

import org.entimoss.kuwaiba.provisioning.model.ProjectConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.BuildingDropPointRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.CleanedBuildingRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.CleanedRoadRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkConnectionRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkPointRepository;

@Service
public class ClusteringService {
   
   private String networkRegion = ProjectConstants.PARENT_LOCATION_VALUE;

    @Autowired
    private BuildingDropPointRepository dropPointRepository;

    @Autowired
    private CleanedRoadRepository roadRepository;

    @Autowired
    private CleanedBuildingRepository buildingRepository;

    @Autowired
    private NetworkPointRepository pointRepository;
    
    @Autowired
    private NetworkConnectionRepository networkRepository;

    @Transactional // all methods are treated as an all or nothing incase things go wrong.
    public String runFullPrediction() {
    	// Deleting the connections before the points
    	networkRepository.deleteAll();
        pointRepository.deleteAll(); // deleting for fresh start

        predictPoles();
        predictCabinets();
        predictAggregators();
        predictExchanges();

        return "Clustering Prediction Completed.";
    }

    public void predictPoles() {
        dropPointRepository.insertPoleClusters(networkRegion);
        dropPointRepository.updateBuildingParents();
    }

    public void predictCabinets() {
        pointRepository.insertCabinetClusters(networkRegion);
        pointRepository.updatePoleParents();
    }

    public void predictAggregators() {
        pointRepository.insertAggregatorClusters(networkRegion);
        pointRepository.updateCabinetParents();
    }

    public void predictExchanges() {
        pointRepository.insertExchangeClusters(networkRegion);
        pointRepository.updateAggregatorParents();
    }
}
