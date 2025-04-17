package gov.epa.ghg.invdb.rest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String passwordHash;
    private List<String> specialRoles;
    private Boolean deactivated;

    public UserDto() {
    }

    public UserDto(String userName, String firstName, String lastName, List<String> specialRoles) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialRoles = specialRoles;
    }

    public UserDto(Long userId, String userName, String firstName, String lastName, List<String> specialRoles) {
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialRoles = specialRoles;
    }

    public UserDto(Long userId, String userName, String firstName, String lastName, String passwordHash,
            List<String> specialRoles) {
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = passwordHash;
        this.specialRoles = specialRoles;
    }

    public UserDto(Long userId, String userName, String firstName, String lastName, List<String> specialRoles,
            Boolean deactivated) {
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialRoles = specialRoles;
        this.deactivated = deactivated;
    }
}
