package gov.epa.ghg.invdb.util;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public class ResponseEntityUtil {

    public static ResponseEntity<StreamingResponseBody> downloadZipResponse(StreamingResponseBody stream,
            String filename) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + ".zip\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header("Pragma", "")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    }

    public static ResponseEntity<StreamingResponseBody> downloadResponse(StreamingResponseBody stream,
            String filename, String contentType) {
        MediaType mediaType = buildMediaType(contentType);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .header("Pragma", "")
                .contentType(mediaType)
                .body(stream);
    }

    private static MediaType buildMediaType(String contentType) {
        MediaType base = MediaType.parseMediaType(contentType);
        if (isTextBased(contentType)) {
            return new MediaType(base, StandardCharsets.UTF_8);
        }
        return base;
    }

    private static boolean isTextBased(String contentType) {
        String ct = contentType.toLowerCase().trim();
        return ct.startsWith("text/") || ct.contains("json") || ct.contains("xml") || ct.contains("csv");
    }
}
