package gov.epa.ghg.invdb.enumeration;

public enum ArchiveObjectType {
    DATAFILE("datafile"),
    REPORT("report"),
    PUBLICATION("publication");

    private final String value;

    private ArchiveObjectType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
