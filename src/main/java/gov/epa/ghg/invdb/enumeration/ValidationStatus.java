package gov.epa.ghg.invdb.enumeration;

public enum ValidationStatus {
    SUCCESS("success"),
    FAILED("failed");

    private final String value;

    private ValidationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}