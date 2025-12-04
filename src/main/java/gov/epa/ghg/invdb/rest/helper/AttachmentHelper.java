package gov.epa.ghg.invdb.rest.helper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import gov.epa.ghg.invdb.exception.BadRequestException;
import gov.epa.ghg.invdb.rest.dto.AttachmentDto;
import gov.epa.ghg.invdb.util.JsonUtil;
import gov.epa.ghg.invdb.util.ResponseEntityUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class AttachmentHelper {

    public void downloadZip(HttpServletResponse response, String filename, List<AttachmentDto> attachmentDtos)
            throws IOException {
        // set up headers
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".zip");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/zip; charset=utf-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "");

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
                ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream)) {
            for (AttachmentDto attachmentDto : attachmentDtos) {
                addFileToZip(zipOutputStream, attachmentDto);
            }
        }
    }

    public ResponseEntity<StreamingResponseBody> downloadZip(String filename, List<AttachmentDto> attachmentDtos) {
        StreamingResponseBody stream = outputStream -> {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
                    ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream)) {
                for (AttachmentDto attachmentDto : attachmentDtos) {
                    addFileToZip(zipOutputStream, attachmentDto);
                }
            }
        };
        return ResponseEntityUtil.downloadZipResponse(stream, filename);
    }

    /**
     * Helper function to create a ResponseEntity with appropriate headers for file
     * download.
     *
     * @param fileData Byte array of the file to be downloaded.
     * @param fileName Name of the file (without extension).
     * @param fileType File type (e.g., "excel", "json", "csv").
     * @return ResponseEntity for file download.
     */
    public ResponseEntity<StreamingResponseBody> createFileDownloadResponse(byte[] fileData, String fileName,
            String fileType) {
        // Determine the content type and file extension based on the file type
        String contentType;
        String fileExtension;

        switch (fileType.toLowerCase()) {
            case "excel":
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                fileExtension = ".xlsx";
                break;
            case "json":
                contentType = MimeTypeUtils.APPLICATION_JSON_VALUE;
                fileExtension = ".json";
                break;
            case "csv":
                contentType = "text/csv";
                fileExtension = ".csv";
                break;
            default:
                throw new BadRequestException("Unsupported file type: " + fileType);
        }
        StreamingResponseBody stream = outputStream -> {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
                bufferedOutputStream.write(fileData);
                bufferedOutputStream.flush();
            } catch (Exception e) {
                log.error("Error streaming file.", e);
                throw e;
            }
        };
        return ResponseEntityUtil.downloadResponse(stream, fileName + fileExtension,
                contentType);
    }

    public byte[] downloadZip(List<AttachmentDto> attachmentDtos) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(baos);
                ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream)) {
            for (AttachmentDto attachmentDto : attachmentDtos) {
                addFileToZip(zipOutputStream, attachmentDto);
            }
            // Ensure everything is written out and buffered stream is flushed
            zipOutputStream.finish();
            bufferedOutputStream.flush();
            byte[] zipData = baos.toByteArray();
            return zipData;
        }
    }

    public void convertAndDownloadExcelFiles(HttpServletResponse response, String filename,
            List<AttachmentDto> attachementDtos)
            throws IOException {
        ByteArrayInputStream input = null;
        ByteArrayOutputStream output = null;
        Workbook workbook = null;
        ZipOutputStream zos = null;
        JsonUtil jsonUtil = new JsonUtil();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".zip");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/zip; charset=utf-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "");
        try {
            zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));
            for (AttachmentDto attachmentDto : attachementDtos) {
                input = new ByteArrayInputStream(attachmentDto.getContentText().getBytes());
                workbook = jsonUtil.convertToExcel(input);
                output = new ByteArrayOutputStream();
                workbook.write(output);
                workbook.close();
                // add to zip
                zos.putNextEntry(new ZipEntry(attachmentDto.getAttachmentName() + ".xlsx"));
                zos.write(output.toByteArray());
                zos.closeEntry();
            }
            zos.flush();
        } catch (IOException e) {
            log.error("Error downloading Excel file attachments.", e);
            throw new IOException("Error downloading Excel file attachments.", e);
        } finally {
            if (zos != null) {
                // Closing ZipOutputStream will also close your BufferedOutputStream which in
                // turn will close any stream it opened.
                // Basically close(): Closes this stream and releases any system resources
                // associated with it.
                zos.close();
            }
        }
    }

    /**
     * TODO - don't need two methods. can merger to one and just pass file type
     * ".json" as parameter
     * but i may have to change field type of json columns from text to jsonb.in
     * that case
     * the data is stored as binary and probably i will not need contextText in
     * attachment table.
     * first figure out how to save a jsonb file to db throug jpa hibernate the
     * refactor below
     * code.
     */
    private void addFileToZip(ZipOutputStream zipOutputStream, AttachmentDto attachmentDto) throws IOException {
        byte[] bytes = null;
        String extension = null;
        switch (attachmentDto.getAttachmentType()) {
            case EXCEL:
                bytes = attachmentDto.getContentBinary();
                extension = ".xlsx";
                break;
            case JSON:
                bytes = attachmentDto.getContentText().getBytes();
                extension = ".json";
                break;
            case CSV:
                bytes = attachmentDto.getContentText().getBytes();
                extension = ".csv";
                break;
            case PDF:
                // TODO: to be implemented
                // bytes = attachmentDto.getContentBinary();
                // extension = attachmentDto.getAttachmentExtension();
                break;
            default:
                break;
        }
        if (bytes != null) {
            try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
                byte[] buffer = new byte[1024];
                int len = 0;
                zipOutputStream.putNextEntry(new ZipEntry(attachmentDto.getAttachmentName()
                        + (attachmentDto.getAttachmentName().indexOf(".") > 0 ? "" : extension)));
                while ((len = input.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, len);
                }
                zipOutputStream.closeEntry();
            }
        }
    }
}
