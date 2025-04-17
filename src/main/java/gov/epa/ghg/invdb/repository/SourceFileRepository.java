package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.SourceFile;
import gov.epa.ghg.invdb.rest.dto.SourceFileDto;
import gov.epa.ghg.invdb.rest.dto.SourceFileSummaryDetailsDto;
import gov.epa.ghg.invdb.rest.dto.SourceFileSummaryDto;

@Repository
public interface SourceFileRepository extends JpaRepository<SourceFile, Long> {
        @Query(name = "get_source_files_by_layer_year", nativeQuery = true)
        java.util.List<SourceFileDto> getFilesForDisplayByLayerAndYear(
                        @Param("layerId") Integer layerId,
                        @Param("rptYr") Integer reportingYear);

        @Query("select new gov.epa.ghg.invdb.rest.dto.SourceFileDto(srcFile.sourceFileId, "
                        + "srcFile.sourceNameId) "
                        + "from SourceFile srcFile "
                        + "where srcFile.layerId = :layerId and srcFile.reportingYear = :rptYr")
        java.util.List<SourceFileDto> getFilesWithSrcNameByLayerAndYear(
                        @Param("layerId") Integer layerId,
                        @Param("rptYr") Integer reportingYear);

        @Query(value = "select ggds_invdb.delete_sourcefiles(:srcfileIds, :userId)", nativeQuery = true)
        Boolean deleteSourcefiles(@Param("srcfileIds") Integer[] srcfileIds, @Param("userId") Integer userId);

        @Query(name = "get_sourcefile_summary", nativeQuery = true)
        SourceFileSummaryDto getSourceFileSummary(Integer yearId, Integer layerId);

        @Query(name = "get_sourcefile_summary_details", nativeQuery = true)
        List<SourceFileSummaryDetailsDto> getSourceFileSummaryDetails(Integer yearId, Integer layerId);
}
