package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

import jakarta.persistence.Embeddable;

@Embeddable
public class KeyValuePair {

    //no need of declaring key
    //key column will be created by MapKeyColumn

    private String value;

    //getter and setter methods
}