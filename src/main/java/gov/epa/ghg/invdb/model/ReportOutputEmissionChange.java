package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "report_output_emission_change")
public class ReportOutputEmissionChange implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "emission_change_id")
        private Long emissionChangeId;

        @Column(name = "report_row_id")
        private Long reportRowId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "report_row_id", insertable = false, updatable = false)
        private DimReportRow reportRow;

        @Column(name = "start_year")
        private Integer startYear;

        @Column(name = "mid_year")
        private Integer midYear;

        @Column(name = "end_year")
        private Integer endYear;

        @Column(name = "start_to_mid_absolute")
        private Float startToMidAbsolute;

        @Column(name = "start_to_mid_percent")
        private Float startToMidPercent;

        @Column(name = "start_to_end_absolute")
        private Float startToEndAbsolute;

        @Column(name = "start_to_end_percent")
        private Float startToEndPercent;

        @Column(name = "mid_to_end_absolute")
        private Float midToEndAbsolute;

        @Column(name = "mid_to_end_percent")
        private Float midToEndPercent;

        @Column(name = "previous_to_end_absolute")
        private Float prevToEndAbsolute;

        @Column(name = "previous_to_end_percent")
        private Float prevToEndPercent;

        @Column(name = "start_to_mid_oftotal_percent")
        private Float startToMidOfTotalPercent;

        @Column(name = "start_to_end_oftotal_percent")
        private Float startToEndOfTotalPercent;

        @Column(name = "mid_to_end_oftotal_percent")
        private Float midToEndOfTotalPercent;

        @Column(name = "previous_to_end_oftotal_percent")
        private Float prevToEndOfTotalPercent;

        @Column(name = "start_to_mid_ofgas_percent")
        private Float startToMidOfGasPercent;

        @Column(name = "start_to_end_ofgas_percent")
        private Float startToEndOfGasPercent;

        @Column(name = "mid_to_end_ofgas_percent")
        private Float midToEndOfGasPercent;

        @Column(name = "previous_to_end_ofgas_percent")
        private Float prevToEndOfGasPercent;

        @Column(name = "end_year_gas_percent")
        private Float endYrGasPercent;

        @Column(name = "end_year_allemissions_percent")
        private Float endYrAllEmissionsPercent;

        @Column(name = "LAST_UPDATED_DATE")
        @Temporal(TemporalType.TIMESTAMP)
        private java.util.Date lastUpdatedDate;

        @Column(name = "LAST_UPDATED_BY")
        private Integer lastUpdatedBy;

        @ManyToOne
        @JoinColumn(name = "LAST_UPDATED_BY", insertable = false, updatable = false)
        private User reportLastUpdateUser;

}
