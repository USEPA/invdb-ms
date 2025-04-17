package gov.epa.ghg.invdb.rest.controller;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import gov.epa.ghg.invdb.model.DimExcelReport;
import gov.epa.ghg.invdb.repository.DimEmissionsQcLoadTargetRepository;
import gov.epa.ghg.invdb.repository.DimExcelReportRepository;
import gov.epa.ghg.invdb.repository.DimPublicationYearRepository;
import gov.epa.ghg.invdb.rest.dto.ApiUser;
import gov.epa.ghg.invdb.rest.dto.DimEmissionsQcLoadTargetDto;
import gov.epa.ghg.invdb.rest.dto.UserDto;
import gov.epa.ghg.invdb.service.DbTableCrudService;
import gov.epa.ghg.invdb.service.EmissionsQcLoadTargetService;
import gov.epa.ghg.invdb.service.S3Service;
import gov.epa.ghg.invdb.service.UserService;
import gov.epa.ghg.invdb.util.FileUtil;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private DimExcelReportRepository excelReportRepository;
    @Autowired
    private EmissionsQcLoadTargetService emissionsQcLoadTargetService;
    @Autowired
    private DimEmissionsQcLoadTargetRepository emissionsQcLoadTargetRepository;
    @Autowired
    private DimPublicationYearRepository pubYearRepository;
    @Autowired
    private DbTableCrudService dbTableCrudService;
    @Autowired
    private FileUtil fileUtil;
    @Value("${s3.bucket}")
    private String bucket;
    @Autowired
    private S3Service s3Service;
    @Autowired
    private UserService userService;

    @PostMapping("/delete/{itemType}")
    public ResponseEntity<String> delete(
            @PathVariable("itemType") String itemType,
            @RequestParam("id") Integer id,
            @RequestParam(name = "user") int userId)
            throws Exception {
        ResponseEntity<String> response = null;
        try {
            switch (itemType) {
                case "excelReport":
                    excelReportRepository.deleteById(id);
                    break;

                default:
                    throw new Exception("Not a valid itemType: " + itemType);
            }
            response = ResponseEntity.ok("Record deleted successfully. ");
        } catch (Exception e) {
            response = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getCause() + ": " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/modifyExcelReport")
    public void modifyExcelReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("id") Integer id,
            @RequestParam("reportname") String reportName,
            @RequestParam("filename") String filename,
            @RequestParam(name = "user") int userId,
            @RequestParam(name = "layer") int layerId,
            @RequestParam(name = "year") int rptYear)
            throws Exception {
        DimExcelReport dimExcelReport = null;
        byte[] fileContent = fileUtil.getFilebytes(file);
        if (id > 0) {
            dimExcelReport = excelReportRepository.findById(id).orElse(null);
        }
        if (dimExcelReport == null) {
            dimExcelReport = new DimExcelReport();
        }
        dimExcelReport.setReportName(reportName);
        if (!file.isEmpty()) {
            dimExcelReport.setFileContent(fileContent);
            dimExcelReport.setFileSize(file.getSize());
            dimExcelReport.setFileType(file.getContentType());
            dimExcelReport.setReportingYear(rptYear);
            dimExcelReport.setLayerId(layerId);
        }
        if (StringUtils.isNotBlank(filename)) {
            dimExcelReport.setFilename(filename);
        }
        dimExcelReport.setLastCreatedDate(new Date());
        dimExcelReport.setLastCreatedBy(userId);
        excelReportRepository.save(dimExcelReport);
    }

    @GetMapping("/emissionsQcLoadTarget")
    public List<DimEmissionsQcLoadTargetDto> loadEmissionsQcLoadTarget(@RequestParam(name = "layer") int layerId,
            @RequestParam(name = "year") int rptYear, HttpServletResponse response)
            throws Exception {
        List<DimEmissionsQcLoadTargetDto> packages = emissionsQcLoadTargetRepository.getbyLayerAndYear(layerId,
                rptYear);
        return packages;
    }

    @PostMapping("/updateQueryEmissionsQcLoadTarget")
    public void updateQueryEmissionsQcLoadTarget(
            @RequestParam("emissionsQcLoadTargetId") Integer emissionsQcId,
            @RequestParam("reportRowId") Integer reportRowId,
            @RequestParam(name = "user") Integer userId) throws Exception {
        emissionsQcLoadTargetService.updateQuery(reportRowId, userId, emissionsQcId);
    }

    @GetMapping("/s3FilesForQcAnalytics")
    public void getS3FilesForQcAnalytics(
            @RequestParam("folderName") String folderName, @RequestParam("recalcJobStatus") String recalcJobStatus,
            @RequestParam("outlierJobStatus") String outlierJobStatus, HttpServletResponse response) throws Exception {
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + folderName + ".zip");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/zip; charset=utf-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "");
        String key = "analytics/qc/" + folderName + "/";
        List<String> filenames = new ArrayList<>();
        filenames.add("metadata.json");
        filenames.add("baseline.json");
        filenames.add("comparator.json");
        if ("complete".equalsIgnoreCase(recalcJobStatus)) {
            filenames.add("recalculations/aggregate_results.json");
            filenames.add("recalculations/raw_results.json");
        }

        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                response.getOutputStream()); ZipOutputStream zos = new ZipOutputStream(bufferedOutputStream)) {
            for (String filename : filenames) {
                ZipEntry zipEntry = new ZipEntry(filename);
                zos.putNextEntry(zipEntry);
                zos.write(s3Service.downloadFile(bucket, key + filename));
                zos.closeEntry();
            }
        }
    }

    @PostMapping("/invokeTargetYearInitialization")
    public String invokeTargetYearInitialization(
            @RequestParam("targetYear") Integer targetYear,
            @RequestParam("sourceYear") Integer sourceYear,
            @RequestParam(name = "user") Integer userId) throws Exception {
        return pubYearRepository.invokeTargetYearInit(userId, targetYear, sourceYear);
    }

    @GetMapping("/queryTable")
    public List<?> queryTable(@RequestParam(name = "tablename") String tablename,
            @RequestParam(name = "user") Integer userId, HttpServletResponse response)
            throws Exception {
        List<?> results = dbTableCrudService.getRecords(tablename, response);
        return results;
    }

    @PostMapping("/updateTable")
    public ResponseEntity<String> updateTable(
            @RequestBody Map<String, Object> record,
            @RequestParam(name = "tablename") String tablename,
            @RequestParam(name = "action") String action,
            @RequestParam(name = "user") int userId)
            throws Exception {
        ResponseEntity<String> response = null;
        Boolean success = false;
        try {
            switch (action) {
                case "update":
                    success = dbTableCrudService.saveRecord(record, tablename);
                    break;
                case "delete":
                    success = dbTableCrudService.deleteRecord(record, tablename);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid action: " + action);
            }
            if (success) {
                response = ResponseEntity.ok("Operation " + action + " on table " + tablename + " was successful.");
            } else {
                response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Operation " + action + " on table " + tablename + " failed.");
            }
        } catch (IllegalArgumentException e) {
            response = ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Bad action error: " + e.getMessage());
        } catch (Exception e) {
            response = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getCause() + ": " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/user/loadAll")
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/user/updateProfile")
    public void updateUser(@RequestBody UserDto userDto) {
        userService.updateUser(userDto);
    }

    @PostMapping("/user/register")
    public void registerUser(@RequestBody UserDto userDto) {
        userService.registerUser(userDto.getUserName(), userDto.getFirstName(), userDto.getLastName(),
                userDto.getPasswordHash(), userDto.getSpecialRoles());
    }

    @PostMapping("/user/changePassword")
    public void changePassword(@RequestBody ApiUser apiUser) {
        userService.changePassword(apiUser.getUsername(), apiUser.getPassword());
    }

    @PostMapping("/user/status")
    public void deactivateUser(@RequestParam(name = "username") String username,
            @RequestParam(name = "deactivate") Boolean deactivate) {
        userService.updateUserStatus(deactivate, username);
    }
}
