package gov.epa.ghg.invdb.repository;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.ValidationLogLoad;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ValidationLogLoadRepository extends JpaRepository<ValidationLogLoad, Long> {
    java.util.List<ValidationLogLoad> findByAttachmentIdOrderByRowNumberAsc(Long attachmentId);
}
