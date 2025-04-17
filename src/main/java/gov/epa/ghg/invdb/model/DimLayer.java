package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import gov.epa.ghg.invdb.rest.dto.DimLayerDto;
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
import lombok.Data;

@Data
@Entity
@Table(name = "dim_layer")
@NamedNativeQuery(name = "get_layers", query = "SELECT dl.layer_id, dl.layer_name, dl.display_name, "
        + "dl.default_year, dpy.pub_year_id "
        + "FROM ggds_invdb.dim_layer dl "
        + "JOIN ggds_invdb.dim_publication_year dpy on dl.default_year = dpy.pub_year ", resultSetMapping = "dim_layer_dto")
@SqlResultSetMapping(name = "dim_layer_dto", classes = @ConstructorResult(targetClass = DimLayerDto.class, columns = {
        @ColumnResult(name = "layer_id", type = Integer.class),
        @ColumnResult(name = "layer_name", type = String.class),
        @ColumnResult(name = "display_name", type = String.class),
        @ColumnResult(name = "default_year", type = Integer.class),
        @ColumnResult(name = "pub_year_id", type = Integer.class)
}))
public class DimLayer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "layer_id")
    private Integer id;

    @Column(name = "layer_name")
    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "default_year")
    private Integer defaultYear;
}
