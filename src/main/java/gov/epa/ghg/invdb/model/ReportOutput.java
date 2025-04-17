package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import gov.epa.ghg.invdb.rest.dto.ReportOutputDto;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "report_output")
@NamedNativeQuery(name = "get_rptop_with_percent_dets", query = "select ro.report_output_id \"reportOutputId\", ro.report_row_id \"reportRowId\", "
                + "ro.report_output_year_id \"reportOutputYearId\", ro.report_output_value \"reportOutputValue\", "
                + "roec.start_year \"startYear\", roec.end_year \"endYear\", roec.start_to_end_percent \"startToEndPercent\", "
                + "roec.end_year_allemissions_percent \"endYearAllEmissionsPercent\" "
                + "from ggds_invdb.report_output ro "
                + "join ggds_invdb.dim_report_row drr ON drr.report_row_id = ro.report_row_id "
                + "left join ggds_invdb.report_output_emission_change roec ON roec.report_row_id = ro.report_row_id "
                + "where drr.report_id = :reportId ", resultSetMapping = "rptop_with_percent_dets")
@SqlResultSetMapping(name = "rptop_with_percent_dets", classes = @ConstructorResult(targetClass = ReportOutputDto.class, columns = {
                @ColumnResult(name = "reportOutputId", type = Long.class),
                @ColumnResult(name = "reportRowId", type = Long.class),
                @ColumnResult(name = "reportOutputYearId", type = Integer.class),
                @ColumnResult(name = "reportOutputValue", type = Float.class),
                @ColumnResult(name = "startYear", type = Integer.class),
                @ColumnResult(name = "endYear", type = Integer.class),
                @ColumnResult(name = "startToEndPercent", type = Float.class),
                @ColumnResult(name = "endYearAllEmissionsPercent", type = Float.class)
}))
public class ReportOutput implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "report_output_id")
        private Long reportOutputId;

        @Column(name = "report_row_id")
        private Long reportRowId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "report_row_id", insertable = false, updatable = false)
        private DimReportRow reportRow;

        @Column(name = "report_output_year_id")
        private Integer reportOutputYearId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "report_output_year_id", insertable = false, updatable = false)
        private DimTimeSeries reportOutputYear;

        @Column(name = "report_output_value")
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

}
