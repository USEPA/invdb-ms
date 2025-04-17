package gov.epa.ghg.invdb.repository;

import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.ValidationLogExtract;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ValidationLogExtractRepository extends JpaRepository<ValidationLogExtract, Long> {
    java.util.List<ValidationLogExtract> findByAttachmentIdOrderByLogIdAsc(Long attachmentId);
}
