package gov.epa.ghg.invdb.repository;

import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimSourceName;
import gov.epa.ghg.invdb.rest.dto.DimSourceNameDto;

@Repository
@DynamicUpdate
public interface DimSourceNameRepository extends JpaRepository<DimSourceName, Long> {
		
	@Query("select new gov.epa.ghg.invdb.rest.dto.DimSourceNameDto(d.id, d.name, d.pubYear.id as pubYearId) "
			+ "from DimSourceName d where d.layerId = :layerId and d.pubYear.year = :year")	
	List<DimSourceNameDto> findByLayerIdAndPubYearYear(Integer layerId, Integer year);
	
	@Query(name = "check_template_3_dim_source_name", nativeQuery = true)
	List<DimSourceNameDto> checkTemplate3DimSourceName(
			@Param("category") String category,
			@Param("subcategory1") String subcategory1,
			@Param("layerId") Integer layerId,
			@Param("rptYr") Integer reportingYear);
	

	@Query(value = "select case when sum (x.foundsubcategory) > 0 then 1 else 0 end foundsubcategory,"
			+ "case when sum(x.catcount) is null then 0 else sum(x.catcount) end foundcategory "
			+ "from ("
			+ "SELECT dsn.source_name_id, dsn.source_name, dsn.pub_year_id,subsector.subsector_id, "
			+ "case when category.category_name = :category and dsn.sub_category_1 is not null and dsn.sub_category_1 = :subcategory1 then 1 else 0 end foundsubcategory, "
			+ "count(*) catCount "		
			+ "FROM ggds_invdb.dim_source_name dsn "
			+ "LEFT JOIN ggds_invdb.dim_category category ON dsn.category_id = category.category_id "
			+ "LEFT JOIN ggds_invdb.dim_subsector subsector ON category.subsector_id = subsector.subsector_id "
			+ "LEFT JOIN ggds_invdb.dim_sector sector ON subsector.sector_id = sector.sector_id "
			+ "LEFT JOIN ggds_invdb.dim_publication_year pubyear ON dsn.pub_year_id = pubyear.pub_year_id "
			+ "WHERE category.category_name = :category " 
			+ "AND dsn.layer_id = :layerId "
			+ "AND pubyear.pub_year = :rptYr "			
			+ "and category.category_name = :category "
			+ "group by source_name_id, source_name, dsn.pub_year_id,subsector.subsector_id, category.category_name,sub_category_1 order by subsector.subsector_id desc "
			+ ") x", nativeQuery = true)
			PubDimSourceCheck  checkDimSourceName(
				@Param("category") String category,
				@Param("subcategory1") String subcategory1,
				@Param("layerId") Integer layerId,
				@Param("rptYr") Integer reportingYear);

	
	public static interface PubDimSourceCheck {
		Integer getFoundsubcategory();
		Integer getFoundcategory();
	}
	
    @Query(value = "SELECT d.source_name_id as id, d.source_name as name, d.pub_year_id as pubYearId ,d.sector_id as sectorId, d.category_id as categoryId, d.sub_category_1 as subCategory1 "
    		+ "FROM ggds_invdb.dim_source_name d "
    		+ "left join ggds_invdb.dim_category dc on ( dc.category_id = d.category_id and dc.category_active like '%' || :pubYearId || '%') "
    		+ "where d.layer_id =:dimLayerId and d.pub_year_id = :pubYearId and dc.category_active is not null  ", nativeQuery = true)
    List<PubDimSourceWithActiveCat> getDimSourceWithActiveCategory(@Param("pubYearId") Integer pubYearId, @Param("dimLayerId") Integer dimLayerId);
    
	public static interface PubDimSourceWithActiveCat {
		Integer getId();
		String getName();
		Integer getPubYearId();
		Integer getSectorId();
		Integer getCategoryId();
		String getSubCategory1();
	}
	
	//@Query(value = "select max(dsn.source_name_id) from ggds_invdb.dim_source_name dsn", nativeQuery = true)
	@Query(value = "select nextval('ggds_invdb.dim_source_name_source_id_seq') ", nativeQuery = true)
	Optional<Long> findMaxIdNative();
}