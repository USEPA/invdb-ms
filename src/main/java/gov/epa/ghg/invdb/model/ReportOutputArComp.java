package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import gov.epa.ghg.invdb.rest.dto.ReportOutputArCompDto;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "report_output_ar_comp")
@NamedNativeQuery(name = "get_ar_comp_dets_refresh", query = "select report_row_id \"reportRowId\", "
                + "report_output_year_id \"reportOutputYearId\", report_output_value \"reportOutputValue\", "
                + "difference_value \"differenceValue\", percent_value \"percentValue\" "
                + "from (select * from ggds_invdb.em_rpt_output_populate_ar_comp(:response, :gwpColumn, :userId)) AS res ", resultSetMapping = "ar_comp_dets")
@NamedNativeQuery(name = "get_ar_comp_dets", query = "select ro.report_row_id \"reportRowId\", "
                + "ro.report_output_year_id \"reportOutputYearId\", ro.report_output_value \"reportOutputValue\", "
                + "ro.difference_value \"differenceValue\", ro.percent_value \"percentValue\" "
                + "from ggds_invdb.report_output_ar_comp ro "
                + "JOIN ggds_invdb.dim_report_row drr on ro.report_row_id = drr.report_row_id "
                + "JOIN ggds_invdb.dim_report dr on drr.report_id = dr.report_id "
                + "where dr.report_id = :reportId and ro.gwp_column = :gwpColumn", resultSetMapping = "ar_comp_dets")
@SqlResultSetMapping(name = "ar_comp_dets", classes = @ConstructorResult(targetClass = ReportOutputArCompDto.class, columns = {
                @ColumnResult(name = "reportRowId", type = Long.class),
                @ColumnResult(name = "reportOutputYearId", type = Integer.class),
                @ColumnResult(name = "reportOutputValue", type = Float.class),
                @ColumnResult(name = "differenceValue", type = Float.class),
                @ColumnResult(name = "percentValue", type = Float.class)
}))
public class ReportOutputArComp implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "report_output_ar_comp_id")
        private Long reportOutputArCompId;

        @Column(name = "report_row_id")
        private Long reportRowId;

        @Column(name = "gwp_column")
        private String gwpColumn;

        @Column(name = "report_output_year_id")
        private Integer reportOutputYearId;

        @Column(name = "report_output_value")
        private Float reportOutputValue;

        @Column(name = "difference_value")
        private Float differenceValue;

        @Column(name = "percent_value")
        private Float percentValue;

        @Column(name = "LAST_UPDATED_DATE")
        @Temporal(TemporalType.TIMESTAMP)
        private java.util.Date lastUpdatedDate;

        @Column(name = "LAST_UPDATED_BY")
        private Integer lastUpdatedBy;
}
