package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

public record OverpassImportResult(
        int importedBuildings,
        int importedRoads,
        int deletedRawRows,
        int deletedDerivedRows) {
}
