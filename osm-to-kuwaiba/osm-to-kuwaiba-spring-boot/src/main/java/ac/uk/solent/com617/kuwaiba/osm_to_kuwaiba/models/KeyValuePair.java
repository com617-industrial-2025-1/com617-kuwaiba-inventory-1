package ac.uk.solent.com617.kuwaiba.osm_to_kuwaiba.models;

import jakarta.persistence.Embeddable;

@Embeddable
public class KeyValuePair {
    private String value;

    public KeyValuePair() {}
    public KeyValuePair(String value) { this.value = value; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}