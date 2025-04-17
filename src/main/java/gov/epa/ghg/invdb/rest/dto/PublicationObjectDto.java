package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PublicationObjectDto {
    private Long pubObjectId;
    private Long pubVersionId;
    private String pubVersionName;
    private Long pubId;
    private String rowName;
    private String rowPrefix;
    private String prepareButtonText;
    private String prepareButtonScript;
    private String refineButtonText;
    private String refineButtonScript;
    private String rawTablename;
    private Integer totalRecordsRaw;
    private Date lastImportDate;
    private String lastImportUser;
    private String refinedTablename;
    private Date lastRefinedDate;
    private String lastRefinedUser;
    private int lastRefinedBy;
    private String rawData;
    private String refinedData;

    public PublicationObjectDto(
            Long pubObjectId,
            Long pubVersionId,
            String pubVersionName,
            Long pubId,
            String rowName,
            String rowPrefix,
            String prepareButtonText,
            String prepareButtonScript,
            String refineButtonText,
            String refineButtonScript,
            String rawTablename,
            Integer totalRecordsRaw,
            Date lastImportDate,
            String lastImportUser,
            String refinedTablename,
            Date lastRefinedDate,
            String lastRefinedUser) {
        this.pubObjectId = pubObjectId;
        this.pubVersionId = pubVersionId;
        this.pubVersionName = pubVersionName;
        this.pubId = pubId;
        this.rowName = rowName;
        this.rowPrefix = rowPrefix;
        this.prepareButtonText = prepareButtonText;
        this.prepareButtonScript = prepareButtonScript;
        this.refineButtonText = refineButtonText;
        this.refineButtonScript = refineButtonScript;
        this.rawTablename = rawTablename;
        this.totalRecordsRaw = totalRecordsRaw;
        this.lastImportDate = lastImportDate;
        this.lastImportUser = lastImportUser;
        this.refinedTablename = refinedTablename;
        this.lastRefinedDate = lastRefinedDate;
        this.lastRefinedUser = lastRefinedUser;
    }

    public PublicationObjectDto(Long pubObjectId, String pubVersionName, String refinedTableName,
            Date lastRefinedDate, String lastRefinedUser) {
        this.pubObjectId = pubObjectId;
        this.pubVersionName = pubVersionName;
        this.refinedTablename = refinedTableName;
        this.lastRefinedDate = lastRefinedDate;
        this.lastRefinedUser = lastRefinedUser;
    }

    public PublicationObjectDto(Long pubObjectId, String refinedTableName, String refinedData,
            Date lastRefinedDate, int lastRefinedBy) {
        this.pubObjectId = pubObjectId;
        this.refinedTablename = refinedTableName;
        this.refinedData = refinedData;
        this.lastRefinedDate = lastRefinedDate;
        this.lastRefinedBy = lastRefinedBy;
    }
}
