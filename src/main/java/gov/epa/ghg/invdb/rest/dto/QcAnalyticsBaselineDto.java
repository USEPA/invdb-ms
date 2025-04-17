package gov.epa.ghg.invdb.rest.dto;

import lombok.Data;

@Data
public class QcAnalyticsBaselineDto {
    private Integer year;
    private String layer;
    private Long archiveObjId;
    private String archiveObjName;
    private Long archivePkgAttachmentId;
    private String archivePkgName;
    String displayName;

    public QcAnalyticsBaselineDto(Integer year, String layer, Long archiveObjId, String archiveObjName,
            Long archivePkgAttachmentId, String archivePkgName) {
        this.year = year;
        this.layer = layer;
        this.archiveObjId = archiveObjId;
        this.archiveObjName = archiveObjName;
        this.archivePkgAttachmentId = archivePkgAttachmentId;
        this.archivePkgName = archivePkgName;
    }
}
