package gov.epa.ghg.invdb.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_profile")
@SequenceGenerator(name = "user_seq_gen", sequenceName = "user_profile_user_id_seq", allocationSize = 1)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "special_roles")
    private List<String> specialRoles;

    @Column(name = "is_deactivated")
    private Boolean deactivated;

    @OneToMany(mappedBy = "srcAttachCreateUser")
    private java.util.Set<SourceFileAttachment> srcAttachmentCreateUsers;

    @OneToMany(mappedBy = "reportLastUpdateUser")
    private java.util.Set<Report> reportLastUpdateUsers;

    @OneToMany(mappedBy = "reportLastUploadedUser")
    private java.util.Set<Report> reportLastUploadedUsers;

    @OneToMany(mappedBy = "reportProcessedByUser")
    private java.util.Set<Report> reportProcessedByUsers;

    @OneToMany(mappedBy = "lastImportUser")
    private java.util.Set<PublicationObject> lastImportUser;

    @OneToMany(mappedBy = "lastRefinedUser")
    private java.util.Set<PublicationObject> lastRefinedUser;

    @OneToMany(mappedBy = "archivePkgCreateUser")
    private java.util.Set<ArchivePackage> archivePackages;

    @OneToMany(mappedBy = "viewerCreateUser")
    private java.util.Set<QcAnalyticsViewer> analyticsViewers;
}
