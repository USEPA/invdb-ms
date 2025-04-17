package gov.epa.ghg.invdb.rest.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.epa.ghg.invdb.enumeration.AttachmentType;
import gov.epa.ghg.invdb.model.SourceFileAttachment;
import gov.epa.ghg.invdb.model.ValidationLogLoad;
import gov.epa.ghg.invdb.model.ValidationLogExtract;
import gov.epa.ghg.invdb.repository.DimSourceNameRepository;
import gov.epa.ghg.invdb.repository.SourceFileAttachmentRepository;
import gov.epa.ghg.invdb.repository.SourceFileRepository;
import gov.epa.ghg.invdb.repository.ValidationLogLoadRepository;
import gov.epa.ghg.invdb.repository.ValidationLogExtractRepository;
import gov.epa.ghg.invdb.rest.dto.AttachmentDto;
import gov.epa.ghg.invdb.rest.dto.DimSourceNameDto;
import gov.epa.ghg.invdb.rest.dto.SourceFileAttachmentDto;
import gov.epa.ghg.invdb.rest.dto.SourceFileDto;
import gov.epa.ghg.invdb.rest.dto.SourceFileSummaryDetailsDto;
import gov.epa.ghg.invdb.rest.dto.SourceFileSummaryDto;
import gov.epa.ghg.invdb.rest.dto.ValidationLogLoadDto;
import gov.epa.ghg.invdb.rest.dto.ValidationLogExtractDto;
import gov.epa.ghg.invdb.rest.dto.ValidationLogWrapperDto;
import gov.epa.ghg.invdb.rest.helper.AttachmentHelper;
import gov.epa.ghg.invdb.service.RestService;
import gov.epa.ghg.invdb.service.SourceFileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/sourceFiles")
@Log4j2
public class SourceFileController {
        @Autowired
        private SourceFileRepository sourceFileRepository;
        @Autowired
        private SourceFileAttachmentRepository attachmentRepository;
        @Autowired
        private SourceFileService sourceFileService;
        @Autowired
        private ValidationLogLoadRepository validationLogRepository;
        @Autowired
        private ValidationLogExtractRepository validationLogExtractRepository;
        @Autowired
        private DimSourceNameRepository dimSourceNameRepository;
        @Autowired
        private AttachmentHelper attachmentHelper;
        @Autowired
        private RestService restService;

        @GetMapping("/load")
        public List<SourceFileDto> getSourceFiles(@RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear) {
                List<SourceFileDto> srcFiles = sourceFileRepository.getFilesForDisplayByLayerAndYear(layerId, rptYear);
                return srcFiles;
        }

        @PostMapping("/save")
        public ResponseEntity<String> saveSourceFiles(@RequestParam("files") List<MultipartFile> files,
                        @RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear,
                        @RequestParam(name = "user") int userId)
                        throws Exception {
                // get source name details
                List<DimSourceNameDto> sourceNames = dimSourceNameRepository.findByLayerIdAndPubYearYear(layerId,
                                rptYear);
                // also get existing source files
                List<SourceFileDto> existingSrcFiles = sourceFileRepository.getFilesWithSrcNameByLayerAndYear(layerId,
                                rptYear);
                sourceFileService.save(files, existingSrcFiles, sourceNames, layerId,
                                rptYear, userId);
                // invoke data validation service
                String uriWithParams = String.format("/source-file-validation?reporting_year=%s&layer_id=%s&user_id=%s",
                                rptYear,
                                layerId,
                                userId);
                log.debug("Source file validate URL: ", uriWithParams);
                return restService.invokeRestClient(uriWithParams);
        }

        @GetMapping("/download")
        public void downloadAttachments(
                        @RequestParam(name = "attachmentIds") List<Long> attachmentIds, HttpServletResponse response)
                        throws IOException {
                List<SourceFileAttachment> attachments = attachmentRepository.findAllById(attachmentIds);
                List<AttachmentDto> attachementDtos = attachments.stream()
                                .map(attachment -> new AttachmentDto(attachment.getAttachmentId(),
                                                attachment.getAttachmentName(),
                                                AttachmentType.EXCEL, attachment.getAttachmentSize(),
                                                attachment.getContent()))
                                .collect(Collectors.toList());
                attachmentHelper.downloadZip(response, "dataFiles", attachementDtos);
        }

