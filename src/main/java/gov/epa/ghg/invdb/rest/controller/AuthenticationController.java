package gov.epa.ghg.invdb.rest.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.epa.ghg.invdb.rest.dto.ApiUser;
import gov.epa.ghg.invdb.rest.dto.Credentials;
import gov.epa.ghg.invdb.rest.dto.EndpointResponse;
import gov.epa.ghg.invdb.rest.dto.Token;
import gov.epa.ghg.invdb.rest.dto.UserDto;
import gov.epa.ghg.invdb.service.AuthenticationService;
import gov.epa.ghg.invdb.service.JwtTokenService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api")
public class AuthenticationController {

    @Autowired
    protected AuthenticationService authenticationService;

    @Autowired
    protected JwtTokenService jwtTokenService;
    // @Value("${jwt.expiration}")
    // private long expiresInMinutes;

    @PostMapping(value = "/login")
    public ResponseEntity<EndpointResponse<Credentials>> login(@RequestBody ApiUser apiUser) {
        UserDto registrationUser = null;
        try {
            registrationUser = authenticationService.authenticate(apiUser.getUsername(),
                    apiUser.getPassword());

            // Get the current date (today)
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            // Set the time to the end of the day (23:59:59)
            LocalTime timeAtEndOfDay = LocalTime.MAX;
            // Combine date and time to a full timestamp
            ZonedDateTime endOfDay = ZonedDateTime.of(today, timeAtEndOfDay, ZoneId.systemDefault());
            Token token = new Token();
            token.setAccessToken(jwtTokenService.generateToken(apiUser.getUsername().toUpperCase(), endOfDay));
            token.setExpiresIn(endOfDay.toEpochSecond());
            token.setTokenType("Bearer");
            return ResponseEntity.ok(new EndpointResponse<>(new Credentials(token), "User logged in successfully"));
        } catch (AuthenticationException ex) {
            return new ResponseEntity<>(new EndpointResponse<>(null,
                    "Invalid username or password."),
                    HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Exception ", e);
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
