package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "qc_comp_report_output")
public class QcCompReportOutput implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "qc_report_output_id")
        private Long reportOutputId;

        @Column(name = "qc_report_row_id")
        private Long reportRowId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "qc_report_row_id", insertable = false, updatable = false)
        private DimQcCompReportRow reportRow;

        @Column(name = "qc_report_output_year_id")
        private Integer reportOutputYearId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "qc_report_output_year_id", insertable = false, updatable = false)
        private DimTimeSeries reportOutputYear;

        @Column(name = "qc_report_output_value")
        private Float reportOutputValue;

        @Column(name = "CREATED_DATE")
        @Temporal(TemporalType.TIMESTAMP)
        private java.util.Date createdDate;

        @Column(name = "CREATED_BY")
        private Integer createdBy;

        @Column(name = "LAST_UPDATED_DATE")
        @Temporal(TemporalType.TIMESTAMP)
        private java.util.Date lastUpdatedDate;

        @Column(name = "LAST_UPDATED_BY")
        private Integer lastUpdatedBy;

        @ManyToOne
        @JoinColumn(name = "LAST_UPDATED_BY", insertable = false, updatable = false)
        private User reportLastUpdateUser;

        @ManyToOne(optional = false)
        @JoinColumn(name = "state_id", insertable = false, updatable = false)
        private DimState reportOutputState;

}
