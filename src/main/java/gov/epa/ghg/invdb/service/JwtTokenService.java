package gov.epa.ghg.invdb.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.nimbusds.jwt.JWTClaimsSet;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class JwtTokenService {
	@Autowired
	private JwtEncoder jwtEncoder;
	// @Value("${jwt.expiration}")
	// private long expiresInMinutes;

	public String generateToken(String username, ZonedDateTime endOfDay) {
		Instant now = Instant.now();

		// String scope = authentication.getAuthorities()
		// .stream()
		// .map(GrantedAuthority::getAuthority)
		// .collect(Collectors.joining(" "));

		// Convert ZonedDateTime to Instant
		Instant expirationDate = endOfDay.toInstant();

		// Create JWT payload
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("invdb")
				.issuedAt(now)
				.expiresAt(expirationDate)
				// .expiresAt(now.plus(expiresInMinutes, ChronoUnit.HOURS))
				// .subject(authentication.getName())
				// .claim("scope", scope)
				.subject(username)
				.build();

		return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
	}

	private boolean isTokenExpired(JWTClaimsSet claims) {
		final Date expiration = claims.getExpirationTime();
		return expiration.before(new Date());
	}

}
