package ac.uk.solent._7.kuwaiba.osm_to_kuwaiba.models;

public enum LinkType {
    TRUNK, // Exchange to Aggregator
    DISTRIBUTION, // Aggregator to Cabinets
    FEEDER, // Cabinets to Poles
    DROP // Poles to Buildings
}
