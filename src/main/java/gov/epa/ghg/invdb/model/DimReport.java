package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.epa.ghg.invdb.rest.dto.ReportWithTabDetails;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_report")
@NamedNativeQuery(name = "get_report_with_tab_details", query = "select r.report_id \"reportId\", r.report_name \"reportName\", "
                + "r.report_title \"reportTitle\", r.report_rows_header \"reportRowsHeader\", r.report_refresh_date \"reportRefreshDate\", "
                + "r.refresh_status \"refreshStatus\", "
                + "json_agg(json_build_object('tabName', e.tab_name, 'tabLabel', e.tab_label) ORDER BY ut.ordinality) \"tabs\" "
                + "from ggds_invdb.dim_report r "
                + "CROSS JOIN LATERAL unnest(r.tabs) WITH ORDINALITY AS ut(tab_id, ordinality) "
                + "JOIN ggds_invdb.dim_report_tab_details e  ON e.tab_id = ut.tab_id "
                + "where layer_id = :layerId and reporting_year = :rptYr "
                + "group by r.report_id, r.report_name, r.report_title, r.report_rows_header, r.report_refresh_date, r.refresh_status "
                + "order by r.report_title ", resultSetMapping = "report_with_tab_details")
@SqlResultSetMapping(name = "report_with_tab_details", classes = @ConstructorResult(targetClass = ReportWithTabDetails.class, columns = {
                @ColumnResult(name = "reportId", type = Integer.class),
                @ColumnResult(name = "reportName", type = String.class),
                @ColumnResult(name = "reportTitle", type = String.class),
                @ColumnResult(name = "reportRowsHeader", type = String.class),
                @ColumnResult(name = "reportRefreshDate", type = Date.class),
                @ColumnResult(name = "refreshStatus", type = String.class),
                @ColumnResult(name = "tabs", type = String.class)
}))
public class DimReport implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "report_id")
        private Integer reportId;

        @Column(name = "report_name")
        private String reportName;

        @Column(name = "report_title")
        private String reportTitle;

        @Column(name = "report_rows_header")
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
        private java.util.Set<DimReportRow> reportRows;

        @Column(name = "reporting_year")
        private Integer reportingYear;

        @Column(name = "layer_id")
        private Integer layerId;
}
