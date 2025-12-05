package gov.epa.ghg.invdb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.DimState;

@Repository
public interface DimStateRepository extends JpaRepository<DimState, Integer> {
    List<DimState> findByOrderByStateCodeAsc();
}
