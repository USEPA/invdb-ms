package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import gov.epa.ghg.invdb.rest.dto.ReportOutputGwpCompDto;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_ghg")
@SequenceGenerator(name = "ghg_seq_gen", sequenceName = "dim_ghg_ghg_id_seq", allocationSize = 1)
@NamedNativeQuery(name = "get_ghg_comparison_dets", query = "select drr.report_row_id \"reportRowId\", "
                + "ar5f_gwp \"firstGwp\", "
                + "(ar5f_gwp - ar5_gwp)/nullif(ar5_gwp, 0) * 100 \"firstPercent\", "
                + "(ar5f_gwp - ar5_gwp) \"firstAbsolute\", "
                + "ar6_gwp \"secondGwp\", "
                + "(ar6_gwp - ar5_gwp)/nullif(ar5_gwp, 0) * 100  \"secondPercent\", "
                + "(ar6_gwp - ar5_gwp) \"secondAbsolute\" "
                + "from ggds_invdb.dim_ghg dg "
                + "join ggds_invdb.dim_report_row drr on drr.ghg_id = dg.ghg_id  "
                + "join ggds_invdb.dim_report dr on dr.report_id = drr.report_id "
                + "where dr.report_id = :reportId ", resultSetMapping = "ghg_comparison_dets")
@SqlResultSetMapping(name = "ghg_comparison_dets", classes = @ConstructorResult(targetClass = ReportOutputGwpCompDto.class, columns = {
                @ColumnResult(name = "reportRowId", type = Long.class),
                @ColumnResult(name = "firstGwp", type = Float.class),
                @ColumnResult(name = "firstPercent", type = Float.class),
                @ColumnResult(name = "firstAbsolute", type = Float.class),
                @ColumnResult(name = "secondGwp", type = Float.class),
                @ColumnResult(name = "secondPercent", type = Float.class),
                @ColumnResult(name = "secondAbsolute", type = Float.class),
}))
public class DimGhg implements BaseModel {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ghg_seq_gen")
        @Column(name = "ghg_id")
        private Integer id;

        @Column(name = "ghg_code")
        private String ghgCode;

        @Column(name = "ghg_longname")
        private String ghgLongname;

        @Column(name = "ghg_category_id")
        private Integer ghgCategoryId;

        @Column(name = "ar4_gwp")
        private Float ar4Gwp;

        @Column(name = "ar5_gwp")
        private Float ar5Gwp;

        @Column(name = "ar5f_gwp")
        private Float ar5fGwp;

        @Column(name = "ar6_gwp")
        private Float ar6Gwp;

        @Column(name = "ghg_formula")
        private String ghgFormula;

        @Column(name = "ghgrp_ghg_id")
        private Integer ghgrpGhgId;

        @Column(name = "ghg_shortname")
        private String ghgShortname;

        @Column(name = "cas_no")
        private String casNo;
}
