package gov.epa.ghg.invdb.rest.dto;

import gov.epa.ghg.invdb.enumeration.AttachmentType;
import lombok.Data;

@Data
public class AttachmentDto {
    private Long attachmentId;
    private String attachmentName;
    private AttachmentType attachmentType;
    private Long attachmentSize;
    private byte[] contentBinary;
    private String contentText;

    public AttachmentDto(Long attachmentId, String attachmentName,
            AttachmentType attachmentType, Long attachmentSize, byte[] content) {
        this.attachmentId = attachmentId;
        this.attachmentName = attachmentName;
        this.attachmentType = attachmentType;
        this.attachmentSize = attachmentSize;
        this.contentBinary = content;
    }

    public AttachmentDto(String attachmentName, String content) {
        this.attachmentName = attachmentName;
        this.contentText = content;
        this.attachmentType = AttachmentType.JSON;
    }

    public AttachmentDto(String attachmentName, String content, AttachmentType attachmentType) {
        this.attachmentName = attachmentName;
        this.contentText = content;
        this.attachmentType = attachmentType;
    }

    public AttachmentDto(String attachmentName, byte[] contentBinary, AttachmentType attachmentType) {
        this.attachmentName = attachmentName;
        this.contentBinary = contentBinary;
        this.attachmentType = attachmentType;
    }

    public String getAttachmentExtension() {
        switch (attachmentType) {
            case JSON:
                return ".json";
            case CSV:
                return ".csv";
            case EXCEL:
                return ".xlsx";
            case PDF:
                return ".pdf";
            default:
                return ".txt";
        }
    }
}
