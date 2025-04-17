package gov.epa.ghg.invdb.repository;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.SourceFileAttachment;
import gov.epa.ghg.invdb.rest.dto.SourceFileAttachmentDto;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SourceFileAttachmentRepository extends JpaRepository<SourceFileAttachment, Long> {
        @Query("select new gov.epa.ghg.invdb.rest.dto.SourceFileAttachmentDto(attachment.attachmentId, "
                        + "attachment.attachmentName, attachment.createdDate, "
                        + "CONCAT(userprofile.firstName, ' ', userprofile.lastName), "
                        + "attachment.hasError, attachment.processedDate, attachment.lastSrcfileLinkedDt) "
                        + "from SourceFileAttachment attachment "
                        + "left join attachment.srcAttachCreateUser userprofile "
                        + "where attachment.sourceFileId = :sourceFileId "
                        + "order by attachment.lastSrcfileLinkedDt desc")
        java.util.List<SourceFileAttachmentDto> getFilesBySourceFileId(
                        @Param("sourceFileId") Long sourceFileId);

                
        @Query("select max(processedDate) "
                + "from SourceFileAttachment attachment "
                + "left join attachment.sourceFile sourceFile "
                + "where sourceFile.layerId = :layerId and sourceFile.reportingYear = :rptYr ")
        Date findReportDataAge(@Param("layerId") Integer layerId,
                @Param("rptYr") Integer reportingYear);                    
}
