package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import gov.epa.ghg.invdb.util.ParamValueJsonConvertor;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_report_row")
public class DimReportRow implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "report_row_id")
        private Long reportRowId;

        @Column(name = "report_id")
        private Long reportId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "report_id", insertable = false, updatable = false)
        private DimReport report;

        @Column(name = "row_order")
        private Integer rowOrder;

        @Column(name = "row_group")
        private String rowGroup;

        @Column(name = "row_subgroup")
        private String rowSubgroup;

        @Column(name = "row_title")
        private String rowTitle;

        @Column(name = "totals_flag")
        private String totalsFlag;

        @Column(name = "exclude_flag")
        private String excludeFlag;

        @Column(name = "query_formula_id")
        private Integer queryFormulaId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "query_formula_id", insertable = false, updatable = false)
        private DimQueryFormula queryFormula;

        @Column(name = "query_formula_parameters")
        @Convert(converter = ParamValueJsonConvertor.class)
        private Map<String, String> queryFormulaParameters;

        @Column(name = "last_created_date")
        @Temporal(TemporalType.TIMESTAMP)
        private Date lastCreatedDate;

        @Column(name = "last_created_by")
        private Integer lastCreatedBy;

        @OneToMany(mappedBy = "reportRow")
        private java.util.Set<DimEmissionsQcLoadTarget> emissionsQcLoadTargets;

        @OneToMany(mappedBy = "reportRow")
        private java.util.Set<ReportOutput> emissions;

        @OneToMany(mappedBy = "reportRow")
        private java.util.Set<ReportOutputEmissionChange> roEmissionChanges;
}
