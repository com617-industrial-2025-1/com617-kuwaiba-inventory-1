package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.repository.LinkedBuildingRepository;

@Service
public class UprnService {
	
	@Autowired
    private LinkedBuildingRepository linkedBuildingRepository;

    @Transactional
    public String linksUprns() {
        linkedBuildingRepository.createLinkedBuildings();
        return "UPRN linking complete.";
    }
}