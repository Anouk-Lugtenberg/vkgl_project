package org.molgenis.vkgl.model;

public class ReferenceAndAlternative {
    private final String reference;
    private final String alternative;

    public ReferenceAndAlternative(String reference, String alternative) {
        this.reference = reference;
        this.alternative = alternative;
    }

    public String getReference() {
        return reference;
    }

    public String getAlternative() {
        return alternative;
    }
}
