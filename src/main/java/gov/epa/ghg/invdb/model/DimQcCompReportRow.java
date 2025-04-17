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
@Table(name = "dim_qc_comp_report_row")
public class DimQcCompReportRow implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "qc_report_row_id")
        private Long reportRowId;

        @Column(name = "qc_report_id")
        private Long reportId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "qc_report_id", insertable = false, updatable = false)
        private DimQcReport report;
                
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

        @Column(name = "emissions_query_formula_id")
        private Integer emissionsQueryFormulaId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "emissions_query_formula_id", insertable = false, updatable = false)
        private DimQueryFormula emissionsQueryFormula;

        @Column(name = "emissions_query_formula_parameters")
        @Convert(converter = ParamValueJsonConvertor.class)
        private Map<String, String> emissionsQueryFormulaParameters;

        @Column(name = "qc_query_formula_id")
        private Integer qcQueryFormulaId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "qc_query_formula_id", insertable = false, updatable = false)
        private DimQueryFormula qcQueryFormula;

        @Column(name = "qc_query_formula_parameters")
        @Convert(converter = ParamValueJsonConvertor.class)
        private Map<String, String> qcQueryFormulaParameters;

        @Column(name = "last_created_date")
        @Temporal(TemporalType.TIMESTAMP)
        private Date lastCreatedDate;

        @Column(name = "last_created_by")
        private Integer lastCreatedBy;

        @OneToMany(mappedBy = "reportRow")
        private java.util.Set<DimEmissionsQcCompLoadTarget> emissionsQcLoadTargets;

        @OneToMany(mappedBy = "reportRow")
        private java.util.Set<QcCompReportOutput> emissions;
}
