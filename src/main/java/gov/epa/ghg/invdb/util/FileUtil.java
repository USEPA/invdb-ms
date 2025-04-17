package gov.epa.ghg.invdb.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class FileUtil {

    public static String sanitizeFileName(String fileName) {
        // Define a regex pattern for invalid characters in file names
        String invalidChars = "[\\\\/:*?\"<>|]";

        // Replace invalid characters with an underscore (_)
        String sanitized = fileName.replaceAll(invalidChars, "_");

        // check for filename length
        int maxLength = 255;
        if (sanitized.length() > maxLength) {
            sanitized = sanitized.substring(0, maxLength);
        }
        return sanitized;
    }

    public byte[] getFilebytes(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024]; // You can adjust the buffer size

                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return outputStream.toByteArray();
            } catch (IOException ex) {
                log.error("Error reading uploaded file: " + file.getOriginalFilename(), ex);
                throw new IOException("Error reading uploaded file.", ex);
            }
        }
    }

    public boolean checkIfFileExists(String filePath) {
        Path path = Paths.get(filePath);
        return Files.exists(path);
    }

    public void writeInputStreamToFile(InputStream inputStream, String filePath) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }
}
