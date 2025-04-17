package gov.epa.ghg.invdb.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "dim_excel_report")
public class DimExcelReport implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "excel_report_id")
        private Integer excelReportId;

        @Column(name = "report_name")
        private String reportName;

        @Column(name = "filename")
        private String filename;

        @Column(name = "file_type")
        private String fileType;

        @Column(name = "file_size")
        private Long fileSize;

        @Column(name = "file_content")
        private byte[] fileContent;

        @Column(name = "last_created_date")
        @Temporal(TemporalType.TIMESTAMP)
        private Date lastCreatedDate;

        @Column(name = "last_created_by")
        private Integer lastCreatedBy;

        @Column(name = "reporting_year")
        private Integer reportingYear;

        @Column(name = "layer_id")
        private Integer layerId;
}
