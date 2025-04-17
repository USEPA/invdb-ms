package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import gov.epa.ghg.invdb.rest.dto.DimSourceNameDto;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_source_name")
@NamedNativeQuery(name = "check_template_3_dim_source_name", query = "SELECT dsn.source_name_id, dsn.source_name, dsn.pub_year_id FROM ggds_invdb.dim_source_name dsn "
                + "LEFT JOIN ggds_invdb.dim_category category ON dsn.category_id = category.category_id "
                + "LEFT JOIN ggds_invdb.dim_subsector subsector ON category.subsector_id = subsector.subsector_id "
                + "LEFT JOIN ggds_invdb.dim_sector sector ON subsector.sector_id = sector.sector_id "
                + "LEFT JOIN ggds_invdb.dim_publication_year pubyear ON dsn.pub_year_id = pubyear.pub_year_id "
                + "WHERE sector.sector_name = :sector "
                + "AND subsector.subsector_name = :subsector "
                + "AND category.category_name = :category "
                + "AND (dsn.sub_category_1 = :subcategory1 OR dsn.sub_category_1 IS NULL) "
                + "AND dsn.layer_id = :layerId "
                + "AND pubyear.pub_year = :rptYr "
                + "ORDER BY subsector.subsector_id DESC;", resultSetMapping = "source_name_dto")
@SqlResultSetMapping(name = "source_name_dto", classes = @ConstructorResult(targetClass = DimSourceNameDto.class, columns = {
                @ColumnResult(name = "source_name_id", type = Integer.class),
                @ColumnResult(name = "source_name", type = String.class),
                @ColumnResult(name = "pub_year_id", type = Integer.class)
}))
public class DimSourceName implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "source_name_id")
        private Integer id;

        @Column(name = "source_name")
        private String name;

        @Column(name = "layer_id")
        private Integer layerId;

        @ManyToOne
        @JoinColumn(name = "pub_year_id")
        private DimPublicationYear pubYear;

        @ManyToOne
        @JoinColumn(name = "sector_id")
        private DimSector sector;

        @OneToMany(mappedBy = "sourceName")
        private java.util.Set<SourceFile> sourceFiles;

        @OneToMany(mappedBy = "dimSourceName")
        private java.util.Set<DimEmissionsQcLoadTarget> emissionsQcLoadTargets;
}
