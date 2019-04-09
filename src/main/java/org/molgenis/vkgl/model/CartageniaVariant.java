package org.molgenis.vkgl.model;

public class CartageniaVariant extends VCFVariant {
    String timestamp;
    String id;
    String variantType;
    String location;
    String effect;
    String lastUpdatedOn;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVariantType() {
        return variantType;
    }

    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(String lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public void setClassification(String classification) {
        switch (classification) {
            case "BENIGN":
                this.classification = ClassificationTypes.BENIGN;
                break;
            case "LIKELY_BENIGN":
                this.classification = ClassificationTypes.LIKELY_BENIGN;
                break;
            case "VOUS":
                this.classification = ClassificationTypes.VOUS;
                break;
            case "LIKELY_PATHOGENIC":
                this.classification = ClassificationTypes.LIKELY_PATHOGENIC;
                break;
            case "PATHOGENIC":
                this.classification = ClassificationTypes.PATHOGENIC;
                break;
        }
    }
}
