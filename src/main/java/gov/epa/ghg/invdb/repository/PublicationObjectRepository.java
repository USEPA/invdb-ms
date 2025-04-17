package gov.epa.ghg.invdb.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.PublicationObject;
import gov.epa.ghg.invdb.rest.dto.PublicationObjectDto;

@Repository
public interface PublicationObjectRepository extends JpaRepository<PublicationObject, Long> {
        @Query("select new gov.epa.ghg.invdb.rest.dto.PublicationObjectDto(pubObjectId, "
                        + "pubVersionId, '', pubId, dimPublication.rowName, dimPublication.rowPrefix, "
                        + "dimPublication.prepareButtonText, dimPublication.prepareButtonScript, "
                        + "dimPublication.refineButtonText, dimPublication.refineButtonScript, "
                        + "rawTablename, totalRecordsRaw, lastImportDate, "
                        + "CONCAT(lastImportUser.firstName, ' ', lastImportUser.lastName), "
                        + "refinedTablename, lastRefinedDate, CONCAT(lastRefinedUser.firstName, ' ', lastRefinedUser.lastName)) "
                        + "from PublicationObject pubObj "
                        + "left join pubObj.dimPublication dimPublication "
                        + "left join pubObj.lastImportUser lastImportUser "
                        + "left join pubObj.lastRefinedUser lastRefinedUser "
                        + "where pubVersionId = :versionId order by dimPublication.id")
        java.util.List<PublicationObjectDto> getRecordsForVersionId(Integer versionId);

        @Query(value = "select raw_data from ggds_invdb.publication_object where pub_object_id=:pubObjId", nativeQuery = true)
        String getRawData(@Param("pubObjId") Long pubObjId);

        @Modifying
        @Query(value = "update ggds_invdb.publication_object "
                        + "set raw_data=:rawData, raw_tablename=:tablename, "
                        + "raw_total_records=:totalRecords, "
                        + "last_import_date=:importDate, last_import_by = :importBy "
                        + "where pub_object_id=:id", nativeQuery = true)
        void updateRawDataInfo(@Param("rawData") String rawData, @Param("tablename") String tablename,
                        @Param("totalRecords") int totalRecords,
                        @Param("importDate") Date importDate, @Param("importBy") int userId,
                        @Param("id") Long pubObjectId);

        @Query(value = "select refined_data from ggds_invdb.publication_object where pub_object_id=:pubObjId", nativeQuery = true)
        String getRefinedData(@Param("pubObjId") Long pubObjId);

        @Query("select new gov.epa.ghg.invdb.rest.dto.PublicationObjectDto(pubObjectId, "
                        + "refinedTablename, refinedData, lastRefinedDate, lastRefinedBy) "
                        + "from PublicationObject "
                        + "where pubObjectId in (:pubObjIds)")
        List<PublicationObjectDto> getRefinedDataObjects(List<Long> pubObjIds);

        @Query("select new gov.epa.ghg.invdb.rest.dto.PublicationObjectDto(pubObjectId, "
                        + "pubVersion.versionName, refinedTablename, lastRefinedDate, "
                        + "CONCAT(lastRefinedUser.firstName, ' ', lastRefinedUser.lastName)) "
                        + "from PublicationObject pubObj "
                        + "left join pubObj.publicationVersion pubVersion "
                        + "left join pubObj.lastRefinedUser lastRefinedUser "
                        + "where pubVersion.pubYear = :year and pubVersion.layerId = :layer "
                        + "and TRIM(COALESCE(refinedTablename, '')) != '' order by pubObj.pubObjectId")
        java.util.List<PublicationObjectDto> getRefinedTablesByLayerAndYear(Integer layer, Integer year);
}
