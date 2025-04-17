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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_emissionsqc_load_target")
public class DimEmissionsQcLoadTarget implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "emissionsqc_load_target_id")
        private Integer emissionsQcLoadTargetId;

        @Column(name = "source_name_id")
        private Integer sourceNameId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "source_name_id", insertable = false, updatable = false)
        private DimSourceName dimSourceName;

        @Column(name = "reporting_year")
        private Integer reportingYear;

        @Column(name = "layer_id")
        private Integer layerId;

        @Column(name = "target_tab")
        private String targetTab;

        @Column(name = "row_title_cell")
        private String rowTitleCell;

        @Column(name = "anticipated_row_title")
        private String anticipatedRowTitle;

        @Column(name = "data_ref_1990")
        private String dataRef1990;

        @Column(name = "emission_parameters")
        @Convert(converter = ParamValueJsonConvertor.class)
        private Map<String, String> emissionParameters;

        @Column(name = "report_row_id")
        private Integer reportRowId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "report_row_id", insertable = false, updatable = false)
        private DimReportRow reportRow;

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
}
