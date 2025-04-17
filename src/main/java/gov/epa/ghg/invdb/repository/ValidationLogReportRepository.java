package gov.epa.ghg.invdb.repository;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.ValidationLogReport;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ValidationLogReportRepository extends JpaRepository<ValidationLogReport, Long> {
    java.util.List<ValidationLogReport> findByReportIdOrderByRowNumberAsc(Long reportId);
}
