package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.config.ProjectConstants;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.BuildingDropPointRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.CleanedBuildingRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.CleanedRoadRepository;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.repository.NetworkConnectionRepository;
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
    
    @Autowired
    private NetworkConnectionRepository networkRepository;
    
    @Autowired
    private ProjectConstants projectConstantValues;

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
       String networkRegion = projectConstantValues.getParentLocationName();
        dropPointRepository.insertPoleClusters(networkRegion);
        dropPointRepository.updateBuildingParents();
    }

    public void predictCabinets() {
       String networkRegion = projectConstantValues.getParentLocationName();
        pointRepository.insertCabinetClusters(networkRegion);
        pointRepository.updatePoleParents();
    }

    public void predictAggregators() {
       String networkRegion = projectConstantValues.getParentLocationName();
        pointRepository.insertAggregatorClusters(networkRegion);
        pointRepository.updateCabinetParents();
    }

    public void predictExchanges() {
       String networkRegion = projectConstantValues.getParentLocationName();
        pointRepository.insertExchangeClusters(networkRegion);
        pointRepository.updateAggregatorParents();
    }
}
