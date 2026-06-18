package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

public class OverpassImportResult {

   Integer importedBuildings = null;
   Integer importedRoads = null;
   Integer deletedRawRows = null;
   Integer deletedDerivedRows = null;
   

   public Integer getImportedBuildings() {
      return importedBuildings;
   }

   public void setImportedBuildings(Integer importedBuildings) {
      this.importedBuildings = importedBuildings;
   }

   public Integer getImportedRoads() {
      return importedRoads;
   }

   public void setImportedRoads(Integer importedRoads) {
      this.importedRoads = importedRoads;
   }

   public Integer getDeletedRawRows() {
      return deletedRawRows;
   }

   public void setDeletedRawRows(Integer deletedRawRows) {
      this.deletedRawRows = deletedRawRows;
   }

   public Integer getDeletedDerivedRows() {
      return deletedDerivedRows;
   }

   public void setDeletedDerivedRows(Integer deletedDerivedRows) {
      this.deletedDerivedRows = deletedDerivedRows;
   }

}
