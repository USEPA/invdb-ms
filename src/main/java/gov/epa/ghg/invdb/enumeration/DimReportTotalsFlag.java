package gov.epa.ghg.invdb.enumeration;

public enum DimReportTotalsFlag {
    SUBTOTAL("Subtotal"),
    GROSS_TOTAL("Gross"),
    NET_TOTAL("Net");

    private final String value;

    private DimReportTotalsFlag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
