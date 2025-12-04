package gov.epa.ghg.invdb.rest.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import gov.epa.ghg.invdb.enumeration.AttachmentType;
import gov.epa.ghg.invdb.exception.BadRequestException;
import gov.epa.ghg.invdb.model.ArchiveAttachment;
import gov.epa.ghg.invdb.model.Report;
import gov.epa.ghg.invdb.repository.ArchiveAttachmentRepository;
import gov.epa.ghg.invdb.repository.ArchiveObjectRepository;
import gov.epa.ghg.invdb.repository.ArchivePackageRepository;
import gov.epa.ghg.invdb.repository.PublicationObjectRepository;
import gov.epa.ghg.invdb.repository.ReportRepository;
import gov.epa.ghg.invdb.rest.dto.ArchiveObjectDto;
import gov.epa.ghg.invdb.rest.dto.ArchivePackageCreateDto;
import gov.epa.ghg.invdb.rest.dto.ArchivePackageRetrieveDto;
import gov.epa.ghg.invdb.rest.dto.AttachmentDto;
import gov.epa.ghg.invdb.rest.dto.PublicationObjectDto;
import gov.epa.ghg.invdb.rest.helper.AttachmentHelper;
import gov.epa.ghg.invdb.service.ArchiveService;
import gov.epa.ghg.invdb.util.ResponseEntityUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import gov.epa.ghg.invdb.rest.dto.ArchiveObjectYearLayerDto;
import gov.epa.ghg.invdb.rest.dto.FactsArchiveYearLayerDto;

@Log4j2
@RestController
@RequestMapping("/api/archive")
public class ArchiveController {
        @Autowired
        private ArchivePackageRepository packageRepository;
        @Autowired
        private ArchiveAttachmentRepository archiveAttachmentRepository;
        @Autowired
        private ArchiveObjectRepository archiveObjectRepository;
        @Autowired
        private ReportRepository reportRepository;
        @Autowired
        private PublicationObjectRepository pubObjectRepository;
        @Autowired
        private ArchiveService archiveService;
        @Autowired
        private AttachmentHelper attachmentHelper;

        @PostMapping("/create")
        public void create(@RequestBody ArchivePackageCreateDto archivePkg,
                        @RequestParam(name = "layer") int layer,
                        @RequestParam(name = "year") int year,
                        @RequestParam(name = "user") int userId)
                        throws Exception {
                List<ArchiveObjectDto> objects = archivePkg.getArchiveObjects();
                List<AttachmentDto> attachementDtos = new ArrayList<AttachmentDto>();
                objects.forEach(o -> {
                        if (("report").equalsIgnoreCase(o.getObjectType())) {
                                Report report = reportRepository.findById(o.getObjectAttachmentId()).orElse(null);
                                attachementDtos.add(new AttachmentDto(report.getReportId(),
                                                report.getAttachmentName(),
                                                AttachmentType.EXCEL, report.getAttachmentSize(),
                                                report.getContent()));
                                o.setLastUpdatedDate(report.getLastUpdatedDate());
                                o.setLastUpdatedBy(report.getLastUpdatedBy());
                        } else if (("publication").equalsIgnoreCase(o.getObjectType())) {
                                List<Long> pubObjectIds = new ArrayList<>();
                                pubObjectIds.add(o.getObjectAttachmentId());
                                List<PublicationObjectDto> pubObjDtos = pubObjectRepository
                                                .getRefinedDataObjects(pubObjectIds);
                                if (pubObjDtos.size() > 0) {
                                        PublicationObjectDto pubObjDto = pubObjDtos.get(0);
                                        attachementDtos.add(
                                                        new AttachmentDto(pubObjDto.getRefinedTablename(),
                                                                        pubObjDto.getRefinedData()));
                                        o.setLastUpdatedDate(pubObjDto.getLastRefinedDate());
                                        o.setLastUpdatedBy(pubObjDto.getLastRefinedBy());
                                }
                        }
                });
                byte[] zipData = attachmentHelper.downloadZip(attachementDtos);
                archiveService.save(archivePkg, zipData, layer, year, userId);
        }

        @GetMapping("/retrieve")
        public List<ArchivePackageRetrieveDto> retrieve(@RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear, HttpServletResponse response)
                        throws Exception {
                List<ArchivePackageRetrieveDto> packages = packageRepository.getPackagesByLayerAndYear(layerId,
                                rptYear);
                return packages;
        }

        @GetMapping("/download")
        public ResponseEntity<StreamingResponseBody> download(
                        @RequestParam(name = "archiveAttachmentId") Long archiveAttachmentId) {
                ArchiveAttachment attachment = archiveAttachmentRepository.findById(archiveAttachmentId).orElse(null);
                if (attachment == null || attachment.getAttachmentContent() == null) {
                        throw new BadRequestException("Attachment is empty");
                }
                byte[] data = attachment.getAttachmentContent();
                StreamingResponseBody stream = outputStream -> {
                        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                                        outputStream);
                                        ByteArrayInputStream input = new ByteArrayInputStream(data)) {
                                byte[] buffer = new byte[1024];
                                int len = 0;
                                while ((len = input.read(buffer)) > 0) {
                                        bufferedOutputStream.write(buffer, 0, len);
                                }
                        }
                };
                return ResponseEntityUtil.downloadZipResponse(stream, attachment.getAttachmentName());
        }

        @GetMapping("/getYearLayerCombos")
        public java.util.List<FactsArchiveYearLayerDto> getYearLayerCombos() {
                List<Object[]> results = archiveObjectRepository.getFactsArchiveYearLayerCombos();
                return results.stream()
                                .map(row -> new FactsArchiveYearLayerDto((Integer) row[0], (Integer) row[1]))
                                .collect(Collectors.toList());
        }

        @GetMapping("/getQCPrefixObjects")
        public java.util.List<ArchiveObjectYearLayerDto> getQCPrefixObjects() {
                List<Object[]> results = archiveObjectRepository.getArchiveObjectsWithQCPrefixes();
                return results.stream()
                                .map(row -> new ArchiveObjectYearLayerDto((Integer) row[0], (String) row[1],
                                                (Integer) row[2], (Integer) row[3], (String) row[4]))
                                .collect(Collectors.toList());
        }

        @GetMapping("/getYearLayerCombosThatHaveData")
        public java.util.List<FactsArchiveYearLayerDto> getYearLayerCombosThatHaveData() {
                List<Object[]> results = archiveObjectRepository.getFactsArchiveYearLayerCombosThatHaveData();
                return results.stream()
                                .map(row -> new FactsArchiveYearLayerDto((Integer) row[0], (Integer) row[1]))
                                .collect(Collectors.toList());
        }
}
