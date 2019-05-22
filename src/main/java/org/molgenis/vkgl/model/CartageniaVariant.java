package org.molgenis.vkgl.model;

public class CartageniaVariant extends RadboudVariant {
    String timestamp;
    String id;
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

    @Override
    public void setClassification(String classification) {
        switch (classification) {
            case "BENIGN":
                this.classification = ClassificationType.BENIGN;
                break;
            case "LIKELY_BENIGN":
                this.classification = ClassificationType.LIKELY_BENIGN;
                break;
            case "VOUS":
                this.classification = ClassificationType.VOUS;
                break;
            case "LIKELY_PATHOGENIC":
                this.classification = ClassificationType.LIKELY_PATHOGENIC;
                break;
            case "PATHOGENIC":
                this.classification = ClassificationType.PATHOGENIC;
                break;
            default:
                this.classification = ClassificationType.UNKNOWN_TYPE;
                break;
        }
    }

    @Override
    public void setVariantType(String variantType) {
        switch (variantType) {
            case "snp":
                this.variantType = VariantType.SNP;
                break;
            case "insertion":
                this.variantType = VariantType.INSERTION;
                break;
            case "deletion":
                this.variantType = VariantType.DELETION;
                break;
            // They've called these kind of variants substitutions, but for clarification it's called a deletion_insertion here.
            // One (or multiple bases) are substituted by other base(s).
            case "substitution":
                this.variantType = VariantType.DELETION_INSERTION;
                break;
            default:
                this.variantType = VariantType.NOT_CLASSIFIED;
                break;
        }
    }
}
