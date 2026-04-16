package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

public enum LinkType {
    TRUNK, // Exchange to Aggregator
    DISTRIBUTION, // Aggregator to Cabinets
    FEEDER, // Cabinets to Poles
    DROP // Poles to Buildings
}
