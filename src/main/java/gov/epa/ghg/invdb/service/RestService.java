package gov.epa.ghg.invdb.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import gov.epa.ghg.invdb.rest.helper.AttachmentHelper;
import jakarta.servlet.http.HttpServletResponse;
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
            response = ResponseEntity.status(HttpStatus.OK)
                    .body(body);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("An error occured: ", e);
            response = ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("An error occured: ", e);
            response = ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(e.getCause() + ": " + e.getMessage());
        }
        return response;
    }

    public void invokeRestClientZipDownload(String uriWithParams,
            String filename, HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".zip");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/zip; charset=utf-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "");
        try {
            byte[] zipContent = restClient.get().uri(uriWithParams)
                    .retrieve()
                    .body(byte[].class);
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                    response.getOutputStream());
                    ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream)) {
                if (zipContent != null) {
                    // read zip file
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(zipContent);
                            ZipInputStream zipStream = new ZipInputStream(bais)) {
                        ZipEntry entry = zipStream.getNextEntry();
                        while (entry != null) {
                            if (!entry.isDirectory()) {
                                byte[] buffer = new byte[1024];
                                int len = 0;
                                if (!StringUtils.hasText(entry.getName())) {
                                    throw new Exception("Null filename for file in zip entry");
                                }
                                zipOutputStream.putNextEntry(new ZipEntry(entry.getName()));
                                while ((len = zipStream.read(buffer)) > 0) {
                                    zipOutputStream.write(buffer, 0, len);
                                }
                                zipOutputStream.closeEntry();
                            }
                            entry = zipStream.getNextEntry();
                        }
                    }
                }
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("An error occured: ", e);
            response.setStatus(e.getStatusCode().value());
            response.getOutputStream().println("Client/Server error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("An error occured: ", e);
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
            response.getOutputStream().println(e.getCause() + ": " + e.getMessage());
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
                    String strErr = handleErrorResponse(error);
                    // Log the error or handle it as needed
                    log.error("Error from PYTHON service: ", error);

                    // Return error msg
                    return Mono.just(ResponseEntity
                            .status(error instanceof WebClientResponseException
                                    ? ((WebClientResponseException) error)
                                            .getStatusCode()
                                    : HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(strErr));
                });
    }

    public String handleErrorResponse(Throwable error) {
        String errorResponseBody = "Unknown internal error occured on python server. Check python server for logs.";
        if (error instanceof WebClientResponseException) {
            WebClientResponseException responseException = (WebClientResponseException) error;
            HttpStatusCode statusCode = responseException.getStatusCode();

            if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
                // Extract and handle the error response
                errorResponseBody = responseException.getResponseBodyAsString();
                System.out.println("Error response body: " + errorResponseBody);
            }
        }
        // Handle other types of errors if necessary
        return errorResponseBody;
    }

    public void invokeRestClientExcelDownload(String uriWithParams, String filename, HttpServletResponse response)
            throws IOException {
        byte[] content = restClient.get().uri(uriWithParams)
                .retrieve()
                .body(byte[].class);
        attachmentHelper.createFileDownloadResponse(response, content, filename, "excel");
    }
}
