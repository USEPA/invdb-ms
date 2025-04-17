package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import java.util.Date;

import gov.epa.ghg.invdb.rest.dto.SourceFileDto;
import gov.epa.ghg.invdb.rest.dto.SourceFileSummaryDetailsDto;
import gov.epa.ghg.invdb.rest.dto.SourceFileSummaryDto;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "source_file")
@NamedNativeQuery(name = "get_source_files_by_layer_year", query = "select srcFile.source_file_id \"sourceFileId\", "
                + "srcFile.source_name_id \"sourceNameId\", srcName.source_name \"sourceName\", "
                + "sector.sector_name \"sectorName\", attachment.processed_date \"processedDate\", "
                + "attachment.attachment_id \"attachmentId\", attachment.attachment_name \"attachmentName\", "
                + "srcFile.reporting_year \"reportingYear\", userprofile.first_name || ' ' || userprofile.last_name AS \"loadedBy\", "
                + "attachment.created_date \"loadedDate\", "
                + "attachment.HAS_ERROR \"hasError\", srcFile.VALIDATION_STATUS \"validationStatus\", "
                + "srcFile.last_attachment_linked_date \"lastAttchLinkedDt\" "
                + "from ggds_invdb.source_file srcFile "
                + "join ggds_invdb.dim_source_name srcName on srcName.source_name_id = srcFile.source_name_id "
                + "left join ggds_invdb.source_file_attachment attachment on attachment.source_file_id = srcFile.source_file_id "
                + "     and attachment.last_srcfile_linked_date = srcFile.last_attachment_linked_date "
                + "left join ggds_invdb.dim_category category on category.category_id = srcName.category_id "
                + "left join ggds_invdb.dim_subsector subsector on subsector.subsector_id = category.subsector_id "
                + "left join ggds_invdb.dim_sector sector on sector.sector_id = subsector.sector_id "
                + "left join ggds_invdb.user_profile userprofile on userprofile.user_id = attachment.created_by "
                + "where srcFile.layer_id = :layerId and srcFile.reporting_year = :rptYr and srcFile.is_deleted is not true "
                + "order by srcFile.last_attachment_linked_date desc NULLS FIRST", resultSetMapping = "source_file_dto")
@SqlResultSetMapping(name = "source_file_dto", classes = @ConstructorResult(targetClass = SourceFileDto.class, columns = {
                @ColumnResult(name = "sourceFileId", type = Long.class),
                @ColumnResult(name = "sourceNameId", type = Integer.class),
                @ColumnResult(name = "sourceName", type = String.class),
                @ColumnResult(name = "sectorName", type = String.class),
                @ColumnResult(name = "processedDate", type = Date.class),
                @ColumnResult(name = "attachmentId", type = Long.class),
                @ColumnResult(name = "attachmentName", type = String.class),
                @ColumnResult(name = "reportingYear", type = Integer.class),
                @ColumnResult(name = "loadedBy", type = String.class),
                @ColumnResult(name = "loadedDate", type = Date.class),
                @ColumnResult(name = "hasError", type = Boolean.class),
                @ColumnResult(name = "validationStatus", type = String.class),
                @ColumnResult(name = "lastAttchLinkedDt", type = Date.class)
}))
@NamedNativeQuery(name = "get_sourcefile_summary", query = "select * from ggds_invdb.sourcefile_summary(:yearId, :layerId)", resultSetMapping = "SourceFileSummaryDTOMapping")
@SqlResultSetMapping(name = "SourceFileSummaryDTOMapping", classes = {
                @ConstructorResult(targetClass = SourceFileSummaryDto.class, columns = {
                                @ColumnResult(name = "missing_files", type = Integer.class),
                                @ColumnResult(name = "files_with_errors", type = Integer.class),
                                @ColumnResult(name = "files_without_errors", type = Integer.class),
                                @ColumnResult(name = "last_upload_date", type = Date.class),
                                @ColumnResult(name = "last_upload_by", type = String.class),
                                @ColumnResult(name = "last_processed_date", type = Date.class),
                                @ColumnResult(name = "last_processed_by", type = String.class)
                })
})
@NamedNativeQuery(name = "get_sourcefile_summary_details", query = "select * from ggds_invdb.sourcefile_summary_details(:yearId, :layerId)", resultSetMapping = "SourceFileSummaryDetailsDTOMapping")
@SqlResultSetMapping(name = "SourceFileSummaryDetailsDTOMapping", classes = {
                @ConstructorResult(targetClass = SourceFileSummaryDetailsDto.class, columns = {
                                @ColumnResult(name = "filetype", type = String.class),
                                @ColumnResult(name = "source_name", type = String.class),
                                @ColumnResult(name = "sector_name", type = String.class),
                                @ColumnResult(name = "last_upload_date", type = Date.class),
                                @ColumnResult(name = "last_upload_by", type = String.class),
                                @ColumnResult(name = "filename", type = String.class)
                })
})
public class SourceFile implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "source_file_id")
        private Long sourceFileId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "source_name_id", insertable = false, updatable = false)
        private DimSourceName sourceName;

        @Column(name = "source_name_id")
        private Integer sourceNameId;

        @Column(name = "UPLOAD_NOTES")
        private String uploadNotes;

        @Column(name = "reporting_year")
        private Integer reportingYear;

        @Column(name = "layer_id")
        private Integer layerId;

        @Column(name = "last_attachment_linked_date")
        @Temporal(TemporalType.TIMESTAMP)
        private java.util.Date lastAttchLinkedDt;

        @Column(name = "VALIDATION_STATUS")
        private String validationStatus;

        @Column(name = "IS_DELETED")
        private Boolean deleted;

        @Column(name = "CREATED_DATE")
        @Temporal(TemporalType.TIMESTAMP)
        private java.util.Date createdDate;

        @Column(name = "CREATED_BY")
        private Integer createdBy;

        @Column(name = "LAST_UPDATED_DATE")
        @Temporal(TemporalType.TIMESTAMP)
        private java.util.Date lastUpdatedDate;

        @Column(name = "LAST_UPDATED_BY")
        private Integer lastUpdatedBy;

        @OneToMany(mappedBy = "sourceFile")
        private java.util.List<SourceFileAttachment> attachments;
}
