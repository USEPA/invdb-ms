package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import org.hibernate.annotations.DynamicUpdate;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@DynamicUpdate
@Table(name = "dim_source_name")
@SequenceGenerator(name = "dim_source_name_seq_gen", sequenceName = "dim_source_name_source_id_seq", allocationSize = 1)

@NamedNativeQuery(name = "check_template_3_dim_source_name", query = "SELECT dsn.source_name_id, dsn.source_name, dsn.pub_year_id FROM ggds_invdb.dim_source_name dsn "
                + "LEFT JOIN ggds_invdb.dim_category category ON dsn.category_id = category.category_id "
                + "LEFT JOIN ggds_invdb.dim_publication_year pubyear ON dsn.pub_year_id = pubyear.pub_year_id "
                + "WHERE "
                + " category.category_name = :category "
                + "AND (dsn.sub_category_1 = :subcategory1 OR dsn.sub_category_1 IS NULL) "
                + "AND dsn.layer_id = :layerId "
                + "AND pubyear.pub_year = :rptYr", resultSetMapping = "source_name_dto")
@SqlResultSetMapping(name = "source_name_dto", classes = @ConstructorResult(targetClass = DimSourceNameDto.class, columns = {
                @ColumnResult(name = "source_name_id", type = Integer.class),
                @ColumnResult(name = "source_name", type = String.class),
                @ColumnResult(name = "pub_year_id", type = Integer.class)
}))
public class DimSourceName implements Serializable {

	private static final long serialVersionUID = 1L;

		@Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dim_source_name_seq_gen")
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
        
        @Column(name = "sub_category_1")
        private String subCategory1;

        @ManyToOne
    	@JoinColumn(name = "category_id")
    	private DimCategory dimCategory;
	
		@OneToMany(mappedBy = "dimSourceName")
		private java.util.Set<DimEmissionsQcLoadTarget> emissionsQcLoadTargets;
}
