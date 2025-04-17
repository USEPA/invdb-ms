package gov.epa.ghg.invdb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gov.epa.ghg.invdb.model.User;
import gov.epa.ghg.invdb.rest.dto.UserDto;
import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        List<User> findByOrderByFirstNameAsc();

        @Query("SELECT new gov.epa.ghg.invdb.rest.dto.UserDto(u.userId, u.username, u.firstName, u.lastName,"
                        + "u.passwordHash, u.specialRoles) FROM User u "
                        + "WHERE lower(u.username) = lower(:username) "
                        + "AND u.deactivated is NULL or u.deactivated = false")
        Optional<UserDto> findByUsername(String username);

        @Modifying
        @Transactional
        @Query("UPDATE User u SET u.firstName = :firstName, u.lastName = :lastName, u.specialRoles = :specialRoles WHERE u.username = :username")
        void updateUserProfile(@Param("firstName") String firstName,
                        @Param("lastName") String lastName, @Param("specialRoles") List<String> specialRoles,
                        @Param("username") String username);

        @Modifying
        @Transactional
        @Query("UPDATE User u SET u.passwordHash = :passwordHash WHERE u.username = :username")
        void updatePassword(@Param("passwordHash") String passwordHash,
                        @Param("username") String username);

        @Modifying
        @Transactional
        @Query("UPDATE User u SET u.deactivated = :deactivated WHERE u.username = :username")
        void updateUserStatus(@Param("deactivated") Boolean deactivated,
                        @Param("username") String username);
}
