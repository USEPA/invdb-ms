package gov.epa.ghg.invdb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import gov.epa.ghg.invdb.rest.dto.UserDto;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class AuthenticationService {
    @Autowired
    private UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto authenticate(String username, String enteredPassword) throws AuthenticationException {
        UserDto user = userService.getUser(username);

        if (user == null) {
            throw new BadCredentialsException("User not found");
        }
        // Check if the entered password matches the hashed password in the database
        if (passwordEncoder.matches(enteredPassword, user.getPasswordHash())) {
            return user; // Authentication successful
        } else {
            throw new BadCredentialsException("Invalid password");
        }
    }
}
