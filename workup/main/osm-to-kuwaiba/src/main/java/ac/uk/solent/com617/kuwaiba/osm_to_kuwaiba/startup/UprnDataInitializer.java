package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services.UprnImportService;
import ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.services.UprnService;

@Component
public class UprnDataInitializer {
	
    private static final Logger logger = LoggerFactory.getLogger(UprnDataInitializer.class);
    
    @Autowired
    private UprnImportService uprnImportService;

    @Autowired
    private UprnService uprnService;
    
    @EventListener(ApplicationReadyEvent.class) // Runs as soon as spring boot runs and is ready.
    @Order(2)
    public void onStartup() throws Exception {
        uprnImportService.importUprns();
        uprnService.linksUprns();
        logger.info("UPRN IMPORT AND LINKING COMPLETE");
    }
    
}