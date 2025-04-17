package gov.epa.ghg.invdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.ArchiveAttachment;

@Repository
public interface ArchiveAttachmentRepository extends JpaRepository<ArchiveAttachment, Long> {

}
