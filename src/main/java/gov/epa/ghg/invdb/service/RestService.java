package gov.epa.ghg.invdb.service;

import java.io.BufferedOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import gov.epa.ghg.invdb.exception.PythonServiceException;
import gov.epa.ghg.invdb.exception.ResponseException;
import gov.epa.ghg.invdb.rest.helper.AttachmentHelper;
import gov.epa.ghg.invdb.util.ResponseEntityUtil;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class RestService {
    @Autowired
    private RestClient restClient;
    @Autowired
    private WebClient webClient;
    @Autowired
    private AttachmentHelper attachmentHelper;

    // Method for synchronous calls
    public ResponseEntity<String> invokeRestClient(String uriWithParams) {
        ResponseEntity<String> response = null;
        try {
            String body = restClient.get().uri(uriWithParams)
                    .retrieve()
                    .body(String.class);
            response = ResponseEntity.ok(body);
        } catch (Exception e) {
            throw new PythonServiceException("Error calling Python service", e);
        }
        return response;
    }

    public ResponseEntity<StreamingResponseBody> invokeRestClientZipDownload(String uriWithParams, String filename) {
        try {
            byte[] zipContent = restClient.get().uri(uriWithParams)
                    .retrieve()
                    .body(byte[].class);
            if (zipContent == null || zipContent.length == 0) {
                throw new ResponseException(HttpStatus.NO_CONTENT, "No content received");
            }
            StreamingResponseBody stream = outputStream -> {
                try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                        outputStream)) {
                    bufferedOutputStream.write(zipContent);
                    bufferedOutputStream.flush(); // ensure all bytes are sent
                }
            };
            return ResponseEntityUtil.downloadZipResponse(stream, filename);
        } catch (Exception e) {
            throw new PythonServiceException("Error calling Python service", e);
        }

    }

    public Mono<ResponseEntity<String>> invokeWebClient(String uriWithParams) {
        return webClient.get().uri(uriWithParams).retrieve()
                .toEntity(String.class)
                .doOnSuccess(responseEntity -> {
                    // Log or perform any additional actions on success if needed
                    log.info(responseEntity);
                })
                .onErrorResume(error -> {
                    return Mono.error(new PythonServiceException("Python call failed", error));
                });
    }

    public ResponseEntity<StreamingResponseBody> invokeRestClientExcelDownload(String uriWithParams, String filename) {
        try {
            byte[] content = restClient.get().uri(uriWithParams)
                    .retrieve()
                    .body(byte[].class);
            return attachmentHelper.createFileDownloadResponse(content, filename,
                    "excel");
        } catch (Exception e) {
            throw new PythonServiceException("Error calling Python service", e);
        }
    }
}
