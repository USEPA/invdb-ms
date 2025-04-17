package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_qc_report")
public class DimQcReport implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "qc_report_id")
        private Integer reportId;

        @Column(name = "qc_report_name")
        private String reportName;

        @Column(name = "qc_report_title")
        private String reportTitle;

        @Column(name = "qc_report_rows_header")
        private String reportRowsHeader;

        @Column(name = "report_refresh_date")
        @Temporal(TemporalType.TIMESTAMP)
        private Date reportRefreshDate;
        
        @Column(name = "refresh_status")
        @Temporal(TemporalType.TIMESTAMP)
        private String refreshStatus;

        @Column(name = "created_date")
        @Temporal(TemporalType.TIMESTAMP)
        private Date createdDate;

        @Column(name = "created_by")
        private Integer createdBy;

        @Column(name = "last_updated_date")
        @Temporal(TemporalType.TIMESTAMP)
        private Date lastUpdatedDate;

        @Column(name = "last_updated_by")
        private Integer lastUpdatedBy;

        @JsonIgnore
        @OneToMany(mappedBy = "report", fetch = FetchType.LAZY)
        private java.util.Set<DimQcCompReportRow> reportRows;

        @Column(name = "reporting_year")
        private Integer reportingYear;

        @Column(name = "layer_id")
        private Integer layerId;
}
