package gov.epa.ghg.invdb.rest.dto;

import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class QcAnalyticsFormDto {
    String baselineYearLayerKey;
    Long baselinePkgAttachmentId;
    String baselineObjName;
    Boolean isRecalculations;
    String comparatorYearLayerKey;
    Long compartorPkgAttachmentId;
    String comparatorObjName;
    String recalcParameter;
    String recalcThreshold;
    List<Integer> recalcYears;
    Boolean isTimeseriesOutlier;
    List<Integer> tsYearsSelected;
    List<String> categories;
    Boolean filterJson;
    List<String> columns;
    String ghgOption;
}
