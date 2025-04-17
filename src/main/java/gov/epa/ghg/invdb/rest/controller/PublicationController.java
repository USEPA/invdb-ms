package gov.epa.ghg.invdb.rest.controller;

import java.io.IOException;
import java.util.ArrayList;
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

import gov.epa.ghg.invdb.model.PublicationVersion;
import gov.epa.ghg.invdb.repository.PublicationObjectRepository;
import gov.epa.ghg.invdb.repository.PublicationVersionRepository;
import gov.epa.ghg.invdb.rest.dto.AttachmentDto;
import gov.epa.ghg.invdb.rest.dto.PublicationObjectDto;
import gov.epa.ghg.invdb.rest.helper.AttachmentHelper;
import gov.epa.ghg.invdb.service.PublicationService;
import gov.epa.ghg.invdb.service.RestService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/api/publication")
@Log4j2
public class PublicationController {
        @Autowired
        private PublicationVersionRepository pubVersionRepository;
        @Autowired
        private PublicationObjectRepository pubObjectRepository;
        @Autowired
        private PublicationService pubService;
        @Autowired
        private AttachmentHelper attachmentHelper;
        @Autowired
        private RestService restService;

        @GetMapping("/versions")
        public List<PublicationVersion> getVersions(@RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int pubYear) {
                List<PublicationVersion> versions = pubVersionRepository.findByLayerIdAndPubYear(layerId, pubYear);
                return versions;
        }

        @PostMapping("/createVersion")
        public ResponseEntity<Object> createVersion(@RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear,
                        @RequestParam(name = "version") String version,
                        @RequestParam(name = "user") int userId) throws Exception {
                Long versionId = pubVersionRepository.createVersion(rptYear, layerId, version, userId);
                if (versionId > 0) {
                        return ResponseEntity.status(HttpStatus.CREATED).body(versionId);
                } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body("An error when creating version");
                }
        }

        @GetMapping("/pubObjects")
        public List<PublicationObjectDto> getPublicationObjects(@RequestParam(name = "version") int versionId) {
                List<PublicationObjectDto> pubObjects = pubObjectRepository.getRecordsForVersionId(versionId);
                return pubObjects;
        }

        @PostMapping("/import")
        public ResponseEntity<String> importObj(@RequestParam("file") MultipartFile file,
                        @RequestParam(name = "pubObjId") int pubObjectId,
                        @RequestParam(name = "tableName") String tableName,
                        @RequestParam(name = "user") int userId)
                        throws Exception {
                pubService.save(file, pubObjectId, tableName, userId);
                return ResponseEntity.ok("Publication object saved successfully");
        }

        @PostMapping("/prepare")
        public ResponseEntity<String> prepare(@RequestParam(name = "pubObjId") int pubObjectId,
                        @RequestParam(name = "user") int userId)
                        throws Exception {
                String uriWithParams = String.format(
                                "/publication-processing?pub_object_id=%s&action=prepare&user_id=%s",
                                pubObjectId,
                                userId);
                log.debug("Publication Prepare URL: ", uriWithParams);
                return restService.invokeRestClient(uriWithParams);
        }

        @GetMapping("/pubObjRawData")
        public String getPubObjRawData(@RequestParam(name = "pubObjectId") Long pubObjectId) {
                String rawData = pubObjectRepository.getRawData(pubObjectId);
                return rawData;
        }

        @PostMapping("/refine")
        public ResponseEntity<String> executeRefine(@RequestParam(name = "pubObjId") int pubObjectId,
                        @RequestParam(name = "refineScript") String refineScript,
                        @RequestParam(name = "tableName") String tableName,
                        @RequestParam(name = "user") int userId)
                        throws Exception {
                String uriWithParams = String.format(
                                "/publication-processing?pub_object_id=%s&action=refine&user_id=%s",
                                pubObjectId,
                                userId);
                log.debug("Publication Prepare URL: ", uriWithParams);
                return restService.invokeRestClient(uriWithParams);
        }

        @GetMapping("/pubObjRefinedData")
        public String getPubObjRefinedData(@RequestParam(name = "pubObjectId") Long pubObjectId) {
                String refinedData = pubObjectRepository.getRefinedData(pubObjectId);
                return refinedData;
        }

        @GetMapping("/download")
        public void downloadAttachments(
                        @RequestParam(name = "pubObjIds") List<Long> pubObjIds,
                        @RequestParam(name = "format") String format,
                        @RequestParam(name = "user") int userId,
                        HttpServletResponse response)
                        throws IOException {
                if (pubObjIds == null) {
                        throw new IOException("pubObjIds list is empty");
                }
                if (format.equalsIgnoreCase("json")) {
                        List<PublicationObjectDto> pubObjDtos = pubObjectRepository.getRefinedDataObjects(pubObjIds);
                        List<AttachmentDto> attachementDtos = new ArrayList<AttachmentDto>();
                        for (PublicationObjectDto pubObjDto : pubObjDtos) {
                                attachementDtos.add(
                                                new AttachmentDto(pubObjDto.getRefinedTablename(),
                                                                pubObjDto.getRefinedData()));
                        }
                        // if (format.equalsIgnoreCase("json")) {
                        attachmentHelper// .downloadJsonZip
                                        .downloadZip(response, "publicationFiles", attachementDtos);
                } else if (format.equalsIgnoreCase("excel")) {
                        String uriWithParams = String.format(
                                        "/publication-download?pub_object_id=%s&user_id=%s",
                                        pubObjIds.stream()
                                                        .map(Object::toString)
                                                        .collect(Collectors.joining(",")),
                                        userId);
                        log.debug("Publication Prepare URL: ", uriWithParams);
                        restService.invokeRestClientZipDownload(uriWithParams, "publicationFiles", response);
                }
        }

        @GetMapping("/refinedTables")
        public List<PublicationObjectDto> getRefinedTablesByLayerAndYear(@RequestParam(name = "layer") int layerId,
                        @RequestParam(name = "year") int rptYear) {
                List<PublicationObjectDto> refinedTables = pubObjectRepository.getRefinedTablesByLayerAndYear(layerId,
                                rptYear);
                return refinedTables;
        }
}
