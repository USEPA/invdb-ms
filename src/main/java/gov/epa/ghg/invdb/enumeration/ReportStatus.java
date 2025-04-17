package gov.epa.ghg.invdb.enumeration;

public enum ReportStatus {
    READY("Ready"), PROCESSING_QUERIES("Processing queries"), PROCESSING_DATA("Processing data"), PROCESSING_TOTALS("Processing totals"), ERROR("Error");

    private final String value;

    private ReportStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