        @GetMapping("/loadHistory")
        public List<SourceFileAttachmentDto> getSourceFileHistory(
                        @RequestParam(name = "sourceFileId") Long sourceFileId) {
                List<SourceFileAttachmentDto> attachmentDtos = attachmentRepository
                                .getFilesBySourceFileId(sourceFileId);
                return attachmentDtos;
        }

        @PostMapping("/revert")
        public ResponseEntity<java.util.Date> revertAttachment(@RequestParam(name = "attachmentId") Long attachmentId,
                        @RequestParam(name = "user") int userId)
                        throws Exception {
                java.util.Date lastAttchLinkedDate = sourceFileService.revert(attachmentId, userId);
                return ResponseEntity.ok(lastAttchLinkedDate);
        }

        @GetMapping("/loadErrorLogs")
        public ValidationLogWrapperDto getAttachmentErrors(@RequestParam(name = "attachmentId") Long attachmentId) {
                List<ValidationLogLoad> validationLogs = validationLogRepository
                                .findByAttachmentIdOrderByRowNumberAsc(attachmentId);
                List<ValidationLogExtract> validationQcLogs = validationLogExtractRepository
                                .findByAttachmentIdOrderByLogIdAsc(attachmentId);
                List<ValidationLogLoadDto> validationLogDtos = validationLogs.stream()
                                .map(log -> new ValidationLogLoadDto(log.getLogId(), log.getFieldName(),
                                                log.getFieldValue(),
                                                log.getRowNumber(),
                                                log.getDescription()))
                                .collect(Collectors.toList());
                List<ValidationLogExtractDto> validationQcLogDtos = validationQcLogs.stream()
                                .map(log -> new ValidationLogExtractDto(log.getLogId(),
                                                log.getEmissionsQcLoadTargetId(),
                                                log.getCellValue(),
                                                log.getCellLocation(),
                                                log.getDescription()))
                                .collect(Collectors.toList());
                ValidationLogWrapperDto wrapper = new ValidationLogWrapperDto(validationLogDtos, validationQcLogDtos);
                return wrapper;
        }

        @PostMapping("/delete")
        public ResponseEntity<String> deleteSourceFiles(
                        @RequestParam("sourceFileIds") Integer[] sourceFileIds,
                        @RequestParam(name = "user") int userId)
                        throws Exception {
                ResponseEntity<String> response = null;
                try {
                        Boolean success = sourceFileRepository.deleteSourcefiles(sourceFileIds, userId);
                        if (success) {
                                response = ResponseEntity.ok("Files deleted successfully. ");
                        }
                } catch (Exception e) {
                        response = ResponseEntity
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(e.getCause() + ": " + e.getMessage());
                }
                return response;
        }

        @PostMapping("/process")
        public Mono<ResponseEntity<String>> process(@RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear, @RequestParam(name = "user") int userId)
                        throws Exception {
                String uriWithParams = String.format("/source-file-load?reporting_year=%s&layer_id=%s&user_id=%s",
                                rptYear,
                                layerId,
                                userId);
                log.debug("Source file load URL: ", uriWithParams);
                return restService.invokeWebClient(uriWithParams);
        }

        @GetMapping("/summary")
        public SourceFileSummaryDto sourceFileSummary(@RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int pubYear) {
                SourceFileSummaryDto summary = sourceFileRepository.getSourceFileSummary(pubYear, layerId);
                return summary;
        }

        @GetMapping("/summary/details")
        public List<SourceFileSummaryDetailsDto> sourceFileSummaryDetails(@RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int pubYear) {
                List<SourceFileSummaryDetailsDto> summaryDetails = sourceFileRepository
                                .getSourceFileSummaryDetails(pubYear, layerId);
                return summaryDetails;
        }

}
