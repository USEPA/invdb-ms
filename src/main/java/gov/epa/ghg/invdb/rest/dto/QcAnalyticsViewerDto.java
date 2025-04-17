package gov.epa.ghg.invdb.rest.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class QcAnalyticsViewerDto {
    private Long viewerId;
    private String folderName;
    private Date createdDate;
    private String createdBy;
    private String baselineYrLayer;
    private String baselineObject;
    private String descAnalysis;
    private String recalcJobStatus;
    private List<String> recalcYears;
    private String outlierJobStatus;
    private String specifications;

    public QcAnalyticsViewerDto(String recalcJobStatus, String outlierJobStatus, Long viewerId) {
        this.recalcJobStatus = recalcJobStatus;
        this.outlierJobStatus = outlierJobStatus;
        this.viewerId = viewerId;
    }

    public QcAnalyticsViewerDto(Long viewerId, String folderName, Date createdDate,
            String createdBy, String baselineYrLayer, String baselineObject, String descAnalysis,
            String recalcJobStatus, List<String> recalcYears, String outlierJobStatus, String specifications) {
        this.viewerId = viewerId;
        this.folderName = folderName;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.baselineYrLayer = baselineYrLayer;
        this.baselineObject = baselineObject;
        this.descAnalysis = descAnalysis;
        this.recalcJobStatus = recalcJobStatus;
        this.recalcYears = recalcYears;
        this.outlierJobStatus = outlierJobStatus;
        this.specifications = specifications;
    }
}
