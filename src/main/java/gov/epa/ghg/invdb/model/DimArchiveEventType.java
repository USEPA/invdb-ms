package gov.epa.ghg.invdb.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gov.epa.ghg.invdb.rest.dto.ArchiveEventDto;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_archive_event_type")
@NamedNativeQuery(name = "get_archive_events", query = "select daet.*, ap.archive_package_id "
                + "from ggds_invdb.dim_archive_event_type daet "
                + "left join ggds_invdb.archive_package ap on ap.event_type_id = daet.event_type_id "
                + "and ap.layer_id = :layer and ap.reporting_year = :year and DATE(ap.last_created_date) = CURRENT_DATE", resultSetMapping = "ArchiveEventDTOMapping")
@SqlResultSetMapping(name = "ArchiveEventDTOMapping", classes = {
                @ConstructorResult(targetClass = ArchiveEventDto.class, columns = {
                                @ColumnResult(name = "event_type_id", type = Integer.class),
                                @ColumnResult(name = "event_name", type = String.class),
                                @ColumnResult(name = "event_abbr", type = String.class),
                                @ColumnResult(name = "archive_package_id", type = Integer.class)
                })
})
public class DimArchiveEventType implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "event_type_id")
        private Integer eventTypeId;

        @Column(name = "event_name")
        private String eventName;

        @Column(name = "event_abbr")
        private String eventAbbr;

        @JsonIgnore
        @OneToMany(mappedBy = "packageEventType")
        private java.util.Set<ArchivePackage> archivePkgEventTypes;
}
