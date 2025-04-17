package gov.epa.ghg.invdb.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.epa.ghg.invdb.model.ArchiveAttachment;
import gov.epa.ghg.invdb.model.ArchiveObject;
import gov.epa.ghg.invdb.model.ArchivePackage;
import gov.epa.ghg.invdb.repository.ArchiveAttachmentRepository;
import gov.epa.ghg.invdb.repository.ArchiveObjectRepository;
import gov.epa.ghg.invdb.repository.ArchivePackageRepository;
import gov.epa.ghg.invdb.rest.dto.ArchivePackageCreateDto;
import jakarta.transaction.Transactional;

@Service
public class ArchiveService {
    @Autowired
    private ArchiveAttachmentRepository archiveAttachmentRepository;
    @Autowired
    private ArchivePackageRepository archivePackageRepository;
    @Autowired
    private ArchiveObjectRepository archiveObjectRepository;

    @Transactional
    public void save(ArchivePackageCreateDto archivePkgDto, byte[] bytes, int layer, int year, int userId)
            throws Exception {
        Date today = new Date();
        ArchivePackage archivePkg = null;
        Boolean isDeleteArchiveObj = null;
        if (archivePkgDto.getArchivePackageId() != null) {
            archivePkg = archivePackageRepository.findById(archivePkgDto.getArchivePackageId()).orElse(null);
            isDeleteArchiveObj = archiveObjectRepository.deleteArchiveObjects(archivePkgDto.getArchivePackageId());
            if (archivePkg == null || !isDeleteArchiveObj) {
                throw new Exception("Error getting archive package and/or deleting existing archvie objects");
            }
        }
        ArchiveAttachment attachment = new ArchiveAttachment();
        attachment.setAttachmentName("test");
        attachment.setAttachmentContent(bytes);
        attachment.setCreatedBy(userId);
        attachment.setCreatedDate(today);
        attachment = archiveAttachmentRepository.save(attachment);
        Long attachmentId = attachment.getAttachmentId();
        if (attachmentId == null) {
            throw new Exception("Error creating archive attachment");
        }
        if (archivePkg == null) {
            archivePkg = new ArchivePackage();
            archivePkg.setEventTypeId(archivePkgDto.getEventTypeId());
            archivePkg.setArchiveName(archivePkgDto.getArchiveName());
            archivePkg.setLayerId(layer);
            archivePkg.setReportingYear(year);
        }
        archivePkg.setArchiveDescription(archivePkgDto.getArchiveDesc());
        archivePkg.setArchiveAttachmentId(attachmentId);
        archivePkg.setLastCreatedBy(userId);
        archivePkg.setLastCreatedDate(today);
        archivePkg = archivePackageRepository.save(archivePkg);
        Integer packageId = archivePkg.getArchivePackageId();
        if (packageId == null) {
            throw new Exception("Error creating archive package");
        }
        // Objects
        // 1. insert source files
        Boolean success = archiveObjectRepository.insertSourcefiles(layer, year, packageId);
        if (!success) {
            throw new Exception("Error inserting data files for archive");
        }
        archivePkgDto.getArchiveObjects().forEach(obj -> {
            ArchiveObject archiveObject = new ArchiveObject();
            archiveObject.setPackageId(packageId);
            archiveObject.setObjectName(obj.getObjectName());
            archiveObject.setObjectType(obj.getObjectType());
            archiveObject.setLastUpdatedDate(obj.getLastUpdatedDate());
            archiveObject.setLastUpdatedBy(obj.getLastUpdatedBy());
            archiveObjectRepository.save(archiveObject);
        });
    }
}
