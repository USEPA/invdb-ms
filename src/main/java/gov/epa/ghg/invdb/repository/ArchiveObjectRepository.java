package gov.epa.ghg.invdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.ArchiveObject;

@Repository
public interface ArchiveObjectRepository extends JpaRepository<ArchiveObject, Long> {
        @Query(value = "select ggds_invdb.archive_insert_sourcefile(:layerId, :rptYr, :archivePkgId)", nativeQuery = true)
        Boolean insertSourcefiles(@Param("layerId") Integer layerId,
                        @Param("rptYr") Integer reportingYear, @Param("archivePkgId") Integer archivePkgId);

        @Query(value = "select ggds_invdb.archive_objects_delete(:archivePkgId)", nativeQuery = true)
        Boolean deleteArchiveObjects(@Param("archivePkgId") Integer archivePkgId);

        @Query(value = "select distinct dpy.pub_year as year, fa.layer_id as layer from ggds_invdb.facts_archive fa "
                        + "left join ggds_invdb.dim_publication_year dpy on fa.pub_year_id = dpy.pub_year_id", nativeQuery = true)
        java.util.List<Object[]> getFactsArchiveYearLayerCombos();

        @Query(value = "SELECT ao.archive_object_id as id, ao.object_name, ap.reporting_year as year, ap.layer_id as layer, ap.archive_name FROM ggds_invdb.archive_object ao "
                        + "left join ggds_invdb.archive_package ap on ao.archive_package_id = ap.archive_package_id "
                        + "WHERE ao.object_name ~* 'EM_Nat_All|EM_Sta_All|EM_Nat_PowerUser|EM_Sta_PowerUser'", nativeQuery = true)
        java.util.List<Object[]> getArchiveObjectsWithQCPrefixes();

        // only get year layer combos that either have data associated with them in
        // archive_package
        // or that have any records at all for that pub_year in facts_archive (for the
        // "Current" option)
        @Query(value = "WITH filtered_archive_objects AS ( "
                        + "SELECT ao.archive_package_id "
                        + "FROM ggds_invdb.archive_object ao "
                        + "WHERE ao.object_name ~* 'EM_Nat_All|EM_Sta_All|EM_Nat_PowerUser|EM_Sta_PowerUser' "
                        + "), "
                        + "filtered_packages AS ( "
                        + "SELECT ap.archive_package_id, ap.reporting_year, ap.layer_id "
                        + "FROM ggds_invdb.archive_package ap "
                        + "JOIN filtered_archive_objects fao ON ap.archive_package_id = fao.archive_package_id "
                        + "), "
                        + "relevant_years AS ( "
                        + "SELECT DISTINCT dpy.pub_year, fa.layer_id "
                        + "FROM ggds_invdb.facts_archive fa "
                        + "JOIN ggds_invdb.dim_publication_year dpy ON fa.pub_year_id = dpy.pub_year_id "
                        + ") "
                        + "SELECT DISTINCT ry.pub_year AS year, ry.layer_id AS layer "
                        + "FROM relevant_years ry "
                        + "LEFT JOIN filtered_packages fp ON fp.reporting_year = ry.pub_year AND fp.layer_id = ry.layer_id", nativeQuery = true)
        java.util.List<Object[]> getFactsArchiveYearLayerCombosThatHaveData();

        @Query(value = ""
                        + "SELECT * from( "
                        + " SELECT DISTINCT dpy.pub_year as year, dl.display_name as layer, 0 as archive_object_id, "
                        // start with '0-' to identify it as Current
                        + " '0-' || dpy.pub_year_id || '-' || dl.layer_id object_name, "
                        + "   0 as archive_attachment_id, 'current' as archive_name "
                        + "  FROM ggds_invdb.facts_archive fa "
                        + "  JOIN ggds_invdb.dim_publication_year dpy ON fa.pub_year_id = dpy.pub_year_id "
                        + "  JOIN ggds_invdb.dim_layer dl on fa.layer_id = dl.layer_id "
                        + " UNION "
                        + " SELECT ap.reporting_year as year, dl2.display_name as layer, ao.archive_object_id, ao.object_name, "
                        + "  ap.archive_attachment_id, ap.archive_name "
                        + "  FROM ggds_invdb.archive_object ao "
                        + "  JOIN ggds_invdb.archive_package ap on ao.archive_package_id = ap.archive_package_id "
                        + "  JOIN ggds_invdb.dim_layer dl2 on ap.layer_id = dl2.layer_id "
                        + "  WHERE ao.object_name ~* 'EM_Nat_All|EM_Sta_All|EM_Nat_PowerUsers|EM_Sta_PowerUsers'"
                        + " )a order by year desc, layer, archive_attachment_id, object_name", nativeQuery = true)
        java.util.List<Object[]> getQcAnalyticsBaselineOptions();
}
