package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_query_formula")
public class DimQueryFormula implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "query_formula_id")
        private Integer queryFormulaId;

        @Column(name = "formula_type")
        private String formulaType;

        @Column(name = "formula_prefix")
        private String formulaPrefix;

        @Column(name = "parameters")
        private String parameters;

        @Column(name = "viewName")
        private String viewName;

        @OneToMany(mappedBy = "queryFormula")
        private java.util.Set<DimReportRow> reportRows;
}
